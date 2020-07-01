package com.jwh.yunchat.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jwh.yunchat.R;

public class Title extends LinearLayout {

    public Title(final Context context){
        super(context);
    }

    public Title(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //通过反射注册
        LayoutInflater.from(context).inflate(R.layout.title,this);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"xxx",Toast.LENGTH_SHORT);
                ((Activity)getContext()).finish();
            }
        });
    }

}
