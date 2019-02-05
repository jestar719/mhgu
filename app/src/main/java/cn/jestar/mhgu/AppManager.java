package cn.jestar.mhgu;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.xsj.crasheye.Crasheye;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 用于提供生命周期为全局的工具的管理类
 * Created by 花京院 on 2019/1/28.
 */

public class AppManager {
    private static final String APP_KEY = "15dcbba0";
    private static Application APP;
    private static Executor sExecutor;

    public static void init(Application application) {
        APP = application;
        sExecutor = Executors.newSingleThreadExecutor();
        Crasheye.setAppVersion(BuildConfig.VERSION_NAME);
        Crasheye.init(application, APP_KEY);
    }

    public static Application getApp() {
        return APP;
    }

    public static SharedPreferences getSp(String name) {
        return APP.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static Executor getExecutor() {
        return sExecutor;
    }
}
