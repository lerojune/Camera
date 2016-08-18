package com.baidu.mapapi.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.yunrich.map.R;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.Plan;
import com.baidu.mapapi.utils.route.RouteParaOption;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    MapActivity instace = this;

    RouteLine route = null;
    OverlayManager routeOverlay = null;

    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    Marker marker;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    TransitRouteResult nowResult = null;
    DrivingRouteResult nowResultd = null;
    LatLng llFrom = null;
    LatLng llTo = null;
    String city = "";
    String detail = "";


    LocHelper locHelper;
    GeoHelper geoHelper;

    int type = 2;//0-walk 1-bus 2-car 3-bike;

    ProgressBar progressBar;

    int red = 0;
    int black = 0;

    View rootView;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        red = getResources().getColor(R.color.com_red);
        black = getResources().getColor(R.color.com_black);
        rootView = LayoutInflater.from(this).inflate(R.layout.layout_map_nav, null);
        address = (TextView) rootView.findViewById(R.id.tv_address);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Resources resources = getResources();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("驾车").setTag(2).setIcon(resources.getDrawable(R.drawable.selector_icon_drive)));
        tabLayout.addTab(tabLayout.newTab().setText("公交").setTag(1).setIcon(resources.getDrawable(R.drawable.selector_icon_bus)));
        tabLayout.addTab(tabLayout.newTab().setText("步行").setTag(0).setIcon(resources.getDrawable(R.drawable.selector_icon_walk)));
        tabLayout.setOnTabSelectedListener(onTabListner);
        tabLayout.setTabTextColors(black, red);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBaidumap.setOnMapClickListener(onMapClickListener);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(planResultListener);

        city = getIntent().getStringExtra("city");
        detail = getIntent().getStringExtra("detail");
        address.setText(detail);
        locHelper = new LocHelper();
        locHelper.init(this, iLoc);
        geoHelper = new GeoHelper();
        geoHelper.init(this, iGeo);

//        searchLine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locHelper.release();
        geoHelper.release();
        mSearch.setOnGetRoutePlanResultListener(null);
        mSearch.destroy();
        mMapView.onDestroy();
        Plan.finish(this);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        searchLine();
    }


    TabLayout.OnTabSelectedListener onTabListner = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            type = (int) tab.getTag();
            searchLine();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    /**
     * 路线检索结果
     */
    OnGetRoutePlanResultListener planResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                prompt("抱歉，未找到结果");
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                prompt("抱歉，未找到结果");
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                //nodeIndex = -1;
                mMapView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                route = walkingRouteResult.getRouteLines().get(0);
                ExtWalkingRouteOverlay overlay = new ExtWalkingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(walkingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                showPopu();
            }
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                prompt("抱歉，未找到结果");
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                prompt("抱歉，未找到结果");
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                //nodeIndex = -1;
                mMapView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
//                if (transitRouteResult.getRouteLines().size() > 1 ) {
//                    nowResult = transitRouteResult;
//
//                    MyTransitDlg myTransitDlg = new MyTransitDlg(instace,
//                            transitRouteResult.getRouteLines(),
//                            RouteLineAdapter.Type.TRANSIT_ROUTE);
//                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
//                        public void onItemClick(int position) {
//                            route = nowResult.getRouteLines().get(position);
//                            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
//                            mBaidumap.setOnMarkerClickListener(overlay);
//                            routeOverlay = overlay;
//                            overlay.setData(nowResult.getRouteLines().get(position));
//                            overlay.addToMap();
//                            overlay.zoomToSpan();
//                        }
//                    });
//                    myTransitDlg.show();
//                } else
                if (transitRouteResult.getRouteLines().size() >= 1) {
                    // 直接显示
                    route = transitRouteResult.getRouteLines().get(0);
                    ExtTransitRouteOverlay overlay = new ExtTransitRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(transitRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    showPopu();
                } else {
                    Log.d("transitresult", "结果数<0");
                    return;
                }
            }
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                prompt("抱歉，未找到结果");
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                prompt("抱歉，未找到结果");
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                if (drivingRouteResult.getRouteLines().size() >= 1) {
                    route = drivingRouteResult.getRouteLines().get(0);
                    ExtDrivingRouteOverlay overlay = new ExtDrivingRouteOverlay(mBaidumap);
                    routeOverlay = overlay;
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    mMapView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    showPopu();
                }
            }
        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                prompt("抱歉，未找到结果");
            }
            if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                prompt("抱歉，未找到结果");
            }
            if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                //nodeIndex = -1;
                mMapView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                route = bikingRouteResult.getRouteLines().get(0);
                BikingRouteOverlay overlay = new BikingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(bikingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                showPopu();
            }
        }
    };

    BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            mBaidumap.hideInfoWindow();
        }

        @Override
        public boolean onMapPoiClick(MapPoi poi) {
            return false;
        }
    };

    LocHelper.ILoc iLoc = new LocHelper.ILoc() {
        @Override
        public void success(LatLng ll) {
            llFrom = ll;
            searchLine();
        }

        @Override
        public void error() {
            prompt("抱歉，未找到结果。错误码:2");
        }
    };

    GeoHelper.IGeo iGeo = new GeoHelper.IGeo() {
        @Override
        public void success(LatLng ll) {
            llTo = ll;
            searchLine();
        }

        @Override
        public void error(String msg) {
            prompt(msg + "。错误码:1");
        }
    };


    /**
     * 检索线路
     */
    private void searchLine() {
        hidePopu();
        progressBar.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.INVISIBLE);

        if (llFrom == null) {
            locHelper.start();
            return;
        }
        if (llTo == null) {
            geoHelper.find(city, detail);
            return;
        }

        PlanNode stNode = PlanNode.withLocation(llFrom);
        PlanNode enNode = PlanNode.withLocation(llTo);

        switch (type) {
            case 0:
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode).to(enNode));
                break;
            case 1:
                mSearch.transitSearch((new TransitRoutePlanOption())
                        .from(stNode).city("上海市").to(enNode));
                break;
            case 2:
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode).to(enNode));
                break;
            case 3:
                mSearch.bikingSearch((new BikingRoutePlanOption())
                        .from(stNode).to(enNode));
                break;
            default:
                break;
        }
        mBaidumap.clear();
    }


    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi(View view) {
        if (llFrom == null || llTo == null) {
            return;
        }
        switch (type) {
            case 0:
                startRoutePlanWalking();
                return;
            case 1:
                startRoutePlanTransit();
                return;
            case 2:
                startRoutePlanDriving();
                return;
        }

        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(llFrom).endPoint(llTo);

        try {
            BaiduMapNavigation.setSupportWebNavi(false);
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog();
        }
    }

    /**
     * 启动百度地图步行路线规划
     */
    public void startRoutePlanWalking() {
        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(llFrom).endPoint(llTo);
        try {
//            BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, this);
            Plan.openBaiduMapWalkingRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }

    }

    /**
     * 启动百度地图驾车路线规划
     */
    public void startRoutePlanDriving() {
        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(llFrom).endPoint(llTo);
        try {
//            BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, this);
            Plan.openBaiduMapDrivingRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }

    }

    /**
     * 启动百度地图公交路线规划
     */
    public void startRoutePlanTransit() {
        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(llFrom).endPoint(llTo)
                .startName("天安门")
                .busStrategyType(RouteParaOption.EBusStrategyType.bus_recommend_way);
        try {
//            BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, this);
            Plan.openBaiduMapTransitRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog();
        }
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(MapActivity.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 自定义overlay
     */
    private void showPopu() {
        InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(rootView), llTo, -47, onInfoWindowClickListener);
        mBaidumap.showInfoWindow(mInfoWindow);
    }

    private void hidePopu(){
        mBaidumap.hideInfoWindow();
    }

    InfoWindow.OnInfoWindowClickListener onInfoWindowClickListener = new InfoWindow.OnInfoWindowClickListener() {
        public void onInfoWindowClick() {
            mBaidumap.hideInfoWindow();
            startNavi(null);
        }
    };

    class ExtTransitRouteOverlay extends TransitRouteOverlay {
        public ExtTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onRouteNodeClick(Marker marker) {
            if (marker.getPosition().latitude == llTo.latitude &&
                    marker.getPosition().longitude == llTo.longitude) {
                showPopu();
            }
            return super.onRouteNodeClick(marker);
        }
    }

    class ExtWalkingRouteOverlay extends WalkingRouteOverlay {
        public ExtWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onRouteNodeClick(Marker marker) {
            if (marker.getPosition().latitude == llTo.latitude &&
                    marker.getPosition().longitude == llTo.longitude) {
                showPopu();
            }
            return super.onRouteNodeClick(marker);
        }
    }

    class ExtDrivingRouteOverlay extends DrivingRouteOverlay {
        public ExtDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onRouteNodeClick(Marker marker) {
            if (marker.getPosition().latitude == llTo.latitude &&
                    marker.getPosition().longitude == llTo.longitude) {
                showPopu();
            }
            return super.onRouteNodeClick(marker);
        }
    }


    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);
            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    dismiss();
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }


    //错误消息提醒
    private void prompt(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        Toast.makeText(instace, msg, Toast.LENGTH_SHORT).show();
    }

}
