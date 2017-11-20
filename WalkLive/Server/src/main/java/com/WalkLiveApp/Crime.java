package com.WalkLiveApp;

public class Crime {
    private int date, time,linkid;
    private String address, type;
    private double latitude, longitude;


    public Crime(int date, int time, String address, String type, double latitude, double longitude, int linkid) {
        this.date = date;
        this.time = time;
        this.address = address;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.linkid = linkid;
    }

    public Crime(int date, double latitude, double longitude) {
        this.date = date;
        this.address = "";
        this.type = "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.linkid = 0;
    }

    /**
     * Get the date
     * @return date
     */
    public int getDate() {
        return date;
    }

    /**
     * Get the address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the latitude
     * @return latitude
     */
    public double getLat() {
        return latitude;
    }

    /**
     * Get the longitude
     * @return longitude
     */
    public double getLng() {
        return longitude;
    }

    /**
     * Get the type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the linkId
     * @return linkId
     */
    public int getLinkId() {
        return linkid;
    }

    /**
     * Convert Crime to string.
     * @return Crime in the form of a string
     */
    public String toString() {
        return "date: " + date
                + " address: " + address
                + " latitude: " + latitude
                + " longitutude: " + longitude
                + " type: " + type
                + " linkId: " + linkid;
    }
}
