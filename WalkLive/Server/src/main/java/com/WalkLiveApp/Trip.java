package com.WalkLiveApp;

import java.util.*;

public class Trip {

    private String username, destination, timeSpent, emergencyNum, startTime, shareTo, address;
    private double startLat,startLong,curLat,curLong,endLat,endLong;
    private boolean completed;
    //private Coordinate currCoordinate;

    private int tripId,timePointId, dangerLevel;

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

    public Trip (String username, String destination, String startTime, boolean completed, double startLat,
                 double startLong, double curLat, double curLong, double endLat, double endLong, String emergencyNum, String timeSpent, String address){
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
        this.address = address;
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



    public Trip (int tripId, String username, String destination, String startTime, boolean completed, double startLat,
                 double startLong, double curLat, double curLong, double endLat, double endLong, String emergencyNum, String timeSpent, String address){

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
        this.address = address;


    }

    public int getTripId() {
        return tripId;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }


    public String getUsername() {
        return username;
    }


    public String getTimeSpent() {
        return timeSpent;
    }


    public String getEmergencyNum() {
        return emergencyNum;
    }


    public double getStartLat() {
        return startLat;
    }


    public double getStartLong() {
        return startLong;
    }


    public String getStartTime() {
        return startTime;
    }

    public double getCurLat() {
        return curLat;
    }

    public double getCurLong() {
        return curLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setTripId(int tripId){ this.tripId = tripId;}

    public String getDestination() {
        return destination;
    }

    public String getAddress() {
        return address;
    }


}
