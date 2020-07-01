package com.jwh.dobotochatserver.service;

import com.jwh.dobotochatserver.dao.UserDao;
import com.jwh.dobotochatserver.entity.User;
import com.jwh.dobotochatserver.util.DRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    MailService msgService;
    @Autowired
    DRedisUtil redisUtil;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public String register(User user, String checkCode){
        try {
            //先检查该用户有没有注册过
            User userFromDB=userDao.getByUsername(user.getUsername());
            if (userFromDB!=null){
                return "该用户名已经注册过了";
            }
            String checkCodeFromRedis=(String) redisUtil.get("checkCode-"+user.getUsername());
            if (checkCodeFromRedis ==null || checkCodeFromRedis.equals("") || !checkCode.equals(checkCodeFromRedis)){
                return "验证码错误,请稍后再试";
            }
            userDao.insert(user);
            return "success";
        }catch (Exception e){
            log.info("register was error, please check the log info");
            e.printStackTrace();
            return "注册系统错误，请联系我";
        }
    }
    //给用户发送验证码，并保存在redis中5分钟，阿里的短信服务有时间和次数限制，同一目标，每分钟1条，每个小时5条，累计10条
    public boolean sendCheckCode(String username){
        try {
            //发送邮箱验证码
            String code=msgService.generateCheckCode();
            boolean sendStatue=msgService.sendCheckCode(username,code);
            if (sendStatue){
                redisUtil.set("checkCode-"+username,code,60*5);
                return true;
            }else {
                log.info("check code is out of time limit");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("send check code is error, please check error info");
            return false;
        }
    }

    public String forgetPW(User user, String checkCode) {
        try {
            //先检查该用户有没有注册过
            User userFromDB=userDao.getByUsername(user.getUsername());
            if (userFromDB==null){
                return "该用户名还未注册，请先注册";
            }
            String checkCodeFromRedis=(String) redisUtil.get("checkCode-"+user.getUsername());
            if (checkCodeFromRedis ==null || checkCodeFromRedis.equals("") || !checkCode.equals(checkCodeFromRedis)){
                return "验证码错误,请稍后再试";
            }
            Map<String,String> param=new HashMap<>();
            param.put("fieldName","password");
            param.put("fieldValue",user.getPassword());
            param.put("username",user.getUsername());
            userDao.updateOneByUsername(param);
            return "success";
        }catch (Exception e){
            log.info("register was error, please check the log info");
            e.printStackTrace();
            return "修改密码系统错误，请联系我";
        }
    }

    public String completeUserInfo(User user){
        try {
            userDao.completeUserInfo(user);
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "完善信息失败，请联系我";
        }
    }

    public String login(String username,String password){
        User dbUser=userDao.getByUsername(username);
        if (dbUser == null){
            return "用户不存在，请注册";
        }
        if (!password.equals(dbUser.getPassword())){
            return "用户名密码错误，请重新输入";
        }
        return "success";
    }

    public User getUserByUsername(String username){
        return userDao.getByUsername(username);
    }

    public User getUserById(int userId){
        return userDao.getById(userId);
    }
}
