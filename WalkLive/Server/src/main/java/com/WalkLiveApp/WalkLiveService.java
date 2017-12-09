package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.ArrayList;

import javax.sql.DataSource;
import org.json.simple.parser.JSONParser;


public class WalkLiveService {
    private String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private String user = "b0a1d19d87f384";
    private String password = "6d11c74b";
    private Connection conn = null;

    private final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     */
    public WalkLiveService() throws WalkLiveService.UserServiceException {
        /**
        String createTableSql = "create table testAutoDeriveColumnNames (id_val integer primary key, another_very_exciting_value varchar(20))";
        String createTableSql = "create table testAutoDeriveColumnNames (id_val integer primary key, another_very_exciting_value varchar(20))";
    */
        Statement stm = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP )");

        } catch (SQLException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new WalkLiveService.UserServiceException("Failed to create schema at startup");

        }  finally {
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
    public void createNew(String body) throws UserServiceException, ParseException, SQLException {
        PreparedStatement ps = null;
        ResultSet res = null;

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();
        String contact = object.get("contact").toString();

        //debugging
        System.out.println("USERNAME:" + username);

        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        //if the query != null then username already exists. - set response code to 401 (invalid UserId)

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) { //if there is something in the response
                //which means that we should stop the process and throw an error
                logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
                throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");

            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry - query error", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - query error", ex);
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

        sql = "INSERT INTO users (username, password, nickname, friendId, createdOn) " +
                "             VALUES (?, ?, ?, NULL, NULL)" ;

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, pw);
            ps.setString(3, contact);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY ADDED.");
        } catch(SQLException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
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

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException {
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
            String friendId;
            String createdOn;

            ArrayList<User> users = new ArrayList<>();
            while (res.next()) {
                 username = res.getString(1);
                 pw = res.getString(2);
                 contact = res.getString(3);
                 friendId = res.getString(4);
                 createdOn = res.getString(5);

                 User u = new User(username, pw, contact);
                 users.add(u);
            }
            return users;

        } catch (SQLException ex) {
            logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
        } finally {
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
    public String login(String body) throws UserServiceException, ParseException {
        ResultSet res = null;
        PreparedStatement ps = null;

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();

        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

//            String targetName = u.getUsername();
//            String targetPassword = u.getPassword();
//
//            //simple authentication
//            if (username.equals(targetName) && password.equals(targetPassword)) {
//                //then allow access, and create login instance
//
//                return ""; //return uri for this user aka uri: WalkLive/api/users/:username
//                //return session token
//            }

            return "";
        } catch(SQLException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
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

    public User getUser(String username) throws UserServiceException, ParseException {
        ResultSet res = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try { //find user by username
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            String pw;
            String contact;
            String friendId;
            String createdOn;

            if (res.next()) {
                pw = res.getString(2);
                contact = res.getString(3);
                friendId = res.getString(4);
                createdOn = res.getString(5);

                return new User(username, pw, contact);
            }
        } catch(SQLException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
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

        return null;
    }

    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

//     //create a new FriendRequest and store in database
//     public void createFriendRequest(String username, String body) throws WalkLiveService.FriendRequestServiceException {
//         FriendRequest fr = new Gson().fromJson(body, FriendRequest.class);
//         System.out.println(fr.toString());

//         String sql = "INSERT INTO friendRequests (sender, recipient, sent_on) " +
//                 "             VALUES (:sender, :recipient, :sent_on)" ;

//         try (Connection conn = db.open()) {
//             conn.createQuery(sql)
//                     .bind(fr)
//                     .executeUpdate();
//         } catch(Sql2oException ex) {
//             logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//             throw new FriendRequestServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//         }
//     }

//     //get my sent friend requests
//     public List<FriendRequest> getOutgoingFriendRequests(String body) throws WalkLiveService.FriendRequestServiceException {
//         //checks needed

//         try (Connection conn = db.open()) {
//             List<FriendRequest> requests = conn.createQuery("SELECT * FROM friendRequests WHERE sender = :sender")
//                     .addParameter("sender", body)
//                     .executeAndFetch(FriendRequest.class);
//             return requests;
//         } catch (Sql2oException ex) {
//             logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
//             throw new FriendRequestServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
//         }
//     }

//     //delete select sent friend request (cancel request) - extended feature
//     public void deleteFriendRequest(String username, String requestId) throws WalkLiveService.FriendRequestServiceException {
//         //checks needed

//         String sql = "DELETE FROM friendRequests WHERE requestId = :requestId" ;

//         try (Connection conn = db.open()) {
//             conn.createQuery(sql)
//                     .addParameter("requestId", requestId)
//                     .executeUpdate();
//         } catch(Sql2oException ex) {
//             logger.error("WalkLiveService.deleteFriendRequest: Failed to create new entry", ex);
//             throw new FriendRequestServiceException("WalkLiveService.deleteFriendRequest: Failed to create new entry", ex);
//         }
//     }

//     //get my received friend requests
//     public List<FriendRequest> getIncomingFriendRequests(String body) throws WalkLiveService.FriendRequestServiceException {
//         //checks needed

//         try (Connection conn = db.open()) {
//             List<FriendRequest> requests = conn.createQuery("SELECT * FROM friendRequests WHERE recipient = :recipient")
//                     .addParameter("recipient", body)
//                     .executeAndFetch(FriendRequest.class);
//             return requests;
//         } catch (Sql2oException ex) {
//             logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
//             throw new FriendRequestServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
//         }
//     }

//     //respond to a friend request (update - should be a put) - receives in the body either "accept", or "decline"
//     //if accept, then add to friends list for both - FIGURE OUT DETAILS
//     //either way, dealt with friend requests should be deleted
//     //delete select sent friend request
//     public FriendRequest respondToFriendRequest(String username, String requestId, String body) throws WalkLiveService.FriendRequestServiceException {
//         return null;
//     }


//     /**
//      * For trip part -----------------------
//      **/

//     public String test() throws UserServiceException {
//         String temp = "success";
//         return temp;
//     }

//     //* **URL:** /WalkLive/api/[userId]
//     //        * **Content:** `{ startTime: [string], destination: [string] }`
//     public Trip startTrip(String body) throws  InvalidDestination,UserServiceException, ParseException {
//         Trip temp = new Trip();
//         return temp;

//     }


// 	//Content:  `{ tripId: [int], dangerLevel: [int], startTime: [string], endTime: [string], destination: [string], coordinateLongtitude:[double],coordniteLatiture complete: [boolean] }`
//     public Trip getTrip(String body) throws UserServiceException,InvalidTargetID, ParseException {

//         JSONObject object = (JSONObject) new JSONParser().parse(body);
//         String tripID = object.get("tripId").toString();

//         String sql = "SELECT * FROM trip WHERE tripId = :tripId LIMIT 1";

//         try (Connection conn = db.open()) { //find user by username
//             Trip u = conn.createQuery(sql)
//                     .addParameter("tripId", tripID)
//                     .executeAndFetchFirst(Trip.class);
//             return u;

//         } catch(Sql2oException ex) {
//             logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", tripID), ex);
//             throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", tripID), ex);
//         }


//     }

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
