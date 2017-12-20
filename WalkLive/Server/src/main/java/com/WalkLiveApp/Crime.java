package com.WalkLiveApp;
import java.util.*;



public class Crime {
    private int dangerLevel;
    private List<Cluster> clusters= new ArrayList<Cluster>();

    /**
     * constructor
     * @param dangerLevel: process danger level
     * @param clusters: array of clusters
     */
    public Crime(int dangerLevel, List<Cluster> clusters ) {
        this.dangerLevel = dangerLevel;
        this.clusters = clusters;

    }

    /**
     * Convert Crime to string.
     * @return Crime in the form of a string
     */
    public String toString() {
        return "danger level: " + dangerLevel
                + " list of clusters "+ clusters;
    }

}
