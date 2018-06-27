package com;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import static com.ConfigString.ANDROID_ELEMENT_NAME;

/**
 * 2018/6/27
 * xml解析  只解析单个值的属性值，单节点的xml  如android的strings.xml
 *
 * @author 黄俊杰
 */
public class XmlHandler extends DefaultHandler {

    private List<XmlModelBean> list;

    private XmlModelBean bean;
    /**
     * 关键字,需要解析的关键字段
     */
    private String key_word;
    /**
     * 用于储存读取的临时变量
     */
    private String tempString;

    public XmlHandler(String key_word) {
        this.key_word = key_word;
    }

    /**
     * 解析到文档开始调用，一般做初始化操作
     *
     * @throws SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<>();
        super.startDocument();
    }

    /**
     * 解析到文档末尾调用，一般做回收操作
     *
     * @throws SAXException
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * 每读到一个元素就调用该方法
     *
     * @param uri        uri
     * @param localName  localName
     * @param qName      当前的节点值
     * @param attributes 属性值
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (key_word.equals(qName)) {
            bean = new XmlModelBean();
            String name = attributes.getValue(ANDROID_ELEMENT_NAME);
            bean.setKey(name);
        }
        super.startElement(uri, localName, qName, attributes);
    }

    /**
     * 读到元素的结尾调用
     *
     * @param uri       uri
     * @param localName localName
     * @param qName     当前的节点值
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (key_word.equals(qName)) {
            bean.setValue(tempString);
            list.add(bean);
        }
        super.endElement(uri, localName, qName);
    }

    /**
     * 解析字段
     *
     * @param ch     字符数组
     * @param start  开始的位置
     * @param length 结束的位置
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempString = new String(ch, start, length);
        super.characters(ch, start, length);
    }

    /**
     * 返回解析的对象的集合
     *
     * @return 对象的集合
     */
    public List<XmlModelBean> getList() {
        return list;
    }
}
