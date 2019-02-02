package cn.jestar.mhgu.splash;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cn.jestar.db.DbConstance;
import cn.jestar.db.IndexDao;
import cn.jestar.db.JsonUtils;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.bean.IndexBean;
import cn.jestar.mhgu.AppManager;

/**
 * 数据库初始化
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
                try {
                    copyDb();
                    SharedPreferences.Editor edit = AppManager.getSp(DbConstance.DB_NAME).edit();
                    edit.putInt(DbConstance.DB_NAME, DbConstance.VERSION);
                    edit.apply();
                    MyDataBase.init(AppManager.getApp());
                    mInitState.postValue(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mInitState.postValue(false);
                }
            }
        });
    }

    /**
     * 从资源文夹中复制数据库到应用的data/data/database文件夹中
     *
     * @throws IOException
     */
    private void copyDb() throws IOException {
        Application app = AppManager.getApp();
        try (InputStream in = app.getAssets().open(DbConstance.DB_NAME);
             FileOutputStream out = new FileOutputStream(app.getDatabasePath(DbConstance.DB_NAME))) {
            int available = in.available();
            byte[] bytes = new byte[available];
            in.read(bytes);
            out.write(bytes);
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
