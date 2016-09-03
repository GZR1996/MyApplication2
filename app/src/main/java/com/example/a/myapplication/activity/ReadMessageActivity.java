package com.example.a.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.a.myapplication.R;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ReadMessageActivity extends AppCompatActivity {

    //控件
    private Toolbar toolbar;
    private TextView readSubject;
    private TextView readFormTo;
    private WebView readContent;
    //邮件内容
    private String subject;
    private String from;
    private String to;
    private String sentDate;
    private String content;
    private boolean isContainAttach;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //显示左侧的返回箭头，并且返回箭头和title一直设置返回箭头才能显示
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //显示标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(" ");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        readSubject = (TextView)findViewById(R.id.read_subject);
        readFormTo = (TextView)findViewById(R.id.read_from_to);

        readContent = (WebView)findViewById(R.id.read_content);
        readContent.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        WebSettings settings = readContent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        content = intent.getStringExtra("content");
        isContainAttach = intent.getBooleanExtra("isContainAttach",false);

        readSubject.setText(subject);
        readFormTo.setText(from +"发送到我");
        readContent.loadDataWithBaseURL(null,content,"text/html","utf-8",null);
    }

}
