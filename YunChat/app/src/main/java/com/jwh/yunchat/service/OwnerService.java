package com.jwh.yunchat.service;

import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.dao.OwnerDao;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.util.OkHttpUtil;

public class OwnerService {

    private BaseActivity activity;
    private OkHttpUtil okHttpUtil;
    private OwnerDao ownerDao;

    public OwnerService(){
        this.ownerDao=new OwnerDao();
    }

    public Owner findByNetId(int netId){
        return ownerDao.findOwnerByNetId(netId);
    }

    public Owner findByUsername(String username){
        return ownerDao.findOwnerByUsername(username);
    }

    public void login(String username,String password){

    }


}
