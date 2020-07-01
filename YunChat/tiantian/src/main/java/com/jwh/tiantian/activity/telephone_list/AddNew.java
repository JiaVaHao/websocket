package com.jwh.tiantian.activity.telephone_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.activity.R;
import com.jwh.tiantian.bean.User;
import com.jwh.tiantian.db.ContactsData;

public class AddNew extends Activity implements ViewFactory {

	EditText et_name;
	EditText et_mobilePhone;
	EditText et_officePhone;
	EditText et_familyPhone;
	EditText et_position;
	EditText et_company;
	EditText et_address;
	EditText et_zipCode;
	EditText et_otherContact;
	EditText et_email;
	EditText et_remark;
	Button btn_save;
	Button btn_return;

	int privacy;// 用于判断添加的用户是不是保密的
	ImageButton imageButton;// 头像按钮
	View imageChooseView;// 图像选择的视图
	AlertDialog imageChooseDialog;// 头像选择对话框
	Gallery gallery;// 头像的Gallery
	ImageSwitcher is;// 头像的ImageSwitcher
	int currentImagePosition;// 用于记录当前选中图像在图像数组中的位置
	int previousImagePosition;// 用于记录上一次图片的位置
	boolean imageChanged;// 判断头像有没有变化
	/**
	 * 所有的图像图片
	 */
	private int[] images = new int[] {R.drawable.image1,
			R.drawable.image2, R.drawable.image3, R.drawable.image4,
			R.drawable.image5, R.drawable.image6, R.drawable.image7,
			R.drawable.image8, R.drawable.image9, R.drawable.image10,
			R.drawable.image11, R.drawable.image12, R.drawable.image13,
			R.drawable.image14, R.drawable.image15, R.drawable.image16,
			R.drawable.image17, R.drawable.image18, R.drawable.image19,
			R.drawable.image20, R.drawable.image21, R.drawable.image22,
			R.drawable.image23, R.drawable.image24, R.drawable.image25,
			R.drawable.image26, R.drawable.image27, R.drawable.image28,
			R.drawable.image29, R.drawable.image30 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew);
		Intent intent = getIntent();
		// 先确定好保密类型
		if (intent.getExtras() != null
				&& intent.getExtras().getInt("privacy") == 1) {
			privacy = 1;
		} else {
			privacy = 0;
		}

		et_name = (EditText) findViewById(R.id.username);
		et_mobilePhone = (EditText) findViewById(R.id.mobilephone);
		et_officePhone = (EditText) findViewById(R.id.officephone);
		et_familyPhone = (EditText) findViewById(R.id.familyphone);
		et_position = (EditText) findViewById(R.id.position);
		et_company = (EditText) findViewById(R.id.company);
		et_address = (EditText) findViewById(R.id.address);
		et_zipCode = (EditText) findViewById(R.id.zipcode);
		et_otherContact = (EditText) findViewById(R.id.othercontact);
		et_email = (EditText) findViewById(R.id.email);
		et_remark = (EditText) findViewById(R.id.remark);

		btn_save = (Button) findViewById(R.id.save);
		btn_return = (Button) findViewById(R.id.btn_return);
		imageButton = (ImageButton) findViewById(R.id.image_button);

		/**
		 * 响应点击事件
		 */
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断姓名是否为空
				String name = et_name.getText().toString();
				if (name.trim().equals("")) {
					Toast.makeText(AddNew.this, "姓名不许为空", Toast.LENGTH_LONG)
							.show();
					return;
				}
				// 从表单上获取数据
				User user = new User();
				user.username = name;
				user.address = et_address.getText().toString();
				user.company = et_company.getText().toString();
				user.email = et_email.getText().toString();
				user.familyPhone = et_familyPhone.getText().toString();
				user.mobilePhone = et_mobilePhone.getText().toString();
				user.officePhone = et_officePhone.getText().toString();
				user.otherContact = et_otherContact.getText().toString();
				user.position = et_position.getText().toString();
				user.remark = et_remark.getText().toString();
				user.zipCode = et_zipCode.getText().toString();
				// 判断头像是否改变，若改变，则用当前的位置，若没有改变，则用前一回的位置
				if (imageChanged) {
					user.imageId = images[currentImagePosition % images.length];
				} else {
					user.imageId = images[previousImagePosition % images.length];
				}
				user.privacy = privacy;
				// 创建数据库帮助类
				ContactsData helper = new ContactsData(AddNew.this);
				// 打开数据库
				helper.openDatabase();
				// 把user存储到数据库里
				long result = helper.insertSQL(user);

				// 通过结果来判断是否插入成功，若为1，则表示插入数据失败
				if (result == -1) {
					Toast.makeText(AddNew.this, "添加失败", Toast.LENGTH_LONG);
				}
				try {
					helper.addContact(user);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setTitle("用户添加成功!");
				// 返回到上一个Activity，也就是Main.activity
				setResult(3);
				// 销毁当前视图
				finish();
			}

		});

		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadImage();// 为gallery装载图片
				initImageChooseDialog();// 初始化imageChooseDialog
				imageChooseDialog.show();
			}
		});

	}

	public void loadImage() {
		if (imageChooseView == null) {
			LayoutInflater li = this.getLayoutInflater();
			imageChooseView = li.inflate(R.layout.imageswitch, null);
			// 通过渲染xml文件，得到一个视图（View），再拿到这个View里面的Gallery
			gallery = (Gallery) imageChooseView.findViewById(R.id.gallery);
			// 为Gallery装载图片
			gallery.setAdapter(new ImageAdapter(this));
			gallery.setSelection(images.length / 2);
			is = (ImageSwitcher) imageChooseView.findViewById(R.id.imageswitch);
			is.setFactory(this);
			is.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			// 卸载图片的动画效果
			is.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));

			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// 当前的头像位置为选中的位置
					currentImagePosition = arg2;
					// 为ImageSwitcher设置图像
					is.setImageResource(images[arg2 % images.length]);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

	}

	/**
	 * 自定义Gallery的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class ImageAdapter extends BaseAdapter {

		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * gallery从这个方法中拿到image
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(images[position % images.length]);
			iv.setLayoutParams(new Gallery.LayoutParams(80, 80));
			iv.setPadding(15, 10, 15, 10);
			return iv;

		}

	}

	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setBackgroundColor(0xff000000);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new ImageSwitcher.LayoutParams(90, 90));
		return view;
	}

	public void initImageChooseDialog() {
		if (imageChooseDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择图像").setView(imageChooseView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									imageChanged = true;
									previousImagePosition = currentImagePosition;
									imageButton
											.setImageResource(images[currentImagePosition
													% images.length]);
								}
							}).setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									currentImagePosition = previousImagePosition;

								}
							});
			imageChooseDialog = builder.create();
		}
	}

	/**
	 * 当退出的时候，回收资源
	 */
	@Override
	protected void onDestroy() {
		if (is != null) {
			is = null;
		}
		if (gallery != null) {
			gallery = null;
		}
		if (imageChooseDialog != null) {
			imageChooseDialog = null;
		}
		if (imageChooseView != null) {
			imageChooseView = null;
		}
		if (imageButton != null) {
			imageButton = null;
		}

		super.onDestroy();
	}
}
