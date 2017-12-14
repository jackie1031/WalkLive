package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;

public class UserManager {
    public User createNew(String body) throws WalkLiveService.UserServiceException, ParseException, SQLException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();
        String contact = object.get("contact").toString();

        //debugging
        System.out.println("USERNAME:" + username);

        //FIRST, check to see if username already exists in databse
        this.checkUniqueness(username);

        //SECOND, if username did not exist, then place the information into the database
        return this.createUser(username, pw, contact);

    }

    private void checkUniqueness(String username) throws WalkLiveService.UserServiceException, ParseException, SQLException{
        PreparedStatement ps = null;
        ResultSet res = null;

        //FIRST, check to see if username already exists in databse
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            if (res.next()) { //if there is something in the response, means that username is already taken (401)
                WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
                throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");
            }

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry - query error", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry - query error", ex);
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

    private User createUser(String username, String password, String contact) throws WalkLiveService.UserServiceException, ParseException, SQLException{
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "INSERT INTO users (username, password, contact, nickname, created_on, emergency_id, emergency_number) " +
                "             VALUES (?, ?, ?, NULL, NULL, NULL, NULL)" ;

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, contact);
            ps.executeUpdate();

            return new User(username, password, contact, null, null, null, null);

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
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
}
