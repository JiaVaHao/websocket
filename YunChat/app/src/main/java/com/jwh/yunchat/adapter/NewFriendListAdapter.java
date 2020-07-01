package com.jwh.yunchat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.activity.FriendListActivity;
import com.jwh.yunchat.activity.LoginActivity;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Adapter 是做一个局部渲染的效果，主要是数据的绑定
public class NewFriendListAdapter extends RecyclerView.Adapter<NewFriendListAdapter.NewFriendListHodler> {

    //使用context绑定的activity上下文
    private Context context;
    private FriendListActivity activity;
    private ArrayList<Friend> friends;
    private Owner owner;
    private FriendService friendService;

    //Adapter 构造器,上下文和数据
    public NewFriendListAdapter(Context context, ArrayList<Friend> friends, Owner owner){
        this.context=context;
        this.activity=(FriendListActivity) context;
        this.friends=friends;
        this.owner=owner;
    }

    //创建View和ViewHolder
    @Override
    public NewFriendListHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //通过inflate获取Item View
        View itemView = View.inflate(context, R.layout.new_friend_item,null);
        NewFriendListHodler hodler= new NewFriendListHodler(itemView);
        return hodler;
    }

    //数据绑定
    @Override
    public void onBindViewHolder(final NewFriendListHodler holder, final int position) {
        //渲染
        holder.friendName.setText(friends.get(position).getName());
        holder.friendId.setText(String.valueOf(friends.get(position).getNetId()));
        Glide.with(context)
                .load(friends.get(position).getImageUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.friendImage);

        friendService=new FriendService();
        //同意好友请求
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int friendId=Integer.parseInt(holder.friendId.getText().toString());
                int ownerId= BaseActivity.owner.getNetId();
                HashMap params=new HashMap();
                params.put("friendId",friendId);
                params.put("ownerId",ownerId);
                acceptFriendRequest(params);
                remove(holder.getAdapterPosition());
                if (friends.size()==0){
                    activity.hasNewFriendRequestImage.setVisibility(View.GONE);
                }
            }
        });
        //拒绝好友请求
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int friendId=Integer.parseInt(holder.friendId.getText().toString());
                int ownerId= BaseActivity.owner.getNetId();
                HashMap params=new HashMap();
                params.put("friendId",friendId);
                params.put("ownerId",ownerId);
                rejectFriendRequest(params);
                remove(holder.getAdapterPosition());
                if (friends.size()==0){
                    activity.hasNewFriendRequestImage.setVisibility(View.GONE);
                }
            }
        });
    }
    //发送接收好友请求
    private void acceptFriendRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/acceptFriendRequest";
                    String jsonStr= OkHttpUtil.synPost(url, paramMap);
                    acceptFriendResponse(jsonStr,(int)paramMap.get("friendId"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void acceptFriendResponse(final String jsonStr,final int friendNetId){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    JSONObject newFriend=(JSONObject) jsonMap.get("data");
                    int netId=newFriend.getIntValue("id");
                    String name=newFriend.getString("name");
                    String imageUrl=newFriend.getString("imagePath");
                    String username=newFriend.getString("username");
                    int ownerNetId=owner.getNetId();
                    Friend newFriend1=new Friend(netId,username,name,imageUrl,ownerNetId);
                    newFriend1.save();
                    activity.friendList.add(newFriend1);
                    activity.adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //发送拒绝好友请求
    private void rejectFriendRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = activity.SERVER_URL + "/user/rejectFriendRequest";
                    String jsonStr= OkHttpUtil.synPost(url, paramMap);
                    rejectFriendResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void rejectFriendResponse(final String jsonStr){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Toast.makeText(activity, "已拒绝该好友请求", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //得到总条数
    @Override
    public int getItemCount() {
        return friends.size();
    }

    // 需要自定义个Hodler的类
    class NewFriendListHodler extends RecyclerView.ViewHolder{

        private ImageView friendImage;
        private TextView friendName;
        private TextView friendId;
        private Button accept;
        private Button reject;


        public NewFriendListHodler(final View itemView) {
            super(itemView);
            friendImage=itemView.findViewById(R.id.new_friend_image);
            friendName=itemView.findViewById(R.id.new_friend_name);
            friendId=itemView.findViewById(R.id.new_friend_id);
            accept=itemView.findViewById(R.id.accept_new_friend);
            reject=itemView.findViewById(R.id.reject_new_friend);
        }
    }

    //添加数据
    public void addItem(int position, Friend newFriend) {
        friends.add(position, newFriend);
        notifyItemInserted(position);//通知演示插入动画
        notifyItemRangeChanged(position,friends.size()-position);
    }
    //删除数据
    public void remove(int position){
        friends.remove(position);//删除数据源,移除集合中当前下标的数据
        notifyItemRemoved(position);
        notifyItemRangeChanged(0,getItemCount()-1);
    }
}
