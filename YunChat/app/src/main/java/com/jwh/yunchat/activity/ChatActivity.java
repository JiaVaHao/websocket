package com.jwh.yunchat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.jwh.yunchat.adapter.MsgAdapter;
import com.jwh.yunchat.R;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.entity.Message;
import com.jwh.yunchat.entity.MessageItemView;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.service.MessageService;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends BaseActivity {

    private EditText inputMessage;

    private TextView titleText;

    private RecyclerView msgRecView;

    private MsgAdapter adapter;

    private Friend currentFriend;
    public int friendNetId;
    private ArrayList<Message> messageList;
    private MessageItemView msgItemView;

    private FriendService friendService;
    private MessageService messageService;

    public ImageView hasNewMsgView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        friendService=new FriendService();
        messageService=new MessageService();

        //聊天记录数据初始化
        initData();
        //渲染非列表界面
        titleText=findViewById(R.id.title_text);
        inputMessage=findViewById(R.id.input_message);

        msgRecView=findViewById(R.id.msg_recycle_view);
        titleText.setText(currentFriend.getName());

        hasNewMsgView=findViewById(R.id.has_new_message);
        hasNewMsgView.setVisibility(View.GONE);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        msgRecView.setLayoutManager(layoutManager);

        adapter=new MsgAdapter(ChatActivity.this,msgItemView);

        msgRecView.setAdapter(adapter);
        msgRecView.scrollToPosition(messageList.size()-1);
        //使用软键盘监听发送
        inputMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
        inputMessage.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        inputMessage.setMaxLines(2);
        inputMessage.setSingleLine(false);
        inputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                     String content=inputMessage.getText().toString();
                        if (!content.equals("")){
                            Message message =new Message(owner.getNetId(),currentFriend.getNetId(),content);
                            addMsg(message,true);
                            inputMessage.setText("");
                            wsClient.send(JSON.toJSONString(message));
                        }
                }
                return false;
            }
        });

    }

    private void initData() {
        //从intent中取出currentNetId并查出对象
        Intent intent=getIntent();
        friendNetId=intent.getIntExtra("friendNetId",-1);
        currentFriend=friendService.findByNetId(friendNetId,owner.getNetId());
        if (currentFriend == null){
            Toast.makeText(ChatActivity.this,"对方不是您的好友",Toast.LENGTH_SHORT);
            //如果不是好友则退出该活动
        }
        //从数据库中查找聊天记录-默认最近20条记录
        messageList=messageService.findNearlyMessage(friendNetId,owner.getNetId());
        //组装成MessageItemViw
        msgItemView=new MessageItemView(currentFriend,owner,messageList);
    }
    //消息添加方法
    public void addMsg(Message message,boolean isRead){
        msgItemView.messages.add(message);
        adapter.notifyItemInserted(messageList.size()-1);
        msgRecView.scrollToPosition(messageList.size()-1);
        message.setRead(isRead);
        message.save();
    }
    public void addMsgNotSave(Message message){
        msgItemView.messages.add(message);
        adapter.notifyItemInserted(messageList.size()-1);
        msgRecView.scrollToPosition(messageList.size()-1);
    }
}
