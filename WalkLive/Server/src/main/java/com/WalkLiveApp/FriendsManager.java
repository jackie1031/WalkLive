package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsManager {
    public void createFriendRequest(String sender, String body) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, ParseException, SQLException, java.text.ParseException {
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
        //THIS IS NOT IMPLEMENTED YET

        this.createNewFriendRequest(sender, recipient, request_id);
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
}
