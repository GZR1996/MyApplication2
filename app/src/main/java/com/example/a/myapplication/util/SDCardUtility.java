package com.example.a.myapplication.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class SDCardUtility {

    private static final String TAG = "SDCardUtility";
    private File path;
    private long availableStorage;

    public SDCardUtility() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory();
            Log.d(TAG,path.getPath());
        }
        StatFs statFs = new StatFs(path.getPath());
        availableStorage = statFs.getAvailableBlocksLong();
    }

    public List<File> loadCurrentPathFiles() {
        File[] files = path.listFiles();
        List<File> list = new ArrayList<>();
        for(File file : files) {
            list.add(file);
        }
        ListComparator comparator = new ListComparator();
        Collections.sort(list,comparator);
        return list;
    }

    public void setPath(File path) {
        this.path = path;
    }

    private class ListComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            String name1 = lhs.getName();
            String name2 = rhs.getName();
            if ((lhs.isDirectory() && rhs.isDirectory()) || (!lhs.isDirectory() && !rhs.isDirectory())) {
                return name1.compareTo(name2);
            } else if (lhs.isDirectory() && !rhs.isDirectory()){
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            }
            return name1.compareTo(name2);
        }
    }
}
