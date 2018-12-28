package com.mvp.lt.airlineview.kml.bean;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/12/6/006
 */


public class KMLFileElementBean {
    /**
     * 名字
     */
    private String name;
    /**
     * 类型
     * Polygon
     * Point
     */
    private String type;
    /**
     * 集合
     */
    private List<LatLng> mLatLngs;
  /**
     * 图片
     */
    private String mBitmapName;

    public KMLFileElementBean() {
    }

    public KMLFileElementBean(String name, String type, List<LatLng> latLngs) {
        this.name = name;
        this.type = type;
        mLatLngs = latLngs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LatLng> getLatLngs() {
        return mLatLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {

        mLatLngs = latLngs;
    }

    public String getBitmapName() {
        return mBitmapName;
    }

    public void setBitmapName(String bitmapName) {
        mBitmapName = bitmapName;
    }
}
