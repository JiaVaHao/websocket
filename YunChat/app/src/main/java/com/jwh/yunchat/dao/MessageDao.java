package com.jwh.yunchat.dao;

import android.database.Cursor;

import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.util.DBUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDao extends BaseDao {

    public Message findLastMessage(int friendNetId,int ownerNetId){
        String fnId=String.valueOf(friendNetId);
        String onId=String.valueOf(ownerNetId);
        Cursor cursor=
                LitePal.findBySQL(
                        "select * from message where (senderNetId = ? AND receiverNetId = ?) OR (senderNetId = ? AND receiverNetId = ?) order by createtime desc limit 1",
                        onId,fnId,
                        fnId,onId);

        ArrayList<Message> messages=DBUtil.cursorToList(cursor,Message.class);
        if (messages.size()==0){
            return null;
        }else {
            return messages.get(0);
        }
    }

    public ArrayList<Message> findUnReadMessage(int ownerNetId){
        String onId=String.valueOf(ownerNetId);
        Cursor cursor=
                LitePal.findBySQL(
                        "select * from message where receiverNetId = ? and isRead=0 order by createTime desc limit 1",
                        onId);

        ArrayList<Message> messages=DBUtil.cursorToList(cursor,Message.class);
        if (messages.size()==0){
            return null;
        }else {
            return messages;
        }
    }

    public ArrayList<Message> findNearlyMessage(int friendNetId,int ownerNetId){
        String fnId=String.valueOf(friendNetId);
        String onId=String.valueOf(ownerNetId);
        Cursor cursor=
                LitePal.findBySQL(
                        "select * from message where (senderNetId = ? AND receiverNetId = ?) OR (senderNetId = ? AND receiverNetId = ?) order by createTime desc limit 20",
                        onId,fnId,
                        fnId,onId);

        ArrayList<Message> messages=DBUtil.cursorToList(cursor,Message.class);
        Collections.reverse(messages);

        if (messages.size()==0||messages==null){
            return new ArrayList<Message>();
        }
        return messages;
    }

    public void changeMsgStatus(int friendNetId,int ownerNetId){
        String fnId=String.valueOf(friendNetId);
        String onId=String.valueOf(ownerNetId);
        Cursor cursor=
                LitePal.findBySQL(
                        "select * from message where senderNetId = ? AND receiverNetId = ? AND isread= 0",
                        fnId,onId);

        ArrayList<Message> messages=DBUtil.cursorToList(cursor,Message.class);
        if (messages !=null || messages.size()>0){
            for (Message message : messages){
                message.setRead(true);
                message.update(message.getId());
            }
        }
    }

    public ArrayList<Message> findAllMessage(int friendNetId,int ownerNetId){
        String fnId=String.valueOf(friendNetId);
        String onId=String.valueOf(ownerNetId);
        Cursor cursor=
                LitePal.findBySQL(
                        "select * from message where (senderNetId = ? AND receiverNetId = ?) OR (senderNetId = ? AND receiverNetId = ?) order by createtime desc",
                        onId,fnId,
                        fnId,onId);

        ArrayList<Message> messages=DBUtil.cursorToList(cursor,Message.class);
        Collections.reverse(messages);

        if (messages.size()==0||messages==null){
            return new ArrayList<Message>();
        }
        return messages;
    }

    public void delete(int friendNetId,int ownerNetId){
        String fnId=String.valueOf(friendNetId);
        String onId=String.valueOf(ownerNetId);
        LitePal.deleteAll(Message.class,
                "(senderNetId = ? AND receiverNetId = ?) OR (senderNetId = ? AND receiverNetId = ?)",
                fnId,onId,
                onId,fnId);
    }
}
