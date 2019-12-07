package com.example.zhihu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * WebView控件加载显示网页
 */
public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        Intent intent = getIntent();
        webLoad(intent.getStringExtra("lxl"));
    }

    private void webLoad(String url) {
        WebView webView = findViewById(R.id.news_webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        /*  为什么要加一个flag：application与activity的context都继承context
        *   此处的context是application的，而application启动activity需要flag   */
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("lxl", url);
        context.startActivity(intent);
    }
}
