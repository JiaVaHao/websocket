package com.jwh.yunchat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


//MD5加密，通过MD5摘要算法，将数据进行计算成128位二进制hash数据（32位16进制的数据），是单向加密，不可反向解密，除非暴力破解
public class DMD5Encrypt {

    public static String MD5Encrypt(String message) {
        try {
            //定义16进制字符，方便通过二进制转化
            char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            byte[] inputBt = message.getBytes();
            //获取Java实现的MD5摘要算法实例
            MessageDigest mdInstance = MessageDigest.getInstance("MD5");
            //执行加密算法，计算出128位的二进制hash码
            mdInstance.update(inputBt);
            byte[] encryptedCharBytes = mdInstance.digest();
            //转换已加密信息为十六位字符串形式
            char[] encryptedCharHexs = new char[encryptedCharBytes.length * 2];
            int i = 0;
            for (byte encryptedCharByte : encryptedCharBytes) {
                encryptedCharHexs[i++] = hexChars[encryptedCharByte >>> 4 & 0xf];
                encryptedCharHexs[i++] = hexChars[encryptedCharByte & 0xf];
            }
            return new String(encryptedCharHexs);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean MD5Check(String orginalMessage, String MD5Message) {
        String newMD5Message = MD5Encrypt(orginalMessage);
        return newMD5Message.equals(MD5Message) ? true : false;
    }
}
