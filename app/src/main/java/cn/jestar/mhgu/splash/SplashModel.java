package cn.jestar.mhgu.splash;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import cn.jestar.db.DbConstants;
import cn.jestar.db.JsonUtils;
import cn.jestar.db.MyDataBase;
import cn.jestar.mhgu.App;
import cn.jestar.mhgu.AppManager;
import cn.jestar.mhgu.version.VersionBean;

/**
 * 始化逻辑类
 * Created by 花京院 on 2019/1/28.
 */

public class SplashModel extends ViewModel {
    private MutableLiveData<Boolean> mInitState = new MutableLiveData<>();

    /**
     * 从资源文件中复制数据库文件，保存当前数据库版本到SP。通过LiveData来传递状态
     *
     * @param owner
     * @param observer
     */
    public void init(LifecycleOwner owner, Observer<Boolean> observer) {
        mInitState.observe(owner, observer);
        AppManager.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                boolean isDbInit = initDb();
                MyDataBase.init(AppManager.getApp());
                mInitState.postValue(isDbInit);
            }
        });
    }

    /**
     * 数据库初始化
     *
     * @return true表示成功初始化，false反之
     */
    public boolean initDb() {
        try {
            Application app = AppManager.getApp();
            if (!checkInit(app)) {
                copyDb();
                SharedPreferences.Editor edit = AppManager.getSp(DbConstants.DB_NAME).edit();
                edit.putInt(DbConstants.DB_NAME, DbConstants.VERSION);
                edit.apply();
                Log.i(App.TAG, "数据库Copy成功");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logE(e);
            return false;
        }
    }

    /**
     * 检查是否数据库是否初始化完成
     *
     * @return true表示已经初始化完成，false反之
     */
    private boolean checkInit(Context context) {
        File dbFile = context.getDatabasePath(DbConstants.DB_NAME);
        int currentDbVersion = AppManager.getSp(DbConstants.DB_NAME).getInt(DbConstants.DB_NAME, 0);
        return dbFile.exists() && currentDbVersion >= DbConstants.VERSION;
    }

    /**
     * 从资源文夹中复制数据库到应用的data/data/database文件夹中
     *
     * @throws Exception
     */
    private void copyDb() throws Exception {
        Application app = AppManager.getApp();
        try (InputStream in = app.getAssets().open(DbConstants.DB_NAME);
             FileOutputStream out = new FileOutputStream(app.getDatabasePath(DbConstants.DB_NAME))) {
            int len;
            byte[] bytes = new byte[1024];
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            logE(e);
            throw e;
        }
    }

    private void logE(Exception e) {
        Log.e(App.TAG, e.getMessage());
    }


}
