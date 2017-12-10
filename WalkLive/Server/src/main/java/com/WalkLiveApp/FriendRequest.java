package com.WalkLiveApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;

public class FriendRequest {
    private int request_id;
    private Date sent_on = new Date();
    private String sender;
    private String recipient;

    public FriendRequest(String sender, String recipient, Date requestTime) throws WalkLiveService.UserServiceException {
        this.request_id = getNewRequestId(); //open connection and retrieve number of requests, increment and return
        this.sent_on = requestTime;
        this.sender = sender;
        this.recipient = recipient;
    }

    public FriendRequest(int requestId, String sender, String recipient, Date requestTime) {
        this.request_id = requestId;
        this.sent_on = requestTime;
        this.sender = sender;
        this.recipient = recipient;
    }

    public int getRequestId() { return this.request_id; }

    public Date getRequestTime() {
        return this.sent_on;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public int getNewRequestId() throws WalkLiveService.UserServiceException {
        Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

        String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
        String user = "b0a1d19d87f384";
        String password = "6d11c74b";
        Connection conn = null;

        ResultSet res;
        Statement stm = null;

        //find user by username counters (friend_request_ids INT)
        String sql = "UPDATE counters SET friend_request_ids = friend_request_ids + 1 ";
        String getValue = "SELECT * FROM counters";

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();
            stm.executeUpdate(sql);
            res = stm.executeQuery(getValue);

            if (res.next()) {
                System.out.println("THIS: " + res.getInt(1));
                return res.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for count"), ex);
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for count"), ex);
        } finally {
            //close connections
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendRequest fr = (FriendRequest) o;

        if (request_id != fr.request_id) return false;
        if (sent_on != null ? !sent_on.equals(fr.sent_on) : fr.sent_on != null) return false;
        if (sender != null ? !sender.equals(fr.sender) : fr.sender != null) return false;
        if (recipient != null ? !recipient.equals(fr.recipient) : fr.recipient != null) return false;

        return true;
    }

    public String toString() {
        String str = "ID: " + this.request_id + " FROM: " + this.sender + " TO: " + this.recipient + " SENT AT: " + this.sent_on;

        return str;
    }
}
