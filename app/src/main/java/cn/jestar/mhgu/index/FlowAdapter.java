package cn.jestar.mhgu.index;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

import cn.jestar.db.bean.IndexBean;
import cn.jestar.mhgu.R;


/**
 * Created by 花京院 on 2019/1/29.
 */

class FlowAdapter extends TagAdapter<IndexBean> {

    public FlowAdapter(List<IndexBean> list) {
        super(list);
    }

    @Override
    public View getView(FlowLayout parent, int position, IndexBean indexBean) {
        TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_button, null);
        tv.setText(indexBean.getName());
        return tv;
    }
}
