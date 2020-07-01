package com.jwh.tiantian.activity.battery;

import com.activity.R;
import com.jwh.tiantian.util.ShareData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PowerMain extends Activity {
	ImageView battery_lv;
	TextView power;
	TextView temp;
	TextView vol;
	TextView statues;
	TextView use;

	int current;//获得当前电量
	int total;//获取总电量
	int i_voltage;//获得电压
	int i_temperature;//获得温度

	ShareData sd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.power_info);
		sd = new ShareData(getApplicationContext());

		init();
		registerBattery();

	}

	private void init() {
		battery_lv = (ImageView) this.findViewById(R.id.battery_lv);
		power = (TextView) this.findViewById(R.id.power);
		statues = (TextView) this.findViewById(R.id.states);
		temp = (TextView) this.findViewById(R.id.temp);
		vol = (TextView) this.findViewById(R.id.vol);
		use = (TextView) this.findViewById(R.id.use);

	}

	private void registerBattery() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 
		BatteryReceiver receiver = new BatteryReceiver();
		registerReceiver(receiver, filter);
	}

	//广播 电池
	private class BatteryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			current = intent.getExtras().getInt("level");//获得当前电量
			total = intent.getExtras().getInt("scale");//获得总电量
			i_voltage = intent.getExtras().getInt("voltage");//获得电压
			i_temperature = intent.getExtras().getInt("temperature");//获得温度
			int percent = current * 100 / total;
			sd.saveFlag(percent);
			
			if(percent==100) {
				battery_lv.setBackgroundResource(R.drawable.bt100);
			}else if(percent>90&&percent<100){
				battery_lv.setBackgroundResource(R.drawable.bt90);
			}else if(percent>80&&percent<=90){
				battery_lv.setBackgroundResource(R.drawable.bt80);
			}else if(percent>60&&percent<=80){
				battery_lv.setBackgroundResource(R.drawable.bt70);
			}else if(percent>40&&percent<=60){
				battery_lv.setBackgroundResource(R.drawable.bt50);
			}else if(percent>20&&percent<=40){
				battery_lv.setBackgroundResource(R.drawable.bt30);
			}else  if(percent>10&&percent<=20){
				battery_lv.setBackgroundResource(R.drawable.bt20);
			}else if(percent>0&&percent<=10){
				battery_lv.setBackgroundResource(R.drawable.bt10);
			}else{
				battery_lv.setBackgroundResource(R.drawable.bt0);
			}

			String BatteryStatus = null;//电池状态??
			String BatteryTemp = null; //电池使用情况
			switch (intent.getIntExtra("status",
					BatteryManager.BATTERY_STATUS_UNKNOWN)) {
			case BatteryManager.BATTERY_STATUS_CHARGING:
				BatteryStatus = "充电状态";
				
				sd.saveStatues(BatteryStatus);
				statues.setTextColor(Color.GREEN);
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				BatteryStatus = "放电状态";
				sd.saveStatues(BatteryStatus);
				statues.setTextColor(Color.YELLOW);
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				BatteryStatus = "未充电";
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				BatteryStatus = "充满电";
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				BatteryStatus = "未知道状态";
				break;
			}

			switch (intent.getIntExtra("health",
					BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				BatteryTemp = "未知错误";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				BatteryTemp = "状态良好";
				use.setTextColor(Color.GREEN);
				sd.saveTemp(BatteryTemp);
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				BatteryTemp = "电池没有电";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				BatteryTemp = "电池电压过高";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				BatteryTemp = "电池过热";
				use.setTextColor(Color.RED);
				break;
			}

			power.setText(percent + "%");
			power.setTextColor(Color.GREEN);
			vol.setText(i_voltage + "mV");
			vol.setTextColor(Color.GREEN);
			if(i_temperature * 0.1f<30){
				temp.setTextColor(Color.GREEN);
			}else{
				temp.setTextColor(Color.RED);
			}
			temp.setText(i_temperature * 0.1f + " 度");
			statues.setText(BatteryStatus);
			use.setText(BatteryTemp);
		}
	}

}
