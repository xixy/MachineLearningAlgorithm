/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月1日 下午7:57:39
 */
package cn.xixy.ml.knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestKNN {
	
	public static String trainfile = "/Users/apple/WorkSpace/ml/src/main/resources/knn/train-file";
	public static String testfile = "/Users/apple/WorkSpace/ml/src/main/resources/knn/test-file";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestKNN t = new TestKNN();
		try {
			List<List<Double>> datas = new ArrayList<List<Double>>();
			List<List<Double>> testDatas = new ArrayList<List<Double>>();
			t.read(datas, trainfile);
			t.read(testDatas, testfile);
			KNN knn = new KNN();
			for (int i = 0; i < testDatas.size(); i++) {
				List<Double> test = testDatas.get(i);
				System.out.print("测试元组: ");
				for (int j = 0; j < test.size(); j++) {
					System.out.print(test.get(j) + " ");
				}
				System.out.print("类别为: ");
				System.out.println(Math.round(Float.parseFloat((knn.knn(datas, test, 4))))); // 返回最接近参数的
																								// int
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 读取数据
	 * 
	 * @param datas
	 *            List
	 * @param path
	 *            数据文本路径
	 */
	public void read(List<List<Double>> datas, String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String data = br.readLine();
			List<Double> l = null;
			while (data != null) {
				String t[] = data.split(" ");
				l = new ArrayList<Double>();
				for (int i = 0; i < t.length; i++) {
					l.add(Double.parseDouble(t[i]));
				}
				datas.add(l);
				data = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
