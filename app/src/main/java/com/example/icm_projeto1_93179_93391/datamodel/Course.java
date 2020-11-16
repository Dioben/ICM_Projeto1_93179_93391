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
    public boolean isprivate;
    public boolean anon;
    public String name;

    public Course(){}
    public Course(String user){
        this.user=user;
        this.course_id=user;
        nodes = new ArrayList<>();
    }
    public Course(String user,String ID){
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
        track_length+=x.getDistance_from_last()/1000;
    }

    public void finalize(){
        //calculate rating and avg_speed here, probably also duration based on nodes*time between node adds
        if (nodes.size()==0){return;}
        runtime = nodes.get(nodes.size()-1).getTime_stamp() - nodes.get(0).getTime_stamp();

        timestamp = System.currentTimeMillis();
        avg_speed = track_length/runtime;
        if (! iscopy)course_id+=timestamp;
        rating = 0; //TODO: IMPLEMENT THIS
    }

    public LatLng centerMapPoint(){return nodes.get(nodes.size()/2).toLatLng();}

    public String getFormattedTimestamp() {//requires tweaks maybe
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    public String formattedRuntime() {//probably reformat worthy, try the thing above maybe idk
        double rt = runtime/1e+9/3600;
        String ret ="";
        if (rt>3600){int hours =(int) rt/3600;
            ret+= hours+":";
            rt-=hours*3600;
        }
        int mins = (int) rt/60;
        ret+=mins +":";
        rt-=60*mins;
        int seconds = (int)rt;
        ret+=seconds;
        return ret;
    }
    public String formattedTrack_length() {
        return String.format("%.3f",track_length)+" km";
    }
    public String formattedMax_speed() {
        return String.format("%.3f",max_speed)+ " km/h";
    }
    public String formattedAvg_speed() {
        return String.format("%.3f",avg_speed)+ " km/h";
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

    public String getName() {
        return name;
    }

    public boolean isIsprivate() {
        return isprivate;
    }

    public boolean isAnon() {
        return anon;
    }
}
