package com.WalkLiveApp;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017/");
    private MongoClient mongoClient = new MongoClient(connectionString);

    private MongoDatabase db;
    private MongoCollection<Document> collection;

    private final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public UserService() throws UserServiceException {
        db = mongoClient.getDatabase("walklive");
        collection = db.getCollection("users");
    }

    /*
    * Should check database if already exists or not, and then if it doesnt exist then create and push into
    * the user database (with username and password information) and then have the remaining fields as null.
    * In the case that the username already exists, it should throw an exception. In the case that the
    * username or password is an invalid string, it should throw an exception. (In the case that we are logging
    * in through Facebook or Google, we should still create a new user in the case that its their first time
    * logging in, so may have to create a user simple only with the email information
    */
    public void createNew(String body) throws UserServiceException {
        //User user = new Gson().fromJson(body, User.class);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONArray array = (JSONArray)obj;
        String username = array.get("username");
        //String password = array.get("password");

        try (validateId(username)) {
            //worked
        } catch (UserServiceException e) {
            logger.error(String.format("UserService.createNew: username too short: %s", username), e);
            throw new InvalidUsernameException("UserService.createNew: username too short: %s", username), e);
        }

        try(usernameDoesNotExist(username)) {

        } catch (UserServiceException e) {
            logger.error(String.format("UserService.createNew: username already exists: %s", username), e);
            throw new InvalidUsernameException("UserService.createNew: username already exists: %s", username), e);
        }


        this.collection.insertOne(user);
    }

    public void login(String body) throws UserServiceException {
        //User user = new Gson().fromJson(body, User.class);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONArray array = (JSONArray)obj;
        String username = array.get("username");
    }

//    public List<User> findAll() {
//
//    }

    //HELPER FUNCTIONS
    public boolean validateId(String id) {
        if (id.length() < 8) {
            return false;
        }

        return true;
    }

    public boolean usernameDoesNotExist(String id) {
        if (collection.find({ "username": id })) {
            return false;
        }

        return true;
    }

}
