package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
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

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.UpdateCourseTask;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.CourseNode;
import com.example.icm_projeto1_93179_93391.network.CourseSubmitListener;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.example.icm_projeto1_93179_93391.network.ImagePostListener;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.util.Log.i;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback, MapUpdater, CourseSubmitListener, ImagePostListener {
    private static final int TAKE_PICTURE = 2;
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
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.course_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(view ->this.onBackPressed());

        ToggleButton info_button = findViewById(R.id.fullcourse_data_button);
        ImageSpan imageSpan = new ImageSpan(this, R.drawable.ic_baseline_arrow_drop_up_24);
        SpannableString content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        info_button.setText(content);
        info_button.setTextOff(content);
        imageSpan = new ImageSpan(this, R.drawable.ic_baseline_arrow_drop_down_24);
        content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        info_button.setTextOn(content);


        ToggleButton record_button = findViewById(R.id.record_button);
        imageSpan = new ImageSpan(this, R.drawable.ic_baseline_play_arrow_24);
        content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        record_button.setText(content);
        record_button.setTextOff(content);
        imageSpan = new ImageSpan(this, R.drawable.ic_baseline_stop_24);
        content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        record_button.setTextOn(content);

        Button camera_button = findViewById(R.id.camera_button);
        imageSpan = new ImageSpan(this, R.drawable.ic_baseline_photo_camera_24);
        content = new SpannableString("X");
        content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        camera_button.setText(content);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(10);
        pathline = mMap.addPolyline(new PolylineOptions().color(Color.RED));

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
    protected void onRestart() {
        super.onRestart();
        if (isrecording) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // If tracking is turned on, reverse geocode into an address
                    if (isrecording) {
                        new UpdateCourseTask(course, TrackingActivity.this)
                                .execute(locationResult.getLastLocation());
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
        }
    }

    private void startRecording() {
        if (mMap==null){isrecording=false;Toast.makeText(this,
                "Please retry after map initializes",
                Toast.LENGTH_SHORT).show(); return;}
        Toast.makeText(this,"Starting tracking...",Toast.LENGTH_LONG).show();
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        course = new Course(usr.getDisplayName(),usr.getUid(),false);
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






    public void cameraButton(View view){
        if (course==null){
            Toast.makeText(this,"Please start a track first",Toast.LENGTH_LONG).show(); return;
        }
        Intent cameraI = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraI.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,"Failed to Create Picture File",Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                cameraI.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraI,TAKE_PICTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String filename = "cycling"+FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis()+"_";
        File ret = File.createTempFile(filename,".png",storageDir);
        currentPhotoPath = ret.getAbsolutePath();
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {

                FirebaseQueryClient.getInstance().postImage(currentPhotoPath,course,this);

                }
            if (resultCode==RESULT_CANCELED){
                new File(currentPhotoPath).delete();
            }


            }
        }catch (Exception ex){
            Toast.makeText(this,"Something went wrong: "+ ex.toString(),Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.recording_stop);
        player.start();
        finish();
    }

    @Override
    public void onCourseSubmitSuccess() {
        Toast.makeText(this,"Course Submitted",Toast.LENGTH_LONG).show();
        Log.i("this",this.toString());
        onBackPressed();
    }


    @Override
    public void onCourseSubmitFailure() {
        Toast.makeText(this,"Submission Failed",Toast.LENGTH_LONG).show();
        submit=false;
      
    }






    public void upload_button_onClick(View view) {
        ((ToggleButton) findViewById(R.id.record_button)).setChecked(true);

        if (!isrecording){
            MediaPlayer player = MediaPlayer.create(this, R.raw.recording_start);
            player.start();
            isrecording=true;
            startRecording();
            return;
        }
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
                    if (private_button.isChecked())course.isprivate=true;
                    else{course.isprivate=false;}
                    course.name=namebox.getEditText().getText().toString();
                    Toast.makeText(getApplication(),"Submitting course...",Toast.LENGTH_SHORT).show();
                    save(null);}
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.course_layout), Gravity.CENTER, 0, 0);
    }

    public void updateData(){
        TextView travelled_info = findViewById(R.id.course_data_travelled);
        TextView runtime_info = findViewById(R.id.course_data_runtime);
        TextView max_speed_info = findViewById(R.id.course_data_max_speed);
        TextView avg_speed_info = findViewById(R.id.course_data_avg_speed);

        if (course!=null){
            MediaPlayer player = MediaPlayer.create(this, R.raw.new_node);
            player.start();
            List<CourseNode> nodes = course.getNodes();
            if (nodes.size()>0){
                travelled_info.setText(course.formattedTrack_length());
                runtime_info.setText(course.formattedRuntime());
                max_speed_info.setText(course.formattedMax_speed());
                avg_speed_info.setText(String.valueOf(course.formattedAvg_speed()));

            }
        }
    }


    public void fullcourse_data_button_onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        ScrollView scroll = findViewById(R.id.course_data_scrollview);
        LinearLayout buttons = findViewById(R.id.course_buttons);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (button.isChecked()){
            scroll.animate().translationY(-scroll.getHeight()+(float) Math.ceil(85 * metrics.density));
            buttons.animate().translationY(-scroll.getHeight()+(float) Math.ceil(85 * metrics.density));
        } else {
            scroll.animate().translationY(0);
            buttons.animate().translationY(0);
        }
    }

    @Override
    public void OnPostSucess() {
    Toast.makeText(this,"Image Posted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnPostFail() {
        Toast.makeText(this,"Image Post Failed",Toast.LENGTH_SHORT).show();
    }
}