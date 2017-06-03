/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月1日 下午9:57:38
 */
package cn.xixy.ml.decisiontree.id3.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.xixy.ml.decisiontree.Setting;
import cn.xixy.ml.decisiontree.id3.train.DataReader;
import cn.xixy.ml.decisiontree.id3.train.ID3;

/**
 * 采用得到的决策树进行预测
 */
public class Prediction {

	private ArrayList<String[]> testData = null;
	Document xmldoc;
	private Map<String, Integer> attributeIndex = null;

	/**
	 * 加载测试文件
	 */
	public void loadTestFile() {
		testData = new ArrayList<String[]>();

		try {
			FileReader fr = new FileReader(new File(Setting.testfile));
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				testData.add(values);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取决策树的xml文件
	 */
	public void loadDecisionTree() {
		SAXReader reader = new SAXReader();
		try {
			xmldoc = reader.read(new File(Setting.xmlfile));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过递归来实现决策树的遍历和匹配
	 * 
	 * @param e
	 *            节点
	 * @param data
	 *            数据
	 * @return label
	 */
	public String validation(Element e, String[] data) {
		if (e.isRootElement() || e.getName().equals("DecisionTree")) {
			@SuppressWarnings("unchecked")
			Iterator<Element> it = e.elementIterator();
			while (it.hasNext()) {
				Element child = it.next();
				String result = validation(child, data);
				if (result != null)
					return result;
			}
		}

		Attribute attribute = e.attribute(0);
		String name = e.getName();
		if (!attributeIndex.containsKey(name))
			return null;
		int index = attributeIndex.get(name);
		String value = attribute.getValue();
		// 如果匹配当前节点条件，那么就以该节点为要求往下进行匹配
		if (value.equals(data[index])) {
			@SuppressWarnings("unchecked")
			Iterator<Element> it = e.elementIterator();
			// 如果是叶子结点，那么就直接给出类别
			if (it.hasNext() == false) {
				return e.getText();
			}
			while (it.hasNext()) {
				Element child = it.next();
				String result = validation(child, data);
				if (result != null)
					return result;
			}
		}
		return null;

	}

	/**
	 * 进行预测工作
	 */
	public void prediction() {
		// 首先获取到属性列表
		ID3 inst = new ID3();
		DataReader.readARFF(new File(Setting.trainingfile), inst.attribute, inst.attributevalue, inst.data);
		inst.setDec(inst.attribute.get(inst.attribute.size() - 1));
		LinkedList<Integer> ll = new LinkedList<Integer>();// 非决策属性的index列表

		attributeIndex = new HashMap<String, Integer>();// 属性与index的对应
		for (int i = 0; i < inst.attribute.size(); i++) {
			if (i != inst.decatt) {
				attributeIndex.put(inst.attribute.get(i), i);
				ll.add(i);
			}
		}
		Element root = xmldoc.getRootElement();
		// 对每一组数据，都进行规则上的遍历，采用宽度优先
		for (String[] data : testData) {
			String label = validation(root, data);
			System.out.println(label);
		}

	}

}
