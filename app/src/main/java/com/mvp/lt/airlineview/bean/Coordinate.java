package com.mvp.lt.airlineview.bean;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/9/30/030
 */


public class Coordinate {
    private double x;
    private double y;
    private String name;
    public Coordinate(double x, double y, String name)
    {
        this.x = x;
        this.y = y;
        this.name = name;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
