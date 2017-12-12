package com.WalkLiveApp;

import java.util.*;

public class Trip {

    private String username, destination, timeSpent, emergencyNum, startTime, shareTo;
    private double startLat,startLong,curLat,curLong,endLat,endLong;
    private boolean completed;
    private Coordinate currCoordinate;

    private int tripId,timePointId, dangerLevel;


    //constructor
    public Trip (int tripId, String username){

        this.tripId =  tripId;
        this.username =  username;

    }

    public Trip (int tripId, String username, String destination, boolean completed){
        this.tripId =  tripId;
        this.username =  username;
        this.destination =  destination;
        this.completed =  completed;
    }

    public Trip (String username, String destination, String startTime, boolean completed, double startLat,
                 double startLong, double curLat, double curLong, double endLat, double endLong, String emergencyNum, String timeSpent){
        this.username =  username;
        this.destination =  destination;
        this.startTime =  startTime;
        this.completed =  completed;
        this.startLat =  startLat;
        this.startLong =  startLong;
        this.curLat =  curLat;
        this.curLong =  curLong;
        this.endLat =  endLat;
        this.endLong =  endLong;
        this.emergencyNum =  emergencyNum;
        this.timeSpent =  timeSpent;


    }

    public Trip (int tripId, String username, String destination, String startTime, boolean completed, double startLat,
                 double startLong, double curLat, double curLong, double endLat, double endLong, String emergencyNum, String timeSpent){

        this.tripId =  tripId;
        this.username =  username;
        this.destination =  destination;
        this.startTime =  startTime;
        this.completed =  completed;
        this.startLat =  startLat;
        this.startLong =  startLong;
        this.curLat =  curLat;
        this.curLong =  curLong;
        this.endLat =  endLat;
        this.endLong =  endLong;
        this.emergencyNum =  emergencyNum;
        this.timeSpent =  timeSpent;


    }

    // still needs timepoint ID?


    public int getTripId() {
        return tripId;
    }


    public String getShareTo() {
        return shareTo;
    }

    public void setShareTo(String shareTo) {
        this.shareTo = shareTo;
    }

    public String getDestination() {
        return destination;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }


    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDestination(String usedestinationrname) {
        this.destination = destination;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getEmergencyNum() {
        return emergencyNum;
    }

    public void setEmergencyNum(String emergencyNum) {
        this.emergencyNum = emergencyNum;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }



    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public double getCurLat() {
        return curLat;
    }

    public void setCurLat(double curLat) {
        this.curLat = curLat;
    }

    public double getCurLong() {
        return curLong;
    }

    public void setCurLong(double curLong) {
        this.curLong = curLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }





//    // test whether is in danger zone
//    public List<TimePoint> getTimepoint (String trip,String user){
//        TimePoint now = new TimePoint(timePointId,startTime, endTime, currCoordinate, dangerLevel);
//        complete = false;
//        while(!complete){
//            timePointId = 0;
//            String timeNow = startTime + 5;
//            ////////
//            TimePoint current = new TimePoint(timePointId, timeNow, currCoordinate, dangerLevel);
//            // parameters for TimePoints int TimePointID, String time, double latiture, double longiture;
//            //add timepoints
//            allTimePoints.add(current);
//            timePointId++;
//            timeNow = startTime;
//
////            if() {
////                complete = true;
////            }
//        }
//
//        return allTimePoints;
//
//    }
//    public TimePoint getLastTimepoint() {
//
//        TimePoint last = allTimePoints.get(allTimePoints.size() - 1);
//        return last;
//    }



//    public List<TimePoint> getAllTimePoints() {
//        return allTimePoints;
//    }
//
//    public void setAllTimePoints(List<TimePoint> allTimePoints) {
//        this.allTimePoints = allTimePoints;
//    }
//
//    private List<TimePoint> allTimePoints = new ArrayList<TimePoint>();



    //content:  `{ tripId: [int], userName:[string], dangerLevel: [int], startTime: [string], endTime: [string], destination: [string], coordinateLongtitude:[double],coordniteLatiture complete: [boolean] }`



}
