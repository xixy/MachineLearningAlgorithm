/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午1:11:54
 */
package cn.xixy.ml.decisiontree.id3.train;

import java.util.ArrayList;

/**
 *
 */
public class GainCalculator {

	/**
	 * 判断数据集是否为同一类别的数据集
	 * 
	 * @param data
	 *            数据集
	 * @return
	 */
	public static boolean isPureDataSet(ArrayList<String[]> data) {
		String[] data1 = data.get(0);
		int labelIndex = data1.length - 1;
		String label1 = data1[labelIndex];
		for (int i = 1; i < data.size(); i++) {
			if (!label1.equals(data.get(i)[labelIndex]))
				return false;
		}
		return true;
	}

	/**
	 * 计算样本的熵，其中数组是同一属性值下的不同类别的数量
	 * 
	 * @param arr
	 * @return
	 */
	public static double getEntropy(int[] arr) {
		double entropy = 0.0;
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			entropy -= arr[i] * Math.log(arr[i] + Double.MIN_VALUE) / Math.log(2);
			sum += arr[i];
		}
		entropy += sum * Math.log(sum + Double.MIN_VALUE) / Math.log(2);
		entropy /= sum;
		return entropy;
	}

	/**
	 * 根据给定的原始数据中的子集，以当前第index个属性节点计算他的信息熵
	 * 
	 * @param subSet
	 * @param attributeIndex
	 * @param attributevalue
	 * @return
	 */
	public static double calNodeEntropy(ArrayList<String[]> subSet, int attributeIndex,
			ArrayList<ArrayList<String>> attributevalue) {
		int sum = subSet.size();
		int decatt = subSet.get(0).length - 1;// 预测label的index
		double entropy = 0.0;
		int[][] info = new int[attributevalue.get(attributeIndex).size()][];
		for (int i = 0; i < info.length; i++)
			info[i] = new int[attributevalue.get(decatt).size()];
		int[] count = new int[attributevalue.get(attributeIndex).size()];// 不同属性值的计数
		// 统计类别和属性关系
		for (int i = 0; i < sum; i++) {
			String nodevalue = subSet.get(i)[attributeIndex];// 属性值，例如outlook是sunny
			int nodeind = attributevalue.get(attributeIndex).indexOf(nodevalue);// 属性值对应的index，例如sunny对应{sunny，rainy}中的0
			count[nodeind]++;// 属性个数+1
			String decvalue = subSet.get(i)[decatt];// 第i个数的类别
			int decind = attributevalue.get(decatt).indexOf(decvalue);// 类别对应的index，例如yes对应{yes,no}中的0
			info[nodeind][decind]++;// 增加相应属性值下的相应类别+1
		}
		// 计算
		// |Di|/|D|*H(Di)
		for (int i = 0; i < info.length; i++) {
			entropy += getEntropy(info[i]) * count[i] / sum;
		}
		return entropy;
	}

}
