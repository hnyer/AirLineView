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
        List<LatLng> sampleList = readKml.getCoordinateList();
        if (markers.size() == 0) {
            //设置marker的图标为默认的天蓝色气泡
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            for (int i = 0; i < sampleList.size(); i++) {
                x = sampleList.get(i).latitude;//获取marker的坐标值
                y = sampleList.get(i).longitude;
                markerOption = new MarkerOptions();
                markerOption.setFlat(true);
                markerOption.anchor(0.5f, 0.5f);
                markerOption.icon(bitmapDescriptor);
                markerOption.position(new LatLng(x, y));
                Log.e("SoilSampleUtil", String.valueOf(i) + "-->" + String.valueOf(x) + "," + String.valueOf(y));
//                if (i == 0) {
//                    aMap.moveCamera(CameraUpdateFactory.
//                            changeLatLng(new LatLng(x, y)));
//                }
                try {
                    if (aMapUtil != null) {
                        Marker marker = aMapUtil.addMarker(markerOption);
                        markers.add(marker);
                    } else {
                        Log.d("SoilSampleUtil", "aMap is null !!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }
}
