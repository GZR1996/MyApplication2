package com.example.a.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a.myapplication.R;
import com.example.a.myapplication.database.MailDB;
import com.example.a.myapplication.model.User;
import com.example.a.myapplication.util.POP3Utility;

import javax.mail.MessagingException;

/**
 * Created by Administrator on 2016/8/7.
 */
public class AddUserActivity extends AppCompatActivity{

    private static final String TAG = "AddUserActivity";
    //控件
    private EditText editUser;
    private EditText editPassword;
    private Button editButton;
    private MailDB db;
    private Toolbar toolbar;
    //信息
    private String user;
    private String password;
    //异步处理
    private static final int LOGIN_SUCCESSFUL = 0;
    private static final int LOGIN_FAIL = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case LOGIN_SUCCESSFUL:
                    Intent intent = new Intent();
                    intent.putExtra("data_user",user);
                    intent.putExtra("data_password",password);
                    setResult(RESULT_OK,intent);
                    Toast.makeText(AddUserActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(AddUserActivity.this,"登录失败,请检查帐号和密码",Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

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
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

        editUser = (EditText)findViewById(R.id.edit_user);
        editPassword = (EditText)findViewById(R.id.edit_password);
        editButton = (Button) findViewById(R.id.edit_button);
        db = MailDB.getInstance(AddUserActivity.this);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = editUser.getText().toString();
                password = editPassword.getText().toString();
                db.saveUser(user,password);
                final POP3Utility utility = new POP3Utility(AddUserActivity.this,user,password);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            utility.login();
                            handler.sendEmptyMessage(LOGIN_SUCCESSFUL);
                        } catch (MessagingException e) {
                            handler.sendEmptyMessage(LOGIN_FAIL);
                        }
                    }
                }).start();
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}
