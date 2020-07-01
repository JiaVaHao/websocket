package com.jwh.yunchat.service;

import com.jwh.yunchat.dao.FriendDao;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;

import org.litepal.LitePal;

import java.util.ArrayList;

public class FriendService {

    public static final boolean IS_CHAT=true;
    public static final boolean IS_NOT_CHAT=false;

    private FriendDao friendDao;

    public FriendService(){
        this.friendDao=new FriendDao();
    }

    public ArrayList<Friend> findAll(int ownerNetId){
        return friendDao.findFriendListByOwnerNetId(ownerNetId);
    }

    public ArrayList<Friend> findChatFriendList(int ownerNetId){
        return friendDao.findChatFriendList(ownerNetId);
    }
    public Friend findByNetId(int netId,int ownerNetId){
        return friendDao.findByNetId(netId,ownerNetId);
    }
    public void changeChatStatus(int netId,int ownerNetId,boolean isChatStatus){
        Friend friend=new Friend();
        friend.setChat(isChatStatus);
        friendDao.changeChatStatus(friend,netId,ownerNetId);
    }
    public void delete(int friendNetId,int ownerNetId){

        LitePal.deleteAll(Friend.class,"netId = ? and ownerNetId =?",String.valueOf(friendNetId),String.valueOf(ownerNetId));
        LitePal.deleteAll(Message.class,"senderNetId = ? and receiverNetId =?",String.valueOf(friendNetId),String.valueOf(ownerNetId));
        LitePal.deleteAll(Message.class,"senderNetId = ? and receiverNetId =?",String.valueOf(ownerNetId),String.valueOf(friendNetId));
    }

    public ArrayList<Friend> findLocalByKeyword(String keyword,int ownerNetId) {
        return friendDao.findByKeyword(keyword,ownerNetId);
    }
    //通过网络查询好友
    public ArrayList<Friend> findNetByKeyword(String keyword) {
        Friend friend1=new Friend(2001,"网络用户1","http://pic1.zhimg.com/50/v2-60ae41c1784d5588e7ee75addd5a8ff4_hd.jpg");
        Friend friend2=new Friend(2002,"网络用户2","http://i2.hdslb.com/bfs/face/d79637d472c90f45b2476871a3e63898240a47e3.jpg");
        Friend friend3=new Friend(2003,"你的名字","http://ztd00.photos.bdimg.com/ztd/w=700;q=50/sign=88f24a05c195d143da76e62343cbf33f/faf2b2119313b07e00d5f60e05d7912397dd8c48.jpg");
        ArrayList<Friend> friends=new ArrayList<>();
        friends.add(friend1);
        friends.add(friend2);
        friends.add(friend3);
        return friends;
    }

}
