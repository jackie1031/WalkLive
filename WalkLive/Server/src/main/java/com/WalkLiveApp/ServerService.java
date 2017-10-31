package com.WalkLiveApp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;

public class ServerService {
    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(ServerService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public ServerService(DataSource dataSource) throws UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException();
        }
    }

    //HELPER FUNCTIONS
//    private void validateId(String id) throws UserServiceException {
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
    public void createNew(String body) throws UserServiceException {
        User user = new Gson().fromJson(body, User.class);
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse(body);
//        JSONArray array = (JSONArray)obj;
//        String username = array.get("username");

        String sql = "INSERT INTO item (username, nickname, friendId, created_on) " +
                "             VALUES (:username, :nickname, :friendId, :createdOn)" ;

//        try (validateId(username)) {
//            //worked
//        } catch (UserServiceException e) {
//            logger.error(String.format("UserService.createNew: username too short: %s", username), e);
//            throw new InvalidUsernameException("UserService.createNew: username too short: %s", username), e);
//        }
//
//        try(usernameDoesNotExist(username)) {
//            //worked
//        } catch (UserServiceException e) {
//            logger.error(String.format("UserService.createNew: username already exists: %s", username), e);
//            throw new InvalidUsernameException("UserService.createNew: username already exists: %s", username), e);
//        }

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(user)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("UserService.createNew: Failed to create new entry", ex);
            throw new UserServiceException();

            //throw new UserServiceException("UserService.createNew: Failed to create new entry");
        }
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public String login(String body) throws UserServiceException {
//        //User user = new Gson().fromJson(body, User.class);
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse(body);
//        JSONArray array = (JSONArray)obj;
//        String username = array.get("username");
        return "";
    }

//    public List<User> findAll() {
//
//    }

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

}
