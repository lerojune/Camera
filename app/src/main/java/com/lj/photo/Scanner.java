package com.lj.photo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lj.photo.interf.IScan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10.
 *
 * 图库扫描数据
 *
 */
public class Scanner implements IScan{
    private Context context;
    final List<PhotoFile> checkedItems = new ArrayList<>();
    Map<String, List<PhotoFile>> folders = new HashMap<String, List<PhotoFile>>();
    boolean scaned = false;
    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };

    private static Scanner instance;
    private Scanner(){
    }


    /**
     * 获取实例, 该函数为线程安全函数
     * */
    public static Scanner getInstance(){
        if (instance != null){
            return instance;
        }
        synchronized (Scanner.class){
            if (instance == null){
                instance = new Scanner();
            }
            return instance;
        }
    }

    public void init(Context context){
        this.context = context;
    }


    /**
     * 启动系统扫描机制，必须拥有存储操作权限
     * */
    @Override
    public void scan() {
        if (context == null)
            throw new RuntimeException("没有初始化");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
        scaned = false;
    }

    @Override
    public  synchronized Map<String, List<PhotoFile>> getFolders() {
        if (folders.size() > 0 && scaned)
            return folders;

        folders.clear();
        //获取大图的游标
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        if (cursor == null)
            return null;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if (TextUtils.isEmpty(uri))
                    continue;
                if (TextUtils.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();

                PhotoFile localFile = new PhotoFile();
                localFile.setOrigenPath(uri);
                localFile.setThumbPath(thumbUri);
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.setDegree(360 - degree);

                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<PhotoFile> files = new ArrayList<>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        cursor.close();
        scaned = true;
        return folders;
    }

    @Override
    public List<PhotoFile> getPhotos(String folder) {
        return folders.get(folder);
    }

    private synchronized String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            String filePath = cursor.getString(1);
            if (!new File(filePath).exists()) {
                cursor.close();
                return null;
            }
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }
}
