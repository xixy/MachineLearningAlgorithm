/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年5月31日 下午7:40:41
 */
package cn.xixy.ml.decisiontree.c45;

/**
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class C45 {

	public ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
	public ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 存储每个属性的取值
	public ArrayList<String[]> data = new ArrayList<String[]>();; // 原始数据
	int decatt; // 决策变量在属性集中的索引
	public static final String patternString = "@attribute(.*)[{](.*?)[}]";

	Document xmldoc;
	Element root;

	public C45() {
		xmldoc = DocumentHelper.createDocument();
		root = xmldoc.addElement("root");
		root.addElement("DecisionTree").addAttribute("value", "null");
	}

	/**
	 * 读取arff文件，并给attribute、attributevalue、data赋值
	 * 
	 * @param file
	 */
	public void readARFF(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern pattern = Pattern.compile(patternString);
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) { // 读@attribute
					attribute.add(matcher.group(1).trim());
					String[] values = matcher.group(2).split(",");
					ArrayList<String> al = new ArrayList<String>(values.length);
					for (String value : values) {
						al.add(value.trim());
					}
					attributevalue.add(al);
				} else if (line.startsWith("@data")) { // 读@data
					while ((line = br.readLine()) != null) {
						if (line == "")
							continue;
						String[] row = line.split(",");
						data.add(row);
					}
				} else {
					continue;
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 设置决策变量index
	 * 
	 * @param n
	 *            index
	 */
	public void setDec(int n) {
		if (n < 0 || n >= attribute.size()) {
			System.err.println("决策变量指定错误。");
			System.exit(2);
		}
		decatt = n;
	}

	/**
	 * 表示决策变量
	 * 
	 * @param name
	 *            据测变量名称
	 */
	public void setDec(String name) {
		int n = attribute.indexOf(name);
		setDec(n);
	}

	/**
	 * 计算样本的熵，其中数组是同一属性值下的不同类别的数量
	 * 
	 * @param arr
	 * @return
	 */
	public double getEntropy(int[] arr) {
		double entropy = 0.0;
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE) / Math.log(2);
			sum += arr[i];
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	// 给一个样本数组及样本的算术和，计算它的熵
	public double getEntropy(int[] arr, int sum) {
		double entropy = 0.0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE) / Math.log(2);
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	/**
	 * 判断是否为属于同一类型的子集
	 * 
	 * @param subset
	 *            数据集
	 * @return true／false
	 */
	public boolean infoPure(ArrayList<Integer> subset) {
		String value = data.get(subset.get(0))[decatt];
		for (int i = 1; i < subset.size(); i++) {
			String next = data.get(subset.get(i))[decatt];
			// equals表示对象内容相同，==表示两个对象指向的是同一片内存
			if (!value.equals(next))
				return false;
		}
		return true;
	}

	/**
	 * 根据给定的原始数据中的子集，以当前第index个属性节点计算他的信息熵
	 * 
	 * @param subset给定原始数据的子集(subset中存储行号)
	 * @param index
	 *            属性index，这里是跟data排列的数据相关的
	 * @return
	 */
	public double calNodeEntropy(ArrayList<Integer> subset, int index) {
		int sum = subset.size();
		double entropy = 0.0;
		int[][] info = new int[attributevalue.get(index).size()][];
		for (int i = 0; i < info.length; i++)
			info[i] = new int[attributevalue.get(decatt).size()];
		int[] count = new int[attributevalue.get(index).size()];// 不同属性值的计数
		// 统计类别和属性关系
		for (int i = 0; i < sum; i++) {
			int n = subset.get(i);
			String nodevalue = data.get(n)[index];// 属性值，例如outlook是sunny
			int nodeind = attributevalue.get(index).indexOf(nodevalue);// 属性值对应的index，例如sunny对应{sunny，rainy}中的0
			count[nodeind]++;// 属性个数+1
			String decvalue = data.get(n)[decatt];// 第n个数的类别
			int decind = attributevalue.get(decatt).indexOf(decvalue);// 类别对应的index，例如yes对应{yes,no}中的0
			info[nodeind][decind]++;// 增加相应属性值下的相应类别+1
		}
		// 计算
		// |Di|/|D|*H(Di)
		for (int i = 0; i < info.length; i++) {
			entropy += getEntropy(info[i]) * count[i] / sum;
		}
		return entropy;
	}

	/**
	 * 构建决策树
	 * 
	 * @param name
	 *            顶层属性名称
	 * @param value
	 *            顶层属性值
	 * @param subset
	 *            所有的需要进行判断的数据集合，这里是data这个list中的index
	 * @param selatt
	 *            可以选择的属性index集合，这个index是attribute这个list中的index
	 */
	public void buildDT(String name, String value, ArrayList<Integer> subset, LinkedList<Integer> selatt) {
		Element ele = null;
		@SuppressWarnings("unchecked")
		List<Element> list = root.selectNodes("//" + name);
		Iterator<Element> iter = list.iterator();
		while (iter.hasNext()) {
			ele = iter.next();
			if (ele.attributeValue("value").equals(value))
				break;
		}
		// 判断子集是否属于同一类型
		if (infoPure(subset)) {
			ele.setText(data.get(subset.get(0))[decatt]);
			return;
		}
		// 选择熵最小的，也就是得到信息增益最大的，这里并没有计算g(D,A)，而是计算了H(D|A)，选择最小的H(D|A)，即可得到最大的g(D,A)
		int minIndex = -1;
		double minEntropy = Double.MAX_VALUE;
		for (int i = 0; i < selatt.size(); i++) {
			if (i == decatt)
				continue;
			double entropy = calNodeEntropy(subset, selatt.get(i));
			if (entropy < minEntropy) {
				minIndex = selatt.get(i);
				minEntropy = entropy;
			}
		}
		// 获取属性名称
		String nodeName = attribute.get(minIndex);
		// 去掉已选属性
		selatt.remove(new Integer(minIndex));
		// 得到该属性的所有值
		ArrayList<String> attvalues = attributevalue.get(minIndex);
		// 按照不同属性值进行划分数据集
		for (String val : attvalues) {
			ele.addElement(nodeName).addAttribute("value", val);
			ArrayList<Integer> al = new ArrayList<Integer>();
			for (int i = 0; i < subset.size(); i++) {
				if (data.get(subset.get(i))[minIndex].equals(val)) {
					al.add(subset.get(i));
				}
			}
			buildDT(nodeName, val, al, selatt);
		}
	}

	// 把xml写入文件
	public void writeXML(String filename) {
		try {
			File file = new File(filename);
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			OutputFormat format = OutputFormat.createPrettyPrint(); // 美化格式
			XMLWriter output = new XMLWriter(fw, format);
			output.write(xmldoc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}