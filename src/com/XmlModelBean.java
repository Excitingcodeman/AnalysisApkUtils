package com;

/**
 * @author 黄俊杰
 * 2018/6/27
 * 接受对象
 */
public class XmlModelBean {
    /**
     * key关键字
     */
    private String key;
    /**
     * 属性值
     */
    private String value;

    public XmlModelBean() {
    }

    public XmlModelBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
