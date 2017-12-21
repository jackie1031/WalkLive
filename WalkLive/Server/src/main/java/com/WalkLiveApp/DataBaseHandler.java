package com.WalkLiveApp;

import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

/**
 * set up the database
 */
public class DataBaseHandler {
    private final Logger logger = WalkLiveService.logger;
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);


    /**
     * initialize the DB
     * @throws WalkLiveService.UserServiceException throws User exception if not created
     */
    public void initializeDataBase() throws WalkLiveService.UserServiceException {

        try {
            String[] tableCommands = this.getCreateTableStrings();
            for (String command: tableCommands) {
                jdbcTemplateObject.update(command);

            }
        } catch (Exception ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new WalkLiveService.UserServiceException("Failed to create schema at startup");
        }
    }

    /**
     * generate sql string to set up the table
     * @return return list of statements
     */
    private String[] getCreateTableStrings() {
        String[] commandTable = new String[7];

        commandTable[0] = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, emergency_id TEXT, emergency_number TEXT)" ;
        commandTable[1] = "CREATE TABLE IF NOT EXISTS counters (friend_request_ids INT DEFAULT 0, trip_ids INT DEFAULT 0,crimes_id INT DEFAULT 0)";
        commandTable[2] = "INSERT INTO counters (friend_request_ids) VALUES (0)";
        commandTable[3] = "CREATE TABLE IF NOT EXISTS friends (_id INT, sender TEXT, recipient TEXT, relationship INT, sent_on TIMESTAMP)";
        commandTable[4] = "CREATE TABLE IF NOT EXISTS ongoingTrips(tripId TEXT, username TEXT, destination TEXT, dangerLevel INT,startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";
        commandTable[5] = "CREATE TABLE IF NOT EXISTS doneTrips(tripId TEXT, userName TEXT, destination TEXT, dangerLevel INT,startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";

        commandTable[6] = "CREATE TABLE IF NOT EXISTS dangerZonesDay(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";
        commandTable[6] = "CREATE TABLE IF NOT EXISTS dangerZonesNight(longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";

        return commandTable;
    }




}
