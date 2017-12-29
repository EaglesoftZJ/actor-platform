package com.android_gaode_maps;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import im.actor.sdk.controllers.BaseFragment;

public class MapFragment extends BaseFragment implements
        LocationSource,
        AMapLocationListener {
    double longitude;
    double latitude;

    private AMapLocationClient mlocationClient = null;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    boolean isLocation = false;


    MapCallBackListener callBackListener;

    public MapFragment() {
//        setHomeAsUp(true);
        setHasOptionsMenu(true);
    }

    public static MapFragment create(double longitude, double latitude) {
        MapFragment res = new MapFragment();
        Bundle arguments = new Bundle();
        arguments.putDouble("longitude", longitude);
        arguments.putDouble("latitude", latitude);
        res.setArguments(arguments);
        return res;
    }

    public static MapFragment create() {
        MapFragment res = new MapFragment();
        return res;
    }

    //    GoogleMap mapController;
    private AMap mapController;
    MapView mMapView;
    Marker centerMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        callBackListener = (MapCallBackListener) getActivity();
        mMapView = (MapView) view.findViewById(R.id.gaode_map);
        mMapView.onCreate(savedInstanceState);


        try {
            longitude = getArguments().getDouble("longitude");
            latitude = getArguments().getDouble("latitude");
            isLocation = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mapController == null) {
            mapController = mMapView.getMap();
            if (isLocation) {
                LatLng latLng = new LatLng(latitude, longitude);
//                centerMarker.setIcon();
                MarkerOptions options = new MarkerOptions().position(latLng);
//                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                        .decodeResource(getResources(),R.mipmap.gaode_location)));
                centerMarker = mapController.addMarker(options);
            }
            mapController.getUiSettings().setRotateGesturesEnabled(true);
            mapController.getUiSettings().setScaleControlsEnabled(true);
            setUpMap();
        }


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map, menu);
        MenuItem item = menu.findItem(R.id.map_send);
        if (isLocation) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map_send) {
//            String uri = "geo:" + latitude + ","
//                    + longitude + "?q=" + latitude
//                    + "," + longitude;
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(uri)));
//            return true;

            Intent returnIntent = new Intent();
            returnIntent.putExtra("latitude", latitude);
            returnIntent.putExtra("longitude", longitude);
//            returnIntent.putExtra("street",street);
//            returnIntent.putExtra("place",place);
//            startActivityForResult(returnIntent, getActivity().RESULT_OK);
            getActivity().setResult(getActivity().RESULT_OK, returnIntent);
            getActivity().finish();
            return true;

        } else if (mapController != null) {
            if (item.getItemId() == R.id.roadmap) {
                mapController.setMapType(AMap.MAP_TYPE_NORMAL);
            } else if (item.getItemId() == R.id.satellite) {
                mapController.setMapType(AMap.MAP_TYPE_SATELLITE);
            } else if (item.getItemId() == R.id.hybrid) {
//                mapController.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        if (isLocation) {
            mapController.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        }else{
            mapController.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        }

        mapController.setLocationSource(this);// 设置定位监听
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.mipmap.center_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mapController.setMyLocationStyle(myLocationStyle);
        mapController.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        mapController.setMyLocationType(AMap.);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mapController.setMyLocationStyle(myLocationStyle);
        mapController.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {

            }
        });

        mapController.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (!isLocation) {
                    LatLng target = cameraPosition.target;
                    centerMarker.setPosition(target);
                }
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!isLocation) {
                    LatLng target = cameraPosition.target;
                    centerMarker.setPosition(target);

                    callBackListener.setLocationCenterText(target);
                }

            }
        });

        mapController.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
//                CameraUpdateFactory.zoomTo(17);

                mapController.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(new LatLng(latitude, longitude), 17, 0, 0)));
            }
        });

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (isLocation) {
                mapController.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(new LatLng(latitude, longitude), 17, 0, 0)));
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                mapController.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
//                        latitude, longitude)));
            } else {
                mapController.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),
                        17, 0, 0)));

                if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                    longitude = amapLocation.getLongitude();
                    latitude = amapLocation.getLatitude();
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                    if (centerMarker == null) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        centerMarker = mapController.addMarker(new MarkerOptions().position(latLng));
                    }
                } else {
                    String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                            + amapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
                }
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getContext());
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(true);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
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


    //设置用于修改文本的回调接口
    public static interface MapCallBackListener {
        public void setLocationCenterText(LatLng target);

//        public void Ad(String data);
    }
}
