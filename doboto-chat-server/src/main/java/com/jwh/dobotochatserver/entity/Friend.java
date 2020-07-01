package com.jwh.dobotochatserver.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Friend implements Serializable {

    @JSONField(serialize = false)
    public final static int ACCEPT=1;
    @JSONField(serialize = false)
    public final static int REJECT=0;
    @JSONField(serialize = false)
    public final static int IS_NEW=2;
    @JSONField(serialize = false)
    public final static int IS_NOT_NEW=3;

    private int id;
    private int ownerId;
    private int friendId;
    //1-接受，0-拒绝
    private int isAccept;
    //2-是新好友，3-不是新好友,添加时候默认是新
    private int isNew;

    public Friend(){
        super();
    }

    public Friend(int ownerId,int friendId,int isAccept,int isNew){
        this.friendId=friendId;
        this.ownerId=ownerId;
        this.isAccept=isAccept;
        this.isNew=isNew;
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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int isAccept() {
        return isAccept;
    }

    public void setAccept(int accept) {
        isAccept = accept;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
