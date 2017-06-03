/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:44:48
 */
package cn.xixy.ml.decisiontree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来读取arff文件，生成相应的训练数据、属性、属性值等
 */
public class DataReader {
	public static final String patternString = "@attribute(.*)[{](.*?)[}]";

	/**
	 * 读取arff文件，并给attribute、attributevalue、data赋值
	 * 
	 * @param file
	 *            文件
	 * @param attribute
	 *            属性名称
	 * @param attributevalue
	 *            属性值
	 * @param data
	 *            得到的数据列表
	 */
	public static void readARFF(File file, ArrayList<String> attribute, ArrayList<ArrayList<String>> attributevalue,
			ArrayList<String[]> data) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern pattern = Pattern.compile(patternString);
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) { // 读@attribute
					attribute.add(matcher.group(1).trim());
					String[] values = matcher.group(2).split(",");
					ArrayList<String> al = new ArrayList<String>(values.length);
					for (String value : values) {
						al.add(value.trim());
					}
					attributevalue.add(al);
				} else if (line.startsWith("@data")) { // 读@data
					while ((line = br.readLine()) != null) {
						if (line == "")
							continue;
						String[] row = line.split(",");
						data.add(row);
					}
				} else {
					continue;
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
