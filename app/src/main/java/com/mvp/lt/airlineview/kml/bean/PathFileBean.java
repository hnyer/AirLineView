package com.mvp.lt.airlineview.kml.bean;

/**
 * $activityName
 * KML文件Bean
 *
 * @author LiuTao
 * @date 2018/11/24/024
 */


public class PathFileBean {
    private String parentPath;
    private String currentName;


    public PathFileBean() {
    }

    public PathFileBean(String parentPath, String currentName) {
        this.parentPath = parentPath;
        this.currentName = currentName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }
}
