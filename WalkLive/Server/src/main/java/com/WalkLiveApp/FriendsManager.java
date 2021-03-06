package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * control all action related to friends
 */
public class FriendsManager {
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);

    /**
     * create friend request
     * @param sender: the one sending the request
     * @param body: the body containg recipient
     * @throws WalkLiveService.UserServiceException: invalid user
     * @throws WalkLiveService.RelationshipServiceException: invalid relationsip
     * @throws WalkLiveService.DuplicateException: duplicate friend request
     * @throws ParseException: cannot parse
     * @throws SQLException: sql statement problem
     * @throws java.text.ParseException: java parsing issue
     */
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

    /**
     * @param sender: the one sending the requests
     * @return List of outgoing friend request
     * @throws WalkLiveService.RelationshipServiceException invalid relationsip
     */
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

    /**
     *
     * @param recipient: user that receives the requests
     * @return list of incoming friend request
     * @throws WalkLiveService.RelationshipServiceException invalid relationsip
     */
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

    /**
     *
     * @param responder the responder to friend request
     * @param requestId request id
     * @param response accept or reject
     * @throws WalkLiveService.UserServiceException invalid user
     * @throws WalkLiveService.RelationshipServiceException invalid relationsip
     */
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

    /**
     *
     * @param username user that requests the friendlist
     * @return a list of friends
     * @throws WalkLiveService.UserServiceException invalid user
     * @throws WalkLiveService.RelationshipServiceException invalid relationship
     * @throws ParseException cannot parse the data retrieved from the table
     * @throws java.text.ParseException cannot parse the data to its corresponding object
     */
    public List<User> getFriendList(String username) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, ParseException, java.text.ParseException {
        ArrayList<User> friends = new ArrayList<>();
        this.addFriendsToList(username, friends, "recipient");
        this.addFriendsToList(username, friends, "sender");
        return friends;
    }

    /*
         Helper Methods
     */

    private void addFriendsToList(String username, ArrayList<User> friends, String tableType) throws WalkLiveService.UserServiceException, WalkLiveService.RelationshipServiceException, ParseException, java.text.ParseException{
        String sql = "SELECT " + tableType + " FROM friends WHERE " + this.getCounter(tableType) + " = ? AND relationship = 1";
        UserManager userManager = new UserManager();
        try {
            List<Relationship> relationships = RowMapper.decodeAllFriendship(jdbcTemplateObject.queryForList(sql, username));
            for (Relationship rel : relationships) {
                String recipient = this.getRecipient(rel, tableType);
                friends.add(new User(recipient, null, userManager.getUser(recipient).getContact(), null, null, null, null));
            }
        } catch (EmptyResultDataAccessException e) {
            /* ignored */
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.getFriendList: Failed to fetch friend list", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.getIncomingFriendList: Failed to fetch friend list", ex);
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

    private String getRecipient(Relationship rel, String tableType){
        if (tableType.equals("sender")){
            return rel.getSender();
        } else {
            return rel.getRecipient();
        }
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
