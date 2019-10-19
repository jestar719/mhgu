package cn.jestar.mhgu.equip;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.jestar.db.bean.BaseEquip;
import cn.jestar.db.bean.SingleSkillEquip;
import cn.jestar.mhgu.BaseAdapter;
import cn.jestar.mhgu.R;

import static cn.jestar.db.bean.BaseEquip.SEX.FEMALE;
import static cn.jestar.db.bean.BaseEquip.SEX.MAN;
import static cn.jestar.db.bean.BaseEquip.TYPE.ALL;
import static cn.jestar.db.bean.BaseEquip.TYPE.ARCHER;
import static cn.jestar.db.bean.BaseEquip.TYPE.FIGHT;
import static cn.jestar.mhgu.equip.BaseEvent.Type.EQUIP;
import static cn.jestar.mhgu.equip.JewelryManager.NULL;

/**
 * 装备选择界面
 * Created by 花京院 on 2019/10/5.
 */

public class EquipFragment extends BaseEquipFragment {
    public int mSexs;
    private int mParts;
    private int mEquipType;
    private String mInput;
    private EquipAdapter mAdapter;
    private View[] mQueryTypeViews;
    private TextView mTvSkillName;
    private TextView mTvSkillValue;
    private MutableLiveData<String> mEquipUrlData;
    private ViewGroup mLlParts;
    private TagAdapter<String> mTagAdapter;


    public EquipFragment() {
        mType = EQUIP;
        mEquipUrlData = new MutableLiveData<>();
        mQueryType = QueryEvent.QUERY_TYPE.QUERY_BY_NAME;
    }

    @Override
    public void onQuery(String input) {
        if (TextUtils.isEmpty(input)) {
            input = null;
        }
        mInput = input;
        queryEquip();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frg_equip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup group = (ViewGroup) view;
        initButtons(group);
        initParts(group);
        initTags(group);
        initRecycle(group);
    }

    private void setSkillVisible(boolean queryBySkill) {
        mTvSkillName.setVisibility(queryBySkill ? View.VISIBLE : View.GONE);
        mTvSkillValue.setVisibility(queryBySkill ? View.VISIBLE : View.GONE);
    }


    private void initRecycle(ViewGroup group) {
        ViewGroup viewGroup = group.findViewById(R.id.ll_equip_title);
        int count = viewGroup.getChildCount();
        String[] array = getResources().getStringArray(R.array.equip_title);
        for (int i = 0; i < count; i++) {
            TextView view = (TextView) viewGroup.getChildAt(i);
            view.setText(array[i]);
            if (i == 8) {
                mTvSkillName = view;
            } else if (i == 9) {
                mTvSkillValue = view;
            }
        }
        setSkillVisible(false);
        RecyclerView view = group.findViewById(R.id.recycler);
        setRecycler(view);
        mAdapter = new EquipAdapter(this);
        mAdapter.setFilter(mParts);
        view.setAdapter(mAdapter);
        mModel.observerEquips(this, new Observer<List<BaseEquip>>() {
            @Override
            public void onChanged(@Nullable List<BaseEquip> equips) {
                boolean queryBySkill = mQueryType == QueryEvent.QUERY_TYPE.QUERY_BY_SKILL;
                setSkillVisible(queryBySkill);
                mAdapter.setSingleEquip(queryBySkill);
                mAdapter.setList(equips);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseEquip item = mAdapter.getItem(position);
        MenuSelectEvent event = getEvent();
        BaseEquip equip = new BaseEquip();
        equip.copy(item);
        if (mQueryType == QueryEvent.QUERY_TYPE.QUERY_BY_SKILL) {
            equip.setId(item.getId() / 10);
        }
        event.setEquip(equip);
        mModel.postMenuSelectEvent(event);
    }

    private void initTags(ViewGroup group) {
        String[] array = group.getResources().getStringArray(R.array.equip_sort);
        TagFlowLayout tfl = group.findViewById(R.id.tfl);
        mTagAdapter = new TagAdapter<String>(array) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_button, null);
                ((TextView) view).setText(s);
                return view;
            }
        };
        tfl.setMaxSelectCount(1);
        tfl.setAdapter(mTagAdapter);
        tfl.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                for (Integer i : selectPosSet) {
                    mAdapter.sort(i);
                }
            }
        });
    }

    private void initParts(ViewGroup group) {
        mLlParts = group.findViewById(R.id.ll_parts);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag();
                int mask = 1 << tag;
                boolean select = isSelect(mParts, mask);
                mParts = select ? mParts | mask : mParts - mask;
                v.setSelected(select);
                mAdapter.setFilter(mParts);
            }
        };
        int childCount = mLlParts.getChildCount();
        String[] array = group.getResources().getStringArray(R.array.equip_part);
        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) mLlParts.getChildAt(i);
            child.setTag(i);
            child.setText(array[i]);
            child.setSelected(true);
            child.setOnClickListener(listener);
        }
        mParts = (1 << childCount) - 1;
    }

    private void initButtons(ViewGroup group) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSexs = onSelect(v, mSexs);
                queryEquip();
            }
        };
        initView(group, listener, R.id.tv_man, MAN);
        initView(group, listener, R.id.tv_female, FEMALE);
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEquipType = onSelect(v, mEquipType);
                queryEquip();
            }
        };
        mSexs = MAN + FEMALE;
        initView(group, listener, R.id.tv_save, FIGHT);
        initView(group, listener, R.id.tv_archer, ARCHER);
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag();
                boolean isSelect = mQueryType != tag;
                if (isSelect) {
                    mQueryType = tag;
                    for (int i = 0; i < mQueryTypeViews.length; i++) {
                        mQueryTypeViews[i].setSelected(i == tag);
                    }
                    mModel.setEquipQueryType(mQueryType == QueryEvent.QUERY_TYPE.QUERY_BY_SKILL);
                    queryEquip();
                }
            }
        };
        mEquipType = FIGHT + ARCHER;
        int[] ids = new int[]{R.id.tv_query_name, R.id.tv_query_skill};
        int num = ids.length;
        mQueryTypeViews = new View[num];
        for (int i = 0; i < num; i++) {
            mQueryTypeViews[i] = initView(group, listener, ids[i], i);
        }
        mQueryTypeViews[1].setSelected(false);
    }


    /**
     * 装备性别,类型的点选逻辑.
     *
     * @param v     被点击的按键
     * @param value 点击状态
     */
    private int onSelect(View v, int value) {
        int tag = (int) v.getTag();
        boolean selected = !v.isSelected();
        if (selected || value != tag) {
            v.setSelected(selected);
            queryEquip();
            return selected ? value | tag : value - tag;
        }
        return value;
    }

    private void queryEquip() {
        int sex = getValue(mSexs, MAN, FEMALE);
        int equipType = getValue(mEquipType, FIGHT, ARCHER);
        QueryEvent query = getQuery();
        query.setType(equipType);
        query.setSex(sex);
        query.setInput(mInput);
        query.setEquipQueryType(mQueryType);
        mModel.onQuery(query);
    }

    private void toEquipDetail(int position) {
        BaseEquip item = mAdapter.getItem(position);
        mEquipUrlData.postValue(item.getUrl());
    }

    private int getValue(int value, int mask1, int mask2) {
        if (value == mask1) {
            value = mask2;
        } else if (value == mask2) {
            value = mask1;
        } else {
            value = -1;
        }
        return value;
    }

    private boolean isSelect(int value, int mask) {
        return (value & mask) == 0;
    }

    private View initView(ViewGroup group, View.OnClickListener listener, int id, int tag) {
        View tv = group.findViewById(id);
        tv.setTag(tag);
        tv.setOnClickListener(listener);
        tv.setSelected(true);
        return tv;
    }

    public LiveData<String> getUrlData() {
        return mEquipUrlData;
    }

    public void setPart(int part) {
        mParts = (1 << part);
        int childCount = mLlParts.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mLlParts.getChildAt(i);
            view.setSelected((mParts & (1 << i)) > 0);
        }
        mAdapter.setFilter(mParts);
    }


    @interface SortType {
        int NULL = -1;
        int DEFENCE = 0;
        int RARE = 1;
        int SLOT = 2;
        int SKILL = 3;

    }

    class EquipComparator implements Comparator<BaseEquip> {
        private final Comparator<BaseEquip> mSkillComparator;
        int mType = SortType.NULL;
        private SparseArray<Comparator<BaseEquip>> mArray = new SparseArray<>();
        private LinkedList<Comparator<BaseEquip>> mList = new LinkedList<>();
        private boolean isSkillMode;

        public EquipComparator() {
            Comparator<BaseEquip> comparator = new Comparator<BaseEquip>() {
                @Override
                public int compare(BaseEquip o1, BaseEquip o2) {
                    return o2.getMaxDefence() - o1.getMaxDefence();
                }
            };
            mArray.put(SortType.DEFENCE, comparator);
            mList.add(comparator);
            comparator = new Comparator<BaseEquip>() {
                @Override
                public int compare(BaseEquip o1, BaseEquip o2) {
                    return o2.getRare() - o1.getRare();
                }
            };
            mArray.put(SortType.RARE, comparator);
            mList.add(comparator);
            comparator = new Comparator<BaseEquip>() {
                @Override
                public int compare(BaseEquip o1, BaseEquip o2) {
                    return o2.getSlotNum() - o1.getSlotNum();
                }
            };
            mArray.put(SortType.SLOT, comparator);
            mList.add(comparator);
            comparator = new Comparator<BaseEquip>() {
                @Override
                public int compare(BaseEquip o1, BaseEquip o2) {
                    SingleSkillEquip e1 = (SingleSkillEquip) o1;
                    SingleSkillEquip e2 = (SingleSkillEquip) o2;
                    return e2.getSkillValue() - e1.getSkillValue();
                }
            };
            mArray.put(SortType.SKILL, comparator);
            mSkillComparator = comparator;
            mList.add(comparator);
        }


        @Override
        public int compare(BaseEquip o1, BaseEquip o2) {
            int result = 0;
            for (Comparator<BaseEquip> comparator : mList) {
                if (comparator != mSkillComparator || isSkillMode) {
                    result = comparator.compare(o1, o2);
                    if (result != 0)
                        break;
                }
            }
            return result;
        }

        public void setSkillMode(boolean mode) {
            isSkillMode = mode;
        }


        public void setSortStart(@SortType int type) {
            if (mType != type) {
                Comparator<BaseEquip> comparator = mArray.get(type);
                mList.remove(comparator);
                mList.addFirst(comparator);
                mType = type;
            }
        }

        public int resetSortType() {
            if (isSkillMode) {
                setSortStart(SortType.SKILL);
            } else {
                if (mType == SortType.NULL || mType == SortType.SKILL) {
                    setSortStart(SortType.RARE);
                }
            }

            return mType;
        }
    }


    class EquipAdapter extends BaseAdapter<BaseEquip, EquipHolder> {
        private int mParts;
        private boolean isSingleEquip;
        private String[] mPartNames;
        private List<BaseEquip> mEquipList;
        private EquipComparator mComparator;


        public EquipAdapter(AdapterView.OnItemClickListener listener) {
            super(listener);
            mPartNames = getResources().getStringArray(R.array.equip_part);
            mComparator = new EquipComparator();
        }


        @NonNull
        @Override
        public EquipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_equip, parent, false);
            return new EquipHolder(view, mPartNames, mListener);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipHolder holder, int position) {
            BaseEquip item = getItem(position);
            holder.setType(isSingleEquip);
            holder.setEquip(item);
            if (isSingleEquip)
                holder.setSingleEquip((SingleSkillEquip) item);
        }

        @Override
        public void setList(List<BaseEquip> list) {
            mList = null;
            mEquipList = list;
            getFilteredList();
            int type = mComparator.resetSortType();
            mTagAdapter.setSelectedList(type);
            sort();
            notifyDataSetChanged();
        }

        /**
         * 排序
         *
         * @param type 排序类型
         */
        public void sort(int type) {
            mComparator.setSortStart(type);
            sort();
            notifyDataSetChanged();
        }

        private void sort() {
            if (mList != null)
                Collections.sort(mList, mComparator);
        }

        public void setSingleEquip(boolean singleEquip) {
            isSingleEquip = singleEquip;
            mComparator.setSkillMode(singleEquip);
        }

        public void setFilter(int parts) {
            mParts = parts;
            getFilteredList();
            sort();
            notifyDataSetChanged();
        }


        private void getFilteredList() {
            if (mEquipList != null) {
                mList = new ArrayList<>();
                for (BaseEquip equip : mEquipList) {
                    boolean visible = (mParts & 1 << equip.getPart()) > 0;
                    if (visible) {
                        mList.add(equip);
                    }
                }
            }
        }
    }


    class EquipHolder extends BaseAdapter.BaseHolder implements View.OnLongClickListener {

        private TextView mName;
        private TextView mPart;
        private TextView mType;
        private TextView mSex;
        private TextView mRare;
        private TextView mDefence;
        private TextView mMaxDefence;
        private TextView mSlotNum;
        private TextView mSkillName;
        private TextView mSkillValue;
        private String[] mPartNames;

        public EquipHolder(View itemView, String[] partNames, AdapterView.OnItemClickListener listener) {
            super(itemView, listener);
            mPartNames = partNames;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        protected void init() {
            ViewGroup group = (ViewGroup) itemView;
            mName = group.findViewById(R.id.tv_name);
            mPart = group.findViewById(R.id.tv_part);
            mType = group.findViewById(R.id.tv_type);
            mSex = group.findViewById(R.id.tv_sex);
            mRare = group.findViewById(R.id.tv_rare);
            mDefence = group.findViewById(R.id.tv_defence);
            mMaxDefence = group.findViewById(R.id.tv_max_defence);
            mSlotNum = group.findViewById(R.id.tv_slot_num);
            mSkillName = group.findViewById(R.id.tv_skill_name);
            mSkillValue = group.findViewById(R.id.tv_skill_value);
        }

        public void setEquip(BaseEquip item) {
            mName.setText(item.getName());
            mPart.setText(mPartNames[item.getPart()]);
            int type = item.getType();
            if (type == ALL) {
                mType.setText(R.string.text_all);
            } else {
                boolean isFight = type == FIGHT;
                mType.setText(isFight ? R.string.fighter : R.string.archer);
            }
            int sex = item.getSex();
            if (sex == BaseEquip.SEX.ALL) {
                mSex.setText(R.string.text_all);
            } else {
                boolean isMan = type == MAN;
                mSex.setText(isMan ? R.string.man : R.string.female);
            }
            int rare = item.getRare();
            mRare.setText(rare == 11 ? "X" : String.valueOf(rare));
            mDefence.setText(String.valueOf(item.getDefence()));
            mMaxDefence.setText(String.valueOf(item.getMaxDefence()));
            mSlotNum.setText(String.valueOf(item.getSlotNum()));
        }

        public void setType(boolean isSingleEquip) {
            mSkillName.setVisibility(isSingleEquip ? View.VISIBLE : View.GONE);
            mSkillValue.setVisibility(isSingleEquip ? View.VISIBLE : View.GONE);
        }

        public void setSingleEquip(SingleSkillEquip equip) {
            mSkillName.setText(equip.getSkillName());
            int value = equip.getSkillValue();
            mSkillValue.setText(value > 0 ? NULL + value : String.valueOf(value));
        }

        @Override
        public boolean onLongClick(View v) {
            toEquipDetail(getAdapterPosition());
            return true;
        }
    }
}
