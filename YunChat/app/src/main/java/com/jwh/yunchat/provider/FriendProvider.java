package com.jwh.yunchat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jwh.yunchat.activity.BaseActivity;
import com.jwh.yunchat.entity.Friend;

import org.litepal.LitePal;

public class FriendProvider extends ContentProvider {

    public static final int FRIEND_TABLE_DIR=0;
    public static final int FRIEND_TABLE_ITEM=1;
    private static UriMatcher uriMatcher;
    //创建自定义 URI 代码
    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.jwh.yunchat.provider","friend",FRIEND_TABLE_DIR);
        //查询单条数据指的是根据Id查询
        uriMatcher.addURI("com.jwh.yunchat.provider","friend/#",FRIEND_TABLE_ITEM);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //匹配自定义的URI，执行相关操作,这里返回的是Cursor,这里使用litepal进行数据库操作
        Cursor cursor=null;
        switch (uriMatcher.match(uri)){
            case FRIEND_TABLE_DIR:
                String ownerNetId=String.valueOf( BaseActivity.owner.getNetId());
                cursor= LitePal.findBySQL("select * from friend where ownerNetId = ?",ownerNetId);
                break;
            case FRIEND_TABLE_ITEM:
                //通过ContentUri 可以解析到uri上的id
                String friendNetId=String.valueOf(ContentUris.parseId(uri));
                ownerNetId=String.valueOf( BaseActivity.owner.getNetId());
                cursor= LitePal.findBySQL("select * from friend where ownerNetId = ? and netId = ?",ownerNetId,friendNetId);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case FRIEND_TABLE_DIR:
                return "vnd.android.cursor.dir/vnd.com.jwh.yunchat.provider.friend";
            case FRIEND_TABLE_ITEM:
                return "vnd.android.cursor.item/vnd.com.jwh.yunchat.provider.friend";
        }
        return null;
    }

    @Nullable
    @Override
    //values 本质是一个HashMap的形式
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Friend friend=new Friend();
        friend.setImageUrl(values.getAsString("imageUrl"));
        friend.setName(values.getAsString("name"));
        friend.setNetId(values.getAsInteger("netId"));
        friend.setOwnerNetId(BaseActivity.owner.getNetId());
        friend.save();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
