package com.example.icm_projeto1_93179_93391;

import android.location.Location;
import android.os.AsyncTask;

import com.example.icm_projeto1_93179_93391.datamodel.CourseComparison;
import com.example.icm_projeto1_93179_93391.ui.CompareMapUpdater;
import com.google.android.gms.maps.model.LatLng;


import java.util.List;

public class CourseCompareTask extends AsyncTask<Location,Void, List<LatLng>> {
    CourseComparison comp;
    CompareMapUpdater listener;

    public CourseCompareTask(CourseComparison cmp,CompareMapUpdater upd){
        comp = cmp;
        listener = upd;
    }


    @Override
    protected List<LatLng> doInBackground(Location... locations) {
        comp.appendNode(locations[0]);
        return comp.toDrawable();
    }

    @Override
    protected void onPostExecute(List<LatLng> latLngs) {
        if (latLngs.size()==1) listener.initMapPath(latLngs.get(0));
        else{
            listener.updateMapPath(latLngs);
        }
        if (!comp.isValidcopy()){ //listener should be the one who makes sure he doesnt do this twice
            listener.onUpdateError(comp.getErrorcode(),comp.getDiffOG());
        }
    }
}
