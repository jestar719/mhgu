package cn.jestar.convert.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import cn.jestar.convert.Constans;
import cn.jestar.convert.bean.LinkInfo;

/**
 * Created by 花京院 on 2019/1/30.
 */

public class CatRiceCompiler extends UrlCompiler {

    private LinkInfo mLinkInfo;
    private String mName;

    public CatRiceCompiler() {
        super(Constans.MH_PATH + "data\\2506.html");
    }

    @Override
    protected void onRead(BufferedReader reader) throws IOException {
        while (true) {
            String s = reader.readLine();
            if (s.contains(START_FLAG)) {
                mName = "猫饭技能";
                mLinkInfo = new LinkInfo().setName(mName);
            } else if (s.contains(END_FLAG)) {
                File file = new File(Constans.TEMP_PATH);
                String name = mName + ".json";
                file = new File(file, name);
                write(mLinkInfo, file);
                return;
            } else if (s.contains(TD)) {
                if (mLinkInfo!=null){
                setLink(s, mLinkInfo);}
            }
        }
    }
}
