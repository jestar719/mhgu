package cn.jestar.convert.bean;

import java.util.Map;
import java.util.TreeMap;

public class LinkInfo {
    private String mName;
    private Map<String, String> mData = new TreeMap<>();

    public String getName() {
        return mName;
    }

    public LinkInfo setName(String name) {
        mName = name;
        return this;
    }

    public Map<String, String> getData() {
        return mData;
    }

    public LinkInfo setData(Map<String, String> data) {
        mData = data;
        return this;
    }

    public LinkInfo setData(String key, String value) {
        mData.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "LinkInfo{" +
                "mName='" + mName + '\'' +
                ", mData=" + mData +
                '}';
    }
}

