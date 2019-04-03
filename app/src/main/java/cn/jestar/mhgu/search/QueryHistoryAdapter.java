package cn.jestar.mhgu.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;

import cn.jestar.db.bean.SearchBean;

/**
 * Created by 花京院 on 2019/4/3.
 */

public class QueryHistoryAdapter extends ArrayAdapter<SearchBean> {

    public QueryHistoryAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public QueryHistoryAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                int count = getCount();
                filterResults.count = count;
                ArrayList<Object> list = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    list.add(getItem(i));
                }
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }
}
