package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;

/**
 * 索引的数据结构
 * Created by 花京院 on 2019/1/27.
 */
@Entity
public class IndexBean extends BaseBean {
    private int type;
    private int parent;
    private String url;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
