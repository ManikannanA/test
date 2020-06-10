package net.nwie.awdtool;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * TODO: Document me!
 *
 * @author mahendraselvik
 *
 */
public class CDataUpdater {

	/**
	 * @param args
	 * @throws TransformerException
	 */
	public static void main(String[] args) throws TransformerException {
		getValue();
	}

	public static String getValue() throws TransformerException {
		String valueRetrieved = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new File("lob.xml"));
			NodeList ndList = doc.getElementsByTagName("AWD");
			for (int i = 0; i < ndList.getLength(); i++) {
				String xmlRetrieved = ndList.item(i).getTextContent();
				System.out.println("AWD: " + xmlRetrieved);
				if (xmlRetrieved != null) {
					doc = db.parse(new InputSource(new StringReader(xmlRetrieved)));
					valueRetrieved = doc.getElementsByTagName("fieldValues").item(i).getTextContent();
					NodeList iParent = doc.getElementsByTagName("fieldValues");
					System.out.println("iParent: " + iParent.getLength());
					for (int j = 0; j < iParent.getLength(); j++) {
						NodeList insParent = doc.getElementsByTagName("filedValue");
						System.out.println("createInstance: " + insParent.item(j).getNodeName());
						for (int k = 0; k < insParent.getLength(); k++) {
							insParent = doc.getElementsByTagName("value");
							System.out.println("value: " + insParent.item(k).getTextContent());
							String output = insParent.item(k).getTextContent();
							if (output.length() > 2) {
								insParent.item(k).getFirstChild().setNodeValue(output.substring(0, 2));
								System.out.println("value after: " + insParent.item(0).getFirstChild().getNodeValue());
							}
						}

					}
				}
			}
			// Create an empty document

			Document doc1 = db.newDocument();

			// Add the new root node

			Element root = doc1.createElement("AWD");
			doc1.appendChild(root);
			Element task = doc1.createElement("TASK");
			root.appendChild(task);
			Node copy = doc1.importNode(doc.getDocumentElement(), true);
			String xmlContent = documentToString(doc);
			CDATASection cdata = doc1.createCDATASection(xmlContent);
			task.appendChild(cdata);
			// task.appendChild(copy);

			// Add a copy of the nodes from existing document

			/*
			 * Node copy = doc1.importNode(doc.getDocumentElement(), true);
			 * task.appendChild(copy);
			 */
			// Element wsdlTypeElement = document.createElement("wsdltype");

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc1);
			StreamResult result = new StreamResult(new File("lob.xml"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("XML file updated successfully");

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return valueRetrieved;

	}

	public static String documentToString(Document document) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter sw = new StringWriter();
			trans.transform(new DOMSource(document), new StreamResult(sw));
			return sw.toString();
		} catch (TransformerException tEx) {
			tEx.printStackTrace();
		}
		return null;
	}

}
