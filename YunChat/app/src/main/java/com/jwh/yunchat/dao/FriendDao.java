package com.jwh.yunchat.dao;

import com.jwh.yunchat.entity.Friend;

import org.litepal.LitePal;

import java.util.ArrayList;

public class FriendDao extends BaseDao{

    public ArrayList<Friend> findFriendListByOwnerNetId(int ownerNetId){
        ArrayList<Friend> friendList=(ArrayList<Friend>)LitePal.where("ownerNetId = ? AND isfriend=1", String.valueOf(ownerNetId)).find(Friend.class);
        return friendList;
    }

    public ArrayList<Friend> findChatFriendList(int ownerNetId){
        ArrayList<Friend> friendList=(ArrayList<Friend>)LitePal.where("ownerNetId = ? AND isChat = ? AND isfriend=1", String.valueOf(ownerNetId),"1").find(Friend.class);
        return friendList;
    }

    public Friend findByNetId(int netId,int ownerNetId){
        ArrayList<Friend> friendList=(ArrayList<Friend>)LitePal.where("netId = ? and ownerNetId = ? AND isfriend=1", String.valueOf(netId),String.valueOf(ownerNetId)).find(Friend.class);
        if (friendList.size()==0){
            return null;
        }
        return friendList.get(0);
    }

    public void changeChatStatus(Friend friend,int netId,int ownerNetId){
        friend.updateAll("netId = ? and ownerNetId = ?",String.valueOf(netId),String.valueOf(ownerNetId));
    }

    public ArrayList<Friend> findByKeyword(String keyword,int ownerNetId){
        String keywordCop="%"+keyword+"%";
        ArrayList<Friend> friends=(ArrayList<Friend>) LitePal.where("name like ? AND isfriend=1 AND ownerNetId=?",keywordCop,String.valueOf(ownerNetId)).find(Friend.class);
        return friends;
    }

    public ArrayList<Friend> findNewFriendList(int ownerNetId){
        ArrayList<Friend> friendList=(ArrayList<Friend>)LitePal.where("ownerNetId = ? AND  isfriend=0", String.valueOf(ownerNetId)).find(Friend.class);
        return friendList;
    }
}
