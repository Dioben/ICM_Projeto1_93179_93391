package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static android.util.Log.i;

public class FollowingActivity extends AppCompatActivity implements OnMapReadyCallback, MapUpdater, CourseSubmitListener {
    private Course course;
    private boolean isrecording;
    public GoogleMap mMap;
    private Polyline pathline;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public Marker firstmarker;
    public Marker lastmarker;
    boolean submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        Toolbar myToolbar = findViewById(R.id.course_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(10);
        pathline = mMap.addPolyline(new PolylineOptions().color(Color.RED));

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


    private void startRecording() {
        if (mMap==null){isrecording=false;
            Toast.makeText(this,
                    "Please retry after map initializes",
                    Toast.LENGTH_SHORT).show(); return;}
        Toast.makeText(this,"Starting tracking...",Toast.LENGTH_LONG).show();
        if (lastmarker!=null){lastmarker.remove();lastmarker=null;}
        if (firstmarker!=null){firstmarker.remove();}
        if (course==null){
            FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
            course = new Course(usr.getDisplayName(),usr.getUid(),false);
        }
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
                        new UpdateCourseTask(course,FollowingActivity.this)
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

    @Override
    protected void onDestroy() {
        if (mLocationCallback!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);}
        super.onDestroy();
    }


    @Override
    public void updateMapPath(List<LatLng> x) {
        if (lastmarker==null)
            lastmarker=mMap.addMarker(new MarkerOptions().position(x.get(x.size()-1)).title("Current"));
        else{ lastmarker.setPosition(x.get(x.size()-1));}
        pathline.setPoints(x);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(x.get(x.size()-1)));
        updateData();
    }


    @Override
    public void initMapPath(LatLng latLng) {
        firstmarker =mMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        updateData();
    }


    public void save(View view){
        FirebaseQueryClient client = FirebaseQueryClient.getInstance();
        client.submitCourse(course,this);
        updateData();
    }


    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(7500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }





    @Override
    public void onCourseSubmitSuccess() {
        Toast.makeText(this,"Course Submitted",Toast.LENGTH_LONG).show();
        Log.i("this",this.toString());

        Intent ret = new Intent(this,main_menu.class);
        startActivity(ret);
    }


    @Override
    public void onCourseSubmitFailure() {
        Toast.makeText(this,"Submission Failed",Toast.LENGTH_LONG).show();
        submit=false;

    }






    public void upload_button_onClick(View view) {

        if (!isrecording){isrecording=true;startRecording();return;}
        course.finalize();
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
                if (!submit){
                    if (course.getNodes().size()<10){Toast.makeText(getApplication(),"Course is too short",Toast.LENGTH_LONG).show();return;}
                    submit=true;
                    course.finalize();
                    CheckBox box = popupView.findViewById(R.id.anonymous_checkbox);
                    if (box.isChecked()) course.anon=true;
                    TextInputLayout namebox = popupView.findViewById(R.id.course_name_box);
                    course.name=namebox.getEditText().getText().toString();
                    Toast.makeText(getApplication(),"Submitting course...",Toast.LENGTH_SHORT).show();
                    save(null);}
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.course_layout), Gravity.CENTER, 0, 0);
    }

    public void updateData(){
        TextView datacontainer = findViewById(R.id.course_data);
        String data ="\nData:\n\n";
        if (course!=null){
            List<CourseNode> nodes = course.getNodes();
            if (nodes.size()>0){
                double runtime = nodes.get(nodes.size()-1).getTime_stamp() - nodes.get(0).getTime_stamp();
                runtime/=1e9;//->seconds
                String rt ="";
                if (runtime>3600){int hours =(int) runtime/3600;
                    rt+= hours+":";
                    runtime-=hours*3600;
                }
                int mins = (int) runtime/60;
                rt+=mins +":";
                runtime-=60*mins;
                int seconds = (int)runtime;
                rt+=seconds;

                data +="Running for "+rt+"\nTravelled "+course.formattedTrack_length()+"\n";
                data+="Max Speed: "+course.formattedMax_speed()+"\n";
                data+="Nodes: "+nodes.size();

            }
        }
        datacontainer.setText(data);
    }


    public void fullcourse_data_button_onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        ScrollView scroll = (ScrollView) findViewById(R.id.course_data_scrollview);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (button.isChecked()){
            scroll.animate().translationY(-scroll.getHeight()+(float) Math.ceil(85 * metrics.density));
        } else {
            scroll.animate().translationY(0);
        }
    }
}