package com.example.icm_projeto1_93179_93391.datamodel;

import android.location.Location;

import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.maps.model.LatLng;

public class CourseNode {
    private double time_stamp; //gonna have to use system.nano timing cuz duration is api 26
    private double distance_from_last;
    private double velocity;// km/h
    private double lat; //saving location to firebase is tricky so we're using this instead
    private double lon;
    public CourseNode(Location place){
                                lat = place.getLatitude();lon=place.getLongitude();
                               time_stamp= System.nanoTime(); //duration.zero requires api lvl 26
                                distance_from_last=0;velocity=0;
    }
    public CourseNode(Location place, CourseNode previous){
        lat = place.getLatitude();lon=place.getLongitude();
        time_stamp = System.nanoTime();


        distance_from_last = place.distanceTo(previous.toLocation()); // in meters
        double time_ellapsed_hours = time_stamp-previous.time_stamp;
        time_ellapsed_hours = time_ellapsed_hours/ 1e+9/3600;

        velocity = distance_from_last/1000/time_ellapsed_hours;

    }
    public LatLng toLatLng(){return new LatLng(lat,lon);}
    public Location toLocation(){Location x = new Location("");x.setLatitude(lat);x.setLongitude(lon);return x;}

    public double getTime_stamp(){return  time_stamp;}
    public double getDistance_from_last(){return  distance_from_last;}
    public double getVelocity(){return  velocity;}

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
