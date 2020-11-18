package com.example.icm_projeto1_93179_93391.network;

import androidx.annotation.NonNull;

import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class FirebaseQueryClient {

    private CollectionReference fetcher;
    private static FirebaseQueryClient instance;
    private static  User user;
    private static DocumentReference userUpstream;

    private FirebaseQueryClient() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        fetcher = db.collection("courses");
        
    }
    public void setUser(FirebaseUser user){
        userUpstream= FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        userUpstream.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebaseQueryClient.user = documentSnapshot.toObject(User.class);
            }
        });
    }
    public User getUser(){return  user;}



    public  static FirebaseQueryClient getInstance(){if (instance==null) instance = new FirebaseQueryClient();
    return instance;
    }
    public void getClosestCourses(LatLng coords, int limit,CourseQueryListener listener){
        //all the math behind this is worded terribly in walls of text, they better pay me for figuring ths out
        // following this http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
        double r = 100/6371; //100 km over earth radius
        double latmin = Math.toDegrees(Math.toRadians(coords.latitude-r));
        double latmax = Math.toDegrees(Math.toRadians(coords.latitude+r));
        double deltaLon = Math.toDegrees(Math.asin(Math.sin(r)/Math.cos(Math.toRadians(coords.longitude))));
        double longmin = coords.longitude-deltaLon;
        double longmax = coords.longitude-deltaLon; //im not accounting for 180th meridian, i literally dont understand how it works

        LinkedList<Course> courses= new LinkedList<>();
        fetcher.whereEqualTo("isprivate",false).
                whereGreaterThanOrEqualTo("lon",longmax).
                whereGreaterThanOrEqualTo("lat",latmax).
                whereLessThanOrEqualTo("lon",longmin).
                whereLessThanOrEqualTo("lat",latmin).
                orderBy("rating", Query.Direction.DESCENDING).limit(limit).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                LinkedList<Course> courses= new LinkedList<>();
                for (DocumentSnapshot snap: queryDocumentSnapshots.getDocuments()){courses.add(snap.toObject(Course.class)); }
                listener.onCourseListing(courses);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseListingFail();
            }
        });
    }





    public void getTopRatedCourse(int limit,CourseQueryListener listener){

        fetcher.whereEqualTo("isprivate",false).orderBy("rating", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            LinkedList<Course> courses= new LinkedList<>();
                            QuerySnapshot result = task.getResult();
                            List<DocumentSnapshot> documents = result.getDocuments();
                            for (DocumentSnapshot snap: documents){courses.add(snap.toObject(Course.class)); }

                            listener.onCourseListing(courses);
                        }
                    }
                }


        );
    }

    private void getMyCourses(CourseQueryListener listener){
        fetcher.whereEqualTo("uID", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(
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

    public void getUserCourses(String user, CourseQueryListener listener){//duplicate usernames

        fetcher.whereEqualTo("user",user).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            LinkedList<Course> courses= new LinkedList<>();
                            QuerySnapshot result = task.getResult();
                            List<DocumentSnapshot> documents = result.getDocuments();
                            for (DocumentSnapshot snap: documents){courses.add(snap.toObject(Course.class)); }

                            listener.onCourseListing(courses);
                        }
                    }
                }


        ); //This can probably get pretty memory intensive after a couple years, maybe archive courses to a different doc after some time in a full implementation

    }




    public void submitCourse(Course course,CourseSubmitListener listener){
        user.addRuntimeNs(course.getRuntime());
        user.addLengthKM(course.getTrack_length());
        user.setAvg_speed(user.getTotal_tracklength()/user.getTotal_runtime()/1e+9/3600);
        if (course.getMax_speed()>user.getMax_speed())
            user.setMax_speed(course.getMax_speed());
        if (course.getRating()>user.getTop_rating())
            user.setTop_rating(course.getRating());

        fetcher.add(course).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) { //TODO: UPDATE USER AND SEND IT TOO
                userUpstream.set(user);
                listener.onCourseSubmitSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onCourseSubmitFailure();
            }
        });
    }


}
