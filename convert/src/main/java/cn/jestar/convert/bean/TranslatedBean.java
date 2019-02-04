package cn.jestar.convert.bean;

import java.util.List;
import java.util.Map;

/**
 * 用与翻译的Bean
 * Created by 花京院 on 2019/2/4.
 */

public class TranslatedBean {
    private List<String> urls;
    private Map<String, String> texts;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Map<String, String> getTexts() {
        return texts;
    }

    public void setTexts(Map<String, String> texts) {
        this.texts = texts;
    }
}
