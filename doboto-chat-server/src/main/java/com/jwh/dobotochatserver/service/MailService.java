package com.jwh.dobotochatserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailService {

    @Autowired
    JavaMailSenderImpl javaMailSender;

    //springboot-email
    public boolean sendCheckCode(String userName,String checkCode){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("2043981013@qq.com");
        simpleMailMessage.setSubject("验证码");
        simpleMailMessage.setText(checkCode);
        simpleMailMessage.setTo(userName+"@qq.com");
        javaMailSender.send(simpleMailMessage);
        return true;
    }
    //生成6位随机数字
    public String generateCheckCode(){
        Random random=new Random(System.currentTimeMillis());
        String checkCode= String.valueOf((int) (random.nextDouble() * (1000000 - 100000 + 1) + 100000));
        return checkCode;
    }
}
