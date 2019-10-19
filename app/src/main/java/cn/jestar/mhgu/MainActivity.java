package cn.jestar.mhgu;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jestar.db.bean.SearchBean;
import cn.jestar.mhgu.equip.EquipSelectActivity;
import cn.jestar.mhgu.index.IndexFragment;
import cn.jestar.mhgu.search.QueryHistoryAdapter;
import cn.jestar.mhgu.search.WebViewManager;
import cn.jestar.mhgu.version.UpdateFragment;
import cn.jestar.mhgu.version.VersionBean;

/**
 * 主页。通过WebView浏览内置HTML攻略。通过上方搜索来定位，通过侧拉菜单来索引跳转
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private MainViewModel mModel;
    private WebViewManager mWebViewManager;
    private VersionBean mVersion;
    private boolean isVersionGetting;
    private IndexFragment mIndexFragment;
    private UpdateFragment mUpdateFragment;
    private Fragment mCurrentFragment;
    private View mIvNewVersion;
    private SearchView mSearchView;
    private View mFabContainer;
    private View mFab;
    private QueryHistoryAdapter<SearchBean> mAdapter;
    private int mRequestCode = 0x19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebViewManager = new WebViewManager((WebView) findViewById(R.id.web));
        mModel = ViewModelProviders.of(this).get(MainViewModel.class);
        initFragment();
        initVersion();
        initLeftMenu();
        initSearch();
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        mIndexFragment = new IndexFragment();
        mUpdateFragment = new UpdateFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frg_container, mIndexFragment)
                .hide(mIndexFragment)
                .add(R.id.frg_container, mUpdateFragment)
                .hide(mUpdateFragment)
                .commit();
    }

    /**
     * 显示Fragment
     *
     * @param isUpdate true表示显示更新弹框
     */
    private void showFragment(boolean isUpdate) {
        mFab.setVisibility(View.GONE);
        Fragment fragment = isUpdate ? mUpdateFragment : mIndexFragment;
        if (fragment == mCurrentFragment)
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        mCurrentFragment = fragment;
        transaction.show(mCurrentFragment).commit();
    }

    /**
     * 隐藏Fragment
     */
    private void hideFragment() {
        mFab.setVisibility(View.VISIBLE);
        if (mCurrentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
            mCurrentFragment = null;
        }
    }

    /**
     * 初始化侧拉菜单
     */
    private void initLeftMenu() {
        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigation = mDrawer.findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        View headerView = initVersion(navigation);
        mIvNewVersion = headerView.findViewById(R.id.iv_new_version);
        NavigationMenuView menuView = (NavigationMenuView) navigation.getChildAt(0);
        menuView.setVerticalScrollBarEnabled(false);
        navigation.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化菜单中的版本信息
     *
     * @param navigation
     * @return
     */
    @NonNull
    private View initVersion(NavigationView navigation) {
        View headerView = navigation.getHeaderView(0);
        headerView.findViewById(R.id.ll_version).setOnClickListener(this);
        TextView view = headerView.findViewById(R.id.tv_version);
        String version = getString(R.string.version_temp);
        view.setText(String.format(version, BuildConfig.VERSION_NAME));
        mModel.getVersion().observe(this, new Observer<VersionBean>() {
            @Override
            public void onChanged(@Nullable VersionBean versionBean) {
                mVersion = versionBean;
                if (versionBean != null && versionBean.getVersion() > BuildConfig.VERSION_CODE) {
                    mIvNewVersion.setVisibility(View.VISIBLE);
                    update();
                }
            }
        });
        return headerView;
    }

    /**
     * 初始化搜索
     */
    private void initSearch() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open, R.string.close);
        toggle.syncState();
        mDrawer.setDrawerListener(toggle);
        mFab = findViewById(R.id.fab_top);
        mFab.setOnClickListener(this);
        findViewById(R.id.fab_up).setOnClickListener(this);
        findViewById(R.id.fab_down).setOnClickListener(this);
        mFabContainer = findViewById(R.id.ll_fab_container);
        //根据搜索结果 显示/隐藏图标
        mWebViewManager.getLiveDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mFabContainer.setVisibility(s == null ? View.GONE : View.VISIBLE);
                Log.d("onChanged", String.format("FabContainerVisible= %s,SearchResult= %s", mFabContainer.getVisibility() == View.VISIBLE, s));
            }
        });
        mWebViewManager.toIndex();
        mModel.observerTagSelect(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                hideFragment();
                mWebViewManager.navigate(s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        initAutoComplete();
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mWebViewManager.search(null);
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                mSearchView.setQuery(mAdapter.getItem(position).toString(), true);
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    mModel.onPerformSearch(query);
                }
                mWebViewManager.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("onQueryTextChange", newText);
                mModel.searchHistory(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 自动完成的初始化
     * 设置Adapter及相关
     */
    public void initAutoComplete() {
        AutoCompleteTextView view = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mAdapter = new QueryHistoryAdapter<SearchBean>(this, R.layout.list_item, 0);
        view.setThreshold(1);
        view.setAdapter(mAdapter);
        mModel.observerHistory(this, new Observer<List<SearchBean>>() {
            @Override
            public void onChanged(@Nullable List<SearchBean> searchBeans) {
//                Log.e("onQueryTextChange", searchBeans.toString());
                mAdapter.clear();
                if (searchBeans != null) {
                    mAdapter.addAll(searchBeans);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null) {
            hideFragment();
        } else if (mWebViewManager.back()) {

        } else if (mDrawer.isDrawerOpen(Gravity.START)) {
            mDrawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_version:
                if (!isVersionGetting) {
                    mModel.getVersion();
                } else {
                    if (mVersion.getVersion() > BuildConfig.VERSION_CODE) {
                        mDrawer.closeDrawers();
                        update();
                    }
                }
                break;
            case R.id.fab_top:
                mWebViewManager.toTop();
                break;
            case R.id.fab_up:
                mWebViewManager.searchNext(false);
                break;
            case R.id.fab_down:
                mWebViewManager.searchNext(true);
                break;
        }
    }

    /**
     * 初始化版本更新
     */
    private void initVersion() {
        mModel.getVersion().observe(this, new Observer<VersionBean>() {
            @Override
            public void onChanged(@Nullable VersionBean versionBean) {
                mVersion = versionBean;
                isVersionGetting = versionBean != null;
                if (isVersionGetting && versionBean.getVersion() > BuildConfig.VERSION_CODE) {
                    mIvNewVersion.setVisibility(View.VISIBLE);
                    update();
                }
            }
        });
    }


    private void update() {
        showFragment(true);
    }

    /**
     * 菜单点击事件
     *
     * @param item 被点击的菜单
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int groupId = item.getGroupId();
        if (groupId == R.id.group_index) {
            showFragment(false);
            mModel.onMenuSelect((String) item.getTitle());
            mDrawer.closeDrawers();
        } else if (R.id.item_set_equip == item.getItemId()) {
            requestPermissions();
        }
        return false;
    }

    private void requestPermissions() {
        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (i != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, mRequestCode);
        } else {
            startActivity(new Intent(this, EquipSelectActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_alert, Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this, EquipSelectActivity.class));
        }
    }
}
