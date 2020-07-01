package com.jwh.yunchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.activity.LoginActivity;
import com.jwh.yunchat.controller.ActivityController;

public class ForceOfflineReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //创建一个弹窗,不可取消类型
        String warringMsg=intent.getStringExtra("warringMsg");
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("警告");
        builder.setMessage(warringMsg);
        builder.setCancelable(false);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityController.finishAll();
                //关闭WS
                BaseActivity.wsClient.close();
                BaseActivity.isNeedReconnect=false;
                Intent intent1=new Intent(context, LoginActivity.class);
                context.startActivity(intent1);
            }
        });
        builder.show();
    }
}