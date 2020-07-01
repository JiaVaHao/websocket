package com.jwh.tiantian.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

//Model类 ，用来存储应用程序信息
public class AppInfo {

	private String appLabel; // 应用程序标签
	private Drawable appIcon; // 应用程序图像
	private Intent intent; // 启动应用程序的Intent，一般是Action为Main和Category为Lancher的Activity
	private String pkgName; // 应用程序所对应的包名

	private String versionCode;// 应用程序版本号
	private String versionName;// 应用程序版本名

	private int servicen;// 服务运行数量
	private String num; // 占用内存大小
	private boolean isCheck;// 选中的应用程序是否被关闭
	private int pid;// 服务数量

	public int getServicen() {
		return servicen;
	}

	public void setServicen(int servicen) {
		this.servicen = servicen;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public AppInfo() {
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
}
