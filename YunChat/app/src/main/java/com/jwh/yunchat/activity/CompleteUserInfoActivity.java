package com.jwh.yunchat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.jwh.yunchat.R;
import com.jwh.yunchat.util.OkHttpUtil;
import com.jwh.yunchat.util.PictureUtil;
import com.jwh.yunchat.util.RegexUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CompleteUserInfoActivity extends BaseActivity {

    private ImageView returnBtn;
    private ImageView headingV;
    private EditText nameEdt;
    private EditText ageEdt;
    private EditText cityEdt;
    private Button completeInfoBtn;
    private String username;
    private PictureUtil pictureUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_info);
        //全屏
        setFullScreen(CompleteUserInfoActivity.this);
        //绑定页面元素
        returnBtn = findViewById(R.id.back_register);
        returnBtn.setVisibility(View.GONE);
        headingV = findViewById(R.id.new_user_heading);
        nameEdt = findViewById(R.id.new_user_name);
        ageEdt = findViewById(R.id.new_user_age);
        cityEdt = findViewById(R.id.new_user_city);
        username = getIntent().getStringExtra("username");
        completeInfoBtn = findViewById(R.id.complete_user_info);

        pictureUtil = new PictureUtil(CompleteUserInfoActivity.this);

        //回退
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompleteUserInfoActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //更换图片
        headingV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] getPictureWays = new String[]{"相册", "相机"};
                AlertDialog dialog = new AlertDialog.Builder(CompleteUserInfoActivity.this)
                        .setItems(getPictureWays, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                switch (i) {
                                    case 0:
                                        pictureUtil.openPictureLib();
                                        break;
                                    case 1:
                                        pictureUtil.openCamera();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create();
                dialog.show();
            }
        });
        //发送
        completeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEdt.getText().toString();
                String age=ageEdt.getText().toString();
                String city=cityEdt.getText().toString();
                File file=pictureUtil.getPictureFile();
                if (name.equals("") || age.equals("") || !RegexUtil.isNumber(age) || city.equals("") || file==null){
                    Toast.makeText(CompleteUserInfoActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                }else{
                    Map params=new HashMap();
                    params.put("name",name);
                    params.put("age",age);
                    params.put("city",city);
                    completeUserInfoRequest(params,file);
                }
            }
        });

    }

    private void completeUserInfoRequest(final Map paramMap,final File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SERVER_URL+"/user/"+username+"/completeUserInfo";
                    String jsonStr=OkHttpUtil.fileUpload(url,file,paramMap);;
                    completeUserInfoResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void completeUserInfoResponse(final String jsonStr){
        CompleteUserInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Toast.makeText(CompleteUserInfoActivity.this, "完成注册，请重新登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CompleteUserInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CompleteUserInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pictureUtil.showPicture(requestCode,resultCode,data,CompleteUserInfoActivity.this,headingV);
    }

}
