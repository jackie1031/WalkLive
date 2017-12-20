package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsManager {
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);

    public void createFriendRequest(String sender, String body) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, WalkLiveService.DuplicateException, ParseException, SQLException, java.text.ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String recipient = object.get("recipient").toString();
        int request_id = getNewRequestId();

        UserManager.isValid(sender);
        User rUser = new UserManager().getUser(recipient);

        if (rUser.getUsername().equals(sender)) {
            WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Unable to create a friend request to yourself.");
        }

        this.checkDuplicateRequest(sender, recipient);
        this.createNewFriendRequest(sender, recipient, request_id);
    }

    public List<Relationship> getOutgoingFriendRequests(String sender) throws WalkLiveService.RelationshipServiceException{
        String sql = "SELECT * FROM friends WHERE sender = ?";

        try {
            return RowMapper.decodeAllRequests(jdbcTemplateObject.queryForList(sql, sender));
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
        }
    }

    public List<Relationship> getIncomingFriendRequests(String recipient) throws WalkLiveService.RelationshipServiceException {
        String sql = "SELECT * FROM friends WHERE recipient = ? AND relationship = 0 ";

        try {
            return RowMapper.decodeAllRequests(jdbcTemplateObject.queryForList(sql, recipient));
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
        }
    }


    public void respondToFriendRequest(String responder, String requestId, String response) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException {
        String sql = "SELECT * FROM friends WHERE _id = ? LIMIT 1";

        try {
            Relationship res = RowMapper.decodeRequest(jdbcTemplateObject.queryForMap(sql, requestId));
            String recipient = res.getRecipient();
            if (!responder.equals(recipient)) {
                WalkLiveService.logger.error(String.format("WalkLiveService.respondToFriendRequest: User %s is unauthorized to respond to request %s", responder, requestId));
                throw new WalkLiveService.RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest:  User %s is unauthorized to respond to request %s", responder, requestId));
            }
        } catch (EmptyResultDataAccessException e) {
            WalkLiveService.logger.error(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %s", requestId));
            throw new WalkLiveService.RelationshipServiceException(String.format("WalkLiveService.respondToFriendRequest: Failed to find relationship id: %s", requestId));
        } catch (java.text.ParseException e) {
            WalkLiveService.logger.error("WalkLiveService.respondToFriendRequest: failed to respond to request");
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.respondToFriendRequest: failed to respond to request");
        }
        if (response.equals("accept")) {
            updateRelationship(requestId);
        } else {
            deleteRelationship(requestId);
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

    private int getNewRequestId() {
        String sql = "UPDATE counters SET friend_request_ids = friend_request_ids + 1 ";
        String getValue = "SELECT friend_request_ids FROM counters";
        try{
            jdbcTemplateObject.update(sql);
            return (int) (jdbcTemplateObject.queryForMap(getValue).get("friend_request_ids"));
        } catch (Exception e) {
            return 0;
        }
    }


    private void createNewFriendRequest(String sender, String recipient, int request_id) throws WalkLiveService.RelationshipServiceException{
        String sql = "INSERT INTO friends (_id, sender, recipient, relationship, sent_on) VALUES (?, ?, ?, 0, null)" ;
        try {
            jdbcTemplateObject.update(sql, request_id, sender, recipient);
            System.out.println("SUCCESSFULLY ADDED.");
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
        }
    }

//    private void updateRelationship(String requestId) throws WalkLiveService.RelationshipServiceException {
//        PreparedStatement ps = null;
//        Connection conn = null;
//
//        String sql = "UPDATE friends SET relationship = ? WHERE _id = ?";
//
//        try {
//            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
//            //check if username exists
//            ps = conn.prepareStatement(sql);
//            ps.setInt(1, 1);
//            ps.setString(2, requestId);
//            ps.executeUpdate();
//
//        } catch (SQLException ex) {
//            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
//            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
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

    private void updateRelationship(String requestId) throws WalkLiveService.RelationshipServiceException {
        String sql = "UPDATE friends SET relationship = ? WHERE _id = ?";

        try {
           jdbcTemplateObject.update(sql, 1, requestId);

        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
        }
    }

    private void deleteRelationship(String requestId) throws WalkLiveService.RelationshipServiceException {
        String sql = "DELETE FROM friends WHERE _id = ?";

        try {
            jdbcTemplateObject.update(sql, requestId);

        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.updateRelationship: Failed to update relationship status", ex);
        }
    }

    private String getCounter(String tableType){
        if (tableType.equals("sender")) {
            return "recipient";
        }
            return "sender";
    }

    private void checkDuplicateRequest(String sender, String recipient) throws WalkLiveService.RelationshipServiceException, WalkLiveService.DuplicateException {
        String sql = "SELECT * FROM friends WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?)";
        try {
            jdbcTemplateObject.queryForMap(sql, sender, recipient, recipient, sender);
            WalkLiveService.logger.error("WalkLiveService.createFriendRequest: Request already exists.");
            throw new WalkLiveService.DuplicateException("WalkLiveService.createFriendRequest: Request already exists.");

        } catch (EmptyResultDataAccessException e) {
            /* ignored */
        }
    }
}
