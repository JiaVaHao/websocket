package com.jwh.yunchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.jwh.yunchat.controller.ActivityController;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.receiver.ForceOfflineReceiver;
import com.jwh.yunchat.service.WebSocketClient;

import java.net.URI;


public class BaseActivity extends AppCompatActivity {

    public static WebSocketClient wsClient=null;


    //SharedPreferences key-value 组件静态声明，则后期也可以使用
    public static SharedPreferences pref;
    public static SharedPreferences.Editor prefEditor;

    //当前用户
    public static Owner owner;

    //服务器链接
    public static final String SERVER_IP="10.0.2.2";
    public static final String SERVER_PORT="8080";
    public static final String SERVER_URL="http://"+SERVER_IP+":"+SERVER_PORT;
    public static String WS_URL="";
    //WS心跳检测10秒一次
    public static final long HEART_BEAT_RATE = 10 * 1000;
    public static boolean isNeedReconnect=true;
    public static Handler mHandler = new Handler();

    //强制下线广播接收器
    private ForceOfflineReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    //注册绑定接收器
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("FORCE_OFFLINE");
        receiver=new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);
    }
    //解绑接收器
    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    }

    //设置全屏并隐藏导航栏
    public static void setFullScreen(Activity activity){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = activity.getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(uiOptions);
                } else {

                }
            }
        });
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    //初始化WS
    public void initWebSocket(int ownerNetId){
        WS_URL="ws://"+SERVER_IP+":"+SERVER_PORT+"/chatWS/"+ownerNetId;
        URI uri=URI.create(WS_URL);
        wsClient=new WebSocketClient(uri);
        isNeedReconnect=true;
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        try {
            wsClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //WS心跳检测
    //心跳检测，每隔10秒进行一次
    public Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("WS", "心跳包检测连接状态");
            if (wsClient != null) {
                if (wsClient.isClosed() && isNeedReconnect) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化连接
                wsClient = null;
                initWebSocket(owner.getNetId());
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };
    //重新链接
    public void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("WS", "开启重连");
                    wsClient.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
