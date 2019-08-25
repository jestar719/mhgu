package cn.jestar.mhgu.index;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;
import java.util.Set;

import cn.jestar.db.bean.IndexBean;
import cn.jestar.mhgu.MainViewModel;
import cn.jestar.mhgu.R;

import static android.app.Activity.RESULT_OK;

/**
 * 索引导航界面
 * Created by 花京院 on 2019/3/14.
 */

public class IndexFragment extends Fragment implements View.OnClickListener {
    private MainViewModel mModel;
    private TagFlowLayout mFlType;
    private TagFlowLayout mFlTitle;
    private TagFlowLayout mFlSelect;
    private FlowAdapter mTypeAdapter;
    private FlowAdapter mSelectAdapter;
    private TagAdapter<String> mAdapter;
    private String[] mTypes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_frg_index_search, container, false);
        mFlTitle = view.findViewById(R.id.tfl_title);
        mFlType = view.findViewById(R.id.tfl_type);
        mFlSelect = view.findViewById(R.id.tfl_select);
        initTitle();
        initFl();
        view.findViewById(R.id.bt_search).setOnClickListener(this);
        view.setOnClickListener(this);
        return view;
    }

    /**
     * 初始化流式按键
     * 设置联动监听
     */
    private void initFl() {
        mFlType.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                int index = getIndex(selectPosSet);
                if (index >= 0)
                    mModel.onTypeParentSelect(mTypeAdapter.getItem(index).getId());
            }
        });
        FragmentActivity owner = getActivity();
        mModel.observerMenuSelect(owner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                for (int i = 0; i < mTypes.length; i++) {
                    if (mTypes[i].equals(s)) {
                        mAdapter.setSelectedList(i);
                        mModel.onTypeSelect(i + 1);
                    }
                }

            }
        });
        mModel.observerType(owner, new Observer<List<IndexBean>>() {
            @Override
            public void onChanged(@Nullable List<IndexBean> indexBeans) {
                mTypeAdapter = new FlowAdapter(indexBeans);
                mFlType.setAdapter(mTypeAdapter);
                mSelectAdapter = new FlowAdapter(null);
                mFlSelect.setAdapter(mSelectAdapter);
            }
        });
        mModel.observerParent(owner, new Observer<List<IndexBean>>() {
            @Override
            public void onChanged(@Nullable List<IndexBean> indexBeans) {
                mSelectAdapter = new FlowAdapter(indexBeans);
                mFlSelect.setAdapter(mSelectAdapter);
            }
        });
    }

    private int getIndex(Set<Integer> set) {
        if (set.isEmpty())
            return RESULT_OK;
        return (int) set.toArray()[0];
    }

    private void initTitle() {
        mTypes = getResources().getStringArray(R.array.types);
        mAdapter = new TagAdapter<String>(mTypes) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_button, null);
                ((TextView) view).setText(s);
                return view;
            }
        };
        mFlTitle.setAdapter(mAdapter);
        mFlTitle.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                int index = getIndex(selectPosSet);
                if (index >= 0)
                    mModel.onTypeSelect(index + 1);
            }
        });
    }

    /**
     * 点击事件
     * 搜索被点击时获取指定HTML的路径
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            int index = mModel.getIndex(mFlSelect.getSelectedList());
            if (index >= 0) {
                IndexBean item = mSelectAdapter.getItem(index);
                String url = item.getUrl();
                mModel.onTagSelect(url);
            }
        }
    }
}
