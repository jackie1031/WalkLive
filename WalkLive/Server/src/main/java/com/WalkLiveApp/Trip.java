package com.WalkLiveApp;

/**
 * trip model
 */
public class Trip {

    private String username, address;
    private String destination;
    private String timeSpent;
    private String emergencyNum;
    private String startTime;
    private double startLat,startLong,curLat,curLong,endLat,endLong;
    private boolean completed;
    private int tripId,dangerLevel;

    /**
     * constructor
     * @param username: string username
     * @param destination: string destination
     * @param startTime: string destination
     * @param completed: boolean destination
     * @param startLat: double destination
     * @param startLong: double destination
     * @param curLat: double destination
     * @param curLong: double destination
     * @param endLat: double destination
     * @param endLong: double destination
     * @param emergencyNum: string destination
     * @param timeSpent: string destination
     * @param address: string destination
     */
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

    /**
     * constructor
     * @param tripId: int trip id
     * @param username: string username
     * @param destination: string destination
     * @param startTime: string destination
     * @param completed: boolean destination
     * @param startLat: double destination
     * @param startLong: double destination
     * @param curLat: double destination
     * @param curLong: double destination
     * @param endLat: double destination
     * @param endLong: double destination
     * @param emergencyNum: string destination
     * @param timeSpent: string destination
     * @param address: string destination
     */

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

    /**
     * get username
     * @return string username
     */
    public String getUsername() {
        return username;
    }

    /**
     * get timeSpent
     * @return string timeSpent
     */
    public String getTimeSpent() {
        return timeSpent;
    }

    /**
     * get emergencyNum
     * @return string emergencyNum
     */
    public String getEmergencyNum() {
        return emergencyNum;
    }

    /**
     * get startLat
     * @return double startLat
     */
    public double getStartLat() {
        return startLat;
    }

    /**
     * get startLong
     * @return double startLong
     */
    public double getStartLong() {
        return startLong;
    }

    /**
     * get startTime
     * @return string startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * get curLat
     * @return string curLat
     */
    public double getCurLat() {
        return curLat;
    }

    /**
     * get curLong
     * @return double curLong
     */
    public double getCurLong() {
        return curLong;
    }

    /**
     * get endLat
     * @return double endLat
     */
    public double getEndLat() {
        return endLat;
    }

    /**
     * get endLong
     * @return double endLong
     */
    public double getEndLong() {
        return endLong;
    }

    /**
     * set tripId
     */
    public void setTripId(int tripId){ this.tripId = tripId;}

    /**
     * get destination
     * @return string destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * get address
     * @return string address
     */
    public String getAddress() {
        return address;
    }


}
