package com.jwh.yunchat.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwh.yunchat.R;
import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.activity.LoginActivity;
import com.jwh.yunchat.adapter.SearchLocalFriendListAdapter;
import com.jwh.yunchat.adapter.SearchNetFriendListAdapter;
import com.jwh.yunchat.entity.Friend;
import com.jwh.yunchat.service.FriendService;
import com.jwh.yunchat.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.Map;

public class SearchFriendFragment extends Fragment {

    private TextView returnBtn;
    private RecyclerView localFriendList;
    private RecyclerView netFriendList;
    private EditText searchFriend;
    private ImageView searchBtn;
    private SearchLocalFriendListAdapter localAdapter;
    private SearchNetFriendListAdapter netAdapter;
    private FriendService friendService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search_friend,container,false);
        friendService=new FriendService();
        returnBtn=view.findViewById(R.id.return_friend_list);
        localFriendList=view.findViewById(R.id.local_friend_list);
        netFriendList=view.findViewById(R.id.net_friend_list);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view1=getActivity().findViewById(R.id.search_friend_panel);
                view1.setVisibility(View.GONE);
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.remove(SearchFriendFragment.this);
                fragmentTransaction.commit();
            }
        });

        searchFriend=view.findViewById(R.id.search_friend);
        searchBtn=view.findViewById(R.id.search_friend_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword=searchFriend.getText().toString();
                //搜索网络
                searchFriendByKeywords(keyword);
                //搜索本地
                ArrayList<Friend> localFriends=friendService.findLocalByKeyword(keyword,BaseActivity.owner.getNetId());
                if (localFriends.size()!=0){
                    localAdapter=new SearchLocalFriendListAdapter(container.getContext(),localFriends, BaseActivity.owner);
                    localFriendList.setAdapter(localAdapter);
                    localFriendList.setLayoutManager(new LinearLayoutManager(getActivity()));
                }

            }
        });
        return view;
    }
    //获取好友请求
    private void searchFriendByKeywords(final String keywords){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = LoginActivity.SERVER_URL + "/user/searchUser/"+keywords;
                    String jsonStr= OkHttpUtil.synGet(url);
                    searchFriendResponse(jsonStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void searchFriendResponse(final String jsonStr){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map jsonMap= JSON.parseObject(jsonStr);
                final String message=(String) jsonMap.get("message");
                if (message.equals("success")){
                    //进行本地存储
                    Log.d("a",jsonStr);
                    ArrayList<Friend> netFriends=new ArrayList<>();
                    //进行本地消息存储
                    JSONObject data=(JSONObject) jsonMap.get("data");
                    Friend friend=new Friend();
                    friend.setImageUrl("E:\\websocketchat\\android\\doboto-chat-web-external\\UserHeading\\"+data.getString("imagePath"));
                    friend.setName(data.getString("name"));
                    friend.setNetId(data.getIntValue("id"));
                    netFriends.add(friend);
                    netAdapter=new SearchNetFriendListAdapter(getActivity(),netFriends, BaseActivity.owner);
                    netFriendList.setAdapter(netAdapter);
                    netFriendList.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                else {
                    Toast.makeText(getContext(),"网络中查不到该用户",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
