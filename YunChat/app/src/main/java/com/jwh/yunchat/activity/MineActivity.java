package com.jwh.yunchat.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jwh.yunchat.R;
import com.jwh.yunchat.util.GlideBlurTransformation;

public class MineActivity extends BaseActivity {

    private ImageView myImage;

    private RelativeLayout myImageBg;

    private TextView myName;

    private TextView titleText;
    private ImageView hasNew;

    private LinearLayout editMyself;
    private LinearLayout update_image;
    private LinearLayout support;
    private LinearLayout account;
    private LinearLayout logout;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);

        titleText=findViewById(R.id.title_text);
        titleText.setText("个人中心");
        ImageView back=findViewById(R.id.back);
        back.setVisibility(View.GONE);
        hasNew=findViewById(R.id.has_new_message);
        hasNew.setVisibility(View.GONE);

        //设置背景图片
        myImage=findViewById(R.id.my_image);
        myImageBg=findViewById(R.id.my_image_bg);
        myName=findViewById(R.id.my_name);
        //功能
        editMyself=findViewById(R.id.edit_myself);
        update_image=findViewById(R.id.update_image);
        support=findViewById(R.id.support);
        account=findViewById(R.id.account);
        logout=findViewById(R.id.logout);

        myName.setText(owner.getName());

        SimpleTarget bgTarget=new SimpleTarget() {
            @Override
            public void onResourceReady(Object resource, Transition transition) {
                myImageBg.setBackground((Drawable)resource);
            }
        };
        //设置背景图
        Glide.with(MineActivity.this)
                .load(owner.getImageURL())
                .apply(RequestOptions.bitmapTransform(new GlideBlurTransformation(this)))
                .into(bgTarget);
        //设置圆角图
        Glide.with(MineActivity.this)
                .load(owner.getImageURL())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(myImage);

        //功能点击监听
        editMyself.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editMyselfPanel();
                return false;
            }
        });
        update_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MineActivity.this,"上传图片",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        support.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getSupport();
                return false;
            }
        });
        account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MineActivity.this,"账户安全",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                prefEditor.remove("ownerNetId");
                prefEditor.commit();
                Intent intent=new Intent("FORCE_OFFLINE");
                intent.putExtra("warringMsg","您将会退出轻聊");
                sendBroadcast(intent);
                return false;
            }
        });

    }

    private EditText myNewName;
    private EditText myNewAge;
    private EditText myNewCity;
    public void editMyselfPanel(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MineActivity.this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.edit_myself,null,false);
        dialog.setView(view);
        dialog.setTitle("修改个人信息");
        myNewName = view.findViewById(R.id.my_new_name);
        myNewAge = view.findViewById(R.id.my_new_name);
        myNewCity = view.findViewById(R.id.my_new_name);
        dialog.setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MineActivity.this,"我不打算实现了",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void getSupport(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MineActivity.this);
        dialog.setMessage("QQ:1577089031");
        dialog.show();
    }
}
