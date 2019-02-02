package cn.jestar.mhgu;

import android.app.Application;

/**
 * Created by 花京院 on 2019/1/28.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.init(this);
    }
}
