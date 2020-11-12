package com.example.icm_projeto1_93179_93391;

import android.location.Location;
import android.os.AsyncTask;

import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.datamodel.CourseNode;
import com.example.icm_projeto1_93179_93391.ui.MapUpdater;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UpdateCourseTask extends AsyncTask<Location,Void, List<LatLng>> {
    Course course;
    MapUpdater mapviewer;
    public UpdateCourseTask(Course course, MapUpdater mUpdater) {
        this.course=course;
        mapviewer = mUpdater;
    }

    @Override
    protected List<LatLng> doInBackground(Location... locations) {

        List<CourseNode> nodes = course.getNodes();

        CourseNode x;

        if(nodes.size()==0){ x= new CourseNode(locations[0]);}
        else{x= new CourseNode(locations[0],nodes.get(nodes.size()-1));}

        course.append_node(x);
        List<LatLng> coords = new ArrayList<>();
        for (CourseNode node:nodes){coords.add(node.toLatLng());}
        return coords;
    }

    @Override
    protected void onPostExecute(List<LatLng> latLngs) {
        if (latLngs.size()==1){mapviewer.initMapPath(latLngs.get(0));}
        mapviewer.updateMapPath(latLngs);
    }
}
