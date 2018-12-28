package com.mvp.lt.airlineview.kml.bean;


import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * $activityName
 * KML点集合
 *
 * @author LiuTao
 * @date 2018/12/5/005
 */


public class KmlLatLnglistBean {
    private String type;
    private List<LatLng> mLatLngList = null;

    public KmlLatLnglistBean(String type, List<LatLng> latLngList) {
        this.type = type;
        mLatLngList = latLngList;
    }

    public List<LatLng> getLatLngList() {
        return mLatLngList;
    }

    public void setLatLngList(List<LatLng> latLngList) {

        mLatLngList = latLngList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
