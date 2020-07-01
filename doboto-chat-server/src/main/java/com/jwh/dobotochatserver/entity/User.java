package com.jwh.dobotochatserver.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private int id;
    private String username;
    @JSONField(serialize = false)
    private String password;
    private String name;
    private String imagePath;
    private int age;
    private String city;

    private ArrayList<User> friends;

    public User(){
        super();
    }

    public User(String username,String password,String name,int age,String city){
        this.username=username;
        this.password=password;
        this.name=name;
        this.imagePath="imagePath";
        this.age=age;
        this.city=city;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
