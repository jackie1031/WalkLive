package com.WalkLiveApp;

import java.util.*;

public class Trip {

    private int tripId,timePointId;
    private String userId, startTime, endTime;
    private boolean complete;
    private double destinationLat, destinationLong;
    private  List<TimePoints> allTimePoints = new ArrayList<TimePoints>();



    public List<TimePoints> getTimepoint (int trip,String user){
        TimePoints now = new TimePoints(timePointId,startTime, destinationLat, destinationLong);
        complete = false;
        while(!complete){
            timePointId = 0;
            String timeNow = startTime + 0.05;
            ////////
            TimePoints current = new TimePoints(timePointId, timeNow, destinationLat,destinationLong);
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
    public TimePoints getLastTimepoint(){

        TimePoints last = allTimePoints.get(allTimePoints.size()-1);
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
