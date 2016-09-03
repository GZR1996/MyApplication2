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
 * Created by Administrator on 2016/8/7.
 */
public class DrawerOptionAdapter extends ArrayAdapter<String> {

    private int resource;
    private static final int[] drawerImage = {R.drawable.inbox,R.drawable.flag,R.drawable.draft,
                                                  R.drawable.ok,R.drawable.junkmail,R.drawable.delete};

    private static final String TAG = "DrawerOptionAdapter";

    public DrawerOptionAdapter(Context context, int resource, List<String> objects) {
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
        viewHolder.imageView.setImageResource(drawerImage[position]);
        viewHolder.textView.setText(text);
        return view;
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
