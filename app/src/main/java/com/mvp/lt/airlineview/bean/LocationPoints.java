package com.mvp.lt.airlineview.bean;

/**
 * $name
 *
 * @author ${LiuTao}
 * @date 2017/12/29/029
 */

public class LocationPoints {
    float x;
    float y;

    public LocationPoints() {

    }


    public LocationPoints(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    //计算两点之间的距离
    public double distanc(LocationPoints B) {
        return Math.sqrt((x - B.getX()) * (x - B.getX()) + (y - B.getY()) * (y - B.getY()));
    }

    /**
     * 是否是同一个点
     *
     * @param B
     * @return
     */
    public boolean isEquil(LocationPoints B) {
        return x == B.getX() && y == B.getY();
    }

    @Override
    public String toString() {
        return "Points{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
