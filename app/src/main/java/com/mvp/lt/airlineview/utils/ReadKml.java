package com.mvp.lt.airlineview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.mvp.lt.airlineview.bean.Coordinate;
import com.mvp.lt.airlineview.kml.bean.KMLFileElementBean;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/9/30/030
 */


public class ReadKml {
    public static boolean addSampleSuccess = false; //判断读取KML是否成功
    private Coordinate coordinate = null; //存储从KML文件中读取出来的坐标值和name
    //    private static List<LatLng> coordinateList = null;//存储每次实例化的Coordinate对象，每个Coordinate都保存着不同的x,y,name
    private Context mContext;
    private Polygon mPolygon;
    private String kmlPath = "/storage/emulated/0/Android/kml演示.kml";

    private List<KMLFileElementBean> mKmlFileElementBeanList = null;
    private Map<String, Bitmap> mMap;

    public interface KmlHelperListener {
        void onSuccessPoint(List<KMLFileElementBean> fileElementBeans);

        void onFaid(String s);
    }

    public ReadKml(Context context) {
        this.mContext = context;

    }

    public void parseKMLOrKMZFileName(String name, final String kmlPath, final KmlHelperListener mKmlHelperListener) {
        if (name.toLowerCase().endsWith("kml")) {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    Log.e("路径", kmlPath);
                    e.onNext(FileUtils.readtext(kmlPath));

                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String result) throws Exception {
                            parseKmlWithDom4j(result, mKmlHelperListener);
                        }
                    });
        } else if (name.toLowerCase().endsWith("kmz")) {
            try {
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        parseKmzFileWithDom4j(kmlPath, mKmlHelperListener);
                        e.onNext("");

                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String result) throws Exception {
                                addSampleSuccess = true;

                                mKmlHelperListener.onSuccessPoint(mKmlFileElementBeanList);

                            }
                        });
            } catch (Exception e) {
                mKmlHelperListener.onFaid("");
                e.printStackTrace();
            }
        }
    }

    /**
     * kml
     *
     * @param name
     * @param mKmlHelperListener
     */
    public void parseKml(String name, KmlHelperListener mKmlHelperListener) {
        InputStream is = null;
        try {
            is = mContext.getAssets().open(name);
            if (is != null) {
                int lenght = is.available();
                byte[] buffer = new byte[lenght];
                is.read(buffer);
                String result = new String(buffer, "utf8");
                Log.e("kml", result);
                parseKmlWithDom4j(result, mKmlHelperListener);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ZIP
     *
     * @param mKmlHelperListener
     * @throws Exception
     */
    public void parseKmzFileWithDom4j(String kmzPath, KmlHelperListener mKmlHelperListener) {
        File file = new File(kmzPath);//pathName为KML文件的路径
        try {
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = null;
            InputStream inputStream = null;
            ZipEntry entry = null;
            mMap = new HashMap<>();

            zipInputStream = new ZipInputStream(new FileInputStream(file));
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = entry.getName().toLowerCase();
                Log.e("压缩实体的名称：", zipEntryName);
                if (zipEntryName.endsWith("kml") || zipEntryName.endsWith("kmz")) {
                    inputStream = zipFile.getInputStream(entry);

                    parseKMZInputStreamWithDom4j(inputStream, mKmlHelperListener);
                } else if (zipEntryName.endsWith("png")) {
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    byte[] b = new byte[512];
                    int readedByteSize = 0;
                    while ((readedByteSize = zipInputStream.read(b)) != -1) {
                        byteArrayOut.write(b, 0, readedByteSize);
                    }
                    byteArrayOut.flush();
                    byteArrayOut.close();
                    InputStream isBitmap = new ByteArrayInputStream(byteArrayOut.toByteArray());
                    Bitmap bitmap = BitmapFactory.decodeStream(isBitmap);
                    mMap.put(zipEntryName, bitmap);
                    isBitmap.close();
                }
            }
            zipInputStream.close();
            inputStream.close();
        } catch (ZipException e) {
            mKmlHelperListener.onFaid("");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            mKmlHelperListener.onFaid("");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseKMZInputStreamWithDom4j(InputStream input, KmlHelperListener mKmlHelperListenr) {
        try {

            SAXReader reader = new SAXReader();
            //读取xml字符串，注意这里要转成输入流
            reader.setEncoding("iso-8859-1");
            Document document = reader.read(input);
            Element root = document.getRootElement();
            //读取xml字符串，注意这里要转成输入流
            Element node = root.element("Document");
            mKmlFileElementBeanList = new ArrayList<>();
            Log.e("节点", node.getName());//Document

            //使用递归
            Iterator iterator = root.elementIterator();
            while (iterator.hasNext()) {
                Element e = (Element) iterator.next();
                listNodes(e, mKmlHelperListenr);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            mKmlHelperListenr.onFaid("");
        }

    }


    private Boolean parseKmlWithDom4j(String xml, KmlHelperListener mKmlHelperListener) throws Exception {
        try {
            SAXReader reader = new SAXReader();
            reader.setEncoding("iso-8859-1");
            Document document = reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
            Element root = document.getRootElement();
            Element node = root.element("Document");
            mKmlFileElementBeanList = new ArrayList<>();
            Iterator<Element> iterator = node.elementIterator();
            while (iterator.hasNext()) {
                Element e = iterator.next();
                listNodes(e, mKmlHelperListener);
            }
            addSampleSuccess = true;
            mKmlHelperListener.onSuccessPoint(mKmlFileElementBeanList);
        } catch (DocumentException e) {
            // TODO: handle exception
            ToastUtils.showToast("文件读取出错");
            e.printStackTrace();
        }
        return addSampleSuccess;
    }

    KMLFileElementBean kmlFileElementBean = null;

    //遍历当前节点下的所有节点
    public void listNodes(Element node, KmlHelperListener mKmlHelperListenr) {
        try {
            Log.e("根节点名字1", node.getName());

            if ("Placemark".equals(node.getName())) {
                kmlFileElementBean = new KMLFileElementBean();
                String name = node.elementText("name");
                kmlFileElementBean.setName(name);
                Log.e("创建实例", name);
            }
            if ("Polygon".equals(node.getName()) && kmlFileElementBean != null) {
                kmlFileElementBean.setType("Polygon");
            }
            if ("Point".equals(node.getName()) && kmlFileElementBean != null) {
                kmlFileElementBean.setType("Point");
            }
            if ("coordinates".equals(node.getName())) {
                List<LatLng> coordinateList = new ArrayList<>();
                String nodeContent = node.getText();
                Log.e("nodes", nodeContent);
                String[] nodeContentSplit = nodeContent.trim().split(" ");
                for (int i = 0; i < nodeContentSplit.length; i++) {
                    String[] coorDinats = nodeContentSplit[i].split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(coorDinats[1]), Double.parseDouble(coorDinats[0]));
                    coordinateList.add(convert(latLng));
                }
                kmlFileElementBean.setLatLngs(coordinateList);
                mKmlFileElementBeanList.add(kmlFileElementBean);
            }
            Log.e("根节点名字2", node.getName());
        } catch (Exception e) {
            ToastUtils.showToast("读取kml文件格式出现异常");
            e.printStackTrace();
            mKmlHelperListenr.onFaid("");
        }
        //同时迭代当前节点下面的所有子节点
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e, mKmlHelperListenr);
        }

    }

    /**
     * 根据类型 转换 坐标
     * GPS转高德坐标
     */
    public LatLng convert(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter(mContext);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);

        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
}
