/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午9:07:07
 */
package cn.xixy.ml.decisiontree;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 */
public abstract class DecisionTreeGenerator {
	abstract public DecisionTree buildDecisionTree(ArrayList<String[]> data, LinkedList<Integer> selatt,
			ArrayList<ArrayList<String>> attributevalue, ArrayList<String> attribute);

}
