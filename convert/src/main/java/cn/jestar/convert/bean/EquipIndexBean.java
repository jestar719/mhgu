package cn.jestar.convert.bean;

import java.util.List;

/**
 * 装备一览
 * Created by 花京院 on 2019/9/4.
 */

public class EquipIndexBean {
   private String url;
   private List<String> equipNameList;
   private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getEquipNameList() {
        return equipNameList;
    }

    public void setEquipNameList(List<String> equipNameList) {
        this.equipNameList = equipNameList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
