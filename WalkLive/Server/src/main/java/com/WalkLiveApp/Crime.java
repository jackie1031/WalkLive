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


    //clustering should be done in pre data process
    public ArrayList<Cluster> findCluster(){

        //go search for cluster in the table

        return clusters;
    }

    /**
    public Crime getDangerLeveLZone(String body) throws WalkLiveService.UserServiceException, SQLException{
        ResultSet res = null;
        PreparedStatement ps = null;
        Connection conn = null;

        //int dangerZoneId = Integer.parseInt(tripIdInStr);
        Trip trip = new Gson().fromJson(body, Trip.class);

        double longitude = trip.getCurLong();
        double latitude = trip.getCurLat();
        double tempRadius = 0.018;
        int dangerLevel = 1;
//
//        SELECT column_name(s)
//                FROM table_name
//        WHERE column_name BETWEEN value1 AND value2;

        String sql = "SELECT * FROM dangerZones (WHERE longitute BETWEEN ? AND ?) AND (WHERE latitude BETWEEN ? AND ?)";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, longitude+tempRadius);
            ps.setDouble(2, longitude-tempRadius);
            ps.setDouble(3, latitude+tempRadius);
            ps.setDouble(4, latitude-tempRadius);
            res = ps.executeQuery();
            logger.error("get the range");

            if (res.next()) {
                //CREATE TABLE IF NOT EXISTS dangerZones(cluster_id TEXT, longitute DOUBLE, latitude DOUBLE, radius DOUBLE, hour_of_day INT)";

                Cluster cluster = new Cluster(res.getInt(1), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14), res.getString(15));

                //return new Trip(tripId,res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14));
            } else{
                WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
                throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
            }
        } catch(SQLException ex){
            WalkLiveService.logger.error(String.format("WalkLiveService.find: Failed to query database for tripId: %s", tripId), ex);

            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", tripId), ex);
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

        return new Crime(dangerLevel,clusters);

    }
    **/

    /**
     *
     * @return get the trip id
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     */
    private int getNewRequestId() throws WalkLiveService.UserServiceException {
        ResultSet res = null;
        Statement stm = null;
        Connection conn = null;
        //find user by username counters (friend_request_ids INT)
        String sql = "UPDATE counters SET trip_ids = trip_ids + 1 ";
        String getValue = "SELECT trip_ids FROM counters";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            stm = conn.createStatement();
            stm.executeUpdate(sql);
            res = stm.executeQuery(getValue);

            if (res.next()) {
                return res.getInt(1);
            } else {
                //backup default
                return 0;
            }
        } catch (SQLException ex) {
            WalkLiveService.logger.error(("WalkLiveService.find: Failed to query database for count"), ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.getUser: Failed to query database for count", ex);
        } finally {
            //close connections
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

}
