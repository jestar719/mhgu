package cn.jestar.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import cn.jestar.db.bean.Equip;
import cn.jestar.db.bean.EquipSkill;
import cn.jestar.db.bean.Jewelry;
import cn.jestar.db.bean.SingleSkillEquip;
import cn.jestar.db.bean.Skill;
import cn.jestar.db.bean.SkillEffect;

/**
 * 技能和装备的Dao
 * Created by 花京院 on 2019/9/28.
 */

@Dao
public interface SkillDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addEquip(List<Equip> equips);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSingleSkillEquip(List<SingleSkillEquip> equips);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addEquipSkills(List<EquipSkill> skills);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSkill(List<Skill> skills);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addJewelry(List<Jewelry> jewelries);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSkillEffect(List<SkillEffect> effects);


    /**
     * 根据id查找装备
     *
     * @param id 装备id
     * @return 装备
     */
    @Query("select * from Equip where id=:id")
    Equip getEquipById(int id);

    /**
     * 根据技能查找单技能装备
     *
     * @param type      装备类型
     * @param sex       性别
     * @param skillName 技能名
     * @return 装备列表
     */
    @Query("select * from SingleSkillEquip where type!=:type and sex!=:sex and skillName=:skillName order by skillValue desc")
    List<SingleSkillEquip> getSingleSkillEquips(int type, int sex, String skillName);

    /**
     * 根据类别姓别查找装备列表
     *
     * @param type 类别
     * @param sex  性别
     * @return 装备列表
     */
    @Query("select * from Equip where type!=:type and sex!=:sex and name like :name")
    List<Equip> getEquips(int type, int sex, String name);

    /**
     * 根据id查找装备技能
     *
     * @param equipId 装备id
     * @return 装备技能列表
     */
    @Query("select * from EquipSkill where equipId=:equipId")
    List<EquipSkill> getEquipSkills(int equipId);

    @Query("select name from Skill")
    List<String> getSkillNames();

    @Query("select name from Skill where name like :name")
    LiveData<List<String>> getSkillNames(String name);


    /**
     * 根据名称查找技能
     *
     * @param name 技能名
     * @return 技能
     */
    @Query("select * from Skill where name =:name")
    List<Skill> getSkill(String name);

    /**
     * 查找相似技能名的技能
     *
     * @param name 技能名
     * @return 技能
     */
    @Query("select * from Skill where name like :name")
    List<Skill> getLikedSkill(String name);

    /**
     * 根据类别查找技能
     *
     * @param type 技能类别
     * @return 技能列表
     */
    @Query("select * from Skill where type=:type order by name")
    List<Skill> getSkillByType(int type);

    /**
     * 根据id查找技能珠
     *
     * @param id 技能id
     * @return 技能珠
     */
    @Query("select * from Jewelry where id=:id")
    Jewelry getJewelryById(int id);

    /**
     * 根据技能名查找技能相似的技能珠
     *
     * @param name 技能名
     * @return 技能珠列表
     */
    @Query("select * from Jewelry where skillName like :name")
    List<Jewelry> getLikedJewelries(String name);

    /**
     * 根据技能名查找技能珠
     *
     * @param name 技能名
     * @return 技能珠列表
     */
    @Query("select * from Jewelry where skillName=:name")
    List<Jewelry> getJewelries(String name);

    /**
     * 根据技能类型查找技能珠
     *
     * @param type 技能类型
     * @return 技能珠列表
     */
    @Query("select * from Jewelry where type=:type order by name")
    List<Jewelry> getJewelriesByType(int type);

    /**
     * 根据技能名查找技能效果
     *
     * @param name 技能名
     * @return 技能效果列表
     */
    @Query("select * from SkillEffect where skillName=:name")
    List<SkillEffect> getSkillEffects(String name);


}
