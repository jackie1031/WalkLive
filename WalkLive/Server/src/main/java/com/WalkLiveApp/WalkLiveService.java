package com.WalkLiveApp;

import com.sun.corba.se.impl.ior.FreezableList;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.lang.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import java.util.Date;

public class WalkLiveService {
    private DataBaseHandler dataBaseHandler = new DataBaseHandler();
    private String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private String user = "b0a1d19d87f384";
    private String password = "6d11c74b";
    private Connection conn = null;

    public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     */

    public WalkLiveService() throws UserServiceException {
        new DataBaseHandler().initializeDataBase();
//        this.dataBaseHandler.initializeDataBase();
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

    //stm.executeUpdate("CREATE TABLE IF NOT EXISTS Trips (tripId INT, username TEXT,start_time TEXT, end_time TEXT, danger_zone INT, " +
    //        "destination TEXT, coord_long DOUBLE,coord_lat DOUBLE,completed BOOLEAN )");

    public Trip startTrip(String body) throws InvalidDestination, UserServiceException, ParseException {
            return new TripManager().startTrip(body);
    }


    public void endTrip(String tripIdInStr) throws InvalidDestination, InvalidTargetID, ParseException {
            new TripManager().endTrip(tripIdInStr);
    }

    public Trip getTripById(String tripIdInStr) throws UserServiceException, ParseException, InvalidTargetID {
        return new TripManager().getTripById(tripIdInStr);
    }

    public Trip getTripByName(String username) throws UserServiceException, ParseException, InvalidTargetID {
        return new TripManager().getTripByName(username);
    }

    public List<Trip> getAllTrips(String username) throws  SQLException,UserServiceException, ParseException, InvalidTargetID, RelationshipServiceException, java.text.ParseException{
        return new TripManager().getAllTrips(username);
    }

     public void updateTrip(String tripIdInStr, String body) throws InvalidTargetID,WalkLiveService.UserServiceException,ParseException {
        new TripManager().updateTrip(tripIdInStr, body);
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