package cn.jestar.mhgu;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import java.util.List;
import java.util.Set;

import cn.jestar.db.IndexDao;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.bean.IndexBean;
import cn.jestar.db.bean.SearchBean;
import cn.jestar.mhgu.version.VersionLiveData;

import static android.app.Activity.RESULT_OK;


/**
 * 主界面的逻辑管理类
 * Created by 花京院 on 2019/1/17.
 */

public class MainViewModel extends ViewModel {

    private final LiveData<List<SearchBean>> mSearchList;
    private IndexDao mDao;
    private LiveData<List<IndexBean>> mTyps;
    private LiveData<List<IndexBean>> mSelects;

    private MutableLiveData<String> mSelectTag = new MutableLiveData<>();
    private MutableLiveData<String> mMenuSelect = new MutableLiveData<>();
    private MutableLiveData<String> mSearchData = new MutableLiveData<>();
    private MutableLiveData<Integer> mSelectType = new MutableLiveData<>();
    private MutableLiveData<Integer> mSelectParent = new MutableLiveData<>();
    private VersionLiveData mVersionData = new VersionLiveData();
    private int mType;

    /**
     * 初始化.获取搜索历史的Dao,创建菜单相关的LivaData
     */
    public MainViewModel() {
        mDao = MyDataBase.getInstance().getDao();
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
        mSearchList = Transformations.switchMap(mSearchData, new Function<String, LiveData<List<SearchBean>>>() {
            @Override
            public LiveData<List<SearchBean>> apply(String input) {
                return mDao.search(input);
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

    public void observerMenuSelect(LifecycleOwner owner, Observer<String> observer) {
        mMenuSelect.observe(owner, observer);
    }

    public void observerTagSelect(LifecycleOwner owner, Observer<String> observer) {
        mSelectTag.observe(owner, observer);
    }

    public VersionLiveData getVersion() {
        mVersionData.getVersion();
        return mVersionData;
    }

    public void onTagSelect(String url) {
        mSelectTag.setValue(url);
    }

    public void onMenuSelect(String title) {
        mMenuSelect.setValue(title);
    }

    public int getIndex(Set<Integer> set) {
        if (set.isEmpty())
            return RESULT_OK;
        return (int) set.toArray()[0];
    }

    public LiveData<List<SearchBean>> onSearch(String text) {
        if (TextUtils.isEmpty(text)) {
            text = "null";
        }
        mSearchData.setValue("%" + text + "%");
        return mSearchList;
    }

    public void onPerformSearch(String query) {
        try {
            mDao.insert(new SearchBean(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSearchCount() {
        return mDao.getSearchCount();
    }
}
