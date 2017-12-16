package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripManager {

    public Trip startTrip(String body) throws WalkLiveService.InvalidDestination, WalkLiveService.UserServiceException, ParseException {
        Connection conn = null;
        PreparedStatement ps = null;

        WalkLiveService.logger.info("the body of start trip"+ body);
        Trip trip = new Gson().fromJson(body, Trip.class);

        int tripId = this.getNewRequestId();
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

    public void endTrip(String tripIdInStr) throws WalkLiveService.InvalidDestination, WalkLiveService.InvalidTargetID, ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;

        int tripId = Integer.parseInt(tripIdInStr);
        this.insertTripToFinished(tripId);
        this.removeCompletedTripFromOngoing(tripId);
    }

    public Trip getTrip(String tripIdInStr) throws WalkLiveService.UserServiceException, ParseException, WalkLiveService.InvalidTargetID {
        ResultSet res = null;
        PreparedStatement ps = null;
        Connection conn = null;

        int tripId = Integer.parseInt(tripIdInStr);

        String sql = "SELECT * FROM ongoingTrips WHERE tripId = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            res = ps.executeQuery();
            if (res.next()) {
                return new Trip(tripId,res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14));

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

    public List<Trip> getAllTrips(String username) throws  SQLException,WalkLiveService.UserServiceException, ParseException, WalkLiveService.InvalidTargetID, WalkLiveService.RelationshipServiceException, java.text.ParseException {
        List<User> friends = new FriendsManager().getFriendList(username);
        ArrayList<Trip> trips = new ArrayList<>();

        for(User user: friends) {
            trips.add(getTripString(user.getUsername()));
        }
        return trips;
    }

    private Trip getTripString(String username) throws SQLException{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql4 = "SELECT * FROM ongoingTrips WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);

            ps = conn.prepareStatement(sql4);
            ps.setString(1, username);
            res = ps.executeQuery();

            if (res.next()) {
                return new Trip(res.getInt("tripId"), res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14));

            }

        }catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            //throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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
        return null;
    }


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

    private void insertTripToFinished(int tripId) throws WalkLiveService.InvalidDestination{
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;

        String sql = "INSERT INTO doneTrips select * from ongoingTrips where tripId = ?";


        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            WalkLiveService.logger.info("pass here in ENDTRIP");
            ps.executeUpdate();

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new WalkLiveService.InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
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

    private void removeCompletedTripFromOngoing(int tripId) throws WalkLiveService.InvalidDestination{
        PreparedStatement ps = null;
        Connection conn = null;


        String sql2 = "DELETE FROM ongoingTrips where tripId = ?";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, tripId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new WalkLiveService.InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
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
}
