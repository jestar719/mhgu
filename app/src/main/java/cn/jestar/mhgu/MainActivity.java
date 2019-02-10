package cn.jestar.mhgu;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;
import java.util.Set;

import cn.jestar.db.bean.IndexBean;
import cn.jestar.mhgu.version.VersionBean;
import cn.jestar.mhgu.version.VersionLiveData;
import cn.jestar.mhgu.web.WebViewManager;

/**
 * 主页。通过WebView浏览内置HTML攻略。通过上方搜索来定位，通过侧拉菜单来索引跳转
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawer;
    private MainViewModel mModel;
    private WebViewManager mWebViewManager;
    private EditText mEtSearch;
    private ImageView mIvDelete;
    private View mLlSearchResult;
    private TextView mTvSearchResult;
    private ArrayAdapter<String> mAdapter;
    private TagFlowLayout mFlType;
    private TagFlowLayout mFlSelect;
    private FlowAdapter mTypeAdapter;
    private FlowAdapter mSelectAdapter;
    private VersionBean mVersion;
    private AlertDialog mDialog;
    private TextView mTvVersion;
    private VersionLiveData mVersionLiveData;
    private String mString;
    private boolean isVersionGetting;
    private View mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        mWebViewManager = new WebViewManager((WebView) findViewById(R.id.web));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebViewManager.toTop();
            }
        });
        mDrawer = findViewById(R.id.drawer_layout);
        findViewById(R.id.rl_menu).setOnClickListener(this);
        initSearch();
        initLeftMenu();
        getVersion();
    }

    /**
     * 初始化侧拉菜单
     */
    private void initLeftMenu() {
        ListView view = findViewById(R.id.list_index);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mAdapter.addAll(getResources().getStringArray(R.array.types));
        view.setAdapter(mAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mModel.onTypeSelect(position + 1);
                mFlSelect.setAdapter(new FlowAdapter(null));
            }
        });
        initFlowLayout();
    }

    /**
     * 初始化单选标签框，从数据库中获取对应选择的标签
     */
    private void initFlowLayout() {
        mFlType = findViewById(R.id.tfl_type);
        mFlSelect = findViewById(R.id.tfl_select);
        mFlType.setMaxSelectCount(1);
        mFlSelect.setMaxSelectCount(1);
        mFlType.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                int index = getIndex(selectPosSet);
                if (index >= 0)
                    mModel.onTypeParentSelect(mTypeAdapter.getItem(index).getId());
            }
        });
        mModel.observerType(this, new Observer<List<IndexBean>>() {
            @Override
            public void onChanged(@Nullable List<IndexBean> indexBeans) {
                mTypeAdapter = new FlowAdapter(indexBeans);
                mFlType.setAdapter(mTypeAdapter);
            }
        });
        mModel.observerParent(this, new Observer<List<IndexBean>>() {
            @Override
            public void onChanged(@Nullable List<IndexBean> indexBeans) {
                mSelectAdapter = new FlowAdapter(indexBeans);
                mFlSelect.setAdapter(mSelectAdapter);
            }
        });
    }

    private void initSearch() {
        mEtSearch = findViewById(R.id.et_search);
        mIvDelete = findViewById(R.id.iv_delete);
        mLlSearchResult = findViewById(R.id.ll_search_result);
        mTvSearchResult = findViewById(R.id.tv_search_result);
        mIvDelete.setOnClickListener(this);
        findViewById(R.id.iv_pre).setOnClickListener(this);
        findViewById(R.id.iv_next).setOnClickListener(this);
        findViewById(R.id.iv_drawer_toggle).setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);
        mWebViewManager.getLiveDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTvSearchResult.setText(s);
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch();
                    return true;
                }
                return false;
            }
        });
        mWebViewManager.toIndex();
        findViewById(R.id.tv_search).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mWebViewManager.back()) {

        } else if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_delete:
                onDelete();
                break;
            case R.id.iv_search:
                onSearch();
                break;
            case R.id.iv_pre:
                toNext(false);
                break;
            case R.id.iv_next:
                toNext(true);
                break;
            case R.id.iv_drawer_toggle:
                onOpenDrawer();
                break;
            case R.id.tv_search:
                searchByTag();
                break;
            case R.id.tv_version:
                if (mVersion == null) {
                    if (!isVersionGetting) {
                        onGetVersion(true);
                        mModel.getVersion();
                    }
                } else {
                    if (mVersion.getVersion() > BuildConfig.VERSION_CODE) {
                        getDialog().show();
                    }
                }
                break;
        }
    }

    private void onGetVersion(boolean isStart) {
        isVersionGetting = isStart;
        mProgress.setVisibility(isStart ? View.VISIBLE : View.GONE);
        if (isStart) {
            mTvVersion.setText(R.string.version_getting);
        }
    }

    private void searchByTag() {
        IndexBean bean = getSelectItem(mFlSelect, mSelectAdapter);
        if (bean == null) {
            bean = getSelectItem(mFlType, mTypeAdapter);
            if (bean == null) {
                // TODO: 2019/1/29
                return;
            } else {
                if (bean.getUrl() == null) {
                    // TODO: 2019/1/29
                    return;
                }
            }
        }
        mWebViewManager.navigate(bean.getUrl());
        mDrawer.closeDrawers();
    }

    private IndexBean getSelectItem(TagFlowLayout layout, FlowAdapter adapter) {
        Set<Integer> selectedList = layout.getSelectedList();
        if (selectedList.isEmpty()) {
            return null;
        } else {
            return adapter.getItem(getIndex(selectedList));
        }
    }

    private int getIndex(Set<Integer> set) {
        if (set.isEmpty())
            return RESULT_OK;
        return (int) set.toArray()[0];
    }


    private void onOpenDrawer() {
        if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawers();
        } else {
            mDrawer.openDrawer(Gravity.LEFT);
        }
    }

    private void toNext(boolean isNext) {
        mWebViewManager.searchNext(isNext);
    }

    private void onSearch() {
        String searchText = mEtSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(searchText)) {
            mWebViewManager.search(searchText);
            mIvDelete.setVisibility(View.VISIBLE);
            mLlSearchResult.setVisibility(View.VISIBLE);
        }
        mEtSearch.clearFocus();
        mDrawer.requestFocus();
    }

    private void onDelete() {
        mEtSearch.setText("");
        mWebViewManager.search(null);
        mIvDelete.setVisibility(View.GONE);
        mLlSearchResult.setVisibility(View.GONE);
    }


    private void getVersion() {
        mTvVersion = findViewById(R.id.tv_version);
        mProgress = findViewById(R.id.pb);
        mString = getString(R.string.version_temp);
        mTvVersion.setVisibility(View.VISIBLE);
        setCurrentVersion();
        mTvVersion.setOnClickListener(this);
        onGetVersion(true);
        mModel.getVersion().observe(this, new Observer<VersionBean>() {
            @Override
            public void onChanged(@Nullable VersionBean versionBean) {
                onGetVersion(false);
                mVersion = versionBean;
                if (versionBean == null) {
                    setCurrentVersion();
                } else {
                    if (versionBean.getVersion() > BuildConfig.VERSION_CODE) {
                        mTvVersion.setText(R.string.has_new_version);
                        getDialog().show();
                    } else {
                        setCurrentVersion();
                    }
                }
            }
        });
    }

    private void setCurrentVersion() {
        mTvVersion.setText(String.format(mString, BuildConfig.VERSION_NAME));
    }

    private Dialog getDialog() {
        if (mDialog == null) {
            String string = getString(R.string.dialog_temp);
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialog.dismiss();
                    if(which==DialogInterface.BUTTON_POSITIVE){
                        update(FileConstans.UPDATE_URL);
                    }else if (which==DialogInterface.BUTTON_NEGATIVE){
                        update(FileConstans.GITHUB+mVersion.getTitle());
                    }
                }
            };
            mDialog = new AlertDialog.Builder(this)
                    .setTitle(String.format(string, mVersion.getTitle()))
                    .setMessage(mVersion.getMsg())
                    .setCancelable(true)
                    .setPositiveButton(R.string.fir_update, listener)
                    .setNegativeButton(R.string.github_update, listener)
                    .setNeutralButton(R.string.cancel,null)
                    .create();
        }
        return mDialog;
    }

    private void update(String updateUrl) {
        Uri uri = Uri.parse(updateUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
