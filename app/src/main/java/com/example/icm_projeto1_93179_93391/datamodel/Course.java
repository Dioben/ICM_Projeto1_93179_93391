package com.example.icm_projeto1_93179_93391.datamodel;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {
    private long timestamp;
    private double runtime;
    private String user;
    private double  track_length;
    private List<CourseNode> nodes;
    private double max_speed; //no min cuz that'll probably be 0 when the user stops
    private double avg_speed;
    private int rating;
    private String course_id; //
    private boolean iscopy;


    Course(){}
    Course(String user){
        this.user=user;
        this.course_id=user;
        nodes = new ArrayList<>();
    }
    Course(String user,String ID){
        this.user=user;
        nodes = new ArrayList<>();
        course_id = ID; //
        iscopy=true;
    }
    public void append_node(CourseNode x){
        //your validation here
        if (x.getVelocity()>max_speed)
            max_speed= x.getVelocity();
        nodes.add(x);
    }

    public void finalize(){
        //calculate rating and avg_speed here, probably also duration based on nodes*time between node adds
        runtime = nodes.get(nodes.size()-1).getTime_stamp() - nodes.get(0).getTime_stamp();
        track_length=0;
        for(CourseNode x: nodes) track_length+=x.getDistance_from_last();
        timestamp = System.currentTimeMillis();
        track_length/=1000;
        avg_speed = track_length/runtime;
        if (! iscopy)course_id+=timestamp;
        rating = 0; //TODO: IMPLEMENT THIS
    }
    public void submit(){}

    public LatLng centerMapPoint(){return nodes.get(nodes.size()/2).toLatLng();}

    public String getFormattedTimestamp() {//requires tweaks maybe
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    public String formattedRuntime() {//probably reformat worthy, try the thing above maybe idk
        return runtime/1e+9/3600 + " Hours";
    }
    public String formattedTrack_length() {
        return track_length+" km";
    }
    public String formattedMax_speed() {
        return max_speed+ " km/h";
    }
    public String formattedAvg_speed() {
        return avg_speed+ " km/h";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getRuntime() {
        return runtime;
    }

    public String getUser() {
        return user;
    }

    public double getTrack_length() {
        return track_length;
    }

    public List<CourseNode> getNodes() {
        return nodes;
    }

    public double getMax_speed() {
        return max_speed;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public int getRating() {
        return rating;
    }

    public String getCourse_id() {
        return course_id;
    }

    public boolean isIscopy() {
        return iscopy;
    }
}
