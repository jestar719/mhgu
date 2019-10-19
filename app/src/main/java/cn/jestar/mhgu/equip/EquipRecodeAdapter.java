package cn.jestar.mhgu.equip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * 保存的配装名称Adapter
 * Created by 花京院 on 2019/10/6.
 */

public class EquipRecodeAdapter extends RecyclerView.Adapter<EquipRecodeAdapter.EquipRecodeHolder> {
    private List<EquipSetRecode> list;
    private AdapterView.OnItemClickListener mListener;

    public EquipRecodeAdapter(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }

    public List<EquipSetRecode> getList() {
        return list;
    }

    public void setList(List<EquipSetRecode> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EquipRecodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        return new EquipRecodeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipRecodeHolder holder, int position) {
        EquipSetRecode recode = list.get(position);
        String description = recode.getDescription();
        holder.mView.setText(description);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public EquipSetRecode getItem(int position) {
        return list.get(position);
    }

    class EquipRecodeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mView;

        public EquipRecodeHolder(View itemView) {
            super(itemView);
            mView = (TextView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(null, v, getAdapterPosition(), getItemId());
        }
    }
}
