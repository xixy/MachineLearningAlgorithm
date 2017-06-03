/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:54:27
 */
package cn.xixy.ml.decisiontree;

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
}
