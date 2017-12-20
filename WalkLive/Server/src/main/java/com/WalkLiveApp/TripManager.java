package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TripManager {
    private final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);


    /**
     *
     * @param body pass in by the front end
     * @return a started trip
     * @throws WalkLiveService.InvalidDestination: the destination is invalid
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     */

    public Trip startTrip(String body) throws WalkLiveService.InvalidDestination, WalkLiveService.UserServiceException, ParseException {
        Trip trip = new Gson().fromJson(body, Trip.class);
        int tripId = this.getNewRequestId();
        trip.setTripId(tripId);
        WalkLiveService.logger.info("the trip id is: " + tripId);

        String sql = "INSERT INTO ongoingTrips (tripId, username, destination, dangerLevel, startTime, completed, startLat , startLong , curLat ,curLong , endLat , endLong, emergencyNum, timeSpent, address)" +
                " VALUES (?,?,?,Null,?,completed,?,?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplateObject.update(sql, tripId, trip.getUsername(), trip.getDestination(), trip.getStartTime(), trip.getStartLat(), trip.getStartLong(), trip.getCurLat(), trip.getCurLong(), trip.getEndLat(), trip.getEndLong(), trip.getEmergencyNum(), trip.getTimeSpent(), trip.getAddress());
            return trip;
        } catch (Exception e) {
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry", e);
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry", e);
        }
    }

    /**
     *
     * @param tripIdInStr
     * @throws WalkLiveService.InvalidDestination: the destination is invalid
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     * @throws ParseException: can't parse the given string into gson
     */
    public void endTrip(String tripIdInStr) throws WalkLiveService.InvalidDestination, WalkLiveService.InvalidTargetID, ParseException {
        int tripId = Integer.parseInt(tripIdInStr);
        this.insertTripToFinished(tripId);
        this.removeCompletedTripFromOngoing(tripId);
    }

    /**
     *
     * @param tripIdInStr
     * @return ongoing trip with the given id
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     */
    public Trip getTripById(String tripIdInStr) throws WalkLiveService.InvalidTargetID {
        int tripId = Integer.parseInt(tripIdInStr);

        String sql = "SELECT * FROM ongoingTrips WHERE tripId = ? LIMIT 1";
        try {
        return (RowMapper.decodeTrip(this.jdbcTemplateObject.queryForMap(sql, tripId)));
        }
        catch (Exception e){
            WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
            throw new WalkLiveService.InvalidTargetID(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
        }
    }


    /**
     *
     * @param username
     * @returnongoing trip with the given user name
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     */
    public Trip getTripByName(String username) throws WalkLiveService.InvalidTargetID {
        String sql = "SELECT * FROM ongoingTrips WHERE username = ? LIMIT 1";
        try {
            return (RowMapper.decodeTrip(this.jdbcTemplateObject.queryForMap(sql, username)));
        }
        catch (Exception e){
            WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
            throw new WalkLiveService.InvalidTargetID(String.format("WalkLiveService.getUser: Failed to find tripid: %s", username));
        }
    }

    /**
     *
     * @param username
     * @return list of trips that are shared to this user
     * @throws SQLException: exception in sql statement or database
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     * @throws WalkLiveService.RelationshipServiceException:the relationship of the users is invalid
     * @throws java.text.ParseException: can't parse the given string into gson
     */
    public List<Trip> getAllTrips(String username) throws  SQLException,WalkLiveService.UserServiceException, ParseException, WalkLiveService.InvalidTargetID, WalkLiveService.RelationshipServiceException, java.text.ParseException {
        List<User> friends = new FriendsManager().getFriendList(username);
        ArrayList<Trip> trips = new ArrayList<>();

        for(User user: friends) {
            Trip trip = getTripString(user.getUsername());
            if (trip != null){
                trips.add(trip);
            }
        }
        return trips;
    }


    /**
     *
     * @param username
     * @return list of trips indicating the travel hstory of this user
     * @throws SQLException: exception in sql statement or database
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     * @throws WalkLiveService.RelationshipServiceException: the relationship of the users is invalid
     * @throws java.text.ParseException: can't parse the given string into gson
     */
    public List<Trip> getTripHistory(String username) throws  WalkLiveService.UserServiceException, java.text.ParseException {
        String sql = "SELECT * FROM doneTrips WHERE username = ?";
        try {
            return RowMapper.decodeAllTrips(jdbcTemplateObject.queryForList(sql, username));
        } catch (EmptyResultDataAccessException e){
            return new ArrayList<>();
        }
    }


    /**
     *
     * @param tripIdInStr the trip id
     * @param body the current coordinate and the time spent
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     */
    public void updateTrip(String tripIdInStr, String body) throws WalkLiveService.InvalidTargetID,WalkLiveService.UserServiceException,ParseException {
        Connection conn = null;
        PreparedStatement ps = null;

        int tripId = Integer.parseInt(tripIdInStr);

        Trip trip = new Gson().fromJson(body, Trip.class);


        String sql = "UPDATE ongoingTrips SET curLong = ?, curLat = ?, timeSpent = ?  WHERE tripId = ? LIMIT 1" ;

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, trip.getCurLong());
            ps.setDouble(2, trip.getCurLat());
            ps.setString(3, trip.getTimeSpent());
            ps.setInt(4, tripId);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY UPDATED.");
        } catch(SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateEmergencyContact: Failed to update emergency information", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateEmergencyContact: Failed to emergency information", ex);
        }  finally {
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

    /**
     *
     * @param username
     * @return the trip given the username
     * @throws SQLException: exception in sql statement or database
     */
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
                //return new Trip(res.getInt("tripId"), res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14), res.getString(15));
                Trip trip = new Trip(res.getInt("tripId"), res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14), res.getString(15),res.getInt(4));
                logger.info("the data now is: "+ trip.getDangerLevel());
                return trip;
                //return new Trip(res.getInt("tripId"), res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14), res.getString(15),res.getInt(4));

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

    /**
     *
     * @param tripId to indicate the trip
     * @throws WalkLiveService.InvalidDestination: the destination is invalid
     */
    private void insertTripToFinished(int tripId) throws WalkLiveService.InvalidDestination{
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;

        String sql = "INSERT INTO doneTrips select * from ongoingTrips where tripId = ?";


        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            //WalkLiveService.logger.info("pass here in ENDTRIP");
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

    /**
     *
     * @param tripId to indicate the trip
     * @throws WalkLiveService.InvalidDestination: the destination is invalid
     */
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
