package com.WalkLiveApp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import javax.sql.DataSource;

<<<<<<< Updated upstream:WalkLive/Server/src/main/java/com/WalkLiveApp/UserService.java
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
            String sql = "CREATE TABLE IF NOT EXISTS item (item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "                                 title TEXT, done BOOLEAN, created_on TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup");
        }
=======
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

public class ServerService {
    private MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017/");
    private MongoClient mongoClient = new MongoClient(connectionString);

    private MongoDatabase db;
    private MongoCollection<Document> collection;

    private final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public ServerService() throws UserServiceException {
        db = mongoClient.getDatabase("walklive");
        collection = db.getCollection("users");
>>>>>>> Stashed changes:WalkLive/Server/src/main/java/com/WalkLiveApp/ServerService.java
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
<<<<<<< Updated upstream:WalkLive/Server/src/main/java/com/WalkLiveApp/UserService.java
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
            throw new UserServiceException("UserService.createNew: Failed to create new entry");
=======
        //User user = new Gson().fromJson(body, User.class);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONArray array = (JSONArray)obj;
        String username = array.get("username");
        //String password = array.get("password");

        try (validateId(username)) {
            //worked
        } catch (UserServiceException e) {
            logger.error(String.format("ServerService.createNew: username too short: %s", username), e);
            throw new InvalidUsernameException("ServerService.createNew: username too short: %s", username), e);
        }

        try(usernameDoesNotExist(username)) {

        } catch (UserServiceException e) {
            logger.error(String.format("ServerService.createNew: username already exists: %s", username), e);
            throw new InvalidUsernameException("ServerService.createNew: username already exists: %s", username), e);
>>>>>>> Stashed changes:WalkLive/Server/src/main/java/com/WalkLiveApp/ServerService.java
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

}
