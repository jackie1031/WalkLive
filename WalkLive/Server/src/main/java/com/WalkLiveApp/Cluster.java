package com.WalkLiveApp;

public class Cluster {

    private double centerLong,centerLat, radius;
    private int dangerLevel;
    /**
     * constructor
     * @param centerLong: the longitude of the cluster center
     * @param centerLat: the latitude of the cluster center
     * @param radius: : the radius of the cluster center
     */

    public Cluster(double centerLong, double centerLat, double radius, int dangerLevel) {
        this.centerLong = centerLong;
        this.centerLat = centerLat;
        this.radius = radius;
        this.dangerLevel = dangerLevel;
    }



    @Override
    public String toString() {
        return "Cluster{" +
                "centerLong=" + centerLong +
                ", centerLat=" + centerLat +
                ", radius=" + radius +
                ", dangerLevel=" + dangerLevel +
                '}';
    }


}
