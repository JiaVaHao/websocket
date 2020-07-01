package com.jwh.tiantian.activity.hardmanager;

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

public class DriverActivity extends TabActivity {
	Intent intent;
	TabHost mTabHost;
	TabWidget mTabWidget;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//删除标题
		deleteTitle();
		//加载
		setContentView(R.layout.maintab);
		mTabHost = getTabHost();
		mTabHost.setup(this.getLocalActivityManager());
		mTabWidget = mTabHost.getTabWidget();
		phoneSpeed();
		systemInfo();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// 子选项改变的方法
				changeTab(mTabHost.getCurrentTab());
			}
		});
		// 设置初始
		setCurrentTab();
		changeTab(0);
	}

	// 手机加速
	private void phoneSpeed() {
		LayoutInflater factory = LayoutInflater.from(DriverActivity.this);
		View view1 = factory.inflate(R.layout.tab, null);
		((TextView) view1.findViewById(R.id.tab_textview_title))
				.setText("手机加速");
		TabHost.TabSpec spec1 = mTabHost.newTabSpec("phoneSpeed")
				.setIndicator(view1)
				.setContent(new Intent(this, SpeedActivity.class));
		mTabHost.addTab(spec1);
	}

	// 系统检测
	private void systemInfo() {
		LayoutInflater factory = LayoutInflater.from(DriverActivity.this);
		View view2 = factory.inflate(R.layout.tab, null);
		((TextView) view2.findViewById(R.id.tab_textview_title))
				.setText("系统检测");
		TabHost.TabSpec spec2 = mTabHost.newTabSpec("systemInfo")
				.setIndicator(view2)
				.setContent(new Intent(this, InfoActivity.class));
		mTabHost.addTab(spec2);
	}
	// 默认设置在第一选项
	private void setCurrentTab() {
		mTabHost.setCurrentTab(0);
	}

	private void deleteTitle() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
	}
	private void changeTab(int curTab) {
		for (int i = 0; i < mTabWidget.getChildCount(); i++) {
			//
			updateTabBackground(curTab, i);
		}

	}

	boolean fristenter = true;

	private void updateTabBackground(int currentTab, int i) {
		// 获取TabHost的子选项
		View view = mTabWidget.getChildAt(i);
		// 设置背景
		view.setBackgroundResource(R.drawable.img_backgra);
		ImageView icon = (ImageView) view.findViewById(R.id.tab_imageview_icon);
		TextView text = (TextView) view.findViewById(R.id.tab_textview_title);
icon.setPadding(0, 0, 0, 0);
		System.out.println(currentTab + "---" + i);
		if (currentTab == i) {
			switch (i) {
			case 0:
				// 手机加速
				icon.setImageResource(R.drawable.phone_speed);
				break;
			case 1:
				// 系统检测
				icon.setImageResource(R.drawable.phone_sys);
				break;

			}
		} else {
			switch (i) {
			case 1:
				view.setBackgroundResource(R.drawable.backimg_fouce);
				icon.setImageResource(R.drawable.phone_sys);
				break;
			case 0:
				if (fristenter) {
					fristenter = false;
 icon.setImageResource(R.drawable.phone_speed);
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