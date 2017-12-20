package com.WalkLiveApp;


//cluster model which stores the center long & lat, radius, and danger level

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


    /**
     * override tostring
     * @return string
     */
    @Override
    public String toString() {
        return "Cluster{" +
                "centerLong=" + centerLong +
                ", centerLat=" + centerLat +
                ", radius=" + radius +
                ", dangerLevel=" + dangerLevel +
                '}';
    }

    /**
     * get the danger level
     * @return int danger level
     */
    public int getDangerLevel() {
        return this.dangerLevel;
    }


}
