package com.jwh.dobotochatserver.controller;

import com.alibaba.fastjson.JSON;
import com.jwh.dobotochatserver.DobotoChatServerApplication;
import com.jwh.dobotochatserver.entity.Friend;
import com.jwh.dobotochatserver.entity.Message;
import com.jwh.dobotochatserver.entity.User;
import com.jwh.dobotochatserver.service.FriendService;
import com.jwh.dobotochatserver.service.MessageService;
import com.jwh.dobotochatserver.service.UserService;
import com.jwh.dobotochatserver.util.DResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Value("${file.isLinux}")
    String isLinux;
    @Value("${file.imagesPath}")
    String imageRootPath;

    @Autowired
    UserService userService;
    @Autowired
    FriendService friendService;
    @Autowired
    MessageService messageService;


    @GetMapping(value = "/user/{username}/sendCheckCode")
    public DResultMsg sendCheckCode(@PathVariable("username") String username){
        if (userService.sendCheckCode(username)){
            return new DResultMsg(DResultMsg.SUCCESS);
        }else {
            return new DResultMsg(DResultMsg.ERROR,"阿里短信服务：3次/分钟；5次/小时；10次/天");
        }
    }
    @PostMapping(value = "/user/register")
    public DResultMsg register(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        User newUser=new User();
        newUser.setName("默认昵称");
        newUser.setImagePath("http://photocdn.sohu.com/20110508/Img307009056.jpg");
        newUser.setUsername((String) params.get("newUsername"));
        newUser.setPassword((String) params.get("newPassword"));
        String message=userService.register(newUser,(String) params.get("checkCode"));
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }else {
            return new DResultMsg(DResultMsg.ERROR,message);
        }
    }
    @PostMapping(value = "/user/forgetPW")
    public DResultMsg forgetPW(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        User user=new User();
        user.setUsername((String) params.get("username"));
        user.setPassword((String) params.get("newPassword"));
        String message=userService.forgetPW(user,(String) params.get("checkCode"));
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }else {
            return new DResultMsg(DResultMsg.ERROR,message);
        }
    }

    //
    @PostMapping("/user/{username}/completeUserInfo")
    public DResultMsg completeUserInfo(MultipartFile file, @PathVariable(name = "username") String username,@RequestParam(name = "jsonStr") String jsonStr){
        DResultMsg result=null;
        Map params= JSON.parseObject(jsonStr);
        String fileName=fileUpload(file,username);
        if (fileName.equals("error")){
            result=new DResultMsg(DResultMsg.ERROR,"上传错误");
        }else {
            User user=new User();
            user.setUsername(username);
            user.setImagePath(fileName);
            user.setName((String) params.get("name"));
            user.setAge(Integer.parseInt((String) params.get("age")));
            user.setCity((String)params.get("city"));
            userService.completeUserInfo(user);
            result=new DResultMsg(DResultMsg.SUCCESS,fileName,"success");
        }
        return result;
    }


    public String fileUpload(MultipartFile file, String username){
        //获取上传文件原始文件名
        String originName=file.getOriginalFilename();
        //截取原始文件名后缀
        String suffixName = originName.substring(originName.lastIndexOf("."));
        //文件命名规则（id+suffixName）
        String fileName= DobotoChatServerApplication.USER_HEADING_ROOT_PATH+username+".jpg";
        //文件存储
        File targetFile=null;
        //用于部署Linux中修改路径添加外部文件根路径
        if (isLinux.equals("true")){
            targetFile=new File(imageRootPath+fileName);
        }else{
            targetFile=new File(fileName);
        }
        try {
            file.transferTo(targetFile);
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "/images/"+fileName;
    }

    @PostMapping(value = "/user/login")
    public DResultMsg login(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        String message=userService.login((String) params.get("username"),(String) params.get("password"));
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }else {
            return new DResultMsg(DResultMsg.ERROR,message);
        }
    }

    @GetMapping(value = "/user/firstLogin/{username}")
    public DResultMsg firstLogin(@PathVariable("username") String username){
        //获取用户
        User user=userService.getUserByUsername(username);
        //获取好友列表
        ArrayList<User> friendList=new ArrayList<User>();
        ArrayList<Friend> friendItems=friendService.getFriendsByUser(user);
        if (friendItems != null || friendItems.size()>0){
            for (Friend item: friendItems){
                User friend=userService.getUserById(item.getFriendId());
                friendList.add(friend);
            }
        }
        Map resultMap=new HashMap();
        resultMap.put("owner",user);
        resultMap.put("friends",friendList);
        return new DResultMsg(DResultMsg.SUCCESS,resultMap,"success");
    }

    @GetMapping(value ="/user/getUnReadMsg/{username}")
    public DResultMsg getUnReadMsg(@PathVariable("username") String username){
        //获取用户
        User user=userService.getUserByUsername(username);
        //获取未读消息列表
        ArrayList<Message> unReadMessageFormDB=messageService.getUnReadMessage(user.getId());
        if (unReadMessageFormDB != null){
            return new DResultMsg(DResultMsg.SUCCESS,unReadMessageFormDB,"success");
        }
        return new DResultMsg(DResultMsg.ERROR,"error");
    }

    @GetMapping(value ="/user/getNewFriendRequests/{username}")
    public DResultMsg getNewFriends(@PathVariable("username") String username){
        //获取用户
        User user=userService.getUserByUsername(username);
        //获取好友请求列表
        ArrayList<Friend> newFriends=friendService.getNewFriendRequestByUser(user);
        ArrayList<User> resultFriends=new ArrayList<>();
        if (newFriends==null){
            return new DResultMsg(DResultMsg.ERROR,"error");
        }
        for (Friend friend:newFriends){
            User newFriend=userService.getUserById(friend.getFriendId());
            resultFriends.add(newFriend);
        }
        return new DResultMsg(DResultMsg.SUCCESS,resultFriends,"success");
    }

    @GetMapping(value ="/user/searchUser/{username}")
    public DResultMsg searchUser(@PathVariable("username") String username){
        //获取用户
        User user=userService.getUserByUsername(username);
        if (user==null){
            return new DResultMsg(DResultMsg.ERROR,"error");
        }
        return new DResultMsg(DResultMsg.SUCCESS,user,"success");
    }

    @PostMapping(value = "/user/sendFriendRequest")
    public DResultMsg sendFriendRequest(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        int friendId=(int)params.get("friendId");
        int userId=(int)params.get("ownerId");
        String message=friendService.sendFriendRequest(userId,friendId);
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }
        return new DResultMsg(DResultMsg.ERROR,message);
    }

    @PostMapping(value = "/user/acceptFriendRequest")
    public DResultMsg acceptFriendRequest(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        int friendId=(int)params.get("friendId");
        int ownerId=(int)params.get("ownerId");
        User newFriend=friendService.accept(ownerId,friendId);
        if (newFriend==null){
            return new DResultMsg(DResultMsg.ERROR,"好友添加错误");
        }
        return new DResultMsg(DResultMsg.SUCCESS,newFriend,"success");
    }

    @PostMapping(value = "/user/rejectFriendRequest")
    public DResultMsg rejectFriendRequest(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        int friendId=(int)params.get("friendId");
        int userId=(int)params.get("ownerId");
        String message=friendService.reject(userId,friendId);
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }
        return new DResultMsg(DResultMsg.ERROR,message);
    }

    @PostMapping(value = "/user/deleteFriendRequest")
    public DResultMsg deleteFriendRequest(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        int friendId=(int)params.get("friendId");
        int userId=(int)params.get("ownerId");
        String message=friendService.delete(userId,friendId);
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }
        return new DResultMsg(DResultMsg.ERROR,message);
    }

    @GetMapping(value ="/user/getNewFriends/{username}")
    public DResultMsg getNewFriend(@PathVariable("username") String username){
        User user=userService.getUserByUsername(username);
        ArrayList<User> newFriends=friendService.getNewFriends(user.getId());
        if (newFriends==null){
            return new DResultMsg(DResultMsg.ERROR,"error");
        }
        return new DResultMsg(DResultMsg.SUCCESS,newFriends,"success");
    }

    @GetMapping(value ="/user/getNewFriendById/{ownerId}/{friendId}")
    public DResultMsg getNewFriendByNetId(@PathVariable("friendId") String friendId,@PathVariable("ownerId") String ownerId){
        int friendId2=Integer.parseInt(friendId);
        int ownerId2=Integer.parseInt(ownerId);
        User user=userService.getUserById(friendId2);
        if (user==null){
            return new DResultMsg(DResultMsg.ERROR,"error");
        }
        friendService.changeNewFriendStatus(friendId2,ownerId2);
        return new DResultMsg(DResultMsg.SUCCESS,user,"success");
    }

    @PostMapping(value = "/user/checkFriend")
    public DResultMsg checkFriend(@RequestParam(name = "jsonStr") String jsonStr){
        Map params= JSON.parseObject(jsonStr);
        int friendId=(int)params.get("friendId");
        int userId=(int)params.get("ownerId");
        String message=friendService.checkIsFriend(userId,friendId);
        if (message.equals("success")){
            return new DResultMsg(DResultMsg.SUCCESS);
        }
        return new DResultMsg(DResultMsg.ERROR,message);
    }


}
