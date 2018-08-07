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
        double steps = (distance(bounds.get(1), bounds.get(4)) / space);
        //纬度差
        double lat = (bounds.get(1).latitude - bounds.get(4).latitude) / steps;
        integers[0] = steps;
        integers[1] = lat;
        return integers;
    }

    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式
    {
        return (v1 * v3 - v2 * v4);
    }

    public static boolean intersect3(Point aa, Point bb, Point cc, Point dd) {
        double delta = determinant(bb.x - aa.x, cc.x - dd.x, bb.y - aa.y, cc.y - dd.y);
        if (delta <= (1e-6) && delta >= -(1e-6))  // delta=0，表示两线段重合或平行
        {
            return false;
        }
        double namenda = determinant(cc.x - aa.x, cc.x - dd.x, cc.y - aa.y, cc.y - dd.y) / delta;
        if (namenda > 1 || namenda < 0) {
            return false;
        }
        double miu = determinant(bb.x - aa.x, cc.x - aa.x, bb.y - aa.y, cc.y - aa.y) / delta;
        if (miu > 1 || miu < 0) {
            return false;
        }
        return true;
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

    public static int sint(int i, int len) {
        if (i > len - 1) {
            return i - len;
        }
        if (i < 0) {
            return len + i;
        }
        return i;
    }

    //    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式{
//        return (v1*v3-v2*v4);
//    }


    //计算纬度线 与边缘线的交点
    public void calcCrossoverPointLatlngs(List<LatLng> rect, List<LatLng> latLngList, Double[] doubles) {

    }

    //计算两个点的距离
    public static float distance(LatLng latLng1, LatLng latLng2) {
        return AMapUtils.calculateLineDistance(latLng1, latLng2);
    }

    /**
     * 求两个经纬度的中点
     *
     * @param l1
     * @param l2
     * @return
     */
    public static LatLng getMidLatLng(LatLng l1, LatLng l2) {
        return new LatLng((l1.latitude + l2.latitude) / 2, (l1.longitude + l2.longitude) / 2);
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

    //点到直线的距离
    public static double PointToSegDist(double x, double y, double x1, double y1, double x2, double y2) {
        double cross = (x2 - x1) * (x - x1) + (y2 - y1) * (y - y1);
        if (cross <= 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (cross >= d2) return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        double r = cross / d2;
        double px = x1 + (x2 - x1) * r;
        double py = y1 + (y2 - y1) * r;
        return Math.sqrt((x - px) * (x - px) + (py - y) * (py - y));

    }



    public static boolean orientations(Point p, Point q, Point c) {
        int val = (q.y - p.y) * (c.x - q.x) - (q.x - p.x) * (c.y - q.y);
        if (val == 0) return true;  // colinear
        return false;
    }
    //计算交点
    public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        // General case
        if (o1 != o2 && o3 != o4) return true;
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        // p1, q1 and p2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;
        return false;
    }
    /**
     * @param p
     * @param q
     * @param r
     * @return 0 --> p, q and r are colinear, 1 --> 顺时针方向, 2 --> 逆时钟方向
     */
    public static int orientation(Point p, Point q, Point r) {
        int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;  // colinear
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }


    public static boolean onSegment(Point p, Point q, Point r) {
        if (q.x <= Math.max(p.x, r.x)
                && q.x >= Math.min(p.x, r.x)
                && q.y <= Math.max(p.y, r.y)
                && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }

    /**
     * @param lat_a 纬度1 y
     * @param lng_a 经度1 x
     *              <p>
     *              目标点/兴趣点
     * @param lat_b 纬度2 y
     * @param lng_b 经度2 x
     * @return
     */
    public static double getAngle(double lat_a, double lng_a, double lat_b, double lng_b) {
        double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
        double x = Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        if (brng < 0)
            brng = brng + 360;
        return brng;

    }

    /**
     * 根据坐标系中的3点确定夹角的方法（注意：夹角是有正负的）
     * 0-->180  0--> -180
     */
    public static float angle(Point cen, Point first, Point second) {
        float dx1, dx2, dy1, dy2;

        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1 * dx1 + dy1 * dy1;
        float ob2 = dx2 * dx2 + dy2 * dy2;

        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x)) > 0;

        // 根据余弦定理计算旋转角的余弦值
        double cosDegree = (oa2 + ob2 - ab2) / (2 * Math.sqrt(oa2) * Math.sqrt(ob2));

        // 异常处理，因为算出来会有误差绝对值可能会超过一，所以需要处理一下
        if (cosDegree > 1) {
            cosDegree = 1;
        } else if (cosDegree < -1) {
            cosDegree = -1;
        }

        // 计算弧度
        double radian = Math.acos(cosDegree);

        // 计算旋转过的角度，顺时针为正，逆时针为负
        float angel = (float) (isClockwise ? Math.toDegrees(radian) : -Math.toDegrees(radian));
        return parseHeadYaw(angel);
    }

    /**
     * 角度转换-180~180转成0~360
     *
     * @param value
     * @return
     */
    private static float parseHeadYaw(float value) {
        float yaw = 0;
        if (value < 0) {
            yaw = -value;
        } else if (value > 0) {
            yaw = 360 - value;
        } else {
            yaw = value;
        }

        return yaw;
    }
}
