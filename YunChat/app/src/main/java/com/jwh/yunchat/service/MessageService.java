package com.jwh.yunchat.service;

import com.jwh.yunchat.dao.MessageDao;
import com.jwh.yunchat.entity.Message;

import java.util.ArrayList;

public class MessageService {

    private MessageDao messageDao;

    public MessageService(){
        messageDao=new MessageDao();
    }

    public ArrayList<Message> findNearlyMessage(int friendNetId,int ownerNetId){
        return messageDao.findNearlyMessage(friendNetId,ownerNetId);
    }

    public void delete(int friendNetId,int ownerNetId){
        messageDao.delete(friendNetId,ownerNetId);
    }
}
