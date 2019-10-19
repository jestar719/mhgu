package cn.jestar.mhgu.equip;

import cn.jestar.db.bean.BaseEquip;

/**
 * 查询的数据对象
 * Created by 花京院 on 2019/10/7.
 */

public class QueryEvent {
    @BaseEquip.TYPE
    private int type;
    private String input;
    @BaseEvent.Type
    private int queryType;
    @BaseEquip.SEX
    private int sex;
    @QUERY_TYPE
    private int equipQueryType;

    public QueryEvent(int queryType) {
        this.queryType = queryType;
    }

    public int getEquipQueryType() {
        return equipQueryType;
    }

    public void setEquipQueryType(int equipQueryType) {
        this.equipQueryType = equipQueryType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public @interface QUERY_TYPE {
        int QUERY_BY_NAME = 0;
        int QUERY_BY_SKILL = 1;
    }
}
