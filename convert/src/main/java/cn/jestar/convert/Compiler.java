package cn.jestar.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 解析器，用与解析HTML文档
 * Created by 花京院 on 2019/1/29.
 */

public abstract class Compiler extends Copyable {
    /**
     * HTML文档
     */
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
       setFile(new File(path));
    }

    /**
     * 读取文档
     */
    public final void readFile() {
        try (FileReader reader = new FileReader(mFile)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            onRead(bufferedReader);
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    /**
     * 文档读取方式的抽象，由子类实现
     * @param reader
     * @throws Exception
     */
    protected abstract void onRead(BufferedReader reader) throws Exception;

}
