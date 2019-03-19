package cn.jestar.mhgu.web;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by 花京院 on 2018/12/14.
 */

public class WebViewManager implements WebView.FindListener {

    private final WebView mView;
    private final MutableLiveData<String> mData;

    public WebViewManager(WebView webView) {
        mView = webView;
        mData = new MutableLiveData<>();
        init();
    }

    private void init() {
        mView.clearHistory();
        mView.clearCache(true);
        mView.clearFormData();
        mView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 隐藏滚动条webView.requestFocus();
        mView.requestFocusFromTouch();
        mView.setFindListener(this);
        initSetting();
        mView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载时
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载结束时
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 获得网页的加载进度 newProgress为当前加载百分比
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // 获取网页的title，客户端可以在这里动态修改页面的title
                // 另外，当加载错误时title为“找不到该网页”
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void initSetting() {
        WebSettings mWebSettings = mView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);// 支持JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        mWebSettings.setBuiltInZoomControls(false);// 设置支持缩放
        mWebSettings.setDomStorageEnabled(true);//使用localStorage则必须打开
        mWebSettings.setBlockNetworkImage(true);// 首先阻塞图片，让图片不显示
        mWebSettings.setBlockNetworkImage(false);//  页面加载好以后，在放开图片：
        mWebSettings.setSupportMultipleWindows(true);// 设置同一个界面
        mWebSettings.setBlockNetworkImage(false);
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setNeedInitialFocus(false);// 禁止webview上面控件获取焦点(黄色边框)

    }


    public void toTop() {
        mView.scrollTo(0, 0);
    }

    public void toIndex() {
        mView.loadUrl(Uris.INDEX_PAGE);
    }

    public boolean back() {
        boolean goBack = mView.canGoBack();
        if (goBack) {
            mView.goBack();
        }
        return goBack;
    }

    public void searchNext(boolean next) {
        mView.findNext(next);
    }

    public void search(String text) {
        mView.clearMatches();
        mView.findAllAsync(text);
    }

    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        boolean isFind = numberOfMatches != 0;
        String format = null;
        if (isFind) {
            format = String.format("%s/%s", activeMatchOrdinal + 1, numberOfMatches);
        }
        mData.setValue(format);
    }

    public LiveData<String> getLiveDate() {
        return mData;
    }

    public void navigate(String url) {
        mView.loadUrl(Uris.BASE_PATH + url);
    }
}
