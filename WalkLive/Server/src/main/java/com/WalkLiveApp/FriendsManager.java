package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsManager {
    public void createFriendRequest(String sender, String body) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, WalkLiveService.DuplicateException, ParseException, SQLException, java.text.ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String recipient = object.get("recipient").toString();
        int request_id = getNewRequestId();

        UserManager.isValid(sender);
        User rUser = new UserManager().getUser(recipient);

        //Third make sure that sender and recipient are not the same
        if (rUser.getUsername().equals(sender)) {
            WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
        }

        //fourth see if there already is a friend request made from either s ro r or r to s already, it shouldnt work.
        checkDuplicateRequest(sender, recipient);

        this.createNewFriendRequest(sender, recipient, request_id);
    }

    public List<Relationship> getOutgoingFriendRequests(String sender) throws WalkLiveService.RelationshipServiceException {
        //checks needed
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;
        String sql = "SELECT * FROM friends WHERE sender = ?";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, sender);
            res = ps.executeQuery();

            ArrayList<Relationship> rs = new ArrayList<>();
            while (res.next()) {
                rs.add(new Relationship(res.getInt(1), sender, res.getString(3), res.getInt(4), (Date) res.getObject(5)));
            }
            return rs;
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
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

    public List<Relationship> getIncomingFriendRequests(String recipient) throws WalkLiveService.RelationshipServiceException {
        //checks needed
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;

        String sql = "SELECT * FROM friends WHERE recipient = ? AND relationship = 0 ";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, recipient);
            res = ps.executeQuery();

            ArrayList<Relationship> rs = new ArrayList<>();
            while (res.next()) {
                rs.add(new Relationship(res.getInt(1), res.getString(2), recipient, res.getInt(4), (Date) res.getObject(5)));
            }
            return rs;
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
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

    public void respondToFriendRequest(String responder, String requestId, String response) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException {
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;
        String sql2 = "SELECT * FROM friends WHERE _id = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql2);
            ps.setString(1, requestId);
            res = ps.executeQuery();

            if (!res.next()) {
                WalkLiveService.logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %s", requestId));
                throw new WalkLiveService.RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %s", requestId));
            }

            String recipient = res.getString(3);
            if (!responder.equals(recipient)) {
                WalkLiveService.logger.error(String.format("WalkLiveService.respondToFriendRequest: User %s is unauthorized to respond to request %s", responder, requestId));
                throw new WalkLiveService.RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest:  User %s is unauthorized to respond to request %s", responder, requestId));
            }

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
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
            updateRelationship(requestId);
        } else if (response.equals("reject")) {
            deleteRelationship(requestId);
        } else {
            //invalid response message. hoping that we can assume that we always get the correct response types
        }
    }

    public List<User> getFriendList(String username) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, ParseException, java.text.ParseException {
        ArrayList<User> friends = new ArrayList<>();
        this.addFriendsToList(username, friends, "recipient");
        this.addFriendsToList(username, friends, "sender");
        return friends;
    }

    private void addFriendsToList(String username, ArrayList<User> friends, String tableType) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, ParseException, java.text.ParseException{
        PreparedStatement ps = null;
        ResultSet res = null;
        Connection conn = null;

        String sql = "SELECT " + tableType + " FROM friends WHERE " + this.getCounter(tableType) + " = ? AND relationship = 1";

        UserManager userManager = new UserManager();

        try{
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            while (res.next()) {
                String recipient = res.getString(tableType);
                User r = new User(recipient, null, userManager.getUser(recipient).getContact(), null, null, null, null);
                friends.add(r);
            }
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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


    private int getNewRequestId() throws WalkLiveService.RelationshipServiceException {
        ResultSet res = null;
        Statement stm = null;
        Connection conn = null;
        //find user by username counters (friend_request_ids INT)
        String sql = "UPDATE counters SET friend_request_ids = friend_request_ids + 1 ";
        String getValue = "SELECT friend_request_ids FROM counters";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
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
            WalkLiveService.logger.error(("WalkLiveService.find: Failed to query database for count"), ex);
            throw new WalkLiveService.RelationshipServiceException(("WalkLiveService.getUser: Failed to query database for count"), ex);
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


    private void createNewFriendRequest(String sender, String recipient, int request_id) throws WalkLiveService.RelationshipServiceException{
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "INSERT INTO friends (_id, sender, recipient, relationship, sent_on) VALUES (?, ?, ?, 0, null)" ;

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, request_id);
            ps.setString(2, sender);
            ps.setString(3, recipient);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY ADDED.");
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
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

    private void updateRelationship(String requestId) throws WalkLiveService.RelationshipServiceException {
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "UPDATE friends SET relationship = ? WHERE _id = ?";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ps.setString(2, requestId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
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

    private void deleteRelationship(String requestId) throws WalkLiveService.RelationshipServiceException {
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "DELETE FROM friends WHERE _id = ?";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setString(1, requestId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
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

    private String getCounter(String tableType){
        if (tableType.equals("sender")) {
            return "recipient";
        }
            return "sender";
    }

    private void checkDuplicateRequest(String sender, String recipient) throws WalkLiveService.RelationshipServiceException, WalkLiveService.DuplicateException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet res = null;

        String sql = "SELECT * FROM friends WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            //check if username exists
            ps = conn.prepareStatement(sql);
            ps.setString(1, sender);
            ps.setString(2, recipient);
            ps.setString(3, recipient);
            ps.setString(4, sender);
            res = ps.executeQuery();

            if (res.next()) {
                WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Request already exists.");
                throw new WalkLiveService.DuplicateException("WalkLiveService.createFriendRequest: Request already exists.");
            }

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
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

}