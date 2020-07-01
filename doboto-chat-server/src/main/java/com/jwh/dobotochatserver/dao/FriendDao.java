package com.jwh.dobotochatserver.dao;

import com.jwh.dobotochatserver.entity.Friend;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

@Repository
public interface FriendDao {

    public Friend getById(Integer id);

    public ArrayList<Friend> getByOwnerId(Integer ownerId);

    public ArrayList<Friend> getNewRequestByOwnerId(Integer ownerId);

    public ArrayList<Friend> getNewFriendsByOwnerId(Integer ownerId);

    public Friend checkIsFriend(int ownerId,int friendId);

    public void insert(Friend friend);

    public void deleteFriend(int id);

    public void acceptFriend(int id);

    public void changeNewFriendStatus(int id);

    public void changeNewFriendStatus2(HashMap params);
}
