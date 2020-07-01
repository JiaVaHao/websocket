package com.jwh.tiantian.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.activity.R;
import com.jwh.tiantian.bean.AppInfo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

@SuppressLint("NewApi")
public class AppInfoManager {
	private ActivityManager mActivityManager;
	private List<PackageInfo> allPackageInfos; // 取得安装的所有软件信息
	public static long total_memory = 0; // 总内存
	public static long memSize = 0;// 可用内存
	List<Map<String, Object>> listItems;
	Context context;

	public AppInfoManager(Context context) {
		this.context = context;
		this.mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		allPackageInfos = context.getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES
						| PackageManager.GET_ACTIVITIES); // 初始化时先要得到当前的所有进程
	}

	public List<PackageInfo> getSystemPackageList() {
		List<PackageInfo> sysPackageInfos = new ArrayList<PackageInfo>();// 定义系统安装软件信息包
		for (int i = 0; i < allPackageInfos.size(); i++) {// 循环取出所有软件信息
			PackageInfo temp = allPackageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;// 得到每个软件信息
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
					|| (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				sysPackageInfos.add(temp);// 系统软件
			}
		}

		return sysPackageInfos;
	}

	// 系统软件 用户软件
	public List<PackageInfo> getUserPackageList() {
		List<PackageInfo> userPackageInfos = new ArrayList<PackageInfo>();// 定义系统安装软件信息包
		for (int i = 0; i < allPackageInfos.size(); i++) {// 循环取出所有软件信息
			PackageInfo temp = allPackageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;// 得到每个软件信息
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
					|| (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

			} else {
				userPackageInfos.add(temp);// 是用户自己安装软件
			}
		}

		return userPackageInfos;
	}

	// 正在运行的应用程序
	public List<PackageInfo> getRunningAppList() {
		// 取得当前的程序
		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();
		List<PackageInfo> RunningAppProcessPackageInfos = new ArrayList<PackageInfo>();
		for (int i = 0; i < allPackageInfos.size(); i++) {
			for (int j = 0; j < runningAppProcessInfos.size(); j++) {
				if (allPackageInfos.get(i).packageName
						.equals(runningAppProcessInfos.get(j).processName)
						&& (allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
						&& ((allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
					RunningAppProcessPackageInfos.add(allPackageInfos.get(i));
					// Log.i("Log.i",
					// allPackageInfos.get(i).applicationInfo.processName);
				}
			}
		}

		return RunningAppProcessPackageInfos;
	}

	// public List<Map<String, Object>> getM() {
	// List<Map<String, Object>> listItems = new ArrayList<Map<String,
	// Object>>();
	// List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
	// .getRunningAppProcesses();
	// List<PackageInfo> RunningAppProcessPackageInfos = new
	// ArrayList<PackageInfo>();
	//
	// //List<AppInfo> list = new ArrayList<AppInfo>();
	// AppInfo aib = null;
	// for (int i = 0; i < allPackageInfos.size(); i++) {
	//
	// for (int j = 0; j < runningAppProcessInfos.size(); j++) {
	//
	// if (allPackageInfos.get(i).packageName
	// .equals(runningAppProcessInfos.get(j).processName)
	// && (allPackageInfos.get(i).applicationInfo.flags &
	// ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
	// && ((allPackageInfos.get(i).applicationInfo.flags &
	// ApplicationInfo.FLAG_SYSTEM) == 0)) {
	// // RunningAppProcessPackageInfos.add(allPackageInfos.get(i));
	//
	// int[] pid={runningAppProcessInfos.get(j).pid};
	//
	// Debug.MemoryInfo[]
	// memoryInfos=mActivityManager.getProcessMemoryInfo(pid);
	//
	// aib = new AppInfo();
	// aib.setNum(Number3((memoryInfos[0].dalvikPrivateDirty)/1024.0)+"MB");
	// aib.setIcon(allPackageInfos.get(i).applicationInfo
	// .loadIcon(context.getPackageManager()));
	// aib.setName(allPackageInfos.get(i).applicationInfo
	// .loadLabel(context.getPackageManager()).toString());
	// aib.setPackageName(allPackageInfos.get(i).applicationInfo.packageName);
	// aib.setPid(runningAppProcessInfos.get(j).pid);
	// aib.setServicen(1);// 暂时没能获取服务数量
	// // list.add(aib);
	//
	// Map<String, Object> listItem = new HashMap<String, Object>();
	// listItem.put("app", aib.getIcon());
	// listItem.put("name", aib.getName());
	// listItem.put("packageName", aib.getPackageName());
	// listItem.put("num", aib.getNum());
	// listItem.put("servicen", aib.getServicen());
	// // listItem.put("check", aib.isCheck());
	// listItems.add(listItem);
	//
	// }
	// }
	// }
	// return listItems;
	// }

	// /**
	// * 先要通过getRunningAppList（）得到当前正在运行的应用程序集合
	// *
	// * @param runningAppList
	// * 当前正在运行的应用程序集合
	// * @return 当前正在运行的应用程序具体信息集合
	// */
	// public List<Map<String, Object>> getRunningAppInfos(
	// List<PackageInfo> runningAppList) {
	// List<Map<String, Object>> listItems = new ArrayList<Map<String,
	// Object>>();
	// AppInfo aib;
	// for (int i = 0; i < runningAppList.size(); i++) {
	// aib = new AppInfo();
	// aib.setIcon(runningAppList.get(i).applicationInfo.loadIcon(context
	// .getPackageManager()));
	// //
	// aib.setIcon(userPackageInfos.get(i).applicationInfo.loadIcon(getPackageManager()));
	// aib.setName(runningAppList.get(i).applicationInfo.loadLabel(
	// context.getPackageManager()).toString());
	// aib.setPackageName(runningAppList.get(i).applicationInfo.packageName);
	// aib.setNum(1 + ""); // 暂时没能获取占用内存
	//
	// aib.setServicen(1);// 暂时没能获取服务数量
	// // aib.setCheck(true);
	// Map<String, Object> listItem = new HashMap<String, Object>();
	// listItem.put("app", aib.getIcon());
	// listItem.put("name", aib.getName());
	// listItem.put("packageName", aib.getPackageName());
	// listItem.put("num", aib.getNum());
	//
	// listItem.put("servicen", aib.getServicen());
	// // listItem.put("check", aib.isCheck());
	//
	// listItems.add(listItem);
	// }
	//
	// return listItems;
	// }

	public ArrayList<AppInfo> getRunningAppInfo() {

		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();

		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo aib = null;
		for (int i = 0; i < allPackageInfos.size(); i++) {

			for (int j = 0; j < runningAppProcessInfos.size(); j++) {

				if (allPackageInfos.get(i).packageName
						.equals(runningAppProcessInfos.get(j).processName)
						&& (allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
						&& ((allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
					// RunningAppProcessPackageInfos.add(allPackageInfos.get(i));
					int[] pid = { runningAppProcessInfos.get(j).pid };
					// 根据pid获取内存信息
					Debug.MemoryInfo[] memoryInfos = 
							mActivityManager.getProcessMemoryInfo(pid);
					aib = new AppInfo();
					// 占用的内存
					aib.setNum(getNumber2((memoryInfos[0].dalvikPrivateDirty) / 1024.0)
							+ "MB");
					// 图标
					aib.setAppIcon(allPackageInfos.get(i).applicationInfo
							.loadIcon(context.getPackageManager()));
					// 名字
					aib.setAppLabel(allPackageInfos.get(i).applicationInfo
							.loadLabel(context.getPackageManager()).toString());
					// 包名
					aib.setPkgName(allPackageInfos.get(i).applicationInfo.packageName);

					// pid
					aib.setPid(
							runningAppProcessInfos.get(j).pid);
					aib.setServicen(
							getServiceNumber(aib.getPid()));
					list.add(aib);

				}
			}
		}
		return list;
	}

	/**
	 * 通过pid得到服务数量
	 * 
	 * @param pid
	 * @return
	 */

	public int getServiceNumber(int pid) {
		List<RunningServiceInfo> runningServiceInfos =
				mActivityManager.getRunningServices(1000);
		int number = 0;
		for (int i = 0; i < runningServiceInfos.size(); i++) {
			if (runningServiceInfos.get(i).pid == pid
					&& runningServiceInfos.get(i).flags
					== RunningServiceInfo.FLAG_STARTED) {
				number++;
			}
		}

		return number;
	}

	/**
	 * 得到用户应用程序信息集合
	 * 
	 * @return
	 */
	public List<AppInfo> getUserApp() {

		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo aib = null;
		List<PackageInfo> userPackageList = getUserPackageList();
		for (int i = 0; i < userPackageList.size(); i++) {
			aib = new AppInfo();
			aib.setAppIcon(userPackageList.get(i).applicationInfo
					.loadIcon(context.getPackageManager()));
			aib.setAppLabel(userPackageList.get(i).applicationInfo.loadLabel(
					context.getPackageManager()).toString());
			aib.setPkgName(userPackageList.get(i).applicationInfo.packageName);
			aib.setVersionCode(String.valueOf(userPackageList.get(i).versionCode));
			aib.setVersionName(userPackageList.get(i).versionName);
			list.add(aib);
		}
		return list;

	}
	
	/**
	 * 得到用户应用程序信息集合
	 * 
	 * @return
	 */
	public List<AppInfo> getSystemApp() {

		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo aib = null;
		List<PackageInfo> systemPackageList = getSystemPackageList();
		for (int i = 0; i < systemPackageList.size(); i++) {
			aib = new AppInfo();
//			aib.setAppIcon(systemPackageList.get(i).applicationInfo
//					.loadIcon(context.getPackageManager()));
			aib.setAppIcon(systemPackageList.get(i).applicationInfo
					.loadLogo(context.getPackageManager()));
			aib.setAppLabel(systemPackageList.get(i).applicationInfo.loadLabel(
					context.getPackageManager()).toString());
			aib.setPkgName(systemPackageList.get(i).applicationInfo.packageName);
			aib.setVersionCode(String.valueOf(allPackageInfos.get(i).versionCode));
			aib.setVersionName(systemPackageList.get(i).versionName);
			list.add(aib);
		}
		return list;

	}
	

	/**
	 * 保留两位小数
	 * 
	 * @param pDouble
	 * @return
	 */
	public double getNumber2(double pDouble) {
		try {
			BigDecimal bd = new BigDecimal(pDouble);
			BigDecimal bd1 = bd.setScale(2, bd.ROUND_HALF_UP);
			pDouble = bd1.doubleValue();
			long ll = Double.doubleToLongBits(pDouble);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return pDouble;
	}

	/**
	 * 得到总内存大小
	 * 
	 * @return
	 */

	public String getTotalMemorys() {

		String str1 = "/proc/meminfo";// 系统内存信息文件

		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader =
					new FileReader(str1);

			BufferedReader localBufferedReader =
		new BufferedReader(localFileReader, 8192);

			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");

			total_memory = Integer.valueOf(
		arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte

			localBufferedReader.close();
		} catch (IOException e) {
		}

		return Formatter.formatFileSize(context,
				total_memory);// Byte转换为KB或者MB，内存大小规格化
																// }
	}

	/**
	 * 得到已使用的内存大小
	 * 
	 * @return
	 */
	public String getEmployMemory() {
//		float employMemory = Float.parseFloat(getTotalMemorys().substring(0,
//				getTotalMemorys().length() - 2))
//				- Float.parseFloat(getSystemAvaialbeMemory().substring(0,
//						getSystemAvaialbeMemory().length() - 2));
//		return employMemory + "MB";
		return ((total_memory - memSize)/1024/1024) + "MB";
	}

	/**
	 * 得到可用内存大小
	 * 
	 * @return
	 */
	public String getSystemAvaialbeMemory() {
		// 获得MemoryInfo对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 获得系统可用内存，保存在MemoryInfo对象上
		mActivityManager.getMemoryInfo(memoryInfo);
		memSize = memoryInfo.availMem;

		// 字符类型转换
		String availMemStr = formateFileSize(memSize);

		return availMemStr;
	}

	/**
	 * 得到已用内存比例
	 * 
	 * @return
	 */
	public int getMemoryPrecent() {

		return (int) (((total_memory - memSize) / (float) total_memory) * 100);

	}

	public String getStrMemoryPrecent() {
		return (int) (((total_memory - memSize) / (float) total_memory) * 100)
				+ "%";
	}

	/**
	 * 格式化内存格式
	 * 
	 * @param size
	 * @return
	 */

	private String formateFileSize(long size) {
		return Formatter.formatFileSize(context, size);
	}

	public void getRunningservices() {
		List<RunningServiceInfo> runningAppProcessInfos = mActivityManager
				.getRunningServices(100);
		for (int i = 0; i < runningAppProcessInfos.size(); i++) {
			// Log.i("Log.i","服务包名："+runningAppProcessInfos.get(i).service.getPackageName());
			// Log.i("Log.i","服务类名："+runningAppProcessInfos.get(i).service.getClassName());
			// Log.i("Log.i","服务pid："+runningAppProcessInfos.get(i).pid);
			// Log.i("Log.i","服务uid："+runningAppProcessInfos.get(i).uid);
			// Log.i("Log.i","服务process："+runningAppProcessInfos.get(i).process);
		}

	}

	/**
	 * 杀掉所有进程（没用上）
	 */
	public boolean killAllProcess() {
		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();
		for (int i = 0; i < runningAppProcessInfos.size(); i++) {
			if (!runningAppProcessInfos.get(i).processName
					.equals("androidyi.demo")
					&& !runningAppProcessInfos.get(i).processName
							.equals("com.miui.home")) {
				mActivityManager.killBackgroundProcesses(runningAppProcessInfos
						.get(i).processName);
			}
		}

		return true;
	}

	/**
	 * 杀掉选中的进程集合（不包括自身和运营商启动器，此处的运营商启动器包名为com.miui.home）
	 */

	public boolean killProcess(HashSet<String> hs) {

		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();
		Iterator<String> it = hs.iterator();
		while (it.hasNext()) {
			String s = it.next();
			for (int i = 0; i < runningAppProcessInfos.size(); i++) {
				if (!runningAppProcessInfos.get(i).processName
						.equals("androidyi.demo")// 不能杀掉自己
						&& !runningAppProcessInfos.get(i).processName
								.equals("com.miui.home")// 不能杀掉启动器
						&& runningAppProcessInfos.get(i).processName.equals(s)) {
					mActivityManager
							.killBackgroundProcesses(runningAppProcessInfos
									.get(i).processName);
				}
			}
		}
		// Iterator<String> it=hs.iterator();
		return true;
	}

}
