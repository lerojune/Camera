package com.baidu.mapapi.utils.route;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.baidu.mapapi.navi.IllegalNaviArgumentException;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.poi.IllegalPoiSearchArgumentException;

/**
 * Created by Administrator on 2016/8/18.
 */
public class Plan {
    private static boolean a = true;

    public Plan() {
    }

    public static void setSupportWebRoute(boolean var0) {
        a = var0;
    }

    public static boolean openBaiduMapWalkingRoute(RouteParaOption var0, Context var1) {
        if(var0 != null && var1 != null) {
            if(var0.b == null && var0.a == null && var0.d == null && var0.c == null) {
                throw new IllegalNaviArgumentException("startPoint and endPoint and endName and startName not all null.");
            } else if(var0.c == null && var0.a == null) {
                throw new IllegalNaviArgumentException("startPoint and startName not all null.");
            } else if(var0.d == null && var0.b == null) {
                throw new IllegalNaviArgumentException("endPoint and endName not all null.");
            } else if((var0.c != null && !var0.c.equals("") || var0.a != null) && (var0.d != null && !var0.d.equals("") || var0.b != null)) {
                if(var0.f == null) {
                    var0.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
                }
                int var2 = OpenClientUtil.getBaiduMapVersion(var1);
                if(var2 != 0) {
                    if(var2 >= 810) {
                        //return com.baidu.mapapi.utils.a.a(var0, var1,2);
                        app(var0,var1,2);
                        return true;
                    } else {
                        Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                        if(a) {
                            a(var0, var1, 2);
                            return true;
                        } else {
                            throw new IllegalPoiSearchArgumentException("Baidumap app version is too lowl.Version is greater than 8.1");
                        }
                    }
                } else {
                    Log.e("baidumapsdk", "BaiduMap app is not installed.");
                    if(a) {
                        a(var0, var1, 2);
                        return true;
                    } else {
                        throw new IllegalPoiSearchArgumentException("BaiduMap app is not installed.");
                    }
                }
            } else {
                Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
                return false;
            }
        } else {
            throw new IllegalPoiSearchArgumentException("para or context can not be null.");
        }
    }

    public static boolean openBaiduMapTransitRoute(RouteParaOption var0, Context var1) {
        if(var0 != null && var1 != null) {
            if(var0.b == null && var0.a == null && var0.d == null && var0.c == null) {
                throw new IllegalNaviArgumentException("startPoint and endPoint and endName and startName not all null.");
            } else if(var0.c == null && var0.a == null) {
                throw new IllegalNaviArgumentException("startPoint and startName not all null.");
            } else if(var0.d == null && var0.b == null) {
                throw new IllegalNaviArgumentException("endPoint and endName not all null.");
            } else if((var0.c != null && !var0.c.equals("") || var0.a != null) && (var0.d != null && !var0.d.equals("") || var0.b != null)) {
                if(var0.f == null) {
                    var0.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
                }

                int var2 = OpenClientUtil.getBaiduMapVersion(var1);
                if(var2 != 0) {
                    if(var2 >= 810) {
                        //return com.baidu.mapapi.utils.a.a(var0, var1, 1);
                        app(var0,var1,1);
                        return true;
                    } else {
                        Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                        if(a) {
                            a(var0, var1, 1);
                            return true;
                        } else {
                            throw new IllegalPoiSearchArgumentException("Baidumap app version is too lowl.Version is greater than 8.1");
                        }
                    }
                } else {
                    Log.e("baidumapsdk", "BaiduMap app is not installed.");
                    if(a) {
                        a(var0, var1, 1);
                        return true;
                    } else {
                        throw new IllegalPoiSearchArgumentException("BaiduMap app is not installed.");
                    }
                }
            } else {
                Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
                return false;
            }
        } else {
            throw new IllegalPoiSearchArgumentException("para or context can not be null.");
        }
    }

    public static void finish(Context var0) {
        if(var0 != null) {
            com.baidu.mapapi.utils.a.a(var0);
        }

    }

    public static boolean openBaiduMapDrivingRoute(RouteParaOption var0, Context var1) {
        if(var0 != null && var1 != null) {
            if(var0.b == null && var0.a == null && var0.d == null && var0.c == null) {
                throw new IllegalNaviArgumentException("startPoint and endPoint and endName and startName not all null.");
            } else if(var0.c == null && var0.a == null) {
                throw new IllegalNaviArgumentException("startPoint and startName not all null.");
            } else if(var0.d == null && var0.b == null) {
                throw new IllegalNaviArgumentException("endPoint and endName not all null.");
            } else if((var0.c != null && !var0.c.equals("") || var0.a != null) && (var0.d != null && !var0.d.equals("") || var0.b != null)) {
                if(var0.f == null) {
                    var0.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
                }
                int var2 = OpenClientUtil.getBaiduMapVersion(var1);
                if(var2 != 0) {
                    if(var2 >= 810) {
                        //return com.baidu.mapapi.utils.a.a(var0, var1, 0);
                        app(var0,var1,0);
                        return true;
                    } else {
                        Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                        if(a) {
                            a(var0, var1, 0);
                            return true;
                        } else {
                            throw new IllegalPoiSearchArgumentException("Baidumap app version is too lowl.Version is greater than 8.1");
                        }
                    }
                } else {
                    Log.e("baidumapsdk", "BaiduMap app is not installed.");
                    if(a) {
                        a(var0, var1, 0);
                        return true;
                    } else {
                        throw new IllegalPoiSearchArgumentException("BaiduMap app is not installed.");
                    }
                }
            } else {
                Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
                return false;
            }
        } else {
            throw new IllegalPoiSearchArgumentException("para or context can not be null.");
        }
    }

    private static void app(RouteParaOption var0, Context var1, int var2){
        StringBuilder var3 = new StringBuilder();
        var3.append("intent://map/direction?");
        doAction(var0,var1,var2,var3);
    }

    private static void a(RouteParaOption var0, Context var1, int var2) {
        StringBuilder var3 = new StringBuilder();
        var3.append("http://api.map.baidu.com/direction?");
        doAction(var0,var1,var2,var3);
    }

    private static void doAction(RouteParaOption var0, Context var1, int var2, StringBuilder var3){
        var3.append("origin=");
        if(var0.a != null && var0.c != null && !var0.c.equals("")) {
            var3.append("latlng:");
            var3.append(var0.a.latitude);
            var3.append(",");
            var3.append(var0.a.longitude);
            var3.append("|");
            var3.append("name:");
            var3.append(var0.c);
        } else if(var0.a != null) {
            var3.append(var0.a.latitude);
            var3.append(",");
            var3.append(var0.a.longitude);
        } else {
            var3.append(var0.c);
        }

        var3.append("&destination=");
        if(var0.b != null && var0.d != null && !var0.d.equals("")) {
            var3.append("latlng:");
            var3.append(var0.b.latitude);
            var3.append(",");
            var3.append(var0.b.longitude);
            var3.append("|");
            var3.append("name:");
            var3.append(var0.d);
        } else if(var0.b != null) {
            var3.append(var0.b.latitude);
            var3.append(",");
            var3.append(var0.b.longitude);
        } else {
            var3.append(var0.d);
        }

        String var4 = "";
        switch(var2) {
            case 0:
                var4 = "driving";
                break;
            case 1:
                var4 = "transit";
                break;
            case 2:
                var4 = "walking";
        }

        var3.append("&mode=");
        var3.append(var4);
        var3.append("&region=");
        if(var0.getCityName() != null && !var0.getCityName().equals("")) {
            var3.append(var0.getCityName());
        } else {
            var3.append("全国");
        }

        var3.append("&output=html");
        var3.append("&src=");
        var3.append(var1.getPackageName());
        Uri var5 = Uri.parse(var3.toString());
        Intent var6 = new Intent();
        var6.setAction("android.intent.action.VIEW");
        var6.setFlags(268435456);
        var6.setData(var5);
        var1.startActivity(var6);
    }
}
