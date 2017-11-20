package com.WalkLiveApp;

import java.util.*;

public class Trip {

    private int tripId,timePointId;
    private String userId, startTime, endTime;
    private boolean complete;
    private double destinationLat, destinationLong;
    private  List<TimePoint> allTimePoints = new ArrayList<TimePoint>();


    // test whether is in danger zone
    public List<TimePoint> getTimepoint (int trip,String user){
        TimePoint now = new TimePoint(timePointId,startTime, destinationLat, destinationLong);
        complete = false;
        while(!complete){
            timePointId = 0;
            String timeNow = startTime + 0.05;
            ////////
            TimePoint current = new TimePoint(timePointId, timeNow, destinationLat,destinationLong);
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
    public String getstartTime(){ return startTime;}
    public String getendTime(){ return endTime;}
    public boolean getcomplete(){ return complete;}
    public double destinationLat(){ return destinationLat;}
    public double destinationLong(){ return destinationLong;}

}
