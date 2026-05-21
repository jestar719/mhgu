package cn.jestar.mhgu.equip;

import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.AdapterView;

/**
 * 配装页面fragment基类
 * Created by 花京院 on 2019/10/5.
 */

public abstract class BaseEquipFragment extends Fragment implements AdapterView.OnItemClickListener {
    protected int mType;
    protected EquipModel mModel;
    protected int mQueryType = QueryEvent.QUERY_TYPE.QUERY_BY_SKILL;

    public int getType() {
        return mType;
    }

    public int getQueryType() {
        return mQueryType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(EquipModel.class);
    }


    protected void setRecycler(RecyclerView view) {
        Context context = view.getContext();
        view.setLayoutManager(new LinearLayoutManager(context));
        view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    protected MenuSelectEvent getEvent() {
        return new MenuSelectEvent(mType);
    }

    protected QueryEvent getQuery() {
        return new QueryEvent(mType);
    }

    public abstract void onQuery(String input);
}
