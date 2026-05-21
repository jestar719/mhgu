package cn.jestar.mhgu;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;
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

    private IndexDao mDao;
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
    }

    public void onTypeSelect(int type) {
        mType = type;
        mSelectType.setValue(type);
    }

    public void onTypeParentSelect(int parent) {
        mSelectParent.setValue(parent);
    }

    public void observerType(LifecycleOwner lifecycle, Observer<List<IndexBean>> observer) {
        mSelectType.observe(lifecycle, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer input) {
                mDao.queryByType(mType).observe(lifecycle, observer);
            }
        });
    }

    public void observerParent(LifecycleOwner owner, Observer<List<IndexBean>> observer) {
        mSelectParent.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer input) {
                if (input != null) {
                    mDao.queryTypeWithParent(mType, input).observe(owner, observer);
                }
            }
        });
    }

    public void observerMenuSelect(LifecycleOwner owner, Observer<String> observer) {
        mMenuSelect.observe(owner, observer);
    }

    public void observerTagSelect(LifecycleOwner owner, Observer<String> observer) {
        mSelectTag.observe(owner, observer);
    }

    public void observerHistory(LifecycleOwner owner, Observer<List<SearchBean>> observer) {
        mSearchData.observe(owner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String input) {
                if (input != null) {
                    mDao.search(input).observe(owner, observer);
                }
            }
        });
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

    public void searchHistory(String text) {
        if (TextUtils.isEmpty(text)) {
            text = "null";
        }
        mSearchData.setValue("%" + text + "%");
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
