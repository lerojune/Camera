package com.baidu.mapapi.ui;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * Created by Administrator on 2016/7/25.
 * 地址反查
 */
public class GeoHelper  implements OnGetGeoCoderResultListener {
    public static interface IGeo{
        public void success(LatLng ll);
        public void error(String msg);
    }

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private Context context;
    private IGeo geoInterface;

    /**初始化
     * */
    public void init(Context context, IGeo geoInterface){
        this.context = context;
        this.geoInterface = geoInterface;
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    /***
     * 释放相关资源
     */
    public void release(){
        mSearch.setOnGetGeoCodeResultListener(null);
        mSearch.destroy();
    }


    /**
     * 发起GEO查询
     * */
    public void find(String city, String detail){
        mSearch.geocode(new GeoCodeOption().city(
                city).address(detail));
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (geoInterface == null)
            return;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            geoInterface.error("抱歉，未能找到结果");
            return;
        }
        geoInterface.success(result .getLocation());;
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (geoInterface == null)
            return;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            geoInterface.error("抱歉，未能找到结果");
            return;
        }
        geoInterface.success(result.getLocation());
    }

    //消息提醒
    private void prompt(String msg){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }
}
