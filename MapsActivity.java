package com.example.foodspy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.6f,%.6f&radius=%d&types=%s&sensor=false&key=%s";
    String API_KEY = "AIzaSyCSPX4MUceAV89e6DISWcR3kOziKH1HZ4I";
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    Set<String> recentList = new HashSet<>();
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String[] menuItems = {"Favourites", "Nearby places", "Recently Viewed"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, menuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
        editor = pref.edit();
        recentList = pref.getStringSet("recents", null);
        if (recentList == null) {
            recentList = new HashSet<>();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        refresh();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(getApplication(), EstablishmentActivity.class);
                i.putExtra("placeID", marker.getTag().toString());
                recentList.add(marker.getTag().toString()+"|"+marker.getTitle());
                Set<String> set = new HashSet<String>(recentList);
                editor.putStringSet("recents", set);
                editor.commit();
                startActivity(i);
                return false;
            }
        });
    }
    private void refresh() {
        getNearEstablishments(getIntent().getExtras().getString("estType"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude")), 17));
    }
    private void getNearEstablishments(String estType){
        RequestQueue queue = Volley.newRequestQueue(this);
        String requestUrl = String.format(API_URL, getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"), 1000, estType, API_KEY);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            JSONArray jPlaces = jObject.getJSONArray("results");
                            int placesCount = jPlaces.length();
                            for (int i = 0; i < placesCount; i++) {
                                JSONObject p = (JSONObject) jPlaces.get(i);

                                if (p.has("geometry")) {
                                    JSONObject g = p.getJSONObject("geometry");
                                    if (g.has("location")) {
                                        JSONObject l = g.getJSONObject("location");
                                        if (l.has("lat") && l.has("lng")){
                                            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(l.getDouble("lat"), l.getDouble("lng"))).title(p.getString("name")));
                                            m.setTag(p.getString("place_id"));
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position){
            case 0:
                //Favourites
                Intent f = new Intent(this, FavouritesActivity.class);
                f.putExtra("estType", getIntent().getExtras().getString("estType"));
                startActivity(f);
                break;
            case 1:
                //Nearby Places
                Intent i = new Intent(MapsActivity.this, NearbyActivity.class);
                i.putExtra("estType", getIntent().getExtras().getString("estType"));
                GPSTracker gps = new GPSTracker(MapsActivity.this);
                // check if GPS enabled
                if(!gps.canGetLocation())
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                i.putExtra("latitude", gps.getLatitude());
                i.putExtra("longitude", gps.getLongitude());
                startActivity(i);
                break;
            case 2:
                //Recently Viewed
                Intent r = new Intent(this, RecentlyViewed.class);
                r.putExtra("estType", getIntent().getExtras().getString("estType"));
                startActivity(r);
                break;
        }
    }
}
