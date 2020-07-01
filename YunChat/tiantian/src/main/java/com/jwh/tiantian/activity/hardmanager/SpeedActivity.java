package com.jwh.tiantian.activity.hardmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.R;
import com.jwh.tiantian.bean.AppInfo;
import com.jwh.tiantian.util.AppInfoManager;

public class SpeedActivity extends Activity {
	ProgressBar progressBar;
	TextView count;
	TextView memory;
	TextView ratio;
	Button onekeyclear;
	ListView listView;
	ImageView app;
	TextView name;
	TextView servicen;
	TextView num;
//	CheckBox check;
	ActivityManager mActivityManager;
	AppInfoManager aim;
	HashSet<String> hs;
	MyAdapter adapter;
	ArrayList<AppInfo> apps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed);
		init();

		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		hs = new HashSet<String>();
		// myHandler = new MyHandler();
		aim = new AppInfoManager(this);
		apps = aim.getRunningAppInfo();
		aim.getTotalMemorys();
		aim.getSystemAvaialbeMemory();

		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = listView.getItemAtPosition(position);
				System.out.println("onitemClick position isï¼š" + position);
				if (obj instanceof AppInfo) {

					CheckBox cb = (CheckBox) view.findViewById(R.id.check);
					AppInfo taskinfo = (AppInfo) obj;

					if (taskinfo.getPkgName().equals(getPackageName())) {
						return;
					}

					if (taskinfo.isCheck()) {
						System.out.println("check  is false ");
						taskinfo.setCheck(false);
						cb.setChecked(false);
					} else {
						taskinfo.setCheck(true);
//						check.isClickable();
						cb.setChecked(true);
						System.out.println("  Check is true ");

					}
				}

			}
		});
		oneKeyClear();
	}

	private void oneKeyClear() {

		onekeyclear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ArrayList<AppInfo> killInfo = new ArrayList<AppInfo>();
				int count = 0;
				for (AppInfo info : apps) {
					System.out.println(info.isCheck() + "`````");
					
					if (info.isCheck()) {
						count++;

						mActivityManager.killBackgroundProcesses(info
								.getPkgName());

						
						killInfo.add(info);
					}

				}
				for (AppInfo info : killInfo) {
					if (apps.contains(info)) {
						apps.remove(info);
					}
				}
				Toast.makeText(getApplicationContext(),
						"成功杀死了" + count + " 个进程", 1).show();
				adapter.notifyDataSetChanged();
				System.gc();

			}
		});

	}

	private void init() {
		memory = (TextView) findViewById(R.id.memory);
		count = (TextView) findViewById(R.id.count);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		ratio = (TextView) findViewById(R.id.ratio);
		listView = (ListView) findViewById(R.id.lv);
		onekeyclear = (Button) findViewById(R.id.acceleration);
	}

	@Override
	protected void onStart() {
		super.onStart();

		memory.setText(aim.getEmployMemory() + "/"
				+ aim.getSystemAvaialbeMemory());
		ratio.setText("已用内存:" + aim.getStrMemoryPrecent());
		apps = aim.getRunningAppInfo();
		progressBar.setProgress((int) aim.getMemoryPrecent());
		for (int i = 0; i < apps.size(); i++) {
			hs.add(apps.get(i).getPkgName().toString());
		}
	}

	/*
	 * 自定义adapter
	 */

	class MyAdapter extends BaseAdapter {
		public List<AppInfo> list1;//
		private LayoutInflater layoutInflater;

		private String packageName;

		@Override
		public int getCount() {
			return apps != null ? apps.size() : 0;
		}

		@Override
		public Object getItem(int position) {

			return apps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				System.out.println("getView positionï¼š" + position);
				layoutInflater = LayoutInflater.from(SpeedActivity.this);
				convertView = layoutInflater.inflate(R.layout.item, null);
			}
			Log.i("Log.i", position + "");
			app = (ImageView) convertView.findViewById(R.id.app);
			name = (TextView) convertView.findViewById(R.id.name);
			num = (TextView) convertView.findViewById(R.id.num);
			servicen = (TextView) convertView.findViewById(R.id.servicen);
			CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
			System.out.println("集合大小：" + apps.size());
			app.setImageDrawable((Drawable) apps.get(position).getAppIcon());
			name.setText(apps.get(position).getAppLabel());
			num.setText("内存:" + apps.get(position).getNum());
			count.setText("应用程序：" + apps.size() + "个");
			servicen.setText("服务" + apps.get(position).getServicen() + "   ");
			if (apps.get(position).getPkgName().equals(getPackageName())) {
				System.out.println(apps.get(position).getPkgName()
						+ "    getPackageName()..");
				check.setVisibility(View.INVISIBLE);
			} else {
				check.setVisibility(View.VISIBLE);
			}
			check.setChecked(apps.get(position).isCheck());
			System.out.println(apps.get(position).isCheck() + "     check");
			return convertView;
		}

	}

}
