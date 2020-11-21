package com.example.icm_projeto1_93179_93391.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course implements Parcelable {
    private long timestamp;
    private double runtime;
    private String user;
    private String uID;
    private double  track_length;
    private List<CourseNode> nodes;
    private List<String> pictures;
    private double max_speed; //no min cuz that'll probably be 0 when the user stops
    private double avg_speed;
    private int rating;
    private String course_id; //
    private boolean iscopy;
    public boolean isprivate;
    public boolean anon;
    public String name;
    private double lat; //querying inner doc range seems like a mess or even impossible
    private double lon;
    public Course(){} //DO NOT REMOVE, FIREBASE NEEDS THIS
    public Course(String user,String uID,boolean copy){
        this.user=user;
        this.course_id=uID;
        nodes = new ArrayList<>();
        iscopy= copy;
        pictures = new ArrayList<>();
        this.uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Course(String user,String uID){ //will add ts to id later
        this.user=user;
        this.course_id=uID;
        nodes = new ArrayList<>();
        this.uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pictures = new ArrayList<>();
    }

    protected Course(Parcel in) {
        timestamp = in.readLong();
        runtime = in.readDouble();
        user = in.readString();
        uID = in.readString();
        track_length = in.readDouble();
        nodes = in.createTypedArrayList(CourseNode.CREATOR);
        pictures = in.createStringArrayList();
        max_speed = in.readDouble();
        avg_speed = in.readDouble();
        rating = in.readInt();
        course_id = in.readString();
        iscopy = in.readByte() != 0;
        isprivate = in.readByte() != 0;
        anon = in.readByte() != 0;
        name = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeDouble(runtime);
        dest.writeString(user);
        dest.writeString(uID);
        dest.writeDouble(track_length);
        dest.writeTypedList(nodes);
        dest.writeStringList(pictures);
        dest.writeDouble(max_speed);
        dest.writeDouble(avg_speed);
        dest.writeInt(rating);
        dest.writeString(course_id);
        dest.writeByte((byte) (iscopy ? 1 : 0));
        dest.writeByte((byte) (isprivate ? 1 : 0));
        dest.writeByte((byte) (anon ? 1 : 0));
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

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
        lat = nodes.get(0).getLat();
        lon = nodes.get(0).getLon();
        timestamp = System.currentTimeMillis();
        avg_speed = track_length/runtime/1e+9/3600;
        if (! iscopy)course_id+=timestamp;
        rating = (int)(avg_speed*runtime/1e+9/60);
    }

    public LatLng centerMapPoint(){return nodes.get(nodes.size()/2).toLatLng();}

    public String getFormattedTimestamp() {//requires tweaks maybe
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(timestamp);
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

    public String getuID(){return uID;}

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public void setIscopy(boolean iscopy) {
        this.iscopy = iscopy;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }
}
