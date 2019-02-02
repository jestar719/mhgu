package cn.jestar.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import cn.jestar.convert.utils.JsonUtils;

/**
 * Created by 花京院 on 2019/1/29.
 */

public abstract class Compiler {

    private final File mFile;

    public Compiler(String path) {
        mFile = new File(path);
    }

    public void readFile() {
        try (FileReader reader = new FileReader(mFile)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            onRead(bufferedReader);
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    protected abstract void onRead(BufferedReader reader) throws Exception;

    protected void write(Object object,File file){
        String s = JsonUtils.toString(object);
        write(file,s);
    }

    protected void write(File file,String str){
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
