package com.jwh.tiantian.activity.photograph;

import java.io.File;

import com.activity.R;
import com.activity.R.id;
import com.activity.R.layout;
import com.jwh.tiantian.activity.BaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ConfirmActivity extends BaseActivity {

	ImageView photo;//照片
	ImageView save;//保存按钮
	ImageView del;//删除按钮
	
	public static String photo_name;//照片保存地址 
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(layout.confirm);
        showPic();
        
        save = (ImageView)findViewById(id.btn_picsave);
        del = (ImageView)findViewById(id.btn_picdel);
        
        save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(ConfirmActivity.this, photo_name + "已保存。",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
        
        del.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				File delphoto = new File(photo_name);
				delphoto.delete();
				Toast.makeText(ConfirmActivity.this, photo_name + "已删除",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
        Toast.makeText(ConfirmActivity.this, "是否保存？", Toast.LENGTH_SHORT).show();
	}

    private void showPic() {
    	photo = (ImageView)findViewById(id.temp_photo);
		Intent picture = getIntent();
		Uri originalUri = picture.getData();
		photo.setImageURI(originalUri);
	}
}
