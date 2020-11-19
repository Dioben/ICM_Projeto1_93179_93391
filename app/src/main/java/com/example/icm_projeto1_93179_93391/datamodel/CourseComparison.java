package com.example.icm_projeto1_93179_93391.datamodel;

import android.location.Location;

import com.example.icm_projeto1_93179_93391.ui.CompareMapUpdater;
import com.google.android.gms.maps.model.LatLng;


import java.util.LinkedList;
import java.util.List;

public class CourseComparison {
    private Course og;
    private Course copy;
    private Location current;
    private int currentnode;
    private boolean validcopy;
    private String errorcode;
    private Location diffOG;
    LinkedList<LatLng> drawable;
    private CourseComparison(Course original,Course dupe){
        og = original;
        copy=dupe;
        validcopy= true;
        currentnode=0;
        current = og.getNodes().get(0).toLocation();
    }
    public static CourseComparison generate(Course og, Course dupe, Location current) {
        Location start = og.getNodes().get(0).toLocation();
        if (start.distanceTo(current)>50)
        return null;
        return new CourseComparison(og,dupe);

    }

    public void appendNode(Location local){
        List<CourseNode> nodes = copy.getNodes();

        CourseNode x;

        if(nodes.size()==0){ x= new CourseNode(local);}
        else{x= new CourseNode(local,nodes.get(nodes.size()-1));}
        drawable.add(x.toLatLng());
        copy.append_node(x);
        if (validcopy) //validate proximity
        {
            while (current.distanceTo(local)>50){
                currentnode++;

                if (currentnode>=copy.getNodes().size()){
                    diffOG=local;
                    validcopy=false;
                    //TODO: CHANGE DUPE ID HERE AND OTHER INFO THAT SAYS IT IS A COPY
                    copy.setIscopy(false);
                    copy.setCourse_id(copy.getuID());

                    errorcode = "Has drifted over 50 meters from original course";
                    break;
                }
                current = copy.getNodes().get(currentnode).toLocation();
            }

        }

    }
    public boolean isValidcopy(){return validcopy;}
    public String getErrorcode(){return  errorcode;}
    public Location getDiffOG(){return  diffOG;}

    public List<LatLng> toDrawable() {
        return drawable;
    }
}
