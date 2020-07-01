package com.jwh.tiantian.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.activity.R;

public class BaseActivity extends Activity {//Activity基础父类，实现了设置全屏和获得宽高的功能
	public static float ScreenW, ScreenH;//屏幕宽高
	public static ProgressDialog mDialog;//进度条
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setFullScreen();//设置全屏显示
	    // 获得屏幕宽高
	 	DisplayMetrics metrics = new DisplayMetrics();
	 	getWindowManager().getDefaultDisplay().getMetrics(metrics);
 		ScreenW = metrics.widthPixels;
 		ScreenH = metrics.heightPixels;
	}
	
	public void setFullScreen() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
	}
	public void showProgressDialog(String message) {
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(message);
		mDialog.setIcon(R.drawable.icon);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
		mDialog.show();
		mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_BACK:
//							Log.i("aaa", "KEYCODE_BACK");
							return true;
						}
						return false;
					}
				});
	}

}
