import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.time.Duration;

public class CourseNode {
    private Location location;
    private double time_stamp; //gonna have to use system.nano timing cuz duration is api 26
    private double distance_from_last;
    private double velocity;// km/h

    CourseNode(Location place){location=place;
                               time_stamp= System.nanoTime(); //duration.zero requires api lvl 26
                                distance_from_last=0;velocity=0;
    }
    CourseNode(Location place, CourseNode previous){
        location= place;
        time_stamp = System.nanoTime();


        distance_from_last = place.distanceTo(previous.location); // in meters
        double time_ellapsed_hours = time_stamp-previous.time_stamp;
        time_ellapsed_hours = time_ellapsed_hours/ 1e+9/3600;

        velocity = distance_from_last/1000/time_ellapsed_hours;

    }
    public LatLng toLatLng(){return new LatLng(location.getLatitude(),location.getLongitude());}


    public double getTime_stamp(){return  time_stamp;}
    public double getDistance_from_last(){return  distance_from_last;}
    public double getVelocity(){return  velocity;}
}
