package com.example.icm_projeto1_93179_93391;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setMaxZoomPreference(16);
        mMap.setMinZoomPreference(10);
        /*ZOOM:
        * 1: World
          5: Landmass/continent
          10: City -> ASSUMING THIS IS LIKE , NEW YORK KIND OF CITY
          15: Streets
          20: Buildings
        * */

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Polyline line = mMap.addPolyline( //line.setPoints is a thing ,not quite sure if it live updates-> it does
                new PolylineOptions().add(sydney,new LatLng(-35,151)).color(Color.RED)
        );
        googleMap.addMarker(new MarkerOptions().position(new LatLng(-35,151)));
        List<LatLng> points = line.getPoints();
        points.add(new LatLng(-33,149));
        line.setPoints(points);
    }

}