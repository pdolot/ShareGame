package com.example.patryk.sharegame2.Objects;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private BitmapDescriptor icon;

    private int id;

    public MyClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyClusterItem(double lat, double lng, BitmapDescriptor icon, int id) {
        mPosition = new LatLng(lat, lng);
        this.icon = icon;
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }
}
