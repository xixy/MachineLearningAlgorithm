/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月2日 下午3:57:41
 */
package cn.xixy.ml.decisiontree;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 */
public class ID3Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ID3 inst = new ID3();
		inst.readARFF(new File(Setting.trainingfile));
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
		inst.buildDT("DecisionTree", "null", al, ll);
		inst.writeXML(Setting.xmlfile);
		
		return;
	}

}
