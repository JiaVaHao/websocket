package com.jwh.tiantian.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.activity.R;

public class LogoActivity extends BaseActivity implements Runnable{//显示logo界面
    long startTime;//开始时间

    NotificationManager nm;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.logo);//设置logo布局
//       
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(LogoActivity.this);

        Intent notificationIntent = new Intent(this, MenuActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder.setTicker("打开天天");

        builder.setContentTitle("天天管家");

        builder.setContentText("点击打开天天手机管家");

        builder.setSmallIcon(R.drawable.icon);

        builder.setWhen(System.currentTimeMillis());

        builder.setContentIntent(contentIntent);//执行intent

        Notification notification = builder.build();//将builder对象转换为普通的notification

        notification.flags = Notification.DEFAULT_VIBRATE;

        manager.notify(1,notification);//运行notification

        Notification not = builder.build();

        long[] vibrate = {0,100,200,300}; 

        not.vibrate = vibrate; 
        /* 

        * 添加声音 

        * notification.defaults |=Notification.DEFAULT_SOUND; 

        * 或者使用以下几种方式 

        * notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3"); 

        * notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6"); 

        * 如果想要让声音持续重复直到用户对通知做出反应，则可以在notification的flags字段增加"FLAG_INSISTENT" 

        * 如果notification的defaults字段包括了"DEFAULT_SOUND"属性，则这个属性将覆盖sound字段中定义的声音 

        */ 

        /* 

        * 添加振动 

        * notification.defaults |= Notification.DEFAULT_VIBRATE; 

        * 或者可以定义自己的振动模式： 

        * long[] vibrate = {0,100,200,300}; //0毫秒后开始振动，振动100毫秒后停止，再过200毫秒后再次振动300毫秒 

        * notification.vibrate = vibrate; 

        * long数组可以定义成想要的任何长度 

        * 如果notification的defaults字段包括了"DEFAULT_VIBRATE",则这个属性将覆盖vibrate字段中定义的振动 

        */ 

        /* 

        * 添加LED灯提醒 

        * notification.defaults |= Notification.DEFAULT_LIGHTS; 

        * 或者可以自己的LED提醒模式: 

        * notification.ledARGB = 0xff00ff00; 

        * notification.ledOnMS = 300; //亮的时间 

        * notification.ledOffMS = 1000; //灭的时间 

        * notification.flags |= Notification.FLAG_SHOW_LIGHTS; 

        */ 

        /* 

        * 更多的特征属性 

        * notification.flags |= FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知 

        * notification.flags |= FLAG_INSISTENT; //重复发出声音，直到用户响应此通知 

        * notification.flags |= FLAG_ONGOING_EVENT; //将此通知放到通知栏的"Ongoing"即"正在运行"组中 

        * notification.flags |= FLAG_NO_CLEAR; //表明在点击了通知栏中的"清除通知"后，此通知不清除， 

        * //经常与FLAG_ONGOING_EVENT一起使用 

        * notification.number = 1; //number字段表示此通知代表的当前事件数量，它将覆盖在状态栏图标的顶部 

        * //如果要使用此字段，必须从1开始 

        * notification.iconLevel = ; // 

        */ 
//        not.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知 


        nm.notify(0, not);

        new Thread(this).start();
    }
    
	public void run() {
		while(System.currentTimeMillis()-startTime < 2000){//控制显示2秒
			Thread.yield();
		}
		Intent menuIntent = new Intent(LogoActivity.this,MenuActivity.class);
		startActivity(menuIntent);//2秒后，跳转到menu
		finish();
	}
}