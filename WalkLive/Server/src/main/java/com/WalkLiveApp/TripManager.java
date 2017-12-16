package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;

public class TripManager {

    public Trip startTrip(String body) throws WalkLiveService.InvalidDestination, WalkLiveService.UserServiceException, ParseException {
        Connection conn = null;
        PreparedStatement ps = null;

        WalkLiveService.logger.info("the body of start trip"+ body);
        Trip trip = new Gson().fromJson(body, Trip.class);

        int tripId = countOngoing()+countDone();
        trip.setTripId(tripId);
        WalkLiveService.logger.info("the trip id is: " + tripId);


        String sql = "INSERT INTO ongoingTrips (tripId, username, destination, dangerLevel, startTime, completed, startLat , startLong , curLat ,curLong , endLat , endLong, emergencyNum, timeSpent)" +
                " VALUES (?,?,?,Null,?,completed,?,?,?,?,?,?,?,?)";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            ps.setString(2, trip.getUsername());
            ps.setString(3, trip.getDestination());
            ps.setString(4, trip.getStartTime());
            ps.setDouble(5, trip.getStartLat());
            ps.setDouble(6, trip.getStartLong());
            ps.setDouble(7, trip.getCurLat());
            ps.setDouble(8, trip.getCurLong());
            ps.setDouble(9, trip.getEndLat());
            ps.setDouble(10, trip.getEndLong());
            ps.setString(11, trip.getEmergencyNum());
            ps.setString(12, trip.getTimeSpent());
            ps.executeUpdate();

            return trip;
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }

    private int countOngoing() throws WalkLiveService.InvalidDestination {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet res = null;
        String sql = "SELECT * FROM ongoingTrips";

        try{
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            int size= 0;
            if (res != null)
            {
                res.beforeFirst();
                res.last();
                size = res.getRow();
                WalkLiveService.logger.info("the size of the table for ongoing is:"+size);
                return size;
            }

        } catch (SQLException ex) {
            WalkLiveService.logger.error("SQL exception", ex);
            throw new WalkLiveService.InvalidDestination("SQL exception in counting", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
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
        return -1;
    }


    private int countDone() throws WalkLiveService.InvalidDestination {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet res = null;
        String sql = "SELECT * FROM doneTrips";

        try{
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            int size= 0;
            if (res != null)
            {
                res.beforeFirst();
                res.last();
                size = res.getRow();
                WalkLiveService.logger.info("the size of the table of done is:"+size);
                return size;
            }

        } catch (SQLException ex) {
            WalkLiveService.logger.error("SQL exception", ex);
            throw new WalkLiveService.InvalidDestination("SQL exception in counting", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
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
        return -1;
    }
}
