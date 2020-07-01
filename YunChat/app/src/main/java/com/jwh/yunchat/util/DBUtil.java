package com.jwh.yunchat.util;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DBUtil {
    public static ArrayList cursorToList(Cursor cursor,Class objClass) {
        try {
            ArrayList objList=new ArrayList();
            //获取全部字段名
            Field[] fields=objClass.getDeclaredFields();
            //通过fieldName获取指定数据
            while (cursor.moveToNext()){
                Object newObj=objClass.newInstance();
                for (Field field : fields){
                    field.setAccessible(true);
                    String colName=field.getName().toLowerCase();
                    Class fileType=field.getType();
                    int valueIndex=cursor.getColumnIndex(colName);
                    Object value=null;
                    //根据字段类型判断调用哪个方法/String/Int/Long/boolean
                    switch (fileType.getSimpleName()){
                        case "String":
                            value=cursor.getString(valueIndex);
                            break;
                        case "int":
                            value=cursor.getInt(valueIndex);
                            break;
                        case "long":
                            value=cursor.getLong(valueIndex);
                            break;
                        case "boolean":
                            int dbVal=cursor.getInt(valueIndex);
                            value=true;
                            if (dbVal == 0){
                                value=false;
                            }
                    }
                    field.set(newObj,value);
                }
                objList.add(newObj);
            }
            return objList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
