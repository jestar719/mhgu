package cn.jestar.mhgu.equip;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jestar.db.bean.BaseBean;
import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.Skill;
import cn.jestar.mhgu.R;
import cn.jestar.mhgu.search.QueryHistoryAdapter;
import cn.jestar.mhgu.search.WebViewManager;

import static cn.jestar.db.bean.BaseEquip.PART.AMULET;
import static cn.jestar.db.bean.BaseEquip.PART.WEAPON;
import static cn.jestar.mhgu.equip.BaseEvent.Type.EQUIP;
import static cn.jestar.mhgu.equip.BaseEvent.Type.JEWELRY;
import static cn.jestar.mhgu.equip.BaseEvent.Type.SKILL;

/**
 * 配装页面
 * Created by 花京院 on 2019/10/4.
 */

public class EquipSelectActivity extends AppCompatActivity implements OnSelectEventListener, View.OnClickListener, Observer<MenuSelectEvent> {

    public static final int DEFAULT = -1;
    private SelectEvent mSelect;
    private EquipModel mModel;
    private TextView[] mMenus = new TextView[3];
    private EquipManager[] mEquipManagers = new EquipManager[5];
    private BaseEquip mEquip;
    private Jewelry mJewelry;
    private Skill mSkill;
    private String[] mMenuTitles;
    private BaseEquipFragment[] mFragments;
    private AmuletManager mAmuletManager;
    private JewelryManager mWeaponManager;
    private View mEquipSetInput;
    private EditText mEtSkill;
    private EquipSkillAdapter mAdapter;
    private String mEquipValuesTemp;
    private TextView mEquipValues;
    private View mEquipLoadDialog;
    private EquipRecodeAdapter mRecodeAdapter;
    private BaseEquipFragment mCurrentFrg;
    private SearchView mSearchView;
    private WebViewManager mWebViewManager;
    private Toolbar mToolbar;
    private String[] mSearchTitle;
    private QueryHistoryAdapter<String> mHistoryAdapter;
    private EditText mEtAmuletSkill;
    private View mCardWeb;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_select);
        initModel();
        mEquipValues = findViewById(R.id.tv_equip_values);
        mEquipValues.setText(String.format(mEquipValuesTemp, 0, 0, 0, 0, 0, 0, 0));
        initToolbar();
        initMenu();
        initWeb();
        initEquips();
        initEquipSaveDialog();
        initEquipLoadDialog();
        initRecycler();
        initFragment();
    }

    private void initWeb() {
        mCardWeb = findViewById(R.id.card_web);
        WebView mWeb = findViewById(R.id.web);
        mWebViewManager = new WebViewManager(mWeb);
    }

    private void initFragment() {
        mSearchTitle = getResources().getStringArray(R.array.search);
        mFragments = new BaseEquipFragment[3];
        EquipFragment frg = new EquipFragment();
        frg.getUrlData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mWebViewManager.navigate(s);
                mCardWeb.setVisibility(View.VISIBLE);
            }
        });
        mFragments[EQUIP] = frg;
        mFragments[JEWELRY] = new JewelryFragment();
        mFragments[SKILL] = new SkillFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (BaseEquipFragment fragment : mFragments) {
            transaction.add(R.id.frg_container, fragment).hide(fragment);
        }
        transaction.commitNow();
    }


    private void initModel() {
        mModel = ViewModelProviders.of(this).get(EquipModel.class);
        mModel.observerEvent(this, this);
        mModel.observerSumSkill(this, new Observer<EquipSetValue>() {
            @Override
            public void onChanged(@Nullable EquipSetValue value) {
                mAdapter.setList(value.getSumSkillList());
                setEquipValue(value.getEquipValues());
            }
        });
        mModel.observerEquipSet(this, new Observer<EquipSetDetail>() {
            @Override
            public void onChanged(@Nullable EquipSetDetail detail) {
                loadEquipSet(detail);
            }
        });
        mModel.observerEquip(this);
        mEquipValuesTemp = getString(R.string.equip_values);
    }

    /**
     * 设置显示装备汇总数据
     *
     * @param equipValues 装备江总数据
     */
    private void setEquipValue(int[] equipValues) {
        String text = String.format(mEquipValuesTemp,
                equipValues[0],
                equipValues[1],
                equipValues[2],
                equipValues[3],
                equipValues[4],
                equipValues[5],
                equipValues[6]
        );
        mEquipValues.setText(text);
    }

    private void initRecycler() {
        ViewGroup view = findViewById(R.id.skill_value_title);
        String[] strings = getResources().getStringArray(R.array.skill_value_title);
        int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView child = (TextView) view.getChildAt(i);
            child.setText(strings[i]);
        }
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new EquipSkillAdapter();
        recycler.setAdapter(mAdapter);
    }

    /**
     * 初始化保存配装的弹框
     */
    private void initEquipSaveDialog() {
        mEquipSetInput = findViewById(R.id.dialog_edit_name);
        mEtSkill = mEquipSetInput.findViewById(R.id.et_skill);
        mEtAmuletSkill = mEquipSetInput.findViewById(R.id.et_amulet_skill);
        mEquipSetInput.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skill = mEtSkill.getText().toString();
                String amuletSkill = mEtAmuletSkill.getText().toString();
                if (TextUtils.isEmpty(skill)) {
                    Toast.makeText(EquipSelectActivity.this, R.string.null_input_alert, Toast.LENGTH_SHORT).show();
                } else {
                    EquipSetRecode recode = new EquipSetRecode();
                    if (amuletSkill == null)
                        amuletSkill = "";
                    recode.setAmuletSkill(amuletSkill);
                    recode.setEquipSkill(skill);
                    mModel.saveEquipSet(recode);
                    mEquipSetInput.setVisibility(View.GONE);
                    mEtAmuletSkill.setText(null);
                    mEtSkill.setText(null);
                }
            }
        });
        mEquipSetInput.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEquipSetInput.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化加载配装的菜单
     */
    private void initEquipLoadDialog() {
        mEquipLoadDialog = findViewById(R.id.dialog_load_equip_recode);
        View view = mEquipLoadDialog.findViewById(R.id.tv_load_cancel);
        view.setSelected(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEquipLoadDialog.setVisibility(View.GONE);
            }
        });
        RecyclerView rv = mEquipLoadDialog.findViewById(R.id.rv_load_equip);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecodeAdapter = new EquipRecodeAdapter(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEquipLoadDialog.setVisibility(View.GONE);
                EquipSetRecode item = mRecodeAdapter.getItem(position);
                mModel.loadEquipSet(item);
            }
        });
        rv.setAdapter(mRecodeAdapter);
    }

    /**
     * 初始化选择菜单（装备，技能，技能珠）
     */
    private void initMenu() {
        ViewGroup view = findViewById(R.id.select_menu);
        int count = view.getChildCount();
        mMenuTitles = getResources().getStringArray(R.array.select_menu);
        for (int i = 0; i < count; i++) {
            TextView child = (TextView) view.getChildAt(i);
            child.setTag(i);
            mMenus[i] = child;
            child.setText(mMenuTitles[i]);
            child.setOnClickListener(this);
        }
    }

    /**
     * 初始化装备部位的按键
     */
    private void initEquips() {
        int[] ids = {
                R.id.equip_item_head,
                R.id.equip_item_body,
                R.id.equip_item_hand,
                R.id.equip_item_leg,
                R.id.equip_item_foot
        };
        int[] images = {
                R.mipmap.ic_head,
                R.mipmap.ic_body,
                R.mipmap.ic_hand,
                R.mipmap.ic_leg,
                R.mipmap.ic_foot
        };
        for (int i = 0; i < ids.length; i++) {
            ViewGroup view = findViewById(ids[i]);
            EquipManager manager = new EquipManager(i, view, this);
            mEquipManagers[i] = manager;
            manager.setImg(images[i]);
        }
        mAmuletManager = new AmuletManager(AMULET, (ViewGroup) findViewById(R.id.equip_item_amulet), this);
        mAmuletManager.setImg(R.mipmap.ic_jewelry);
        mWeaponManager = new JewelryManager(BaseEquip.PART.WEAPON, (ViewGroup) findViewById(R.id.equip_item_weapon), this);
        mWeaponManager.setImg(R.mipmap.ic_weapon);
    }

    /**
     * 初始化顶部菜单
     */
    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mCurrentFrg == null || mCurrentFrg.isHidden()) {
                    int itemId = item.getItemId();
                    switch (itemId) {
                        case R.id.menu_clear_all:
                            clearAll();
                            mModel.clearAll();
                            break;
                        case R.id.menu_clear_equip:
                            clearEquip();
                            break;
                        case R.id.menu_clear_jewelry:
                            clearJewelry();
                            break;
                        case R.id.menu_load:
                            onLoadEquipSet(false);
                            break;
                        case R.id.menu_load_default:
                            onLoadEquipSet(true);
                            break;
                        case R.id.menu_save:
                            mEquipSetInput.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 初始化搜索框及右上菜单
     *
     * @param menu 右上菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_equip_select, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        initSearch();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 初始化搜索.初始化自动完成并设置查询监听
     */
    private void initSearch() {
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        initAutoComplete();
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setQuery(null);
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
                mSearchView.setQuery(mHistoryAdapter.getItem(position), true);
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mHistoryAdapter.clear();
                mModel.getLikedSkill(newText);
                return false;
            }
        });
    }

    /**
     * 自动完成的初始化
     * 设置Adapter及相关
     */
    public void initAutoComplete() {
        AutoCompleteTextView completeTextView = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mHistoryAdapter = new QueryHistoryAdapter<>(this, R.layout.list_item, 0);
        completeTextView.setThreshold(1);
        completeTextView.setAdapter(mHistoryAdapter);
        mModel.observeHistory(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                mHistoryAdapter.clear();
                if (list != null) {
                    mHistoryAdapter.addAll(list);
                }
            }
        });
    }


    /**
     * 设置搜索文本
     *
     * @param query 搜索文本
     */
    private void setQuery(String query) {
        if (mCurrentFrg != null && !mCurrentFrg.isHidden())
            mCurrentFrg.onQuery(query);
    }

    /**
     * 返回键的处理
     */
    @Override
    public void onBackPressed() {
        if (mCardWeb.getVisibility() == View.VISIBLE) {
            mCardWeb.setVisibility(View.GONE);
        } else if (mEquipLoadDialog.getVisibility() == View.VISIBLE) {
            mEquipLoadDialog.setVisibility(View.GONE);
        } else if (mCurrentFrg != null && !mCurrentFrg.isHidden()) {
            getSupportFragmentManager().beginTransaction().hide(mCurrentFrg).commit();
            setToolbarTitle(DEFAULT);
            mModel.setEquipQueryType(false);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 显示保存的配置
     *
     * @param isDefault true表示常用配装,false表示自定义配装
     */
    private void onLoadEquipSet(boolean isDefault) {
        List<EquipSetRecode> recodes = mModel.getRecodes(isDefault);
        mRecodeAdapter.setList(recodes);
        mEquipLoadDialog.setVisibility(View.VISIBLE);
    }

    /**
     * 装备栏的点击事件,如果有选择好的装备,技能,技能珠,则添加,
     * 否则移除指定的装备,技能,技能珠或打开选择菜单
     *
     * @param event 点击事件
     */
    @Override
    public void onSelectEvent(SelectEvent event) {
        boolean remove = event.isRemove();
        int type = event.getType();
        if (remove) {
            mModel.onRemove(event);
            canAdd(event, type);
        } else {
            if (!canAdd(event, type)) {
                mSelect = event;
                showSelectPanel(type);
            }
        }
    }

    /**
     * 被选择的部位能否添加装备,技能,技能珠
     *
     * @param event 点击事件
     * @param type  部位的类别
     * @return true表示可以添加, false反之
     */
    private boolean canAdd(SelectEvent event, int type) {
        int part = event.getPart();
        boolean isSave = false;
        if (type == EQUIP && mEquip != null && mEquip.getPart() == part) {
            mEquipManagers[part].addEquip(mEquip);
            mModel.addEquip(mEquip);
            mEquip = null;
            resetMenuText(type);
            isSave = true;
        } else if (type == JEWELRY && mJewelry != null) {
            JewelryManager manager;
            if (part < AMULET) {
                manager = mEquipManagers[part];
            } else if (part == AMULET) {
                manager = mAmuletManager;
            } else {
                manager = mWeaponManager;
            }
            manager.setJewelry(event.getIndex(), mJewelry);
            mModel.addJewelry(mJewelry, part);
            mJewelry = null;
            resetMenuText(type);
            isSave = true;
        } else if (type == SKILL) {
            BaseSkill skill = event.getSkill();
            if (skill != null) {
                mModel.setSkill(skill, event.getIndex());
                isSave = true;
            } else if (mSkill != null) {
                mAmuletManager.setSkill(mSkill, event.getIndex());
                mSkill = null;
                resetMenuText(type);
                isSave = true;
            }

        }
        return isSave;
    }

    /**
     * 重置菜单文本
     *
     * @param type 类型
     */
    private void resetMenuText(int type) {
        mMenus[type].setText(mMenuTitles[type]);
    }

    /**
     * 菜单的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        showSelectPanel(index);
    }

    /**
     * 根据类型显示选择菜单（装备，技能珠，技能）
     *
     * @param type 类型{@link SelectEvent.Type}
     */
    private void showSelectPanel(int type) {
        resetMenuText(type);
        if (type == EQUIP) {
            mEquip = null;
        } else if (type == JEWELRY) {
            mJewelry = null;
        } else if (type == SKILL) {
            mSkill = null;
        }
        showFragment(type);
    }

    /**
     * 显示选择菜单
     *
     * @param type 类型
     */
    private void showFragment(int type) {
        BaseEquipFragment fragment = mFragments[type];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFrg != fragment) {
            if (mCurrentFrg != null) {
                transaction.hide(mCurrentFrg);
            }
            transaction.show(fragment).commitNow();
            mCurrentFrg = fragment;
        } else {
            transaction.show(fragment).commit();
        }
        setToolbarTitle(type);
        if (mSelect != null && type == EQUIP) {
            if (!(mCurrentFrg instanceof EquipFragment)) {
                Log.e("frg", String.valueOf(type) + mCurrentFrg.getClass().getSimpleName());
            }
            ((EquipFragment) mCurrentFrg).setPart(mSelect.getPart());
        }
        int queryType = mCurrentFrg.getQueryType();
        mModel.setEquipQueryType(queryType == QueryEvent.QUERY_TYPE.QUERY_BY_SKILL);
    }

    /**
     * 菜单选择事件的监听
     *
     * @param event 菜单选择事件
     */
    @Override
    public void onChanged(@Nullable MenuSelectEvent event) {
        mModel.setEquipQueryType(false);
        setToolbarTitle(DEFAULT);
        if (event != null) {
            int type = event.getType();
            getSupportFragmentManager().beginTransaction().hide(mCurrentFrg).commit();
            switch (type) {
                case EQUIP:
                    BaseEquip equip = event.getEquip();
                    setEquip(equip);
                    break;
                case JEWELRY:
                    Jewelry jewelry = event.getJewelry();
                    setJewelry(jewelry);
                    break;
                case SKILL:
                    Skill skill = event.getSkill();
                    setSkill(skill);
                    break;
                default:
                    break;
            }
            mSelect = null;
        }
    }

    /**
     * 设置顶部文本
     *
     * @param type 类型
     */
    private void setToolbarTitle(int type) {
        boolean isDefault = type < 0;
        if (isDefault) {
            mSearchView.onActionViewCollapsed();
            mToolbar.setTitle(R.string.equip_set);
        } else {
            mToolbar.setTitle(mSearchTitle[type]);
        }
    }

    /**
     * 加载保存的配装
     *
     * @param detail 配装
     */
    private void loadEquipSet(EquipSetDetail detail) {
        List<List<Jewelry>> jewelries = detail.getJewelries();
        Skill[] skills = detail.getSkills();
        mAmuletManager.clear();
        addJewelries(jewelries.get(AMULET), mAmuletManager);
        mWeaponManager.clear();
        addJewelries(jewelries.get(WEAPON), mWeaponManager);
        int length = skills.length;
        for (int i = 0; i < length; i++) {
            Skill skill = skills[i];
            if (skill != null)
                mAmuletManager.setSkill(skill, i);
        }
        BaseEquip[] equips = detail.getEquips();
        for (int i = 0; i < equips.length; i++) {
            BaseEquip equip = equips[i];
            EquipManager manager = mEquipManagers[i];
            manager.clear();
            manager.addEquip(equip);
            addJewelries(jewelries.get(i), manager);
        }
    }

    /**
     * 添加技能珠
     *
     * @param jewelries 技能珠列表
     * @param manager   部件管理类
     */
    private void addJewelries(List<Jewelry> jewelries, JewelryManager manager) {
        if (jewelries != null) {
            int size = jewelries.size();
            for (int i = 0; i < size; i++) {
                Jewelry jewelry = jewelries.get(i);
                manager.setJewelry(i, jewelry);
            }
        }
    }

    /**
     * 清空所有配装
     */
    private void clearAll() {
        for (EquipManager manager : mEquipManagers) {
            manager.clear();
        }
        mWeaponManager.clear();
        mAmuletManager.clear();
        mModel.clearAll();
    }

    /**
     * 清空所有技能珠
     */
    private void clearJewelry() {
        for (EquipManager manager : mEquipManagers) {
            manager.clearJewelry();
        }
        mWeaponManager.clearJewelry();
        mAmuletManager.clearJewelry();
        mModel.clearJewelry();
    }

    /**
     * 清空所有装备
     */
    private void clearEquip() {
        for (EquipManager manager : mEquipManagers) {
            manager.clear();
        }
        mModel.clearEquip();
    }

    /**
     * 设置技能
     *
     * @param skill 技能
     */
    private void setSkill(Skill skill) {
        mSkill = skill;
        if (skill != null && isTargetType(SKILL) && mSelect.getPart() == AMULET) {
            mAmuletManager.setSkill(skill, mSelect.getIndex());
            mSkill = null;
        }
        setMenuText(mSkill, SKILL);
    }

    /**
     * 设置技能珠（添加/移除/覆盖）
     *
     * @param jewelry 技能珠
     */
    private void setJewelry(Jewelry jewelry) {
        mJewelry = jewelry;
        if (jewelry != null && isTargetType(JEWELRY)) {
            int part = mSelect.getPart();
            JewelryManager manager;
            if (part == AMULET) {
                manager = mAmuletManager;
            } else if (WEAPON == part) {
                manager = mWeaponManager;
            } else {
                manager = mEquipManagers[part];
            }
            if (manager.setJewelry(mSelect.getIndex(), jewelry)) {
                mModel.addJewelry(jewelry, part);
                mJewelry = null;
            }
        }
        setMenuText(mJewelry, JEWELRY);
    }

    /**
     * 设置装备
     *
     * @param equip 装备
     */
    private void setEquip(BaseEquip equip) {
        mEquip = equip;
        if (equip != null && isTargetType(EQUIP) && mSelect.getPart() == equip.getPart()) {
            EquipManager manager = mEquipManagers[mSelect.getPart()];
            manager.addEquip(equip);
            mModel.addEquip(equip);
            mEquip = null;
        }
        setMenuText(mEquip, EQUIP);
    }

    /**
     * 设置选择按键的文本,如果有选好的装备,技能,技能珠,则显示其名称.否则显示默认文本
     *
     * @param bean 选择的数据
     * @param type 类型 {@link SelectEvent.Type}
     */
    private void setMenuText(BaseBean bean, int type) {
        TextView menu = mMenus[type];
        menu.setText(bean == null ? mMenuTitles[type] : bean.getName());
    }

    /**
     * 装备部位的点击匹配
     *
     * @param type 类型  {@link SelectEvent.Type}
     * @return true表示匹配, false反之
     */
    private boolean isTargetType(int type) {
        return mSelect != null && mSelect.getType() == type;
    }
}
