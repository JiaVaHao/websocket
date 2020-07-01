package com.jwh.yunchat.service;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.activity.ChatActivity;
import com.jwh.yunchat.activity.ChatListActivity;
import com.jwh.yunchat.controller.ActivityController;
import com.jwh.yunchat.dao.FriendDao;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.util.OkHttpUtil;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private Activity currentActivity;
    private ChatActivity chatActivity;
    private ChatListActivity chatListActivity;
    private FriendDao friendDao;

    public static final int MSG_TEXT=1;
    public static final int MSG_IMAGE=2;
    public static final int MSG_SYSTEM=3;

    public WebSocketClient(URI serverURI){
        super(serverURI,new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WS","链接开启");
    }

    @Override
    public void onMessage(String message){
        try {
            friendDao=new FriendDao();
            Log.d("WS",message);
            //提取消息
            JSONObject unReadMsg= JSON.parseObject(message);
            String content=(String) unReadMsg.get("content");
            int messageType=(int)unReadMsg.get("messageType");
            int receiverId=(int)unReadMsg.get("receiverNetId");
            int senderId=(int)unReadMsg.get("senderNetId");
            long createTime=(long) unReadMsg.get("createTime");
            Message newMessage=new Message(senderId,receiverId,content,messageType, createTime);
            //获取当前活动对象
            currentActivity=ActivityController.getCurrentActivity();
            //判断消息发送者是否为本地好友，如果不是本地好友，需要进行好友同步,0表示是系统发送的消息
            if (senderId!=0){
                Friend friend=friendDao.findByNetId(senderId,receiverId);
                if (friend==null){
                    getNewFriends(senderId,receiverId);
                }
            }

            //根据消息类型进行判断
            switch (messageType){
                case MSG_TEXT:
                    String currentActivityName=currentActivity.getLocalClassName();
                    //如果当前活动为聊天活动则进行数据展示
                    if (currentActivityName.equals("activity.ChatActivity")){
                        updateChatUi(newMessage);
                        //如果当前活动为聊天列表界面，需要对指定的元素进行局部更新，如果没有该用户在聊天列表中，需要添加该好友项
                    }else if (currentActivityName.equals("activity.ChatListActivity")){
                        newMessage.save();
                        updateChatListUi();
                    }else{
                        newMessage.save();
                        notification();
                    }
                    break;
                case MSG_IMAGE:
                    break;
                case MSG_SYSTEM:
                    if (content.equals("offline")){
                        offline();
                    }
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void offline(){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent("FORCE_OFFLINE");
                intent.putExtra("warringMsg","您的账号异地登录，您将被强制下线");
                currentActivity.sendBroadcast(intent);
            }
        });
    }

    public void updateChatUi(final Message newMessage){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatActivity=(ChatActivity) currentActivity;
                //如果当前正在聊天的人为消息的发送者，则进行展示,并存为已读消息
                if (chatActivity.friendNetId==newMessage.getSenderNetId()){
                    chatActivity.addMsgNotSave(newMessage);
                }
                //如果当前正在聊天的人不是消息发送者，则更新一个未读标记
                else {
                    chatActivity.hasNewMsgView.setVisibility(View.VISIBLE);
                    newMessage.setRead(false);
                    newMessage.save();
                }
            }
        });
    }

    public void updateChatListUi(){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatListActivity=(ChatListActivity) currentActivity;
                chatListActivity.updateUi();
            }
        });
    }
    public void notification(){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(currentActivity,"您有新的消息，请查收",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WS","链接关闭");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    //好友数据同步
    private void getNewFriends(final int friendNetId,final int ownerNetId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = BaseActivity.SERVER_URL + "/user/getNewFriendById/"+ownerNetId+"/"+friendNetId;
                    String jsonStr= OkHttpUtil.synGet(url);
                    getNewFriendsResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getNewFriendsResponse(final String jsonStr){
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    JSONObject newFriend=(JSONObject) jsonMap.get("data");
                    int netId=newFriend.getIntValue("id");
                    int ownerNetId=BaseActivity.owner.getNetId();
                    String name=newFriend.getString("name");
                    String url="E:\\websocketchat\\android\\doboto-chat-web-external\\UserHeading\\"+newFriend.getString("imagePath");
                    String username=newFriend.getString("username");
                    Friend friend=new Friend(netId,username,name,url,ownerNetId);
                    friend.save();
                    Toast.makeText(currentActivity,"你有新的好友与消息",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
