package com.jwh.yunchat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.ChatListActivity;
import com.jwh.yunchat.activity.FriendListActivity;
import com.jwh.yunchat.activity.MineActivity;
import com.jwh.yunchat.util.ViewUtil;

public class Navigation extends LinearLayout implements View.OnClickListener {

    private ImageView openChatList;
    private ImageView openFriendList;
    private ImageView openMine;
    private ImageView openQR;
    private Context context;
    private Activity activity;

    public Navigation(final Context context, AttributeSet attrs)  {
        super(context, attrs);
        this.context=context;
        this.activity=(Activity)context;
        LayoutInflater.from(context).inflate(R.layout.navigation,this);

        openChatList=findViewById(R.id.open_chat_list);
        openFriendList=findViewById(R.id.open_friend_list);
        openMine=findViewById(R.id.open_mine);
        openQR=findViewById(R.id.open_QR);

        openQR.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.makePopup(context,"抱歉","该功能暂为开放,敬请期待");
            }
        });

        openMine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MineActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });

        openChatList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatListActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });

        openFriendList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FriendListActivity.class);
                context.startActivity(intent);
                activity.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_chat_list:
                Intent intent=new Intent(context, ChatListActivity.class);
                context.startActivity(intent);
                break;
            case R.id.open_friend_list:
                Intent intent1=new Intent(context, FriendListActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.open_mine:

                break;
            case R.id.open_QR:
                Toast.makeText(context,"该功能暂未实现",Toast.LENGTH_SHORT);
                break;
        }
    }
}
