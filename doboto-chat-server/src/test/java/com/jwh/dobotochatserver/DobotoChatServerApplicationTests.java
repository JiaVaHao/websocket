package com.jwh.dobotochatserver;

import com.alibaba.fastjson.JSON;
import com.jwh.dobotochatserver.dao.MessageDao;
import com.jwh.dobotochatserver.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.Map;

@SpringBootTest
class DobotoChatServerApplicationTests {

    @Autowired
    MessageDao messageDao;
    @Test
    void test() {
        String jsonStr="{\"content\":\"123123123123123\",\"createTime\":1588584649062,\"id\":28,\"messageType\":1,\"read\":false,\"receiverNetId\":1562,\"saved\":true,\"senderNetId\":1561}";
        Map jsonMap=(Map) JSON.parse(jsonStr);
        int receiverId=(int)jsonMap.get("receiverNetId");
        int senderId=(int)jsonMap.get("senderNetId");
        long createTime=(long) jsonMap.get("createTime");
        int msgType=(int) jsonMap.get("messageType");
        String content=(String) jsonMap.get("content");
        Message newMessage=new Message(senderId,receiverId,msgType,content,new Timestamp(createTime));
        messageDao.insert(newMessage);
    }

}
