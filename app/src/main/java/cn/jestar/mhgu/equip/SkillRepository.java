package cn.jestar.mhgu.equip;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.SparseArray;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jestar.db.JsonUtils;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.SkillDao;
import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.BaseSkill;
import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.EquipSkill;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.SingleSkillEquip;
import cn.jestar.db.bean.Skill;
import cn.jestar.db.bean.SkillEffect;
import cn.jestar.mhgu.AppManager;

/**
 * 技能，装备数据操作类
 * Created by 花京院 on 2019/10/6.
 */

public class SkillRepository {
    private SkillDao mDao;
    private HashMap<String, Skill> mSkills = new HashMap<>();
    private SparseArray<Equip> mEquips = new SparseArray<>();
    private SparseArray<Jewelry> mJewelries = new SparseArray<>();
    private MutableLiveData<Equip> mEquipData = new MutableLiveData<>();
    private MutableLiveData<List<Jewelry>> mJewelriesData = new MutableLiveData<>();
    private MutableLiveData<List<Skill>> mSkillsData = new MutableLiveData<>();
    private MutableLiveData<List<BaseEquip>> mEquipsData = new MutableLiveData<>();
    private MutableLiveData<EquipSetDetail> mRecodeData = new MutableLiveData<>();
    private File mFile;
    private List<EquipSetRecode> mDefaultRecodes;
    private List<EquipSetRecode> mRecodes;

    public SkillRepository() {
        mDao = MyDataBase.getInstance().getSkillDao();
        mFile = new File(Environment.getExternalStorageDirectory(), Constans.FILE_DIR);
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        mFile = new File(mFile, Constans.RECODE_FILE);
        initEquipSet();
    }

    /**
     * 初始化配装备数据,从本地文件及SP中读取配装数据
     */
    private void initEquipSet() {
        getDefaultRecodes();
        SharedPreferences sp = AppManager.getSp(Constans.RECODE_NAME);
        String string = sp.getString(Constans.RECODE_NAME, null);
        if (string == null) {
            if (mFile.exists()) {
                try {
                    mRecodes = getRecodes(new FileReader(mFile));
                } catch (IOException e) {
                    e.printStackTrace();
                    mRecodes = new ArrayList<>();
                }
            } else {
                mRecodes = new ArrayList<>();
            }
        } else {
            mRecodes = JsonUtils.getList(string, EquipSetRecode.class);
            sortRecodes(mRecodes);
        }
    }

    private void getDefaultRecodes() {
        try (InputStream open = AppManager.getApp().getAssets().open(Constans.RECODE_FILE)) {
            InputStreamReader reader = new InputStreamReader(open);
            mDefaultRecodes = getRecodes(reader);
        } catch (IOException e) {
            e.printStackTrace();
            mDefaultRecodes = new ArrayList<>();
        }

    }

    private List<EquipSetRecode> getRecodes(Reader reader) {
        List<EquipSetRecode> list = JsonUtils.getList(reader, EquipSetRecode.class);
        sortRecodes(list);
        return list;
    }

    private void sortRecodes(List<? extends BaseEquipSet> recodes) {
        Collections.sort(recodes, new Comparator<BaseEquipSet>() {
            @Override
            public int compare(BaseEquipSet o1, BaseEquipSet o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
    }

    /**
     * 异步获取完整的技能数据
     *
     * @param equip 基础技能数据
     */
    public void getEquip(BaseEquip equip) {
        int id = equip.getId();
        Equip equip1 = mEquips.get(id);
        if (equip1 == null) {
            queryInThread(new EquipQuery(id));
        } else {
            mEquipData.setValue(equip1);
        }
    }

    /**
     * 根据查询事件获取装备列表
     *
     * @param query 装备查询事件
     */
    public void getEquips(QueryEvent query) {
        queryInThread(new EquipsQuery(query));
    }

    /**
     * 根据id同步获取装备数据
     *
     * @param id 装备id
     * @return 装备数据
     */
    private Equip queryEquipSync(int id) {
        Equip equip = mDao.getEquipById(id);
        return getEquipSync(equip);
    }

    /**
     * 根据基础装备数据查询装备技能,缓存并返回完整的装备数据
     *
     * @param baseEquip 基础装备数据
     * @return 完整的装备数据
     */
    private Equip getEquipSync(BaseEquip baseEquip) {
        List<EquipSkill> skills = mDao.getEquipSkills(baseEquip.getId());
        Equip equip = new Equip();
        equip.copy(baseEquip);
        equip.setSkills(skills);
        mEquips.append(equip.getId(), equip);
        for (EquipSkill skill : skills) {
            String name = skill.getName();
            getSkillSync(name);
        }
        return equip;
    }

    /**
     * 同步获取已缓存的技能
     *
     * @param name 技能名
     * @return 完整技能数据
     */
    public Skill getCachedSkill(String name) {
        return mSkills.get(name);
    }

    /**
     * 根据名称同步获取技能
     *
     * @param name 技能名
     */
    public void getSkillSync(String name) {
        if (!mSkills.containsKey(name))
            querySkillSync(name);
    }

    /**
     * 根据名称同步查询完技能并缓存
     *
     * @param name 技能名
     */
    private void querySkillSync(String name) {
        if (name == null)
            return;
        List<Skill> list = mDao.getSkill(name);
        if (list == null || list.isEmpty()) {
            throw new RuntimeException(name);
        } else {
            Skill skill = list.get(0);
            List<SkillEffect> effects = mDao.getSkillEffects(name);
            Collections.sort(effects, new Comparator<SkillEffect>() {
                @Override
                public int compare(SkillEffect o1, SkillEffect o2) {
                    return o2.getValue() - o1.getValue();
                }
            });
            skill.setEffectList(effects);
            List<Jewelry> jewelries = mDao.getJewelries(name);
            skill.setJewelryList(jewelries);
            mSkills.put(name, skill);
        }
    }

    /**
     * 异步查询技能列表
     *
     * @param type  技能类型
     * @param input 技能名
     */
    public void getSkills(int type, String input) {
        queryInThread(new SkillQuery(type, input));
    }

    /**
     * 根据技能名称获取技能珠
     *
     * @param skillName 技能名称
     */
    public void getJewelry(String skillName) {
        Skill skill = mSkills.get(skillName);
        if (skill != null) {
            mJewelriesData.postValue(skill.getJewelryList());
        } else {
            queryInThread(new JewelryQuery(skillName));
        }
    }

    /**
     * 根据id同步获取技能珠
     *
     * @param id 技能珠id
     * @return 技能珠
     */
    private Jewelry queryJewelrySync(int id) {
        Jewelry jewelry = mJewelries.get(id);
        if (jewelry == null) {
            jewelry = mDao.getJewelryById(id);
        }
        return jewelry;
    }

    /**
     * 根据类型获取技能珠列表
     *
     * @param type 类型
     */
    public void queryJewelries(int type) {
        queryInThread(new JewelryTypeQuery(type));
    }

    /**
     * 检查技能珠的技能是否被缓存
     *
     * @param jewelry 技能珠
     * @return true 表示已经缓存，false反之
     */
    public boolean isCachedSkill(Jewelry jewelry) {
        if (mSkills.get(jewelry.getSkillName()) == null) {
            return false;
        } else {
            return jewelry.getDebuffValue() == 0 || mSkills.get(jewelry.getDebuff()) != null;
        }
    }

    /**
     * 根据技能珠同步查询技能
     *
     * @param jewelry 技能珠
     */
    public void querySkillByJewelrySync(Jewelry jewelry) {
        getSkillSync(jewelry.getSkillName());
        if (jewelry.getDebuffValue() > 0) {
            getSkillSync(jewelry.getDebuff());
        }
    }


    /**
     * 保存配装到SP及本地
     *
     * @param recode 配装数据
     */
    public void saveEquipSet(EquipSetRecode recode) {
        mRecodes.add(0, recode);
        SharedPreferences sp = AppManager.getSp(Constans.RECODE_NAME);
        String value = JsonUtils.toString(mRecodes);
        sp.edit().putString(Constans.RECODE_NAME, value).apply();
        try {
            FileWriter writer = new FileWriter(mFile);
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步查询详细配装数据
     *
     * @param recode 配装数据
     */
    public void loadEquipSet(EquipSetRecode recode) {
        queryInThread(new EquipRecodeQuery(recode));
    }

    public List<EquipSetRecode> getRecodes(boolean isDefault) {
        return isDefault ? mDefaultRecodes : mRecodes;
    }

    public LiveData<List<Skill>> getSkillsData() {
        return mSkillsData;
    }

    public LiveData<Equip> getEquipData() {
        return mEquipData;
    }

    public LiveData<List<Jewelry>> getJewelriesData() {
        return mJewelriesData;
    }

    public LiveData<EquipSetDetail> getRecodeData() {
        return mRecodeData;
    }

    public LiveData<List<BaseEquip>> getEquipsData() {
        return mEquipsData;
    }

    public LiveData<List<String>> getSkillNames(String input) {
        if (input == null) {
            input = "null";
        }
        return mDao.getSkillNames("%" + input + "%");
    }

    /**
     * 异步查询
     *
     * @param query 查询动作
     */
    private void queryInThread(Runnable query) {
        AppManager.getExecutor().execute(query);
    }

    /**
     * 技能查询并投递
     */
    class SkillQuery implements Runnable {
        private String mName;
        private int mType;

        public SkillQuery(int type, String name) {
            mName = name;
            mType = type;
        }

        @Override
        public void run() {
            List<Skill> skills;
            if (mName == null) {
                skills = mDao.getSkillByType(mType);
            } else {
                skills = mDao.getLikedSkill("%" + mName + "%");
            }
            mSkillsData.postValue(skills);
        }
    }


    /**
     * 根据技能名查找技能珠并投递
     */
    class JewelryQuery implements Runnable {
        private String mName;

        public JewelryQuery(String name) {
            mName = "%" + name + "%";
        }

        @Override
        public void run() {
            List<Jewelry> jewelries = mDao.getLikedJewelries(mName);
            mJewelriesData.postValue(jewelries);
        }
    }

    /**
     * 根据技能名查找技能珠并投递
     */
    class JewelryTypeQuery implements Runnable {
        private int mType;

        public JewelryTypeQuery(int type) {
            mType = type;
        }

        @Override
        public void run() {
            List<Jewelry> jewelries = mDao.getJewelriesByType(mType);
            mJewelriesData.postValue(jewelries);
        }
    }

    /**
     * 装备查询并缓存
     */
    class EquipQuery implements Runnable {

        private int mId;

        public EquipQuery(int id) {
            mId = id;
        }

        @Override
        public void run() {
            Equip equip = queryEquipSync(mId);
            mEquipData.postValue(equip);
        }
    }

    /**
     * 装备列表查询
     */
    class EquipsQuery implements Runnable {

        private QueryEvent mQuery;

        public EquipsQuery(QueryEvent query) {
            mQuery = query;
        }

        @Override
        public void run() {
            int sex = mQuery.getSex();
            int type = mQuery.getType();
            String input = mQuery.getInput();
            List<BaseEquip> list = new ArrayList<>();
            if (input == null) {
                input = "";
            }
            if (mQuery.getEquipQueryType() == QueryEvent.QUERY_TYPE.QUERY_BY_NAME) {
                List<Equip> equips = mDao.getEquips(type, sex, "%" + input + "%");
                list.addAll(equips);
            } else {
                List<SingleSkillEquip> equips = mDao.getSingleSkillEquips(type, sex, input);
                list.addAll(equips);
            }
            mEquipsData.postValue(list);
        }
    }

    /**
     * 详细配装数据查询
     */
    class EquipRecodeQuery implements Runnable {
        private EquipSetRecode mRecode;

        public EquipRecodeQuery(EquipSetRecode recode) {
            mRecode = recode;
        }

        @Override
        public void run() {
            EquipSetDetail detail = new EquipSetDetail(mRecode);
            Set<String> set = new HashSet<>();
            int[] ids = mRecode.getIds();
            Equip[] equips = new Equip[Constans.EQUIP_SUM];
            for (int i = 0; i < Constans.EQUIP_SUM; i++) {
                int id = ids[i];
                if (id != 0) {
                    Equip equip = queryEquipSync(id);
                    equips[i] = equip;
                    for (EquipSkill skill : equip.getSkills()) {
                        set.add(skill.getName());
                    }
                }
            }
            detail.setEquips(equips);
            List<int[]> jewelryIds = mRecode.getJewelryIds();
            List<List<Jewelry>> list = new ArrayList<>();
            for (int[] jewelryId : jewelryIds) {
                List<Jewelry> array = new ArrayList<>();
                for (int i : jewelryId) {
                    if (i != 0) {
                        Jewelry jewelry = queryJewelrySync(i);
                        array.add(jewelry);
                        set.add(jewelry.getSkillName());
                        set.add(jewelry.getDebuff());
                    }
                }
                list.add(array);
            }
            detail.setJewelries(list);
            List<BaseSkill> values = mRecode.getSkillValues();
            Skill[] skills = new Skill[Constans.SKILL_NUM];
            for (BaseSkill value : values) {
                if (value != null)
                    set.add(value.getName());
            }
            for (String s : set) {
                querySkillSync(s);
            }
            int size = values.size();
            for (int i = 0; i < size; i++) {
                BaseSkill skill = values.get(i);
                if (skill != null) {
                    Skill skill1 = mSkills.get(skill.getName());
                    skill1 = new Skill(skill1);
                    skill1.setValue(skill.getValue());
                    skills[i] = skill1;
                }
            }
            detail.setSkills(skills);
            mRecodeData.postValue(detail);
        }
    }

}
