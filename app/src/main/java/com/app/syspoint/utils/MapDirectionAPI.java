package com.app.syspoint.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.app.syspoint.MainActivity;
import com.app.syspoint.utils.gmap.Directions;
import com.app.syspoint.utils.gmap.GMapDirection;
import com.app.syspoint.utils.gmap.directions.Leg;
import com.app.syspoint.utils.gmap.directions.Route;
import com.app.syspoint.utils.gmap.directions.Step;

import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapDirectionAPI {

    public static Call getDirection(LatLng pickUp, LatLng destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build();

        return client.newCall(request);
    }

    public static Call getAddress(LatLng address) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng="+address.latitude+","+address.longitude+"&key="+ MainActivity.apikey)
                .build();

        return client.newCall(request);
    }

    public static Call getDirectionVia(LatLng pickUp, LatLng... destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrlVia(GMapDirection.MODE_DRIVING, false, pickUp, destination))
                .build();

        return client.newCall(request);
    }

    public static long getDistance(Context context, String json) {
        long dist = 0;
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        dist += step.getDistance().getValue();
                    }
                }
            }

            if (routes.size() == 0) return -1L;

        }
        return dist;
    }

    public static String getTimeDistance(Context context, String json) {
        String time = "0 mins";
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes = null;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    time = leg.getDuration().getText();

                }
            }
        }
        return time;
    }

}
