package com.example.icm_projeto1_93179_93391;

import android.os.AsyncTask;

import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.CourseNode;
import com.example.icm_projeto1_93179_93391.ui.OnLatLngExtract;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ExtractOGTask extends AsyncTask<Course,Void, List<LatLng> > {
    private OnLatLngExtract listener;
    public ExtractOGTask(OnLatLngExtract l){listener=l;}

    @Override
    protected List<LatLng> doInBackground(Course... courses) {
        List<LatLng> coords= new ArrayList<>();
        for(CourseNode x:courses[0].getNodes()){
            coords.add(x.toLatLng());
        }
        return coords;
    }

    @Override
    protected void onPostExecute(List<LatLng> latLngs) {
        listener.OnLatLngExtract(latLngs);
    }
}
