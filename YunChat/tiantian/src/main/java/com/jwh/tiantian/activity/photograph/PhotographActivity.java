package com.jwh.tiantian.activity.photograph;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


import com.activity.R;
import com.activity.R.id;
import com.activity.R.layout;
import com.jwh.tiantian.activity.BaseActivity;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

public class PhotographActivity extends BaseActivity implements
		SeekBar.OnSeekBarChangeListener, OnClickListener, Runnable {
//	static final int INT_CAPTER = 0;
//	static final int INT_VIDEO = 1;
//	static final int INT_TOPCAPTER = 2;
//	static final int INT_BACKCAPTER = 3;
	private SeekBar zoomSet;// 调整焦距
	private ImageView takePic, back, flash;// 按钮
//	private CheckBox videoChange;
	int flashState;//闪光灯状态
	final int FLASH_AUTO = 0;//自动闪光
	final int FLASH_OFF = 1;//闪光灯关闭
	final int FLASH_ON = 2;	//闪光灯打开
	//闪光灯图标
	int flashImg[] = {R.drawable.light_auto, R.drawable.light_off, R.drawable.light_on};
	private int zoom, maxZoom;// zoom现在焦距，maxZoom最大焦距
	private boolean preview = false, flashOpen = false;// preview预览 flashOpen闪光灯
	private Camera camera;// 硬件相机
	private SurfaceView cameraBG;// 相机预览
	private float windowH, windowW;//窗口宽高
	PopupWindow popupWindow;

	Camera.Parameters parameters;//照相机参数集

	Button RecButton;
	Button VideoButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(PhotographActivity.this, "SD卡没有插入或不能读写",
					Toast.LENGTH_LONG).show();
		}
		Window window = getWindow();
		window.addFlags(
				WindowManager.LayoutParams.
				FLAG_KEEP_SCREEN_ON);// 高亮
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
		getWindow().setFormat(
				PixelFormat.TRANSLUCENT);// 选择支持半透明模式,在有surfaceview的activity中使用。
		super.onCreate(savedInstanceState);
		setContentView(layout.photograph);
		init();
//		new Thread(this).start();
	}

	private void init() {
		// TODO Auto-generated method stub
		takePic = (ImageView) findViewById(id.btn_picture);
//		menu = (Button) findViewById(R.id.btn_menu);
		back = (ImageView) findViewById(id.btn_backcapter);
		flash = (ImageView) findViewById(id.btn_flash);
		zoomSet = (SeekBar) findViewById(id.seekbar_focal);
		cameraBG = (SurfaceView) findViewById(id.camrea);
		windowW = this.getWindowManager().getDefaultDisplay().getWidth();
		windowH = this.getWindowManager().getDefaultDisplay().getHeight();
		cameraBG.getHolder().setFixedSize((int) windowW, (int) windowH);
		cameraBG.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		cameraBG.getHolder().addCallback(new SurfaceCallback());
		zoomSet.setOnSeekBarChangeListener(this);
		takePic.setOnClickListener(this);
		back.setOnClickListener(this);
		flash.setOnClickListener(this);
		cameraBG.setOnClickListener(this);

		RecButton = (Button) findViewById(id.button_rec);
		VideoButton = (Button) findViewById(id.button_video);
		RecButton.setOnClickListener(this);
		VideoButton.setOnClickListener(this);
	}

	private final class SurfaceCallback implements SurfaceHolder.Callback {

		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				camera.release();// 相机释放
			}
			camera = Camera.open();// 打开相机
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay();
			parameters = camera.getParameters();// 获取相机参数集
			if (parameters.getFlashMode() == null){
				flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), flashImg[FLASH_OFF]));
				Toast.makeText(PhotographActivity.this, "无闪光灯", Toast.LENGTH_SHORT)
				.show();
			}else{
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				camera.setParameters(parameters);
			}
			maxZoom = parameters.getMaxZoom();// 获取最大像素
			zoomSet.setMax(maxZoom);// 设置最大像素
			try {
				camera.setPreviewDisplay(cameraBG.getHolder());// 设置相机预览
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camera.startPreview();// 开始预览
			preview = true;

		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				if (preview)
					camera.stopPreview();
				camera.release();
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (camera != null && event.getRepeatCount() == 0) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_CAMERA:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				takPicture();
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				zoom = (zoom + 5) < maxZoom ? (zoom + 5) : maxZoom;
				zoomSet.setProgress(zoom);
				zoomChanged(zoom);
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				zoom = (zoom - 5) > 0 ? (zoom - 5) : 0;
				zoomSet.setProgress(zoom);
				zoomChanged(zoom);
				break;
			case KeyEvent.KEYCODE_BACK:
				if (flashOpen) {
					popupWindow.dismiss();
				} else {
					finish();
				}
			}
		}
		return true;
	}

	private final class TakePictureCallback implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.v("TestFile", "没有SD卡或SD卡不可用！");
				Toast.makeText(PhotographActivity.this, "没有SD卡或SD卡不可用！",
						Toast.LENGTH_LONG).show();
				return;
			} else {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				new DateFormat();
				String name = DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
				File file = new File("sdcard/PhoneHelper/Picture/");
				file.mkdirs();// 创建文件夹
				String fileName = "/mnt/sdcard/PhoneHelper/Picture/" + name;
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							fileName));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 把数据写入文件
					ConfirmActivity.photo_name = fileName;
					Intent intent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					Uri imageUri = Uri.fromFile(new File(fileName));
					intent.setData(imageUri);
					Intent starEdit = new Intent(PhotographActivity.this,
							ConfirmActivity.class);
					starEdit.setData(imageUri);
					startActivity(starEdit);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						// bos.flush();
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.getMessage();
					}
				}
			}
		}
	}

	public void takPicture() {
		camera.autoFocus(null);// 自动对焦
		camera.takePicture(null, null, new TakePictureCallback());
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		zoom = zoomSet.getProgress();
		zoomChanged(zoom);
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	private void zoomChanged(int zoom) {
		Camera.Parameters parameters = camera.getParameters();
		parameters.setZoom(zoom);
		camera.setParameters(parameters);
	}

	boolean recing;
	boolean videoing;
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case id.button_rec:
			if(videoing){
				Toast.makeText(this, "录像中，此按钮无效", Toast.LENGTH_LONG).show();
				return;
			}
			if(recing){
				RecButton.setText("开始录音");
				stopRec();
			}else{
				RecButton.setText("停止录音");
				startRec();
			}
			recing = !recing;
			break;
		case id.button_video:
			if(recing){
				Toast.makeText(this, "录音中，此按钮无效", Toast.LENGTH_LONG).show();
				return;
			}else{
				skipToVideo();
				finish();
			}
			videoing = !videoing;
			break;
		case id.btn_picture:
			takPicture();
			break;
//		case R.id.check_change:
			// CameraInfo info = new CameraInfo();
			// for (int i = 0; i < cameraNum; i++) {
			// Camera.getCameraInfo(i, info);
			// if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
			// cameraId = i;
			// }
			// }
			// int cameraCurrentId = cameraId;
			// if (camera != null) {
			// camera.release();
			// camera = null;
			// }
			// camera = Camera.open((cameraCurrentId + 1) % cameraNum);
			// cameraCurrentId = (cameraCurrentId + 1) % cameraNum;
			// camera.startPreview();
//			break;
		case id.btn_backcapter:
			if (flashOpen) {
				popupWindow.dismiss();
			} else {
				finish();
			}
			break;
		case id.btn_flash:
//			changeFlashMode();
			if (parameters.getFlashMode() == null){
				Toast.makeText(PhotographActivity.this, "无闪光灯", Toast.LENGTH_SHORT).show();
			}else if(parameters.getFlashMode() == Camera.Parameters.FLASH_MODE_AUTO){
				flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), flashImg[FLASH_OFF]));
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(parameters);
			}else if(parameters.getFlashMode() == Camera.Parameters.FLASH_MODE_OFF){
				flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), flashImg[FLASH_ON]));
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				camera.setParameters(parameters);
			}else if(parameters.getFlashMode() == Camera.Parameters.FLASH_MODE_ON){
				flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), flashImg[FLASH_AUTO]));
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				camera.setParameters(parameters);
			}
			break;
		case id.camrea:
				camera.autoFocus(null);
				break;
		}
	}

	private void skipToVideo() {
		Intent vedioIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		vedioIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		startActivityForResult(vedioIntent, 1);
		PhotographActivity.this.finish();
	}
	
	MediaRecorder mr;
	String recFileName;
	private void stopRec() {
		if(mr!=null){
			mr.stop();
			mr.release();
			mr = null;
			System.gc();
			
			Toast.makeText(getApplicationContext(), recFileName + "已保存", Toast.LENGTH_LONG);
		}
	}

	private void startRec() {
		mr = new MediaRecorder();//创建MediaRecorder类对象
		//设置录音源，从mic获得声音
		mr.setAudioSource(MediaRecorder.AudioSource.MIC);
		//录音格式为3GP
		mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		//设置编码格式
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		String path = "/mnt/sdcard/mediatest/";
		new DateFormat();
		String name = DateFormat.format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA)) + "recorder.3gp";
		File file = new File(path);
		file.mkdirs();// 创建文件夹
		file = new File(path+name);
		recFileName = path+name;
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!Environment.getExternalStorageState().
		equals(Environment.MEDIA_MOUNTED)){
		System.out.println("没有安装SD卡，不能进行录音。");
		return;
		}//存档前应检测路径是否存在，是否可读写
		mr.setOutputFile(path+name);//将路径设置给MediaRecorder

		try {
			mr.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mr.start();
	}

	public void run() {
		while(true){
			if (camera != null) {
				camera.autoFocus(null);
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

	File myVideoPATH;
	File myVideo;
	String videoName;
	private Uri videoFilePath;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				try {
					videoFilePath = data.getData();
					Cursor pathCursor = managedQuery(videoFilePath,
							new String[] { MediaColumns.DATA },
							null, null, null);
					pathCursor.moveToFirst();
					String path = pathCursor
							.getString(pathCursor
									.getColumnIndexOrThrow(MediaColumns.DATA));
					File videoFile = new File(path);// 视频源文件
					new DateFormat();
					videoName = DateFormat.format("yyyyMMdd_hhmmss",
							Calendar.getInstance(Locale.CHINA))
							+ ".3gp";
					File file = new File("mnt/sdcard/mediatest/video/");
					file.mkdir();
					System.out.println("123");
					myVideo = new File("mnt/sdcard/mediatest/video/"
							+ videoName);// 保存视频路径+名称
					FileInputStream fis = new FileInputStream(videoFile);
					FileOutputStream outStream = new FileOutputStream(myVideo);
					BufferedInputStream bis = new BufferedInputStream(fis);
					BufferedOutputStream bos = new BufferedOutputStream(
							outStream);
					byte[] bt = new byte[8192];
					int len = bis.read(bt);
					while (len != -1) {
						bos.write(bt, 0, len);
						len = bis.read(bt);
					}
					System.out.println("ok");
					bis.close();
					bos.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.putExtra("return", videoName + ".3gp");
				setResult(RESULT_OK, intent);
				Toast.makeText(PhotographActivity.this, "视频存储成功",
						Toast.LENGTH_LONG).show();
				this.finish();
			} else {
				Toast.makeText(PhotographActivity.this, "视频录制失败",
						Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
	}
}

