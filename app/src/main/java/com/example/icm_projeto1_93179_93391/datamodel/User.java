package com.example.icm_projeto1_93179_93391.datamodel;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String username;
    private String uid;
    private double avg_speed;
    private double max_speed;
    private long top_rating;
    private double total_runtime;
    private double total_tracklength;

    public User(){}



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(double avg_speed) {
        this.avg_speed = avg_speed;
    }

    public double getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(double max_speed) {
        this.max_speed = max_speed;
    }

    public long getTop_rating() {
        return top_rating;
    }

    public void setTop_rating(long top_rating) {
        this.top_rating = top_rating;
    }

    public double getTotal_runtime() {
        return total_runtime;
    }

    public void setTotal_runtime(double total_runtime) {
        this.total_runtime = total_runtime;
    }

    public double getTotal_tracklength() {
        return total_tracklength;
    }

    public void setTotal_tracklength(double total_tracklength) {
        this.total_tracklength = total_tracklength;
    }
    public void addRuntimeNs(double rt){total_runtime+=rt;}
    public void addLengthKM(double km){total_tracklength+=km;}
}
