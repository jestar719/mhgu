package cn.jestar.mhgu.splash;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jestar.db.DbConstants;
import cn.jestar.db.MyDataBase;
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
                    SharedPreferences.Editor edit = AppManager.getSp(DbConstants.DB_NAME).edit();
                    edit.putInt(DbConstants.DB_NAME, DbConstants.VERSION);
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
        try (InputStream in = app.getAssets().open(DbConstants.DB_NAME);
             FileOutputStream out = new FileOutputStream(app.getDatabasePath(DbConstants.DB_NAME))) {
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
