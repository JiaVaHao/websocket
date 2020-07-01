package com.jwh.yunchat.entity;

//Chat 相当于是Message和Friend 的封装类<一个朋友+最后一条信息>，专门用于列表渲染的
public class Chat {

    private int friendId;
    private String friendName;
    private String friendImage;
    private String lastMessage;
    private boolean isRead;
    private String time;

    public Chat(int friendNetId,String friendName,String friendImage,String lastMessage,boolean isRead,String time){
       this.friendId=friendNetId;
       this.friendImage=friendImage;
       this.friendName=friendName;
       this.isRead=isRead;
       this.lastMessage=lastMessage;
       this.time=time;
    }


    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(String friendImage) {
        this.friendImage = friendImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
