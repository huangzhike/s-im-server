package mmp.im.gate.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class XML {

    private static DocumentBuilder builder;
    private static XPath xPath;

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xPath = XPathFactory.newInstance().newXPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element rootElement;

    public XML(String path) {
        Document document;
        try {
            document = builder.parse(new File(path));
        } catch (Exception e) {
            document = null;
        }

        rootElement = document.getDocumentElement();
    }

    public String get(String expression) throws Exception {

        // XPathExpression xPathExpression = xPath.compile("/gate/id");
        XPathExpression xPathExpression = xPath.compile(expression);
        NodeList nodeList = (NodeList) xPathExpression.evaluate(rootElement, XPathConstants.NODESET);
        Element element = (Element) nodeList.item(0);

        return element.getAttribute("value");
    }

}
