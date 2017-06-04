/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:48:23
 */
package cn.xixy.ml.decisiontree.id3.train;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import cn.xixy.ml.decisiontree.DataReader;
import cn.xixy.ml.decisiontree.DecisionTree;
import cn.xixy.ml.decisiontree.DecisionTreeGenerator;
import cn.xixy.ml.decisiontree.Node;
import cn.xixy.ml.decisiontree.Setting;
import cn.xixy.ml.decisiontree.XmlGenerator;

/**
 *
 */
public class ID3 extends DecisionTreeGenerator {
	public ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
	public ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 存储每个属性的取值
	public ArrayList<String[]> data = new ArrayList<String[]>();; // 原始数据
	public int decatt; // 决策变量在属性集中的索引

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
	 * @param args
	 */
	public static void main(String[] args) {
		ID3 inst = new ID3();
		DataReader.readARFF(new File(Setting.trainingfile), inst.attribute, inst.attributevalue, inst.data);

		inst.setDec("play");
		LinkedList<Integer> ll = new LinkedList<Integer>();// 非决策属性的index列表
		for (int i = 0; i < inst.attribute.size(); i++) {
			if (i != inst.decatt)
				ll.add(i);
		}
		ArrayList<Integer> al = new ArrayList<Integer>();// 所有的data的列表
		for (int i = 0; i < inst.data.size(); i++) {
			al.add(i);
		}
		DecisionTree dt = inst.buildDecisionTree(inst.data, ll, inst.attributevalue, inst.attribute);
		XmlGenerator xmlGenerator = new XmlGenerator();
		xmlGenerator.generateXml(dt);
		xmlGenerator.outputXmlFile(Setting.id3xmlfile);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.xixy.ml.decisiontree.DecisionTreeGenerator#buildDecisionTree(java.util
	 * .ArrayList, java.util.LinkedList, java.util.ArrayList,
	 * java.util.ArrayList)
	 */
	@Override
	public DecisionTree buildDecisionTree(ArrayList<String[]> data, LinkedList<Integer> selatt,
			ArrayList<ArrayList<String>> attributevalue, ArrayList<String> attribute) {
		DecisionTree de = new DecisionTree();
		buildDecisionTreeRecursively(de.getRoot(), data, selatt, attributevalue, attribute);
		return de;

	}

	/**
	 * 迭代产生决策树
	 * 
	 * @param node
	 *            当前节点，已经给出了属性和属性值
	 * @param data
	 *            当前属性值下的数据集
	 * @param selatt
	 *            当前可用的属性index
	 * @param attributevalue
	 *            属性值list
	 * @param attribute
	 *            属性list
	 */
	public static void buildDecisionTreeRecursively(Node node, ArrayList<String[]> data, LinkedList<Integer> selatt,
			ArrayList<ArrayList<String>> attributevalue, ArrayList<String> attribute) {
		// 如果数据已经是同一类别了，那就没有意义往下分了
		if (GainCalculator.isPureDataSet(data)) {
			node.setLabel(data.get(0)[data.get(0).length - 1]);
			return;
		}
		// 如果数据不够纯净
		// 选择熵最小的，也就是得到信息增益最大的，这里并没有计算g(D,A)，而是计算了H(D|A)，选择最小的H(D|A)，即可得到最大的g(D,A)
		int minIndex = -1;// 属性index
		double minEntropy = Double.MAX_VALUE;// 最小熵
		for (int i = 0; i < selatt.size(); i++) {
			double entropy = GainCalculator.calNodeEntropy(data, selatt.get(i), attributevalue);
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
			Node child = new Node(nodeName, val);
			node.addChild(child);
			ArrayList<String[]> subset = new ArrayList<String[]>();
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i)[minIndex].equals(val)) {
					subset.add(data.get(i));
				}
			}
			buildDecisionTreeRecursively(child, subset, selatt, attributevalue, attribute);
		}

	}

}
