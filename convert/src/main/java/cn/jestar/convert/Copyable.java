package cn.jestar.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/2/2.
 */

public class Copyable {

    public void copy(File source, File target) {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(target)) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Object object, File file) {
        String s = JsonUtils.toString(object);
        write(file, s);
    }

    public void write(File file, String str) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
