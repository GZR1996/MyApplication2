package com.example.a.myapplication.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by ASUS on 2016/8/25.
 */
public class PageAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
