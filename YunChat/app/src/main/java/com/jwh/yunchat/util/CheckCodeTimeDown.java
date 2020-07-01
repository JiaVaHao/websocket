package com.jwh.yunchat.util;

import android.os.CountDownTimer;
import android.widget.Button;

import com.jwh.yunchat.R;

public class CheckCodeTimeDown extends CountDownTimer {

    //倒计时绑定的view
    private Button btn;

    public CheckCodeTimeDown(Button btn, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.btn=btn;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        //view设置不可点击
        btn.setClickable(false);
        btn.setText(millisUntilFinished / 1000 + "s");
        //设置背景颜色为灰色
        btn.setBackgroundResource(R.drawable.btn_unable);

    }

    @Override
    public void onFinish() {
        btn.setText("获取验证码");
        btn.setClickable(true);//重新获得点击
        btn.setBackgroundResource(R.drawable.btn2);  //还原背景色
    }
}
