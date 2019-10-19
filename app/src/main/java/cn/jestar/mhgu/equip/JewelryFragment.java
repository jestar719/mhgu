package cn.jestar.mhgu.equip;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;
import java.util.Set;

import cn.jestar.db.bean.Jewelry;
import cn.jestar.mhgu.BaseAdapter;
import cn.jestar.mhgu.R;

import static cn.jestar.mhgu.equip.BaseEvent.Type.JEWELRY;

/**
 * 技能珠的查询界面
 * Created by 花京院 on 2019/10/5.
 */

public class JewelryFragment extends BaseEquipFragment {
    private JewelryAdapter mJewelryAdapter;

    public JewelryFragment() {
        mType = JEWELRY;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel.observerJewelry(this, new Observer<List<Jewelry>>() {
            @Override
            public void onChanged(@Nullable List<Jewelry> jewelries) {
                mJewelryAdapter.setList(jewelries);
            }
        });
    }

    @Override
    public void onQuery(String input) {
        QueryEvent query = getQuery();
        query.setInput(input);
        mModel.onQuery(query);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_frg_jewelry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intFlt(view);
        initRecycler(view);
    }

    private void initRecycler(View view) {
        RecyclerView recycler = view.findViewById(R.id.recycler);
        setRecycler(recycler);
        mJewelryAdapter = new JewelryAdapter(this);
        recycler.setAdapter(mJewelryAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Jewelry item = mJewelryAdapter.getItem(position);
        MenuSelectEvent event = getEvent();
        event.setJewelry(item);
        mModel.postMenuSelectEvent(event);
    }

    private void intFlt(View view) {
        TagFlowLayout tfl = view.findViewById(R.id.tfl_title);
        String[] array = view.getContext().getResources().getStringArray(R.array.skill_type);
        TagAdapter<String> adapter = new TagAdapter<String>(array) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_button, null);
                ((TextView) view).setText(s);
                return view;
            }
        };
        tfl.setAdapter(adapter);
        tfl.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                for (Integer i : selectPosSet) {
                    QueryEvent query = getQuery();
                    query.setType(i);
                    mModel.onQuery(query);
                }
            }
        });
    }


    class JewelryAdapter extends BaseAdapter<Jewelry, JewelryHolder> {

        public JewelryAdapter(AdapterView.OnItemClickListener listener) {
            super(listener);
        }

        @NonNull
        @Override
        public JewelryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new JewelryHolder(view, mListener);
        }

        @Override
        public void onBindViewHolder(@NonNull JewelryHolder holder, int position) {
            holder.setText(getItem(position).getName());
        }
    }

    class JewelryHolder extends BaseAdapter.BaseHolder {
        private TextView mItemView;

        public JewelryHolder(View itemView, AdapterView.OnItemClickListener listener) {
            super(itemView, listener);
        }

        @Override
        protected void init() {
            mItemView = (TextView) itemView;
            itemView.setOnClickListener(this);
        }

        public void setText(String text) {
            mItemView.setText(text);
        }
    }
}
