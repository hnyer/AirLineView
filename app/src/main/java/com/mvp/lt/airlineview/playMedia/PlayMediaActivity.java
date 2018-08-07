package com.mvp.lt.airlineview.playMedia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.google.gson.Gson;
import com.mvp.lt.airlineview.R;
import com.mvp.lt.airlineview.bean.RecodeHistroyTrailDate;
import com.mvp.lt.airlineview.utils.FileUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/8/6/006
 */


public class PlayMediaActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceview;
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.select_path)
    Button mSelectPath;
    @BindView(R.id.path_show)
    TextView mPathShow;
    @BindView(R.id.play)
    Button mPlay;
    private MediaPlayer player;//媒体播放器
    private int position = 0;
    private String mediaPath = null;
    private SurfaceHolder mSurfaceHolder;
    private boolean isPause = false;
    private List<LatLng> mListPolylines;
    private RecodeHistroyTrailDate mRecodeHistroyTrailDate;
    private Polyline mPolyline;
    private long mTimeFifference;
    private SmoothMoveMarker mSmoothMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_media_activity);
        ButterKnife.bind(this);
        mPlay.setEnabled(false);
        mMapView.onCreate(savedInstanceState);
        initMap();
        mSurfaceHolder = mSurfaceview.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlay.setEnabled(true);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (player != null) {
                    position = player.getCurrentPosition();
                    //stop();
                }
            }
        });
        mSelectPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(intent, 1);
            }
        });
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMedia();
            }
        });
    }
    public String getsaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/" + "com.uav.dji.quantum" + "/";
            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }

            return rootDir;
        } else {
            return null;
        }
    }
    private void playMedia() {
        mPlay.setEnabled(false);

        if (isPause) {
            isPause = false;
            player.start();
            return;
        }
        if (mediaPath == null) {
            return;
        }
        File file = new File(mediaPath);
        if (!file.exists()) {
            return;
        }
        try {
            if (player == null) {
                player = new MediaPlayer();
            }
            player.setDataSource(mediaPath);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlay.setEnabled(true);
                    stopMedia();
                }
            });
            player.prepare();
            player.setDisplay(mSurfaceHolder);//将影像播放控件与媒体播放控件关联起来

            player.start();
            startMove(mListPolylines, (int) mTimeFifference);
            //startMove(mListPolylines, (int) mTimeFifference);
            // startSmoothMove(mListPolylines, (int) mTimeFifference);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void stopMedia() {
        isPause = false;
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            mPlay.setEnabled(true);
        }
    }

    DecimalFormat df = new DecimalFormat("#.0000000000000");

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == 1) {
                    Uri uri = data.getData();
                    String path = getPath(uri);
                    mPathShow.setText(path + "");
                    mediaPath = path;
                    if (mediaPath != null) {
                        File file = new File(mediaPath);
                        String name = file.getName();
                        Log.e("onActivityResult", name);
                        String filePath = mediaPath.substring(0, mediaPath.lastIndexOf("."));

                        Log.e("onActivityResult", filePath);
                        Gson gson = new Gson();

                        String value = FileUtils.readtext(filePath);

                        if (value != null) {
                            mRecodeHistroyTrailDate = gson.fromJson(value, RecodeHistroyTrailDate.class);
                            Log.e("时间差", mRecodeHistroyTrailDate.getTimeSec()
                                    + ",开始时间：" + mRecodeHistroyTrailDate.getStartTimeSec()
                                    + ",结束时间：" + mRecodeHistroyTrailDate.getEndTimeSec());
                            long st = Long.parseLong(mRecodeHistroyTrailDate.getStartTimeSec());
                            long et = Long.parseLong(mRecodeHistroyTrailDate.getEndTimeSec());
                            mTimeFifference = (et - st) / 1000;
                            Log.e("onActivityResult", "秒:" + mTimeFifference);
                            mListPolylines = new ArrayList<>();

                            if (mRecodeHistroyTrailDate.getLngArrayList() != null) {
                                for (int i = 0; i < mRecodeHistroyTrailDate.getLngArrayList().size(); i = i + 10) {
                                    double lat = mRecodeHistroyTrailDate.getLngArrayList().get(i).getLat();
                                    double lng = mRecodeHistroyTrailDate.getLngArrayList().get(i).getLng();
                                    mListPolylines.add(new LatLng(lat, lng));
                                    Log.e("轨迹", lat + "::" + lng + "");
                                    addMarkersToMap(new LatLng(lat, lng));
                                }
                            }
                            if (mPolyline != null) {
                                mPolyline.remove();
                            }
                            mPolyline = aMap.addPolyline(new PolylineOptions().
                                    setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr)) //setCustomTextureList(bitmapDescriptors)
                                    .addAll(mListPolylines)
                                    .useGradient(true)
                                    .width(20));
                            LatLngBounds bounds = new LatLngBounds(mListPolylines.get(0), mListPolylines.get(mListPolylines.size() - 2));
                            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                        }

                    }

                }

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始移动
     * List<LatLng> points, int time
     */
    public void startMove(List<LatLng> points, int time) {

        if (mPolyline == null) {

            return;
        }
        // 获取轨迹坐标点
        if (points == null) {
            return;
        }
        if (points.size() == 0) {
            return;
        }

        // 构建 轨迹的显示区域
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        if (mSmoothMarker != null) {
            mSmoothMarker.removeMarker();
        }
        // 实例 SmoothMoveMarker 对象
        mSmoothMarker = new SmoothMoveMarker(aMap);
        // 设置 平滑移动的 图标
        mSmoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));

        // 取轨迹点的第一个点 作为 平滑移动的启动
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置轨迹点
        mSmoothMarker.setPoints(subList);
        // 设置平滑移动的总时间  单位  秒
        mSmoothMarker.setTotalDuration(time);

        // 设置  自定义的InfoWindow 适配器
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        // 显示 infowindow
        mSmoothMarker.getMarker().showInfoWindow();

        // 设置移动的监听事件  返回 距终点的距离  单位 米
        mSmoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
            @Override
            public void move(final double distance) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (infoWindowLayout != null && title != null) {

                            title.setText("距离终点还有： " + (int) distance + "米");
                        }
                    }
                });

            }
        });

        // 开始移动
        mSmoothMarker.startSmoothMove();

    }

    /**
     * 个性化定制的信息窗口视图的类
     * 如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。
     * 如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {

        // 个性化Marker的InfoWindow 视图
        // 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会调用getInfoContents(Marker)方法获取
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        // 这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
        // 定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染
        @Override
        public View getInfoContents(Marker marker) {

            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    /**
     * 自定义View并且绑定数据方法
     *
     * @param marker 点击的Marker对象
     * @return 返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(this);
            snippet = new TextView(this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLngs) {
        //添加标记
        MarkerOptions mark = new MarkerOptions();
        mark.position(latLngs);
        mark.visible(true);
        mark.snippet("1");
        mark.title("2");
        Marker marker = aMap.addMarker(mark);
        marker.setZIndex(2);
    }

    public String getPath(Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        player.stop();
        player.release();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    private RxPermissions mRxPermissions;
    //定位监听
    private LocationSource.OnLocationChangedListener mListener = null;
    /*地图相关*/
    private AMap aMap;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private UiSettings mUiSettings;

    //声明AMapLocationClient类对象
    private AMapLocationClient mlocationClient;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    /**
     * 设置一些amap的属性
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();

        }


    }

    List<Polyline> dragPolylines = new ArrayList<>();

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.mylocation));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.location));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位资源。如果不设置此定位资源则定位按钮不可点击。并且实现activate激活定位,停止定位的回调方法
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        mUiSettings.setRotateGesturesEnabled(false);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);


    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                //定位成功回调信息，设置相关消息
                // addMarkerToMap(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                //  Log.e("location", aMapLocation.getLatitude() + ";" + aMapLocation.getLongitude() + "");
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;

                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 读取坐标点
     *
     * @return
     */
    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }

    /**
     * 坐标点数组数据
     */
    private double[] coords = {116.3499049793749, 39.97617053371078,
            116.34978804908442, 39.97619854213431, 116.349674596623,
            39.97623045687959, 116.34955525200917, 39.97626931100656,
            116.34943728748914, 39.976285626595036, 116.34930864705592,
            39.97628129172198, 116.34918981582413, 39.976260803938594,
            116.34906721558868, 39.97623535890678, 116.34895185151584,
            39.976214717128855, 116.34886935936889, 39.976280148755315,
            116.34873954611332, 39.97628182112874, 116.34860763527448,
            39.97626038855863, 116.3484658907622, 39.976306080391836,
            116.34834585430347, 39.976358252119745, 116.34831166130878,
            39.97645709321835, 116.34827643560175, 39.97655231226543,
            116.34824186261169, 39.976658372925556, 116.34825080406188,
            39.9767570732376, 116.34825631960626, 39.976869087779995,
            116.34822111635201, 39.97698451764595, 116.34822901510276,
            39.977079745909876, 116.34822234337618, 39.97718701787645,
            116.34821627457707, 39.97730766147824, 116.34820593515043,
            39.977417746816776, 116.34821013897107, 39.97753930933358
            , 116.34821304891533, 39.977652209132174, 116.34820923399242,
            39.977764016531076, 116.3482045955917, 39.97786190186833,
            116.34822159449203, 39.977958856930286, 116.3482256370537,
            39.97807288885813, 116.3482098441266, 39.978170063673524,
            116.34819564465377, 39.978266951404066, 116.34820541974412,
            39.978380693859116, 116.34819672351216, 39.97848741209275,
            116.34816588867105, 39.978593409607825, 116.34818489339459,
            39.97870216883567, 116.34818473446943, 39.978797222300166,
            116.34817728972234, 39.978893492422685, 116.34816491505472,
            39.978997133775266, 116.34815408537773, 39.97911413849568,
            116.34812908154862, 39.97920553614499, 116.34809495907906,
            39.979308267469264, 116.34805113358091, 39.97939658036473,
            116.3480310509613, 39.979491697188685, 116.3480082124968,
            39.979588529006875, 116.34799530586834, 39.979685789111635,
            116.34798818413954, 39.979801430587926, 116.3479996420353,
            39.97990758587515, 116.34798697544538, 39.980000796262615,
            116.3479912988137, 39.980116318796085, 116.34799204219203,
            39.98021407403913, 116.34798535084123, 39.980325006125696,
            116.34797702460183, 39.98042511477518, 116.34796288754136,
            39.98054129336908, 116.34797509821901, 39.980656820423505,
            116.34793922017285, 39.98074576792626, 116.34792586413015,
            39.98085620772756, 116.3478962642899, 39.98098214824056,
            116.34782449883967, 39.98108306010269, 116.34774758827285,
            39.98115277119176, 116.34761476652932, 39.98115430642997,
            116.34749135408349, 39.98114590845294, 116.34734772765582,
            39.98114337322547, 116.34722082902628, 39.98115066909245,
            116.34708205250223, 39.98114532232906, 116.346963237696,
            39.98112245161927, 116.34681500222743, 39.981136637759604,
            116.34669622104072, 39.981146248090866, 116.34658043260109,
            39.98112495260716, 116.34643721418927, 39.9811107163792,
            116.34631638374302, 39.981085081075676, 116.34614782996252,
            39.98108046779486, 116.3460256053666, 39.981049089345206,
            116.34588814050122, 39.98104839362087, 116.34575119741586,
            39.9810544889668, 116.34562885420186, 39.981040940565734,
            116.34549232235582, 39.98105271658809, 116.34537348820508,
            39.981052294975264, 116.3453513775533, 39.980956549928244
    };
}
