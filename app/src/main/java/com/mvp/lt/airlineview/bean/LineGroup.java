package com.mvp.lt.airlineview.bean;

import android.graphics.Point;

/**
 * $name
 *  范围点的线段组合
 * @author ${LiuTao}
 * @date 2018/1/3/003
 */

public class LineGroup {
    Point p1;
    Point p2;
    public LineGroup() {

    }

    public LineGroup(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
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
