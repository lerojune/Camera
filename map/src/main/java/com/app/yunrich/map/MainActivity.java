package com.app.yunrich.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapapi.so.SOLoader;
import com.baidu.mapapi.so.SoFile;
import com.baidu.mapapi.ui.MapActivity;
import com.baidu.mapapi.ui.PlugsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void baidu(View view){
        SoFile soFile = new SoFile(getApplication());
        if (soFile.hasInitialize()){//直接进入百度地图界面
            SOLoader.initNativeDirectory(getApplication());
            System.loadLibrary(VersionInfo.getKitName());
            // System.loadLibrary("locSDK6a");
            SDKInitializer.initialize(getApplication());
            startActivity(new Intent(this, MapActivity.class).putExtra("city", "上海市")
                        .putExtra("detail", "外滩"));
        }else{//进入插件安装界面
            startActivity(new Intent(this, PlugsActivity.class).putExtra("city", "上海市")
                        .putExtra("detail", "外滩"));
        }
    }
}
