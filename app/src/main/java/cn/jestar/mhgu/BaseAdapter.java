package cn.jestar.mhgu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

/**
 * RecyclerView和ViewHolder的基类
 * Created by 花京院 on 2019/10/7.
 */

public abstract class BaseAdapter<T, VH extends BaseAdapter.BaseHolder> extends RecyclerView.Adapter<VH> {
    protected AdapterView.OnItemClickListener mListener;
    protected List<T> mList;

    public BaseAdapter() {
    }

    public BaseAdapter(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }

    public void setListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
    }

    public void setList(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public T getItem(int position) {
        return mList.get(position);
    }


    public static abstract class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterView.OnItemClickListener mListener;

        public BaseHolder(View itemView, AdapterView.OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            init();
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(null, v, getAdapterPosition(), getItemId());
        }

        protected abstract void init();
    }
}
