package com.jwh.dobotochatserver.dao;

import com.jwh.dobotochatserver.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDao {

    public List<Message> getByReceiverId(Integer receiverId);

    public void insert(Message message);

    public void changeReadStatus(Integer receiverId);

    public void delete();
}
