/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月1日 下午7:53:54
 */
package cn.xixy.ml.knn;

/**
 *
 */
public class KNNNode {
	private int index; // 训练数据集中的点的index
	private double distance; // 与测试数据的距离
	private String c; // 所属类别

	public KNNNode(int index, double distance, String c) {
		super();
		this.index = index;
		this.distance = distance;
		this.c = c;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

}
