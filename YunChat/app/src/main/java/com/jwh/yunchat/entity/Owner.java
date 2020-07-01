package com.jwh.yunchat.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Owner extends LitePalSupport {

    private int id;
    @Column(unique = true)
    private int netId;
    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private String imageURL;

    public Owner(){
        super();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getNetId() {
        return netId;
    }

    public void setNetId(int netId) {
        this.netId = netId;
    }
}
