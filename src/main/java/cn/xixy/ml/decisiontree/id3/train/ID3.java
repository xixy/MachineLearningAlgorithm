/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:48:23
 */
package cn.xixy.ml.decisiontree.id3.train;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import cn.xixy.ml.decisiontree.Setting;

/**
 *
 */
public class ID3 {
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
		DecisionTree dt = new DecisionTree();
		dt.buildDecisionTree(inst.data, ll, inst.attributevalue, inst.attribute);
		XmlGenerator xmlGenerator = new XmlGenerator();
		xmlGenerator.generateXml(dt);
		xmlGenerator.outputXmlFile(Setting.xmlfile);
		return;
	}

}
