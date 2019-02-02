package cn.jestar.db;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import cn.jestar.db.bean.IndexBean;

/**
 * Created by 花京院 on 2019/1/28.
 */
@android.arch.persistence.room.Database(entities = {IndexBean.class}, version = DbConstants.VERSION, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    private static MyDataBase INSTANCE;


    public static MyDataBase getInstace() {
        return INSTANCE;
    }

    public static MyDataBase init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDataBase.class, DbConstants.DB_NAME).build();
        }
        return INSTANCE;
    }

    public abstract IndexDao getDao();
}
