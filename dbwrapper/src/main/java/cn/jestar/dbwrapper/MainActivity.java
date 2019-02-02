package cn.jestar.dbwrapper;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.jestar.db.DbConstants;
import cn.jestar.db.IndexDao;
import cn.jestar.db.JsonUtils;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.bean.IndexBean;

public class MainActivity extends AppCompatActivity {

    private String mPath = "index";
    private MutableLiveData<String> mData;
    private String mTag = "Jestar";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = new MutableLiveData<>();
        mData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ((TextView) findViewById(R.id.tv_msg)).setText(s);
            }
        });
        initDb();
    }

    private void initDb() {
        File file = getDatabasePath(DbConstants.DB_NAME);
        if (file.exists()) {
            file.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetManager assets = getAssets();
                    String[] indexs = assets.list(mPath);
                    List<IndexBean> mList = new ArrayList<>();
                    for (String index : indexs) {
                        Log.i(mTag, index);
                        InputStream open = assets.open(mPath + "/" + index);
                        InputStreamReader reader = new InputStreamReader(open);
                        List<IndexBean> list = JsonUtils.toList(reader, IndexBean.class);
                        if (list == null || list.isEmpty()) {
                            Log.i(mTag, index + "解析错误");
                        }
                        reader.close();
                        mList.addAll(list);
                    }
                    IndexDao dao = MyDataBase.init(getApplicationContext()).getDao();
                    dao.insert(mList);
                    int count = dao.getCount();
                    mData.postValue(String.format("插入完成 数据共%s个", count));
                } catch (Exception e) {
                    e.printStackTrace();
                    mData.postValue(String.format("创建数据库失败，原因\n%s", e.getMessage()));
                }
            }
        }).start();
    }
}
