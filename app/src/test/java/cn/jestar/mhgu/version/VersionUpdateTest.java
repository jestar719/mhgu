package cn.jestar.mhgu.version;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.jestar.db.JsonUtils;

/**
 * 创建更新信息
 * Created by 花京院 on 2019/2/5.
 */
public class VersionUpdateTest {
    @Test
    public void getVersion() throws Exception {
        int version = 111;
        String versionName = "1.1.1";
        updateVersionBean(version, versionName);
        updateVersion(version, versionName);
    }


    public void updateVersionBean(int version, String versionName) throws IOException {
        String fileName = "version.json";
        VersionBean bean = new VersionBean();
        bean.setVersion(version);
        bean.setTitle(versionName);
        StringBuilder builder = new StringBuilder("更新说明:");
        addUpdateMsg(builder);
        bean.setMsg(builder.toString());
        FileWriter writer = new FileWriter(fileName);
        writer.write(JsonUtils.toString(bean));
        writer.close();
    }

    private void addUpdateMsg(StringBuilder builder) {
        String separator = System.lineSeparator();
        builder.append(separator)
                .append("1 新增自动更新").append(separator)
                .append("2 新增崩溃日志收集");

    }

    private void updateVersion(int version, String versionName) throws IOException {
        String fileName = "config.gradle";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder builder = new StringBuilder();
        String separator = System.lineSeparator();
        String text;
        while ((text = reader.readLine()) != null) {
            if (text.contains("vCode")) {
                text = String.format("vCode=%s", version);
            } else if (text.contains("vName")) {
                text = String.format("vName='%s'", versionName);
            }
            builder.append(text).append(separator);
        }
        reader.close();
        FileWriter writer = new FileWriter(fileName);
        writer.write(builder.toString());
        writer.close();
    }

}
