package com.jwh.yunchat.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.jwh.yunchat.R;
import com.jwh.yunchat.util.CheckCodeTimeDown;
import com.jwh.yunchat.util.DMD5Encrypt;
import com.jwh.yunchat.util.OkHttpUtil;
import com.jwh.yunchat.util.RegexUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

public class RegisterActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{

    private ImageView goToLogin;
    private EditText newUsernameEdt;
    private EditText newPasswordEdt;
    private EditText newPassword2Edt;
    private Button getCheckCode;
    private Button registerBtn;
    private EditText checkCodeEdt;
    private String username;

    private String[] permissions={
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //全屏
        setFullScreen(RegisterActivity.this);
        //绑定页面元素
        newUsernameEdt=findViewById(R.id.new_username);
        newPasswordEdt=findViewById(R.id.new_password);
        newPassword2Edt=findViewById(R.id.new_password2);
        getCheckCode=findViewById(R.id.get_check_ode);
        checkCodeEdt=findViewById(R.id.check_code);
        registerBtn=findViewById(R.id.register);
        //跳转注册界面
        goToLogin=findViewById(R.id.go_to_login);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //发生验证码请求,这里为异步形式
        getCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=newUsernameEdt.getText().toString();
                /*if (RegexUtil.isMobile(username)){
                    getCheckCode(username);
                    CheckCodeTimeDown countDown = new CheckCodeTimeDown(getCheckCode, 60000, 1000); //倒计时1分钟
                    countDown.start();
                }else{
                    Toast.makeText(RegisterActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        //注册
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername=newUsernameEdt.getText().toString();
                String newPassword1=newPasswordEdt.getText().toString();
                String newPassword2=newPassword2Edt.getText().toString();
                String checkCode=checkCodeEdt.getText().toString();
                if (newUsername.equals("")){
                    Toast.makeText(RegisterActivity.this,"手机号码格式错误",Toast.LENGTH_SHORT).show();
                }else if (newPassword1.equals("") || newPassword2.equals("") || !newPassword1.equals(newPassword2)){
                    Toast.makeText(RegisterActivity.this,"密码为空，或者两次密码不一致",Toast.LENGTH_SHORT).show();
                }else if (checkCode.equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else {
                    Map params=new HashMap();
                    String password = DMD5Encrypt.MD5Encrypt(newPassword1);
                    params.put("newUsername",newUsername);
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
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "短信已经发送，请注意查收", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    String url = SERVER_URL + "/user/register";
                    String jsonStr=OkHttpUtil.synPost(url, paramMap);
                    registerResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void registerResponse(final String jsonStr){
        RegisterActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Intent intent = new Intent(RegisterActivity.this, CompleteUserInfoActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //替换fragment
    public void openFragment(Fragment fragment, int viewId){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(viewId,fragment);
        transaction.commit();
    }

    //检查权限
    private void getPermission(){
        if (!EasyPermissions.hasPermissions(this,permissions)){
            EasyPermissions.requestPermissions(this,"需要获取您的相册、照相使用权限",1,permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this,"权限获取成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this,"请同意相关权限，否则无法正常使用",Toast.LENGTH_SHORT).show();
    }

}
