package com.WalkLiveApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;

// import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
// import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

// import java.io.*;
// import java.util.*;
// import java.sql.*;


public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 5000;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static ServerController controller = null;


    public static void main(String[] args) throws Exception {
        // //Check if the database file exists in the current directory. Abort if not

        port(getPort());

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");


        /****
         * NEED TO LOAD CRIME INTO TABLE!
         */
        //Create the model instance and then configure and start the web service

        try {
            WalkLiveService model = new WalkLiveService();
            controller = new ServerController(model);
            //setupCrimeDB();

            //model.test();

        } catch (WalkLiveService.UserServiceException ex) {
            logger.error("Failed to create a WalkLiveService instance. Aborting");
        }



    }


    static int getPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            logger.info("GOT PORT: " + processBuilder.environment().get("PORT"));

            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        logger.info("listening on: " + PORT);
        return 5000; //return default port if heroku-port isn't set (i.e. on localhost)
    }

//    private static void setupCrimeDB() throws FileNotFoundException,IOException,ParseException {
//        try{
//            CrimeDataHandler.updateDB();
//        } catch (IOException ex){
//            throw new IOException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//
//        } catch (ParseException ex) {
//            throw new org.json.simple.parser.ParseException(1,ex);
//
//        }
//
//    }

}


