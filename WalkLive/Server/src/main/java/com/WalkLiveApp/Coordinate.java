package com.WalkLiveApp;

/**
 * Coordinate in latitude and longitude.
 * Used by TimePoints,
 */
public class Coordinate {
    private double latitude, longitude;

    /**
     * Gets the latitude
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Coordinate constructor
     * @param latitude the latitude
     * @param longitude the longitude
     * @throws Exception if the coordinate is invalid
     */
    public Coordinate(double latitude, double longitude) throws Exception {
		/*
		abort if the coordinate parameter is illegal
		 */
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90 ) {
            throw new Exception("Not valid coordinate");
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Sort coordinates and expand a bit so that after the processing, we can always assume that
     * latitude of a less than latitude of b, longitude of a less than longitude of b
     * @param a set to top left coordinate
     * @param b set to bottom right coordinate
     */
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