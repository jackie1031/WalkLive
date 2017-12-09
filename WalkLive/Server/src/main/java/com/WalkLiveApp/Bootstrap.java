package com.WalkLiveApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

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

    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    //static final Connection con = null;
    //static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB";


    /** now use the db.properties file*/
    //static String url = "jdbc:mysql://localhost:3306/testdb";
    //static final String USER = "root";
    //static final String PASS = "123456";

    public static void main(String[] args) throws Exception {
        // //Check if the database file exists in the current directory. Abort if not
        // DataSource dataSource = configureDataSource();
        // //System.out.println(dataSource);

        // if (dataSource == null) {
        //     System.out.printf("Could not find walklive.db in the current directory (%s). Terminating\n",
        //             Paths.get(".").toAbsolutePath().normalize());
        //     System.exit(1);
        // }

        //Specify the IP address and Port at which the server should be run
        /** comment out this line, and then HEROKU works!**/

        //ipAddress(IP_ADDRESS);

        //port(PORT);

        port(getPort());

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");


        //Create the model instance and then configure and start the web service

        try {
            WalkLiveService model = new WalkLiveService();
            controller = new ServerController(model);
            //model.test();

        } catch (WalkLiveService.UserServiceException ex) {
            logger.error("Failed to create a WalkLiveService instance. Aborting");
        }


        //get("/hello", (req, res) -> "Hello Heroku World");


    }

//    public static void main(String[] args) {
//        port(getHerokuAssignedPort());
//        get("/hello", (req, res) -> "Hello Heroku World");
//    }


    static int getPort() {
       ProcessBuilder processBuilder = new ProcessBuilder();
       if (processBuilder.environment().get("PORT") != null) {
           logger.info("GOT PORT: " + processBuilder.environment().get("PORT"));

           return Integer.parseInt(processBuilder.environment().get("PORT"));
       }
        logger.info("listening on: " + PORT);
        return 5000; //return default port if heroku-port isn't set (i.e. on localhost)
    }

//     /**
//      * Check if the database file exists in the current directory. If it does
//      * create a DataSource instance for the file and return it.
//      * @return javax.sql.DataSource corresponding to the todo database
//      */
//     private static DataSource configureDataSource() {
//         Connection con = null;
//         PreparedStatement pst = null;
//         ResultSet rs = null;

//         Path walkLivePath = Paths.get(".", "walklive.db");
//         //Path walkLivePath = Paths.get("2017-group-14/Walklive/Server/walklive.db");

//         System.out.println(walkLivePath);


//         if ( !(Files.exists(walkLivePath) )) {
//             try { Files.createFile(walkLivePath); }
//             catch (java.io.IOException ex) {
//                 logger.error("config data source error: Failed to create walklive.db file in current directory. Aborting");
//             }
//         }
//         try {
//             MysqlDataSource ds = getMySQLDataSource();
//             con = ds.getConnection();
//             logger.error("checkpoint 1");

//             pst = con.prepareStatement("SELECT * FROM users");
//             rs = pst.executeQuery();
//             logger.error("checkpoint 2");

// //            while (rs.next()) {
// //
// ////                System.out.print(rs.getInt(1));
// //                System.out.print(": ");
// ////                System.out.println(rs.getString(2));
// //            }

//             return ds;
//         } catch (FileNotFoundException fnf ) {
//             logger.error("Failed to found file");
//         }catch (IOException e ) {
//             logger.error("IO exception");
//         }catch (SQLException e ) {
//             logger.error("SQL exception");
//         }


//         //SQLiteDataSource dataSource = new SQLiteDataSource();
//         //dataSource.setUrl("jdbc:sqlite:walklive.db");


//         return null;

//     }

//     private static MysqlDataSource getMySQLDataSource() throws FileNotFoundException, IOException {

//         Properties props = new Properties();
//         FileInputStream fis = null;
//         MysqlDataSource ds = null;

//         fis = new FileInputStream("src/main/resources/db.properties");
//         props.load(fis);

//         ds = new MysqlConnectionPoolDataSource();
//         ds.setURL(props.getProperty("mysql.url"));
//         ds.setUser(props.getProperty("mysql.username"));
//         ds.setPassword(props.getProperty("mysql.password"));

//         return ds;
//     }


}


