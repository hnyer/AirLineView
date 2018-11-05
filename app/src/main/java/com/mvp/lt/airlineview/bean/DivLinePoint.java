package com.mvp.lt.airlineview.bean;

import android.graphics.Point;

/**
 * $name
 * 等分线，点
 *
 * @author ${LiuTao}
 * @date 2018/1/3/003
 */

public class DivLinePoint {
    double k;
    int b;
    Point p1;
    Point p2;

    public DivLinePoint() {

    }

    public DivLinePoint(double k, int b, Point p1, Point p2) {
        this.k = k;
        this.b = b;
        this.p1 = p1;
        this.p2 = p2;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }
}
