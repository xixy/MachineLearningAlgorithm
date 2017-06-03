/**
 * @author xixy10@foxmail.com
 * @version V0.1 2017年6月3日 下午12:48:53
 */
package cn.xixy.ml.decisiontree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 根据决策树结构生成xml文件用于持久化
 */
public class XmlGenerator {

	public Document xmldoc = null;

	/**
	 * 根据决策树生成xml
	 * 
	 * @param dt
	 *            决策树模型
	 * @return xmldocument
	 */
	public Document generateXml(DecisionTree dt) {
		xmldoc = DocumentHelper.createDocument();
		Element root = xmldoc.addElement("root");
		Node treeroot = dt.getRoot();

		generateXmlNodeRecursively(root, treeroot);
		return xmldoc;
	}

	/**
	 * 迭代生成xml node
	 * 
	 * @param e
	 *            xml node
	 * @param nd
	 *            决策树中的node
	 */
	public void generateXmlNodeRecursively(Element e, Node nd) {
		Element xmlChild = e.addElement(nd.getAttribute()).addAttribute("value", nd.getValue());
		if (nd.getLabel() != null) {
			xmlChild.setText(nd.getLabel());
			return;
		}
		for (Node child : nd.getChilds())
			generateXmlNodeRecursively(xmlChild, child);
	}

	/**
	 * 输出xml文件
	 * 
	 * @param filename
	 */
	public void outputXmlFile(String filename) {
		try {
			File file = new File(filename);
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			OutputFormat format = OutputFormat.createPrettyPrint(); // 美化格式
			XMLWriter output = new XMLWriter(fw, format);
			output.write(xmldoc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
