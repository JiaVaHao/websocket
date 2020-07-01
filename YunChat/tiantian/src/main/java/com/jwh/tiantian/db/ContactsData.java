package com.jwh.tiantian.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.view.View;

import com.jwh.tiantian.bean.User;

/*
 * 用于读取用户通讯录，管理软件通讯录相关数据库，
 * 实现数据库以及手机通讯录的增删改查等功能的类。 
 * 
 */
public class ContactsData {
	//数据库名称
	public static final String DB_DBNAME = "tiantian_contact";
	//数据库中对应的表名
	public static final String DB_TABLENAME = "user";
	//数据库版本
	public static final int VERSION = 5;
	//数据库对象，存储当前软件的通讯录信息
	public static SQLiteDatabase dbInstance;
	//数据库打开助手
	private MyDBHelper myDBHelper;
//  系统资源
	private Context context;
//  内容解析器，用于解析Uri，读取对应的Content数据库
	ContentResolver myResolver;

	public ContactsData(Context context) {
		this.context = context;
		myResolver = context.getContentResolver();
	}

	//打开数据库
	public void openDatabase() {
		if (dbInstance == null) {
			myDBHelper = new MyDBHelper(context, DB_DBNAME, VERSION);
			dbInstance = myDBHelper.getWritableDatabase();
		}
	}

	//关闭数据库
	public void closeDatabase() {
		if(dbInstance != null){
			dbInstance.close();
		}
	}
	
	//判断是否需要重新读取系统通讯录
	public boolean needFresh(){		
		Uri uri = 
		Data.CONTENT_URI;// 2.0以上系统使用ContactsContract.Data访问联系人
		Cursor cursor = myResolver.
				query(uri, null, null,
				null, "display_name");// 显示联系人时按显示名字排序
		int num = getContactsNum();
		System.out.println(cursor.getCount()+" Rcontacts() "+num);
		if(cursor.getCount()!=num){
			return true;
		}else{
			return false;
		}
	}

	//存储通讯录条目数量
	public void setContactsNum(Cursor cursor){
		SharedPreferences sharedPreferences = 
				context.getSharedPreferences("getCount", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();	
		editor.putInt("count", cursor.getCount());
		editor.commit();
	}
	//获取之前的通讯录条目数量
	public int getContactsNum(){
		SharedPreferences sharedPreferences =
				context.getSharedPreferences("getCount", Context.MODE_PRIVATE);
		return sharedPreferences.getInt("count", 0);
	}
	
	//删除数据库信息并重新读取系统通讯录
	public void openContacts(){
		deleteAllSQL(0);
		getAllContacts();
	}

	/** 
	 * 得到所有手机通讯录联系人信息 
	 * 并存入到软件数据库
	 * 
	 * 
	 * **/
	
	public void getAllContacts() {
//		通过URI使用ContentResolver
//		将通讯录信息读取到数据库查询结果游标集
//		cursor中，并按照显示名称排序
		
//		ContactsContract.RawContacts.CONTENT_URI
		
//		ContactsContract.Data.CONTENT_URI
		
//		ContactsContract.Contacts.CONTENT_URI
		
		Uri uri = 
				Data.CONTENT_URI;// 2.0以上系统使用ContactsContract.Data访问联系人
		Cursor cursor = myResolver.
				query(uri, null, null,
				null, "display_name");
		cursor.moveToFirst();//指针指向开头
		/*获得通讯录ID,数据类型和具体对应数据
		 * 对应列名的列ID
		 */
		int Index_CONTACT_ID = cursor
				.getColumnIndex(
				Data.CONTACT_ID);// 获得CONTACT_ID在ContactsContract.Data中的列数
		int Index_DATA1 = cursor.getColumnIndex(
				Data.DATA1);// 获得DATA1在ContactsContract.Data中的列数
		int Index_MIMETYPE = cursor
				.getColumnIndex(
						Data.MIMETYPE);// 获得MIMETYPE在ContactsContract.Data中的列数

		//获得通讯录条目数量（非联系人数量）
		setContactsNum(cursor);
		//对应的临时查询结果集，通讯人信息和通讯人ID
		ArrayList<User> userList=new ArrayList<User>();//存储已添加过的USER
		ArrayList<Integer> contact_ids = new ArrayList<Integer>();//对应USER的CONTACT_ID
				
		if(cursor.getCount()>0){//保证数据库非空时读取
			do{//循环读取通讯录Content中的所有条目
				//判读是否已有该联系人的其他信息
				boolean needNewUser = true;
				
				//联系人信息类对象
				User user = null;
				
				//联系人id，数据内容，数据mime类型
				String id = cursor.getString(Index_CONTACT_ID);// 获得CONTACT_ID列的内容
				String info = cursor.getString(Index_DATA1);// 获得DATA1列的内容
				String mimeType = cursor.getString(Index_MIMETYPE);// 获得MIMETYPE列的内容
				
				//通过contact_ids判断是否已有该联系人信息
				for (int i = 0; i < contact_ids.size(); i++) {
					if(contact_ids.get(i) ==
							Integer.parseInt(id)){
						user = userList.get(i);//已添加过，不用从新创建，可以直接获得
						setUserData(user, mimeType, info);
						modify(user);//更新数据库中对应条目信息
						needNewUser = false;
						break;
					}
				}
				
				if(needNewUser){
					//已有数据中无该联系人，新建联系人并插入数据库
					user = new User();
					user.contactid = Integer.parseInt(id);
					setUserData(user, mimeType, info);
					insertSQL(user);//插入数据库
					//记录该联系人id及信息
					contact_ids.add(user.contactid);
					userList.add(user);
				}
				
			}while(cursor.moveToNext());
			
		}
	}

	public void setUserData(User user, String mimeType, String info) {
		user.company = "";
		user.familyPhone = "";
		user.officePhone = "";
		user.otherContact = "";
		user.position = "";
		user.remark = "";
		user.zipCode = "";
		user.imageId = 0;
		
		if (mimeType.equals("vnd.android.cursor.item/email_v2"))// 该行数据为邮箱
		{
			user.email = info;
		} else if (mimeType.equals("vnd.android.cursor.item/postal-address_v2"))// 该行数据为地址
		{
			user.address = info;
		} else if (mimeType.equals("vnd.android.cursor.item/phone_v2"))// 该行数据为电话号码
		{
			user.mobilePhone = info;
		} else if (mimeType.equals("vnd.android.cursor.item/name"))// 该行数据为名字
		{
			user.username = info;
		}
	}
	//更新数据库以及通讯录
	public void modify(User user) {
		modifySQL(user);
		//-------下面是修改手机电话簿方法--------//
		try {
			modifyContacts(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 往数据库里面的user表插入一条数据，若失败返回-1
	 * 
	 * @param user
	 * @return 失败返回-1
	 */
	public long insertSQL(User user) {
		ContentValues values = new ContentValues();
		values.put("name", user.username);
		values.put("mobilephone", user.mobilePhone);
		values.put("officephone", user.officePhone);
		values.put("familyphone", user.familyPhone);
		values.put("address", user.address);
		values.put("othercontact", user.otherContact);
		values.put("email", user.email);
		values.put("position", user.position);
		values.put("company", user.company);
		values.put("zipcode", user.zipCode);
		values.put("remark", user.remark);
		values.put("imageid", user.imageId);
		values.put("privacy", user.privacy);
		values.put("contactid", user.contactid);
		
		return dbInstance.insert(DB_TABLENAME, null, values);
	}
	
	public void deleteAllSQL(int privacy) {
		dbInstance.delete(DB_TABLENAME, "privacy=?",
				new String[] { String.valueOf(privacy) });
	}

	public void deleteSQL(int _id) {		
		dbInstance.delete(DB_TABLENAME, "_id=?",
				new String[] { String.valueOf(_id) });
	}
	public void modifySQL(User user) {
		ContentValues values = new ContentValues();
		values.put("name", user.username);
		values.put("mobilephone", user.mobilePhone);
		values.put("officephone", user.officePhone);
		values.put("familyphone", user.familyPhone);
		values.put("address", user.address);
		values.put("othercontact", user.otherContact);
		values.put("email", user.email);
		values.put("position", user.position);
		values.put("company", user.company);
		values.put("zipcode", user.zipCode);
		values.put("remark", user.remark);
		values.put("imageid", user.imageId);
		values.put("contactid", user.contactid);

		dbInstance.update(DB_TABLENAME, values, "contactid=?",
				new String[] { String.valueOf(user.contactid) });
		
	}

	/**
	 * 获得数据库中所有的用户，将每一个用户放到一个map中去，然后再将map放到list里面去返回
	 * 
	 * @param privacy
	 * @return list
	 */

	public ArrayList getAllUser(boolean privacy) {
		ArrayList list = new ArrayList();
		Cursor cursor = null;
		if (privacy) {
			cursor = dbInstance.query(DB_TABLENAME, new String[] { "_id",
					"name", "mobilephone", "officephone", "familyphone",
					"address", "othercontact", "email", "position", "company",
					"zipcode", "remark", "imageid", "contactid" }, "privacy=1", null, null,
					null, null);
		} else {
			cursor = dbInstance.query(DB_TABLENAME, new String[] { "_id",
					"name", "mobilephone", "officephone", "familyphone",
					"address", "othercontact", "email", "position", "company",
					"zipcode", "remark", "imageid", "contactid" }, "privacy=0", null, null,
					null, null);
		}
		
		while (cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("name", cursor.getString(cursor.getColumnIndex("name")));
			item.put("mobilephone",
					cursor.getString(cursor.getColumnIndex("mobilephone")));
			item.put("officephone",
					cursor.getString(cursor.getColumnIndex("officephone")));
			item.put("familyphone",
					cursor.getString(cursor.getColumnIndex("familyphone")));
			item.put("address",
					cursor.getString(cursor.getColumnIndex("address")));
			item.put("othercontact",
					cursor.getString(cursor.getColumnIndex("othercontact")));
			item.put("email", cursor.getString(cursor.getColumnIndex("email")));
			item.put("position",
					cursor.getString(cursor.getColumnIndex("position")));
			item.put("company",
					cursor.getString(cursor.getColumnIndex("company")));
			item.put("zipcode",
					cursor.getString(cursor.getColumnIndex("zipcode")));
			item.put("remark",
					cursor.getString(cursor.getColumnIndex("remark")));
			item.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
			item.put("contactid", cursor.getInt(cursor.getColumnIndex("contactid")));
			list.add(item);
		}
		return list;
	}
	//添加联系人，使用事务
	public void addContact(User user) throws Exception {
		ArrayList<ContentProviderOperation> operations =
				new ArrayList<ContentProviderOperation>();
		
		operations.add(addNewRawContact("account_name"));
		
		operations.add(addNewContactData(
				"vnd.android.cursor.item/name",
				user.username,
				user.username));

		operations.add(addNewContactData(
				"vnd.android.cursor.item/phone_v2",
				user.mobilePhone,
				user.mobilePhone));

		operations.add(addNewContactData(
				"vnd.android.cursor.item/email_v2",
				user.email,
				user.email));
		
//		ContentResolver resolver =
//				context.getContentResolver();
		myResolver.applyBatch(
				"com.android.contacts",
				operations);

//		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//		
//		
//		ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
//				.withValue("account_name", null).build();
		
//		uri = Uri.parse("content://com.android.contacts/data");
//		ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
//				.withValueBackReference("raw_contact_id", 0)
//				.withValue("mimetype", "vnd.android.cursor.item/name")
//				.withValue("data2", user.username).build();
//		operations.add(op2);
//
//		ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
//				.withValueBackReference("raw_contact_id", 0)
//				.withValue("mimetype", "vnd.android.cursor.item/phone_v2")
//				.withValue("data1", user.mobilePhone)
//				.withValue("data2", "2")
//				.build();
//		operations.add(op3);
//
//		ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri)
//				.withValueBackReference("raw_contact_id", 0)
//				.withValue("mimetype", "vnd.android.cursor.item/email_v2")
//				.withValue("data1", user.email)
//				.withValue("data2", "2").build();
//		operations.add(op4);
		
//		ContentResolver resolver =
//				context.getContentResolver();
//		resolver.applyBatch("com.android.contacts", operations);
	}

	public ContentProviderOperation addNewContactData(String mimeType,
			String data1, String data2) {
		 Uri uri = Uri.parse("content://com.android.contacts/data");
		 ContentProviderOperation op = ContentProviderOperation.newInsert(uri)
					.withValueBackReference("raw_contact_id", 0)
					.withValue("mimetype", mimeType)
					.withValue("data1", data1)
					.withValue("data2", data2).
					build();
		return op;
	}

	public ContentProviderOperation addNewRawContact(String key) {
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
				.withValue(key, null).build();
		return op1;
	}

	 public void deleteContacts(String name){
		    //根据姓名求id 
		    Uri uri = Uri.parse("content://com.android.contacts/raw_contacts"); 
		    Cursor cursor = myResolver.query(
		    		uri, 
		    		new String[]{Data._ID},
		    		"display_name=?", new String[]{name},
		    		null); 
		    if(cursor.moveToFirst()){ 
		        int id = cursor.getInt(0); 
		        //根据id删除data中的相应数据 
		        myResolver.delete(uri, "display_name=?", new String[]{name}); 
		        uri = Uri.parse("content://com.android.contacts/data"); 
		        myResolver.delete(uri, "raw_contact_id=?", new String[]{id+""}); 
		    } 
		    cursor.close();
	   }

		public void deleteContacts(int contactid) {			
			myResolver.delete(
					ContactsContract.RawContacts.CONTENT_URI,
					"contact_id=?", new String[]{"" + contactid}); 
			
			myResolver.delete(
					Data.CONTENT_URI,
					"raw_contact_id=?", new String[]{contactid+""}); 
		}
	/**
	 * 修改手机电话簿
	 * **/
	public void modifyContacts(User user)
			throws Exception{
		modifyName(user);//更新联系人姓名
		modifyPhone(user);//更新联系人电话
		modifyEmail(user);//更新联系人email
	}

	public void modifyEmail(User user) {
		ContentValues values = new ContentValues();
		values.put(Email.DATA, user.email);//键，值
		myResolver.update(
				Data.CONTENT_URI,
				values,//更新该条目中对应键的值
				" raw_contact_id=? and mimetype=?",//对应的联系人ID和MIME数据类型
				new String[] { user.contactid+"", Email.CONTENT_ITEM_TYPE});
	}

	public void modifyPhone(User user) {
		ContentValues values = new ContentValues();
		values.put(Phone.NUMBER, user.mobilePhone);
		myResolver.update(
			Data.CONTENT_URI,
			values,
			" raw_contact_id=? and mimetype=? and data2=?",
			new String[] { user.contactid+"",
					Phone.CONTENT_ITEM_TYPE,
					"2" });
	}

	public void modifyName(User user) {
		ContentValues values = new ContentValues();

		values.put(StructuredName.GIVEN_NAME, user.username);
		myResolver.update(
				Data.CONTENT_URI,
				values,
				" raw_contact_id=? and mimetype=?",
				new String[] { user.contactid + "",
						StructuredName.CONTENT_ITEM_TYPE });

	}



	public int getTotalCount() {
		Cursor cursor = dbInstance.query(DB_TABLENAME,
				new String[] { "count(*)" }, null, null, null, null, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}

	public ArrayList getUsers(String condition, boolean privacy) {
		ArrayList list = new ArrayList();
		String strSelection = "";
		if (privacy) {
			strSelection = "and privacy = 1";
		} else {
			strSelection = "and privacy = 0";
		}
		String sql = "select * from " + DB_TABLENAME
				+ " where 1=1 and (name like '%" + condition + "%' "
				+ "or mobilephone like '%" + condition
				+ "%' or familyphone like '%" + condition + "%' "
				+ "or officephone like '%" + condition + "%')" + strSelection;
		Cursor cursor = dbInstance.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("name", cursor.getString(cursor.getColumnIndex("name")));
			item.put("mobilephone",
					cursor.getString(cursor.getColumnIndex("mobilephone")));
			item.put("officephone",
					cursor.getString(cursor.getColumnIndex("officephone")));
			item.put("familyphone",
					cursor.getString(cursor.getColumnIndex("familyphone")));
			item.put("address",
					cursor.getString(cursor.getColumnIndex("address")));
			item.put("othercontact",
					cursor.getString(cursor.getColumnIndex("othercontact")));
			item.put("email", cursor.getString(cursor.getColumnIndex("email")));
			item.put("position",
					cursor.getString(cursor.getColumnIndex("position")));
			item.put("company",
					cursor.getString(cursor.getColumnIndex("company")));
			item.put("zipcode",
					cursor.getString(cursor.getColumnIndex("zipcode")));
			item.put("remark",
					cursor.getString(cursor.getColumnIndex("remark")));
			item.put("imageid", cursor.getInt(cursor.getColumnIndex("imageid")));
			list.add(item);
		}
		return list;
	}

	public void deleteMarked(ArrayList<Integer> deleteId) {
		StringBuffer strDeleteId = new StringBuffer();
		strDeleteId.append("_id=");
		for (int i = 0; i < deleteId.size(); i++) {
			if (i != deleteId.size() - 1) {
				strDeleteId.append(deleteId.get(i) + " or _id=");
			} else {
				strDeleteId.append(deleteId.get(i));
			}

		}
		dbInstance.delete(DB_TABLENAME, strDeleteId.toString(), null);
		//System.out.println(strDeleteId.toString());

	}

	public void backupData(boolean privacy) {
		StringBuffer sqlBackup = new StringBuffer();
		Cursor cursor = null;
		if (privacy) {
			cursor = dbInstance.query(DB_TABLENAME, new String[] { "_id",
					"name", "mobilephone", "officephone", "familyphone",
					"address", "othercontact", "email", "position", "company",
					"zipcode", "remark", "imageid,privacy" }, "privacy=1",
					null, null, null, null);
		} else {
			cursor = dbInstance.query(DB_TABLENAME, new String[] { "_id",
					"name", "mobilephone", "officephone", "familyphone",
					"address", "othercontact", "email", "position", "company",
					"zipcode", "remark", "imageid,privacy" }, "privacy=0",
					null, null, null, null);
		}

		while (cursor.moveToNext()) {
			sqlBackup
					.append("insert into "
							+ DB_TABLENAME
							+ "(name,mobilephone,officephone,familyphone,address,othercontact,email,position,company,zipcode,remark,imageid,privacy)")
					.append(" values ('")
					.append(cursor.getString(cursor.getColumnIndex("name")))
					.append("','")
					.append(cursor.getString(cursor
							.getColumnIndex("mobilephone")))
					.append("','")
					.append(cursor.getString(cursor
							.getColumnIndex("officephone")))
					.append("','")
					.append(cursor.getString(cursor
							.getColumnIndex("familyphone")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("address")))
					.append("','")
					.append(cursor.getString(cursor
							.getColumnIndex("othercontact")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("email")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("position")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("company")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("zipcode")))
					.append("','")
					.append(cursor.getString(cursor.getColumnIndex("remark")))
					.append("',")
					.append(cursor.getInt(cursor.getColumnIndex("imageid")))
					.append(",")
					.append(cursor.getInt(cursor.getColumnIndex("privacy")))
					.append(");").append("\n");
		}
		saveDataToFile(sqlBackup.toString(), privacy);
	}

	public void saveDataToFile(String strData, boolean privacy) {
		String fileName = "";
		if (privacy) {
			fileName = "priv_data.bk";
		} else {
			fileName = "comm_data.bk";
		}
		try {
			String SDPATH = Environment.getExternalStorageDirectory() + "/";
			File fileParentPath = new File(SDPATH + "zpContactData/");
			fileParentPath.mkdirs();
			File file = new File(SDPATH + "zpContactData/" + fileName);
			//System.out.println("the file previous path = "
//					+ file.getAbsolutePath());

			file.createNewFile();
			//System.out
//					.println("the file next path = " + file.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(file);

			fos.write(strData.getBytes());
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void restoreData(String fileName) {
		try {
			String SDPATH = Environment.getExternalStorageDirectory() + "/";
			File file = null;
			if (fileName.endsWith(".bk")) {
				file = new File(SDPATH + "zpContactData/" + fileName);
			} else {
				file = new File(SDPATH + "zpContactData/" + fileName + ".bk");
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";
			while ((str = br.readLine()) != null) {
				//System.out.println(str);
				dbInstance.execSQL(str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean findFile(String fileName) {
		String SDPATH = Environment.getExternalStorageDirectory() + "/";
		File file = null;
		if (fileName.endsWith(".bk")) {
			file = new File(SDPATH + "zpContactData/" + fileName);
		} else {
			file = new File(SDPATH + "zpContactData/" + fileName + ".bk");
		}

		if (file.exists()) {
			return true;
		} else {
			return false;
		}

	}


	class MyDBHelper extends SQLiteOpenHelper {
		//数据库管理器
		public MyDBHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuffer tableCreate = 
					new StringBuffer();
			tableCreate.append("create table ")
					.append(DB_TABLENAME)
					.append(" (")
					.append("_id integer primary key autoincrement,")
					.append("name text,")
					.append("mobilephone text,")
					.append("officephone text,")
					.append("familyphone text,")
					.append("address text,")
					.append("othercontact text,")
					.append("email text,")
					.append("position text,")
					.append("company text,")
					.append("zipcode text,")
					.append("remark text,")
					.append("imageid int,")
					.append("privacy int,")
					.append("contactid int ")
					.append(")");
			
			String sqlTxt = tableCreate.toString();
			System.out.println(sqlTxt);
			db.execSQL(tableCreate.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "drop table if exists " + DB_TABLENAME;
			db.execSQL(sql);
			myDBHelper.onCreate(db);
		}

	}

}
