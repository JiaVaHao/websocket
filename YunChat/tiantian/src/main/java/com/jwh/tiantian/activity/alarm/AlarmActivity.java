package com.jwh.tiantian.activity.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

import com.activity.R;
import com.activity.R.drawable;

public class AlarmActivity extends Activity {
	private MediaPlayer mMediaPlayer;
	int recLen = 0;
	// 创建定时器
	Timer timer = new Timer(true);
	TimerTask task = new TimerTask() {
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recLen = 0;
		timer.schedule(task, 1000, 1000);
		// 开启音乐，设置手机铃声为闹钟音乐
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.reset();
			mMediaPlayer.setLooping(true);
			mMediaPlayer.setDataSource(this, RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			Log.e("Timedown", e.toString());
		}
		// 闹钟提示对话框
		new AlertDialog.Builder(AlarmActivity.this)
				.setIcon(drawable.clock)
				.setTitle("闹钟")
				.setMessage("亲，时间到了！")
				.setPositiveButton("知道了",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mMediaPlayer.stop();
								mMediaPlayer.release();
								mMediaPlayer = null;
								AlarmActivity.this.finish();
//								Intent intent=new Intent(AlarmActivity.this,MenuActivity.class);
//								startActivity(intent);
								timer.cancel();
								
							}
						}).show();
	}

	// 创建Handler
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				recLen++;
				Log.e("Timer", "" + recLen);
				break;
			}
			super.handleMessage(msg);
		}
	};
}
