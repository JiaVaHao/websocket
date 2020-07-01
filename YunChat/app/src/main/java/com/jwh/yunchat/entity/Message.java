package com.jwh.yunchat.entity;

import com.jwh.yunchat.service.WebSocketClient;

import org.litepal.crud.LitePalSupport;

public class Message extends LitePalSupport {

    private int id;

    private int senderNetId;

    private int receiverNetId;

    private String content;

    //消息类型，1为普通文本消息，2为图像消息，3为系统推送消息
    private int messageType;

    private boolean isRead;

    private long createTime;


    public Message(){
        super();
    }

    public Message(int senderId,int receiverId,String content){
        this.content=content;
        this.senderNetId=senderId;
        this.receiverNetId=receiverId;
        this.messageType= WebSocketClient.MSG_TEXT;
        this.isRead=false;
        this.createTime=System.currentTimeMillis();
    }

    public Message(int senderId,int receiverId,String content,int messageType){
        this.content=content;
        this.senderNetId=senderId;
        this.receiverNetId=receiverId;
        this.messageType=messageType;
        this.isRead=false;
        this.createTime=System.currentTimeMillis();
    }
    public Message(int senderId,int receiverId,String content,int messageType,long createTime){
        this.content=content;
        this.senderNetId=senderId;
        this.receiverNetId=receiverId;
        this.messageType=messageType;
        this.isRead=false;
        this.createTime=createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }


    public int getSenderNetId() {
        return senderNetId;
    }

    public void setSenderNetId(int senderNetId) {
        this.senderNetId = senderNetId;
    }

    public int getReceiverNetId() {
        return receiverNetId;
    }

    public void setReceiverNetId(int receiverNetId) {
        this.receiverNetId = receiverNetId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
