package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.network.CourseQueryListener;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;

public class CourseListActivity extends AppCompatActivity implements CourseQueryListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    CourseAdapter mAdapter;
    AutoCompleteTextView dropdown1;
    AutoCompleteTextView dropdown2;
    RecyclerView courselist;
    FirebaseQueryClient client;
    LatLng coords;
    TextWatcher myWatcher;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    FusedLocationProviderClient locationclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        courselist = findViewById(R.id.course_list);
        client = FirebaseQueryClient.getInstance();

        getSupportActionBar().setElevation(20);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }
        locationclient = LocationServices.getFusedLocationProviderClient(this);
        locationclient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            coords = new LatLng(location.getLatitude(), location.getLongitude());
                            CourseListActivity.this.onLocationLoad();
                        } else {
                            Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
            }
        });

        myWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (coords == null) {
                    Toast.makeText(getApplication(), "We do not know your location,cannot fetch tracks", Toast.LENGTH_LONG).show();

                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationclient.getLastLocation().addOnSuccessListener(
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        coords = new LatLng(location.getLatitude(), location.getLongitude());
                                        CourseListActivity.this.onLocationLoad();
                                    } else {
                                        Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
                        }
                    });


                    return;
                }
                String ownvalue = dropdown1.getText().toString().trim();
                String sortval = dropdown2.getText().toString().trim();
                if (ownvalue.equalsIgnoreCase("me")) {
                    if (sortval.equalsIgnoreCase("rating")) {
                        client.getMyCoursesbyRating(CourseListActivity.this);
                    } else {
                        client.getMyCourses(CourseListActivity.this);
                    }
                } else {
                    if (sortval.equalsIgnoreCase("rating")) {
                        client.getTopRatedCourseNearby(coords, 30, CourseListActivity.this);
                    } else {
                        client.getNearbyRecent(coords, 30, CourseListActivity.this);
                    }
                }
            }
        };

        String[] owners = new String[]{"Anyone", "Me"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.course_sort_item, owners);
        dropdown1 = findViewById(R.id.owner_dropdown);
        dropdown1.setAdapter(adapter);
        //dropdown.setFreezesText(false);
        dropdown1.setText(owners[0], false);

        dropdown1.addTextChangedListener(myWatcher);

        String[] sorts = new String[]{"Date", "Rating"};
        adapter = new ArrayAdapter<>(this, R.layout.course_sort_item, sorts);
        dropdown2 = findViewById(R.id.sort_dropdown);
        dropdown2.setAdapter(adapter);
        //dropdown.setFreezesText(false);
        dropdown2.setText(sorts[0], false);
        dropdown2.addTextChangedListener(myWatcher);
        courselist.setLayoutManager(layoutManager);
    }

    private void onLocationLoad() {
        myWatcher.afterTextChanged(null);

    }


    @Override
    public void onCourseListing(LinkedList<Course> list) {
        mAdapter = new CourseAdapter(CourseListActivity.this, list, this);
        courselist.setAdapter(mAdapter);
    }

    @Override
    public void onCourseListingFail() {
        Toast.makeText(this, "Track fetch failed", Toast.LENGTH_LONG).show();
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationclient.getLastLocation().addOnSuccessListener(
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        coords = new LatLng(location.getLatitude(), location.getLongitude());
                                        CourseListActivity.this.onLocationLoad();
                                    } else {
                                        Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplication(), "Failed to get user location", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}