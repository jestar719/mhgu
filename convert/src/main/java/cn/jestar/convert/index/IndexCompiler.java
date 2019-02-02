package cn.jestar.convert.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import cn.jestar.convert.Constants;
import cn.jestar.convert.bean.LinkInfo;
import cn.jestar.convert.utils.RegexUtils;

/**
 * Created by 花京院 on 2019/1/29.
 */

public class IndexCompiler extends UrlCompiler {
    private LinkInfo mLinkInfo;
    private int size = 17;

    public IndexCompiler() {
        super(Constants.INDEX);
    }

    @Override
    protected void onRead(BufferedReader reader) throws IOException {
        int num = 0;
        File file = new File(Constants.TEMP_PATH);
        String nameTemp = "%s.json";
        while (num < size) {
            String s = reader.readLine();
            if (s.contains(START_FLAG)) {
                mLinkInfo = new LinkInfo();
            } else if (s.contains(END_FLAG)) {
                File file1 = new File(file, String.format(nameTemp, mLinkInfo.getName()));
                write(mLinkInfo, file1);
                num++;
            } else if (s.contains(NAME_FLAG)) {
                setName(reader, s, mLinkInfo);
            } else if (s.contains(TD)) {
                setLink(s, mLinkInfo);
            }
        }
    }

    @Override
    public void setLink(String text, LinkInfo linkInfo) {
        if (text.contains(SPLIT)) {
            for (String s : text.split(SPLIT)) {
                setLink(s, linkInfo);
            }
        } else {
            super.setLink(text, linkInfo);
        }
    }

    public void setName(BufferedReader reader, String s, LinkInfo linkInfo) throws IOException {
        String name;
        if (s.endsWith(NAME_END)) {
            name = RegexUtils.getMatchText(s, NAME_REGEX);
        } else {
            name = reader.readLine().trim();
        }
        linkInfo.setName(name);
    }
}
