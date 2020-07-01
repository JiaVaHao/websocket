package com.jwh.yunchat.controller;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

//管理所有的Activity
public class ActivityController {

    public static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        activity.finish();
    }

    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing() && !activity.getLocalClassName().equals("activity.LoginActivity")){
                activity.finish();
            }
        }
    }

    public static Activity getCurrentActivity(){
        return activities.get(activities.size()-1);
    }
}
