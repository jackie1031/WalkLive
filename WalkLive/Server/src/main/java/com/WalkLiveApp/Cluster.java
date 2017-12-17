package com.WalkLiveApp;

public class Cluster {

    private double centerLong,centerLat, radius;



    public Cluster(double centerLong, double centerLat, double radius) {
        this.centerLong = centerLong;
        this.centerLat = centerLat;
        this.radius = radius;
    }


    public double getCenterLong() {
        return centerLong;
    }

    public void setCenterLong(double centerLong) {
        this.centerLong = centerLong;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }




}
