package com.jwh.tiantian.activity.battery;

import com.activity.R;
import com.jwh.tiantian.util.ShareData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class PowerFix extends Activity {
	ImageView battery_lv1;
	TextView speed;
	TextView finish;
	TextView trickle;
	TextView pf;
	TextView tv_notice;
	TextView batery_statues;
	ImageView image1;
	ImageView image2;
	ImageView image3;

	int current;
	int charging;

	ShareData sd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		deleteTitle();
		setContentView(R.layout.power_fix);

		sd = new ShareData(getApplicationContext());
		init();
		phoneCharge();
		phoneStates();
		String notice = new StringBuffer()
				.append("健康小巴士：\n   电量低于10%时打开电池维护，为您进行健康充电，建议每月进行一次健康充电，保养电池 ")
				.append("\n快速充电阶段：使用恒流进行快速充电，快速将电池充到80%，建议继续进行，将手机电池充满。\n循环充电阶段：电量即将充满的状态。\n涓流充电：保持充电状态。")
				.toString();
		tv_notice.setText(notice);
	}

	private void deleteTitle() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
	}

	private void init() {
		battery_lv1 = (ImageView) this.findViewById(R.id.battery_lv1);
		speed = (TextView) this.findViewById(R.id.speed);
		finish = (TextView) this.findViewById(R.id.finish);
		trickle = (TextView) this.findViewById(R.id.trickle);
		tv_notice = (TextView) this.findViewById(R.id.tv_notice);
		pf = (TextView) this.findViewById(R.id.pf);
		batery_statues = (TextView) this.findViewById(R.id.battery_statues);

		image1 = (ImageView) this.findViewById(R.id.image1);
		image2 = (ImageView) this.findViewById(R.id.image2);
		image3 = (ImageView) this.findViewById(R.id.image3);
	}

	private void phoneCharge() {
		//当前电量
		int current = sd.getFlag();
		//电池状态
		String statues = sd.getStatues();
		
		if ("充电状态".equals(statues)) {
			pf.setText("正在充电中");
			pf.setTextSize(15);
			pf.setTextColor(Color.GREEN);
			battery_lv1.setBackgroundResource(R.drawable.battery_charging);
			if (current < 80) {
				speed.setTextColor(Color.GREEN);
				speed.setText("1、快速充电        \n进行中");
				finish.setTextColor(Color.BLACK);
				trickle.setTextColor(Color.BLACK);

				image1.setBackgroundResource(R.drawable.battery_bulb_active);
				image2.setBackgroundResource(R.drawable.battery_bulb_deactive);
				image3.setBackgroundResource(R.drawable.battery_bulb_deactive);
			} else if (current < 99 && current > 80) {

				speed.setTextColor(Color.WHITE);
				speed.setText("1、快速充电       \n已经完成");
				finish.setText("2、循环充电        \n进行中");
				finish.setTextColor(Color.GREEN);
				trickle.setTextColor(Color.BLACK);

				image1.setBackgroundResource(R.drawable.battery_bulb_deactive);
				image2.setBackgroundResource(R.drawable.battery_bulb_active);
				image3.setBackgroundResource(R.drawable.battery_bulb_deactive);

			} else {

				speed.setTextColor(Color.BLACK);
				speed.setText("1、快速充电       \n已经完成");
				finish.setText("2、循环充电        \n已经完成");
				finish.setTextColor(Color.BLACK);
				trickle.setText("3、涓流充电        \n进行中");
				trickle.setTextColor(Color.GREEN);

				image1.setBackgroundResource(R.drawable.battery_bulb_deactive);
				image2.setBackgroundResource(R.drawable.battery_bulb_deactive);
				image3.setBackgroundResource(R.drawable.battery_bulb_active);
			}

		} else  {
			
			pf.setText("电量：" + current + "%");
			if(current==100) {
				battery_lv1.setBackgroundResource(R.drawable.bt100);
				
				pf.setTextColor(Color.GREEN);
			}else if(current>90&&current<100){
				battery_lv1.setBackgroundResource(R.drawable.bt90);
				
			}else if(current>80&&current<=90){
				battery_lv1.setBackgroundResource(R.drawable.bt80);
				
			}else if(current>60&&current<=80){
				battery_lv1.setBackgroundResource(R.drawable.bt70);
				
			}else if(current>40&&current<=60){
				battery_lv1.setBackgroundResource(R.drawable.bt50);
				
			}else if(current>20&&current<=40){
				battery_lv1.setBackgroundResource(R.drawable.bt30);
				
			}else  if(current>10&&current<=20){
				battery_lv1.setBackgroundResource(R.drawable.bt20);
				
			}else if(current>0&&current<=10){
				battery_lv1.setBackgroundResource(R.drawable.bt10);
				
			}else{
				battery_lv1.setBackgroundResource(R.drawable.bt0);
				
			}
			image1.setBackgroundResource(R.drawable.battery_bulb_deactive);
			image2.setBackgroundResource(R.drawable.battery_bulb_deactive);
			image3.setBackgroundResource(R.drawable.battery_bulb_deactive);
			pf.setTextSize(20);
		}

	}

	private void phoneStates() {
		String temp = sd.getTemp();
		if ("状态良好".equals(temp)) {
			batery_statues.setText("电池正常使用中");
		} else {
			batery_statues.setText("电池异常，建议关闭手机");
			batery_statues.setTextColor(Color.RED);
		}

	}

}
