package com.WalkLiveApp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;



public class Crime {
    private int date, timeOfDay,crimeId, dangerLevel;
    private double longitude, latitude;
    private String address, type;
    private ArrayList<Cluster> clusters= new ArrayList<Cluster>();
    private final Logger logger = LoggerFactory.getLogger(Crime.class);

//
//
//    public void setDate(int date) {
//        this.date = date;
//    }
//
//    public void setTimeOfDay(int timeOfDay) {
//        this.timeOfDay = timeOfDay;
//    }
//
//    public void setcrimeId(int crimeId) {
//        this.crimeId = crimeId;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }



//    public Crime(int date, int timeOfDay, String address, String type, double longitude, double latitude, int crimeId) {
//        this.date = date;
//        this.timeOfDay = timeOfDay;
//        this.address = address;
//        this.type = type;
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.crimeId = crimeId;
//    }

//    public Crime(int date,double longitude, double latitude) {
//        this.date = date;
//        this.longitude = longitude;
//        this.latitude = latitude;
//    }


    public Crime(int dangerLevel, ArrayList<Cluster> clusters ) {
        this.dangerLevel = dangerLevel;
        this.clusters = clusters;

    }

    public Crime(ArrayList<Cluster> clusters ) {
        this.clusters = clusters;

    }

    public Crime(){}

    /**
     * Get the date
     * @return date
     */
    public int getDate() {
        return date;
    }
    public int getTimeOfDay() {
        return timeOfDay;
    }
    /**
     * Get the address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the crimeId
     * @return crimeId
     */
    public int getcrimeId() {
        return crimeId;
    }

    /**
     * Convert Crime to string.
     * @return Crime in the form of a string
     */
    public String toString() {
        return "date: " + date
                + "time of the day: "+ timeOfDay
                + " address: " + address
                //+ " Coordinate: "+coordinate
                + " type: " + type
                + " crimeId: " + crimeId;
    }



    public ArrayList<Cluster> getDangerLeveLZone(String latitudeStr, String longitudeStr) throws WalkLiveService.UserServiceException, SQLException{
        ResultSet res = null;
        PreparedStatement ps = null;
        Connection conn = null;

        //int dangerZoneId = Integer.parseInt(tripIdInStr);
        //Trip trip = new Gson().fromJson(body, Trip.class);

        //int clusterIdInt = this.getNewRequestId();
        //String clusterId = String.valueOf(clusterIdInt);

        double longitude = Double.parseDouble(longitudeStr);
        double latitude = Double.parseDouble(latitudeStr);
        logger.info("long: "+longitude +" lat: "+ latitude);

        double longRadius = 0.018;
        double latRadius = 0.14449;
        int dangerLevel = 1;

        //String sql = "SELECT * FROM dangerZones WHERE (longitude BETWEEN ? AND ?) AND (latitude BETWEEN ? AND ?)";
        String sql = "SELECT * FROM dangerZones WHERE (longitude < ? AND longitude > ?) AND (latitude < ? AND latitude > ?)";
        //String sql2 = "SELECT * FROM dangerZones WHERE (latitude < ? AND longitude > ?)";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, longitude+longRadius);
            ps.setDouble(2, longitude-longRadius);
            ps.setDouble(3, latitude+latRadius);
            ps.setDouble(4, latitude-latRadius);
            res = ps.executeQuery();

            ArrayList<Cluster> clusters = new ArrayList<>();

            while (res.next()) {
                //logger.info("got here");

                //CREATE TABLE IF NOT EXISTS dangerZones(cluster_id TEXT, longitute DOUBLE, latitude DOUBLE, radius DOUBLE, hour_of_day INT)";

                Cluster cluster = new Cluster(res.getDouble(2), res.getDouble(3), res.getDouble(4));
                clusters.add(cluster);
                logger.info("the final cluster is: "+ cluster.toString());

            }
            //logger.info("the final cluster is: "+ clusters);

            return clusters;

        } catch(SQLException ex){
            WalkLiveService.logger.error(String.format("WalkLiveService.find: Failed to query database for get danger level"), ex);

            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for get danger level"), ex);
        } finally{
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { }
            }
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) { }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }

    }



}
