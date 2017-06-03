/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:36:40
 */
package cn.xixy.ml.decisiontree.id3.train;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来描述决策树中的一个节点
 */
public class Node {

	private String attribute;// 属性名称
	private String value;// 属性值
	private List<Node> childs;// 子节点
	private String label;// 类别标记，如果是叶子结点的话

	public Node(String attribute, String value) {
		this.setAttribute(attribute);
		this.setValue(value);
	}

	public void addChild(Node child) {
		if (childs == null)
			childs = new ArrayList<Node>();
		childs.add(child);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Node> getChilds() {
		return childs;
	}

	public void setChilds(List<Node> childs) {
		this.childs = childs;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
