package cn.jestar.mhgu.splash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.jestar.mhgu.MainActivity;
import cn.jestar.mhgu.R;

/**
 * 欢迎页，用于初始化
 * Created by 花京院 on 2019/1/28.
 */

public class SplashActivity extends AppCompatActivity {
    private long mStartTime;
    private long mDuration = 1500;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        mView = findViewById(R.id.fl_splash);
        mStartTime = System.currentTimeMillis();
        SplashModel model = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(SplashModel.class);
        model.init(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInit) {
                if (isInit) {
                    toMain();
                } else {
                    // TODO: 2019/1/28 初始化失败
                    finish();
                }
            }
        });
    }

    /**
     * 跳转到主页。确保在Splash界面停留一定时间{@link #mDuration}
     */
    private void toMain() {
        long duration = System.currentTimeMillis() - mStartTime;
        if (duration >= mDuration) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toMain();
                }
            }, mDuration - duration);
        }
    }
}
