//package com.WalkLiveApp;
//
//
////import sun.jvm.hotspot.code.ConstantOopReadValue;
//
//public class Crime {
//    private int date, timeOfDay,linkID;
//
//    public void setDate(int date) {
//        this.date = date;
//    }
//
//    public void setTimeOfDay(int timeOfDay) {
//        this.timeOfDay = timeOfDay;
//    }
//
//    public void setLinkID(int linkID) {
//        this.linkID = linkID;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public void setCoordinate(Coordinate coordinate) {
//        this.coordinate = coordinate;
//    }
//
//    private String address, type;
//    private Coordinate coordinate;
//
//
//    public Crime(int date, int timeOfDay, String address, String type, Coordinate coordinate, int linkID) {
//        this.date = date;
//        this.timeOfDay = timeOfDay;
//        this.address = address;
//        this.type = type;
//        this.coordinate = coordinate;
//        this.linkID = linkID;
//    }
//
//    public Crime(int date, Coordinate coordinate) {
//        this.date = date;
//        this.address = "";
//        this.type = "";
//        this.coordinate = coordinate;
//        this.linkID = 0;
//    }
//
//
//    public Crime() {
//
//    }
//
//    /**
//     * Get the date
//     * @return date
//     */
//    public int getDate() {
//        return date;
//    }
//    public int getTimeOfDay() {
//        return timeOfDay;
//    }
//    /**
//     * Get the address
//     * @return address
//     */
//    public String getAddress() {
//        return address;
//    }
//
//    /**
//     * Get the latitude
//     * @return latitude
//     */
//    public Coordinate getCoordinate(){return coordinate;}
//
//    /**
//     * Get the type
//     * @return type
//     */
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * Get the linkId
//     * @return linkId
//     */
//    public int getLinkId() {
//        return linkID;
//    }
//
//    /**
//     * Convert Crime to string.
//     * @return Crime in the form of a string
//     */
//    public String toString() {
//        return "date: " + date
//                + "time of the day: "+ timeOfDay
//                + " address: " + address
//                + " Coordinate: "+coordinate
//                + " type: " + type
//                + " linkId: " + linkID;
//    }
//}
