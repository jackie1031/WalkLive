package com.WalkLiveApp;

import java.util.*;

public class Trip {


    // still needs timepoint ID?
    private int tripId, timePointId, dangerLevel;
    private String userId, destination,startTime,endTime;
    private boolean complete;
    private Coordinate currCoordinate;
    private List<TimePoint> allTimePoints = new ArrayList<TimePoint>();


    //constructor
    public Trip (){};

    // test whether is in danger zone
    public List<TimePoint> getTimepoint (int trip,String user){
        TimePoint now = new TimePoint(timePointId,startTime, endTime, currCoordinate, dangerLevel);
        complete = false;
        while(!complete){
            timePointId = 0;
            String timeNow = startTime + 5;
            ////////
            TimePoint current = new TimePoint(timePointId, timeNow, currCoordinate, dangerLevel);
            // parameters for TimePoints int TimePointID, String time, double latiture, double longiture;
            //add timepoints
            allTimePoints.add(current);
            timePointId++;
            timeNow = startTime;

//            if() {
//                complete = true;
//            }
        }

        return allTimePoints;

    }
    public TimePoint getLastTimepoint(){

        TimePoint last = allTimePoints.get(allTimePoints.size()-1);
        return last;

    }

    public int getTripId(){ return tripId;}
    public String getuserId(){ return userId;}
    public String setstartTime(){ return startTime;}
    public String  getendTime(){ return endTime;}
    public boolean getcomplete(){ return complete;}
    public Coordinate getCoordinate(){return currCoordinate;}
    public double getCoordLong(){return currCoordinate.getLongitude();}
    public double getCoordLatit(){return currCoordinate.getLatitude();}

    //content:  `{ tripId: [int], dangerLevel: [int], startTime: [string], endTime: [string], destination: [string], coordinateLongtitude:[double],coordniteLatiture complete: [boolean] }`



}
