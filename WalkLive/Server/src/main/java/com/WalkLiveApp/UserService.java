package com.WalkLiveApp;

import com.google.gson.Gson;
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

public class UserService {
    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public UserService(DataSource dataSource) throws UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP )";
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup");
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

        String sql = "INSERT INTO user (username, password, nickname, friendId, createdOn) " +
                "             VALUES (:username, :password, :nickname, :friendId, :createdOn)" ;

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
            throw new UserServiceException("UserService.createNew: Failed to create new entry");
        }
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException {
       try (Connection conn = db.open()) {
            List<User> users = conn.createQuery("SELECT * FROM user")
                    .addColumnMapping("username", "username")
                    .addColumnMapping("createdOn", "createdOn")
                    .executeAndFetch(User.class);
            return users;
       } catch (Sql2oException ex) {
            logger.error("UserService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("UserService.findAllUsers: Failed to fetch user entries");
       }
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public String login(String body) throws UserServiceException {
        User user = new Gson().fromJson(body, User.class);

        JSONParser parser = new JSONParser();
        //Object obj = parser.parse(body);
        //JSONArray array = (JSONArray)obj;
        //String username = array.get("username");

        //String sql = "SELECT * FROM item WHERE username = :username ";

//        try (Connection conn = db.open()) {
//            return conn.createQuery(sql)
//                    .addParameter("username", username)
//                    .addColumnMapping("item_id", "id")
//                    .addColumnMapping("created_on", "createdOn")
//                    .executeAndFetchFirst(User.class);
//        } catch(Sql2oException ex) {
//            logger.error(String.format("TodoService.find: Failed to query database for username: %s", username), ex);
//            throw new UserServiceException(String.format("TodoService.find: Failed to query database for username: %s", username));
//        }

        return null;
    }

}
