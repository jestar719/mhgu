package cn.jestar.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import cn.jestar.db.bean.IndexBean;
import cn.jestar.db.bean.SearchBean;

/**
 * Created by 花京院 on 2019/1/28.
 */
@android.arch.persistence.room.Database(entities = {IndexBean.class, SearchBean.class}, version = DbConstants.VERSION, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    private static MyDataBase INSTANCE;


    public static MyDataBase getInstance() {
        return INSTANCE;
    }

    public static MyDataBase init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDataBase.class, DbConstants.DB_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(new Migration(1, 2) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL(
                                    "CREATE TABLE IF NOT EXISTS 'SearchBean' ('name' TEXT NOT NULL,PRIMARY KEY('name'))");
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    public abstract IndexDao getDao();
}
