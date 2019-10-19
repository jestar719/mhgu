package cn.jestar.db.bean;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 数据基类
 * Created by 花京院 on 2019/10/3.
 */

public class BaseBean implements Comparable<BaseBean> {
    @PrimaryKey
    public int id;
    public String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull BaseBean o) {
        return id - o.getId();
    }
}
