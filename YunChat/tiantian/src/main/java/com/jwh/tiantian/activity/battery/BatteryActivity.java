package com.jwh.tiantian.activity.battery;

import com.activity.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;


public class BatteryActivity extends TabActivity{
	
	
	Intent intent;
	TabHost mTabHost;
	TabWidget mTabWidget;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		deleteTitle();
		setContentView(R.layout.battery_main);
//		mTabHost = (TabHost) findViewById(R.id.tab_host);
		mTabHost =getTabHost();
		mTabHost.setup(this.getLocalActivityManager());
		mTabWidget = mTabHost.getTabWidget();
		batteryLitsen();
		batteryRepair();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
			//子选项改变的方法
				changeTab(mTabHost.getCurrentTab());
				
			}
		});
		//设置开始
				setCurrentTab();
				changeTab(0);
	}
	//电池监控
	private void batteryLitsen(){
		LayoutInflater factory = LayoutInflater.from(BatteryActivity.this);
		View view1 = factory.inflate(R.layout.tab, null);
		((TextView) view1.findViewById(R.id.tab_textview_title))
				.setText("电池监控");
		TabHost.TabSpec spec1 = mTabHost.newTabSpec("电池监控")
				.setIndicator(view1).setContent(
						new Intent(this, PowerMain.class));
		mTabHost.addTab(spec1);
	}
	//电池维护
	private void batteryRepair() {
		LayoutInflater factory = LayoutInflater.from(BatteryActivity.this);
		View view2 = factory.inflate(R.layout.tab, null);
		((TextView) view2.findViewById(R.id.tab_textview_title))
				.setText("电池维护");
		TabHost.TabSpec spec2 = mTabHost.newTabSpec("systemInfo")
				.setIndicator(view2).setContent(
						new Intent(this, PowerFix.class));
		mTabHost.addTab(spec2);
		
	}
	//默认设置在第一项
		private void setCurrentTab() {
			mTabHost.setCurrentTab(0);
		}
	
private void deleteTitle() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
	}
private void changeTab(int curTab) {
	for (int i = 0; i < mTabWidget.getChildCount(); i++) {
		updateTabBackground(curTab, i);
	}

}
boolean fristenter = true;
private void updateTabBackground(int currentTab, int i) {
		//获取TabHost的子选项
				View view = mTabWidget.getChildAt(i);
				//设置背景
				view.setBackgroundResource(R.drawable.img_backgra);
				ImageView icon = (ImageView) view.findViewById(R.id.tab_imageview_icon);
				TextView text = (TextView) view.findViewById(R.id.tab_textview_title);
				icon.setPadding(0, 0, 0, 0);//设置补白系数
				System.out.println(currentTab+"---"+i);
				if (currentTab == i) {
					switch (i) {
					case 0:
						//电池监控
						icon.setImageResource(R.drawable.battery_manager);
						break;
					case 1:
						//电池维护
						icon.setImageResource(R.drawable.battery_fix);
						break;
					
					}
				} else {
					switch (i) {
					case 1:
						view.setBackgroundResource(R.drawable.backimg_fouce);
						icon.setImageResource(R.drawable.battery_fix);
						break;
					case 0:
						if (fristenter) {
							fristenter = false;
							icon.setImageResource(R.drawable.battery_manager);
						}
						view.setBackgroundResource(R.drawable.backimg_fouce);
						break;
					default:
						break;
					}
				}
				view.setPadding(0, 0, 0, 0);
	}   
    }
