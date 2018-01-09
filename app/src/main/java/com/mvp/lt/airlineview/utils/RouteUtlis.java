package com.mvp.lt.airlineview.utils;

import android.graphics.Point;
import android.util.Log;

import com.mvp.lt.airlineview.DivLinePoint;
import com.mvp.lt.airlineview.LineGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 直线交点的算法
 *
 * @author ${LiuTao}
 * @date 2017/12/30/030
 */

public class RouteUtlis {
    private static String TAG = "RouteUtlis";
    private static double sSlope;

    /**
     * 求两点之间的距离
     */

    public static double disTancePoint(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }


    /**
     * 已知两点求直线方程 等分点
     * 等分点
     *
     * @param p1     起点
     * @param p2     终点
     * @param divDis 等分长度
     * @return 等分点
     */
    public static List<Point> getLinePoint(Point p1, Point p2, double divDis) {//divdis =1
        int disNum;
        List<Point> list = new ArrayList<>();
        double dis = disTancePoint(p1, p2);
        if (divDis > dis / 2) {
            disNum = 1;
            divDis = dis / 2;
        } else {
            disNum = (int) (dis / divDis); //等分个数
        }
        if (p1.equals(p2)) { //同一个点
            list.add(p1);
        }
        if (p1.x == p2.x) {
            int divStep = (int) ((p1.y > p2.y) ? -divDis : divDis);//divstep = 1
            if (p1.y >= p2.y) {
                for (int i = p1.y + divStep; i >= p2.y; i += divStep) {
                    list.add(new Point(p1.x, i));
                }
            } else {
                for (int i = p1.y + divStep; i <= p2.y; i += divStep) {
                    list.add(new Point(p1.x, i));
                }
            }

        } else if (p1.y == p2.y) {
            int divStep = (int) ((p1.x > p2.x) ? -divDis : divDis);//divstep = 1
            if (p1.y >= p2.y) { // divstep = -1
                for (int i = p1.x + divStep; i >= p2.x; i += divStep) {
                    list.add(new Point(i, p1.y));
                }
            } else { // divstep = 1
                for (int i = p1.x + divStep; i <= p2.x; i += divStep) {
                    list.add(new Point(i, p1.y));
                }
            }
        } else {

            double s1 = p2.y - p1.y;
            double s2 = p2.x - p1.x;
            double slope = s1 / s2;
            if (p2.x > p1.x) {
                for (int i = 0; i < disNum; i++) {
                    double everyDis = (divDis + divDis * i);
                    Point p0 = new Point();
                    //已知斜率 slope, 和 点（p2.x,p2.y） 直线 y = slope*x +p2.y-slope*p2.x;
                    double x = (everyDis * (p2.x - p1.x) / dis) + p1.x;
                    p0.x = (int) x;
                    p0.y = (int) (slope * x + p2.y - slope * p2.x);
                    list.add(p0);
                }
            }
            if (p2.x < p1.x) {
                for (int i = 0; i < disNum; i++) {
                    double everyDis = (divDis + divDis * i);
                    Point p0 = new Point();
                    double x = p1.x - (everyDis * (p1.x - p2.x) / dis);
                    double y = slope * x + p2.y - slope * p2.x;
                    p0.x = (int) x;
                    p0.y = (int) y;
                    list.add(p0);
                }
            }
        }
        return list;
    }

    /**
     * @param edgPointList 所有的范围点
     * @param flag      x,y轴 方向
     * @return
     */
    public static Point getMaxDisPonit(Point startPoint, List<Point> edgPointList, String flag) {
        int index = 0;
        if (flag == null) {
            flag = "x";
        }
        double maxX = Math.abs(edgPointList.get(0).x - startPoint.x);
        double maxY = Math.abs(edgPointList.get(0).y - startPoint.y);
        double maxDis = disTancePoint(startPoint, edgPointList.get(0));
        switch (flag) {
            case "x":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = Math.abs(edgPointList.get(i).x - startPoint.x);
                    if (maxX < num) {
                        maxX = num;
                        index = i;
                    }
                }
                break;
            case "y":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = Math.abs(edgPointList.get(i).y - startPoint.y);
                    if (maxY < num) {
                        maxY = num;
                        index = i;
                    }
                }
                break;
            case "m":
                //求中点
                if ((edgPointList.size() - 1) % 2 == 0) {  //偶数
                    int x = (edgPointList.size() - 1) / 2;
                    Point p1 = edgPointList.get(x);
                    Point p2 = edgPointList.get(x + 1);
                    int dis1 = (int) disTancePoint(startPoint, p1);
                    int dis2 = (int) disTancePoint(startPoint, p2);
                    if (dis1 > dis2) {
                        index = x;
                    } else {
                        index = x + 1;
                    }
                } else {//奇数
                    index = edgPointList.size() / 2;
                }
                break;
            case "d":
                for (int i = 0; i < edgPointList.size(); i++) {
                    double num = disTancePoint(startPoint, edgPointList.get(i));
                    if (maxDis < num) {
                        maxDis = num;
                        index = i;
                    }
                }
                break;
        }
        return edgPointList.get(index);
    }

    /**
     * 得到目标点的斜率
     * 首尾相连
     *
     * @param tartgetPoint
     */
    public static List<Double> getSlope(Point startPoint, Point endPoint, List<Point> tartgetPoint) {

        for (int i = 0; i < tartgetPoint.size(); i++) {
            //double slope = ((double) (endPoint.y - startPoint.y)) / ((double) (endPoint.x - startPoint.x));
        }
        return null;
    }

    //得到等分线点的组合
    public static List<DivLinePoint> getDivLineAndPoint(Point startPoint, Point endPoint, List<Point> divPointList) {
        double s1 = endPoint.y - startPoint.y;
        double s2 = endPoint.x - startPoint.x;
        List<DivLinePoint> divLinePointList = new ArrayList<>();
        if (s2 == 0) {
            //等分线
            for (int i = 0; i < divPointList.size(); i++) {
                //第一个点
                DivLinePoint divLinePoint = new DivLinePoint();
                divLinePoint.setP1(divPointList.get(i));

                //第二个点
                Point Point = new Point();
                Point.x = divPointList.get(i).x + 50;
                Point.y = divPointList.get(i).y;
                divLinePoint.setP2(Point);
                divLinePointList.add(divLinePoint);
            }
        } else if (s1 == 0) {
            //等分线
            for (int i = 0; i < divPointList.size(); i++) {
                //第一个点
                DivLinePoint divLinePoint = new DivLinePoint();
                divLinePoint.setP1(divPointList.get(i));

                //第二个点
                Point Point = new Point();
                Point.x = divPointList.get(i).x;
                Point.y = divPointList.get(i).y + 50;
                divLinePoint.setP2(Point);
                divLinePointList.add(divLinePoint);
            }
        } else {
            //已知斜率 k, 和 点（a,b） 直线 y = kx +b-ka;
            sSlope = s1 / s2;
            double verticalSlope = -(1 / sSlope); //斜率
            Log.e(TAG, "slope:" + sSlope + ",verticalSlope:" + verticalSlope);
            //等分线
            for (int i = 0; i < divPointList.size(); i++) {
                //第一个点
                DivLinePoint divLinePoint = new DivLinePoint();
                divLinePoint.setP1(divPointList.get(i));
                //第二个点
                Point point = new Point();
                int x2 = divPointList.get(i).x + 50;
                int y2 = (int) (verticalSlope * x2 + divPointList.get(i).y - verticalSlope * divPointList.get(i).x);
                point.x = x2;
                point.y = y2;
                divLinePoint.setP2(point);
                divLinePointList.add(divLinePoint);
            }

        }
        return divLinePointList;
    }

    /**
     * 边缘点 交点
     *
     * @param divLinePointList 等分线
     * @param lineGroupList    范围线
     * @return
     */
    public static List<Point> getAllEdgPoint(Point startPoint, List<DivLinePoint> divLinePointList, List<LineGroup> lineGroupList, Point endPoint) {
        List<Point> allEdgPoint = new ArrayList<>();
        for (int i = 0; i < divLinePointList.size(); i++) {
            DivLinePoint divLinePoint = divLinePointList.get(i);
            if (i == 0) {
                allEdgPoint.add(startPoint);
            }
            if (i % 2 == 0) {
                for (int j = 0; j < lineGroupList.size(); j++) {
                    LineGroup lineGroup = lineGroupList.get(j);
                    Point point = getIntersectionPoint(divLinePoint.getP1(), divLinePoint.getP2(), lineGroup.getP1(), lineGroup.getP2());
                    if (point.x != 0 && point.y != 0) {
                        if (!allEdgPoint.contains(point))
                            allEdgPoint.add(point);
                    }

                }
            } else {
                for (int j = lineGroupList.size() - 1; j >= 0; j--) {
                    LineGroup lineGroup = lineGroupList.get(j);
                    Point point = getIntersectionPoint(divLinePoint.getP1(), divLinePoint.getP2(), lineGroup.getP1(), lineGroup.getP2());
                    if (point.x != 0 && point.y != 0) {
                        if (!allEdgPoint.contains(point))
                            allEdgPoint.add(point);
                    }

                }
            }
            if (i == divLinePointList.size() - 1) {
                allEdgPoint.add(endPoint);
            }
        }
        return allEdgPoint;
    }

    /**
     * 范围点组成的线段集合
     */
    public static List<LineGroup> getVerticalLinePiont2(List<Point> targetPoint) {
        Log.e("TAG", "targetPoint:" + targetPoint.size());
        List<LineGroup> lineGroupsList = new ArrayList<>();
        //范围点围成线段
        LineGroup lineGroup;
        for (int i = 0; i < targetPoint.size(); i++) {
            if (i == targetPoint.size() - 1) {
                lineGroup = new LineGroup(targetPoint.get(i), targetPoint.get(0));
            } else {
                lineGroup = new LineGroup(targetPoint.get(i), targetPoint.get(i + 1));
            }

            lineGroupsList.add(lineGroup);
        }
        return lineGroupsList;
    }

    /**
     * 得到交点
     * 行列式
     *
     * @param aa 直线点
     * @param bb 直线点
     * @param cc 线段点
     * @param dd 线段点
     * @return
     */
    public static Point getIntersectionPoint(Point aa, Point bb, Point cc, Point dd) {
        Point point = new Point();
        double delta = determinant(bb.x - aa.x, cc.x - dd.x, bb.y - aa.y, cc.y - dd.y);
        if (delta > (1e-6) || delta < -(1e-6))  // delta=0，排除两线段重合或平行的情况
        {
            //
            double namenda = determinant(cc.x - aa.x, cc.x - dd.x, cc.y - aa.y, cc.y - dd.y) / delta;
            double miu = determinant(bb.x - aa.x, cc.x - aa.x, bb.y - aa.y, cc.y - aa.y) / delta;
            //求交点
            double x = aa.x + namenda * (bb.x - aa.x);
            double y = aa.y + namenda * (bb.y - aa.y);
            System.out.println("X:" + x + ",Y:" + y);
            if (cc.x <= dd.x) {
                if (x >= cc.x && x <= dd.x) {
                    if (x != aa.x && y != aa.y) {
                        point.x = (int) x;
                        point.y = (int) y;
                    }

                }
            } else if (cc.x > dd.x) {
                if (x >= dd.x && x <= cc.x) {
                    if (x != aa.x && y != aa.y) {
                        point.x = (int) x;
                        point.y = (int) y;
                    }
                }
            }
            System.out.println("getX:" + point.x + ",getY:" + point.y);
        }
        return point;
    }

    public static double determinant(double v1, double v2, double v3, double v4)  // 行列式
    {
        return (v1 * v4 - v2 * v3);
    }
}
