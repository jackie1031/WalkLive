package com.WalkLiveApp;

import com.sun.corba.se.impl.ior.FreezableList;
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
    private DataBaseHandler dataBaseHandler = new DataBaseHandler();
    private String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private String user = "b0a1d19d87f384";
    private String password = "6d11c74b";
    private Connection conn = null;

    public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     */

    public WalkLiveService() throws UserServiceException {
        new DataBaseHandler().initializeDataBase();
//        this.dataBaseHandler.initializeDataBase();
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
        return new UserManager().createNew(body);
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException, java.text.ParseException {
        return new UserManager().findAllUsers();
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public User login(String body) throws UserServiceException, ParseException, java.text.ParseException {
        return new UserManager().login(body);
    }

    public User getUser(String username) throws UserServiceException, ParseException, java.text.ParseException {
       return new UserManager().getUser(username);
    }

    /**
     * ================================================================
     * Emergency Contact PUT
     * ================================================================
     */

    public User updateEmergencyContact(String username, String body) throws UserServiceException, ParseException, SQLException, java.text.ParseException {
        return new UserManager().updateEmergencyContact(username, body);
    }

    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

    //create a new friend request and store in database
    public void createFriendRequest(String sender, String body) throws UserServiceException, RelationshipServiceException, ParseException, SQLException, java.text.ParseException {
        new FriendsManager().createFriendRequest(sender, body);
    }

    //get my sent friend requests
    public List<Relationship> getOutgoingFriendRequests(String sender) throws RelationshipServiceException {
        return new FriendsManager().getOutgoingFriendRequests(sender);
    }

    //get my received friend requests
    public List<Relationship> getIncomingFriendRequests(String recipient) throws RelationshipServiceException {
        return new FriendsManager().getIncomingFriendRequests(recipient);
    }

    //     //respond to select sent friend request
    public void respondToFriendRequest(String responder, String requestId, String response) throws UserServiceException, RelationshipServiceException {
        new FriendsManager().respondToFriendRequest(responder, requestId, response);
    }

    public List<User> getFriendList(String username) throws UserServiceException, RelationshipServiceException, ParseException, java.text.ParseException {
        return new FriendsManager().getFriendList(username);
    }


    /**
     * For trip part -----------------------
     **/

    //stm.executeUpdate("CREATE TABLE IF NOT EXISTS Trips (tripId INT, username TEXT,start_time TEXT, end_time TEXT, danger_zone INT, " +
    //        "destination TEXT, coord_long DOUBLE,coord_lat DOUBLE,completed BOOLEAN )");


    private int countOngoing() throws InvalidDestination{
        PreparedStatement ps = null;
        ResultSet res = null;
        String sql = "SELECT * FROM ongoingTrips";

        try{
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            int size= 0;
            if (res != null)
            {
                res.beforeFirst();
                res.last();
                size = res.getRow();
                logger.info("the size of the table for ongoing is:"+size);
                return size;
            }

        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new InvalidDestination("SQL exception in counting", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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
        return -1;
    }


    private int countDone() throws InvalidDestination{
        PreparedStatement ps = null;
        ResultSet res = null;
        String sql = "SELECT * FROM doneTrips";

        try{
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            int size= 0;
            if (res != null)
            {
                res.beforeFirst();
                res.last();
                size = res.getRow();
                logger.info("the size of the table of done is:"+size);
                return size;
            }

        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new InvalidDestination("SQL exception in counting", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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
        return -1;
    }



    public Trip startTrip(String body) throws InvalidDestination, UserServiceException, ParseException {
            return new TripManager().startTrip(body);
    }


//    }

    public void endTrip(String tripIdInStr) throws InvalidDestination, InvalidTargetID, ParseException {
        //public void endTrip(String body) throws InvalidDestination, InvalidTargetID, ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;

        int tripId = Integer.parseInt(tripIdInStr);


        Boolean completed = false;

        //String sql = "SELECT * FROM Trips WHERE tripId = ? LIMIT 1";

        //INSERT INTO persons_table select * from customer_table where person_name = 'tom';



        String sql = "INSERT INTO doneTrips select * from ongoingTrips where tripId = ?";


        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            logger.info("pass here in ENDTRIP");

            //res = ps.executeQuery();
            ps.executeUpdate();
//
//            while (res.next()) { //if there is something in the response
//                //which means that we should stop the process and throw an error
//                logger.error("WalkLiveService.startTrip: Failed to create new entry ");
//                throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - duplicate username");
//
//            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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

        String sql2 = "DELETE FROM ongoingTrips where tripId = ?";
        //customer_table where person_name = 'tom';


        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, tripId);
            ps.executeUpdate();


//            while (res.next()) { //if there is something in the response
//                //which means that we should stop the process and throw an error
//                logger.error("WalkLiveService.startTrip: Failed to create new entry ");
//                throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - duplicate username");
//
//            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
            throw new InvalidDestination("WalkLiveService.startTrip: Failed to create new entry - query error", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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
    /**
     //* **URL:** /WalkLive/api/[userId]
     //        * **Content:** `{ startTime: [string], destination: [string] }`
     public Trip startTrip(String body) throws  InvalidDestination,UserServiceException, ParseException {
     **/


    public Trip getTrip(String tripIdInStr) throws UserServiceException, ParseException, InvalidTargetID {
        ResultSet res = null;
        PreparedStatement ps = null;
        //logger.info("in the get trip, the string form "+ tripIdInStr);
        //logger.info("================================");
        //logger.info("in the get trip, the int form "+ tripId);

        int tripId = Integer.parseInt(tripIdInStr);


        //find user by username
        String sql = "SELECT * FROM ongoingTrips WHERE tripId = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            res = ps.executeQuery();

            String username;
            String startTime;
            String destination;
            String timeSpent;
            String emergencyNum;
            String shareTo = null;
            double startLong;
            double startLat;
            double curLong;
            double curLat;
            double endLong;
            double endLat;
            boolean completed;


//            String setup = "users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, " +
//                    "emergency_id TEXT, emergency_number TEXT)" ;
//            pw = res.getString(2);

//            stm.executeUpdate("(tripId TEXT, username TEXT, shareTo TEXT, " +
//                  4  "destination TEXT, dangerZone INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, " +
//                  9  "startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, " +
//                  14  "emergencyNum TEXT, timeSpent TEXT)");



            if (res.next()) {
                username = res.getString(2);
                destination = res.getString(3);
                startTime = res.getString(5);
                completed = res.getBoolean(6);
                startLat = res.getDouble(7);
                startLong = res.getDouble(8);
                curLat = res.getDouble(9);
                curLong = res.getDouble(10);
                endLat = res.getDouble(11);
                endLong = res.getDouble(12);
                emergencyNum = res.getString(13);
                timeSpent = res.getString(14);


                return new Trip(tripId,username, destination, startTime, completed, startLat, startLong, curLat, curLong, endLat, endLong, emergencyNum, timeSpent);

            } else{
                logger.error(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
                throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to find tripid: %s", tripId));
            }
        } catch(SQLException ex){
            logger.error(String.format("WalkLiveService.find: Failed to query database for tripId: %s", tripId), ex);

            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", tripId), ex);
        } finally{
            //close connections
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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

    public List<Trip> getAllTrips(String username) throws  SQLException,UserServiceException, ParseException, InvalidTargetID{
        PreparedStatement ps = null;
        ResultSet res = null;
        String sender;
        String recipient;
        int tempTripId;
        String passInUserName;

        ArrayList<User> friends = new ArrayList<>();
        ArrayList<Trip> trips = new ArrayList<>();

        String sql = "SELECT * from users WHERE username = ?";
        String sql2 = "SELECT recipient FROM friends WHERE sender = ? AND relationship = 1";

        // A send to B, B can c A, A can't c B
        String sql3 = "SELECT sender FROM friends WHERE recipient = ? AND relationship = 1";
        //String sql4 = "SELECT * FROM ongoingTrips WHERE username = ? ";

        try {
            conn = DriverManager.getConnection(url, user, password);

            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            if (!res.next()) {
                logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", username));
                throw new UserServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", username));
            }
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            //throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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


        //query for accepted requests where the user was the sender
        try{
            conn = DriverManager.getConnection(url, user, password);

            ps = conn.prepareStatement(sql2);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) {
                //recipient = res.getString(1);
                passInUserName = res.getString("recipient");
                logger.info("the recipient in get friend: " + passInUserName);
                Trip t = getTripString(passInUserName);
                trips.add(t);
                //Trip t = getTrip();
                //User u = getUser(recipient);
                //String contact = u.getContact();

                //User r = new User(recipient, null, contact, null, null, null, null);
                //friends.add(r);
            }
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            //throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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
        try{
            conn = DriverManager.getConnection(url, user, password);
            //query for responded requests where the user was the recipient
            ps = conn.prepareStatement(sql3);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) {
                //sender = res.getString(1);
                passInUserName = res.getString("sender");
                logger.info("the sender in get friend: " + passInUserName);
                Trip t = getTripString(passInUserName);
                trips.add(t);
            }

            return trips;
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            //throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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
        return null;
    }

    public Trip getTripString(String username) throws SQLException{

        PreparedStatement ps = null;
        ResultSet res = null;
        String sender;
        String recipient;
        int tempTripId;


        //String username;
        String startTime;
        String destination;
        String timeSpent;
        String emergencyNum;
        String shareTo = null;
        double startLong;
        double startLat;
        double curLong;
        double curLat;
        double endLong;
        double endLat;
        boolean completed;
        int tripId;


        String sql4 = "SELECT * FROM ongoingTrips WHERE username = ? ";

        try {
            conn = DriverManager.getConnection(url, user, password);

            ps = conn.prepareStatement(sql4);
            ps.setString(1, username);
            res = ps.executeQuery();

//            String setup = "users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, " +
//                    "emergency_id TEXT, emergency_number TEXT)" ;
//            pw = res.getString(2);

//            stm.executeUpdate("(tripId TEXT, username TEXT, shareTo TEXT, " +
//                  4  "destination TEXT, dangerZone INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, " +
//                  9  "startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, " +
//                  14  "emergencyNum TEXT, timeSpent TEXT)");


            if (res.next()) {
                tripId = res.getInt("tripId");
                username = res.getString(2);
                destination = res.getString(3);
                startTime = res.getString(5);
                completed = res.getBoolean(6);
                startLat = res.getDouble(7);
                startLong = res.getDouble(8);
                curLat = res.getDouble(9);
                curLong = res.getDouble(10);
                endLat = res.getDouble(11);
                endLong = res.getDouble(12);
                emergencyNum = res.getString(13);
                timeSpent = res.getString(14);
                return new Trip(tripId, username, destination, startTime, completed, startLat, startLong, curLat, curLong, endLat, endLong, emergencyNum, timeSpent);

            }

        }catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            //throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
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

        return null;

    }


     public void updateTrip(String tripIdInStr, String body) throws InvalidTargetID,WalkLiveService.UserServiceException,ParseException {

         PreparedStatement ps = null;
         ResultSet res = null;

         int tripId = Integer.parseInt(tripIdInStr);

         JSONObject object = (JSONObject) new JSONParser().parse(body);


         //Content: {curLong:[double],curLat:[double],timeSpent:[String]}


         //should be in a try catch block in case that incorrect body is given
         String curLongStr = object.get("curLong").toString();
         String curLatStr = object.get("curLat").toString();
         String timeSpent = object.get("timeSpent").toString();

         double curLong = Double.parseDouble(curLongStr);
         double curLat = Double.parseDouble(curLatStr);


         //query to see if username given is valid
         String sql = "SELECT * FROM ongoingTrips WHERE tripId = ? LIMIT 1";

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setInt(1, tripId);
             res = ps.executeQuery();

             if (!res.next()) {
                 logger.error(String.format("WalkLiveService.updateEmergencyContact: Failed to find username: %s", tripId));
                 throw new UserServiceException(String.format("WalkLiveService.updateEmergencyContact: Failed to find username: %s", tripId));
             }

//             //autofill the contact information is the username is given )and valid_ but the contact info is not given
//             if (number.equals("")) {
//                 number = res.getString("emergency_number");
//             }

         } catch(SQLException ex) {
             logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", tripId), ex);
             throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", tripId), ex);
         } finally {
             //close connections
             if (ps != null) {
                 try {
                     ps.close();
                 } catch (SQLException e) { /* ignored */}
             }
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

//         String timeSpent = object.get("timeSpent").toString();
//         double curLong = Double.parseDouble(curLongStr);
//         double curLat = Double.parseDouble(curLatStr);

         sql = "UPDATE ongoingTrips SET curLong = ?, curLat = ?, timeSpent = ?  WHERE tripId = ? LIMIT 1" ;

         try {
             conn = DriverManager.getConnection(url, user, password);
             ps = conn.prepareStatement(sql);
             ps.setDouble(1, curLong);
             ps.setDouble(2, curLat);
             ps.setString(3, timeSpent);
             ps.setInt(4, tripId);
             ps.executeUpdate();

             System.out.println("SUCCESSFULLY UPDATED.");
             //return new User(null, null, null, null, null, id, number);

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


    //=====================EXCEPTIONS============================
    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public UserServiceException(String message) {
            super(message);
        }
    }

    public static class RelationshipServiceException extends Exception {
        public RelationshipServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public RelationshipServiceException(String message) {
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