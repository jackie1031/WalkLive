package com.WalkLiveApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

//import javax.sql.DataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        //Check if the database file exists in the current directory. Abort if not
        DataSource dataSource = configureDataSource();
        if (dataSource == null) {
            System.out.printf("Could not find todo.db in the current directory (%s). Terminating\n",
                    Paths.get(".").toAbsolutePath().normalize());
            System.exit(1);
        }

        //Specify the IP address and Port at which the server should be run
        ipAddress(IP_ADDRESS);
        port(PORT);

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service
        try {
            WalkLiveService model = new WalkLiveService(dataSource);
            new ServerController(model);
        } catch (WalkLiveService.UserServiceException ex) {
            logger.error("Failed to create a WalkLiveService instance. Aborting");
        }
    }

    /**
     * Check if the database file exists in the current directory. If it does
     * create a DataSource instance for the file and return it.
     * @return javax.sql.DataSource corresponding to the todo database
     */
    private static DataSource configureDataSource() {
        Path walkLivePath = Paths.get(".", "walklive.db");
        if ( !(Files.exists(walkLivePath) )) {
            try { Files.createFile(walkLivePath); }
            catch (java.io.IOException ex) {
                logger.error("Failed to create walklive.db file in current directory. Aborting");
            }
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:walklive.db");
        return dataSource;

    }


}


