package cn.jestar.mhgu.version;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import cn.jestar.db.JsonUtils;
import cn.jestar.mhgu.App;
import cn.jestar.mhgu.AppManager;

/**
 * 检查更新的LiveData
 * Created by 花京院 on 2019/2/7.
 */

public class VersionLiveData extends MutableLiveData<VersionBean> {
    private static final String LINK = "https://raw.githubusercontent.com/jestar719/mhgu/master/version.json";

    /**
     * 获取版本更新数据并保存
     */
    public void getVersion() {
        AppManager.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                VersionBean bean = null;
                try {
                    URL url = new URL(LINK);
                    InputStream stream = url.openStream();
                    InputStreamReader reader = new InputStreamReader(stream, "utf-8");
                    bean = JsonUtils.fromString(reader, VersionBean.class);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    logE(e);
                }
                postValue(bean);
            }
        });
    }


    private void logE(Exception e) {
        Log.e(App.TAG, e.getMessage());
    }
}
