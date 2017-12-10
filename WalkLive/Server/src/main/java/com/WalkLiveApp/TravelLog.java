package com.WalkLiveApp;

import java.util.*;


public class TravelLog {
    private String userId;
    private int tripCount;
    // arraylist
    private  List<Trip> tripList = new ArrayList<Trip>();

    public TravelLog(String userId, int tripCount, List<Trip> tripList) {
        this.userId = userId;
        this.tripCount = tripCount;
        this.tripList = tripList;
    }

    public Trip getTrip(String user, int count, List<Trip> trip) {

        //get specific trip
        Trip temp = new Trip(count, user);
        return temp;
    }

    public List<Trip> getTripHist() {
        return tripList;

    }

    public Trip getCurrTrip(){
        //get trip
        Trip temp = new Trip(1,"kasfa");
        return tripList.get(tripList.size()-1);
    }
}
