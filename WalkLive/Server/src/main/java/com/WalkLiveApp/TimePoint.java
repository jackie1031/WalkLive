package com.WalkLiveApp;

import java.util.*;

public class TimePoint {

    //add trip id ?
    private int TimePointID;
    private String startTime, endTime, currTime;
    private Coordinate thisCoordinate;
    private int dangerLevel;


    /**
     * Constructor
     */
    public TimePoint(int TimePointID, String startTime, String endTime, Coordinate thisCoordinate, int dangerLevel) {
        this.TimePointID = TimePointID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.thisCoordinate = thisCoordinate;
        this.dangerLevel = dangerLevel;

    }

    public TimePoint(int TimePointID, String currTime, Coordinate thisCoordinate, int dangerLevel) {
        this.TimePointID = TimePointID;
        this.currTime = currTime;
        this.thisCoordinate = thisCoordinate;
        this.dangerLevel = dangerLevel;

    }


    /**
     * get time points based on TimePointID
     */
    public TimePoint getTimePoint(int TimePointID) {
        //find the timePoint among the array or table?

        //String time = "a"; //find in table
        //Coordinate thisCoordinate = new Coordinate(1.2,1.2);
        int dangerLevel = 4;

        TimePoint temp = new TimePoint(12, startTime, endTime, thisCoordinate, dangerLevel);
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
