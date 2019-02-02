package cn.jestar.convert.bean;

/**
 * Created by 花京院 on 2019/1/27.
 */

public class DbBean {
    private int id;
    private int type;
    private String name;
    private String url;
    private int parent;

    public int getId() {
        return id;
    }

    public DbBean setId(int id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public DbBean setType(int type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public DbBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DbBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getParent() {
        return parent;
    }

    public DbBean setParent(int parent) {
        this.parent = parent;
        return this;
    }
}
