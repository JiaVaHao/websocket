package com.jwh.yunchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.LoginActivity;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchNetFriendListAdapter extends RecyclerView.Adapter<SearchNetFriendListAdapter.ListHolder> {

    private ArrayList<Friend> friends;
    private Owner owner;
    private Context context;
    private Activity activity;
    private FriendService friendService;

    public SearchNetFriendListAdapter(Context context,ArrayList<Friend> friends,Owner owner){
        this.context=context;
        this.activity=(Activity) context;
        this.friends=friends;
        this.owner=owner;
        this.friendService=new FriendService();
    }

    //动态加载Item布局，并创建Holder
    @NonNull
    @Override
    public SearchNetFriendListAdapter.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=View.inflate(context,R.layout.friend_list_item,null);
        SearchNetFriendListAdapter.ListHolder holder=new SearchNetFriendListAdapter.ListHolder(itemView);
        return holder;
    }
    //绑定数据，在这里会有具体的业务控制
    @Override
    public void onBindViewHolder(@NonNull final SearchNetFriendListAdapter.ListHolder holder, int position) {
        //item渲染
        holder.friendName.setText(friends.get(position).getName());
        holder.friendId.setText(String.valueOf(friends.get(position).getNetId()));
        Glide.with(context)
                .load(friends.get(position).getImageUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.friendImage);

        //item监听，发送好友请求
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.search_net,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        int friendId=Integer.parseInt(holder.friendId.getText().toString());
                        int ownerId=owner.getNetId();
                        HashMap params=new HashMap();
                        params.put("friendId",friendId);
                        params.put("ownerId",ownerId);
                        sendFriendRequest(params);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    //ListHolder 用于Item布局的定义,Holder则抓住了具体的Item，数据填充及渲染在onBindViewHolder中，包括对数据项的监听
    class ListHolder extends RecyclerView.ViewHolder{

        private ImageView friendImage;
        private TextView friendName;
        private TextView friendId;

        public ListHolder(@NonNull View itemView) {
            super(itemView);
            friendImage=itemView.findViewById(R.id.friend_image);
            friendName=itemView.findViewById(R.id.friend_name);
            friendId=itemView.findViewById(R.id.friend_id);
        }
    }

    //发送好友请求
    private void sendFriendRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/sendFriendRequest";
                    String jsonStr= OkHttpUtil.synPost(url, paramMap);
                    sendFriendResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendFriendResponse(final String jsonStr){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Toast.makeText(activity, "好友请求发送成功，等待好友同意", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
