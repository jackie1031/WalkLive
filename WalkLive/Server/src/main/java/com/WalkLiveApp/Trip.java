package com.WalkLiveApp;

import java.util.*;

public class Trip {


    // still needs timepoint ID?

    private int tripId, timePointId, dangerLevel;
    private String userName, destination,startTime,endTime;
    private boolean complete;
    private Coordinate currCoordinate;

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
    public TimePoint getLastTimepoint() {

        TimePoint last = allTimePoints.get(allTimePoints.size() - 1);
        return last;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTimePointId() {
        return timePointId;
    }

    public void setTimePointId(int timePointId) {
        this.timePointId = timePointId;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Coordinate getCurrCoordinate() {
        return currCoordinate;
    }

    public void setCurrCoordinate(Coordinate currCoordinate) {
        this.currCoordinate = currCoordinate;
    }

    public List<TimePoint> getAllTimePoints() {
        return allTimePoints;
    }

    public void setAllTimePoints(List<TimePoint> allTimePoints) {
        this.allTimePoints = allTimePoints;
    }

    private List<TimePoint> allTimePoints = new ArrayList<TimePoint>();



    //content:  `{ tripId: [int], userName:[string], dangerLevel: [int], startTime: [string], endTime: [string], destination: [string], coordinateLongtitude:[double],coordniteLatiture complete: [boolean] }`



}
