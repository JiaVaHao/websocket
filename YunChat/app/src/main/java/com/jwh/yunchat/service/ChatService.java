package com.jwh.yunchat.service;

import com.jwh.yunchat.dao.FriendDao;
import com.jwh.yunchat.dao.MessageDao;
import com.jwh.yunchat.entity.Chat;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;

public class ChatService {

    private FriendDao friendDao;
    private MessageDao messageDao;

    public ChatService(){
        this.friendDao=new FriendDao();
        this.messageDao=new MessageDao();
    }

    //接收消息后进行发送者的判断，如果该消息不是本地同步的好友发送的，则先将消息保存，然后发送好友同步请求，然后再进行聊天列表的展示
    //这里需要先进行未读消息的朋友判断，改变isChat状态，然后再进行展示
    public ArrayList<Chat> findChatList(int ownerNetId){

        ArrayList<Friend> isChatFriends=friendDao.findChatFriendList(ownerNetId);

        ArrayList<Message> unReadMsgs=messageDao.findUnReadMessage(ownerNetId);
        if (unReadMsgs!=null){
            for (Message message:unReadMsgs){
                Friend friend=friendDao.findByNetId(message.getSenderNetId(),ownerNetId);
                if (friend!=null && friend.getChat()==false){
                    friend.setChat(true);
                    friend.update(friend.getId());
                    isChatFriends.add(friend);
                }
            }
        }

        ArrayList<Chat> chats=new ArrayList<>();
        if (isChatFriends.size()==0){
            return null;
        }
        for (Friend friend : isChatFriends){
            Message message=messageDao.findLastMessage(friend.getNetId(),ownerNetId);
            Chat chat;
            if (message == null){
                chat=new Chat(friend.getNetId(),friend.getName(),friend.getImageUrl(),"",true,"");
            }else{
                String time= TimeUtil.dateToString(new Date(message.getCreateTime()));
                chat=new Chat(friend.getNetId(),friend.getName(),friend.getImageUrl(),message.getContent(),message.getRead(),time);
            }
            chats.add(chat);
        }

        return chats;
    }
}
