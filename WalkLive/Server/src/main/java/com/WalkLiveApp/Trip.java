package com.WalkLiveApp;

import java.util.*;

public class Trip {

    private int tripId,timePointId, dangerlevel, startTime, endTime;
    private String userId;
    private boolean complete;
    private Coordinate currCoordinate;
    private  List<TimePoint> allTimePoints = new ArrayList<TimePoint>();
    public Trip (){};

    // test whether is in danger zone
    public List<TimePoint> getTimepoint (int trip,String user){
        TimePoint now = new TimePoint(timePointId,startTime, currCoordinate, dangerlevel);
        complete = false;
        while(!complete){
            timePointId = 0;
            int timeNow = startTime + 5;
            ////////
            TimePoint current = new TimePoint(timePointId, timeNow, currCoordinate, dangerlevel);
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
    public int getstartTime(){ return startTime;}
    public int getendTime(){ return endTime;}
    public boolean getcomplete(){ return complete;}
    public Coordinate getCoordinate(){return currCoordinate;}

}
