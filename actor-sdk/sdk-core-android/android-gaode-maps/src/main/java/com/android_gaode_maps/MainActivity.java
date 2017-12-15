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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS_CONTACT = {
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };

            requestPermission(PERMISSIONS_CONTACT, new OnPermissionListener() {
                @Override
                public void permissionGranted() {
                    getSupportFragmentManager().beginTransaction()
//                            .add(R.id.search_framelayout, new MapSearchFragment())
                            .add(R.id.map, new MapFragment())
                            .commit();
                }
            });
        }
        locationCenter = (TextView) findViewById(R.id.location_center_text);
        list = (ListView) findViewById(R.id.list);
//        list.setOnScrollListener(this);
//        list.setOnItemClickListener(this);
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
    }


    //6.0之后权限返回方法

    public static final int PERMISSIONS_REQUEST = 0;
    private OnPermissionListener activitylistener;

    public void requestPermission(String[] permission, OnPermissionListener listener) {
        boolean flag = true;
        activitylistener = listener;
        for (int i = 0; i < permission.length; i++) {
            if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, permission[i])
                    != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            requestContactsPermissions(permission);
        } else {
            listener.permissionGranted();
        }

    }

    private boolean requestShowRequestPermission(String[] permission) {
        boolean flag = false;
        for (int i = 0; i < permission.length; i++) {
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this, permission[i])) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void requestContactsPermissions(String[] permission) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (requestShowRequestPermission(permission)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            // Display a SnackBar with an explanation and a button to trigger the request.
            android.support.v4.app.ActivityCompat.requestPermissions(this, permission,
                    PERMISSIONS_REQUEST);

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            android.support.v4.app.ActivityCompat.requestPermissions(this, permission, PERMISSIONS_REQUEST);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean flag = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    flag = false;
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(this, "因位置权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "因相机权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "因存储权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(this, "因使用电话权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(this, "因部分权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            if (flag) {
                activitylistener.permissionGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
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
