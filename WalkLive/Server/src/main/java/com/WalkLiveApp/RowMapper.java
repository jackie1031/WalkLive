package com.WalkLiveApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowMapper {

    public static List<User> decodeAllUsers(List<Map<String, Object>> rows) throws java.text.ParseException{
        ArrayList<User> users = new ArrayList<>();
        for(Map<String, Object> row : rows){
            users.add(RowMapper.decodeUser(row));
        }
        return users;
    }

    public static User decodeUser(Map<String, Object> row) throws java.text.ParseException{
            return new User(row.get("username").toString(),
                    row.get("password").toString(),
                    (String)row.get("contact"),
                    null,
                    WalkLiveService.df.parse(row.get("created_on").toString()),
                    (String) row.get("emergency_id"),
                    (String) row.get("emergency_number"));
    }

    public static Trip decodeTrip(Map<String, Object> row) {
        //             return new Trip(tripId,res.getString(2), res.getString(3), res.getString(5), res.getBoolean(6), res.getDouble(7), res.getDouble(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getString(13), res.getString(14), res.getString(15));

        return new Trip((int)row.get("tripId"),
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
}
