package com.WalkLiveApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * get data from map and decode into objects
 */
public class RowMapper {

    /**
     * Decode a list of users from jdbc template query
     * @param rows rows of information
     * @return list of users
     * @throws java.text.ParseException cannot parse data
     */
    public static List<User> decodeAllUsers(List<Map<String, Object>> rows) throws java.text.ParseException {
        ArrayList<User> users = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            users.add(RowMapper.decodeUser(row));
        }
        return users;
    }

    /**
     * Decode a list of trips from jdbc template query
     * @param rows rows of information
     * @return list of trips
     * @throws java.text.ParseException cannot parse data
     */
    public static List<Trip> decodeAllTrips(List<Map<String, Object>> rows) throws java.text.ParseException {
        ArrayList<Trip> trips = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            trips.add(RowMapper.decodeTrip(row));
        }
        return trips;
    }

    /**
     * Decode a list of clusters from jdbc template
     * @param rows rows of information
     * @return list of clusters
     * @throws java.text.ParseException cannot parse data
     */
    public static List<Cluster> decodeAllClusters(List<Map<String, Object>> rows) throws java.text.ParseException{
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            clusters.add(RowMapper.decodeCluster(row));
        }
        return clusters;
    }

    /**
     * Decode a list of requests from jdbc template
     * @param rows rows of information
     * @return list of requests
     * @throws java.text.ParseException cannot parse data
     */
    public static List<Relationship> decodeAllRequests(List<Map<String, Object>> rows) throws java.text.ParseException {
        ArrayList<Relationship> relationships = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            relationships.add(RowMapper.decodeRequest(row));
        }
        return relationships;
    }


    /**
     * Decode a list of friends from jdbc template
     * @param rows rows of information
     * @return list of friends
     * @throws java.text.ParseException cannot parse data
     */
    public static List<Relationship> decodeAllFriendship(List<Map<String, Object>> rows) throws java.text.ParseException{
        ArrayList<Relationship> relationships = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            relationships.add(RowMapper.decodeFriendship(row));
        }
        return relationships;
    }


    /**
     * Decode user from jdbc template
     * @param row rows of information
     * @return user
     * @throws java.text.ParseException cannot parse data
     */
    public static User decodeUser(Map<String, Object> row) throws java.text.ParseException {
        return new User(row.get("username").toString(),
                row.get("password").toString(),
                (String) row.get("contact"),
                null,
                WalkLiveService.df.parse(row.get("created_on").toString()),
                (String) row.get("emergency_id"),
                (String) row.get("emergency_number"));
    }

    /**
     * Decode trip from jdbc template
     * @param row rows of information
     * @return trip
     */
    public static Trip decodeTrip(Map<String, Object> row) {
        return new Trip((int) row.get("tripId"),
                (String) row.get("username"),
                (String) row.get("destination"),
                (String) row.get("startTime"),
                false,
                (double) row.get("startLat"),
                (double) row.get("startLong"),
                (double) row.get("curLat"),
                (double) row.get("curLong"),
                (double) row.get("endLat"),
                (double) row.get("endLong"),
                (String) row.get("emergencyNum"),
                (String) row.get("timeSpent"),
                (String) row.get("address"));
    }

    /**
     * Decode relationship from jdbc template
     * @param row rows of information
     * @return requests
     * @throws java.text.ParseException cannot parse data
     */
    public static Relationship decodeRequest(Map<String, Object> row) throws java.text.ParseException {
        return new Relationship((int) row.get("_id"),
                (String) row.get("sender"),
                (String) row.get("recipient"),
                (int) row.get("relationship"),
                (Date) row.get("sent_on"));
    }

    /**
     * Decode friend from jdbc template
     * @param row rows of information
     * @return requests
     * @throws java.text.ParseException cannot parse data
     */
    public static Relationship decodeFriendship(Map<String, Object> row) throws java.text.ParseException {
        return new Relationship(0,
                (String) row.get("sender"),
                (String) row.get("recipient"),
                1,
                null);
    }

    /**
     * Decode cluster from jdbc template
     * @param row rows of information
     * @return cluster
     * @throws java.text.ParseException cannot parse data
     */
    public static Cluster decodeCluster(Map<String, Object> row) throws java.text.ParseException {
        return new Cluster(
                (Double) row.get("longitude"),
                (Double) row.get("latitude"),
                (Double) row.get("radius"),
                (int) row.get("dangerLevel")
        );
    }


}