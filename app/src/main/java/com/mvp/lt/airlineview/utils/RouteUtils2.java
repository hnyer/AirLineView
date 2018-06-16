package com.mvp.lt.airlineview.utils;

import android.graphics.Point;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * $activityName
 *
 * @author ${LiuTao}
 * @date 2018/6/13/013
 */

public class RouteUtils2 {
    // [ {},{},{},{}]
    //得到东南西北四个点 //外接矩形
    public static List<LatLng> createPolygonBounds(List<LatLng> latLngList) {
        List<LatLng> latLngSparseArray = new ArrayList<>();

        if (latLngList.size() < 1) {
            return latLngSparseArray;
        }
        List<Double> latsLists = new ArrayList<>();//纬度
        List<Double> lngsLists = new ArrayList<>();//经度
        for (int i = 0; i < latLngList.size(); i++) {
            latsLists.add(latLngList.get(i).latitude);
            lngsLists.add(latLngList.get(i).longitude);
        }
        double lngsMax = Collections.max(lngsLists); //最东经
        double latsMax = Collections.max(latsLists); //最北纬
        double latsMin = Collections.min(latsLists); //最南纬
        double lngsMin = Collections.min(lngsLists); //最西经
        double lngsCenter = (lngsMax + lngsMin) / 2;
        double latsCenter = (latsMax + latsMin) / 2;
        latLngSparseArray.add(new LatLng(latsCenter, lngsCenter));//0中点
        latLngSparseArray.add(new LatLng(latsMax, lngsMin));      //1西北
        latLngSparseArray.add(new LatLng(latsMax, lngsMax));      //2东北
        latLngSparseArray.add(new LatLng(latsMin, lngsMax));      //3东南
        latLngSparseArray.add(new LatLng(latsMin, lngsMin));      //4西南

        return latLngSparseArray;
    }

    public static List<LatLng> createRotatePolygon(List<LatLng> mLatlists, List<LatLng> mEWSNLists, int rotate) {
        List<LatLng> latLngList = new ArrayList<>();

        for (int i = 0; i < mLatlists.size(); i++) {
            LatLng tr = transform(
                    mLatlists.get(i).longitude,
                    mLatlists.get(i).latitude,
                    mEWSNLists.get(0).longitude,
                    mEWSNLists.get(0).latitude,
                    rotate, 0, 0
            );
            latLngList.add(tr);
        }
        return latLngList;
    }


//    function createRotatePolygon(latlngs, bounds, rotate) {
//        if (typeof U.latlng2Px !== 'function' && typeof U.px2Latlng !== 'function') {
//            return false
//        }
//        var res = [],
//        a, b;
//        var c = U.latlng2Px(bounds.center);
//        for (var i = 0; i < latlngs.length; i++) {
//            a = U.latlng2Px(latlngs[i]);
//            b = transform(a.x, a.y, c.x, c.y, rotate);
//            res.push(U.px2Latlng(b));
//        }
//        return res;
//    }

    public static LatLng transform(double x, double y, double tx, double ty, int deg, int sx, int sy) {
        Double[] doubles = new Double[2];
        double sdeg = deg * Math.PI / 180;
        if (sy == 0) sy = 1;
        if (sx == 0) sx = 1;
        doubles[0] = sx * ((x - tx) * Math.cos(sdeg) - (y - ty) * Math.sin(sdeg)) + tx;
        doubles[1] = sy * ((x - tx) * Math.sin(sdeg) + (y - ty) * Math.cos(sdeg)) + ty;
        LatLng latLng = new LatLng(doubles[1], doubles[0]);
        return latLng;
    }

    //最北的点
    public static LatLng getNorthLatlngs(List<LatLng> latLngList, List<LatLng> latLngListBounds) {
        //最北的点
        LatLng latLng = null;
        for (int i = 0; i < latLngList.size(); i++) {
            if (latLngList.get(i).latitude == latLngListBounds.get(0).latitude) {
                latLng = latLngList.get(i);
                break;
            }
        }
        return latLng;
    }

    //计算纬度线 与边缘线的交点
    public static LatLng createInlinePoint(LatLng latLng1, LatLng latLng2, double y) {
        LatLng latLng;
        double s = latLng1.latitude - latLng2.latitude;
        double x;
        if (s > 0 || s < 0) {
            x = (y - latLng1.latitude) * (latLng1.longitude - latLng2.longitude) / s + latLng1.longitude;
        } else {
            return null;
        }

        /**判断x是否在p1,p2在x轴的投影里，不是的话返回null*/
        if (x > latLng1.longitude && x > latLng2.longitude) {
            return null;
        }
        if (x < latLng1.longitude && x < latLng2.longitude) {
            return null;
        }
        latLng = new LatLng(y, x);
        return latLng;
    }


    //计算有多少条纬度线穿过 纬度线相差lat
    public static Double[] createLats(List<LatLng> bounds, int space) {
        Double[] integers = new Double[2];
        //线条数量
        double steps = (distance(bounds.get(1), bounds.get(4)) / space );
        //纬度差
        double lat = (bounds.get(1).latitude - bounds.get(4).latitude) / steps;
        integers[0] = steps;
        integers[1] = lat;
        return integers;
    }

    public static boolean getCrossIndexId(LatLng aa, LatLng bb, LatLng cc, LatLng dd) {
        boolean isCunzai = false;
        double pointx = 0;
        double pointy = 0;
        double delta = determinant(bb.longitude - aa.longitude,
                cc.longitude - dd.longitude,
                bb.latitude - aa.latitude,
                cc.latitude - dd.latitude);
        if (delta > (1e-6) || delta < -(1e-6))  // delta=0，排除两线段重合或平行的情况
        {
            double namenda = determinant(cc.longitude - aa.longitude, cc.longitude - dd.longitude,
                    cc.latitude - aa.latitude, cc.latitude - dd.latitude) / delta;
            double miu = determinant(bb.longitude - aa.longitude, cc.longitude - aa.longitude,
                    bb.latitude - aa.latitude, cc.latitude - aa.latitude) / delta;
            //求交点
            double x = aa.longitude + namenda * (bb.longitude - aa.longitude);
            double y = aa.latitude + namenda * (bb.latitude - aa.latitude);
            if (cc.longitude <= dd.longitude) {
                if (x >= cc.longitude && x <= dd.longitude) {
                    if (x != aa.longitude && y != aa.latitude) {
                        pointx = x;
                        pointy = y;
                        isCunzai = true;
                    }

                }
            } else if (cc.longitude > dd.longitude) {
                if (x >= dd.longitude && x <= cc.longitude) {
                    if (x != aa.longitude && y != aa.longitude) {
                        pointx = x;
                        pointy = y;
                        isCunzai = true;
                    }
                }
            }
        }
        return isCunzai;
    }

    /**
     * 得到交点
     * 行列式
     *
     * @param aa 直线点
     * @param bb 直线点
     * @param cc 线段点
     * @param dd 线段点
     * @return
     */
    public static LatLng getXYPoint(LatLng aa, LatLng bb, LatLng cc, LatLng dd) {
        LatLng latLng = null;
        double pointx = 0;
        double pointy = 0;
        double delta = determinant(bb.longitude - aa.longitude,
                cc.longitude - dd.longitude,
                bb.latitude - aa.latitude,
                cc.latitude - dd.latitude);
        if (delta > (1e-6) || delta < -(1e-6))  // delta=0，排除两线段重合或平行的情况
        {
            double namenda = determinant(cc.longitude - aa.longitude, cc.longitude - dd.longitude,
                    cc.latitude - aa.latitude, cc.latitude - dd.latitude) / delta;
            double miu = determinant(bb.longitude - aa.longitude, cc.longitude - aa.longitude,
                    bb.latitude - aa.latitude, cc.latitude - aa.latitude) / delta;
            //求交点
            double x = aa.longitude + namenda * (bb.longitude - aa.longitude);
            double y = aa.latitude + namenda * (bb.latitude - aa.latitude);
            if (cc.longitude <= dd.longitude) {
                if (x >= cc.longitude && x <= dd.longitude) {
                    if (x != aa.longitude && y != aa.latitude) {
                        pointx = x;
                        pointy = y;
                    }

                }
            } else if (cc.longitude > dd.longitude) {
                if (x >= dd.longitude && x <= cc.longitude) {
                    if (x != aa.longitude && y != aa.longitude) {
                        pointx = x;
                        pointy = y;
                    }
                }
            }
        }
        return new LatLng(pointy, pointx);
    }
    public static  int  sint(int i, int len) {
        if (i > len - 1) {
            return i - len;
        }
        if (i < 0) {
            return len + i;
        }
        return i;
    }
    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式
    {
        return (v1 * v4 - v2 * v3);
    }

    //计算纬度线 与边缘线的交点
    public void calcCrossoverPointLatlngs(List<LatLng> rect, List<LatLng> latLngList, Double[] doubles) {

    }

    //计算两个点的距离
    public static float distance(LatLng latLng1, LatLng latLng2) {
        return AMapUtils.calculateLineDistance(latLng1, latLng2);
    }
    /**
     * 求两个经纬度的中点
     * @param l1
     * @param l2
     * @return
     */
    public static LatLng getMidLatLng(LatLng l1, LatLng l2) {
        return new LatLng((l1.latitude + l2.latitude ) / 2, (l1.longitude + l2.longitude ) / 2);
    }

    //一段线
    public static Polyline drawOneLenthPolyline(AMap amap, List<Marker> markerList) {
        List<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions opts = new PolylineOptions();
        try {
            if (markerList == null) {
                return null;
            } else if (markerList.size() > 1) {
                Marker marker0 = markerList.get(markerList.size() - 2);
                Marker marker1 = markerList.get(markerList.size() - 1);
                points.add(marker0.getPosition());
                points.add(marker1.getPosition());
                opts.addAll(points);
                opts.width(10);
                opts.color(0xAAFF0000);
                return amap.addPolyline(opts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //一段线
    public static Polyline drawOneCLenthPolyline(AMap amap, List<LatLng> latLngList) {
        List<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions opts = new PolylineOptions();
        try {
            if (latLngList == null) {
                return null;
            } else if (latLngList.size() > 1) {
                points.add(latLngList.get(latLngList.size() - 2));
                points.add(latLngList.get(latLngList.size() - 1));
                opts.addAll(points);
                opts.width(10);
                opts.color(0xAAFF0000);
                return amap.addPolyline(opts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //封口线
    public static Polyline drawClosePolyline(AMap amap, List<Marker> markerList) {
        if (markerList != null) {
            if (markerList.size() > 1) {
                Marker marker0 = markerList.get(0);
                Marker marker1 = markerList.get(markerList.size() - 1);
                PolylineOptions optss = new PolylineOptions();
                List<LatLng> pointss = new ArrayList<LatLng>();
                pointss.add(marker0.getPosition());
                pointss.add(marker1.getPosition());
                optss.addAll(pointss);
                optss.width(10);
                optss.color(0xAAFF0000);
                return amap.addPolyline(optss);
            }

        }
        return null;
    }

    //求点到线段的距离
    public static int pointToLineDistance(LatLng latLng1, LatLng latLng2, LatLng targetPoint) {
        //线段的长度
        float twoPDistance = AMapUtils.calculateLineDistance(latLng1, latLng2);
        //到点1的距离
        float toPoint1Distance = AMapUtils.calculateLineDistance(latLng1, targetPoint);
        float toPoint2Distance = AMapUtils.calculateLineDistance(latLng2, targetPoint);

        float adddiatance = toPoint1Distance + toPoint2Distance;

        return 0;
    }


    /**
     * @param {Object} latlng - {lat,lng}
     * @method 设置经纬度转换成页面像素坐标的方法
     */
    public static Point latlng2px(AMap aMap, LatLng latlng) {
        /**百度，map为 new BMap.Map() 对象*/
        return aMap.getProjection().toScreenLocation(latlng);


    }

    /**
     * @param {Array} px - [lng,lat]
     * @method 设置像素坐标转换成经纬度点的方法
     */
    public static LatLng px2latlng(AMap aMap, Point point) {
        /**百度，map为 new BMap.Map() 对象*/

        return aMap.getProjection().fromScreenLocation(point);


    }


}
