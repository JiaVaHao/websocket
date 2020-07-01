package com.jwh.yunchat.entity;

import java.util.ArrayList;

public class MessageItemView {

    public Friend friend;
    public Owner owner;
    public ArrayList<Message> messages;

    public MessageItemView(Friend friend,Owner owner,ArrayList<Message> messages){
        this.friend=friend;
        this.owner=owner;
        this.messages=messages;
    }

}
