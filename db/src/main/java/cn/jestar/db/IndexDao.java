package cn.jestar.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import cn.jestar.db.bean.IndexBean;


/**
 * Created by 花京院 on 2019/1/28.
 */

@Dao
public interface IndexDao {
    @Insert
    void insert(List<IndexBean> list);

    @Query("SELECT count(*) FROM IndexBean")
    int getCount();

    @Query("SELECT * FROM IndexBean WHERE type = :type AND parent = 0")
    LiveData<List<IndexBean>> queryByType(int type);

    @Query("SELECT * FROM IndexBean WHERE type = :type AND parent = :parent")
    LiveData<List<IndexBean>> queryTypeWithParent(int type, int parent);
}
