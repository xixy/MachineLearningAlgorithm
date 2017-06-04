/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月1日 下午10:03:26
 */
package cn.xixy.ml.decisiontree.cart.prediction;

/**
 *
 */
public class PredictionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Prediction p = new Prediction();
		p.loadDecisionTree();
		p.loadTestFile();
		p.prediction();

	}

}
