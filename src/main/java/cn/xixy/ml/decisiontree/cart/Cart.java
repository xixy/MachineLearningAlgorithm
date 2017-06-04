/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:48:23
 */
package cn.xixy.ml.decisiontree.cart;

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
public class Cart extends DecisionTreeGenerator {
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
		Cart inst = new Cart();
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
		xmlGenerator.outputXmlFile(Setting.cartxmlfile);
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
	public cn.xixy.ml.decisiontree.DecisionTree buildDecisionTree(ArrayList<String[]> data, LinkedList<Integer> selatt,
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
		System.out.println(node.getAttribute() + "|" + node.getValue());
		System.out.println(attributevalue.get(0));
		// 如果数据已经是同一类别了，那就没有意义往下分了
		if (GiniCalculator.isPureDataSet(data)) {
			node.setLabel(data.get(0)[data.get(0).length - 1]);
			return;
		}
		// 如果数据不够纯净
		// 选择熵最小的，也就是得到信息增益最大的，这里并没有计算g(D,A)，而是计算了H(D|A)，选择最小的H(D|A)，即可得到最大的g(D,A)
		int minIndex = -1;// 属性index
		String attributeValueSelected = null;// 最小基尼指数对应的属性的属性值
		double minGini = Double.MAX_VALUE;// 最小熵
		for (int i = 0; i < selatt.size(); i++) {
			Pair<Double, String> result = GiniCalculator.calNodeGini(data, selatt.get(i), attributevalue);
			if (result.first < minGini) {
				minIndex = selatt.get(i);
				minGini = result.first;
				attributeValueSelected = result.second;
			}
		}
		System.out.println(minIndex);

		// 获取属性名称
		String nodeName = attribute.get(minIndex);
		// 去掉该属性值
		attributevalue.get(minIndex).remove(attributeValueSelected);
		ArrayList<ArrayList<String>> leftAttributeValues = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> rightAttributeValues = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < attributevalue.size(); i++) {
			ArrayList<String> values = attributevalue.get(i);
			leftAttributeValues.add(new ArrayList<String>(values));
			rightAttributeValues.add(new ArrayList<String>(values));
		}

		// 如果只剩下一个，那么去掉已选属性

		if (attributevalue.get(minIndex).size() == 1)
			selatt.remove(new Integer(minIndex));

		// 按照不同属性值进行划分数据集，然后进行迭代
		// 1. 左子树节点
		Node leftChild = new Node(nodeName, attributeValueSelected);

		ArrayList<String[]> subset = new ArrayList<String[]>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i)[minIndex].equals(attributeValueSelected)) {
				subset.add(data.get(i));
			}
		}
		if (subset.size() > 0) {

			node.addChild(leftChild);
			buildDecisionTreeRecursively(leftChild, subset, new LinkedList<Integer>(selatt), leftAttributeValues,
					attribute);
		}
		// 右子树节点

		Node rightChild = new Node(nodeName, "Not " + attributeValueSelected);
		// 如果剩下的属性值只有一个，那么就直接赋值，不需要other
		if (attributevalue.get(minIndex).size() == 1) {
			rightChild.setValue(attributevalue.get(minIndex).get(0));
		}

		subset = new ArrayList<String[]>();
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i)[minIndex].equals(attributeValueSelected)) {
				subset.add(data.get(i));
			}
		}
		if (subset.size() > 0) {
			node.addChild(rightChild);
			buildDecisionTreeRecursively(rightChild, subset, new LinkedList<Integer>(selatt), rightAttributeValues,
					attribute);
		}

	}

}
