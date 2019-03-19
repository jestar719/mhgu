package cn.jestar.mhgu.version;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.jestar.mhgu.FileConstans;
import cn.jestar.mhgu.MainViewModel;
import cn.jestar.mhgu.R;

/**
 * Created by 花京院 on 2019/3/13.
 */

public class UpdateFragment extends Fragment implements Observer<VersionBean>, View.OnClickListener {
    private MainViewModel mModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        mModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        mModel.getVersion().observe(activity, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.layout_frg_update, container, false);
        inflate.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onChanged(@Nullable VersionBean versionBean) {
        if (versionBean != null) {
            String string = getString(R.string.dialog_temp);
            getTextView(R.id.tv_version_title).setText(String.format(string, versionBean.getTitle()));
            getTextView(R.id.tv_update_msg).setText(versionBean.getMsg());
            getTextView(R.id.tv_cancel).setOnClickListener(this);
            setUrlSpan(getString(R.string.fir_update), FileConstans.UPDATE_FIR, R.id.tv_fir_update);
            setUrlSpan(getString(R.string.baidu_update), FileConstans.UPDATE_BAIDU_PAN, R.id.tv_baidu_update);

            TextView view = getTextView(R.id.tv_baidu_psd);
            view.setOnClickListener(this);
            SpannableString span = new SpannableString(getString(R.string.baidu_psd));
            int length = span.length();
            span.setSpan(new URLSpan(FileConstans.PSD_BAIDU_PAN), length - 4, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(span);
        }
    }

    private TextView getTextView(@IdRes int id) {
        return getView().findViewById(id);
    }

    private TextView setUrlSpan(String text, String url, int id) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new URLSpan(url), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView view = getTextView(id);
        view.setText(ss);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            getActivity().onBackPressed();
        } else if (id == R.id.tv_baidu_psd) {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", FileConstans.PSD_BAIDU_PAN);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(getContext(), R.string.psd_copied, Toast.LENGTH_SHORT).show();
        }
    }
}
