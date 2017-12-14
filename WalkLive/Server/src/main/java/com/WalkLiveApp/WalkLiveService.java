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
    private DataBaseHandler dataBaseHandler = new DataBaseHandler();
    private String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private String user = "b0a1d19d87f384";
    private String password = "6d11c74b";
    private Connection conn = null;

    public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
     * returns emergencyId and emergencyNumber
     */
    public User login(String body) throws UserServiceException, ParseException, java.text.ParseException {
        ResultSet res = null;
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

    public User getUser(String username) throws UserServiceException, ParseException, java.text.ParseException {
        ResultSet res = null;
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

        //query to see if username given is valid
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            if (!res.next()) {
                logger.error(String.format("WalkLiveService.updateEmergencyContact: Failed to find username: %s", username));
                throw new UserServiceException(String.format("WalkLiveService.updateEmergencyContact: Failed to find username: %s", username));
            }

            //autofill the contact information is the username is given )and valid_ but the contact info is not given
            if (number.equals("")) {
                number = res.getString("emergency_number");
            }

        } catch(SQLException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", username), ex);
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

        sql = "UPDATE users SET emergency_id = ?, emergency_number = ? WHERE username = ? LIMIT 1" ;

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
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

    //create a new friend request and store in database
    public void createFriendRequest(String sender, String body) throws UserServiceException, RelationshipServiceException, ParseException, SQLException, java.text.ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;

        User sUser = null;
        User rUser = null;

        //first check if the sender exists at all
        try {
            sUser = getUser(sender);
        } catch (UserServiceException e) {
            logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", sender));
            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", sender));
        }

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String recipient = object.get("recipient").toString();
        int request_id = getNewRequestId();

        //second check if the recipient exists at all
        try {
            rUser = getUser(recipient);
        } catch (UserServiceException e) {
            logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", recipient));
            throw new UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", recipient));
        }

        //Third make sure that sender and recipient are not the same
        if (rUser.getUsername().equals(sender)) {
            logger.error("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
            throw new RelationshipServiceException("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
        }

        //fourth see if there already is a friend request made from either s ro r or r to s already, it shouldnt work.


        String sql = "INSERT INTO friends (_id, sender, recipient, relationship, sent_on) VALUES (?, ?, ?, 0, null)" ;

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, request_id);
            ps.setString(2, sender);
            ps.setString(3, recipient);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY ADDED.");
        } catch (SQLException ex) {
            logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
            throw new RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
        }  finally {
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

    //get my sent friend requests
    public List<Relationship> getOutgoingFriendRequests(String sender) throws RelationshipServiceException {
        //checks needed
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql = "SELECT * FROM friends WHERE sender = ?";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, sender);
            res = ps.executeQuery();

            int request_id;
            String recipient;
            int relationship;
            Date sent_on;

            ArrayList<Relationship> rs = new ArrayList<>();
            while (res.next()) {
                request_id = res.getInt(1);
                recipient = res.getString(3);
                relationship = res.getInt(4);
                sent_on = (Date) res.getObject(5);

                Relationship r = new Relationship(request_id, sender, recipient, relationship, sent_on);
                rs.add(r);
            }
            return rs;
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
            throw new RelationshipServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
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

    //get my received friend requests
    public List<Relationship> getIncomingFriendRequests(String recipient) throws RelationshipServiceException {
        //checks needed
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql = "SELECT * FROM friends WHERE recipient = ? AND relationship = 0 ";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, recipient);
            res = ps.executeQuery();

            int request_id;
            String sender;
            Date sent_on;
            int relationship;

            ArrayList<Relationship> rs = new ArrayList<>();
            while (res.next()) {
                request_id = res.getInt(1);
                sender = res.getString(2);
                relationship = res.getInt(4);
                sent_on = (Date) res.getObject(5);

                Relationship r = new Relationship(request_id, sender, recipient, relationship, sent_on);
                rs.add(r);
            }
            return rs;
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
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

    //     //respond to select sent friend request
    public void respondToFriendRequest(String responder, String requestId, String response) throws UserServiceException, RelationshipServiceException {
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        String sql2 = "SELECT * FROM friends WHERE _id = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);

            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setString(1, responder);
            res = ps.executeQuery();

            if (!res.next()) {
                logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", responder));
                throw new UserServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", responder));
            }

            //check if the requestid exists, and if the responder is qualified to respond to the request (check if it is the recipient)
            ps = conn.prepareStatement(sql2);
            ps.setString(1, requestId);
            res = ps.executeQuery();

            if (!res.next()) {
                logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %d", requestId));
                throw new RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %d", requestId));
            }

            String recipient = res.getString(3);
            if (!responder.equals(recipient)) {
                logger.error(String.format("WalkLiveService.respondToFriendRequest: User %s is unauthorized to respond to request %s", responder, requestId));
                throw new RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest:  User %s is unauthorized to respond to request %s", responder, requestId));
            }

        } catch (SQLException ex) {
            logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
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

        //now that all checks are done, update the actual relationship
        if (response.equals("accept")) {
            updateRelationship(requestId, 1);
        } else if (response.equals("reject")) {
            updateRelationship(requestId, 2);
        } else {
            //invalid response message. hoping that we can assume that we always get the correct response types
        }
    }

    //helper methods
    public int getNewRequestId() throws RelationshipServiceException {
        ResultSet res = null;
        Statement stm = null;

        //find user by username counters (friend_request_ids INT)
        String sql = "UPDATE counters SET friend_request_ids = friend_request_ids + 1 ";
        String getValue = "SELECT friend_request_ids FROM counters";

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            stm.executeUpdate(sql);
            res = stm.executeQuery(getValue);

            if (res.next()) {
                return res.getInt(1);
            } else {
                //backup default
                return 0;
            }
        } catch (SQLException ex) {
            logger.error(("WalkLiveService.find: Failed to query database for count"), ex);
            throw new RelationshipServiceException(("WalkLiveService.getUser: Failed to query database for count"), ex);
        } finally {
            //close connections
            if (stm != null) {
                try {
                    stm.close();
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

//    //delete select sent friend request but dont need this for now (EXTENDED FEATURE: CANCEL FRIEND REQUEST)
//    public void deleteFriendRequest(String username, String requestId) throws RelationshipServiceException {
//        //checks needed
//
//        PreparedStatement ps = null;
//
//        String sql = "DELETE FROM friendRequests WHERE requestId = ?" ;
//
//        try {
//            conn = DriverManager.getConnection(url, user, password);
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, requestId);
//            ps.executeUpdate();
//
//        } catch (SQLException ex) {
//            logger.error("WalkLiveService.deleteFriendRequest: Failed to delete request", ex);
//            throw new RelationshipServiceException("WalkLiveService.deleteFriendRequest: Failed to delete request", ex);
//        } finally {
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
//    }


    public void updateRelationship(String requestId, int response) throws RelationshipServiceException {
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql = "UPDATE friends SET relationship = ? WHERE _id = ?";

        try {
            conn = DriverManager.getConnection(url, user, password);

            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setInt(1, response);
            ps.setString(2, requestId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
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

//    public List<User> getFriendList(String username) throws UserServiceException, RelationshipServiceException, ParseException, java.text.ParseException {
//        PreparedStatement ps = null;
//        ResultSet res = null;
//
//        String sql = "SELECT * from users WHERE username = ?";
//        String sql2 = "SELECT recipient FROM friends WHERE sender = ? AND relationship = 1";
//
//
//        // A send to B, B can c A, A can't c B
//        String sql3 = "SELECT sender FROM friends WHERE recipient = ? AND relationship = 1";
//
//        try {
//            conn = DriverManager.getConnection(url, user, password);
//
//            //check if username exists
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, username);
//            res = ps.executeQuery();
//
//            if (!res.next()) {
//                logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", username));
//                throw new UserServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find responder username: %s", username));
//            }
//
//            String sender;
//            String recipient;
//
//            //query for accepted requests where the user was the sender
//
//            ps = conn.prepareStatement(sql2);
//            ps.setString(1, username);
//            res = ps.executeQuery();
//
//            ArrayList<User> friends = new ArrayList<>();
//            while (res.next()) {
//                //recipient = res.getString(1);
//                recipient = res.getString("recipient");
//
//                User u = getUser(recipient);
//                String contact = u.getContact();
//
//                User r = new User(recipient, null, contact, null, null, null, null);
//                friends.add(r);
//            }
//
//            //query for responded requests where the user was the recipient
//            ps = conn.prepareStatement(sql3);
//            ps.setString(1, username);
//            res = ps.executeQuery();
//
//            while (res.next()) {
//                //sender = res.getString(1);
//                sender = res.getString("sender");
//
//                User u = getUser(sender);
//                String contact = u.getContact();
//
//                User r = new User(sender, null, contact, null, null, null, null);
//                friends.add(r);
//            }
//
//            return friends;
//        } catch (SQLException ex) {
//            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
//            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
//        } finally {
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//            if (res != null) {
//                try {
//                    res.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//        }
//    }


    public List<User> getFriendList(String username) throws UserServiceException, RelationshipServiceException, ParseException, java.text.ParseException {
        PreparedStatement ps = null;
        ResultSet res = null;
        String sender;
        String recipient;
        int tempTripId;

        ArrayList<User> friends = new ArrayList<>();
        ArrayList<Trip> trips = new ArrayList<>();

        String sql = "SELECT * from users WHERE username = ?";
        String sql2 = "SELECT recipient FROM friends WHERE sender = ? AND relationship = 1";

        String sql3 = "SELECT sender FROM friends WHERE recipient = ? AND relationship = 1";

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
            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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
                recipient = res.getString("recipient");

                User u = getUser(recipient);
                String contact = u.getContact();

                User r = new User(recipient, null, contact, null, null, null, null);
                friends.add(r);
            }
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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
                sender = res.getString("sender");

                User u = getUser(sender);
                String contact = u.getContact();

                User r = new User(sender, null, contact, null, null, null, null);
                friends.add(r);
            }

            return friends;
        } catch (SQLException ex) {
            logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            throw new RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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
     * For trip part -----------------------
     **/

    public String test() throws UserServiceException {
        String temp = "success";
        return temp;
    }

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


        PreparedStatement ps = null;
        ResultSet res = null;

        logger.info("the body of start trip"+ body);

        int test;
        //Trips(tripId TEXT, username TEXT, shareTo TEXT, destination TEXT, dangerZone INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)");

        //generate our trip id
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String startTime = object.get("startTime").toString();
        String destination = object.get("destination").toString();
        String startLat1 = object.get("startLat").toString();
        String startLong1 = object.get("startLong").toString();
        String curLat1 = object.get("curLat").toString();
        String curLong1 = object.get("curLong").toString();
        String endLat1 = object.get("endLat").toString();
        String endLong1 = object.get("endLong").toString();
        String emergencyNum = object.get("emergencyNum").toString();
        String timeSpent = object.get("timeSpent").toString();
        String shareTo;
        int dangerLevel;


        double startLat = Double.parseDouble(startLat1);
        double startLong = Double.parseDouble(startLong1);
        double curLat = Double.parseDouble(curLat1);
        double curLong = Double.parseDouble(curLong1);
        double endLat = Double.parseDouble(endLat1);
        double endLong = Double.parseDouble(endLong1);

        Boolean completed = false;



        int tripId = countOngoing()+countDone();

        logger.info("the trip id is: " + tripId);


        String sql = "SELECT * FROM ongoingTrips WHERE tripId = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tripId);
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

        // Trips (tripId INT, username TEXT, destination TEXT, completed BOOLEAN )");


//        executeUpdate("CREATE TABLE IF NOT EXISTS Trips(tripId TEXT, username TEXT, shareTo TEXT, destination TEXT, " +
//                "dangerLevel INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, " +
//                "curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)");


        sql = "INSERT INTO ongoingTrips (tripId, username, destination, dangerLevel, startTime, completed, startLat , startLong , curLat ,curLong , endLat , endLong, emergencyNum, timeSpent)" +
                " VALUES (?,?,?,Null,?,completed,?,?,?,?,?,?,?,?)";

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = this.conn.prepareStatement(sql);
            ps.setInt(1, tripId);
            ps.setString(2, username);
            ps.setString(3, destination);
            ps.setString(4, startTime);
            ps.setDouble(5, startLat);
            ps.setDouble(6, startLong);
            ps.setDouble(7, curLat);
            ps.setDouble(8, curLong);
            ps.setDouble(9, endLat);
            ps.setDouble(10, endLong);
            ps.setString(11, emergencyNum);
            ps.setString(12, timeSpent);
            ps.executeUpdate();

          return new Trip(tripId,username, destination, startTime, completed, startLat, startLong, curLat,curLong, endLat, endLong, emergencyNum,timeSpent);

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
//
//     public String shareTrip(String body) throws WalkLiveService.UserServiceException {
//         // to another user
//         return "";
//
//     }
//
//     public String respondTripRequest(String body) throws WalkLiveService.UserServiceException {
//         return "";
//
//     }
//
//     //Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//     public Trip addTimePoint(String body) throws WalkLiveService.UserServiceException {
//         Trip addToTrip = new Trip();
//         return addToTrip;
//
//     }
//
//     public String getTimePoint(String body) throws WalkLiveService.UserServiceException {
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//
//         return "";
//
//     }
//
//
//
//             public List<Crime> getCrimes(Crime from, Crime to, int timeOfDay, String table) {
//                 try (Connection conn = db.open()) {
//                     String sql = "SELECT date, address, coordinate, type FROM " + table + " WHERE "
//                             + "latitude >= :fromLat AND latitude <= :toLat AND date >= :fromDate AND "
//                             + "longitude >= :fromLng AND longitude <= :toLng AND date <= :toDate;";
//
//                     Query query = conn.createQuery(sql);
//                     query.addParameter("fromLat", from.getCoordinate())
//                             //.addParameter("toLat", to.getLat())
//                             //.addParameter("fromLng", from.getLng())
//                             .addParameter("toLng", to.getCoordinate())
//                             .addParameter("fromDate", from.getDate())
//                             .addParameter("toDate", to.getDate());
//
//                     List<Crime> results = query.executeAndFetch(Crime.class);
//                     return results;
//                 } catch (Sql2oException e) {
//                     logger.error("Failed to get crimes", e);
//                     return null;
//                 }
//             }
//
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
//
//             private Crime[] getLinkIds(Connection conn, Coordinate from, Coordinate to, String predicate, String table)
//             throws Sql2oException, NullPointerException {
//                 Table fromGrid = new Table(from.getLatitude(), from.getLongitude());
//                 Table toGrid = new Table(to.getLatitude(), to.getLongitude());
//
//                 String sqlGetAvoidLindIds = "SELECT DISTINCT linkId FROM "
//                         + table
//                         + " WHERE "
//                         + "x >= :fromX AND x <= :toX AND "
//                         + "y <= :fromY AND y >= :toY AND "
//                         + predicate + " ORDER BY alarm DESC LIMIT 20";
//                 Query queryGetAvoidLindIds = conn.createQuery(sqlGetAvoidLindIds);
//
//                 List<Integer> avoidLindIds = queryGetAvoidLindIds
//                         .addParameter("fromX", fromGrid.getX())
//                         .addParameter("toX", toGrid.getX())
//                         .addParameter("fromY", fromGrid.getY())
//                         .addParameter("toY", toGrid.getY())
//                         .executeAndFetch(Integer.class);
//
//                 int size = avoidLindIds.size();
//                 Crime[] linkIds = new Crime[size];
//                 for (int i = 0; i < size; i++) {
//                     //linkIds[i] = avoidLindIds.get(i);
//                 }
//                 return linkIds;
//
//             }
//
//     public Trip getLatestTimePoint(String body) throws WalkLiveService.UserServiceException {
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//
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