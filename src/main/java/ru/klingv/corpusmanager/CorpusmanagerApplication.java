package ru.klingv.corpusmanager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class CorpusmanagerApplication {

	public static void main(String[] args) {
//		SpringApplication.run(CorpusmanagerApplication.class, args);

		testMain();
	}

	private static void testMain() {
		String pathConfig = "config/config.xml";
		String pathConfigXsd = "config/config.xsd";
		try {
			boolean res = validateXml(pathConfig, pathConfigXsd);
			if (res) {
				readConfig(pathConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean validateXml(String xml, String schemaFile) throws Exception {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new File(schemaFile));

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xml)));
			return true;
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void readConfig(String xmlPath) throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder =
				dbFactory.newDocumentBuilder();
		Document doc = null;
		try {
			doc = dBuilder.parse(xmlPath);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();

		NodeList nodesUtils = doc.getElementsByTagName("util");

		for (int i = 0; i < nodesUtils.getLength(); i++) {

			String utilPath = "";
			String utilParams = "";
			String tagName = "";

			Node node = nodesUtils.item(0);
			NodeList nodes = node.getChildNodes();
			for (int j = 0; j < nodes.getLength(); j++) {
				if (nodes.item(j).getNodeName().equals("path")) {
					utilPath = nodes.item(j).getTextContent();
				} else if (nodes.item(j).getNodeName().equals("parameters")) {
					utilParams = nodes.item(j).getTextContent();
				} else if (nodes.item(j).getNodeName().equals("tagName")) {
					tagName = nodes.item(j).getTextContent();
				}
			}

			System.out.println("java -jar " + utilPath + " texts " + tagName + utilParams);

			try {
				Process p = Runtime.getRuntime().exec("java -jar " + utilPath + " .\\texts " + tagName + utilParams);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Новая разметка добавлена");
		}
	}
}

