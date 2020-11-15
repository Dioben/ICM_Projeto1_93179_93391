package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import static android.util.Log.i;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback, MapUpdater, CourseSubmitListener {
    private Course course;
    private boolean isrecording;
    public GoogleMap mMap;
    private String user;
    private Polyline pathline;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public Marker firstmarker;
    public Marker lastmarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.course_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        ScrollView sv = findViewById(R.id.course_data_scrollview);
//        ConstraintLayout sl = findViewById(R.id.course_layout);
//        sv.getLayoutParams().height = (int) sl.getHeight()/2;
//        sl.requestLayout();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng place = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                }
            }
        });
    }
    public void recordPress(View view){
        if (isrecording){isrecording=false;stopRecording();}
        else{isrecording=true;startRecording();}
    }

    private void startRecording() {
        Log.i("starting", "startRecording called");
        if (mMap==null){isrecording=false;Toast.makeText(this,
                "Please retry after map initializes",
                Toast.LENGTH_SHORT).show(); return;}
        if (lastmarker!=null){lastmarker.remove();lastmarker=null;}
        if (firstmarker!=null){firstmarker.remove();}
        course = new Course(user); //WARNING: USER IS NOT SET AT THE MOMENT
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // If tracking is turned on, reverse geocode into an address
                    i("isrecording","record status: "+isrecording);
                    if (isrecording) {
                        new UpdateCourseTask(course,TrackingActivity.this)
                                .execute(locationResult.getLastLocation());}
                }
            };
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
        if (lastmarker!=null)lastmarker.setTitle("Finish");
    }

    @Override
    public void updateMapPath(List<LatLng> x) {
        if (lastmarker==null)
            lastmarker=mMap.addMarker(new MarkerOptions().position(x.get(x.size()-1)).title("Current"));
        else{ lastmarker.setPosition(x.get(x.size()-1));}
        pathline.setPoints(x);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(x.get(x.size()-1)));
    }


    @Override
    public void initMapPath(LatLng latLng) {
        firstmarker =mMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    public void fullcourse_data_button_onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        ScrollView scroll = (ScrollView) findViewById(R.id.course_data_scrollview);

        if(button.isChecked())
        {
            ObjectAnimator animation = ObjectAnimator.ofFloat(scroll, "translationY", -800);
            animation.setDuration(500);
            animation.start();
        }
        else
        {
            ObjectAnimator animation = ObjectAnimator.ofFloat(scroll, "translationY", 0);
            animation.setDuration(500);
            animation.start();
        }
    }

    public void upload_button_onClick(View view) {
        recordPress(null);
        if (isrecording){return;}
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_tracking_popup, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        Button confirm_upload_button = (Button) popupView.findViewById(R.id.confirm_upload_button);
        confirm_upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                onBackPressed();
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.course_layout), Gravity.CENTER, 0, 0);
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