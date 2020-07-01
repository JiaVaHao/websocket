package com.jwh.yunchat.dao;

import com.jwh.yunchat.entity.Owner;

import org.litepal.LitePal;

import java.util.ArrayList;

public class OwnerDao extends BaseDao {

    public Owner findOwnerByNetId(int netId){
        ArrayList<Owner> owner= (ArrayList<Owner>) LitePal.where("netId = ?", String.valueOf(netId)).find(Owner.class);
        if (owner.size()==0){
            return null;
        }else {
            return owner.get(0);
        }
    }

    public Owner findOwnerByUsername(String username){
        ArrayList<Owner> owner= (ArrayList<Owner>) LitePal.where("username = ?", username).find(Owner.class);
        if (owner.size()==0){
            return null;
        }else {
            return owner.get(0);
        }
    }
}
