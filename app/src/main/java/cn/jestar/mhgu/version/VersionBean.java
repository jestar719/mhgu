package cn.jestar.mhgu.version;

/**
 * 用于版本更新的数据
 * Created by 花京院 on 2019/2/5.
 */

public class VersionBean {
    private int version;
    private String msg;
    private String title;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
