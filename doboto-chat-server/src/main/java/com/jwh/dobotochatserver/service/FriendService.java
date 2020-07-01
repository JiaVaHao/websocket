package com.jwh.dobotochatserver.service;

import com.jwh.dobotochatserver.dao.FriendDao;
import com.jwh.dobotochatserver.dao.UserDao;
import com.jwh.dobotochatserver.entity.Friend;
import com.jwh.dobotochatserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class FriendService {

    @Autowired
    private FriendDao friendDao;
    @Autowired
    private UserDao userDao;

    public ArrayList<Friend> getFriendsByUser(User user){
        try {
            int ownerId=user.getId();
            return friendDao.getByOwnerId(ownerId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Friend> getNewFriendRequestByUser(User user){
        try {
            int ownerId=user.getId();
            return friendDao.getNewRequestByOwnerId(ownerId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //好友请求
    public String sendFriendRequest(int userId,int friendId){
        try {
            Friend isFriend=friendDao.checkIsFriend(friendId,userId);
            if (isFriend!=null){
                return "该好友已经发生过好友请求";
            }
            Friend newFriend=new Friend(friendId,userId,Friend.REJECT,Friend.IS_NEW);
            friendDao.insert(newFriend);
            //此时朋友会查到这条记录，而发送请求的用户的朋友记录是不存在的。
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "好友请求系统错误";
        }
    }
    //接受好友请求
    public User accept(int ownerId,int friendId){
        try {
            Friend friend=friendDao.checkIsFriend(ownerId,friendId);
            if (friend==null){
                return null;
            }
            friendDao.changeNewFriendStatus(friend.getId());
            Friend friend1=new Friend(friendId,ownerId,Friend.ACCEPT,Friend.IS_NEW);
            friendDao.insert(friend1);
            friendDao.acceptFriend(friend.getId());
            User newFriend=userDao.getById(friendId);
            return newFriend;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //拒绝好友请求
    public String reject(int userId,int friendId){
        try {
            Friend friend=friendDao.checkIsFriend(userId,friendId);
            if (friend==null){
                return "该好友并未发送好友请求";
            }
            friendDao.deleteFriend(friend.getId());
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "系统错误";
        }
    }
    //删除好友
    public String delete(int userId,int friendId){
        try {
            Friend friend1=friendDao.checkIsFriend(userId,friendId);
            Friend friend2=friendDao.checkIsFriend(friendId,userId);
            if (friend1==null || friend2==null){
                return "对方不是您的好友";
            }
            friendDao.deleteFriend(friend1.getId());
            friendDao.deleteFriend(friend2.getId());
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "系统错误";
        }
    }
    //获取新的好友数据
    public ArrayList<User> getNewFriends(int ownerId){
        try {
            ArrayList<Friend> friends=friendDao.getNewFriendsByOwnerId(ownerId);
            if (friends==null||friends.size()==0){
                return null;
            }
            ArrayList<User> newFriends=new ArrayList<>();
            for (Friend friend:friends){
                friendDao.changeNewFriendStatus(friend.getId());
                User newFriend=userDao.getById(friend.getFriendId());
                newFriends.add(newFriend);
            }
            return newFriends;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //
    public void changeNewFriendStatus(int friendId,int ownerId){
        HashMap<String,Integer> params=new HashMap<>();
        params.put("friendId",friendId);
        params.put("ownerId",ownerId);
        friendDao.changeNewFriendStatus2(params);
    }

    //检查是否是好友
    public String checkIsFriend(int userId,int friendId){
        try {
            Friend friend1=friendDao.checkIsFriend(userId,friendId);
            Friend friend2=friendDao.checkIsFriend(friendId,userId);
            if (friend1==null || friend2==null){
                return "对方不是您的好友";
            }
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "系统错误";
        }
    }
}
