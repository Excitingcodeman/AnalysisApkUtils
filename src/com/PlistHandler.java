package com;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static com.ConfigString.*;

/**
 * @author 黄俊杰
 * 2018/6/27
 * .plist配置文件的解析器
 * 转载自：http://archive.cnblogs.com/a/2391527/
 */
public class PlistHandler extends DefaultHandler {
    //是否是根节点
    private boolean isRootElement = false;
    //是否是key的节点
    private boolean keyElementBegin = false;

    private String key;

    Stack<Object> stack = new Stack<Object>();

    private boolean valueElementBegin = false;

    private Object root;

    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getMapResult() {
        return (HashMap<String, Object>) root;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getArrayResult() {
        return (List<Object>) root;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (IOS_ELEMENT_PLIST.equals(qName)) {
            isRootElement = true;
        }

        if (IOS_ELEMENT_DICT.equals(qName)) {
            if (isRootElement) {
                stack.push(new HashMap<String, Object>());
                isRootElement = !isRootElement;
            } else {
                Object object = stack.peek();
                HashMap<String, Object> dict = new HashMap<String, Object>();

                if (object instanceof ArrayList) {
                    ((ArrayList<Object>) object).add(dict);
                } else if (object instanceof HashMap) {
                    ((HashMap<String, Object>) object).put(key, dict);
                }
                stack.push(dict);
            }
        }

        if (IOS_ELEMENT_KEY.equals(qName)) {
            keyElementBegin = true;
        }
        if ("true".equals(qName)) {
            HashMap<String, Object> parent = (HashMap<String, Object>) stack.peek();
            parent.put(key, true);
        }
        if ("false".equals(qName)) {
            HashMap<String, Object> parent = (HashMap<String, Object>) stack.peek();
            parent.put(key, false);
        }
        if (IOS_ELEMENT_ARRAY.equals(qName)) {
            if (isRootElement) {
                ArrayList<Object> obj = new ArrayList<Object>();
                stack.push(obj);
                isRootElement = !isRootElement;
            } else {
                HashMap<String, Object> parent = (HashMap<String, Object>) stack.peek();
                ArrayList<Object> obj = new ArrayList<Object>();
                stack.push(obj);
                parent.put(key, obj);
            }
        }
        if (IOS_ELEMENT_STRING.equals(qName)) {
            valueElementBegin = true;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length > 0) {
            if (keyElementBegin) {
                key = new String(ch, start, length);
            }
            if (valueElementBegin) {
                if (HashMap.class.equals(stack.peek().getClass())) {
                    HashMap<String, Object> parent = (HashMap<String, Object>) stack.peek();
                    String value = new String(ch, start, length);
                    parent.put(key, value);
                } else if (ArrayList.class.equals(stack.peek().getClass())) {
                    ArrayList<Object> obj = (ArrayList<Object>) stack.peek();
                    String value = new String(ch, start, length);
                    obj.add(value);
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (IOS_ELEMENT_PLIST.equals(qName)) {

        }
        if (IOS_ELEMENT_KEY.equals(qName)) {
            keyElementBegin = false;
        }
        if (IOS_ELEMENT_STRING.equals(qName)) {
            valueElementBegin = false;
        }

        if (IOS_ELEMENT_ARRAY.equals(qName)) {
            root = stack.pop();
        }
        if (IOS_ELEMENT_DICT.equals(qName)) {
            root = stack.pop();
        }
    }
}
