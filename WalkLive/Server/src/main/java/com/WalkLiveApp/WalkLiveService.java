package com.WalkLiveApp;
import com.mysql.jdbc.Driver;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.lang.*;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WalkLiveService {

    public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     */

    public WalkLiveService() throws UserServiceException {
        new DataBaseHandler().initializeDataBase();
        // 1. Register the Driver to the jbdc.driver java property
    }

    /*
    * Create a new User and add to database.
    * Should check database if already exists or not, and then if it doesnt exist then create and push into
    * the user database (with username and password information) and then have the remaining fields as null.
    * In the case that the username already exists, it should throw an exception. In the case that the
    * username or password is an invalid string, it should throw an exception. (In the case that we are logging
    * in through Facebook or Google, we should still create a new user in the case that its their first time
    * logging in, so may have to create a user simple only with the email information
    */
    public User createNew(String body) throws UserServiceException, ParseException, SQLException {
        return new UserManager().createNew(body);
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException, java.text.ParseException {
        return new UserManager().findAllUsers();
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public User login(String body) throws UserServiceException, ParseException, java.text.ParseException {
        return new UserManager().login(body);
    }

    public User getUser(String username) throws UserServiceException, ParseException, java.text.ParseException {
       return new UserManager().getUser(username);
    }

    /**
     * ================================================================
     * Emergency Contact PUT
     * ================================================================
     */

    public User updateEmergencyContact(String username, String body) throws UserServiceException, ParseException, SQLException, java.text.ParseException {
        return new UserManager().updateEmergencyContact(username, body);
    }

    /**
     * ================================================================
     * User Contact PUT
     * ================================================================
     */

    public User updateUserContact(String username, String body) throws UserServiceException, ParseException, SQLException, java.text.ParseException {
        return new UserManager().updateUserContact(username, body);
    }


    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

    //create a new friend request and store in database
    public void createFriendRequest(String sender, String body) throws UserServiceException, DuplicateException, RelationshipServiceException, ParseException, SQLException, java.text.ParseException {
        new FriendsManager().createFriendRequest(sender, body);
    }

    //get my sent friend requests
    public List<Relationship> getOutgoingFriendRequests(String sender) throws RelationshipServiceException {
        return new FriendsManager().getOutgoingFriendRequests(sender);
    }

    //get my received friend requests
    public List<Relationship> getIncomingFriendRequests(String recipient) throws RelationshipServiceException {
        return new FriendsManager().getIncomingFriendRequests(recipient);
    }

    //     //respond to select sent friend request
    public void respondToFriendRequest(String responder, String requestId, String response) throws UserServiceException, RelationshipServiceException {
        new FriendsManager().respondToFriendRequest(responder, requestId, response);
    }

    public List<User> getFriendList(String username) throws UserServiceException, RelationshipServiceException, ParseException, java.text.ParseException {
        return new FriendsManager().getFriendList(username);
    }


    /**
     * For trip part -----------------------
     **/

    /**
     * @param body: the body being passed in
     * @return trip
     * @throws InvalidDestination: the destination is invalid
     * @throws UserServiceException: invalid user
     * @throws ParseException : invalid parsing
     */
    public Trip startTrip(String body) throws InvalidDestination, UserServiceException, ParseException {
            return new TripManager().startTrip(body);
    }

    /**
     *
     * @param tripIdInStr： the trip id in string form
     * @throws InvalidDestination: the destination is invalid
     * @throws InvalidTargetID：the id of the trip is invalid
     * @throws ParseException: invalid parsing
     */
    public void endTrip(String tripIdInStr) throws InvalidDestination, InvalidTargetID, ParseException {
            new TripManager().endTrip(tripIdInStr);
    }

    /**
     *
     * @param tripIdInStr： the trip id in string form
     * @return a new trip with the given id
     * @throws UserServiceException: invalid user
     * @throws ParseException: invalid parsing
     * @throws InvalidTargetID：the id of the trip is invalid
     */

    public Trip getTripById(String tripIdInStr) throws UserServiceException, ParseException, InvalidTargetID {
        return new TripManager().getTripById(tripIdInStr);
    }

    /**
     *
     * @param username
     * @return a new trip with the given user name
     * @throws UserServiceException: invalid user
     * @throws ParseException: invalid parsing
     * @throws InvalidTargetID：the id of the trip is invalid
     */
    public Trip getTripByName(String username) throws UserServiceException, ParseException, InvalidTargetID {
        return new TripManager().getTripByName(username);
    }

    /**
     *
     * @param username
     * @return list of trips that this user can see
     * @throws SQLException: sql statement/database exception
     * @throws UserServiceException: invalid user
     * @throws ParseException: invalid parsing
     * @throws InvalidTargetID：the id of the trip is invalid
     * @throws RelationshipServiceException: the relationship of the users is invalid
     * @throws java.text.ParseException: invalid object to parse
     */
    public List<Trip> getAllTrips(String username) throws  SQLException,UserServiceException, ParseException, InvalidTargetID, RelationshipServiceException, java.text.ParseException{
        return new TripManager().getAllTrips(username);
    }

    /**
     *
     * @param username
     * @return this user's travel history
     * @throws SQLException: sql statement/database exception
     * @throws UserServiceException: invalid user
     * @throws ParseException: invalid parsing
     * @throws InvalidTargetID：the id of the trip is invalid
     * @throws RelationshipServiceException: the relationship of the users is invalid
     * @throws java.text.ParseException: invalid object to parse
     */
    public List<Trip> getTripHistory(String username) throws  SQLException,UserServiceException, ParseException, InvalidTargetID, RelationshipServiceException, java.text.ParseException{
        return new TripManager().getTripHistory(username);
    }

    /**
     *
     * @param tripIdInStr： the trip id in string form
     * @param body: the body being passed in
     * @throws InvalidTargetID：the id of the trip is invalid
     * @throws WalkLiveService.UserServiceException: invalid user in the database
     * @throws ParseException: invalid parsing
     */
     public void updateTrip(String tripIdInStr, String body) throws InvalidTargetID,WalkLiveService.UserServiceException,ParseException {
        new TripManager().updateTrip(tripIdInStr, body);
     }


    /**
     *
     * @param latitude: latitude of current position
     * @param longitude: longitude of current position
     * @return Crime: a crime object with danger level and nearby clusters
     * @throws SQLException： invalid sql statement
     * @throws WalkLiveService.UserServiceException: can't find user
     * @throws ParseException: can't parse into parameters
     * @throws InvalidTargetID: invalid trip id
     */
    //public ArrayList<Cluster> getDangerZone(String latitude, String longitude) throws SQLException,WalkLiveService.UserServiceException,ParseException,InvalidTargetID {
        public Crime getDangerZone(String latitude, String longitude, String isDay) throws SQLException,WalkLiveService.UserServiceException,ParseException,InvalidTargetID, java.text.ParseException {
        return new CrimeCalculator().getDangerLeveLZone(latitude, longitude,isDay);
    }


    //=====================EXCEPTIONS============================

    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public UserServiceException(String message) {
            super(message);
        }
    }

    public static class RelationshipServiceException extends Exception {
        public RelationshipServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public RelationshipServiceException(String message) {
            super(message);
        }
    }

    public static class DuplicateException extends Exception {
        public DuplicateException(String message, Throwable cause) {
            super(message, cause);
        }

        public DuplicateException(String message) {
            super(message);
        }
    }

    public static class InvalidDestination extends Exception {
        public InvalidDestination(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidDestination(String message) {
            super(message);
        }
    }

    public static class InvalidTargetID extends Exception {
        public InvalidTargetID(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidTargetID(String message) {
            super(message);
        }
    }


}