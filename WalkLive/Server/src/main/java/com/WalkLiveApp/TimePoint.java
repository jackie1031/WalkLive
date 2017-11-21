package com.WalkLiveApp;

import java.util.*;

public class TimePoint {

    //add trip id ?
    private int TimePointID;
    private String time;
    private Coordinate thisCoordinate;
    private int dangerLevel;


    /**
     * Constructor
     */
    public TimePoint(int TimePointID, String time, Coordinate thisCoordinate, int dangerLevel) {
        this.TimePointID = TimePointID;
        this.time = time;
        this.thisCoordinate = thisCoordinate;
        this.dangerLevel = dangerLevel;

    }


    /**
     * get time points based on TimePointID
     */
    public TimePoint getTimePoint(int TimePointID) {
        //find the timePoint among the array or table?

        String time = "a"; //find in table
        //Coordinate thisCoordinate = new Coordinate(1.2,1.2);
        int dangerLevel = 4;

        TimePoint temp = new TimePoint( TimePointID, time, thisCoordinate, dangerLevel);
        return temp;
    }


    /**
     * get time points based on TimePointID
     * what to pass in ?
     */
    public int getDangerLevel (Coordinate findDanger) {
        int a = 5;
        return a;
    }


}
