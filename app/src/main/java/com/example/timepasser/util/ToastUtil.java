package com.example.timepasser.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private final Context mContext;

    public ToastUtil(Context context){
        this.mContext = context;
    }
    public void showToast(CharSequence text){
        Toast.makeText(mContext,text,Toast.LENGTH_SHORT).show();
    }
}
