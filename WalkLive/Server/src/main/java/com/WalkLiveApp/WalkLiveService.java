package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;

public class WalkLiveService {
    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public WalkLiveService(DataSource dataSource) throws UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS user (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP )";
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup", ex);
        }
    }

//    //HELPER FUNCTIONS
//    public void validateId(String id) throws UserServiceException {
//        if (id.length() < 8) {
//            throw new UserServiceException();
//        }
//    }
//
//    private void usernameDoesNotExist(String id) throws UserServiceException {
//        if (collection.find({ "username": id })){
//            throw new UserServiceException();
//        }
//    }

    /*
    * Create a new User and add to database.
    * Should check database if already exists or not, and then if it doesnt exist then create and push into
    * the user database (with username and password information) and then have the remaining fields as null.
    * In the case that the username already exists, it should throw an exception. In the case that the
    * username or password is an invalid string, it should throw an exception. (In the case that we are logging
    * in through Facebook or Google, we should still create a new user in the case that its their first time
    * logging in, so may have to create a user simple only with the email information
    */
    public void createNew(String body) throws UserServiceException, ParseException, Sql2oException {
        User user = new Gson().fromJson(body, User.class);

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();

        System.out.println("USERNAME:" + username);

        String sql = "SELECT * FROM user WHERE username = :username LIMIT 1";
        //if the query != null then username already exists. - set response code to 401 (invalid UserId)

        try (Connection conn = db.open()) {

            List<User> found = conn.createQuery(sql)
                    .addParameter("username", username)
                    .executeAndFetch(User.class);

            if (!found.isEmpty()) { //the fact that something exists here means that the username exists
                //which means that we should stop the process and throw an error
                logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
                throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");
            }

        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry - query error", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - query error", ex);
        }
//        catch (UserServiceException e) {
//            logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username, return 401 error", e);
//            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username, return 401 error");
//        }


        sql = "INSERT INTO user (username, password, nickname, friendId, createdOn) " +
                "             VALUES (:username, :password, :nickname, :friendId, :createdOn)" ;

//NEED TO DO THIS===================================================
//        try {
//            if (username.length() < 8) {
//                throw new UserServiceException();
//            }
//        } catch (UserServiceException e) {
//            logger.error(String.format("WalkLiveService.createNew: username too short: %s", username), e);
//            throw new UserServiceException("WalkLiveService.createNew: username too short: " + username, e); //call it this for now
//        }


//
//        try(usernameDoesNotExist(username)) {
//            //worked
//        } catch (UserServiceException e) {
//            logger.error(String.format("WalkLiveService.createNew: username already exists: %s", username), e);
//            throw new InvalidUsernameException("WalkLiveService.createNew: username already exists: %s", username), e);
//        }

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(user)
                    .executeUpdate();

            System.out.println("SUCCESSFULLY ADDED.");
        } catch(Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        }
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException {
       try (Connection conn = db.open()) {
            List<User> users = conn.createQuery("SELECT * FROM user")
                    .executeAndFetch(User.class);
            return users;
       } catch (Sql2oException ex) {
            logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
       }
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public String login(String body) throws UserServiceException, ParseException {
        User user = new Gson().fromJson(body, User.class);

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String password = object.get("password").toString();

        String sql = "SELECT * FROM user WHERE username = :username LIMIT 1";

        try (Connection conn = db.open()) {
            User u = conn.createQuery(sql)
                    .addParameter("username", username)
                    .executeAndFetchFirst(User.class);
            String targetName = u.getUsername();
            String targetPassword = u.getPassword();

            //simple authentication
            if (username.equals(targetName) && password.equals(targetPassword)) {
                //then allow access, and create login instance

                return ""; //return uri for this user aka uri: WalkLive/api/users/:username
                //return session token
            }
        } catch(Sql2oException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
        }
        return "";
    }


    /** For trip part -----------------------**/

    public String startTrip(String body) throws UserServiceException {

        return "";

    }

    public Trip getTrip(String body) throws UserServiceException {
        //process body
        Trip thisTrip = new Trip();

        return thisTrip;

    }
    public Trip updateDestination(String body) throws UserServiceException {

        //{ tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }
        Trip thisTrip = new Trip();

        return thisTrip;
    }
    public String shareTrip(String body) throws UserServiceException {

        // to another user
        return "";

    }
    public String respondTripRequest(String body) throws UserServiceException {
        return "";


    }

    //Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
    public Trip addTimePoint(String body) throws UserServiceException {
        Trip addToTrip = new Trip();
        return addToTrip;

    }

    public String getTimePoint(String body) throws UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        return "";

    }
    public Trip getLatestTimePoint(String body) throws UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        Trip addToTrip = new Trip();
        return addToTrip;
    }

    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public UserServiceException(String message) {
            super(message);
        }
    }

}
