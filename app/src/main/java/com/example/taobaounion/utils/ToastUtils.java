package com.example.taobaounion.utils;

import android.widget.Toast;

import com.example.taobaounion.base.BaseApplication;

public class ToastUtils {

    private static Toast toast;

    public static void showToast(String tips) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getAppContext(), tips, Toast.LENGTH_SHORT);
        }else{
            toast.setText(tips);
        }
        toast.show();
    }
}
