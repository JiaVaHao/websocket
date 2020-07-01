package com.jwh.tiantian.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareData {

	Context context;
	SharedPreferences preferences; 
    SharedPreferences.Editor editor; 
	
	public ShareData(Context context){
		this.context= context;
		preferences = context.getSharedPreferences("11",context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	public void saveFlag(int i){
		editor.putInt("flag",i);
		editor.commit();
	}
	public void saveStatues(String statues){
		editor.putString("statues",statues);
		editor.commit();
	}
	public void saveTemp(String temp){
		editor.putString("temp",temp);
		editor.commit();
	}
	public int getFlag(){
		return preferences.getInt("flag", 0);
	}
	public String getStatues(){
		return preferences.getString("statues", "null");
	}
	public String getTemp(){
		return preferences.getString("temp", "null");
	}
	
}
