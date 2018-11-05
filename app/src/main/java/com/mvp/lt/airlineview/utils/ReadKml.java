package com.mvp.lt.airlineview.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.mvp.lt.airlineview.bean.Coordinate;
import com.mvp.lt.airlineview.ui.MainActivity;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/9/30/030
 */


public class ReadKml {
    public static boolean addSampleSuccess = false; //判断读取KML是否成功
    private Coordinate coordinate = null; //存储从KML文件中读取出来的坐标值和name
    private static List<LatLng> coordinateList = new ArrayList();//存储每次实例化的Coordinate对象，每个Coordinate都保存着不同的x,y,name
    private MainActivity activity;
    private Polygon mPolygon;
    private String kmlPath = "/storage/emulated/0/Android/kml演示.kml";

    public ReadKml(MainActivity activity) {
        this.activity = activity;
    }

    public void parseKmlFile() throws Exception {
        File file = new File(kmlPath);//pathName为KML文件的路径
        try {
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = null;
            InputStream inputStream = null;
            ZipEntry entry = null;
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = entry.getName();
                if (zipEntryName.endsWith("kml") || zipEntryName.endsWith("kmz")) {
                    inputStream = zipFile.getInputStream(entry);
                    //parseXmlWithDom4j(inputStream);
                } else if (zipEntryName.endsWith("png")) {

                }
            }
            zipInputStream.close();
            inputStream.close();
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseKml(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getAssets().open("kml演示.kml");
            if (is != null) {
                int lenght = is.available();
                byte[] buffer = new byte[lenght];
                is.read(buffer);
                String result = new String(buffer, "utf8");
                Log.e("kml", result);
                parseXmlWithDom4j(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Boolean parseXmlWithDom4j(String xml) throws Exception {

        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            //读取xml字符串，注意这里要转成输入流
            document = reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
            //  document = reader.read(input);
            //读取xml字符串，注意这里要转成输入流
            Element root = document.getRootElement();
            //读取xml字符串，注意这里要转成输入流
            Element node = root.element("Document");
            listNodes(node);
            addSampleSuccess = true;

        } catch (DocumentException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return addSampleSuccess;
    }

    //遍历当前节点下的所有节点
    public void listNodes(Element node) {
        //根节点名
        //Placemark节点中的name属性
        try {
            //如果当前节点是Placemark就解析其子节点
            if ("Placemark".equals(node.getName())) {
                //遍历Point节点的所有子节点
                Element i1 = node.element("Polygon");
                Element i2 = i1.element("outerBoundaryIs");
                Element i3 = i2.element("LinearRing");
                String nodeContent = i3.elementText("coordinates");
                Log.e("while", "nodeContent:" + nodeContent);
                String nodeContentSplit[] = null;
                nodeContentSplit = nodeContent.trim().split(" ");
                for (int i = 0; i < nodeContentSplit.length; i++) {
                    String[] coorDinats = nodeContentSplit[i].split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(coorDinats[1]), Double.parseDouble(coorDinats[0]));
                    coordinateList.add(activity.convert(latLng));
                    activity.polygons = coordinateList;
                }
                activity.aMap.moveCamera(CameraUpdateFactory.changeLatLng(coordinateList.get(0)));
                activity.aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

                activity.drawWaypointOrcPicture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    public List<LatLng> getCoordinateList() {
        return this.coordinateList;
    }

}
