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



public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 5000;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static ServerController controller = null;

    /**
     * Main class, set up heroku
     * @param args series of arguments
     * @throws Exception general exception
     */
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


    /**
     * heroku deployment
     * @return post number
     */
    static int getPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            logger.info("GOT PORT: " + processBuilder.environment().get("PORT"));

            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        logger.info("listening on: " + PORT);
        return 5000; //return default port if heroku-port isn't set (i.e. on localhost)
    }


}


