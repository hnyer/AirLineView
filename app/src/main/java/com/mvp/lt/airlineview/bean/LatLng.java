package com.mvp.lt.airlineview.bean;



import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/12.
 */
public class LatLng implements Serializable{
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
