package com.jwh.yunchat.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jwh.yunchat.dao.MessageDao;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.service.OwnerService;
import com.jwh.yunchat.util.OkHttpUtil;

import org.litepal.LitePal;

import java.sql.Timestamp;
import java.util.Map;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //检查本地是否已有登录用户，则跳过登录直接进入聊天界面，否则跳转到登录界面
        //获取SharedPreferences持久化组件
        pref= MainActivity.this.getSharedPreferences("localInfo",MODE_PRIVATE);
        prefEditor=pref.edit();
        //获取数据库
        SQLiteDatabase db = LitePal.getDatabase();
        //数据库调试器初始化
        SQLiteStudioService.instance().start(this);

        int ownerNetId=pref.getInt("ownerNetId",-1);
        if (ownerNetId==-1){
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            OwnerService ownerService=new OwnerService();
            //将查询出来的owner赋值给baseActivity中的全局owner
            owner=ownerService.findByNetId(ownerNetId);

            //初始化WS链接
            initWebSocket(owner.getNetId());

            getUnReadMsg(owner.getUsername());
            this.finish();
        }
    }


    //登录成功后获取未读消息
    private void getUnReadMsg(final String username){
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
                        Message newMessage=new Message(senderId,receiverId,content,messageType, Timestamp.valueOf(createTime).getTime());
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
                        String url="E:\\websocketchat\\android\\doboto-chat-web-external\\UserHeading\\"+netFriend.getString("imagePath");
                        String username=netFriend.getString("username");
                        Friend friend=new Friend(netId,username,name,url,ownerNetId);
                        friend.save();
                    }
                }
                //跳转到chatListActivity
                Intent intent=new Intent(MainActivity.this,ChatListActivity.class);
                startActivity(intent);
            }
        });
    }
}
