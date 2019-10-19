package cn.jestar.dbwrapper;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.jestar.db.DbConstants;
import cn.jestar.db.IndexDao;
import cn.jestar.db.JsonUtils;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.SkillDao;
import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.EquipSkill;
import cn.jestar.db.bean.IndexBean;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.SearchBean;
import cn.jestar.db.bean.SingleSkillEquip;
import cn.jestar.db.bean.Skill;
import cn.jestar.db.bean.SkillEffect;

public class MainActivity extends AppCompatActivity {

    private String mPath = "index";
    private MutableLiveData<String> mData;
    private String mTag = "Jestar";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = new MutableLiveData<>();
        mData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ((TextView) findViewById(R.id.tv_msg)).setText(s);
            }
        });
        initDb();
    }

    private void initDb() {
        File file = getDatabasePath(DbConstants.DB_NAME);
        if (file.exists()) {
            file.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetManager assets = getAssets();
                    MyDataBase dataBase = MyDataBase.init(getApplicationContext());
                    IndexDao dao = dataBase.getDao();
                    List<IndexBean> mList = getIndex(assets);
                    initIndex(mList, dao);
                    SkillDao skillDao = dataBase.getSkillDao();
                    initEquip(assets, skillDao);
                    initSkill(assets, skillDao);
                    int count = dao.getCount();
                    mData.postValue(String.format("插入完成 数据共%s个", count));
                } catch (Exception e) {
                    e.printStackTrace();
                    mData.postValue(String.format("创建数据库失败，原因\n%s", e.getMessage()));
                }
            }
        }).start();
    }

    @NonNull
    private List<IndexBean> getIndex(AssetManager assets) throws IOException {
        String[] indexs = assets.list(mPath);
        List<IndexBean> mList = new ArrayList<>();
        SparseArray<IndexBean> array = new SparseArray<>();
        for (String index : indexs) {
            Log.i(mTag, index);
            String fileName = mPath + "/" + index;
            List<IndexBean> list = getListInJson(assets, fileName, IndexBean.class);
            if (list == null || list.isEmpty()) {
                Log.i(mTag, index + "解析错误");
            }
            mList.addAll(list);
        }
        for (IndexBean bean : mList) {
            int id = bean.getId();
            IndexBean indexBean = array.get(id);
            if (indexBean == null) {
                array.append(id, bean);
            } else {
                Log.i(mTag, bean.getName() + " " + indexBean.getName());
            }
        }
        return mList;
    }

    private void initSkill(AssetManager assets, SkillDao dao) throws IOException {
        List<Skill> skills = getListInJson(assets, "skills.json", Skill.class);
        for (Skill skill : skills) {
            int id = skill.getId() * 10;
            String name = skill.getName();
            int type = skill.getType() - 1;
            skill.setType(type);
            List<SkillEffect> effectList = skill.getEffectList();
            int size = effectList.size();
            for (int i = 0; i < size; i++) {
                SkillEffect effect = effectList.get(i);
                effect.setId(id + i);
                effect.setSkillName(name);
            }
            dao.addSkillEffect(effectList);
            List<Jewelry> jewelryList = skill.getJewelryList();
            if (jewelryList != null) {
                size = jewelryList.size();
                for (int i = 0; i < size; i++) {
                    Jewelry jewelry = jewelryList.get(i);
                    jewelry.setId(id + i);
                    jewelry.setType(type);
                    if (jewelry.getDebuffValue() == 0) {
                        jewelry.setDebuff(null);
                        Log.e("jewelry", jewelry.getName());
                    }
                }
                dao.addJewelry(jewelryList);
            }
        }
        dao.addSkill(skills);
    }

    private void initEquip(AssetManager assets, SkillDao skillDao) throws IOException {
        List<EquipSkill> equipSkills = getListInJson(assets, "equip_skills.json", EquipSkill.class);
        List<Equip> equips = getListInJson(assets, "equips.json", Equip.class);
        skillDao.addEquip(equips);
        skillDao.addEquipSkills(equipSkills);
        SparseArray<BaseEquip> array = new SparseArray<>();
        for (BaseEquip equip : equips) {
            array.append(equip.getId(), equip);
        }
        List<SingleSkillEquip> singleSkillEquips = new ArrayList<>(equips.size());
        int id = -1;
        int parent = -1;
        BaseEquip equip = null;
        for (EquipSkill skill : equipSkills) {
            int equipId = skill.getEquipId();
            if (parent != equipId) {
                parent = equipId;
                id = parent * 10;
                array.get(parent);
                equip = array.get(parent);
            }
            SingleSkillEquip skillEquip = new SingleSkillEquip(equip, skill);
            skillEquip.setId(id++);
            singleSkillEquips.add(skillEquip);
        }
        skillDao.addSingleSkillEquip(singleSkillEquips);
    }

    private void initIndex(List<IndexBean> mList, IndexDao dao) {
        dao.insert(mList);
        ArrayList<SearchBean> list = new ArrayList<>();
        for (IndexBean bean : mList) {
            if (bean.getType() == 6 && bean.getParent() > 393472) {
                list.add(new SearchBean(bean.getName()));
            }
        }
        SearchBean[] beans = new SearchBean[list.size()];
        list.toArray(beans);
        dao.insert(beans);
    }

    @NonNull
    private <T> List<T> getListInJson(AssetManager assets, String fileName, Class<T> clazz) throws IOException {
        InputStream open = assets.open(fileName);
        return JsonUtils.getList(new InputStreamReader(open), clazz);
    }
}
