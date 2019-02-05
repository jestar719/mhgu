package cn.jestar.mhgu.version;

import org.junit.Test;

import java.io.FileWriter;

import cn.jestar.db.JsonUtils;

/**
 * 创建更新信息
 * Created by 花京院 on 2019/2/5.
 */
public class VersionBeanTest {
    @Test
    public void getVersion() throws Exception {
        String fileNmae = "version.json";
        int version = 111;
        String versionName = "1.1.1";
        VersionBean bean = new VersionBean();
        bean.setVersion(version);
        bean.setTitle(versionName);
        StringBuilder builder = new StringBuilder("更新说明:");
        addUpdateMsg(builder);
        bean.setMsg(builder.toString());
        FileWriter writer = new FileWriter(fileNmae);
        writer.write(JsonUtils.toString(bean));
        writer.close();
    }

    private void addUpdateMsg(StringBuilder builder) {
        String separator = System.lineSeparator();
        builder.append(separator)
                .append("1 新增自动更新").append(separator)
                .append("2 新增崩溃日志收集");

    }

}
