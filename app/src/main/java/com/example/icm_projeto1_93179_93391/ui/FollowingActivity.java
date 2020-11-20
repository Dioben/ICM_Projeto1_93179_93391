package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import com.example.icm_projeto1_93179_93391.CourseCompareTask;
import com.example.icm_projeto1_93179_93391.ExtractOGTask;
import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.UpdateCourseTask;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.CourseComparison;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.i;

public class FollowingActivity extends AppCompatActivity implements OnMapReadyCallback, CompareMapUpdater, CourseSubmitListener,OnLatLngExtract {
    private Course course;
    private Course og;
    private boolean isrecording;
    public GoogleMap mMap;
    private Polyline pathline;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public Marker firstmarker;
    public Marker lastmarker;
    boolean submit;
    boolean haserror;
    Location start;
    CourseComparison comp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.course_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(view ->this.onBackPressed());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent origin = getIntent();
        og = origin.getParcelableExtra("course");
        start = og.getNodes().get(0).toLocation();

    }

    @Override
    public void onBackPressed() {
        Intent ret = new Intent();
        ret.putExtra("course",course);
        finish();
    }



    private void startRecording() {
        if (mMap==null){Toast.makeText(this,
                "Please retry after map initializes",
                Toast.LENGTH_SHORT).show(); return;}


        if (course==null){
            FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
            course = new Course(usr.getDisplayName(),og.getCourse_id(),true);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else {
            Toast.makeText(this,"Checking Starting Point...",Toast.LENGTH_LONG).show();
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location.distanceTo(start)<250) {
                                ActualStartTracking();
                                course.append_node(new CourseNode(location));
                                FollowingActivity.this.initMapPath(new LatLng(location.getLatitude(),location.getLongitude()));
                            }
                            else{
                                Toast.makeText(getApplication(),"You must be within 250m of starting point to start,current distance: "+location.distanceTo(start),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplication(),"Could not find your location",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void ActualStartTracking() {
        Button btn = findViewById(R.id.start_button);
        comp = new CourseComparison(og,course);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (isrecording) {
                    new CourseCompareTask(comp,FollowingActivity.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        mFusedLocationClient.requestLocationUpdates
                (getLocationRequest(), mLocationCallback,
                        null /* Looper */);
        Toast.makeText(getApplication(),"Starting tracking...",Toast.LENGTH_LONG).show();
        isrecording=true;
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

    @Override
    public void onUpdateError(String errorstring, Location spot) {
    if (!haserror){
        haserror=true;
        mMap.addMarker(new MarkerOptions().title(errorstring).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        Toast.makeText(this,"Divergence Detected",Toast.LENGTH_SHORT).show();
    }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) { //TODO:PROBABLY ADD IN CAMERA SUPPORT
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
    public void upload_button_onClick(View view) {

        if (!isrecording){startRecording();return;}
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
                TextInputLayout namebox = popupView.findViewById(R.id.course_name_box);
                MaterialButton private_button = popupView.findViewById(R.id.privacy_private_button);
                MaterialButton public_button = popupView.findViewById(R.id.privacy_public_button);
                TextView privacy_error = popupView.findViewById(R.id.privacy_button_bar_error);
                namebox.setErrorEnabled(false);
                privacy_error.setVisibility(View.GONE);
                if ((namebox.getEditText().getText().toString().trim().isEmpty())||(!(private_button.isChecked() || public_button.isChecked()))) {
                    if (namebox.getEditText().getText().toString().trim().isEmpty()) {
                        namebox.setError("Name field cannot be empty");
                        namebox.setErrorEnabled(true);
                    }
                    if (!(private_button.isChecked() || public_button.isChecked())) {
                        privacy_error.setVisibility(View.VISIBLE);
                    }
                    return;
                }

                if (!submit){
                    if (course.getNodes().size()<10){Toast.makeText(getApplication(),"Course is too short",Toast.LENGTH_LONG).show();return;}
                    submit=true;
                    course.finalize();
                    CheckBox box = popupView.findViewById(R.id.anonymous_checkbox);
                    if (box.isChecked()) course.anon=true;
                    course.name=namebox.getEditText().getText().toString();
                    Toast.makeText(getApplication(),"Submitting course...",Toast.LENGTH_SHORT).show();
                    save(null);}
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.course_layout), Gravity.CENTER, 0, 0);
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


    public void fullcourse_data_button_onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        ScrollView scroll = findViewById(R.id.course_data_scrollview);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (button.isChecked()){
            scroll.animate().translationY(-scroll.getHeight()+(float) Math.ceil(85 * metrics.density));
        } else {
            scroll.animate().translationY(0);
        }
    }

    @Override
    public void OnLatLngExtract(List<LatLng> x) {
    mMap.addPolyline(new PolylineOptions().color(Color.BLUE).addAll(x));
    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Starting position").position(x.get(0)));
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Expected end").position(x.get(x.size()-1)));
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(10);

        pathline = mMap.addPolyline(new PolylineOptions().color(Color.RED));
        new ExtractOGTask(this).execute(og);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    protected void onDestroy() {
        if (mLocationCallback!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);}
        super.onDestroy();
    }
}