package com.jwh.dobotochatserver.websoket;

import com.alibaba.fastjson.JSON;
import com.jwh.dobotochatserver.dao.MessageDao;
import com.jwh.dobotochatserver.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chatWS/{userId}")
@Component
public class WebSocketServer {


    private static MessageDao messageDao;
    @Autowired
    public void setMessageDao(MessageDao messageDao){
        WebSocketServer.messageDao=messageDao;
    }


    private static int onlineCount = 0;
    private static ConcurrentHashMap<Integer,WebSocketServer> onlineUserMap = new ConcurrentHashMap<>();
    private Session session;
    private int userId;


    //开启链接，将链接用户注册到线程安全的WebSocketMap中
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") int userId) throws IOException {
        this.session = session;
        this.userId= userId;
        //这里说明异地登录了，所以主动发出消息强制之前的登录的session下线
        if(onlineUserMap.containsKey(userId)){
            Map jsonMap=new HashMap();
            jsonMap.put("content","offline");
            jsonMap.put("messageType",3);
            jsonMap.put("receiverNetId",userId);
            jsonMap.put("senderNetId",0);
            jsonMap.put("createTime",System.currentTimeMillis());

            String jsonStr=JSON.toJSONString(jsonMap);
            onlineUserMap.get(userId).sendMessageToReceiver(jsonStr);

            onlineUserMap.remove(userId);
            onlineUserMap.put(userId,this);
        }else{
            onlineUserMap.put(userId,this);
            addOnlineCount();
        }
        System.out.println("用户连接:"+userId+",当前在线人数为:" + onlineUserMap.size());
    }

    @OnClose
    public void onClose(Session session) {
        onlineUserMap.remove(this.userId);
        subOnlineCount();
        System.out.println(this.userId+"---退出");
    }
    @OnError
    public void onError(Throwable throwable) {

    }
    //消息传的都是json字符串,session是链接会话。
    @OnMessage
    public void onMessage(String jsonStr,Session session) {
        //解析字符串为MAP对象
        Map jsonMap=(Map) JSON.parse(jsonStr);
        int receiverId=(int)jsonMap.get("receiverNetId");
        int senderId=(int)jsonMap.get("senderNetId");
        //判断接收者是否在线
        //如果在线则这直接转发，如果不在线则存储到数据库，接收者下次链接时会请求未读信息。
        if (onlineUserMap.containsKey(receiverId)){
            onlineUserMap.get(receiverId).sendMessageToReceiver(jsonStr);
        }else {
            long createTime=(long) jsonMap.get("createTime");
            int msgType=(int) jsonMap.get("messageType");
            String content=(String) jsonMap.get("content");
            Message newMessage=new Message(senderId,receiverId,msgType,content,new Timestamp(createTime));
            messageDao.insert(newMessage);
        }
    }

    //自定义发送方法
    //发送给用户
    //保存到服务器本地以备用户查询聊天记录
    //将json数据原封不动的发送出去，让接收方再次解析出发送方是谁
    public void sendMessageToReceiver(String jsonStr){
        try {
            this.session.getBasicRemote().sendText(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
