package com.jwh.yunchat.dao;

import org.litepal.LitePal;

import java.lang.reflect.Method;

public class BaseDao {

    //添加数据
    public void insert(Object obj){
        Class objClass=obj.getClass();
        try {
            Method save=objClass.getMethod("save");
            save.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //删除数据
    public void deleteById(Object obj,int id){
        LitePal.delete(obj.getClass(),id);
    }
    //更新数据
    public void update(Object obj,int id){
        Class objClass=obj.getClass();
        try {
            Method save=objClass.getMethod("update");
            save.invoke(obj,id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
