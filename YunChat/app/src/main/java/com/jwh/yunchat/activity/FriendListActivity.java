package com.jwh.yunchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jwh.yunchat.adapter.FriendListAdapter;
import com.jwh.yunchat.R;
import com.jwh.yunchat.adapter.NewFriendListAdapter;
import com.jwh.yunchat.dao.FriendDao;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.util.OkHttpUtil;
import com.jwh.yunchat.view.SearchFriendFragment;

import java.util.ArrayList;
import java.util.Map;

public class FriendListActivity extends BaseActivity {

    private RecyclerView recyclerView;

    private RecyclerView newFriendRView;

    public FriendListAdapter adapter;

    private NewFriendListAdapter adapter2;

    public ArrayList<Friend> friendList;

    private ArrayList<Friend> newFriendList=new ArrayList<>();

    private TextView titleText;
    private ImageView hasNew;

    private TextView openSearchPanel;

    private RelativeLayout openNewFriendBtn;

    private FrameLayout searchFriendPanel;

    private Boolean newFriendPanelVisibility=false;

    public ImageView hasNewFriendRequestImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        recyclerView=findViewById(R.id.friend_list_view);
        newFriendRView=findViewById(R.id.new_friend_list);

        titleText=findViewById(R.id.title_text);
        titleText.setText("好友列表");
        hasNew=findViewById(R.id.has_new_message);
        hasNew.setVisibility(View.GONE);

        ImageView back=findViewById(R.id.back);
        back.setVisibility(View.GONE);

        openSearchPanel=findViewById(R.id.open_search_friend);
        openNewFriendBtn=findViewById(R.id.open_new_friend_panel);
        searchFriendPanel=findViewById(R.id.search_friend_panel);
        hasNewFriendRequestImage=findViewById(R.id.has_new_friend);
        hasNewFriendRequestImage.setVisibility(View.GONE);

        searchFriendPanel.setVisibility(View.GONE);
        newFriendRView.setVisibility(View.GONE);

        //准备数据集合,名称+头像的数据集合
        initDatas();


        //设置RecycleView适配器
        adapter=new FriendListAdapter(FriendListActivity.this,friendList,owner);
        adapter2=new NewFriendListAdapter(FriendListActivity.this,newFriendList,owner);

        recyclerView.setAdapter(adapter);
        newFriendRView.setAdapter(adapter2);

        //LayoutManager,设置布局类型才会显示
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendListActivity.this));
        newFriendRView.setLayoutManager(new LinearLayoutManager(FriendListActivity.this));
        //打开搜索好友面板
        openSearchPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new SearchFriendFragment());
                searchFriendPanel.setVisibility(View.VISIBLE);
            }
        });
        //打开好友申请列表
        openNewFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newFriendPanelVisibility){
                    newFriendRView.setVisibility(View.GONE);
                    newFriendPanelVisibility=false;
                }else{
                    newFriendRView.setVisibility(View.VISIBLE);
                    newFriendPanelVisibility=true;
                }
            }
        });
    }

    private void openFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.search_friend_panel,fragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initDatas() {
        FriendDao friendDao=new FriendDao();
        friendList=friendDao.findFriendListByOwnerNetId(owner.getNetId());
        getNewFriendRequest();
    }
    //获取好友请求
    private void getNewFriendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SERVER_URL + "/user/getNewFriendRequests/"+owner.getUsername();
                    String jsonStr= OkHttpUtil.synGet(url);
                    getNewFriendResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getNewFriendResponse(final String jsonStr){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")) {
                    //进行本地展示即可，不进行存储
                    JSONArray newFriends = (JSONArray) jsonMap.get("data");
                    for (int i = 0; i < newFriends.size(); i++) {
                        JSONObject jsonObj = (JSONObject) newFriends.get(i);
                        Friend friend = new Friend();
                        friend.setName(jsonObj.getString("name"));
                        friend.setImageUrl("E:\\websocketchat\\android\\doboto-chat-web-external\\UserHeading\\" + jsonObj.getString("imagePath"));
                        friend.setNetId(jsonObj.getIntValue("id"));
                        newFriendList.add(friend);
                    }
                }
                if (newFriendList.size()>0){
                    hasNewFriendRequestImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    //同步好友信息到本地数据库
    //当对方同意了你的好友请求，对方的本地数据库会进行同步，但你的本地数据库没有同步，这时候我该在什么时候进行好友列表的同步呢？
    //当我应用关闭后，重新打开应用时进行数据同步，或者应用正在运行中通过主动推送进行同步。
    //开启聊天之前会进行一个好友验证，如果对方将你删除了，那么不能开启聊天

    //在Adapter中请求后进行界面更新
}
