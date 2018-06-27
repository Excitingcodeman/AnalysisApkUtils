package com;

import org.dom4j.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 黄俊杰
 * 2018/6/27
 */
public class Dom4jHelper {
    /**
     * 创建一个android xml
     *
     * @param fileName 文件路径
     * @param list     数据源
     */
    public static void createXml(String fileName, List<XmlModelBean> list, String keyWord) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(ConfigString.ANDROID_ELEMENT_ROOT);
        for (XmlModelBean item : list) {
            if (null != item) {
                Element element = root.addElement(keyWord);
                List<Attribute> attributes = new ArrayList<>();
                Attribute attribute = new DOMAttribute(new QName(ConfigString.ANDROID_ELEMENT_NAME), item.getKey());
                attributes.add(attribute);
                element.setAttributes(attributes);
                element.setText(item.getValue());
            }
        }
        try {
            Writer fileWriter = new FileWriter(fileName);
            XMLWriter xmlWriter = new XMLWriter(fileWriter);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析xml
     *
     * @param fileName 文件路径
     * @return List<XmlModelBean>
     */
    public static List<XmlModelBean> parseXml(String fileName) {
        List<XmlModelBean> list = new ArrayList<>();
        File file = new File(fileName);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(file);
            Element rootElement = document.getRootElement();
            for (Iterator i = rootElement.elementIterator(); i.hasNext(); ) {
                Element next = (Element) i.next();
                Attribute attribute = next.attribute(0);
                list.add(new XmlModelBean(next.attribute(0).getValue(), next.getText()));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return list;
    }
}
