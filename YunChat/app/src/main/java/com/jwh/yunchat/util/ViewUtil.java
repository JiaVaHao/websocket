package com.jwh.yunchat.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class ViewUtil {
    //统一提示弹窗接口
    public static void makePopup(Context context, String title, String content){
        //创建一个弹窗,不可取消类型
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setCancelable(false);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}
