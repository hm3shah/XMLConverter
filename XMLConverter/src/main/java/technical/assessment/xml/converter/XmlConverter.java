package technical.assessment.xml.converter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlConverter {

	public static final Logger LOGGER = LoggerFactory.getLogger(XmlConverter.class);
	//Though these values are here part of the code but we can 
	//externalize them and pass it from configuration properties.
    public static final String OUTPUT_FILENAME = "output.xml";

	public static void main(String[] args) throws IOException {
		XmlConverter app = new XmlConverter();
		if (args.length > 0) {
			File input = new File(args[0]);
			Document doc = app.readXMLFromFile(input);
			boolean success = app.writeXMLFile(doc);
			if(!success) {
				LOGGER.error("Failed to write the xml file");
			}
		}
	}

	public Document readXMLFromFile(File input) {
		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(input);

			//- Search for the <credentials> in the root node:
			Node credentialsNode = doc.getElementsByTagName("credentials").item(0);
			//- Combine the <name> and <domain> elements into a <name type=”UPN”> element
			Node nameNode = doc.getElementsByTagName("name").item(0);
			Node domainNode = doc.getElementsByTagName("domain").item(0);
			String name = nameNode.getTextContent();
			String domain = domainNode.getTextContent();
			LOGGER.info("name : " + name);
			LOGGER.info("domain : " + domain);
			((Element)nameNode).setAttribute("type", "UPN");
			nameNode.setTextContent(name + "@" +domain);
			
			//- Delete the <domain> element
			Node prev = domainNode.getPreviousSibling();
			 if (prev != null && 
			     prev.getNodeType() == Node.TEXT_NODE &&
			     prev.getNodeValue().trim().length() == 0) {
				 credentialsNode.removeChild(prev);
			 }
			credentialsNode.removeChild(domainNode);

//			- Delete the hide=”on” attribute in the <password> parameter
			Element element = (Element) doc.getElementsByTagName("password").item(0);
			element.removeAttribute("hide");
			 
//			- Search for a <via> element in the root node and rename that to a <protocol> element and move it after the <credentials>
			Node auth = doc.getElementsByTagName("authenticate").item(0);
			Node viaNode = (Element) doc.getElementsByTagName("via").item(0);
			doc.renameNode(viaNode, viaNode.getNamespaceURI(), "protocol");
			Node protocolNode = (Element) doc.getElementsByTagName("protocol").item(0);
			Node viaProCopy = protocolNode.cloneNode(true);
			prev = protocolNode.getPreviousSibling();
			 if (prev != null && 
			     prev.getNodeType() == Node.TEXT_NODE &&
			     prev.getNodeValue().trim().length() == 0) {
				 auth.removeChild(prev);
			 }
			auth.removeChild(protocolNode);
			Node credNode = (Element) doc.getElementsByTagName("credentials").item(0);
			auth.insertBefore(viaProCopy, credNode.getNextSibling());
		} catch (Exception e) {
			LOGGER.debug("Exception happened: " +e.getMessage());
		}
		return doc;
	}

	public boolean writeXMLFile(Document doc) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			doc .setXmlStandalone(true); 
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("./output.xml"));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			LOGGER.debug("Transformer Configuration Exception: " + e.getMessage());
			return false;
		} catch (TransformerException e) {
			LOGGER.debug("Transformer Exception: " + e.getMessage());
			return false;
		}
		return true;
	}
}
