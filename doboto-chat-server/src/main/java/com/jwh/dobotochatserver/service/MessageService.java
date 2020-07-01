package com.jwh.dobotochatserver.service;

import com.jwh.dobotochatserver.dao.MessageDao;
import com.jwh.dobotochatserver.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MessageService {

    @Autowired
    MessageDao messageDao;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    //获取未读消息列表
    public ArrayList<Message> getUnReadMessage(int receiverId){
        ArrayList<Message> unReadMsgList=(ArrayList<Message>) messageDao.getByReceiverId(receiverId);
        if (unReadMsgList==null||unReadMsgList.size()==0){
            return null;
        }
        for (Message message:unReadMsgList){
            messageDao.changeReadStatus(message.getId());
        }
        return unReadMsgList;
    }
}
