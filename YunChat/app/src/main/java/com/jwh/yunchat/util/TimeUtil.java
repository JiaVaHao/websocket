package com.jwh.yunchat.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String pattern="yyyy-MM-dd HH:mm";

    public static String timeToString(Timestamp timestamp){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.format(timestamp);
    }

    public static Timestamp stringToTime(String string){
        return Timestamp.valueOf(string);
    }

    public static Date stringToDate(String string) throws ParseException {
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.parse(string);
    }

    public static String dateToString(Date date){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
