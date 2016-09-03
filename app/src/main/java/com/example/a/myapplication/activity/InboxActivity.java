package com.example.a.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a.myapplication.R;
import com.example.a.myapplication.adapter.DrawerOptionAdapter;
import com.example.a.myapplication.adapter.MailAdapter;
import com.example.a.myapplication.component.RefreshListView;
import com.example.a.myapplication.database.MailDB;
import com.example.a.myapplication.model.MailInformation;
import com.example.a.myapplication.util.POP3Utility;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

public class InboxActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,RefreshListView.OnRefreshListener{

    private static final String TAG = "InboxActivity";
    private static final int addUserActivity = 10;
    private static MailDB mailDB;
    //当前用户
    private String user = "m15119699419@163.com";
    private String password;
    //Toolbar
    private Toolbar toolbar;
    private FloatingActionButton fab;
    //测滑菜单
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private static String[] drawerContent = {"收件箱","红旗邮件","草稿箱","已发送", "垃圾邮件","已删除"};
    private ListView drawerListView;
    private List<String> drawerList;
    private DrawerOptionAdapter drawerMenuAdapter;
    private LinearLayout drawerAddLayout;
    private LinearLayout drawerInboxLayout;
    private LinearLayout drawerUnreadLayout;
    private LinearLayout drawerFlagLayout;
    private ListView drawerUserList;
    private DrawerOptionAdapter drawerUserAdapter;
    //显示邮件
    private RefreshListView listView;
    private List<MailInformation> list;
    private MailAdapter adapter;
    //异步处理
    private POP3Utility utility;
    private static final int GET_NEW_MAIL_FINISH = 0;
    private static final int GET_NEW_MAIL_FAIL = 1;
    private Handler handler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_NEW_MAIL_FINISH:
                    adapter.notifyDataSetChanged();
                    Log.d(TAG,"set finish");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        listView = (RefreshListView) findViewById(R.id.list_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("收件箱");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton)findViewById(R.id.floating_button);
        fab.setOnClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(InboxActivity.this,drawerLayout,toolbar,R.string.open, R.string.close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        drawerListView = (ListView) findViewById(R.id.drawer_option_list);
        drawerList = new ArrayList<>();
        for (int i = 0;i < 6;i++) {
            drawerList.add(drawerContent[i]);
        }
        drawerMenuAdapter = new DrawerOptionAdapter(InboxActivity.this,R.layout.drawer_list_item,drawerList);
        drawerListView.setAdapter(drawerMenuAdapter);
        drawerAddLayout = (LinearLayout)findViewById(R.id.drawer_linear_layout);
        drawerAddLayout.setOnClickListener(this);

        mailDB = MailDB.getInstance(InboxActivity.this);
        if (mailDB.isEmpty()) {
            list = new ArrayList<>();
        } else {
            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
            user = pref.getString("user",null);
            password = pref.getString("password",null);
            Log.d(TAG,user + " " + password);
            if (user != null){
                Log.d(TAG,"zheng chang");
                list = mailDB.loadMailInformation(user);
                connectWithServer();
            } else {
                list = new ArrayList<>();
            }
        }
        adapter = new MailAdapter(InboxActivity.this,R.layout.inbox_list_item,list);
        listView.setAdapter(adapter);
        listView.setMailAdapter(adapter );
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    //点击事件监听
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.drawer_linear_layout:
                Intent addIntent = new Intent(InboxActivity.this,AddUserActivity.class);
                startActivityForResult(addIntent,addUserActivity);
                break;
            case R.id.floating_button:
                Intent sendIntent = new Intent(InboxActivity.this,SendMessageActivity.class);
                sendIntent.putExtra("user",user);
                startActivity(sendIntent);
            default:
                break;
        }
    }

    //返回信息
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
            case addUserActivity:
                if (resultCode == RESULT_OK) {
                    if (user != null && user.length() > 0 && !user.equals(data.getStringExtra("data_user"))) {
                        user = data.getStringExtra("data_user");
                        password = data.getStringExtra("data_password");
                        Log.d(TAG,"add user ok");
                        connectWithServer();
                    } else {
                        user = data.getStringExtra("data_user");
                        password = data.getStringExtra("data_password");
                        Log.d(TAG,"add user ok");
                        connectWithServer();
                    }
                }
                break;
            default:
                break;
        }
    }

    //toolbar菜单栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inbox_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(InboxActivity.this,"search",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectWithServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG,"connecting");
                    utility = new POP3Utility(InboxActivity.this,user,password);
                    utility.getMailInformation();
                    list.clear();
                    List<MailInformation> dataList = mailDB.loadMailInformation(user);
                    list.addAll(dataList);
                    for (int i = 0;i < list.size();i++) {
                        Log.d(TAG,list.get(i).getFrom());
                    }
                    handler.sendEmptyMessage(GET_NEW_MAIL_FINISH);
                } catch (MessagingException e) {
                    Log.d(POP3Utility.TAG,"POP3 fail");
                    handler.sendEmptyMessage(GET_NEW_MAIL_FAIL);
                }

            }
        }).start();
    }

    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("user",user);
        editor.putString("password",password);
        editor.commit();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < list.size()) {
            MailInformation mail = list.get(position);
            Intent readIntent = new Intent(InboxActivity.this,ReadMessageActivity.class);
            readIntent.putExtra("subject",mail.getSubject());
            readIntent.putExtra("from",mail.getFrom());
            readIntent.putExtra("to",mail.getReceiveAddress());
            readIntent.putExtra("sent_date",mail.getSentDate());
            readIntent.putExtra("content",mail.getContent());
            readIntent.putExtra("isContainAttach",mail.getIsContainAttachment());
            startActivity(readIntent);
        }
    }

    @Override
    public void onPullRefresh() {
        connectWithServer();
        listView.completeRefresh();
    }

    @Override
    public void onLoadingMore() {
        adapter.setCount();
        adapter.notifyDataSetChanged();
        listView.completeRefresh();
    }
}
