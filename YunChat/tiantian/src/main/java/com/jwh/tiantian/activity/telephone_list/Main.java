package com.jwh.tiantian.activity.telephone_list;

import java.util.ArrayList;
import java.util.HashMap;

import com.activity.R;
import com.jwh.tiantian.activity.BaseActivity;
import com.jwh.tiantian.bean.User;
import com.jwh.tiantian.db.ContactsData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Main extends Activity {

	// 显示所有数据的ListView
	ListView lv;

	ArrayList list;

	// 拥有所有数据的Adapter
	SimpleAdapter adapter;
	// 屏幕下方的工具栏
	GridView bottomMenuGrid;
	// 主菜单的布局
	GridView mainMenuGrid;
	// 主菜单的视图
	View mainMenuView;
	// 登录的视图
	View loginView;

	// 装搜索框的linearlayout,默认情况下visibility=gone
	LinearLayout searchLinearout;
	LinearLayout mainLinearLayout;
	// 搜索框
	EditText et_search;
	EditText et_enter_file_name;

	// 主菜单的对话框
	AlertDialog mainMenuDialog;
	// 确认对话框
	AlertDialog confirmDialog;
	// 进度条对话框
	AlertDialog progressDialog;
	// 输入文件名的对话框
	AlertDialog enterFileNameDialog;
	// 输入用户名密码的对话框
	AlertDialog loginDialog;
	// 表示保密状态
	boolean privacy = false;
	// 存储标记的数目
	int markedNum;
	// 存储标记条目的_id号
	ArrayList<Integer> deleteId;
	// 菜单文字
	String[] main_menu_itemName = { "显示所有", "删除所有", "后退" };
	// 主菜单图片
	int[] main_menu_itemSource = { R.drawable.showall, R.drawable.menu_delete1,

	R.drawable.menu_return };

	String[] bottom_menu_itemName = { "增加", "查找", "通讯", "菜单", "退出" };
	String fileName;
	int[] bottom_menu_itemSource = { R.drawable.menu_new_user,
			R.drawable.menu_search, R.drawable.menu_delete,
			R.drawable.controlbar_showtype_list, R.drawable.menu_exit };

	/**
	 * onCreate做的工作就是把listView显示出来
	 * bottomMenuGrid，mainMenuGrid，searchLinearout都是到要用 的时候再初始化，并且只初始化一次
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mam.xml采用相对布局，listview,edittext,menu
		setContentView(R.layout.main_telephone_list);
		// setTitleColor(0xff46b695);

		// 得到用于存放listView的linearLayout
		mainLinearLayout = (LinearLayout) findViewById(R.id.list_ll);
		ContactsData helper = new ContactsData(this);// 获得所有用户的list
		helper.openDatabase(); // 打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
		list = helper.getAllUser(privacy);// 拿到所有保密状态为privacy的用户的list
		lv = (ListView) findViewById(R.id.lv_userlist); // 创建ListView对象
		lv.setCacheColorHint(Color.TRANSPARENT);
		// 当数据库中没有数据的时候，显示一个提示图片
		if (list.size() == 0) {
			Drawable nodata_bg = getResources().getDrawable(
					R.drawable.contact_bg);
			mainLinearLayout.setBackgroundDrawable(nodata_bg);
			setTitle("没有查到任何数据");

		}
		// 将数据与adapter集合起来
		adapter = new SimpleAdapter(this, list, R.layout.listitem,
				new String[] { "imageid", "name", "mobilephone" }, new int[] {
						R.id.user_image, R.id.tv_name, R.id.tv_mobilephone });

		lv.setAdapter(adapter);// 将整合好的adapter交给listview，显示给用户看

		lv.setOnItemClickListener(new OnItemClickListener() {
			/**
			 * 响应单击事件，单点击某一个选项的时候，跳转到用户详细信息页面
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int postion,
					long arg3) {
				HashMap item = (HashMap) adapter.getItemAtPosition(postion);
				int _id = Integer.parseInt(String.valueOf(item.get("_id")));
				Intent intent = new Intent(Main.this, UserDetail.class);
				User user = new User();
				user._id = Integer.parseInt(String.valueOf(item.get("_id")));
				user.address = String.valueOf(item.get("address"));
				user.company = String.valueOf(item.get("company"));
				user.email = String.valueOf(item.get("email"));
				user.familyPhone = String.valueOf(item.get("familyphone"));
				user.mobilePhone = String.valueOf(item.get("mobilephone"));
				user.officePhone = String.valueOf(item.get("officephone"));
				user.otherContact = String.valueOf(item.get("othercontact"));
				user.position = String.valueOf(item.get("position"));
				user.remark = String.valueOf(item.get("remark"));
				user.username = String.valueOf(item.get("name"));
				user.zipCode = String.valueOf(item.get("zipcode"));
				user.imageId = Integer.parseInt(String.valueOf(item
						.get("imageid")));
				user.contactid = Integer.parseInt(String.valueOf(item
						.get("contactid")));
				intent.putExtra("user", user);
				if (searchLinearout != null
						&& searchLinearout.getVisibility() == View.VISIBLE) {
					searchLinearout.setVisibility(View.GONE);
				}
				/** 将arg2作为请求码传过去 用于标识修改项的位置 */
				startActivityForResult(intent, postion);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (deleteId == null) {
					deleteId = new ArrayList<Integer>();
				}
				HashMap item = (HashMap) arg0.getItemAtPosition(arg2);
				Integer _id = Integer.parseInt(String.valueOf(item.get("_id")));
				RelativeLayout r = (RelativeLayout) arg1;
				ImageView markedView = (ImageView) r.getChildAt(2);
				if (markedView.getVisibility() == View.VISIBLE) {
					markedView.setVisibility(View.GONE);
					deleteId.remove(_id);
					// phone_id = deleteId.size()-1;
				} else {
					markedView.setVisibility(View.VISIBLE);
					deleteId.add(_id);

				}
				System.out.println("deleteId " + deleteId.size());
				return true;
			}
		});
		// 为list添加item选择器
		if (BaseActivity.mDialog != null && BaseActivity.mDialog.isShowing())
			BaseActivity.mDialog.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 清除deleteId的内容
		if (deleteId != null) {
			deleteId.clear();
		}
		// 当resultCode==3时代表添加了一个用户返回，当resultCode==4的时候代表修改了用户，或者删除了用户，其他条件代表数据没有变化
		if (resultCode == 3 || resultCode == 4) {
			ContactsData helper = new ContactsData(this);
			list = helper.getAllUser(privacy);
			adapter = new SimpleAdapter(this, list, R.layout.listitem,
					new String[] { "imageid", "name", "mobilephone" },
					new int[] { R.id.user_image, R.id.tv_name,
							R.id.tv_mobilephone });
			if (list.size() > 0) {
				mainLinearLayout.setBackgroundDrawable(null);
			}
		}

		lv.setAdapter(adapter); // 将整合好的adapter交给listview，显示给用户看
		/**
		 * resultCode只有3、4、5 当等于4或者5的时候，代表由UserDetail转过来的。在转想UserDetail的时候，
		 * requestCode的值设置的是选中项的位置
		 */
		if (resultCode == 3) {
			lv.setSelection(list.size());
		} else {
			lv.setSelection(requestCode);
		}

	}

	/**
	 * 响应点击Menu按钮时的事件，用于设置底部菜单是否可见
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			loadBottomMenu();
			if (bottomMenuGrid.getVisibility() == View.VISIBLE) {
				if (searchLinearout != null
						&& searchLinearout.getVisibility() == View.VISIBLE) {
					searchLinearout.setVisibility(View.GONE);
				}
				bottomMenuGrid.setVisibility(View.GONE);
			} else {
				bottomMenuGrid.setVisibility(View.VISIBLE);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loadBottomMenu() {

		if (bottomMenuGrid == null) {
			bottomMenuGrid = (GridView) findViewById(R.id.gv_buttom_menu);
			bottomMenuGrid.setBackgroundResource(R.drawable.channelgallery_bg);// 设置背景
			bottomMenuGrid.setNumColumns(5);// 设置每行列数
			bottomMenuGrid.setGravity(Gravity.CENTER);// 位置居中
			bottomMenuGrid.setVerticalSpacing(10);// 垂直间隔
			bottomMenuGrid.setHorizontalSpacing(10);// 水平间隔
			bottomMenuGrid.setAdapter(getMenuAdapter(bottom_menu_itemName,
					bottom_menu_itemSource));// 设置菜单Adapter
			/** 监听底部菜单选项 **/
			bottomMenuGrid.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					switch (arg2) {
					case 0: {

						if (searchLinearout != null
								&& searchLinearout.getVisibility() == View.VISIBLE) {
							searchLinearout.setVisibility(View.GONE);
						}

						if (bottomMenuGrid.getVisibility() == View.VISIBLE) {
							bottomMenuGrid.setVisibility(View.GONE);
						}

						Intent intent = new Intent(Main.this, AddNew.class);
						startActivityForResult(intent, 3);
						break;
					}

					case 1:
						loadSearchLinearout();
						if (searchLinearout.getVisibility() == View.VISIBLE) {
							searchLinearout.setVisibility(View.GONE);
						} else {
							searchLinearout.setVisibility(View.VISIBLE);
							et_search.requestFocus();
							et_search.selectAll();
						}
						break;
					case 2:
						// liang
						AlertDialog.Builder builder = new AlertDialog.Builder(
								Main.this);
						builder.setTitle("选项");
						builder.setItems(R.array.choice1,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										switch (which) {
										case 0:
											if (searchLinearout != null
													&& searchLinearout
															.getVisibility() == View.VISIBLE) {
												searchLinearout
														.setVisibility(View.GONE);
											}
											if (deleteId == null
													|| deleteId.size() == 0) {
												Toast.makeText(
														Main.this,
														"    没有标记任何记录\n长按一条记录即可标记",
														Toast.LENGTH_LONG)
														.show();
											} else {
												if (deleteId.size() == 1) {
													String phone = "";
													for (int i = 0; i < list
															.size(); i++) {
														String str = list
																.get(i)
																.toString()
																.trim();
														if (str.indexOf("_id="
																+ deleteId
																		.get(0)
																		.intValue()) != -1) {
															phone = str.substring(
																	str.indexOf("mobilephone=")
																			+ "mobilephone="
																					.length(),
																	str.indexOf(", _id"));

															Intent intent = new Intent(
																	Intent.ACTION_CALL,
																	Uri.parse("tel:"
																			+ phone));
															startActivity(intent);
														}

													}

												} else {
													Toast.makeText(
															Main.this,
															"请选择一条记录做标记\n长按一条记录即可标记",
															Toast.LENGTH_LONG)
															.show();
												}

											}
											break;
										case 1:

											if (searchLinearout != null
													&& searchLinearout
															.getVisibility() == View.VISIBLE) {
												searchLinearout
														.setVisibility(View.GONE);
											}
											if (deleteId == null
													|| deleteId.size() == 0) {
												Toast.makeText(
														Main.this,
														"    没有标记任何记录\n长按一条记录即可标记",
														Toast.LENGTH_LONG)
														.show();
											} else {

												if (deleteId.size() == 1) {
													String phone = "";
													for (int i = 0; i < list
															.size(); i++) {
														String str = list
																.get(i)
																.toString()
																.trim();
														if (str.indexOf("_id="
																+ deleteId
																		.get(0)
																		.intValue()) != -1) {

															phone = str.substring(
																	str.indexOf("mobilephone=")
																			+ "mobilephone="
																					.length(),
																	str.indexOf(", _id"));
															System.out
																	.println(str
																			+ " str");
															// Intent intent =
															// new
															// Intent(SendMessage,Uri.parse("tel:"+phone));

															Intent intent = new Intent(
																	Main.this,
																	SendMessage.class);
															intent.putExtra(
																	"tel",
																	phone);
															startActivity(intent);
														}
													}

												} else {
													Toast.makeText(
															Main.this,
															"请选择一条记录做标记\n长按一条记录即可标记",
															Toast.LENGTH_LONG)
															.show();
												}

											}
											break;

										}

									}
								});
						builder.setNegativeButton("取消", null);
						builder.create().show();

						break;
					case 3:
						if (searchLinearout != null
								&& searchLinearout.getVisibility() == View.VISIBLE) {
							searchLinearout.setVisibility(View.GONE);
						}
						loadMainMenuDialog();
						mainMenuDialog.show();
						break;
					case 4:
						finish();
						break;
					}
				}
			});
		}

	}

	private void loadMainMenuDialog() {

		if (mainMenuDialog == null) {
			LayoutInflater li = this.getLayoutInflater();
			mainMenuView = li.inflate(R.layout.main_menu_grid, null);
			// 根据主菜单视图，创建主菜单对话框
			mainMenuDialog = new AlertDialog.Builder(this)
					.setView(mainMenuView).create();
			// 根据主菜单视图，拿到视图文件中的GridView，然后再往里面放Adapter
			mainMenuGrid = (GridView) mainMenuView.findViewById(R.id.gridview);
			SimpleAdapter menuAdapter = getMenuAdapter(main_menu_itemName,
					main_menu_itemSource);
			mainMenuGrid.setAdapter(menuAdapter);
			// 响应点击事件
			mainMenuGrid.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0: {
						ContactsData helper = new ContactsData(Main.this);
						list = helper.getAllUser(privacy);
						adapter = new SimpleAdapter(Main.this, list,
								R.layout.listitem, new String[] { "imageid",
										"name", "mobilephone" }, new int[] {
										R.id.user_image, R.id.tv_name,
										R.id.tv_mobilephone });

						lv.setAdapter(adapter);// 显示所有数据
						mainMenuDialog.dismiss();
						break;
					}
					case 1: {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								Main.this);
						confirmDialog = builder.create();
						builder.setTitle("是否删除所有！?");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										ContactsData helper = new ContactsData(
												Main.this);
										helper.deleteAllSQL(0);
										list = helper.getAllUser(privacy);
										adapter = new SimpleAdapter(
												Main.this,
												list,
												R.layout.listitem,
												new String[] { "imageid",
														"name", "mobilephone" },
												new int[] { R.id.user_image,
														R.id.tv_name,
														R.id.tv_mobilephone });

										lv.setAdapter(adapter);// 显示所有数据
										mainMenuDialog.dismiss();
									}
								});
						builder.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										confirmDialog.dismiss();
									}
								});
						builder.create().show();
						break;
					}
					case 2: {
						mainMenuDialog.dismiss();
						break;
					}

					}

				}
			});
		}

	}

	private void loadSearchLinearout() {
		if (searchLinearout == null) {
			searchLinearout = (LinearLayout) findViewById(R.id.ll_search);
			et_search = (EditText) findViewById(R.id.et_search);
			et_search.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					String condition = et_search.getText().toString();
					if (condition.equals("")) {
						lv.setAdapter(adapter);
					}
					ContactsData helper = new ContactsData(Main.this);
					list = helper.getUsers(condition, privacy);
					SimpleAdapter searchAdapter = new SimpleAdapter(Main.this,
							list, R.layout.listitem, new String[] { "imageid",
									"name", "mobilephone" }, new int[] {
									R.id.user_image, R.id.tv_name,
									R.id.tv_mobilephone });
					lv.setAdapter(searchAdapter); // 将整合好的adapter交给listview，显示给用户看
					if (list.size() == 0) {
						Drawable nodata_bg = getResources().getDrawable(
								R.drawable.nodata_bg);
						mainLinearLayout.setBackgroundDrawable(nodata_bg);
						setTitle("没有查到任何数据");
					} else {
						setTitle("共查到 " + list.size() + " 条记录");

						mainLinearLayout.setBackgroundDrawable(null);
					}
					return false;
				}
			});
		}

	}

	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	/**
	 * 当退出的时候，回收资源
	 */
	@Override
	protected void onDestroy() {
		if (confirmDialog != null) {
			confirmDialog = null;
		}
		if (mainMenuDialog != null) {
			mainMenuDialog = null;
		}
		if (searchLinearout != null) {
			searchLinearout = null;
		}
		if (mainMenuView != null) {
			mainMenuView = null;
		}
		if (mainMenuGrid != null) {
			mainMenuGrid = null;
		}
		if (bottomMenuGrid != null) {
			bottomMenuGrid = null;
		}
		if (adapter != null) {
			adapter = null;
		}
		if (list != null) {
			list = null;
		}
		if (lv != null) {
			lv = null;
		}
		if (ContactsData.dbInstance != null) {
			ContactsData.dbInstance.close();
			ContactsData.dbInstance = null;
		}
		System.out.println("destory!!!");
		super.onDestroy();
	}

}