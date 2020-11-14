package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.UpdateCourseTask;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.CourseNode;
import com.example.icm_projeto1_93179_93391.network.CourseSubmitListener;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, MapUpdater, CourseSubmitListener {
    private Course course;
    private boolean isrecording;
    public GoogleMap mMap;
    private String user;
    private Polyline pathline;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (isrecording) {
                    new UpdateCourseTask(course,TrackingActivity.this)
                            .execute(locationResult.getLastLocation());}
            }
        };
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
        mMap.setMaxZoomPreference(16);
        mMap.setMinZoomPreference(10);
    }
    public void recordPress(View view){
        if (isrecording){isrecording=false;stopRecording();}
        else{isrecording=true;startRecording();}
    }

    private void startRecording() {
        if (mMap==null){isrecording=false;Toast.makeText(this,
                "Please retry after map initializes",
                Toast.LENGTH_SHORT).show(); return;}
        course = new Course(user); //WARNING: USER IS NOT SET AT THE MOMENT
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
        }
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void stopRecording() {
        course.finalize();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        List<CourseNode> nodes =course.getNodes();
        mMap.addMarker(new MarkerOptions().position(nodes.get(nodes.size()-1).toLatLng()).title("Finish"));
    }

    @Override
    public void updateMapPath(List<LatLng> x) {
    pathline.setPoints(x);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(x.get(x.size()-1)));
    }

    @Override
    public void initMapPath(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void showMore(View view){

    }
    public void save(View view){
        FirebaseQueryClient client = FirebaseQueryClient.getInstance();
        client.submitCourse(course,this);
    }


    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onCourseSubmitSuccess() {
    Toast.makeText(this,"Course Submitted",Toast.LENGTH_LONG);
    }

    @Override
    public void onCourseSubmitFailure() {
        Toast.makeText(this,"Submission Failed",Toast.LENGTH_LONG);
    }
}