package com.mvp.lt.airlineview.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * $activityName
 *  视频关联轨迹
 *  *  * @author LiuTao
 * @date 2018/8/4/004
 */


public class RecodeHistroyTrailDate implements Serializable {
    private ArrayList<LatLng> mLngArrayList = null;
    private long timeSec;
    private String endTimeSec;
    private String startTimeSec;

    public ArrayList<LatLng> getLngArrayList() {
        return mLngArrayList;
    }

    public void setLngArrayList(ArrayList<LatLng> lngArrayList) {
        mLngArrayList = lngArrayList;
    }

    public long getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(long timeSec) {
        this.timeSec = timeSec;
    }

    public String getEndTimeSec() {
        return endTimeSec;
    }

    public void setEndTimeSec(String endTimeSec) {
        this.endTimeSec = endTimeSec;
    }

    public String getStartTimeSec() {
        return startTimeSec;
    }

    public void setStartTimeSec(String startTimeSec) {
        this.startTimeSec = startTimeSec;
    }
}
