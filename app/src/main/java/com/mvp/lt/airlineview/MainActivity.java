package com.mvp.lt.airlineview;

import android.Manifest;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.mvp.lt.airlineview.utils.RouteUtlis;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements AMap.OnMapClickListener, LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {
    @BindView(R.id.textGetBtn)
    Button mTextGetBtn;
    @BindView(R.id.text_get_tv)
    TextView mTextGetTv;
    @BindView(R.id.ds)
    DragScaleView mDs;
    @BindView(R.id.confirm)
    Button mConfirm;
    @BindView(R.id.clear)
    Button mClear;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.amap)
    MapView mMapView;
    @BindView(R.id.show)
    Button mShow;
    boolean isShow = true;
    @BindView(R.id.set_div_et)
    EditText mSetDivEt;
    //定位监听
    private OnLocationChangedListener mListener = null;
    /*地图相关*/
    private AMap aMap;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private RxPermissions mRxPermissions;
    // 范围点确定标志符
    private boolean isOK = true;
    //声明AMapLocationClient类对象
    private AMapLocationClient mlocationClient;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //等分线
    private List<Polyline> linePolygonList = new ArrayList<>();
    //范围点
    private List<Point> ScreenSelectPoints = new ArrayList<>();
    //航线拐点 屏幕点
    private List<Point> mAllEdgPointList = new ArrayList<>();
    //航线拐点 经纬度坐标点 与屏幕点对应
    private List<LatLng> mLinelatLngs = new ArrayList<>();
    //设置方向
    private List<MarkerOptions> listMarker = new ArrayList<>();
    //所选 选点的marker
    private List<Marker> markerList = new ArrayList<>();
    //选点 经纬度集合
    private List<LatLng> mLatLngs = new ArrayList<LatLng>();
    //所选点德 面的集合
    private List<Polygon> mPolygonList = new ArrayList<Polygon>();
    //线
    private List<Polyline> mPolylineList = new ArrayList<>();
    private String mS;
    //实际间隔距离 可自己设置
    private double divDis = 10;
    //屏幕默认距离 5dp
    private double mDpis = 5;
    //起点
    private Point mStartPoint = new Point();
    //终点
    private Point mEndPoint = new Point();

    //是否是重新选点
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    mDs.setVisibility(View.VISIBLE);
                } else {
                    mDs.setVisibility(View.GONE);
                    isShow = true;
                }

            }
        });
        mTextGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer stringBuffer = new StringBuffer();
                List<Point> list = RouteUtlis.getLinePoint(aMap.getProjection().toScreenLocation(mLatLngs.get(0)), aMap.getProjection().toScreenLocation(mLatLngs.get(1)), 20);
                for (int i = 0; i < list.size(); i++) {
                    Log.e("TAG", list.size() + "" + list.get(i).toString());
                    stringBuffer.append(list.get(i).toString());
                }
                addPoints(stringBuffer);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshUi();
            }
        });


        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDs.clearPath();
                mDs.setVisibility(View.INVISIBLE);
                clearLine();
            }
        });
        mMapView.onCreate(savedInstanceState);
        initMap();

    }

    private void refreshUi() {
        aMap.clear();
        initMap();
        for (int i = 0; i < listMarker.size(); i++) {
            aMap.addMarker(listMarker.get(i));
        }
        drawPolygon();
        if (!TextUtils.isEmpty(mSetDivEt.getText().toString())) {
            divDis = Double.valueOf(mSetDivEt.getText().toString());
        }
        ScreenSelectPoints.clear();
        mLinelatLngs.clear();
        linePolygonList.clear();
        isOK = false;
        isShow = false;
        //mDs.setVisibility(View.VISIBLE);
        mS = (String) mSpinner.getSelectedItem();
        if (mS != null)
            mDs.setStandPointTrack(mS);

        for (int i = 0; i < mLatLngs.size(); i++) {
            ScreenSelectPoints.add(LatLngToScreenLocation(mLatLngs.get(i)));
        }
        if (mLatLngs.size() > 2) {
            mDpis = divDis * (RouteUtlis.disTancePoint(ScreenSelectPoints.get(0), ScreenSelectPoints.get(1)) /
                    AMapUtils.calculateLineDistance(mLatLngs.get(0), mLatLngs.get(1)));
        }
        mDs.addPoint(ScreenSelectPoints);
        draw(ScreenSelectPoints, mDpis);
    }



    public void draw(List<Point> screenSelectPoints, double divDis) {
        if (screenSelectPoints.size() < 2) {
            return;
        }
        mStartPoint = screenSelectPoints.get(0);
        mEndPoint = RouteUtlis.getMaxDisPonit(screenSelectPoints.get(0), screenSelectPoints, mS);
        // 对角线上的等分点
        List<Point> list = RouteUtlis.getLinePoint(mStartPoint, mEndPoint, divDis);
        for (int i = 0; i < list.size(); i++) {
            Log.e("isDraw", "x:" + list.get(i).x + ",y:" + list.get(i).y);
        }
        List<DivLinePoint> divLinePointList = RouteUtlis.getDivLineAndPoint(mStartPoint, mEndPoint, list); //等分线
        List<LineGroup> lineGroupList = RouteUtlis.getVerticalLinePiont2(screenSelectPoints);// 范围线
        mAllEdgPointList = RouteUtlis.getAllEdgPoint(mStartPoint, divLinePointList, lineGroupList, mEndPoint);
        for (int i = 0; i < mAllEdgPointList.size(); i++) {
            mLinelatLngs.add(aMap.getProjection().fromScreenLocation(mAllEdgPointList.get(i)));
            Log.e("TAG", mAllEdgPointList.size() + ",Main:allEdgPointList:" + mAllEdgPointList.get(i).x + "," + mAllEdgPointList.get(i).y);
        }
        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                addAll(mLinelatLngs)
                .width(2)
                .color(getResources().getColor(R.color.colorPrimary)));
        linePolygonList.add(polyline);
    }


    //清除
    private void clearLine() {
        if (markerList.size() > 0 || mPolygonList.size() > 0) {
            isOK = true;
            aMap.clear();
            mLinelatLngs.clear();
            mPolylineList.clear();
            listMarker.clear();
            ScreenSelectPoints.clear();

            mPolygonList.clear();
            mLatLngs.clear();
            mAllEdgPointList.clear();
            linePolygonList.clear();
            for (int i = 0; i < markerList.size(); i++) {
                markerList.get(i).remove();
            }
            markerList.clear();
            initMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setOnMapClickListener(MainActivity.this);
        }
        mRxPermissions = new RxPermissions(this);
        mRxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Log.e("TAG", "accept:true");
                            setUpMap();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.permission_request_denied, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

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
        aMap.setOnMarkerClickListener(this); //点击事件
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {//长按拖动开始

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.e("TAG", "拖动中,经纬度：" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                Log.e("TAG", marker.getId() + "拖动结束,经纬度：" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "");
            }
        });   //拖拽事件

        //setPointToCenter(int x, int y);
    }


    public void addPoints(StringBuffer stringBuffer) {
        mTextGetTv.setText(stringBuffer);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (isOK) {
            addMarkersToMap(latLng);
            drawPolygon();
        }

    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLngs) {
        //添加标记
        MarkerOptions mark = new MarkerOptions();
        mark.position(latLngs);
        mark.visible(true);
        mark.draggable(true);
        listMarker.add(mark);
        Marker marker = aMap.addMarker(mark);
        markerList.add(marker);
        mLatLngs.add(marker.getPosition());
        mLinelatLngs.clear();
        Log.e("TAG", "markerList.size:" + markerList.size() + "," + marker.getId() + "," + marker.getTitle());
    }

    /**
     * 画面
     */
    private void drawPolygon() {
        Log.e("TAG", "drawPolygon:markerList.size:" + markerList.size() + ",mLatLngs" + mLatLngs.size());
        Polygon polygon = aMap.addPolygon(new PolygonOptions()
                .addAll(mLatLngs)
                .fillColor(getResources().getColor(R.color.transparent))
                .strokeColor(getResources().getColor(R.color.colorAccent)));
        mPolygonList.add(polygon);
    }

    /**
     * 在地图上添加线
     */
    private void addPolyline() {
        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                addAll(mLatLngs).width(10).color(Color.argb(255, 1, 1, 1)));
        mPolylineList.add(polyline);
    }


    //返回经纬度坐标转屏幕坐标
    private Point LatLngToScreenLocation(LatLng paramLatLng) {
        return aMap.getProjection().toScreenLocation(paramLatLng);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    // 定义 Marker 点击事件监听
    // marker 对象被点击时回调的接口
    // 返回 true 则表示接口已响应事件，否则返回false

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("TAG", "onMarkerClick:true" + marker.getPosition().toString());
        Log.e("TAG", "onMarkerClick:true" + marker.getTitle());
        marker.showInfoWindow();
        return false;
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
                Log.e("location", aMapLocation.getLatitude() + ";" + aMapLocation.getLongitude() + "");
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

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.e("TAG", marker.getTitle() + "," + marker.getId());
        return null;
    }

//    private void addMarkerToMap(double jingdu, double weidu) {
//        latLng = new LatLng(jingdu, weidu);
//        markerOption = new MarkerOptions();
//        markerOption.position(latLng);
//        Marker marker = aMap.addMarker(markerOption);
//        marker.setDraggable(false);
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(), R.drawable.ic_navigation)));
//        marker.showInfoWindow();
//        marker.setRotateAngle(0);
//    }
}
