package com.example.a.myapplication.component;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.a.myapplication.R;
import com.example.a.myapplication.adapter.DrawerOptionAdapter;
import com.example.a.myapplication.adapter.DrawerUserAdapter;
import com.example.a.myapplication.database.MailDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/8/26.
 */
public class InboxFragment extends Fragment implements View.OnClickListener{

    private MailDB mailDB;
    private Activity activity;
    //组件
    private @LayoutRes int layout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    //一般操作菜单
    private static String[] drawerContent = {"收件箱","红旗邮件","草稿箱","已发送", "垃圾邮件","已删除"};
    private ListView drawerOptionListView;
    private List<String> drawerOptionList;
    private DrawerOptionAdapter drawerOptionAdapter;
    //添加邮箱
    private LinearLayout drawerAddLayout;
    //所有收件箱
    private LinearLayout drawerInboxLayout;
    //所有未读
    private LinearLayout drawerUnreadLayout;
    //所有红旗
    private LinearLayout drawerFlagLayout;
    //所有用户
    private List<String> drawerUserList;
    private ListView drawerUserListView;
    private DrawerUserAdapter drawerUserAdapter;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        activity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceState) {
        if (mailDB.isEmpty() && mailDB.getNumUser() < 1) {
            layout = R.layout.drawer_menu_single;
        } else {
            layout = R.layout.drawer_menu_multi;
        }

        View view = inflater.inflate(layout,parent,false);

        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        drawerToggle = new ActionBarDrawerToggle(activity,drawerLayout,toolbar,R.string.open, R.string.close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        //所有收件箱
        drawerInboxLayout = (LinearLayout) view.findViewById(R.id.drawer_inbox_layout);
        if (drawerInboxLayout != null) {
            drawerInboxLayout.setOnClickListener(this);
        }

        //所有未读
        drawerUnreadLayout = (LinearLayout) view.findViewById(R.id.drawer_unread_layout);
        if (drawerInboxLayout != null) {
            drawerUnreadLayout.setOnClickListener(this);
        }

        //所有红旗
        drawerFlagLayout = (LinearLayout) view.findViewById(R.id.drawer_flag_layout);
        if (drawerFlagLayout != null) {
            drawerFlagLayout.setOnClickListener(this);
        }

        //所有用户
        drawerUserList = mailDB.loadAllUser();
        drawerUserAdapter = new DrawerUserAdapter(activity,R.layout.drawer_list_item,drawerUserList);



        //一般操作菜单
        drawerOptionListView = (ListView) view.findViewById(R.id.drawer_option_list);
        drawerOptionList = new ArrayList<>();
        for (int i = 0;i < 6;i++) {
            drawerOptionList.add(drawerContent[i]);
        }
        drawerOptionAdapter = new DrawerOptionAdapter(activity,R.layout.drawer_list_item,drawerOptionList);
        drawerOptionListView.setAdapter(drawerOptionAdapter);

        //添加邮箱
        drawerAddLayout = (LinearLayout) view.findViewById(R.id.drawer_linear_layout);
        drawerAddLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
