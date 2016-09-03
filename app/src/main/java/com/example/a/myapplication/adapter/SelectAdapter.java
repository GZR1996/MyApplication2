package com.example.a.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a.myapplication.R;

import java.io.File;
import java.util.List;

/**
 * Created by ASUS on 2016/8/18.
 */
public class SelectAdapter extends ArrayAdapter<File> {

    private int resource;

    private static final String TAG = "SelectAdapter";

    public SelectAdapter(Context context, int resource, List<File> objects) {
        super(context,resource,objects);
        this.resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        File file = (File) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resource,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)view.findViewById(R.id.select_file_image);
            viewHolder.textView = (TextView)view.findViewById(R.id.select_file_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (file.isDirectory()) {
            viewHolder.imageView.setImageResource(R.drawable.folder);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.file);
        }
        viewHolder.textView.setText(file.getName());
        return view;
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
