package com.android_gaode_maps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;

import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;
import im.actor.sdk.controllers.placeholder.GlobalPlaceholderFragment;
import im.actor.sdk.controllers.search.GlobalSearchDefaultFragment;
import im.actor.sdk.permisson_interface.OnPermissionListener;

public class MainActivity extends AppCompatActivity implements MapFragment.MapCallBackListener {
    TextView locationCenter;

    private ListView list;
    private TextView status, location_center_text;
    private ProgressBar loading;
    View header;
    private ArrayList<MapItem> places;

    MapItem mapItemSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_map_picker);

        getSupportActionBar().setTitle(R.string.map_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        locationCenter = (TextView) findViewById(R.id.location_center_text);
        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mapItemSend = (MapItem) adapterView.getItemAtPosition(position);
            }
        });
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        loading = (ProgressBar) findViewById(R.id.loading);
        status = (TextView) findViewById(R.id.status);
        header = findViewById(R.id.location_center_lay);
        location_center_text = (TextView) findViewById(R.id.location_center_text);


        getSupportFragmentManager().beginTransaction()
//                            .add(R.id.search_framelayout, new MapSearchFragment())
                .add(R.id.map_fray, new MapFragment())
                .commit();

    }


    @Override
    public void setLocationCenterText(LatLng target) {
//        locationCenter.setText(data);
        currentLocation = target;
        fetchPlaces(null);
    }

    LatLng currentLocation;
    PlaceFetchingTask fetchingTask;

    private void fetchPlaces(String query) {

        if (currentLocation == null) {
            Toast.makeText(this, R.string.picker_map_sory_notdefined, Toast.LENGTH_SHORT).show();
            return;
        }
        list.setAdapter(null);
        status.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        fetchingTask = new PlaceFetchingTask(query, 1000, currentLocation.latitude, currentLocation.longitude) {
            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof ArrayList) {
//                    org.json.JSONObject json = (org.json.JSONObject) o;
                    loading.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    try {
//                        location_center_text.setText(json.getString("center"));
                        places = (ArrayList<MapItem>) o;

                    } catch (Exception e) {
                        e.printStackTrace();
                        errorList("数据出错");
                    }
                    if (places.isEmpty()) {
                        status.setText(R.string.picker_map_nearby_empty);
                    } else {
                        list.setAdapter(new PlacesAdapter(MainActivity.this, places));
                        mapItemSend = places.get(0);
                        list.setSelected(true);
                        list.setItemChecked(0, true);
//                        list.getOnItemSelectedListener().onItemSelected(list,);
                    }
                } else {
                    errorList(o.toString());
                }
            }
        };
        fetchingTask.execute();
    }

    private void errorList(String string) {
        places = new ArrayList<MapItem>();
        list.setAdapter(null);
        header.setVisibility(View.GONE);
        status.setText(R.string.picker_internalerror);
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.map_send) {
//            String uri = "geo:" + latitude + ","
//                    + longitude + "?q=" + latitude
//                    + "," + longitude;
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(uri)));
//            return true;

            Intent returnIntent = new Intent();
            returnIntent.putExtra("latitude", mapItemSend.getLatLng().latitude);
            returnIntent.putExtra("longitude", mapItemSend.getLatLng().longitude);
            returnIntent.putExtra("street", mapItemSend.vicinity);
            returnIntent.putExtra("place", mapItemSend.name);

            setResult(RESULT_OK, returnIntent);
            finish();
            return true;

        }
//        else if (mapController != null) {
//            if (item.getItemId() == R.id.roadmap) {
//                mapController.setMapType(AMap.MAP_TYPE_NORMAL);
//            } else if (item.getItemId() == R.id.satellite) {
//                mapController.setMapType(AMap.MAP_TYPE_SATELLITE);
//            } else if (item.getItemId() == R.id.hybrid) {
////                mapController.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//            }
//        }
        return super.onOptionsItemSelected(item);
    }
}
