package com.jwh.yunchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jwh.yunchat.R;
import com.jwh.yunchat.adapter.ChatListAdapter;
import com.jwh.yunchat.entity.Chat;
import com.jwh.yunchat.service.ChatService;

import java.util.ArrayList;


public class ChatListActivity extends BaseActivity {

    private TextView titleText;
    private RelativeLayout chatListNull;
    private ImageView back;
    private ImageView hasNew;

    public ArrayList<Chat> chatList;

    private RecyclerView recyclerView;

    private ChatListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        titleText=findViewById(R.id.title_text);
        titleText.setText("聊天");
        back=findViewById(R.id.back);
        back.setVisibility(View.GONE);
        hasNew=findViewById(R.id.has_new_message);
        hasNew.setVisibility(View.GONE);

        chatListNull=findViewById(R.id.chat_list_null);
        recyclerView=findViewById(R.id.chat_list);

        dataInit();

        if (chatList != null){
            chatListNull.setVisibility(View.GONE);
            adapter=new ChatListAdapter(ChatListActivity.this,chatList,owner);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataInit();
        if (chatList != null){
            chatListNull.setVisibility(View.GONE);
            adapter=new ChatListAdapter(ChatListActivity.this,chatList,owner);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        }
    }

    public void dataInit(){
        ChatService chatService=new ChatService();
        chatList=chatService.findChatList(owner.getNetId());
    }

    public void updateUi(){
        dataInit();
        if (chatList != null){
            chatListNull.setVisibility(View.GONE);
            adapter=new ChatListAdapter(ChatListActivity.this,chatList,owner);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        }
    }
}
