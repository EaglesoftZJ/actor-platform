package com.android_gaode_maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by kiolt_000 on 22/09/2014.
 */
public class PlaceFetchingTask extends AsyncTask<Void, Void, Object> {
    private static final String LOG_TAG = "ExampleApp";

    //    http://restapi.amap.com/v3/geocode/regeo?key=您的key&location=116.481488,39.990464&poitype=商务写字楼&radius=1000&extensions=all&batch=false&roadlevel=0
    private static final String PLACES_API_BASE = "http://restapi.amap.com/v3/geocode/regeo";
    private static final String METHOD_NAME = "/search";
    private static final String OUT_JSON = "/json";

    //    private static final String API_KEY = "AIzaSyBV6Kul7Ybt_7yeEd6Im7gSjN_0fNu_Psw";
    private static final String API_KEY = "2c676be192641e1611b4e44087061878";
    private final String query;
    private final int radius;
    private final double latitude;
    private final double longitude;
    // todo google places api key ?


    public PlaceFetchingTask(String query, int radius, double latitude, double longitude) {
        this.query = query;
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Object doInBackground(Void... voids) {

        ArrayList<MapItem> resultList = null;
//        JSONObject json = new JSONObject();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append("?key=" + API_KEY);
            sb.append("&radius=" + radius);
            sb.append("&extensions=all");
            sb.append("&location=" + longitude + "," + latitude);
            if (query != null && query.trim().length() > 0)
                sb.append("&poitype=" + URLEncoder.encode(query, "utf8"));
            else
                sb.append("&poitype=");

            sb.append("&batch=false");
            sb.append("&roadlevel=0");
            URL url = new URL(sb.toString());
            Log.i(LOG_TAG, "url: " + sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            Log.i(LOG_TAG, "Response: " + jsonResults.toString());
        } catch (MalformedURLException e) {
            if (conn != null) {
                conn.disconnect();
            }
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return e;
        } catch (IOException e) {
            if (conn != null) {
                conn.disconnect();
            }
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonResult = new JSONObject(jsonResults.toString());
            JSONArray jsonResultItems = jsonResult.getJSONObject("regeocode").getJSONArray("pois");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<MapItem>(jsonResultItems.length());
            JSONObject adressJson = jsonResult.getJSONObject("regeocode").getJSONObject("addressComponent");
            final String address = adressJson.getString("province") + adressJson.getString("city") +
                    adressJson.getString("district") +
                    adressJson.getString("township");
            for (int i = 0; i < jsonResultItems.length(); i++) {
                // todo json parser

                final JSONObject jsonResultItem = jsonResultItems.getJSONObject(i);
                MapItem item = new MapItem() {
                    {
                        id = jsonResultItem.optString("id", null);
                        name = jsonResultItem.optString("name", null);
                        vicinity = address+jsonResultItem.optString("address", null);
                        icon = jsonResultItem.optString("icon", null);
                        if (jsonResultItem.has("location")) {
                            geometry = new Geometry();
                            geometry.location = new Location() {
                                {
                                    String[] location = jsonResultItem.optString("location").split(",");
                                    lng = Double.parseDouble(location[0]);
                                    lat = Double.parseDouble(location[1]);
                                }
                            };
                        }
                    }
                };
                resultList.add(item);
            }
//            json.put("center",jsonResult.getJSONObject("regeocode").getString("formatted_address"));
//            json.put("list",resultList);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "数据解析出错", e);
            return e;
        }


        return resultList;

    }
}
