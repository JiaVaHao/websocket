package com.jwh.yunchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jwh.yunchat.R;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.entity.MessageItemView;
import com.jwh.yunchat.entity.Owner;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Message> messageList;
    private Friend friend;
    private Owner owner;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        ImageView rightImg;
        ImageView leftImg;

        public ViewHolder(View view){
            super(view);
            leftLayout=view.findViewById(R.id.left_layout);
            rightLayout=view.findViewById(R.id.right_layout);
            leftMsg=view.findViewById(R.id.left_msg);
            rightMsg=view.findViewById(R.id.right_msg);
            rightImg=view.findViewById(R.id.right_msg_img);
            leftImg=view.findViewById(R.id.left_msg_img);
        }
    }

    public MsgAdapter(Context context,MessageItemView messageItemView){
        this.context=context;
        this.messageList = messageItemView.messages;
        this.friend=messageItemView.friend;
        this.owner=messageItemView.owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);

        return new ViewHolder(view);
    }
    //这里需要重新写，结合数据库
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSenderNetId()== owner.getNetId()){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(message.getContent());
            Glide.with(context)
                    .load(owner.getImageURL())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.rightImg);
        }else {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(message.getContent());
            Glide.with(context)
                    .load(friend.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.leftImg);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
