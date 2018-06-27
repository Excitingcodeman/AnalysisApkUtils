package com;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ConfigString.*;

/**
 * @author 黄俊杰
 * 2018/6/27
 */
public class DomParseTool {


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

    /**
     * @param root        根节点元素
     * @param key         子节点元素
     * @param elementName 子节点属性
     * @param dataList    数据集合
     * @param newFile     要写入的新文件
     * @return
     */
    public static boolean createAndroidXml(String root, String key, String elementName, List<XmlModelBean> dataList, File newFile) {
        boolean success = true;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlStandalone(true);
            Element rootElement = document.createElement(root);
            for (XmlModelBean item : dataList) {
                if (null != item) {
                    Element element = document.createElement(key);
                    element.setAttribute(elementName, item.getKey());
                    element.setTextContent(item.getValue());
                    rootElement.appendChild(element);
                }
            }
            document.appendChild(rootElement);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(newFile));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            success = false;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            success = false;
        } catch (TransformerException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * 获取项目根路径
     *
     * @return
     */
    public static String getRootPath() {
        String path = "";
        File file = new File("");
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public List<XmlModelBean> pull2Xml(InputStream is) {
        List<XmlModelBean> list = new ArrayList<>();
        XmlModelBean bean=null;

        return list;
    }




    public static void main(String[] args) {
        String root_path = getRootPath() + File.separator + "res" + File.separator;
        String resources = root_path + "strings.xml";
        File file = new File(resources);
        if (file.exists()) {
            try {
                InputStream in = new FileInputStream(file);
                List<XmlModelBean> beans = domGetData(in, ANDROID_ELEMENT_STRING, ANDROID_ELEMENT_NAME);
                if (null != beans) {
                    boolean success = createPlist(root_path + "test.plist", "1.0", beans);
                    boolean androidXml = createAndroidXml(ANDROID_ELEMENT_ROOT, ANDROID_ELEMENT_STRING, ANDROID_ELEMENT_NAME, beans, new File(root_path + "testString.xml"));
                    Dom4jHelper.parseXml(resources);
                    Dom4jHelper.createXml(root_path+"string2.xml",beans,ConfigString.ANDROID_ELEMENT_STRING);
                    System.out.println(success);
                    System.out.println(androidXml);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(root_path);

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
            PlistHandler plistHandler = new PlistHandler();
            saxParser.parse(new File(root_path + "test.plist"), plistHandler);
            HashMap<String, Object> mapResult = plistHandler.getMapResult();
            System.out.println(mapResult.toString());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlHandler xmlHandler = new XmlHandler(ConfigString.ANDROID_ELEMENT_STRING);
        try {
            SAXParser newSAXParser = saxParserFactory.newSAXParser();
            newSAXParser.parse(new File(root_path + "strings.xml"), xmlHandler);
            List<XmlModelBean> list = xmlHandler.getList();
            XmlModelBean xmlModelBean = list.get(0);
            System.out.println(xmlModelBean.getKey() + "-->" + xmlModelBean.getValue());
            System.out.println(list.toString());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



}
