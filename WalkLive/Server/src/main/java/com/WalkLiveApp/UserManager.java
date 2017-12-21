package com.WalkLiveApp;
import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.*;
import java.util.List;

public class UserManager {
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);

    /**
     * Create new user
     * @param body information passed in for user registration
     * @return new User object
     * @throws WalkLiveService.UserServiceException cannot create user
     * @throws ParseException cannot parse information
     */
    public User createNew(String body) throws WalkLiveService.UserServiceException, ParseException{
        User user = new Gson().fromJson(body, User.class);

        System.out.println("USERNAME:" + user.getUsername());
        this.checkUniqueness(user.getUsername());
        return this.createUser(user.getUsername(), user.getPassword(), user.getContact());
    }

    /**
     * find all users
     * @return User with appropriate information
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws java.text.ParseException cannot parse information
     */
    public List<User> findAllUsers() throws WalkLiveService.UserServiceException, java.text.ParseException {
        String sql = "SELECT * FROM users";
        try {
            return RowMapper.decodeAllUsers(jdbcTemplateObject.queryForList(sql));
        } catch (Exception e){
            WalkLiveService.logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", e);
            throw new WalkLiveService.UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", e);
        }
    }

    /**
     * login with user information
     * @param body  information passed in for user lgoin
     * @return User with appropriate information
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws java.text.ParseException cannot parse information
     */
    public User login(String body) throws WalkLiveService.UserServiceException, java.text.ParseException {
        User user = new Gson().fromJson(body, User.class);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? LIMIT 1";
        try{
        return RowMapper.decodeUser(jdbcTemplateObject.queryForMap(sql, user.getUsername(), user.getPassword()));}
        catch (Exception e){
            WalkLiveService.logger.error(String.format("WalkLiveService.login: Failed to query database for username: %s", user.getUsername()), e);
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.login: Failed to query database for username: %s", user.getUsername()), e);
        }
    }

    /**
     *
     * @param username  information passed in for user
     * @return User with appropriate information
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws java.text.ParseException cannot parse information
     */
    public User getUser(String username) throws WalkLiveService.UserServiceException, java.text.ParseException{
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try {
            return RowMapper.decodeUser(jdbcTemplateObject.queryForMap(sql, username));
        }catch (Exception e){
            WalkLiveService.logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), e);
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to query database for username: %s", username), e);
        }
    }

    /**
     *
     * @param username  information passed in for user update
     * @param body  information passed in for user update
     * @return User with appropriate information
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws ParseException cannot parse information
     * @throws java.text.ParseException cannot parse information
     */
    public User updateEmergencyContact(String username, String body) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException{
        User tempt = new Gson().fromJson(body, User.class);
        String id = tempt.getEmergencyId();
        String number = tempt.getEmergencyNumber();
        User user = null;

        if (!id.equals("")) {
            user = this.getUser(id);}

        if (number.equals("")) {
            number = user.getContact(); }

        return this.setEmergencyContact(username, id, number);
    }

    /**
     *
     * @param username  information passed in for user update
     * @param body  information passed in for user update
     * @return User with appropriate information
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws ParseException cannot parse information
     * @throws java.text.ParseException cannot parse information
     */
    public User updateUserContact(String username, String body) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException, SQLException {
        User user = new Gson().fromJson(body, User.class);
        String contact = user.getContact();

        return this.setUserContact(username, contact);
    }

    /**
     *
     * @param username  information passed in for user check
     * @return boolean of whether user exists
     * @throws WalkLiveService.UserServiceException cannot update/find information
     * @throws ParseException cannot parse information
     * @throws java.text.ParseException cannot parse information
     */
    public static boolean isValid(String username) throws WalkLiveService.UserServiceException, ParseException, java.text.ParseException {
        try {
            new UserManager().getUser(username);
            return true;
        } catch (WalkLiveService.UserServiceException e) {
            WalkLiveService.logger.error(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
            throw new WalkLiveService.UserServiceException(String.format("WalkLiveService.getUser: Failed to find username: %s", username));
        }
    }

    /*
        Helper Methods
     */
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

    private User setEmergencyContact(String username, String id, String number) throws WalkLiveService.UserServiceException, ParseException {
        String sql = "UPDATE users SET emergency_id = ?, emergency_number = ? WHERE username = ? LIMIT 1";
        try {
            jdbcTemplateObject.update(sql, id, number, username);
            return new User(null, null, null, null, null, id, number);

        } catch (Exception e){
            WalkLiveService.logger.error("WalkLiveService.updateEmergencyContact: Failed to update emergency information", e);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateEmergencyContact: Failed to emergency information", e);
        }
    }

    private User setUserContact(String username, String number) throws WalkLiveService.UserServiceException, ParseException, SQLException {
        String sql = "UPDATE users SET contact = ? WHERE username = ? LIMIT 1";

        try {
            jdbcTemplateObject.update(sql, number, username);
            return new User(username, null, number, null, null, null, null);

        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.updateUserContact: Failed to update user contact information", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.updateUserContact: Failed to user contact information", ex);
        }
    }
}
