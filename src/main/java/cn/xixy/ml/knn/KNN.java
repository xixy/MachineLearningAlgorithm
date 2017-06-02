/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月1日 下午7:55:09
 */
package cn.xixy.ml.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class KNN {
	/**
	 * 设置优先级队列的比较函数，距离越大，优先级越高
	 */
	private Comparator<KNNNode> comparator = new Comparator<KNNNode>() {
		public int compare(KNNNode o1, KNNNode o2) {
			if (o1.getDistance() >= o2.getDistance()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	/**
	 * 获取K个不同的随机数
	 * 
	 * @param k
	 *            随机数的个数
	 * @param max
	 *            随机数最大的范围
	 * @return 生成的随机数数组
	 */
	public List<Integer> getRandKNum(int k, int max) {
		List<Integer> rand = new ArrayList<Integer>(k);
		for (int i = 0; i < k; i++) {
			int temp = (int) (Math.random() * max);
			if (!rand.contains(temp)) {
				rand.add(temp);
			} else {
				i--;
			}
		}
		return rand;
	}

	/**
	 * 计算测试元组与训练元组之前的距离
	 * 
	 * @param d1
	 *            测试元组
	 * @param d2
	 *            训练元组
	 * @return 距离值
	 */
	public double calDistance(List<Double> d1, List<Double> d2) {
		double distance = 0.00;
		for (int i = 0; i < d1.size(); i++) {
			distance += (d1.get(i) - d2.get(i)) * (d1.get(i) - d2.get(i));
		}
		return Math.sqrt(distance);
	}

	/**
	 * 执行KNN算法，获取测试元组的类别
	 * 
	 * @param datas
	 *            训练数据集
	 * @param testData
	 *            测试元组
	 * @param k
	 *            设定的K值
	 * @return 测试元组的类别
	 */
	public String knn(List<List<Double>> datas, List<Double> testData, int k) {
		ArrayList<KNNNode> alKN = new ArrayList<KNNNode>();
		for (int i = 0; i < k; i++) {
			List<Double> currData = datas.get(i);
			String c = currData.get(currData.size() - 1).toString();
			KNNNode node = new KNNNode(i, calDistance(testData, currData), c);
			alKN.add(node);
		}
		for (int i = k; i < datas.size(); i++) {
			List<Double> t = datas.get(i);
			double distance = calDistance(testData, t);
			for (int j = 0; j < alKN.size(); j++) {
				if ((alKN.get(j).getDistance()) >= distance) {
					alKN.remove(j);
					alKN.add(new KNNNode(i, distance, t.get(t.size() - 1).toString()));
					break;
				}
			}

		}

		return getMostClass(alKN);
	}

	/**
	 * 获取所得到的k个最近邻元组的多数类
	 * 
	 * @param pq
	 *            存储k个最近近邻元组的优先级队列
	 * @return 多数类的名称
	 */
	private String getMostClass(ArrayList<KNNNode> alKN) {
		Map<String, Integer> classCount = new HashMap<String, Integer>();
		for (int i = 0; i < alKN.size(); i++) {
			KNNNode node = alKN.remove(i);
			String c = node.getC();
			if (classCount.containsKey(c)) {
				classCount.put(c, classCount.get(c) + 1);
			} else {
				classCount.put(c, 1);
			}
		}
		int maxIndex = -1;
		int maxCount = 0;
		Object[] classes = classCount.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			if (classCount.get(classes[i]) > maxCount) {
				maxIndex = i;
				maxCount = classCount.get(classes[i]);
			}
		}
		return classes[maxIndex].toString();
	}

}
