package com.jwh.dobotochatserver.dao;

import com.jwh.dobotochatserver.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface UserDao {

    public User getById(Integer id);

    public List<User> getByKeyword(String keyword);

    public User getByUsername(String username);

    public void insert(User user);

    public void updateOne(Map<String, String> map);

    public void updateOneByUsername(Map<String, String> map);

    public void completeUserInfo(User user);
}
