package com.jwh.yunchat.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

public class PictureUtil {
    public static final String APP_NAME="com.jwh.yunchat";
    public static final int FROM_CAMERA=101;
    public static final int FROM_PICTURE_LIB=102;

    public static final int HORIZONTAL_PICTURE=201;
    public static final int VERTICAL_PICTURE=202;
    public static final int SQUARE_PICTURE=203;

    private Activity activity;
    private File pictureFile;
    private Uri pictureUri;

    public PictureUtil(Activity activity){
        this.activity=activity;
    }

    public Uri getPictureUri(){
        return pictureUri;
    }

    public File getPictureFile(){
        return pictureFile;
    }
    public void setPictureFile(File file){
        this.pictureFile=file;
    }

    //打开相册
    public void openPictureLib() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, FROM_PICTURE_LIB);
    }

    //打开相机,返回照片的零时存储路径
    public void openCamera(){
        pictureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pictureUri = FileProvider.getUriForFile(activity, APP_NAME+".fileprovider", pictureFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            pictureUri = Uri.fromFile(pictureFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        activity.startActivityForResult(intent, FROM_CAMERA);
    }
    //从图库中获取图片路径，来自网络
    public  String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) {
            return getRealPathFromUriAboveApi19(activity, uri);
        } else {
            return getRealPathFromUriBelowAPI19(activity, uri);
        }
    }
    private String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }
    @SuppressLint("NewApi")
    private String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    //通过Glide4 获取网络图片的长宽高并进行分类，分为横向图片，纵向图片，正方形
    public static int getPictureType(String url,Activity activity){
        int type;
        final int[] width=new int[1];
        final int[] height=new int[1];
        Glide.with(activity)
                .asBitmap()
                .load(url)//强制Glide返回一个Bitmap对象
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        width[0]= resource.getWidth();
                        height[0]= resource.getHeight();
                    }
                });
        int x=width[0]/2;
        if (0.9<x && x<1.11111){
            type=SQUARE_PICTURE;
        }else if ((width[0] - height[0]) > 0){
            type=HORIZONTAL_PICTURE;
        }else{
            type=VERTICAL_PICTURE;
        }
        return type;
    }

    //在活动回调数据中调用即可
    public void showPicture(int requestCode, int resultCode, @Nullable Intent data, Activity activity, ImageView imageView){
        switch (requestCode) {
            case PictureUtil.FROM_CAMERA:
                if (resultCode == -1) {
                    Glide.with(activity)
                            .load(pictureUri)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(imageView);
                }
                break;
            case PictureUtil.FROM_PICTURE_LIB:
                String path = getRealPathFromUri(activity, data.getData());
                pictureFile=new File(path);
                Glide.with(activity)
                        .load(path)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(imageView);
                break;
            default:
                break;
        }
    }
}
