package com.jwh.tiantian.activity;

import com.activity.R;
import com.jwh.tiantian.activity.alarm.ClockActivity;
import com.jwh.tiantian.activity.battery.BatteryActivity;
import com.jwh.tiantian.activity.hardmanager.DriverActivity;
import com.jwh.tiantian.activity.photograph.PhotographActivity;
import com.jwh.tiantian.activity.softmanager.SoftManagementActivity;
import com.jwh.tiantian.activity.telephone_list.Main;
import com.jwh.tiantian.db.ContactsData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends BaseActivity implements OnClickListener, OnLongClickListener,
        Runnable {

    Vibrator vb;
    long vbTime[] = {100, 400, 100, 400};
    ImageView teleLine;// 通讯录
    ImageView batterLine;// 电池信息
    ImageView cameraLine;// 照相机
    ImageView softLine;// 软件管理
    ImageView hardLine;// 硬件信息
    ImageView clockLine;// 闹钟
    ImageView chatLine;// 聊天

    ImageView selView;//选中按钮
    int oldsel;//选中按钮ID
    //ID对应的选中图标
    int selId[] = {R.drawable.menu_icon_0_1, R.drawable.menu_icon_1_1, R.drawable.menu_icon_2_1, R.drawable.menu_icon_3_1, R.drawable.menu_icon_4_1, R.drawable.menu_icon_5_1, R.drawable.menu_icon_6_0};
    //ID对应的未选中图标
    int seledId[] = {R.drawable.menu_icon_0_0, R.drawable.menu_icon_1_0, R.drawable.menu_icon_2_0, R.drawable.menu_icon_3_0, R.drawable.menu_icon_4_0, R.drawable.menu_icon_5_0, R.drawable.menu_icon_6_0};
    //按钮信息文字
    int selText[] = {R.string.menu_telelist, R.string.menu_soft, R.string.menu_hard, R.string.menu_battery, R.string.menu_clock, R.string.menu_camera, R.string.menu_chat};
    Bitmap selImg[], seledImg[];//选中图片和未选中图片
    TextView intro;//文字信息

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        init();// 初始化控件
    }

    private void init() {// 获得布局中控件并设置监听
        intro = (TextView) findViewById(R.id.menu_text);

        teleLine = (ImageView) findViewById(R.id.menu_icon0);
        batterLine = (ImageView) findViewById(R.id.menu_icon3);
        cameraLine = (ImageView) findViewById(R.id.menu_icon5);
        softLine = (ImageView) findViewById(R.id.menu_icon1);
        hardLine = (ImageView) findViewById(R.id.menu_icon2);
        clockLine = (ImageView) findViewById(R.id.menu_icon4);
        chatLine = (ImageView) findViewById(R.id.menu_icon6);

        teleLine.setOnClickListener(this);
        batterLine.setOnClickListener(this);
        cameraLine.setOnClickListener(this);
        softLine.setOnClickListener(this);
        hardLine.setOnClickListener(this);
        clockLine.setOnClickListener(this);
        chatLine.setOnClickListener(this);

        teleLine.setOnLongClickListener(this);
        batterLine.setOnLongClickListener(this);
        cameraLine.setOnLongClickListener(this);
        softLine.setOnLongClickListener(this);
        hardLine.setOnLongClickListener(this);
        clockLine.setOnLongClickListener(this);
        chatLine.setOnLongClickListener(this);

        selImg = new Bitmap[6];
        for (int i = 0; i < selImg.length; i++) {
            selImg[i] = BitmapFactory.decodeResource(getResources(), selId[i]);
        }

        seledImg = new Bitmap[6];
        for (int i = 0; i < seledImg.length; i++) {
            seledImg[i] = BitmapFactory.decodeResource(getResources(), seledId[i]);
        }

        vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    public void reSel(ImageView v, int newid) {//替换选项按钮图片
        if (selView != null) selView.setImageBitmap(seledImg[oldsel]);
        selView = v;
        oldsel = newid;
        selView.setImageBitmap(selImg[oldsel]);
        intro.setText(selText[oldsel]);
    }

    public void onClick(View v) {// 点击跳转到不同功能主界面
        int t = v.getId();
        System.out.println("on click " + t);
        switch (v.getId()) {
            case R.id.menu_icon0:
                reSel(teleLine, 0);
                break;
            case R.id.menu_icon3:
                reSel(batterLine, 3);
                break;
            case R.id.menu_icon5:
                reSel(cameraLine, 5);
                break;
            case R.id.menu_icon1:
                reSel(softLine, 1);
                break;
            case R.id.menu_icon2:
                reSel(hardLine, 2);
                break;
            case R.id.menu_icon4:
                reSel(clockLine, 4);
                break;
            case R.id.menu_icon6:
                reSel(chatLine, 6);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        ContactsData helper = new ContactsData(this);// 获得所有用户的list
        helper.openDatabase(); // 打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        if (helper.needFresh()) {//检查通讯录是否有变化
            helper.openContacts();//获得通讯录信息
        }
        Intent telephone = new Intent(this, Main.class);
        startActivity(telephone);
    }

    @Override
    public boolean onLongClick(View v) {
        vb.vibrate(500);
        int t = v.getId();
        System.out.println("on long click " + t);
        switch (v.getId()) {
            case R.id.menu_icon0:
                reSel(teleLine, 0);
                showProgressDialog("加载中请稍等……");
                new Thread(this).start();
                return true;
            case R.id.menu_icon3:
                reSel(batterLine, 3);
                Intent battery = new Intent(this, BatteryActivity.class);
                startActivity(battery);
                return true;
            case R.id.menu_icon5:
                reSel(cameraLine, 5);
                Intent photo = new Intent(this, PhotographActivity.class);
                startActivity(photo);
                return true;
            case R.id.menu_icon1:
                reSel(softLine, 1);
                Intent soft = new Intent(this, SoftManagementActivity.class);
                startActivity(soft);
                return true;
            case R.id.menu_icon2:
                reSel(hardLine, 2);
                Intent hard = new Intent(this, DriverActivity.class);
                startActivity(hard);
                return true;
            case R.id.menu_icon4:
                reSel(clockLine, 4);
                Intent clock = new Intent(this, ClockActivity.class);
                startActivity(clock);
                return true;
            case R.id.menu_icon6:
                reSel(chatLine, 6);
                Intent chat = new Intent(this, ClockActivity.class);
                startActivity(chat);
                return true;
        }
        return false;
    }
}
