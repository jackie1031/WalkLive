package com.WalkLiveApp;

/**
 * Coordinate in latitude and longitude.
 * Used by TimePoints.
 */
public class Coordinate {
    private double latitude, longitude;


    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }



    public Coordinate(double latitude, double longitude) throws Exception {
		//check whether the coordinate is valid
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90 ) {
            throw new Exception("Not valid coordinate");
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }


    public static void sortAndExpand(Coordinate a, Coordinate b) {
        if (a.latitude < b.latitude) {
            a.latitude -= 0.01;
            b.latitude += 0.01;
        } else {
            double temp = a.latitude;
            a.latitude = b.latitude - 0.01;
            b.latitude = temp + 0.01;
        }
        if (a.longitude < b.longitude) {
            a.longitude -= 0.01 / Math.cos(Math.toRadians(a.latitude));
            b.longitude += 0.01 / Math.cos(Math.toRadians(b.latitude));
        } else {
            double temp = a.longitude;
            a.longitude = b.longitude - 0.01 / Math.cos(Math.toRadians(a.latitude));
            b.longitude = temp + 0.01 / Math.cos(Math.toRadians(b.latitude));
        }
    }
}