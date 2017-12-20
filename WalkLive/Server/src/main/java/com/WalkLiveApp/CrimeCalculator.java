package com.WalkLiveApp;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrimeCalculator {
    public static double LONGRADIUS = 0.01818;
    public static double LATRADIUS = 0.014449;
    private JdbcTemplate jdbcTemplateObject = new JdbcTemplate(ConnectionHandler.dataSource);


    /**
     * function to find the cluster around the given coordinate
     * @param latitudeStr
     * @param longitudeStr
     * @return arraylist of clusters
     * @throws WalkLiveService.UserServiceException: invalid
     */

    public Crime getDangerLeveLZone(String latitudeStr, String longitudeStr, String isDaystr) throws WalkLiveService.UserServiceException, java.text.ParseException{
        double longitude = Double.parseDouble(longitudeStr);
        double latitude = Double.parseDouble(latitudeStr);
        int isDay = Integer.parseInt(isDaystr);
        WalkLiveService.logger.info("long: "+longitude +" lat: "+ latitude);


        String sql = this.getDangerSqlString(isDay);
        try {
            List<Cluster> clusters = RowMapper.decodeAllClusters(jdbcTemplateObject.queryForList(sql, longitude+LONGRADIUS, longitude-LONGRADIUS, latitude+LATRADIUS, latitude-LATRADIUS));
            return new Crime(this.getLocationDangerLevel(clusters),clusters);
        } catch(EmptyResultDataAccessException ex){
            return new Crime(0, new ArrayList<>());
        } catch (Exception ex) {
            WalkLiveService.logger.error("WalkLiveService.find: Failed to query database for get danger level", ex);
            throw new WalkLiveService.UserServiceException("WalkLiveService.getUser: Failed to query database for get danger level", ex);
        }
    }


    private int getLocationDangerLevel(List<Cluster> clusters) {
        int dangerLevel = 0;
        for (Cluster cluster: clusters){
            dangerLevel += cluster.getDangerLevel();
        }
        if (clusters.size() > 0){
            return (dangerLevel/clusters.size());
        }
        return 0;
    }

    private String getDangerSqlString(int isDay){
        if (isDay == 0){
            return "SELECT * FROM dangerZonesDay WHERE (longitude < ? AND longitude > ?) AND (latitude < ? AND latitude > ?)";
        }
        return "SELECT * FROM dangerZonesNight WHERE (longitude < ? AND longitude > ?) AND (latitude < ? AND latitude > ?)";
    }
}
