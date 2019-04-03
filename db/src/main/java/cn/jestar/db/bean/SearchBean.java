package cn.jestar.db.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by 花京院 on 2019/3/21.
 */
@Entity()
public class SearchBean {
    @NonNull
    @PrimaryKey
    private String name;

    public SearchBean() {
    }

    @Ignore
    public SearchBean(@NonNull String name) {
        this.name = name;
    }


    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
