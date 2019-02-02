package cn.jestar.convert.index;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.jestar.convert.bean.DbBean;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.JsonUtils;

/**
 * 读取List<LinkInfo>类型的Json文件，转换成对应的List<IndexBean> 并保存
 * Created by 花京院 on 2019/1/27.
 */

public class IndexConvert {
    private int size = 8;

    /**
     * 读取源文件，转换成List<IndexBean>,写入目标文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @param type   类型
     * @throws Exception {@link java.io.IOException},{@link ClassCastException},{@link NullPointerException}
     */
    public void convert2Json(File source, File target, int type) throws Exception {
        List<LinkInfo> read = read(source);
        List<DbBean> beans = linkInfo2DbBean(read, type);
        String json = JsonUtils.toString(beans);
        try (FileWriter writer = new FileWriter(target)) {
            writer.write(json);
            writer.close();
        }
    }

    /**
     * 读取源文件，解析成List<LinkInfo>
     *
     * @param file 源文件
     * @return List<LinkInfo>
     * @throws Exception {@link java.io.IOException},{@link ClassCastException},{@link NullPointerException}
     */
    public List<LinkInfo> read(File file) throws Exception {
        FileReader reader = new FileReader(file);
        return JsonUtils.toList(reader, LinkInfo.class);
    }

    /**
     * LinkInfo集合转换成List<IndexBean>,设置对应的类型，自动设置Id及其它数据
     *
     * @param list LinkInfo集合
     * @param type 类型
     * @return IndexBean集合
     */
    public List<DbBean> linkInfo2DbBean(List<LinkInfo> list, int type) {
        List<DbBean> beans = new ArrayList<>();
        int typeSuff = (type << (2 * size));
        int index = 1;
        int subIndex;
        for (LinkInfo linkInfo : list) {
            int i = typeSuff + (index << size);
            DbBean dbBean = createBean(linkInfo.getName(), type, i);
            beans.add(dbBean);
            int id = dbBean.getId();
            Map<String, String> data = linkInfo.getData();
            List<String> names = new ArrayList<>(data.keySet());
            subIndex = i + 1;
            Collections.sort(names);
            if (i==524544){
                System.out.println(names);
            }
            for (String s : names) {
                dbBean = createBean(s, type, subIndex).setUrl(data.get(s)).setParent(id);
                beans.add(dbBean);
                subIndex++;
            }
            index++;
        }
        return beans;
    }

    /**
     * 实例化IndexBean对象
     *
     * @param name  名称
     * @param type  类型
     * @param index 序号
     * @return IndexBean对象
     */
    private DbBean createBean(String name, int type, int index) {
        DbBean dbBean = new DbBean().setName(name).setType(type).setId(index);
        return dbBean;
    }

}
