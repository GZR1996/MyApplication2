package com.example.a.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a.myapplication.R;
import com.example.a.myapplication.model.MailInformation;

import java.util.List;

/**
 * Created by a on 2016/8/3.
 */
public class MailAdapter extends ArrayAdapter<MailInformation> {

    private int resourceId;
    private int count = 0;
    private int listSize;
    private boolean isAllItemEnabled;

    private static final String TAG = "MailAdapter";

    public MailAdapter(Context context, int resource, List<MailInformation> objects) {
        super(context, resource, objects);
        resourceId = resource;
        isAllItemEnabled = true;
        listSize = objects.size();
        Log.d(TAG,listSize+"");
        if (listSize > 12) {
            count = 12;
        }
    }

    public int getCount() {
        return count;
    }

    public void disableAllItemChooser() {
        isAllItemEnabled = false;
        notifyDataSetChanged();
    }

    public void enableAllItemChooser() {
        isAllItemEnabled = true;
        notifyDataSetChanged();
    }

    public boolean isEnabled(int position) {
        if (position == 0) {
            return false;
        } else if (position == listSize - 1){
            return false;
        }
        return isAllItemEnabled;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MailInformation mail = (MailInformation) getItem(position);
        View view ;
        ViewHolder viewHolder;
        Log.d(TAG,mail.getFrom());
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView)view.findViewById(R.id.item_image);
            viewHolder.from = (TextView)view.findViewById(R.id.item_from);
            viewHolder.sentDate = (TextView)view.findViewById(R.id.item_sent_date);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.image.setImageResource(R.drawable.full_moon);
        viewHolder.from.setText(mail.getFrom());
        viewHolder.sentDate.setText(mail.getSentDate());
        if (isAllItemEnabled  == true) {
            viewHolder.image.setEnabled(true);
            viewHolder.from.setEnabled(true);
            viewHolder.sentDate.setEnabled(true);
        } else {
            viewHolder.image.setEnabled(false);
            viewHolder.from.setEnabled(false);
            viewHolder.sentDate.setEnabled(false);
        }
        return view;
    }

    class ViewHolder {
        private ImageView image;
        private TextView from;
        private TextView sentDate;
    }

    public void setCount() {
        if (listSize > (count + 12)) {
            count += 12;
        } else {
            count = listSize;
        }
    }
}
