package com.WalkLiveApp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<User> findAllUsers() throws WalkLiveService.UserServiceException, java.text.ParseException {
        Connection conn = null;
        Statement stm = null;
        ResultSet res = null;
        String sql = "SELECT * FROM users";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
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
                createdOn = WalkLiveService.df.parse(res.getString(5));
                emergencyId = res.getString(6);
                emergencyNumber = res.getString(7);

                User u = new User(username, pw, contact, nickname, createdOn, emergencyId, emergencyNumber);
                users.add(u);
            }
            return users;

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
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

    public User login(String body) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException {
        ResultSet res = null;
        PreparedStatement ps = null;
        Connection conn = null;

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String pw = object.get("password").toString();

        //FIRST, search to see if username exists in database
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url,ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            return this.checkCorrectPassword(res, username, pw);

        } catch (SQLException ex) {
            WalkLiveService.logger.error(String.format("WalkLiveService.login: Failed to query database for username: %s", username), ex);
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.login: Failed to query database for username: %s", username), ex);
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

    private User checkCorrectPassword(ResultSet res, String username, String pw) throws WalkLiveService.UserServiceException, SQLException, ParseException{
        if (res.next()) {
            String targetPw = res.getString(2);
            if (!pw.equals(targetPw)) {
                WalkLiveService.logger.error(String.format("WalkLiveService.login: Failed to authenticate - incorrect password"));
                throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.login: Failed to authenticate - incorrect password"));
            }

            return new User(username, null, res.getString(3), null, null, res.getString(6), res.getString(7));

        } else {
            //if the response is empty, aka the username does not exist in database
            WalkLiveService.logger.error(String.format("WalkLiveService.login: Failed to find username: %s", username));
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.login: Failed to find username: %s", username));
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
