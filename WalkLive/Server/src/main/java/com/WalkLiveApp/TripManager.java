package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.ArrayList;
import java.util.List;


public class TripManager {
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
     * @return ongoing trip with the given user name
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
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
     * @throws ParseException: can't parse the given string into gson
     * @throws WalkLiveService.InvalidTargetID: invalid user or trip id
     * @throws WalkLiveService.RelationshipServiceException:the relationship of the users is invalid
     * @throws java.text.ParseException: can't parse the given string into gson
     */
    public List<Trip> getAllTrips(String username) throws  WalkLiveService.UserServiceException, ParseException, WalkLiveService.InvalidTargetID, WalkLiveService.RelationshipServiceException, java.text.ParseException {
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
     * @throws WalkLiveService.UserServiceException: invalid user (not in the database)
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
     * @throws ParseException: can't parse the given string into gson
     */
    public void updateTrip(String tripIdInStr, String body) throws WalkLiveService.InvalidTargetID, ParseException {
        int tripId = Integer.parseInt(tripIdInStr);
        Trip trip = new Gson().fromJson(body, Trip.class);
        String sql = "UPDATE ongoingTrips SET curLong = ?, curLat = ?, timeSpent = ?  WHERE tripId = ? LIMIT 1" ;
        try {
           jdbcTemplateObject.update(sql, trip.getCurLong(), trip.getCurLat(), trip.getTimeSpent(), tripId);
        } catch(Exception e) {
            WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find trip: %s", tripId));
            throw new WalkLiveService.InvalidTargetID(String.format("WalkLiveService.getTrip: Failed to find tripid: %s", tripId));
        }
    }


    /*
        Helper Functions
     */

    private Trip getTripString(String username){
        String sql = "SELECT * FROM ongoingTrips WHERE username = ? LIMIT 1";
        try {
            return RowMapper.decodeTrip(jdbcTemplateObject.queryForMap(sql, username));
            } catch (Exception e) {
            return null;
        }
    }

    private int getNewRequestId() {
        String sql = "UPDATE counters SET trip_ids = trip_ids + 1 ";
        String getValue = "SELECT trip_ids FROM counters";

        try{
            jdbcTemplateObject.update(sql);
            return (int) (jdbcTemplateObject.queryForMap(getValue).get("trip_ids"));
        } catch (Exception e) {
            return 0;
        }
    }

    private void insertTripToFinished(int tripId) throws WalkLiveService.InvalidDestination{
        String sql = "INSERT INTO doneTrips select * from ongoingTrips where tripId = ?";
        try {
            jdbcTemplateObject.update(sql, tripId);

        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new WalkLiveService.InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
        }
    }


    private void removeCompletedTripFromOngoing(int tripId) throws WalkLiveService.InvalidDestination{
        String sql = "DELETE FROM ongoingTrips where tripId = ?";
        try {
            jdbcTemplateObject.update(sql, tripId);
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.startTrip: Failed to delete entry - query error", ex);
            throw new WalkLiveService.InvalidDestination("WalkLiveService.completeTrip: Failed to delete new entry - query error", ex);
        }
    }
}
