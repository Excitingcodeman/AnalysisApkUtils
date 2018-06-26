package com;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * xml的解析工具
 */
public class XmlParseTools {

    public static final String file_path = "C:\\Users\\Administrator\\Desktop\\strings.xml";
    public static final String plist_path = "C:\\Users\\Administrator\\Desktop\\test.plist";

    /**
     * Android xml的根节点值
     */
    public static final String ANDROID_ELEMENT_ROOT = "resources";
    /**
     * Android xml的节点值
     */
    public static final String ANDROID_ELEMENT_NODE = "string";
    /**
     * Android xml节点下的属性值
     */
    public static final String ANDROID_ELEMENT_NAME = "name";

    public static void domParse(InputStream sources, String key, File newFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(sources);
            NodeList nodeList = document.getElementsByTagName(key);
            Document newDocument = builder.newDocument();
            newDocument.setXmlStandalone(true);
            Element root = newDocument.createElement(ANDROID_ELEMENT_ROOT);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String name = item.getAttributes().getNamedItem(ANDROID_ELEMENT_NAME).getNodeValue();
                Element element = newDocument.createElement(key);
                String value = item.getFirstChild().getNodeValue();
                element.setAttribute(ANDROID_ELEMENT_NAME, name);
                element.setTextContent(value);
                root.appendChild(element);
            }
            newDocument.appendChild(root);
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(document), new StreamResult(newFile));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析xml获取数据对象
     *
     * @param sources     xml 数据流
     * @param key         指定的节点
     * @param elementName 节点的属性值
     * @return 对象集合
     */
    public static List<XmlModelBean> domGetData(InputStream sources, String key, String elementName) {
        List<XmlModelBean> beanList = new ArrayList<XmlModelBean>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(sources);
            NodeList nodeList = document.getElementsByTagName(key);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String name = item.getAttributes().getNamedItem(elementName).getNodeValue();
                String value = item.getFirstChild().getNodeValue();
                beanList.add(new XmlModelBean(name, value));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 创建plist文件
     *
     * @param path     plist文件路径
     * @param version  plist文件版本号
     * @param dataList 需要的数据
     * @return TRUE 成功 FALSE 失败
     */
    public static boolean createPlist(String path, String version, List<XmlModelBean> dataList) {
        boolean success = true;
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
                return success;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        stringBuilder.append("\n");
        stringBuilder.append("<plist version=\"" + version + "\">");
        stringBuilder.append("\n");
        stringBuilder.append("<dict>");
        stringBuilder.append("\n");
        for (XmlModelBean item : dataList) {
            if (null != item) {
                stringBuilder.append("<key>" + item.getKey() + "</key>" + "\n");
                stringBuilder.append("<string>" + item.getValue() + "</string>" + "\n");
            }
        }
        stringBuilder.append("</dict>\n" +
                "</plist>\n");
        try {
            FileOutputStream output = new FileOutputStream(file);
            OutputStreamWriter writer;
            writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(stringBuilder.toString());
            writer.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }


    public static void main(String[] args) {
        File xml = new File(file_path);
        File file = new File("C:\\Users\\Administrator\\Desktop\\strings1.xml");
        if (xml.exists()) {
            try {
                InputStream in = new FileInputStream(xml);
//                domParse(in, ANDROID_ELEMENT_NODE, file);
                List<XmlModelBean> beanList = domGetData(in, ANDROID_ELEMENT_NODE, ANDROID_ELEMENT_NAME);
                createPlist(plist_path, "1.0", beanList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
