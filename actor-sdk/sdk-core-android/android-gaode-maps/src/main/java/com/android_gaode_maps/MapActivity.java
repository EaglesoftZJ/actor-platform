package com.android_gaode_maps;

import android.os.Bundle;

import com.amap.api.maps.model.LatLng;

import im.actor.sdk.controllers.activity.BaseFragmentActivity;

public class MapActivity extends BaseFragmentActivity implements MapFragment.MapCallBackListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.map_title);

        double longitude = getIntent().getDoubleExtra("longitude", 0);
        double latitude = getIntent().getDoubleExtra("latitude", 0);

        if (savedInstanceState == null) {
            showFragment(MapFragment.create(longitude, latitude), false);
        }

    }

    @Override
    public void setLocationCenterText(LatLng target) {

    }
}