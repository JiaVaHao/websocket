package com.jwh.yunchat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.ChatActivity;
import com.jwh.yunchat.activity.FriendListActivity;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Adapter 是做一个局部渲染的效果，主要是数据的绑定
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListHodler> {

    //使用context绑定的activity上下文
    private Context context;
    private FriendListActivity activity;
    private ArrayList<Friend> friends;
    private Owner owner;
    private FriendService friendService;

    //Adapter 构造器,上下文和数据
    public FriendListAdapter(Context context, ArrayList<Friend> friends,Owner owner){
        this.context=context;
        this.friends=friends;
        this.activity=(FriendListActivity) context;
        this.owner=owner;
    }

    //创建View和ViewHolder
    @Override
    public FriendListHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //通过inflate获取Item View
        View itemView = View.inflate(context, R.layout.friend_list_item,null);
        FriendListHodler hodler= new FriendListHodler(itemView);
        return hodler;
    }

    //数据绑定
    @Override
    public void onBindViewHolder(final FriendListHodler holder, final int position) {
        //渲染
        holder.friendName.setText(friends.get(position).getName());
        holder.friendId.setText(String.valueOf(friends.get(position).getNetId()));
        Glide.with(context)
                .load(friends.get(position).getImageUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.friendImage);

        friendService=new FriendService();
        // item 操作监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.friend_item_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        //开启聊天
                        if (item.getItemId()==R.id.chat_with_friend){
                            int friendNetId=Integer.parseInt(holder.friendId.getText().toString());
                            int ownerNetId=owner.getNetId();
                            friendService.changeChatStatus(friendNetId,ownerNetId,FriendService.IS_CHAT);
                            HashMap params=new HashMap();
                            params.put("friendId",friendNetId);
                            params.put("ownerId",ownerNetId);
                            checkFriendRequest(params);

                            //删除好友
                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("删除好友");
                            builder.setMessage("确定要删除好友吗？删除后聊天记录将消失");
                            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    remove(holder.getAdapterPosition());
                                    int friendNetId=Integer.parseInt(holder.friendId.getText().toString());
                                    int ownerNetId=owner.getNetId();
                                    friendService.delete(friendNetId,ownerNetId);
                                    HashMap params=new HashMap();
                                    params.put("friendId",friendNetId);
                                    params.put("ownerId",ownerNetId);
                                    deleteFriendRequest(params);
                                }
                            });
                            builder.show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    //得到总条数
    @Override
    public int getItemCount() {
        return friends.size();
    }

    // 需要自定义个Hodler的类
    class FriendListHodler extends RecyclerView.ViewHolder{

        private ImageView friendImage;
        private TextView friendName;
        private TextView friendId;

        public FriendListHodler(final View itemView) {
            super(itemView);
            friendImage=itemView.findViewById(R.id.friend_image);
            friendName=itemView.findViewById(R.id.friend_name);
            friendId=itemView.findViewById(R.id.friend_id);

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

    //网络删除好友
    //发送拒绝好友请求
    private void deleteFriendRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = activity.SERVER_URL + "/user/deleteFriendRequest";
                    String jsonStr= OkHttpUtil.synPost(url, paramMap);
                    deleteFriendResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteFriendResponse(final String jsonStr){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Toast.makeText(activity, "已删除好友", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //检测对方是否是好友
    private void checkFriendRequest(final Map paramMap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = activity.SERVER_URL + "/user/checkFriend";
                    String jsonStr2= OkHttpUtil.synPost(url, paramMap);
                    checkFriendResponse(jsonStr2,(int)paramMap.get("friendId"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkFriendResponse(final String jsonStr2,final int friendNetId){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr2);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    Intent intent=new Intent(context, ChatActivity.class);
                    intent.putExtra("friendNetId",friendNetId);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(activity, "该好友已将你删除，请重新申请好友", Toast.LENGTH_SHORT).show();
                    friendService.delete(friendNetId,owner.getNetId());
                    Intent intent=new Intent(context,FriendListActivity.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            }
        });
    }
}
