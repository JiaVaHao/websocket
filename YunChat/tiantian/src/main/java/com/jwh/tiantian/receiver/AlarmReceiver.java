package com.jwh.tiantian.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.jwh.tiantian.activity.alarm.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {
	//定义广播，重写onReceive方法
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AlarmActivity.class);
		Bundle bundleRet = new Bundle();
		bundleRet.putString("STR_CALLER", "");
		i.putExtras(bundleRet);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
