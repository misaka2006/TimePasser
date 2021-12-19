package com.example.timepasser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }


    //定义一个保存数据的方法
    public void save(String yer, String mon, String day, String name) {
        SharedPreferences sp = mContext.getSharedPreferences("114514", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Y",yer).putString("M",mon).putString("D",day).putString("N",name);
        editor.apply();
        Toast.makeText(mContext, "设置成功！", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> read() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("114514", Context.MODE_PRIVATE);
        data.put("Y", sp.getString("Y", ""));
        data.put("M", sp.getString("M", ""));
        data.put("D", sp.getString("D", ""));
        data.put("N", sp.getString("N", ""));
        return data;
    }
}