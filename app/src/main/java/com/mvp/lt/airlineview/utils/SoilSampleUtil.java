package com.mvp.lt.airlineview.utils;

import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/9/30/030
 */


public class SoilSampleUtil {
    public static AMap aMapUtil;//添加marker到地图上需要使用AMap类的实例化对象

    private static MarkerOptions markerOption;
    private static ArrayList<Marker> markers = new ArrayList<Marker>();

    static double x = 0.0;
    static double y = 0.0;

    public static void addSampleMarkersData(AMap aMap,ReadKml readKml) {


    }
}
