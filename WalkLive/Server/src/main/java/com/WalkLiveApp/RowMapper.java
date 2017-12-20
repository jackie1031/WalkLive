package com.WalkLiveApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowMapper {

    public static List<User> decodeAllUsers(List<Map<String, Object>> rows) throws java.text.ParseException{
        ArrayList<User> users = new ArrayList<>();
        for(Map<String, Object> row : rows){
            users.add(new User(row.get("username").toString(),
                    row.get("password").toString(),
                    row.get("contact").toString(),
                    null,
                    WalkLiveService.df.parse(row.get("created_on").toString()),
                    (String) row.get("emergency_id"),
                    (String) row.get("emergency_number")));
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
}
