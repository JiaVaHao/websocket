package com.jwh.yunchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jwh.yunchat.R;
import com.jwh.yunchat.util.CheckCodeTimeDown;
import com.jwh.yunchat.util.DMD5Encrypt;
import com.jwh.yunchat.util.OkHttpUtil;
import com.jwh.yunchat.util.RegexUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgetPasswordActivity extends BaseActivity {

    private ImageView goToLogin;
    private EditText usernameEdt;
    private EditText newPasswordEdt;
    private EditText newPassword2Edt;
    private Button getCheckCode;
    private Button sendPWBtn;
    private EditText checkCodeEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        //全屏
        setFullScreen(this);
        //绑定页面元素
        usernameEdt=findViewById(R.id.f_username);
        newPasswordEdt=findViewById(R.id.f_new_password);
        newPassword2Edt=findViewById(R.id.f_new_password2);
        getCheckCode=findViewById(R.id.f_get_check_ode);
        checkCodeEdt=findViewById(R.id.f_check_code);
        sendPWBtn=findViewById(R.id.send_password);
        //跳转注册界面
        goToLogin=findViewById(R.id.go_to_login);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //发生验证码请求,这里为异步形式
        getCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameEdt.getText().toString();
                if (RegexUtil.isMobile(username)){
                    getCheckCode(username);
                    CheckCodeTimeDown countDown = new CheckCodeTimeDown(getCheckCode, 60000, 1000); //倒计时1分钟
                    countDown.start();
                }else{
                    Toast.makeText(ForgetPasswordActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册
        sendPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameEdt.getText().toString();
                String newPassword1=newPasswordEdt.getText().toString();
                String newPassword2=newPassword2Edt.getText().toString();
                String checkCode=checkCodeEdt.getText().toString();
                if (username.equals("") || !RegexUtil.isMobile(username)){
                    Toast.makeText(ForgetPasswordActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }else if (newPassword1.equals("") || newPassword2.equals("") || !newPassword1.equals(newPassword2)){
                    Toast.makeText(ForgetPasswordActivity.this,"密码为空，或者两次密码不一致",Toast.LENGTH_SHORT).show();
                }else if (checkCode.equals("")){
                    Toast.makeText(ForgetPasswordActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else {
                    Map params=new HashMap();
                    String password = DMD5Encrypt.MD5Encrypt(newPassword1);
                    params.put("username",username);
                    params.put("newPassword",password);
                    params.put("checkCode",checkCode);
                    sendRegisterRequest(params);
                }
            }
        });
    }

    private void getCheckCode(final String username){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url=SERVER_URL+"/user/"+username+"/sendCheckCode";
                    OkHttpUtil.asyGet(url, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String jsonStr=response.body().string();
                            Map jsonMap= JSON.parseObject(jsonStr);
                            final String message=(String) jsonMap.get("message");
                            if (message.equals("success")){
                                ForgetPasswordActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, "短信已经发送，请注意查收", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                ForgetPasswordActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendRegisterRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SERVER_URL + "/user/forgetPW";
                    String jsonStr=OkHttpUtil.synPost(url, paramMap);
                    registerResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void registerResponse(final String jsonStr){
        ForgetPasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Toast.makeText(ForgetPasswordActivity.this, "修改成功，请返回登录", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
