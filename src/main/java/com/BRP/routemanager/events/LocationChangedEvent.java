package com.BRP.routemanager.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by durgesh on 5/11/16.
 */
public class LocationChangedEvent {
    private LatLng location;

    public LocationChangedEvent(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}

