package com.WalkLiveApp;

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
    private String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private String user = "b0a1d19d87f384";
    private String password = "6d11c74b";
    private Connection conn = null;

    private final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     */

    public WalkLiveService() throws UserServiceException {
        /**
        String createTableSql = "create table testAutoDeriveColumnNames (id_val integer primary key, another_very_exciting_value varchar(20))";
        String createTableSql = "create table testAutoDeriveColumnNames (id_val integer primary key, another_very_exciting_value varchar(20))";
    */
        Statement stm = null;
        Statement stm2 = null; //TEMP GET RID OF THIS
        String setup = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, emergency_id TEXT, emergency_number TEXT)" ;
            //String setup2 = "CREATE TABLE IF NOT EXISTS counters (friend_request_ids INT)";
        //stm.executeUpdate(setup);
      
        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            stm.executeUpdate(setup);

        } catch (SQLException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new WalkLiveService.UserServiceException("Failed to create schema at startup");

        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }


        String sql = "CREATE TABLE IF NOT EXISTS Trips(tripId TEXT, username TEXT, shareTo TEXT, destination TEXT, startTime TEXT, completed BOOL not NULL " +
                " startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)";
//dangerZone INT,
        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS Trips(tripId TEXT, username TEXT, shareTo TEXT, destination TEXT, angerZone INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)");
            //stm.executeUpdate(sql);  //+, completed BOOL
                    //"dangerZone INT ,startLat DOUBLE(16,4),startLong DOUBLE (16,4),curLat DOUBLE PRECISION,curLong DOUBLE PRECISION,endLat DOUBLE PRECISION,endLong DOUBLE PRECISION, emergencyNum Text");


        } catch (SQLException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new WalkLiveService.UserServiceException("Failed to create schema at startup");

        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
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
        PreparedStatement ps = null;
        ResultSet res;

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();
        String contact = object.get("contact").toString();
        //debugging
        System.out.println("USERNAME:" + username);

        //FIRST, check to see if username already exists in databse
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) { //if there is something in the response, means that username is already taken (401)
                logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
                throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");
            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry - query error", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - query error", ex);
        }  finally {
            //close connections
          if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        //SECOND, if username did not exist, then place the information into the database
        sql = "INSERT INTO users (username, password, contact, nickname, created_on, emergency_id, emergency_number) " +
                "             VALUES (?, ?, ?, NULL, NULL, NULL, NULL)" ;

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, pw);
            ps.setString(3, contact);
            ps.executeUpdate();

            return new User(username, pw, contact, null, null, null, null);

        } catch (SQLException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        }  finally {
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException, java.text.ParseException {
        Statement stm = null;
        ResultSet res = null;
        String sql = "SELECT * FROM users";

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            res = stm.executeQuery(sql);

            String username;
            String pw;
            String contact;
            String nickname;
            Date createdOn;
            String emergencyId;
            String emergencyNumber;

            ArrayList<User> users = new ArrayList<>();
            while (res.next()) {
                username = res.getString(1);
                pw = res.getString(2);
                contact = res.getString(3);

                nickname = res.getString(4);
                createdOn = df.parse(res.getString(5));
                emergencyId = res.getString(6);
                emergencyNumber = res.getString(7);

                User u = new User(username, pw, contact, nickname, createdOn, emergencyId, emergencyNumber);
                users.add(u);
            }
            return users;

        } catch (SQLException ex) {
            logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
        } finally {
            //close connections
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public User login(String body) throws UserServiceException, ParseException, java.text.ParseException {
        ResultSet res;
        PreparedStatement ps = null;

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();

        //FIRST, search to see if username exists in database
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            String contact;
            String nickname;
            Date createdOn;
            String emergencyId;
            String emergencyNumber;

            if (res.next()) {

                //SECOND, check password
                String targetPw = res.getString(2);
                if (!pw.equals(targetPw)) {
                    logger.error(String.format("WalkLiveService.login: Failed to authenticate - incorrect password"));
                    throw new UserServiceException(String.format("WalkLiveService.login: Failed to authenticate - incorrect password"));
                }

                //retrieve necessary information to return
                contact = res.getString(3);
                nickname = res.getString(4);
                createdOn = df.parse(res.getString(5));
                emergencyId = res.getString(6);
                emergencyNumber = res.getString(7);

                return new User(username, null, contact, null, null, emergencyId, emergencyNumber);

            } else {
                //if the response is empty, aka the username does not exist in database
                logger.error(String.format("WalkLiveService.login: Failed to find username: %s", username));
                throw new UserServiceException(String.format("WalkLiveService.login: Failed to find username: %s", username));
            }

        } catch (SQLException ex) {
            logger.error(String.format("WalkLiveService.login: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.login: Failed to query database for username: %s", username), ex);
        }  finally {
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    public User getUser(String username) throws UserServiceException, ParseException, java.text.ParseException {
        ResultSet res;
        PreparedStatement ps = null;

        //find user by username
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            String pw;
            String contact;
            String nickname;
            Date createdOn;
            String emergencyId;
            String emergencyNumber;

            if (res.next()) {
                pw = res.getString(2);
                contact = res.getString(3);
                nickname = res.getString(4);
                createdOn = df.parse(res.getString(5));
                emergencyId = res.getString(6);
                emergencyNumber = res.getString(7);

                return new User(username, pw, contact, nickname, createdOn, emergencyId, emergencyNumber);
            } else {
                logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
                throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
            }
        } catch (SQLException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);

            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", username), ex);
        } catch (java.text.ParseException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to properly parse date"), ex);
            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to properly parse date"), ex);
        } finally {
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }

    /**
     * ================================================================
     * Emergency Contact PUT
     * ================================================================
     */

    public User updateEmergencyContact(String username, String body) throws UserServiceException, ParseException, SQLException {
        PreparedStatement ps = null;
        ResultSet res = null;

        JSONObject object = (JSONObject) new JSONParser().parse(body);

        //should be in a try catch block in case that incorrect body is given
        String id = object.get("emergency_id").toString();
        String number = object.get("emergency_number").toString();

        String sql = "UPDATE users SET emergency_id = ?, emergency_number = ? WHERE username = ? LIMIT 1" ;

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, number);
            ps.setString(3, username);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY UPDATED.");
            return new User(null, null, null, null, null, id, number);

        } catch(SQLException ex) {
            logger.error("WalkLiveService.updateEmergencyContact: Failed to update emergency information", ex);
            throw new UserServiceException("WalkLiveService.updateEmergencyContact: Failed to emergency information", ex);
        }  finally {
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }


    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

     //create a new FriendRequest and store in database
     public void createFriendRequest(String sender, String body) throws FriendRequestServiceException, ParseException, SQLException {
         PreparedStatement ps = null;
         ResultSet res = null;

         JSONObject object = (JSONObject) new JSONParser().parse(body);
         String recipient = object.get("recipient").toString();

         String sql = "INSERT INTO friendRequests (sender, recipient, sent_on) VALUES (?, ?, null)" ;

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setString(1, sender);
             ps.setString(2, recipient);
             ps.executeUpdate();

             System.out.println("SUCCESSFULLY ADDED.");
         } catch(SQLException ex) {
             logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
             throw new FriendRequestServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
         }  finally {
             if (ps != null) {
                 try {
                     ps.close();
                 } catch (SQLException e) { /* ignored */}
             }
             if (conn != null) {
                 try {
                     conn.close();
                 } catch (SQLException e) { /* ignored */}
             }
         }
     }

     //get my sent friend requests
     public List<FriendRequest> getOutgoingFriendRequests(String sender) throws FriendRequestServiceException {
         //checks needed
         PreparedStatement ps = null;
         ResultSet res = null;

         String sql = "SELECT * FROM friendRequests WHERE sender = ?";

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setString(1, sender);
             res = ps.executeQuery();

             int request_id;
             String recipient;
             Date sent_on;

             ArrayList<FriendRequest> frs = new ArrayList<>();
             while (res.next()) {
                 request_id = res.getInt(1);
                 recipient = res.getString(3);
                 sent_on = (Date) res.getObject(4);

                 FriendRequest fr = new FriendRequest(request_id, sender, recipient, sent_on);
                 frs.add(fr);
             }
             return frs;
         } catch (SQLException ex) {
             logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
             throw new FriendRequestServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
         } finally {
             if (ps != null) {
                 try {
                     ps.close();
                 } catch (SQLException e) { /* ignored */}
             }
             if (conn != null) {
                 try {
                     conn.close();
                 } catch (SQLException e) { /* ignored */}
             }
         }
     }

     //delete select sent friend request (cancel request) - extended feature
     public void deleteFriendRequest(String username, String requestId) throws FriendRequestServiceException {
         //checks needed

         PreparedStatement ps = null;

         String sql = "DELETE FROM friendRequests WHERE requestId = ?" ;

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setString(1, requestId);
             ps.executeUpdate();

         } catch (SQLException ex) {
             logger.error("WalkLiveService.deleteFriendRequest: Failed to delete request", ex);
             throw new FriendRequestServiceException("WalkLiveService.deleteFriendRequest: Failed to delete request", ex);
         } finally {
             if (ps != null) {
                 try {
                     ps.close();
                 } catch (SQLException e) { /* ignored */}
             }
             if (conn != null) {
                 try {
                     conn.close();
                 } catch (SQLException e) { /* ignored */}
             }
         }
     }

     //get my received friend requests
     public List<FriendRequest> getIncomingFriendRequests(String recipient) throws FriendRequestServiceException {
         //checks needed
         PreparedStatement ps = null;
         ResultSet res = null;

         String sql = "SELECT * FROM friendRequests WHERE recipient = ?";

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setString(1, recipient);
             res = ps.executeQuery();

             int request_id;
             String sender;
             Date sent_on;

             ArrayList<FriendRequest> frs = new ArrayList<>();
             while (res.next()) {
                 request_id = res.getInt(1);
                 sender = res.getString(2);
                 sent_on = (Date) res.getObject(4);

                 FriendRequest fr = new FriendRequest(request_id, sender, recipient, sent_on);
                 frs.add(fr);
             }
             return frs;
         } catch (SQLException ex) {
             logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
             throw new FriendRequestServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
         } finally {
             if (ps != null) {
                 try {
                     ps.close();
                 } catch (SQLException e) { /* ignored */}
             }
             if (conn != null) {
                 try {
                     conn.close();
                 } catch (SQLException e) { /* ignored */}
             }
         }
     }

//     //respond to a friend request (update - should be a put) - receives in the body either "accept", or "decline"
//     //if accept, then add to friends list for both - FIGURE OUT DETAILS
//     //either way, dealt with friend requests should be deleted
//     //delete select sent friend request
     public void respondToFriendRequest(String responder, String requestId, String response) throws FriendRequestServiceException {
         //checks needed

         if (response.equals("accept")) {
             //update friends list

         } else if (response.equals("reject")) {
             //check to see if youre the recipient of the requestId

             //if not, throw error

         } else if (response.equals("cancel")) {
             //check to see if you're sender of that requestId

             //if not, throw error

         } else {
             //invalid response message. hoping that we can assume that we always get the correct response types
         }

         deleteFriendRequest(responder, requestId);
     }


    /**
     * For trip part -----------------------
     **/

    public String test() throws UserServiceException {
        String temp = "success";
        return temp;
    }

    //stm.executeUpdate("CREATE TABLE IF NOT EXISTS Trips (tripId INT, username TEXT,start_time TEXT, end_time TEXT, danger_zone INT, " +
    //        "destination TEXT, coord_long DOUBLE,coord_lat DOUBLE,completed BOOLEAN )");


    // trip id is hard listed
    public void startTrip(String body) throws InvalidDestination, UserServiceException, ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;

        //generate our trip id
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String startTime = object.get("startTime").toString();
        String destination = object.get("destination").toString();
        String startLat = object.get("startLat").toString();
        String startLong = object.get("startLong").toString();
        String curLat = object.get("curLat").toString();
        String curLong = object.get("curLong").toString();
        String endLat = object.get("endLat").toString();
        String endLong = object.get("endLong").toString();
        String emergencyNum = object.get("emergencyNum").toString();
        String timeSpent = object.get("timeSpent").toString();


        Boolean completed = false;
        String s = UUID.randomUUID().toString();
        String tripId = s.substring(0, 8);
        //去掉“-”符号

        //debugging
        System.out.println("USERNAME:" + username);
        System.out.println("TRIP-ID:" + tripId);

        String sql = "SELECT * FROM Trips WHERE tripId = ? LIMIT 1";

        //String findTripsql = "SELECT COUNT(*) FROM Trips";


        //if the query != null then username already exists. - set response code to 401 (invalid UserId)

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) { //if there is something in the response
                //which means that we should stop the process and throw an error
                logger.error("WalkLiveService.startTrip: Failed to create new entry ");
                throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - duplicate username");

            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        // Trips (tripId INT, username TEXT, destination TEXT, completed BOOLEAN )");
/**
        String sql = "CREATE TABLE IF NOT EXISTS Trips (tripId TEXT, username TEXT, shareTo TEXT, destination TEXT,  " +
                "completed BOOL, dangerZone INT, startLat DOUBLE(16,4),startLong DOUBLE (16,4),curLat DOUBLE (16,4),
            curLong DOUBLE (16,4), endLat DOUBLE PRECISION, endLong DOUBLE PRECISION, emergencyNum TEXT, timeSpent String)";
*/


        sql = "INSERT INTO Trips (tripId, username, startTime, destination, startLat, startLong, curLat, curLong, endLat, endLong, emergencyNum, timeSpent) " +
                "             VALUES (?,?, ?,?,?,?,?,?,?,?,?,?)";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, tripId);
            ps.setString(2, username);
            ps.setString(3, startTime);
            ps.setString(4, destination);
            ps.setString(5, startLat);
            ps.setString(6, startLong);
            ps.setString(7, curLat);
            ps.setString(8, curLong);
            ps.setString(9, endLat);
            ps.setString(10, endLong);
            ps.setString(11, emergencyNum);
            ps.setString(12, timeSpent);
            ps.executeUpdate();

            //Trip newTrip = new Trip(tripId, username, destination, completed);
            //return newTrip;

        } catch (SQLException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

    }


//    }

    public void endTrip(String body) throws InvalidDestination, InvalidTargetID, ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;

        //generate our trip id
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String tripId = object.get("tripId").toString();

        Boolean completed = false;
//        String s = UUID.randomUUID().toString();
//        String tripId = s.substring(0, 8);
        //去掉“-”符号

        //debugging
        System.out.println("TRIP-ID:" + tripId);

        //String sql = "SELECT * FROM Trips WHERE tripId = ? LIMIT 1";

        String sql = "update Trips set completed = 0 where tripId = ?";


        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, tripId);
            res = ps.executeQuery();

            while (res.next()) { //if there is something in the response
                //which means that we should stop the process and throw an error
                logger.error("WalkLiveService.startTrip: Failed to create new entry ");
                throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - duplicate username");

            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }


    }
    /**
     //* **URL:** /WalkLive/api/[userId]
     //        * **Content:** `{ startTime: [string], destination: [string] }`
     public Trip startTrip(String body) throws  InvalidDestination,UserServiceException, ParseException {

     **/

//    public Trip getTrip(String body) throws UserServiceException,InvalidTargetID, ParseException {
//        ResultSet res = null;
//        PreparedStatement ps = null;
//
//        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
//
//        try { //find user by username
//            conn = DriverManager.getConnection(url, user, password);
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, username);
//            res = ps.executeQuery();
//
//            String pw;
//            String contact;
//            String friendId;
//            String createdOn;
//
//            if (res.next()) {
//                pw = res.getString(2);
//                contact = res.getString(3);
//                friendId = res.getString(4);
//                createdOn = res.getString(5);
//
//                return new User(username, pw, contact);
//            }
//        } catch(SQLException ex) {
//            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
//            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
//        }  finally {
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//        }
//
//        return null;
////
//    }


//     public Trip updateDestination(String body) throws WalkLiveService.UserServiceException {

//         //{ tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }
//         Trip thisTrip = new Trip();

//         return thisTrip;
//     }

//     public String shareTrip(String body) throws WalkLiveService.UserServiceException {
//         // to another user
//         return "";

//     }

//     public String respondTripRequest(String body) throws WalkLiveService.UserServiceException {
//         return "";

//     }

//     //Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//     public Trip addTimePoint(String body) throws WalkLiveService.UserServiceException {
//         Trip addToTrip = new Trip();
//         return addToTrip;

//     }

//     public String getTimePoint(String body) throws WalkLiveService.UserServiceException {
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

//         return "";

//     }



//             public List<Crime> getCrimes(Crime from, Crime to, int timeOfDay, String table) {
//                 try (Connection conn = db.open()) {
//                     String sql = "SELECT date, address, coordinate, type FROM " + table + " WHERE "
//                             + "latitude >= :fromLat AND latitude <= :toLat AND date >= :fromDate AND "
//                             + "longitude >= :fromLng AND longitude <= :toLng AND date <= :toDate;";

//                     Query query = conn.createQuery(sql);
//                     query.addParameter("fromLat", from.getCoordinate())
//                             //.addParameter("toLat", to.getLat())
//                             //.addParameter("fromLng", from.getLng())
//                             .addParameter("toLng", to.getCoordinate())
//                             .addParameter("fromDate", from.getDate())
//                             .addParameter("toDate", to.getDate());

//                     List<Crime> results = query.executeAndFetch(Crime.class);
//                     return results;
//                 } catch (Sql2oException e) {
//                     logger.error("Failed to get crimes", e);
//                     return null;
//                 }
//             }

//             public DangerZone getDangerZone(Coordinate from, Coordinate to, String table) throws UserServiceException {
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//                 try (Connection conn = db.open()) {
//                     Crime[] z3 = getLinkIds(conn, from, to, "alarm > 2000", table);
//                     Crime[] z2 = getLinkIds(conn, from, to, "alarm <= 2000 AND alarm >1000 ", table);
//                     Crime[] z1 = getLinkIds(conn, from, to, "alarm < 1000", table);
//                     return new DangerZone(z1, z2,z3);
//                 } catch (Sql2oException e) {
//                     logger.error("Failed to fetch linkIds", e);
//                     return null;
//                 } catch (NullPointerException e) {
//                     logger.error("Null pointer, failed to fetch linkIds", e);
//                     return null;
//                 }
//             }

//             private Crime[] getLinkIds(Connection conn, Coordinate from, Coordinate to, String predicate, String table)
//             throws Sql2oException, NullPointerException {
//                 Table fromGrid = new Table(from.getLatitude(), from.getLongitude());
//                 Table toGrid = new Table(to.getLatitude(), to.getLongitude());

//                 String sqlGetAvoidLindIds = "SELECT DISTINCT linkId FROM "
//                         + table
//                         + " WHERE "
//                         + "x >= :fromX AND x <= :toX AND "
//                         + "y <= :fromY AND y >= :toY AND "
//                         + predicate + " ORDER BY alarm DESC LIMIT 20";
//                 Query queryGetAvoidLindIds = conn.createQuery(sqlGetAvoidLindIds);

//                 List<Integer> avoidLindIds = queryGetAvoidLindIds
//                         .addParameter("fromX", fromGrid.getX())
//                         .addParameter("toX", toGrid.getX())
//                         .addParameter("fromY", fromGrid.getY())
//                         .addParameter("toY", toGrid.getY())
//                         .executeAndFetch(Integer.class);

//                 int size = avoidLindIds.size();
//                 Crime[] linkIds = new Crime[size];
//                 for (int i = 0; i < size; i++) {
//                     //linkIds[i] = avoidLindIds.get(i);
//                 }
//                 return linkIds;

//             }

//     public Trip getLatestTimePoint(String body) throws WalkLiveService.UserServiceException {
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

//         Trip addToTrip = new Trip();
//         return addToTrip;
//     }


    //=====================EXCEPTIONS============================
    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public UserServiceException(String message) {
            super(message);
        }
    }

    public static class FriendRequestServiceException extends Exception {
        public FriendRequestServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public FriendRequestServiceException(String message) {
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
