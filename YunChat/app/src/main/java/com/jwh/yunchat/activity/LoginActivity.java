package com.jwh.yunchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jwh.yunchat.R;
import com.jwh.yunchat.dao.FriendDao;
import com.jwh.yunchat.dao.MessageDao;
import com.jwh.yunchat.dao.OwnerDao;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.util.DMD5Encrypt;
import com.jwh.yunchat.util.OkHttpUtil;
import com.jwh.yunchat.util.RegexUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity{

    private OwnerDao ownerDao;

    //存储界面元素
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private TextView goToRegisterBtn;
    private TextView goToForgetPWBtn;
    private CheckBox rememberPSW;

    private static String username;
    private static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //全屏
        setFullScreen(this);
        //获取页面元素
        usernameEdit=findViewById(R.id.username);
        passwordEdit=findViewById(R.id.password);
        loginBtn=findViewById(R.id.login);
        goToRegisterBtn=findViewById(R.id.go_to_register);
        rememberPSW=findViewById(R.id.remember_psw);
        goToForgetPWBtn=findViewById(R.id.go_to_forgetPW);

        //判断是否已经记住密码
        boolean isRememberPSW=pref.getBoolean("remember_password",false);
        //如果已经记住则将存储的信息绑定到文本框中
        if (isRememberPSW){
            String username=pref.getString("username","");
            String password=pref.getString("password","");
            usernameEdit.setText(username);
            passwordEdit.setText(password);
            rememberPSW.setChecked(true);
        }
        //登录事件
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=usernameEdit.getText().toString();
                password=passwordEdit.getText().toString();
                //先从本地数据库中查询用户是否存在，如果存在说明不是首次本地登录，则不发送请求
                //先发送登录请求，服务器端返回登录信息，如果登录成功则查询本地，如果本地用户存在，则不发送请求朋友列表，如果本地没有用户，则发送查询朋友列表
                //将朋友存入本地服务器，之后的朋友添加再发送相应的请求
                //发送网络请求
                if (!username.equals("") && !password.equals("")){

                    Map paramMap=new HashMap();
                    paramMap.put("username",username);
                    paramMap.put("password", DMD5Encrypt.MD5Encrypt(password));
                    loginRequest(paramMap);

                } else{
                    Toast.makeText(LoginActivity.this,"用户名或密码格式错误",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //跳转注册界面
        goToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //跳转忘记密码活动
        goToForgetPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/login";
                    String jsonStr= OkHttpUtil.synPost(url, paramMap);
                    loginResponse(jsonStr,(String) paramMap.get("username"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loginResponse(final String jsonStr,final String username){
        runOnUiThread(new Runnable() {
            private OwnerDao ownerDao=new OwnerDao();
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Owner dbOwner=ownerDao.findOwnerByUsername(username);
                    if (dbOwner!=null){
                        owner=dbOwner;
                        ownerInfoSaveToPref();
                        //初始化WS链接
                        initWebSocket(owner.getNetId());
                        getUnReadMsg();
                    }else{
                        //如果本地为空则说明是首次登录，则需要发送请求获取用户的个人信息及朋友列表数据存入本地数据库中，再进行存储
                        firstLoginRequest();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //记住密码和记录免密登录
    private void ownerInfoSaveToPref(){
        prefEditor.putInt("ownerNetId",owner.getNetId());
        //检查记住密码是否勾选,如果被选择则通过 editor进行保存
        if (rememberPSW.isChecked()){
            prefEditor.putBoolean("remember_password",true);
            prefEditor.putString("username",username);
            prefEditor.putString("password",password);
        }else{
            prefEditor.clear();
        }
        prefEditor.commit();
    }
    //首次登录发送请求数据信息
    private void firstLoginRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/firstLogin/"+username;
                    String jsonStr= OkHttpUtil.synGet(url);
                    firstLoginResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void firstLoginResponse(final String jsonStr){
        runOnUiThread(new Runnable() {
            private OwnerDao ownerDao=new OwnerDao();
            private FriendDao friendDao=new FriendDao();
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    //进行本地数据存储,本地数据库不存储密码，防止泄露，需要补全imageURL路径
                    JSONObject data=(JSONObject) jsonMap.get("data");
                    JSONObject netUser=(JSONObject) data.get("owner");
                    JSONArray netFriends=(JSONArray) data.get("friends");
                    //
                    Owner newOwner = new Owner();
                    newOwner.setUsername(netUser.getString("username"));
                    newOwner.setPassword("password");
                    newOwner.setNetId(netUser.getInteger("id"));
                    newOwner.setName(netUser.getString("name"));
                    newOwner.setImageURL(netUser.getString("imagePath"));
                    System.out.println(netUser.getString("imagePath"));
                    ownerDao.insert(newOwner);
                    //
                    if (netFriends!=null && netFriends.size()>0){
                        for (int i=0;i<netFriends.size();i++){
                            JSONObject netFriend=(JSONObject) netFriends.get(i);
                            String name=netFriend.getString("name");
                            String imageUrl=netFriend.getString("imagePath");
                            String username=netFriend.getString("username");
                            int netId=netFriend.getInteger("id");
                            Friend newFriend=new Friend(netId,username,name,imageUrl,newOwner.getNetId());
                            friendDao.insert(newFriend);
                        }
                    }
                    owner=newOwner;
                    ownerInfoSaveToPref();
                    //初始化WS链接
                    initWebSocket(owner.getNetId());
                    getUnReadMsg();
                }else{
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //登录成功后获取未读消息
    private void getUnReadMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/getUnReadMsg/"+username;
                    String jsonStr= OkHttpUtil.synGet(url);
                    getUnReadMsgResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getUnReadMsgResponse(final String jsonStr){
        runOnUiThread(new Runnable() {
            private MessageDao messageDao=new MessageDao();
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    //进行本地消息存储
                    JSONArray data=(JSONArray) jsonMap.get("data");
                    for (int i=0;i<data.size();i++){
                        JSONObject unReadMsg=(JSONObject) data.get(i);
                        String content=(String) unReadMsg.get("content");
                        int messageType=(int)unReadMsg.get("msgType");
                        int receiverId=(int)unReadMsg.get("receiverId");
                        int senderId=(int)unReadMsg.get("senderId");
                        String createTime=(String) unReadMsg.get("createTime");
                        Message newMessage=new Message(senderId,receiverId,content,messageType,Timestamp.valueOf(createTime).getTime());
                        messageDao.insert(newMessage);
                    }
                }
               getNewFriends(owner.getUsername());
            }
        });
    }
    //登录成功后进行好友数据同步
    private void getNewFriends(final String username){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/getNewFriends/"+username;
                    String jsonStr= OkHttpUtil.synGet(url);
                    getNewFriendsResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getNewFriendsResponse(final String jsonStr){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    JSONArray friends=(JSONArray) jsonMap.get("data");
                    for (int i=0;i<friends.size();i++){
                        JSONObject netFriend=(JSONObject) friends.get(i);
                        int netId=netFriend.getIntValue("id");
                        int ownerNetId=owner.getNetId();
                        String name=netFriend.getString("name");
                        String url=netFriend.getString("imagePath");
                        String username=netFriend.getString("username");
                        Friend friend=new Friend(netId,username,name,url,ownerNetId);
                        friend.save();
                    }
                }

                //跳转到chatListActivity
                Intent intent=new Intent(LoginActivity.this,ChatListActivity.class);
                startActivity(intent);
            }
        });
    }
}
