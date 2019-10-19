package cn.jestar.mhgu.equip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jestar.db.bean.SkillEffect;
import cn.jestar.mhgu.R;

/**
 * 已经装备技能的Adapter
 * Created by 花京院 on 2019/10/5.
 */

public class EquipSkillAdapter extends RecyclerView.Adapter<EquipSkillAdapter.SkillHolder> {
    private List<SumSkill> list;
    private Comparator<SumSkill> mComparator;

    public EquipSkillAdapter() {
        mComparator = new Comparator<SumSkill>() {
            @Override
            public int compare(SumSkill o1, SumSkill o2) {
                return o2.getValue() - o1.getValue();
            }
        };
    }

    public void setList(List<SumSkill> list) {
        this.list = list;
        if (list != null) {
            Collections.sort(list, mComparator);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SkillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_skill_value_item, parent, false);
        return new SkillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillHolder holder, int position) {
        SumSkill skill = list.get(position);
        holder.setText(holder.skillNameIndex, skill.getName());
        int value = skill.getValue();
        holder.setText(holder.skillSum, String.valueOf(value));
        SkillEffect effect = skill.getEffect(value);
        holder.setText(holder.skillEffectIndex, effect == null ? "" : effect.getName());
        int[] parts = skill.getSkillParts();
        holder.setText(parts);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        int skillNameIndex = 0;
        int skillSum = 8;
        int skillEffectIndex = 9;
        TextView[] mViews;

        public SkillHolder(View itemView) {
            super(itemView);
            ViewGroup group = (ViewGroup) itemView;
            int count = group.getChildCount();
            mViews = new TextView[count];
            for (int i = 0; i < count; i++) {
                TextView tv = (TextView) group.getChildAt(i);
                mViews[i] = tv;
            }
        }

        public void setText(int index, String text) {
            mViews[index].setText(text);
        }

        public void setText(int[] parts) {
            for (int i = 0; i < parts.length; i++) {
                mViews[i + 1].setText(String.valueOf(parts[i]));
            }
        }
    }
}
