package cn.jestar.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by 花京院 on 2019/1/29.
 */

public abstract class Compiler extends Copyable {

    protected File mFile;

    public Compiler() {
    }

    public Compiler(String path) {
        setFile(path);
    }

    public Compiler(File file) {
        mFile = file;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public void setFile(String path) {
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

}
