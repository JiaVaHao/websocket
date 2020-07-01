package com.jwh.tiantian.activity.telephone_list;

import com.activity.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews.ActionException;
import android.widget.Toast;

public class SendMessage extends Activity {
private Button button;
private EditText etTel;
private EditText etContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendmessage);
		button=(Button)findViewById(R.id.button);
		etTel=(EditText)findViewById(R.id.etext);
		Intent intent=this.getIntent();
		
		etTel.setText(intent.getStringExtra("tel"));
		etContent=(EditText)findViewById(R.id.ftext);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String strTel=etTel.getText().toString();
				String strContent=etContent.getText().toString();
				if(PhoneNumberUtils.isGlobalPhoneNumber(strTel)){
					button.setEnabled(false);
					System.out.println("strTel "+strTel);
					sendSMS(strTel,strContent,v);
				}else{
					Toast.makeText(SendMessage.this, "输入的电话号码不符合格式", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
public void sendSMS(String telephoneNo,String smsContent,View v){
	PendingIntent pintent=PendingIntent.getActivity(this, 0, new Intent(), 0);
	SmsManager sms=SmsManager.getDefault();
	sms.sendTextMessage(
			telephoneNo, 
			null,
			smsContent, 
			pintent,
			null);
	Toast.makeText(SendMessage.this, "恭喜，短信发送成功!!", Toast.LENGTH_LONG).show();
}
}
