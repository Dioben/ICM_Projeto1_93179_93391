package com.example.icm_projeto1_93179_93391.network;

import androidx.annotation.NonNull;

import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    CollectionReference fetcher;
    public FirebaseQueryClient() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        fetcher = db.collection("courses");
        
    }
    public void getClosestCourses(LatLng coords, int limit,CourseQueryListener listener){
        LinkedList<Course> courses= new LinkedList<>();
        //come up with whatever criteria make up being close and copy paste another of the functions here
    }





    public void getTopRatedCourse(int limit,CourseQueryListener listener){

        fetcher.orderBy("rating", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(
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



    public void getUserCourses(String user, CourseQueryListener listener){

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
    //TODO: FIGURE OUT AUTOINCREMENT ID I GUESS  -> just use .add instead of .set
        fetcher.add(course).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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
