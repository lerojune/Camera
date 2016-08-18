package com.baidu.mapapi.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.so.IProgress;
import com.baidu.mapapi.so.SOLoader;
import com.baidu.mapapi.so.SoFile;
import com.baidu.mapapi.so.SoNet;
import com.sh.yirisheng.yijiaren.yrsheng_yijiaren.R;
import com.sh.yirisheng.yijiaren.yrsheng_yijiaren.base.App;

public class PlugsActivity extends AppCompatActivity {

    static final int P_START = 0;
    static final int P_PROGRESS = 1;
    static final int P_END = 2;
    static final int P_RESULT = 3;

    ProgressDialog dialog;
    SoFile soFile;
    int count = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case P_START:
                    //msg.arg1//有几个文件需要加载
                    count = msg.arg1;
                    dialog.setMessage(String.format("正在安装插件：0/%d",count));
                    break;
                case P_PROGRESS://
                    dialog.setMessage(String.format("正在安装插件：%d/%d",msg.arg1,count));
                    //msg.arg1当前加载第几个
                    break;
                case P_END:
                    dialog.hide();
                    break;
                case P_RESULT:
                    if(true == (boolean)msg.obj){
                        goBaidu();
                        finish();
                    }else{
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugs);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("正在安装...");
        dialog.show();
        soFile = new SoFile(App.getInstance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean res = soFile.plugInstall(new SoNet(), new IProgress() {
                    @Override
                    public void onStart(int count) {
                        Message msg = handler.obtainMessage();
                        msg.what = P_START;
                        msg.arg1 = count;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onProgress(int index) {
                        Message msg = handler.obtainMessage();
                        msg.what = P_PROGRESS;
                        msg.arg1 = index;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onEnd() {
                        handler.sendEmptyMessage(P_END);
                    }
                });
                Message msg = handler.obtainMessage();
                msg.what = P_RESULT;
                msg.obj = res;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void load(View view){
    }

    private void goBaidu(){
//        System.loadLibrary(VersionInfo.getKitName());
        SOLoader.initNativeDirectory(App.getInstance());
        SDKInitializer.initialize(App.getInstance());
        String city = getIntent().getStringExtra("city");
        String detail = getIntent().getStringExtra("detail");
        startActivity(new Intent(this, MapActivity.class).putExtra("city", city)
                .putExtra("detail", detail));
    }
}
