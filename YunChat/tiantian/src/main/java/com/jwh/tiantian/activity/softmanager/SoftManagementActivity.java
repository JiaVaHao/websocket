package com.jwh.tiantian.activity.softmanager;

import java.util.List;

import com.activity.R;
import com.activity.R.array;
import com.activity.R.drawable;
import com.activity.R.id;
import com.activity.R.layout;
import com.jwh.tiantian.activity.BaseActivity;
import com.jwh.tiantian.adapter.GridViewAdapter;
import com.jwh.tiantian.adapter.ListViewAdapter;
import com.jwh.tiantian.bean.AppInfo;
import com.jwh.tiantian.util.AppInfoManager;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SoftManagementActivity extends BaseActivity implements
		OnItemClickListener {
	private static final int SEARCH_APP = 0;//

	private PackageInfo info;

	private GridView gv;

	private ListView lv;
	// 显示出来的应用程序
	private List<AppInfo> showPackageInfos;

	private AppInfoManager aim;

	private ImageButton iv_thirdapp_view;
	private ImageButton iv_systemapp_view;
	private ImageButton ib_change_view;

	// 应用程序个数
	private TextView apps_num;
	// 设置是否是系统应用程序
	private boolean allApplication = true;
	// 设置是否是列表
	private boolean isListView = false;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == SEARCH_APP) {
				// showPackageInfos = queryFilterAppInfo(filter); // 查询系统应用程序信息
				showPackageInfos = aim.getSystemApp();
				apps_num.setText("系统程序个数为" + showPackageInfos.size());
				isListView = false;
				gv.setAdapter(new GridViewAdapter(SoftManagementActivity.this,
						showPackageInfos));
				lv.setAdapter(new ListViewAdapter(SoftManagementActivity.this,
						showPackageInfos));
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(5000);// 让他显示5秒后，取消ProgressDialog
							BaseActivity.mDialog.dismiss();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				});
				t.start();
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showProgressDialog("正在搜索安装的应用程序...");
		setContentView(layout.softmanagement);

		apps_num = (TextView) this.findViewById(id.tv_appsnum);
		// 实例化GridView
		gv = (GridView) this.findViewById(id.gv_apps);
		// 实例化ListView
		lv = (ListView) this.findViewById(id.lv_apps);
//		lv.setCacheColorHint(0);

		aim = new AppInfoManager(this);
		// 在网格布局上添加监听
		gv.setOnItemClickListener(this);
		// 在列表布局上添加监听
		lv.setOnItemClickListener(this);
		iv_thirdapp_view = (ImageButton) this
				.findViewById(id.iv_thirdapp_view);
		iv_systemapp_view = (ImageButton) this
				.findViewById(id.iv_systemapp_view);
		ib_change_view = (ImageButton) this.findViewById(id.ib_change_view);
		ib_change_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isListView) {
					ib_change_view.setImageResource(drawable.grids);
					Toast.makeText(SoftManagementActivity.this, "网格布局",
							Toast.LENGTH_LONG).show();
					lv.setVisibility(View.GONE);
					gv.setVisibility(View.VISIBLE);

					isListView = false;
				} else {
					ib_change_view.setImageResource(drawable.list);
					Toast.makeText(SoftManagementActivity.this, "列表布局",
							Toast.LENGTH_LONG).show();
					lv.setVisibility(View.VISIBLE);

					isListView = true;
				}

			}

		});
		iv_thirdapp_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_systemapp_view.setBackgroundResource(drawable.top_bg);
				iv_thirdapp_view.setBackgroundResource(drawable.btn_bg);
				iv_thirdapp_view.setEnabled(false);
				iv_systemapp_view.setEnabled(true);
				Toast.makeText(SoftManagementActivity.this, "用户程序",
						Toast.LENGTH_LONG).show();
				// showPackageInfos = queryFilterAppInfo(FILTER_THIRD_APP); //
				// 查询域用户程序信息
				showPackageInfos = aim.getUserApp();
				allApplication = false;
				apps_num.setText("用户程序个数为" + showPackageInfos.size());
				gv.setAdapter(new GridViewAdapter(SoftManagementActivity.this,
						showPackageInfos));
				lv.setAdapter(new ListViewAdapter(SoftManagementActivity.this,
						showPackageInfos));

			}
		});

		iv_systemapp_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_thirdapp_view.setBackgroundResource(drawable.top_bg);
				iv_systemapp_view.setBackgroundResource(drawable.btn_bg);
				iv_systemapp_view.setEnabled(false);
				iv_thirdapp_view.setEnabled(true);
				System.out.println("iv_systemapp_view");
				Toast.makeText(SoftManagementActivity.this, "系统程序",
						Toast.LENGTH_LONG).show();
				// showPackageInfos = queryFilterAppInfo(FILTER_SYSTEM_APP);//
				// 查询系统应用程序信息
				showPackageInfos = aim.getSystemApp();
				allApplication = true;
				apps_num.setText("系统程序个数为" + showPackageInfos.size());
				gv.setAdapter(new GridViewAdapter(SoftManagementActivity.this,
						showPackageInfos));
				lv.setAdapter(new ListViewAdapter(SoftManagementActivity.this,
						showPackageInfos));

			}
		});
		mHandler.sendEmptyMessage(SEARCH_APP);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		final AppInfo tempPkInfo = showPackageInfos.get(position);
		// 创建Dialog的构造器
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选项");
		builder.setItems(array.choice, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showAppDetail(tempPkInfo);
					break;
				case 1:
					System.out.println("package:" + tempPkInfo.getPkgName());
					Uri packageUri = Uri.parse("package:"
							+ tempPkInfo.getPkgName());

					Intent deleteIntent = new Intent();
					deleteIntent.setAction(Intent.ACTION_DELETE);
					deleteIntent.setData(packageUri);
					startActivityForResult(deleteIntent, 0);
					break;
				}

			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();

	}

	/*
	 * 程序卸载结束，重新遍历应用程序和设置适配器
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult-----allApplication"
				+ allApplication);
		if (allApplication) {
			// 系统应用程序
			// showPackageInfos = queryFilterAppInfo(FILTER_SYSTEM_APP);
			showPackageInfos = aim.getSystemApp();
			apps_num.setText("系统程序个数为" + showPackageInfos.size());

		} else {
			// 用户应用程序
			// showPackageInfos = queryFilterAppInfo(FILTER_THIRD_APP);
			showPackageInfos = aim.getUserApp();
			apps_num.setText("用户程序个数为" + showPackageInfos.size());
			System.out.println(showPackageInfos.size());
		}
		// 为网格布局设置适配器
		gv.setAdapter(new GridViewAdapter(SoftManagementActivity.this,
				showPackageInfos));
		// 为列表布局设置适配器
		lv.setAdapter(new ListViewAdapter(SoftManagementActivity.this,
				showPackageInfos));
	}

	/*
	 * 创建应用程序详细信息对话框
	 */
	private void showAppDetail(AppInfo packageInfo) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("    详细信息");
		StringBuffer message = new StringBuffer();
		// 获得应用程序名
		message.append("  程序名称:" + packageInfo.getAppLabel());
		// 获得应用程序的包名
		message.append("\n  程序包名:" + packageInfo.getPkgName());
		// 获得应用程序的版本编号
		message.append("\n  版本编号:" + packageInfo.getVersionCode());
		// 获得应用程序的版本名称
		message.append("\n  版本名称:" + packageInfo.getVersionName());
		// 设置对话框内容：程序名称、程序包名、版本编号、版本名称
		builder.setMessage(message.toString());
		// 设置对话框图标：应用程序图标
		builder.setIcon(packageInfo.getAppIcon());
		builder.setPositiveButton("确定", null);
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			SoftManagementActivity.this.finish();
			return true;
		}
		return false;
	}
}