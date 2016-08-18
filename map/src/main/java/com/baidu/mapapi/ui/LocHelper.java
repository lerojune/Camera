package com.baidu.mapapi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/7/25.
 *
 * 地址定位信息
 */
public class LocHelper {

    public static interface ILoc{
        public void success(LatLng ll);
        public void error();
    }


    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                prompt("key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                prompt("网络出错");
            }
        }
    }

    private SDKReceiver mReceiver;
    private Context context;
    private ILoc locInterface;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true; // 是否首次定位

    /**
     * 初始化
     * */
    public void init(Context context, ILoc locInterface){
        this.context = context;
        this.locInterface = locInterface;
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        context.registerReceiver(mReceiver, iFilter);
    }

    /**
     * 资源释放
     * */
    public void release(){
        // 取消监听 SDK 广播
        context.unregisterReceiver(mReceiver);
        if (mLocClient != null && mLocClient.isStarted()){
            mLocClient.stop();
        }
    }


    /**
     * 启动查询地址，通过ILoc接口返回
     * */
    public void start(){
        if (mLocClient != null){
            isFirstLoc = false;
            return;
        }
        // 定位初始化
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    //消息提醒
    private void prompt(String msg){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                //返回结果
                if (locInterface != null){
                    locInterface.success(ll);
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}