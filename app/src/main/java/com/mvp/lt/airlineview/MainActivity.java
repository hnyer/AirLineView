package com.mvp.lt.airlineview;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.mvp.lt.airlineview.utils.RouteUtils2;
import com.mvp.lt.airlineview.view.MaterialRangeSlider;
import com.mvp.lt.airlineview.view.MyDoubleSeekBar;
import com.mvp.lt.airlineview.view.Mycircle2;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements AMap.OnMapClickListener, LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, MaterialRangeSlider.RangeSliderListener {
    @BindView(R.id.textGetBtn)
    Button mTextGetBtn;
    @BindView(R.id.text_get_tv)
    TextView mTextGetTv;
    @BindView(R.id.confirm)
    Button mConfirm;
    @BindView(R.id.clear)
    Button mClear;
    @BindView(R.id.amap)
    MapView mMapView;
    @BindView(R.id.set_div_et)
    EditText mSetDivEt;
    @BindView(R.id.seekbar_head)
    SeekBar mSeekbarHead;
    @BindView(R.id.reduce_head_deg)
    TextView mReduceHeadDeg;
    @BindView(R.id.add_head_deg)
    TextView mAddHeadDeg;
    @BindView(R.id.seekbar_head_layout)
    LinearLayout mSeekbarHeadLayout;
    @BindView(R.id.show_rotation)
    TextView mShowRotation;
    @BindView(R.id.rotation_line)
    RadioButton mRotationLine;
    @BindView(R.id.rotation_all)
    RadioButton mRotationAll;
    @BindView(R.id.rotation_type)
    RadioGroup mRotationType;
    @BindView(R.id.price_slider)
    MaterialRangeSlider mPriceSlider;
    @BindView(R.id.fly_progress)
    TextView mFlyProgress;
    @BindView(R.id.llLayout)
    LinearLayout mLlLayout;
    @BindView(R.id.revoker)
    Button mRevoker;
    @BindView(R.id.mycircle)
    Mycircle2 mMycircle;

    //定位监听
    private OnLocationChangedListener mListener = null;
    /*地图相关*/
    private AMap aMap;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private RxPermissions mRxPermissions;
    //声明AMapLocationClient类对象
    private AMapLocationClient mlocationClient;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //航线拐点 经纬度坐标点 与屏幕点对应
    private List<LatLng> mLinelatLngs = new ArrayList<>();

    //范围点marker
    private List<Marker> missionFpv4Markes = new ArrayList<>();
    //飞行航线点marker
    private List<Marker> wayPointsmarkerList = new ArrayList<>();
    //地图上 选点 经纬度集合
    private List<LatLng> polygons = new ArrayList<LatLng>();

    private List<LatLng> rPolygons;

    //多边形
    private Polygon mPolygon = null;
    public Polyline mPolyline;
    private Polyline mHangXianPolyline1;
    private Polyline mHangXianPolyline2;
    private SetHeadDegReduceThread mSetHeadDegReduceThread;
    private SetHeadDegAddThread mSetHeadDegAddThread;
    private List<LatLng> mListPolylines;
    private int Type_LINE = 0;
    private UiSettings mUiSettings;

    private int fenColor = Color.argb(255, 255, 64, 129);
    private int backColor = Color.argb(187, 255, 255, 255);
    //航线间隔
    int space = 20;
    //外接矩形的集合
    List<LatLng> mBoundsEWSNLatLng;
    List<LatLng> mRBoundsEWSNLatLng;
    public List<Polyline> mPolylineList = new ArrayList<Polyline>();//
    private List<Text> mDistanceText = new ArrayList<Text>();
    private Marker mCenterMarker;
    private MyDoubleSeekBar<Integer> mDoubleSeekbar;
    List<Integer> mIntegerList = new ArrayList<>();
    int lastMarkerLatlngId = 0;

    //清除
    private void clearLine() {
        for (int i = 0; i < mDistanceText.size(); i++) {
            mDistanceText.get(i).destroy();
        }
        mDistanceText.clear();
        if (mPolygon != null) {
            mPolygon.remove();
        }
        mPolygon = null;
        if (mPolyline != null) {
            mPolyline.remove();
        }
        for (int i = 0; i < mPolylineList.size(); i++) {
            mPolylineList.get(i).remove();
        }
        if (mHangXianPolyline1 != null) {
            mHangXianPolyline1.remove();
        }
        if (mHangXianPolyline2 != null) {
            mHangXianPolyline2.remove();
        }
        // mPolygonList.clear();
        polygons.clear();

        for (int i = 0; i < wayPointsmarkerList.size(); i++) {
            wayPointsmarkerList.get(i).destroy();
        }
        wayPointsmarkerList.clear();
        for (int i = 0; i < wayPointsmarkerList.size(); i++) {
            wayPointsmarkerList.get(i).destroy();
        }

        for (int i = 0; i < missionFpv4Markes.size(); i++) {
            missionFpv4Markes.get(i).destroy();
        }
        missionFpv4Markes.clear();
        if (mCenterMarker != null) {
            mCenterMarker.destroy();
            mCenterMarker = null;
        }

        mLinelatLngs.clear();
        mPolylineList.clear();
    }

    private List<LatLng> mNewListPolylines;

    //是否是重新选点
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDoubleSeekbar = new MyDoubleSeekBar<Integer>(0, 100, this);
        mDoubleSeekbar.setOnRangeSeekBarChangeListener(new MyDoubleSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(MyDoubleSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // TODO 左右游标滑动监听事件
                Log.e("onValuesChanged", minValue + "" + maxValue);
                updateDoubleSeekbarFlyWayPoint(minValue, maxValue);
            }
        });


        ViewTreeObserver vto = mMycircle.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.e("位置", "" + mMycircle.getLeft() + ":" + mMycircle.getTop());
                return true;
            }
        });
        mMycircle.setOnDateDragChangeListener(new Mycircle2.OnDateDragChangeListener() {
            @Override
            public void onDateChangeDrag(int angle) {
                Log.e("角度", "" + angle);
                currentHeadDeg = angle;
                mShowRotation.setText(currentHeadDeg + "");
                updateRotation();
            }
        });

        mLlLayout.addView(mDoubleSeekbar);
        mPriceSlider.setRangeSliderListener(MainActivity.this);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLine();
            }
        });
        mMapView.onCreate(savedInstanceState);
        initMap();
        setSeekBarListener();
        mRotationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rotation_line:
                        Type_LINE = 0;

                        break;
                    case R.id.rotation_all:
                        Type_LINE = 1;
                        break;
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = mSetDivEt.getText().toString().trim();
                if (!TextUtils.isEmpty(height)) {
                    int flyHeight = Integer.parseInt(height);
                    space = (int) Math.floor(flyHeight / 2 + 3);
                    updateSpaceLine();


                }
            }
        });
        mRevoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //撤销一个航点
                //更新seekbar
                //更新航线
                revokerOrcPoints();

            }
        });
    }

    public void revokerOrcPoints() {
        if (mIntegerList.size() > 0) {
            int id = mIntegerList.get(mIntegerList.size() - 1);
            Log.e("mRevoker", "onClick: " + id);
            polygons.remove(id);
            mIntegerList.remove(id);
            missionFpv4Markes.get(id).destroy();
            missionFpv4Markes.remove(id);
            //清除航线
            //航点
            for (int i = 0; i < missionFpv4Markes.size(); i++) {
                missionFpv4Markes.get(i).destroy();
            }
            for (int i = 0; i < polygons.size(); i++) {
                addRangeMarkerToMap(polygons.get(i), i);

            }
            drawWaypointOrcPicture();
            if (mIntegerList.size() == 0) {
                mPolygon = null;
            }
        } else {
            mPolygon = null;
            polygons.clear();
            mIntegerList.clear();
            missionFpv4Markes.clear();
        }
    }

    private void updateDoubleSeekbarFlyWayPoint(Integer minValue, Integer maxValue) {
        if (mListPolylines == null || mListPolylines.size() == 0) {
            return;
        }
        if (mHangXianPolyline1 != null) {
            mHangXianPolyline1.remove();
        }
        mNewListPolylines = new ArrayList<>();

        for (int i = minValue - 1; i < maxValue; i++) {
            mNewListPolylines.add(mListPolylines.get(i));

        }

        mHangXianPolyline1 = aMap.addPolyline(new PolylineOptions().
                setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr))
                .addAll(mNewListPolylines)
                .useGradient(true)
                .width(18));

        for (int i = 0; i < wayPointsmarkerList.size(); i++) {
            wayPointsmarkerList.get(i).destroy();
        }
        wayPointsmarkerList.clear();
        for (int i = 0; i < mNewListPolylines.size(); i++) {
            addCrossMarkerToMap(mNewListPolylines, mNewListPolylines.get(i), i + 1);
        }
    }

    @Override
    public void onMaxChanged(int newValue) {
        Log.e("onMaxChanged", newValue + "");
    }

    @Override
    public void onMinChanged(int newValue) {
        Log.e("onMinChanged", newValue + "");
    }

    //加点
    @Override
    public void onMapClick(LatLng latLng) {
        Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPolygon != null && mPolygon.isVisible()) {
                    if (mPolygon.contains(latLng)) {
                        List<Double> list = new ArrayList<>();
                        //在多边形内
                        for (int i = 0; i < polygons.size(); i++) {
                            LatLng latLng1;
                            LatLng latLng2;
                            if (i == polygons.size() - 1) {
                                latLng1 = polygons.get(i);
                                latLng2 = polygons.get(0);
                            } else {
                                latLng1 = polygons.get(i);
                                latLng2 = polygons.get(i + 1);
                            }
                            //多边形内
                            list.add(RouteUtils2.PointToSegDist(latLng.longitude, latLng.latitude,
                                    latLng1.longitude, latLng1.latitude,
                                    latLng2.longitude, latLng2.latitude))
                            ;
                        }
                        double x = Collections.min(list);
                        int index = list.indexOf(x);
                        Log.e("多边形内index", index + "");
                        polygons.add(index + 1, latLng);
                        lastMarkerLatlngId = index + 1;
                        mIntegerList.add(lastMarkerLatlngId);
                    } else {
                        //在多边形外
                        //是否有交点
                        LatLng centerLatlng = mBoundsEWSNLatLng.get(0);
                        for (int i = 0; i < polygons.size(); i++) {
                            LatLng latLng1;
                            LatLng latLng2;
                            if (i == polygons.size() - 1) {
                                latLng1 = polygons.get(i);
                                latLng2 = polygons.get(0);
                                boolean iscross = RouteUtils2.doIntersect(
                                        RouteUtils2.latlng2px(aMap, centerLatlng),
                                        RouteUtils2.latlng2px(aMap, latLng),
                                        RouteUtils2.latlng2px(aMap, latLng1),
                                        RouteUtils2.latlng2px(aMap, latLng2));
                                if (iscross) {
                                    //计算距离
                                    //计算距离
                                    if (AMapUtils.calculateLineDistance(polygons.get(i), latLng) > 2000
                                            || AMapUtils.calculateLineDistance(polygons.get(0), latLng) > 2000) {
                                        Log.e("", "距离过大");
                                        missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                        return;
                                    }
                                    if (AMapUtils.calculateLineDistance(polygons.get(i), latLng) < 0.5
                                            || AMapUtils.calculateLineDistance(polygons.get(0), latLng) < 0.5) {
                                        Log.e("", "距离过小");
                                        missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                        return;
                                    }
                                    lastMarkerLatlngId = i + 1;
                                    mIntegerList.add(lastMarkerLatlngId);
                                    polygons.add(i + 1, latLng);
                                    break;
                                } else {
                                    if (mPolygon.contains(centerLatlng)) {
                                        Log.e("中点在多边形内", ":" + true);
                                    }
                                    LatLng latLng3 = null;
                                    LatLng latLng4 = null;
                                    List<LatLng> latLngs = new ArrayList<>();
                                    for (int j = 0; j < polygons.size(); j++) {
                                        if (j == polygons.size() - 1) {
                                            latLng3 = polygons.get(j);
                                            latLng4 = polygons.get(0);
                                        } else {
                                            latLng3 = polygons.get(j);
                                            latLng4 = polygons.get(j + 1);
                                        }
                                        latLngs.add(RouteUtils2.getMidLatLng(latLng3, latLng4));
                                    }
                                    List<Float> integerList = new ArrayList<>();
                                    for (int j = 0; j < latLngs.size(); j++) {
                                        integerList.add(AMapUtils.calculateLineDistance(latLngs.get(j), centerLatlng));
                                    }
                                    float maxd = Collections.min(integerList);
                                    int j = integerList.indexOf(maxd);
                                    //计算距离
                                    if (j == integerList.size()) {
                                        if (AMapUtils.calculateLineDistance(polygons.get(j), latLng) > 2000
                                                || AMapUtils.calculateLineDistance(polygons.get(0), latLng) > 2000) {
                                            Log.e("", "距离过大");
                                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                            return;
                                        }
                                        if (AMapUtils.calculateLineDistance(polygons.get(j), latLng) < 0.5
                                                || AMapUtils.calculateLineDistance(polygons.get(0), latLng) < 0.5) {
                                            Log.e("", "距离过小");
                                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                            return;
                                        }
                                    } else {
                                        if (AMapUtils.calculateLineDistance(polygons.get(j), latLng) > 2000
                                                || AMapUtils.calculateLineDistance(polygons.get(j + 1), latLng) > 2000) {
                                            Log.e("", "距离过大");
                                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                            return;
                                        }
                                        if (AMapUtils.calculateLineDistance(polygons.get(j), latLng) < 0.5
                                                || AMapUtils.calculateLineDistance(polygons.get(j + 1), latLng) < 0.5) {
                                            Log.e("", "距离过小");
                                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                            return;
                                        }
                                    }
                                    lastMarkerLatlngId = j + 1;
                                    mIntegerList.add(lastMarkerLatlngId);
                                    polygons.add(j + 1, latLng);
                                    Log.e("多边形外交点,中点在多边形内", ":" + true);
                                }
                            } else {
                                latLng1 = polygons.get(i);
                                latLng2 = polygons.get(i + 1);
                                boolean iscross = RouteUtils2.doIntersect(
                                        RouteUtils2.latlng2px(aMap, centerLatlng),
                                        RouteUtils2.latlng2px(aMap, latLng),
                                        RouteUtils2.latlng2px(aMap, latLng1),
                                        RouteUtils2.latlng2px(aMap, latLng2));
                                Log.e("多边形外交点", i + ":" + iscross);
                                if (iscross) {
                                    //计算距离
                                    if (AMapUtils.calculateLineDistance(polygons.get(i), latLng) > 2000
                                            || AMapUtils.calculateLineDistance(polygons.get(i + 1), latLng) > 2000) {
                                        Log.e("", "距离过大");
                                        missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                        return;
                                    }
                                    if (AMapUtils.calculateLineDistance(polygons.get(i), latLng) < 0.5
                                            || AMapUtils.calculateLineDistance(polygons.get(i + 1), latLng) < 0.5) {
                                        Log.e("", "距离过小");
                                        missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                                        return;
                                    }
                                    lastMarkerLatlngId = i + 1;
                                    mIntegerList.add(lastMarkerLatlngId);
                                    polygons.add(i + 1, latLng);
                                    break;
                                }
                            }

                        }
                    }
                } else {
                    //计算距离
                    if (polygons.size() > 0) {
                        if (AMapUtils.calculateLineDistance(polygons.get(0), latLng) > 2000) {
                            Log.e("", "距离过大");
                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                            return;
                        }
                        if (AMapUtils.calculateLineDistance(polygons.get(0), latLng) < 0.5) {
                            Log.e("", "距离过小");
                            missionFpv4Markes.get(missionFpv4Markes.size() - 1).destroy();
                            return;
                        }
                        polygons.add(latLng);
                        lastMarkerLatlngId = polygons.size() - 1;
                        mIntegerList.add(lastMarkerLatlngId);
                    } else {
                        polygons.add(latLng);
                        lastMarkerLatlngId = polygons.size() - 1;
                        mIntegerList.add(lastMarkerLatlngId);

                    }


                }
                for (int i = 0; i < missionFpv4Markes.size(); i++) {
                    missionFpv4Markes.get(i).destroy();
                }
                for (int i = 0; i < polygons.size(); i++) {
                    addRangeMarkerToMap(polygons.get(i), i);
                }
                drawWaypointOrcPicture();

            }
        }).start();

    }

    private void drawWaypointOrcPicture() {

        for (int i = 0; i < wayPointsmarkerList.size(); i++) {
            wayPointsmarkerList.get(i).destroy();
        }
        wayPointsmarkerList.clear();
        if (mPolygon != null) {
            mPolygon.remove();
            mPolygon = null;
        }
        if (polygons.size() < 2) {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            return;
        }
        if (polygons.size() > 2) {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //区域
                    mPolygon = drawPolygonOptions(polygons
                            , getResources().getColor(R.color.gary)
                            , getResources().getColor(R.color.trgary));
                }
            });
        } else {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            mPolyline = drawPolyLineOptions(polygons
                    , getResources().getColor(R.color.gary)
                    , getResources().getColor(R.color.trgary));
        }
        for (int i = 0; i < mDistanceText.size(); i++) {
            if (mDistanceText.get(i) != null)
                mDistanceText.get(i).destroy();
        }
        mDistanceText.clear();
        //计算中点距离
        for (int i = 0; i < polygons.size(); i++) {
            LatLng latLng1;
            LatLng latLng2;
            if (i == polygons.size() - 1) {
                latLng1 = polygons.get(i);
                latLng2 = polygons.get(0);
            } else {
                latLng1 = polygons.get(i);
                latLng2 = polygons.get(i + 1);
            }
            float distance = (float) (Math.round(AMapUtils.calculateLineDistance(latLng1, latLng2) * 10)) / 10;
            LatLng mediaLatlng = RouteUtils2.getMidLatLng(latLng1, latLng2);
            if (!mDistanceText.contains(mediaLatlng))
                mDistanceText.add(aMap.addText(
                        new TextOptions().position(mediaLatlng).fontColor(fenColor)
                                .backgroundColor(backColor).text(distance + "米")
                ));
        }
        // 1
        mBoundsEWSNLatLng = RouteUtils2.createPolygonBounds(polygons);
        //  addDMarkerToMap(mBoundsEWSNLatLng, 0);
        drawOricFlyLines(polygons);
    }

    /***
     *  旋转航线
     * @param polygons
     */
    private void drawOricFlyLines(List<LatLng> polygons) {
        if (polygons.size() < 3) {
            if (mHangXianPolyline1 != null) {
                mHangXianPolyline1.remove();
            }
            if (mHangXianPolyline2 != null) {
                mHangXianPolyline2.remove();
            }
            for (int i = 0; i < wayPointsmarkerList.size(); i++) {
                wayPointsmarkerList.get(i).destroy();
            }
            wayPointsmarkerList.clear();
            return;
        }
        try {
            rPolygons = RouteUtils2.createRotatePolygon(polygons, mBoundsEWSNLatLng, -currentHeadDeg);
            mRBoundsEWSNLatLng = RouteUtils2.createPolygonBounds(rPolygons);
            Double latlines[] = RouteUtils2.createLats(mRBoundsEWSNLatLng, space);
            int xxx = (int) Math.ceil(latlines[0]);
            List<LatLng> lines;
            List<LatLng> polylines = new ArrayList<>();
            for (int i = 1; i < xxx + 1; i++) {
                lines = new ArrayList<>();
                double fen = (latlines[1]) / 3 * 2;
                for (int j = 0; j < rPolygons.size(); j++) {
                    int si = RouteUtils2.sint(j + 1, rPolygons.size());
                    LatLng checklatlng = RouteUtils2.createInlinePoint(rPolygons.get(j), rPolygons.get(si),
                            mRBoundsEWSNLatLng.get(1).latitude + fen - i * latlines[1]);
                    if (checklatlng != null) {
                        lines.add(checklatlng);
                    }
                }
                if (lines.size() < 2) {
                    continue;
                }
                if (lines.get(0) == lines.get(1)) {
                    continue;
                }
                if (i % 2 == 0) {
                    double min2 = Math.min(lines.get(0).longitude, lines.get(1).longitude);
                    LatLng latLng1 = new LatLng(lines.get(0).latitude, min2);


                    double max1 = Math.max(lines.get(0).longitude, lines.get(1).longitude);
                    LatLng latLng2 = new LatLng(lines.get(0).latitude, max1);
                    //
                    polylines.add(latLng1);
                    polylines.add(latLng2);
                } else {
                    double max1 = Math.max(lines.get(0).longitude, lines.get(1).longitude);
                    LatLng latLng1 = new LatLng(lines.get(0).latitude, max1);
                    double min2 = Math.min(lines.get(0).longitude, lines.get(1).longitude);
                    LatLng latLng2 = new LatLng(lines.get(0).latitude, min2);
                    polylines.add(latLng1);
                    polylines.add(latLng2);
                }
                mListPolylines = RouteUtils2.createRotatePolygon(polylines, mBoundsEWSNLatLng, currentHeadDeg);

                mNewListPolylines = mListPolylines;
            }
            //划线
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mHangXianPolyline1 != null) {
                        mHangXianPolyline1.remove();
                    }
                    if (mHangXianPolyline2 != null) {
                        mHangXianPolyline2.remove();
                    }
                    mHangXianPolyline2 = aMap.addPolyline(new PolylineOptions().color(Color.WHITE)
                            .addAll(mListPolylines)
                            .useGradient(true)
                            .width(5));

                    mHangXianPolyline1 = aMap.addPolyline(new PolylineOptions().
                            setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr)) //setCustomTextureList(bitmapDescriptors)
                            .addAll(mListPolylines)
                            .useGradient(true)
                            .width(20));


                    mPriceSlider.setMin(1);
                    mPriceSlider.setMax(mListPolylines.size());
                    mPriceSlider.reset();

                    mDoubleSeekbar.updateMaxDate(1, mListPolylines.size());
                    Log.e("TAG", "MAx:" + mPriceSlider.getMax());
                    Log.e("TAG", "min:" + mPriceSlider.getMin());

                }
            });
            for (int i = 0; i < wayPointsmarkerList.size(); i++) {
                wayPointsmarkerList.get(i).destroy();
            }
            wayPointsmarkerList.clear();
            for (int i = 0; i < mListPolylines.size(); i++) {
                addCrossMarkerToMap(mListPolylines, mListPolylines.get(i), i + 1);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }

    //宽度
    private void updateSpaceLine() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mRBoundsEWSNLatLng == null || mRBoundsEWSNLatLng.size() == 0) {
                    return;
                }
                //4
                Double latlines[] = RouteUtils2.createLats(mRBoundsEWSNLatLng, space);

                int xxx = (int) Math.ceil(latlines[0]);
                List<LatLng> lines;
                List<LatLng> polylines = new ArrayList<>();

                for (int i = 1; i < xxx + 1; i++) {
                    lines = new ArrayList<>();
                    double fen = (latlines[1]) / 3 * 2;
                    for (int j = 0; j < rPolygons.size(); j++) {
                        int si = RouteUtils2.sint(j + 1, rPolygons.size());
                        LatLng checklatlng = RouteUtils2.createInlinePoint(rPolygons.get(j), rPolygons.get(si),
                                mRBoundsEWSNLatLng.get(1).latitude + fen - i * latlines[1]);
                        if (checklatlng != null) {
                            lines.add(checklatlng);
                        }
                    }
                    if (lines.size() < 2) {
                        continue;
                    }
                    if (lines.get(0) == lines.get(1)) {
                        continue;
                    }
                    if (i % 2 == 0) {
                        double min2 = Math.min(lines.get(0).longitude, lines.get(1).longitude);
                        LatLng latLng1 = new LatLng(lines.get(0).latitude, min2);


                        double max1 = Math.max(lines.get(0).longitude, lines.get(1).longitude);
                        LatLng latLng2 = new LatLng(lines.get(0).latitude, max1);
                        //
                        polylines.add(latLng1);
                        polylines.add(latLng2);
                    } else {
                        double max1 = Math.max(lines.get(0).longitude, lines.get(1).longitude);
                        LatLng latLng1 = new LatLng(lines.get(0).latitude, max1);


                        double min2 = Math.min(lines.get(0).longitude, lines.get(1).longitude);
                        LatLng latLng2 = new LatLng(lines.get(0).latitude, min2);
                        polylines.add(latLng1);
                        polylines.add(latLng2);
                    }
                    mListPolylines = RouteUtils2.createRotatePolygon(polylines, mBoundsEWSNLatLng, currentHeadDeg);

                }
                //划线
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mHangXianPolyline1 != null) {
                            mHangXianPolyline1.remove();
                        }
                        if (mHangXianPolyline2 != null) {
                            mHangXianPolyline2.remove();
                        }
                        mHangXianPolyline2 = aMap.addPolyline(new PolylineOptions().color(Color.WHITE)
                                .addAll(mListPolylines)
                                .useGradient(true)
                                .width(5));
                        mHangXianPolyline1 = aMap.addPolyline(new PolylineOptions().
                                setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr)) //setCustomTextureList(bitmapDescriptors)
                                .addAll(mListPolylines)
                                .useGradient(true)
                                .width(20));

                        mPriceSlider.setRangeSliderListener(MainActivity.this);
                        mPriceSlider.setMin(1);
                        mPriceSlider.setMax(mListPolylines.size());
                        mPriceSlider.reset();

                        Log.e("TAG", "MAx:" + mPriceSlider.getMax());
                        Log.e("TAG", "min:" + mPriceSlider.getMin());
                    }
                });
                for (int i = 0; i < wayPointsmarkerList.size(); i++) {
                    wayPointsmarkerList.get(i).destroy();
                }
                wayPointsmarkerList.clear();
                for (int i = 0; i < mListPolylines.size(); i++) {
                    addCrossMarkerToMap(mListPolylines, mListPolylines.get(i), i + 1);
                }
            }
        }).start();
    }

    /**
     * 旋转航线和边界
     *
     * @param polygon
     */
    private void drawOricFlyBoundsLines(List<LatLng> polygon) {
        //currentHeadDeg
        polygons = RouteUtils2.createRotatePolygon(polygon, mBoundsEWSNLatLng, currentHeadDeg);

        if (mPolygon != null) {
            mPolygon.remove();
        }
        mPolygon = null;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //区域
                mPolygon = drawPolygonOptions(polygons
                        , getResources().getColor(R.color.gary)
                        , getResources().getColor(R.color.trgary));
            }
        });
        for (int i = 0; i < missionFpv4Markes.size(); i++) {
            missionFpv4Markes.get(i).destroy();
        }
        for (int i = 0; i < polygons.size(); i++) {
            addMarkersToMap(polygons.get(i));
        }
        mListPolylines = RouteUtils2.createRotatePolygon(mListPolylines, mBoundsEWSNLatLng, currentHeadDeg);

        if (mHangXianPolyline1 != null) {
            mHangXianPolyline1.remove();
        }
        if (mHangXianPolyline2 != null) {
            mHangXianPolyline2.remove();
        }
        //划线
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHangXianPolyline2 = aMap.addPolyline(new PolylineOptions().color(Color.WHITE)
                        .addAll(mListPolylines)
                        .useGradient(true)
                        .width(5));
                mHangXianPolyline1 = aMap.addPolyline(new PolylineOptions().
                        setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.map_alr)) //setCustomTextureList(bitmapDescriptors)
                        .addAll(mListPolylines)
                        .useGradient(true)
                        .width(20));


            }
        });
        for (int i = 0; i < wayPointsmarkerList.size(); i++) {
            wayPointsmarkerList.get(i).destroy();
        }
        wayPointsmarkerList.clear();
        for (int i = 0; i < mListPolylines.size(); i++) {
            addCrossMarkerToMap(mListPolylines, mListPolylines.get(i), i + 1);
        }
    }

    private void updateRotation() {
        if (Type_LINE == 0) {
            //旋转航线
            drawOricFlyLines(polygons);
        } else {
            //旋转航线和边界
            drawOricFlyBoundsLines(polygons);
        }

    }


    private Polygon drawPolygonOptions(List<LatLng> linelatLngs, int color, int color2) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(linelatLngs);
        polygonOptions.strokeWidth(5) // 多边形的边框
                .strokeColor(color) // 边框颜色
                .fillColor(color2);   // 多边形的填充色
        return aMap.addPolygon(polygonOptions);
    }

    private Polyline drawPolyLineOptions(List<LatLng> linelatLngs, int color, int color2) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(linelatLngs);
        polylineOptions.width(5) // 多边形的边框
                .color(color); // 边框颜色
        return aMap.addPolyline(polylineOptions);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLngs) {
        MarkerOptions mark = new MarkerOptions().position(latLngs);
        Marker marker = aMap.addMarker(mark);
        marker.setZIndex(0);

    }

//
//    /**
//     * 顶点
//     *
//     * @param i
//     */
//
//
//    public void addDMarkerToMap(List<LatLng> linelatLngs, int i) {
//        if (mCenterMarker != null) {
//            mCenterMarker.destroy();
//            mCenterMarker = null;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                View view = getInsrestPointView("中点");
//                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
//                final MarkerOptions markerOptions = new MarkerOptions().position(linelatLngs.get(i)).icon(markerIcon).zIndex(1);
//                mCenterMarker = aMap.addMarker(markerOptions);
//                mCenterMarker.setAnchor(0.5f, 0.9f);
//                mCenterMarker.setSnippet(String.valueOf(i));
//
//            }
//        });
//
//    }

    //范围点
    public void addRangeMarkerToMap(LatLng latLng, int id) {
        View view = getInsrestPointView(String.valueOf(id + 1));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
                final MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(2);
                Marker marker = aMap.addMarker(markerOptions);
                marker.setAnchor(0.5f, 0.9f);
                marker.setDraggable(true);
                marker.setSnippet(String.valueOf(id));
                missionFpv4Markes.add(marker);
            }
        });

    }

    //航线点
    public void addCrossMarkerToMap(List<LatLng> listPolylines, LatLng LatLng, int i) {
        View view;
        if (i == 1) {
            view = getCrossPointView("起");
        } else if (i == listPolylines.size()) {
            view = getCrossPointView("终");
        } else {
            view = getCrossPointView(String.valueOf(i));
        }
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
        final MarkerOptions markerOptions = new MarkerOptions().position(LatLng).icon(markerIcon);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = aMap.addMarker(markerOptions);
                marker.setAnchor(0.5f, 0.9f);
                marker.setSnippet(String.valueOf(i));
                wayPointsmarkerList.add(marker);
            }
        });


    }


    /**
     * 设置一些amap的属性
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
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
        aMap.setOnMarkerClickListener(this); //点击事件
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {//长按拖动开始
                int s = Integer.parseInt(marker.getSnippet());
                Vibrator vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                //missionFpv4Markes
                missionFpv4Markes.set(s, marker);

                Log.e("TAG", "拖动中,经纬度：" + s + "," + marker.getPosition().latitude + "," + marker.getPosition().longitude + "");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                int s = Integer.parseInt(marker.getSnippet());
                Log.e("TAG", "拖动中,经纬度：" + s + "," + marker.getPosition().latitude + "," + marker.getPosition().longitude + "");
                missionFpv4Markes.set(s, marker);
                polygons.set(s, marker.getPosition());
                if (mPolygon != null) {
                    mPolygon.remove();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //区域
                        mPolygon = drawPolygonOptions(polygons
                                , getResources().getColor(R.color.gary)
                                , getResources().getColor(R.color.trgary));
                    }
                });
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                int s = Integer.parseInt(marker.getSnippet());
                Log.e("TAG", marker.getId() + "拖动结束,经纬度：" + s + "," + marker.getPosition().latitude + "," + marker.getPosition().longitude + "");
                missionFpv4Markes.set(s, marker);
                polygons.set(s, marker.getPosition());
                for (int i = 0; i < missionFpv4Markes.size(); i++) {
                    missionFpv4Markes.get(i).destroy();
                }
                for (int i = 0; i < polygons.size(); i++) {
                    addRangeMarkerToMap(polygons.get(i), i);

                }
                drawWaypointOrcPicture();
            }
        });   //拖拽事件

    }


    /**
     * 画面
     */
    private void drawPolygon() {
        Log.e("TAG", "drawPolygon:markerList.size:" + missionFpv4Markes.size() + ",mLatLngs" + polygons.size());
        mPolygon = aMap.addPolygon(new PolygonOptions()
                .addAll(polygons)
                .fillColor(getResources().getColor(R.color.transparent))
                .strokeColor(getResources().getColor(R.color.colorAccent)));
        //  mPolygonList.add(mPolygon);
    }

    /**
     * 在地图上添加线
     */
    private void addPolyline() {
        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                addAll(polygons).width(10).color(Color.argb(255, 1, 1, 1)));
        mPolylineList.add(polyline);
    }


    //返回经纬度坐标转屏幕坐标
    private Point LatLngToScreenLocation(LatLng paramLatLng) {
        return aMap.getProjection().toScreenLocation(paramLatLng);
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
        //marker.showInfoWindow();
        marker.hideInfoWindow();
        return true;
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

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.e("TAG", marker.getTitle() + "," + marker.getId());
        return null;
    }

    protected View getInsrestPointView(String pm_val) {
        View view = getLayoutInflater().inflate(R.layout.way_instrest_point_marker, null);
        TextView markerIndexNumber = (TextView) view.findViewById(R.id.instrest_marker_index_number);
        markerIndexNumber.setText(pm_val);
        return view;
    }

    private int startPointId;
    private int endPointId;

    protected View getCrossPointView(String pm_val) {
        View view = getLayoutInflater().inflate(R.layout.way_cross_point_marker, null);
        TextView markerIndexNumber = (TextView) view.findViewById(R.id.instrest_marker_index_number);
        if (pm_val.equals("起") || pm_val.equals("终")) {
            markerIndexNumber.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            markerIndexNumber.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
        markerIndexNumber.setText(pm_val);
        return view;
    }

    /****
     * 旋转 SeeckBar
     *
     */
    private int currentHeadDeg = 0;

    public void setSeekBarListener() {
        CompentOnTouch b = new CompentOnTouch();
        mReduceHeadDeg.setOnTouchListener(b);
        mAddHeadDeg.setOnTouchListener(b);
        mSeekbarHead.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentHeadDeg = progress;
                mShowRotation.setText(currentHeadDeg + "");
                updateRotation();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    // Touch事件
    class CompentOnTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                // 这是btnMius下的一个层，为了增强易点击性  减 -
                case R.id.add_head_deg:
                    onTouchChange("add_head_deg", event.getAction());
                    break;
                case R.id.reduce_head_deg:
                    onTouchChange("reduce_head_deg", event.getAction());
            }
            return true;
        }
    }

    private void onTouchChange(String methodName, int eventAction) {

        if ("reduce_head_deg".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                mSetHeadDegReduceThread = new SetHeadDegReduceThread();
                isOnLongClick = true;
                mSetHeadDegReduceThread.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (mSetHeadDegReduceThread != null) {
                    isOnLongClick = false;
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                if (mSetHeadDegReduceThread != null) {
                    isOnLongClick = true;
                }
            }
        }
        if ("add_head_deg".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                mSetHeadDegAddThread = new SetHeadDegAddThread();
                isOnLongClick = true;
                mSetHeadDegAddThread.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (mSetHeadDegAddThread != null) {
                    isOnLongClick = false;
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                if (mSetHeadDegAddThread != null) {
                    isOnLongClick = true;
                }
            }
        }
    }

    public boolean isOnLongClick = false;

    class SetHeadDegReduceThread extends Thread {
        @Override
        public void run() {
            while (isOnLongClick) {
                //三种类型
                try {
                    Thread.sleep(100);
                    currentHeadDeg -= 1;
                    if (currentHeadDeg < 0) {
                        currentHeadDeg = 0;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSeekbarHead.setProgress(currentHeadDeg);
                            mShowRotation.setText(currentHeadDeg + "");
                            updateRotation();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    class SetHeadDegAddThread extends Thread {
        @Override
        public void run() {
            while (isOnLongClick) {
                try {
                    Thread.sleep(100);
                    currentHeadDeg += 1;
                    if (currentHeadDeg > 360) {
                        currentHeadDeg = 0;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSeekbarHead.setProgress(currentHeadDeg);
                            mShowRotation.setText(currentHeadDeg + "");
                            updateRotation();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

    }

    /**
     * 高德坐标转GPS
     */

    public void changGaoDeToGPS() {

    }

    /**
     * GPS转高德坐标
     */
    public void changGPSToGaoDe() {
        String latStr = "39.989646";
        String lngStr = "116.480864";
        double lat = 0.0;
        double lon = 0.0;
        lat = Double.parseDouble(latStr);
        lon = Double.parseDouble(lngStr);
        LatLng sourceLatLng = new LatLng(lat, lon);
        convert(sourceLatLng);
    }

    /**
     * 根据类型 转换 坐标
     * GPS转高德坐标
     */
    private LatLng convert(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

}
