package com.example.a.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a.myapplication.R;

import java.util.List;

/**
 * Created by zero on 2016/9/3.
 */
public class DrawerUserAdapter extends ArrayAdapter<String> {

    private int resource;

    private static final String TAG = "DrawerOptionAdapter";

    public DrawerUserAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String text = (String) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resource,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)view.findViewById(R.id.drawer_item_image);
            viewHolder.textView = (TextView)view.findViewById(R.id.drawer_item_text);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.letter);
        viewHolder.textView.setText(text);
        return view;
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
