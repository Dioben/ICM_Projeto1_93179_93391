package com.example.icm_projeto1_93179_93391.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Ordering;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.QueryListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FirebaseQueryClient {
    //DISCLAIMER: FIREBASE SORTING AND IF CONDITIONS ARE BASICALLY WORTHLESS, LEADING TO THIS MESS OF A SYSTEM
    private CollectionReference fetcher;
    private static FirebaseQueryClient instance;
    private static  User user;
    private static DocumentReference userUpstream;
    private static CollectionReference userCourses;
    private FirebaseQueryClient() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        fetcher = db.collection("courses");
        
    }
    public void setUser(FirebaseUser user){
        userUpstream= FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        userCourses = FirebaseFirestore.getInstance().collection("privatecourses").document("mandatory").collection(user.getUid());
        userUpstream.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                FirebaseQueryClient.user = documentSnapshot.toObject(User.class);
                else{
                    FirebaseQueryClient.user = new User();
                    FirebaseQueryClient.user.setUid(user.getUid());
                    FirebaseQueryClient.user.setUsername(user.getDisplayName());
                }
            }

        });
    }
    public User getUser(){return  user;}



    public  static FirebaseQueryClient getInstance(){if (instance==null) instance = new FirebaseQueryClient();
    return instance;
    }


    public void submitCourse(Course course,CourseSubmitListener listener){

        user.addRuntimeNs(course.getRuntime());
        user.addLengthKM(course.getTrack_length());
        user.setAvg_speed(user.getTotal_tracklength()/user.getTotal_runtime()/1e+9/3600);
        if (course.getMax_speed()>user.getMax_speed())
            user.setMax_speed(course.getMax_speed());
        if (course.getRating()>user.getTop_rating())
            user.setTop_rating(course.getRating());
        if( !course.isprivate)
            fetcher.add(course);
        userCourses.add(course).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        user.setCoursecount(user.getCoursecount()+1);
                        userUpstream.set(user);
                        listener.onCourseSubmitSuccess();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseSubmitFailure();
            }
        });





    }










    public void getNearbyRecent(LatLng coords, int limit,CourseQueryListener listener){
        double r = 100.0/6371.0; //100 km over earth radius
        double latmin = Math.toDegrees(Math.toRadians(coords.latitude)-r);
        double latmax = Math.toDegrees(Math.toRadians(coords.latitude)+r);

        double deltaLon = Math.toDegrees(Math.asin(Math.sin(r)/Math.cos(Math.toRadians(coords.longitude))));
        double longmin = coords.longitude-deltaLon;
        double longmax = coords.longitude+deltaLon;
        //where statements dont work because or orderby, instead queried way more stuff and filtered locally
        //if id known this i wouldve just used mongodb
        fetcher.orderBy("timestamp", Query.Direction.DESCENDING).
                limit(limit*20).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                LinkedList<Course> courses= new LinkedList<>();
                int count = 0;
                for (DocumentSnapshot snap: queryDocumentSnapshots.getDocuments()){
                    if (count==limit)break;
                    Course toadd = snap.toObject(Course.class);
                    if (toadd.getLon()<=longmax && toadd.getLon()>=longmin && toadd.getLat()>=latmin && toadd.getLat()<=latmax)
                    {courses.add(toadd);count++;}
                }

                listener.onCourseListing(courses);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseListingFail();
            }
        });

    }





    public void getMyCourses(CourseQueryListener listener){
        userCourses.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        LinkedList<Course> courses= new LinkedList<>();
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snap: documents){courses.add(snap.toObject(Course.class)); }
                        listener.onCourseListing(courses);
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseListingFail();
            }
        });
    }








    public void getMyCoursesbyRating(CourseQueryListener listener){
        userCourses.orderBy("rating", Query.Direction.DESCENDING).get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        LinkedList<Course> courses= new LinkedList<>();
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snap: documents){courses.add(snap.toObject(Course.class)); }
                        listener.onCourseListing(courses);
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseListingFail();
            }
        });
    }


    public void getTopRatedCourseNearby(LatLng coords, int limit,CourseQueryListener listener){
        double r = 100.0/6371.0; //100 km over earth radius
        double latmin = Math.toDegrees(Math.toRadians(coords.latitude)-r);
        double latmax = Math.toDegrees(Math.toRadians(coords.latitude)+r);

        double deltaLon = Math.toDegrees(Math.asin(Math.sin(r)/Math.cos(Math.toRadians(coords.longitude))));
        double longmin = coords.longitude-deltaLon;
        double longmax = coords.longitude+deltaLon;


        fetcher.orderBy("rating", Query.Direction.DESCENDING).
                limit(limit*20).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                LinkedList<Course> courses= new LinkedList<>();
                int count = 0;
                for (DocumentSnapshot snap: queryDocumentSnapshots.getDocuments()){
                    if (count==limit) break;
                    Course toadd = snap.toObject(Course.class);
                    if (toadd.getLon()<=longmax && toadd.getLon()>=longmin && toadd.getLat()>=latmin && toadd.getLat()<=latmax)
                    {courses.add(toadd);count++;}
                    }
                listener.onCourseListing(courses);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseListingFail();
            }
        });

    }


}
