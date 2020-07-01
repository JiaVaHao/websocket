package com.jwh.yunchat.entity;

import org.litepal.crud.LitePalSupport;

public class Friend extends LitePalSupport {

    private String name;
    private String imageUrl;
    private int id;
    private String username;
    //网络id
    private int netId;
    private int ownerNetId;
    //isChat表示聊天列表有没有这个好友的列表
    private Boolean isChat;
    //用于好友添加
    private Boolean isFriend;

    public Friend(){
        super();
    }

    //用于网络搜索的
    public Friend(int netId,String name,String url){
        this.name=name;
        this.netId=netId;
        this.imageUrl=url;
        this.isChat=false;
        this.isFriend=false;
    }
    //用于本地操作的
    public Friend(int netId,String username,String name,String url,int ownerNetId){
        this.name=name;
        this.netId=netId;
        this.imageUrl=url;
        this.ownerNetId=ownerNetId;
        this.username=username;
        this.isChat=false;
        this.isFriend=true;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChat() {
        return isChat;
    }

    public void setChat(Boolean chat) {
        isChat = chat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }

    public int getOwnerNetId() {
        return ownerNetId;
    }

    public void setOwnerNetId(int ownerNetId) {
        this.ownerNetId = ownerNetId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }
}
