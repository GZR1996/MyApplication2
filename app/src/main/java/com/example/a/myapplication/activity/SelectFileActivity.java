package com.example.a.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.a.myapplication.R;
import com.example.a.myapplication.adapter.SelectAdapter;
import com.example.a.myapplication.util.SDCardUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ASUS on 2016/8/18.
 */
public class SelectFileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "SelectFileActivity";
    private File currentFile = Environment.getExternalStorageDirectory();
    //控件
    private List<File> list;
    private ListView listView;
    private ArrayAdapter<File> adapter;
    private SDCardUtility utility;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        utility = new SDCardUtility();

        list = new ArrayList<>();
        List<File> dataList = utility.loadCurrentPathFiles();
        list.addAll(dataList);
        adapter = new SelectAdapter(SelectFileActivity.this,R.layout.select_list_item,list);
        listView = (ListView)findViewById(R.id.select_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = list.get(position);
        if (file.isDirectory()) {
            utility.setPath(file);
            Log.d(TAG,file.getPath());
            list.clear();
            if (!file.getPath().equals(Environment.getExternalStorageState())) {
                Log.d(TAG,file.getPath() + "\n" +Environment.getExternalStorageState());
                list.add(file);
            }
            List<File> dataList = utility.loadCurrentPathFiles();
            list.addAll(dataList);
            adapter.notifyDataSetChanged();
        } else {
            Intent intent = new Intent();
            intent.putExtra("file_path",file.getAbsolutePath());
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
