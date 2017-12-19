package com.WalkLiveApp;

import com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserManager {
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);


    public User createNew(String body) throws WalkLiveService.UserServiceException, ParseException{
        User user = new Gson().fromJson(body, User.class);

        System.out.println("USERNAME:" + user.getUsername());
        this.checkUniqueness(user.getUsername());
        return this.createUser(user.getUsername(), user.getPassword(), user.getContact());
    }

//    public List<User> findAllUsers() throws WalkLiveService.UserServiceException, java.text.ParseException {
//        Connection conn = null;
//        Statement stm = null;
//        ResultSet res = null;
//        String sql = "SELECT * FROM users";
//
//        try {
//            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
//            stm = conn.createStatement();
//            res = stm.executeQuery(sql);
//
//            ArrayList<User> users = new ArrayList<>();
//            while (res.next()) {
//                User u = new User(res.getString(1), res.getString(2), res.getString(3), res.getString(4), WalkLiveService.df.parse(res.getString(5)), res.getString(6), res.getString(7));
//                users.add(u);
//            }
//            return users;
//
//        } catch (SQLException ex) {
//            WalkLiveService.logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
//            throw new WalkLiveService.UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
//        } finally {
//            //close connections
//            if (res != null) {
//                try {
//                    res.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//            if (stm != null) {
//                try {
//                    stm.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) { /* ignored */}
//            }
//        }
//    }

    public List<User> findAllUsers() throws WalkLiveService.UserServiceException, java.text.ParseException {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(sql);
        for(Map<String, Object> row : rows){
            users.add(new User(row.get("username").toString(),
                    row.get("password").toString(),
                    row.get("contact").toString(),
                    row.get("nickname").toString(),
                    WalkLiveService.df.parse(row.get("created_on").toString()),
                    row.get("emergency_id").toString(),
                    row.get("emergency_number").toString()));
        }
        return users;
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

    public User getUser(String username) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException {
        ResultSet res = null;
        PreparedStatement ps = null;
        Connection conn = null;
        //find user by username
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            res = ps.executeQuery();

            if (res.next()) {
                return new User(username, res.getString(2), res.getString(3), res.getString(4), WalkLiveService.df.parse(res.getString(5)), res.getString(6), res.getString(7));
            } else {
                WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
                throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
            }
        } catch (SQLException ex) {
            WalkLiveService.logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);

            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", username), ex);
        } catch (java.text.ParseException ex) {
            WalkLiveService.logger.error("WalkLiveService.find: Failed to properly parse date", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.getUser: Failed to properly parse date", ex);
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

    public void updatePassword(String username, String body) throws SQLException, WalkLiveService.UserServiceException, ParseException, java.text.ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String password = object.get("password").toString();
        this.setPassword(username, password);
    }

    public User updateEmergencyContact(String username, String body) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException, SQLException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String id = object.get("emergency_id").toString();
        String number = object.get("emergency_number").toString();
        User user = null;
        if (!id.equals("")) {
            user = this.getUser(id);}

        if (number.equals("")) {
            number = user.getContact(); }

        return this.setEmergencyContact(username, id, number);
    }

    public User updateUserContact(String username, String body) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException, SQLException {
        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String contact = object.get("contact").toString();

        return this.setUserContact(username, contact);
    }

    public static boolean isValid(String username) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException {
        try {
            new UserManager().getUser(username);
            return true;
        } catch (WalkLiveService.UserServiceException e) {
            WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
        }
    }

    private User checkCorrectPassword(ResultSet res, String username, String pw) throws WalkLiveService.UserServiceException, SQLException, ParseException{
        if (res.next()) {
            String targetPw = res.getString(2);
            if (!pw.equals(targetPw)) {
                WalkLiveService.logger.error("WalkLiveService.login: Failed to authenticate - incorrect password");
                throw new WalkLiveService.UserServiceException("WalkLiveService.login: Failed to authenticate - incorrect password");
            }

            return new User(username, null, res.getString(3), null, null, res.getString(6), res.getString(7));

        } else {
            //if the response is empty, aka the username does not exist in database
            WalkLiveService.logger.error(String.format("WalkLiveService.login: Failed to find username: %s", username));
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.login: Failed to find username: %s", username));
        }
    }

        private User createUser(String username, String password, String contact) throws WalkLiveService.UserServiceException{
        String sql = "INSERT INTO users (username, password, contact, nickname, created_on, emergency_id, emergency_number) " +
                "             VALUES (?, ?, ?, NULL, NULL, NULL, NULL)" ;
        try {
            jdbcTemplateObject.update(sql, username, password, contact);
            return new User(username, password, contact, null, null, null, null);

        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        }
    }


    private void checkUniqueness(String username) throws WalkLiveService.UserServiceException{
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try {
            this.jdbcTemplateObject.queryForMap(sql, username);
            WalkLiveService.logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
            throw new WalkLiveService.UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");
        } catch (EmptyResultDataAccessException e) {
            /* ignored */
        }
    }

    private User setEmergencyContact(String username, String id, String number) throws WalkLiveService.UserServiceException, ParseException, SQLException {
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "UPDATE users SET emergency_id = ?, emergency_number = ? WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, number);
            ps.setString(3, username);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY UPDATED.");
            return new User(null, null, null, null, null, id, number);

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateEmergencyContact: Failed to update emergency information", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateEmergencyContact: Failed to emergency information", ex);
        } finally {
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

    private User setUserContact(String username, String number) throws WalkLiveService.UserServiceException, ParseException, SQLException {
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "UPDATE users SET contact = ? WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, number);
            ps.setString(2, username);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY UPDATED.");
            return new User(username, null, number, null, null, null, null);

        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateUserContact: Failed to update user contact information", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateUserContact: Failed to user contact information", ex);
        } finally {
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



    private void setPassword(String username, String password) throws WalkLiveService.UserServiceException, ParseException, SQLException{
        PreparedStatement ps = null;
        Connection conn = null;

        String sql = "UPDATE users SET password = ? WHERE username = ? LIMIT 1";

        try {
            conn = DriverManager.getConnection(ConnectionHandler.url, ConnectionHandler.user, ConnectionHandler.password);
            ps = conn.prepareStatement(sql);
            ps.setString(1, password);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY UPDATED.");
        } catch (SQLException ex) {
            WalkLiveService.logger.error("WalkLiveService.updateEmergencyContact: Failed to update emergency information", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateEmergencyContact: Failed to emergency information", ex);
        } finally {
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
