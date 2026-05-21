package cn.jestar.db.bean;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
