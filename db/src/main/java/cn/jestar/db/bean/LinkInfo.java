package cn.jestar.db.bean;

import java.util.Map;

/**
 * Created by 花京院 on 2019/1/27.
 */

public class LinkInfo {
    private String mName;
    private Map<String, String> mData;

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
}
