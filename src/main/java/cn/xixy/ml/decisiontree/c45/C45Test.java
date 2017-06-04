/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月2日 下午4:58:43
 */
package cn.xixy.ml.decisiontree.c45;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import cn.xixy.ml.decisiontree.Setting;

/**
 *
 */
public class C45Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		C45 inst = new C45();
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
		inst.writeXML(Setting.c45xmlfile);
		
		return;
	}

}
