package com.jwh.yunchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.ChatActivity;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Owner;
import com.jwh.yunchat.service.FriendService;

import java.util.ArrayList;

public class SearchLocalFriendListAdapter extends RecyclerView.Adapter<SearchLocalFriendListAdapter.ListHolder> {
    //使用context绑定的activity上下文
    private Context context;
    private ArrayList<Friend> friends;
    private Owner owner;
    private FriendService friendService;

    //Adapter 构造器,上下文和数据
    public SearchLocalFriendListAdapter(Context context, ArrayList<Friend> friends,Owner owner){
        this.context=context;
        this.friends=friends;
        this.owner=owner;
    }

    //创建View和ViewHolder
    @Override
    public SearchLocalFriendListAdapter.ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //通过inflate获取Item View
        View itemView = View.inflate(context, R.layout.friend_list_item,null);
        SearchLocalFriendListAdapter.ListHolder hodler= new SearchLocalFriendListAdapter.ListHolder(itemView);
        return hodler;
    }

    //数据绑定
    @Override
    public void onBindViewHolder(final SearchLocalFriendListAdapter.ListHolder holder, final int position) {
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
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.search_loacl,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                            int friendNetId=Integer.parseInt(holder.friendId.getText().toString());
                            friendService.changeChatStatus(friendNetId,owner.getNetId(),FriendService.IS_CHAT);
                            Intent intent=new Intent(context, ChatActivity.class);
                            intent.putExtra("friendNetId",Integer.parseInt(holder.friendId.getText().toString()));
                            context.startActivity(intent);
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

    class ListHolder extends RecyclerView.ViewHolder {

        private ImageView friendImage;
        private TextView friendName;
        private TextView friendId;

        public ListHolder(final View itemView) {
            super(itemView);
            friendImage=itemView.findViewById(R.id.friend_image);
            friendName=itemView.findViewById(R.id.friend_name);
            friendId=itemView.findViewById(R.id.friend_id);

        }
    }
}
