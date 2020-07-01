package com.jwh.tiantian.activity.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.activity.R;
import com.activity.R.drawable;
import com.activity.R.id;
import com.activity.R.layout;
import com.jwh.tiantian.activity.BaseActivity;
import com.jwh.tiantian.receiver.AlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log; //调试包
import android.view.Gravity;

public final class ClockActivity extends BaseActivity {
	Button btn_settime; // 设置闹钟时间
	Button btn_setclock; // 闹钟是否可用
	TextView tv_date;// 显示日期，格式yyyy-MM-dd
	TextView tv_week;// 显示星期
	String week;// 存储星期
	int state = 0; // 闹钟状态 （0:disable 1:enable）
	
	Calendar c = Calendar.getInstance();// 定义日历控件
	Date date = new Date(System.currentTimeMillis());// 获取当前时间
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.clock);
		tv_date = (TextView) findViewById(id.tv_date);
		tv_week = (TextView) findViewById(id.tv_week);
		btn_settime = (Button) findViewById(id.btn_settime);
		btn_setclock = (Button) findViewById(id.btn_setclock);
		tv_date.setText(simpleDateFormat.format(date));
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			week = "星期天";
		} else {
			switch ((c.get(Calendar.DAY_OF_WEEK) - 1)) {
			case 1:
				week = "星期一";
				break;
			case 2:
				week = "星期二";
				break;
			case 3:
				week = "星期三";
				break;
			case 4:
				week = "星期四";
				break;
			case 5:
				week = "星期五";
				break;
			case 6:
				week = "星期六";
				break;
			}
		}
		tv_week.setText(week);
		// 加载timeset.xml布局
		LayoutInflater factory = LayoutInflater.from(this);
		final View setView = factory.inflate(layout.timeset, null);
		final TimePicker tPicker = (TimePicker) setView
				.findViewById(id.tPicker);
		tPicker.setIs24HourView(true);// 设置日期为24小时格式

		// 使用AlertDialog设置时间
		final AlertDialog di = new AlertDialog.Builder(ClockActivity.this)
				.setIcon(drawable.clock)
				.setTitle("设置")
				.setView(setView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 取得设置的开始时间，秒及毫秒设为0
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, tPicker.getCurrentHour());
						c.set(Calendar.MINUTE, tPicker.getCurrentMinute());
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						// 更新显示的设置闹钟时间
						String tmpS = format(tPicker.getCurrentHour()) + "："
								+ format(tPicker.getCurrentMinute());
						btn_settime.setText("" + tmpS);
						// 以Toast提示设置已完成
						Toast toast = Toast.makeText(ClockActivity.this,
								"设置闹钟时间为" + tmpS, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 40);
						toast.show();
						state = 1;
						btn_setclock.setText("关闭闹钟");
						Toast.makeText(ClockActivity.this, "闹钟启用",
								Toast.LENGTH_SHORT).show();
						if (state == 1)// 闹钟启用状态
						{
							// 指定闹钟设置时间到时要运行AlarmReceiver.class
							Intent intent = new Intent(ClockActivity.this,
									AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(
									ClockActivity.this, 1, intent, 0);
							// 设置闹钟
							AlarmManager am;
							am = (AlarmManager) getSystemService(ALARM_SERVICE);
							if (c.getTimeInMillis()
									- System.currentTimeMillis() > 0) {
								am.set(AlarmManager.RTC_WAKEUP,
										c.getTimeInMillis(), sender);
							} else {
								Toast.makeText(getApplicationContext(),
										"闹钟时间已过，请重新设置···", 1).show();
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		// 设置时间按钮点击事件
		btn_settime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 取得点击按钮时的时间作为tPicker的默认值
				c.setTimeInMillis(System.currentTimeMillis());
				tPicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				tPicker.setCurrentMinute(c.get(Calendar.MINUTE));
				di.show();
			}
		});
		// 设置闹钟按钮点击事件
		btn_setclock.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == 0)// 闹钟禁用状态
				{
					Intent intent = new Intent(ClockActivity.this,
							AlarmReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							ClockActivity.this, 1, intent, 0);
					// 设置闹钟
					AlarmManager am;
					am = (AlarmManager) getSystemService(ALARM_SERVICE);
					// 在指定的时刻（设置Alarm的时候），唤醒设备来触发Intent。
					if (c.getTimeInMillis() - System.currentTimeMillis() > 0) {
						am.set(AlarmManager.RTC_WAKEUP,// 设置服务在系统休眠时同样会运行
								c.getTimeInMillis(), sender);
					} else {
						Toast.makeText(getApplicationContext(),
								"闹钟时间已过，请重新设置···", 1).show();
					}
					state = 1;
					// 提示设置已完成
					Toast toast = Toast.makeText(ClockActivity.this, "闹钟启用",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 40);
					toast.show();
					btn_setclock.setText("关闭闹钟");
				} else if (state == 1)// 闹钟启用状态
				{
					Intent intent = new Intent(ClockActivity.this,
							AlarmReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							ClockActivity.this, 1, intent, 0);
					// 由AlarmManager中删除
					AlarmManager am;
					am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.cancel(sender);
					Toast toast = Toast.makeText(ClockActivity.this, "闹钟禁用",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 40);
					toast.show();
					state = 0;
					btn_settime.setText("设置时间");
					btn_setclock.setText("开启闹钟");
				}
			}
		});
	}

	// 设置时间格式方法
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
}
