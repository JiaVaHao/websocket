package com.jwh.yunchat.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    private static OkHttpClient client = new OkHttpClient();
    //同步GET请求
    //需要在调用的activity或者fragment中添加响应的回调处理方法runOnUiThread(new Runable())，同步的方法统一返回的是响应内容的字符串形式
    //如果有参数，参数加载在url中，就是先拼接好URL
    public static String synGet(String url){
        try{
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            Log.d("ok http","ok http is error");
            e.printStackTrace();
            return "error";
        }
    }
    //同步POST请求
    //参数以map形式传入，通过fastjson进行解析成json字符串形式，加载到请求body中
    public static String synPost(String url, Map params){
        try{

            String jsonStr=JSON.toJSONString(params);
            RequestBody requestBody=new FormBody.Builder()
                    .add("jsonStr",jsonStr)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            Log.d("ok http","ok http is error");
            e.printStackTrace();
            return "error";
        }
    }
    //异步GET请求
    //对于异步请求就不需要返回值，因为会提供一个自定义处理方法，数据交给这个方法处理即可
    public static void asyGet(String url, Callback callback){
        try{
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(callback);
        }catch (Exception e){
            Log.d("ok http","ok http is error");
            e.printStackTrace();
        }
    }
    //异步POST请求
    public static void asyPost(String url, Map params,Callback callback){
        try{
            String jsonStr=JSON.toJSONString(params);
            RequestBody requestBody=new FormBody.Builder()
                    .add("jsonStr",jsonStr)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(callback);
        }catch (Exception e){
            Log.d("ok http","ok http is error");
            e.printStackTrace();
        }
    }
    //文件上传,同步类型
    public static String fileUpload(String url, File file,Map params){
        try{
            String jsonStr=JSON.toJSONString(params);
            RequestBody fileBody=RequestBody.create(file,MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"));
            MultipartBody multipartBody=new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("jsonStr",jsonStr)
                    .addFormDataPart("file",file.getName(),fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(multipartBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            Log.d("ok http","ok http is error");
            e.printStackTrace();
            return "error";
        }
    }
//    返回主线程并执行更改UI操作
//    private void showResponse(final String string) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //这里进行UI操作
//                mTv_responseText.setText(string);
//            }
//        });
//    }
}
