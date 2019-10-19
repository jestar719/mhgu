package cn.jestar.convert.bean;

/**
 * Created by 花京院 on 2019/9/25.
 */

public class BaseEquip {
    private int id;
    private String name;
    private int defence;
    private int maxDefence;
    private int rare;
    @PART
    private int part;
    @TYPE
    private int type;
    @SEX
    private int sex;
    private int slotNum;
    private int fire;
    private int water;
    private int ice;
    private int flash;
    private int dragon;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void copy(BaseEquip equip) {
        id = equip.id;
        name = equip.name;
        defence = equip.defence;
        maxDefence = equip.maxDefence;
        sex = equip.sex;
        rare = equip.rare;
        part = equip.part;
        type = equip.type;
        slotNum = equip.slotNum;
        fire = equip.fire;
        water = equip.water;
        ice = equip.ice;
        flash = equip.flash;
        dragon = equip.dragon;
        url = equip.url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getMaxDefence() {
        return maxDefence;
    }

    public void setMaxDefence(int maxDefence) {
        this.maxDefence = maxDefence;
    }

    public int getRare() {
        return rare;
    }

    public void setRare(int rare) {
        this.rare = rare;
    }

    public int getPart() {
        return part;
    }

    public void setPart(@PART int part) {
        this.part = part;
    }

    public @TYPE
    int getType() {
        return type;
    }


    public void setType(@TYPE int type) {
        this.type = type;
    }

    public @SEX
    int getSex() {
        return sex;
    }

    public void setSex(@SEX int sex) {
        this.sex = sex;
    }

    public int getSlotNum() {
        return slotNum;
    }

    public void setSlotNum(int slotNum) {
        this.slotNum = slotNum;
    }

    public int getFire() {
        return fire;
    }

    public void setFire(int fire) {
        this.fire = fire;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getIce() {
        return ice;
    }

    public void setIce(int ice) {
        this.ice = ice;
    }

    public int getFlash() {
        return flash;
    }

    public void setFlash(int flash) {
        this.flash = flash;
    }

    public int getDragon() {
        return dragon;
    }

    public void setDragon(int dragon) {
        this.dragon = dragon;
    }

    public @interface SEX {
        int ALL = 0;
        int MAN = 1;
        int FELMAN = 2;
    }

    public @interface TYPE {
        int ALL = 0;
        int FIGHT = 1;
        int ARCHER = 2;
    }

    public @interface PART {
        int HEAD = 0;
        int BODY = 1;
        int ARMR = 2;
        int LEG = 3;
        int FOOT = 4;
        int JEWELRY = 5;
        int WEAPON = 6;
    }

    public static class EquipSkill {
        private int id;
        private String name;
        private int value;
        private int equipId;

        public EquipSkill() {
        }

        public EquipSkill(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getEquipId() {
            return equipId;
        }

        public void setEquipId(int equipId) {
            this.equipId = equipId;
        }
    }

}
