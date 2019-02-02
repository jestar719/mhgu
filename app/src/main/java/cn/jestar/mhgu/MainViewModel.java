package cn.jestar.mhgu;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import cn.jestar.db.IndexDao;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.bean.IndexBean;


/**
 * Created by 花京院 on 2019/1/17.
 */

public class MainViewModel extends ViewModel {

    private LiveData<List<IndexBean>> mTyps;
    private LiveData<List<IndexBean>> mSelects;
    private IndexDao mDao;
    private MutableLiveData<Integer> mSelectType = new MutableLiveData<>();
    private MutableLiveData<Integer> mSelectParent = new MutableLiveData<>();
    private int mType;

    public MainViewModel() {
        mDao = MyDataBase.getInstace().getDao();
        mTyps = Transformations.switchMap(mSelectType, new Function<Integer, LiveData<List<IndexBean>>>() {
            @Override
            public LiveData<List<IndexBean>> apply(Integer input) {
                return mDao.queryByType(mType);
            }
        });
        mSelects = Transformations.switchMap(mSelectParent, new Function<Integer, LiveData<List<IndexBean>>>() {
            @Override
            public LiveData<List<IndexBean>> apply(Integer input) {
                return mDao.queryTypeWithParent(mType, input);
            }
        });
    }

    public void onTypeSelect(int type) {
        mType = type;
        mSelectType.setValue(type);
    }

    public void onTypeParentSelect(int parent) {
        mSelectParent.setValue(parent);
    }

    public void observerType(LifecycleOwner lifecycle, Observer<List<IndexBean>> observer) {
        mTyps.observe(lifecycle, observer);
    }

    public void observerParent(LifecycleOwner owner, Observer<List<IndexBean>> observer) {
        mSelects.observe(owner, observer);
    }

}
