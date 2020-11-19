package com.example.icm_projeto1_93179_93391.ui;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface CompareMapUpdater {
    void updateMapPath(List<LatLng> x);

    void initMapPath(LatLng latLng);

    void onUpdateError(String errorstring, Location spot);


}
