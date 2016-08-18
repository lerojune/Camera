package com.baidu.mapapi.so;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/17.
 * 七牛存储
 */
public class SoNet implements INet{
    @Override
    public InputStream getFile(String type, String name) {
        String base = "你的so文件地址";
        URL url = null;
        try {
            url = new URL(String.format("%s/%s/%s",base,type,name));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            InputStream inputStream =  urlConnection.getInputStream();
            return inputStream;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        File file = new File(Config.ApkPath+type,name);
//        try {
//            FileInputStream inputStream = new FileInputStream(file);
//            return inputStream;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


}
