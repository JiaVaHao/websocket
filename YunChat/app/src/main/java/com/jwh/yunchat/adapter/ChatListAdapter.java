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
import com.jwh.yunchat.activity.ChatListActivity;
import com.jwh.yunchat.dao.MessageDao;
import com.jwh.yunchat.entity.Chat;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.service.MessageService;
import com.jwh.yunchat.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHodler> {

    private final Context context;
    private ArrayList<Chat> chatList;
    private FriendService friendService;
    private MessageService messageService;
    private Owner owner;
    private MessageDao messageDao;
    private ChatListActivity activity;

    public ChatListAdapter(Context context, ArrayList<Chat> chatList, Owner owner){
        this.context=context;
        this.chatList=chatList;
        this.friendService=new FriendService();
        this.messageService=new MessageService();
        this.owner=owner;
        this.messageDao=new MessageDao();
        this.activity=(ChatListActivity) context;
    }

    @Override
    public ChatListHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=View.inflate(context, R.layout.chat_list_item,null);
        return new ChatListHodler(itemView);
    }

    //如何渲染item
    @Override
    public void onBindViewHolder(final ChatListHodler holder, final int position) {
        Chat chat=chatList.get(position);

        holder.friendId.setText(String.valueOf(chat.getFriendId()));

        holder.friendName.setText(chat.getFriendName());
        holder.lastMessage.setText(chat.getLastMessage());
        holder.time.setText(chat.getTime());

        //设置未读显示
        if (chat.isRead()){
            holder.unread.setVisibility(View.GONE);
        }
        //设置头像
        Glide.with(context)
                .load(chat.getFriendImage())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.friendImage);

        //设置Item监听事件
        //进入聊天
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启聊天之前需要进行消息修改
                messageDao.changeMsgStatus(Integer.parseInt(holder.friendId.getText().toString()),owner.getNetId());
                HashMap params=new HashMap();
                params.put("friendId",Integer.parseInt(holder.friendId.getText().toString()));
                params.put("ownerId",owner.getNetId());
                checkFriendRequest(params);
            }
        });
        //长按删除聊天
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.chat_operate,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        //删除聊天
                        if (item.getItemId()==R.id.remove_chat_item){
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("删除聊天记录");
                            builder.setMessage("确定要删除聊天记录吗？删除后聊天记录将会清空");
                            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    int friendNetId=Integer.parseInt(holder.friendId.getText().toString());
                                    //修改数据库
                                    friendService.changeChatStatus(friendNetId,owner.getNetId(), FriendService.IS_NOT_CHAT);
                                    messageService.delete(friendNetId,owner.getNetId());
                                    //修改界面
                                    remove(position);

                                }
                            });
                            builder.show();

                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatListHodler extends RecyclerView.ViewHolder{

        private ImageView friendImage;
        private TextView friendName;
        private TextView lastMessage;
        private TextView time;
        private ImageView unread;
        private TextView friendId;

        public ChatListHodler(View itemView) {
            super(itemView);
            friendImage=itemView.findViewById(R.id.chat_friend_image);
            friendId=itemView.findViewById(R.id.chat_friend_id);
            friendName=itemView.findViewById(R.id.chat_friend_name);
            lastMessage=itemView.findViewById(R.id.last_message);
            time=itemView.findViewById(R.id.last_msg_time);
            unread=itemView.findViewById(R.id.unread);

        }
    }
    //删除数据
    public void remove(int position){
        chatList.remove(position);//删除数据源,移除集合中当前下标的数据
        notifyItemRemoved(position);
        notifyItemRangeChanged(0,getItemCount()-1);
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
                    Intent intent=new Intent(context, ChatListActivity.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            }
        });
    }

}
