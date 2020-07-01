package com.jwh.dobotochatserver.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    @JSONField(serialize = false)
    public final static int TEXT=1;
    @JSONField(serialize = false)
    public final static int IMAGE=2;
    @JSONField(serialize = false)
    public final static int SYSTEM=3;
    @JSONField(serialize = false)
    public final static int FILE=4;

    private int id;
    private int senderId;
    private int receiverId;
    private int msgType;
    private String content;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;
    //1为一读，0为未读
    private int isRead;

    public Message(){
        super();
    }

    public Message(int senderId,int receiverId,int msgType,String content,Timestamp createTime){
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.msgType=msgType;
        this.content=content;
        this.createTime=createTime;
        this.isRead=0;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
