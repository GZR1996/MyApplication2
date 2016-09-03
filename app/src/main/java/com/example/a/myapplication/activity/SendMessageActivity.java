package com.example.a.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a.myapplication.R;
import com.example.a.myapplication.adapter.SelectAdapter;
import com.example.a.myapplication.util.SMTPUtility;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

/**
 * Created by Administrator on 2016/8/13.
 */
public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SendMessageActivity";
    //控件
    private Toolbar toolbar;
    private TextView send;
    private EditText editReceiver;
    private EditText editTheme;
    private ImageView editAttach;
    private EditText editContent;
    //参数
    private String user;
    private String password;
    private String host = "smtp.163.com";
    private boolean isContainAttach = false;
    private String receiver;
    private String content;
    private String subject;
    private List<String> attachPath;
    //异步处理
    private static final int SEND_SUCCESSFUL = 0;
    private static final int SEND_FAIL = 1;
    private static final int SEND_CANCEL = 2;
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case SEND_SUCCESSFUL:
                    Toast.makeText(SendMessageActivity.this,"邮件发送成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case SEND_FAIL:
                    break;
                case SEND_CANCEL:
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("发邮件");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //显示左侧的返回箭头，并且返回箭头和title一直设置返回箭头才能显示
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver = editReceiver.getText().toString();
                subject = editTheme.getText().toString();
                content = editContent.getText().toString();
                if (!receiver.equals("") || !subject.equals("") || !content.equals("")) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SendMessageActivity.this);
                    dialogBuilder.setPositiveButton("保存草稿", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialogBuilder.setNegativeButton("删除草稿", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.setTitle("是否保存草稿");
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    finish();
                }
            }
        });
        send = (TextView)findViewById(R.id.send);
        send.setOnClickListener(this);
        editReceiver = (EditText)findViewById(R.id.edit_receiver);
        editTheme = (EditText)findViewById(R.id.edit_message_theme);
        editAttach = (ImageView)findViewById(R.id.edit_attach);
        editAttach.setOnClickListener(this);
        editContent = (EditText)findViewById(R.id.edit_content);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        attachPath = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.send:
                receiver = editReceiver.getText().toString();
                subject = editTheme.getText().toString();
                content = editContent.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SMTPUtility utility = new SMTPUtility(SendMessageActivity.this,host,user,receiver);
                        if (isContainAttach == false) {
                            try {
                                utility.sendMessage(subject,content);
                                handler.sendEmptyMessage(SEND_SUCCESSFUL);
                            } catch (MessagingException e) {
                                handler.sendEmptyMessage(SEND_FAIL);
                            }
                        } else {
                            try{
                                utility.sendMessageWithAttach(subject,content,attachPath);
                                handler.sendEmptyMessage(SEND_SUCCESSFUL);
                            } catch (MessagingException e) {
                                handler.sendEmptyMessage(SEND_FAIL);
                            }
                        }
                    }
                }).start();
                break;
            case R.id.edit_attach:
                Intent selectIntent = new Intent(SendMessageActivity.this, SelectFileActivity.class);
                String path = null;
                if (attachPath != null && attachPath.size() > 0) {
                    path = attachPath.get(attachPath.size());
                }
                selectIntent.putExtra("path",path);
                startActivityForResult(selectIntent,1);
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    attachPath.add(data.getStringExtra("file_path"));
                    isContainAttach = true;
                }
                break;
        }
    }
}
