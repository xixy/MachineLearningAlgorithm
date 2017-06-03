/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:54:27
 */
package cn.xixy.ml.decisiontree.id3.train;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 决策树的生成
 */
public class DecisionTree {
	private Node root;

	public DecisionTree() {
		root = new Node("DecisionTree", "NULL");
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
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

	/**
	 * 构建决策树
	 * 
	 * @param data
	 *            训练数据集
	 * @param selatt
	 *            属性index列表
	 * @param attributevalue
	 *            属性值列表
	 * @param attribute
	 *            属性列表
	 * @return
	 */
	public Node buildDecisionTree(ArrayList<String[]> data, LinkedList<Integer> selatt,
			ArrayList<ArrayList<String>> attributevalue, ArrayList<String> attribute) {
		buildDecisionTreeRecursively(root, data, selatt, attributevalue, attribute);
		return root;

	}

}
