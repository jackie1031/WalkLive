package com.WalkLiveApp;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

import javax.sql.DataSource;

import org.json.simple.parser.JSONParser;


public class WalkLiveService {
    private Sql2o db;


    private final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

    public Sql2o getDb() {
        return db;
    }

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public WalkLiveService(DataSource dataSource) throws UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP )";
            conn.createQuery(sql).executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new UserServiceException("Failed to create schema at startup");
        }
    }

    //HELPER FUNCTIONS
//    private void validateId(String id) throws UserServiceException {
//        if (id.length() < 8) {
//            throw new UserServiceException();
//        }
//    }
//
//    private void usernameDoesNotExist(String id) throws UserServiceException {
//        if (collection.find({ "username": id })){
//            throw new UserServiceException();
//        }
//    }

    /*
    * Create a new User and add to database.
    * Should check database if already exists or not, and then if it doesnt exist then create and push into
    * the user database (with username and password information) and then have the remaining fields as null.
    * In the case that the username already exists, it should throw an exception. In the case that the
    * username or password is an invalid string, it should throw an exception. (In the case that we are logging
    * in through Facebook or Google, we should still create a new user in the case that its their first time
    * logging in, so may have to create a user simple only with the email information
    */
    public void createNew(String body) throws UserServiceException {
        User user = new Gson().fromJson(body, User.class);
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse(body);
//        JSONArray array = (JSONArray)obj;
//        String username = array.get("username");

        String sql = "INSERT INTO user (username, password, nickname, friendId, createdOn) " +
                "             VALUES (:username, :password, :nickname, :friendId, :createdOn)";

//        try (validateId(username)) {
//            //worked
//        } catch (UserServiceException e) {
//            logger.error(String.format("WalkLiveService.createNew: username too short: %s", username), e);
//            throw new InvalidUsernameException("WalkLiveService.createNew: username too short: %s", username), e);
//        }
//
//        try(usernameDoesNotExist(username)) {
//            //worked
//        } catch (UserServiceException e) {
//            logger.error(String.format("WalkLiveService.createNew: username already exists: %s", username), e);
//            throw new InvalidUsernameException("WalkLiveService.createNew: username already exists: %s", username), e);
//        }

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(user)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry");
        }
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException {
        try (Connection conn = db.open()) {
            List<User> users = conn.createQuery("SELECT * FROM user")
                    .addColumnMapping("username", "username")
                    .addColumnMapping("createdOn", "createdOn")
                    .executeAndFetch(User.class);
            return users;
        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries");
        }
    }

    /*
     * returns emergencyId and emergencyNumber
     */
    public String login(String body) throws UserServiceException {
        User user = new Gson().fromJson(body, User.class);

        JSONParser parser = new JSONParser();
        //Object obj = parser.parse(body);
        //JSONArray array = (JSONArray)obj;
        //String username = array.get("username");

        //String sql = "SELECT * FROM item WHERE username = :username ";

//        try (Connection conn = db.open()) {
//            return conn.createQuery(sql)
//                    .addParameter("username", username)
//                    .addColumnMapping("item_id", "id")
//                    .addColumnMapping("created_on", "createdOn")
//                    .executeAndFetchFirst(User.class);
//        } catch(Sql2oException ex) {
//            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
//            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username));
//        }

        return null;
    }


    /**
     * For trip part -----------------------
     **/

    public String startTrip(String body) throws UserServiceException {

        return "";

    }

    public Trip getTrip(String body) throws UserServiceException {
        //process body
        Trip thisTrip = new Trip();

        return thisTrip;

    }

    public Trip updateDestination(String body) throws UserServiceException {

        //{ tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }
        Trip thisTrip = new Trip();

        return thisTrip;
    }

    public String shareTrip(String body) throws UserServiceException {

        // to another user
        return "";

    }

    public String respondTripRequest(String body) throws UserServiceException {
        return "";


    }

    //Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
    public Trip addTimePoint(String body) throws UserServiceException {
        Trip addToTrip = new Trip();
        return addToTrip;

    }

    public String getTimePoint(String body) throws UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        return "";

    }

    public Trip getLatestTimePoint(String body) throws UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        Trip addToTrip = new Trip();
        return addToTrip;
    }

    public List<Crime> getCrimes(Crime from, Crime to, int timeOfDay, String table) {
        try (Connection conn = db.open()) {
            String sql = "SELECT date, address, latitude, longitude, type FROM " + table + " WHERE "
                    + "latitude >= :fromLat AND latitude <= :toLat AND date >= :fromDate AND "
                    + "longitude >= :fromLng AND longitude <= :toLng AND date <= :toDate;";

            Query query = conn.createQuery(sql);
            query.addParameter("fromLat", from.getLat())
                    .addParameter("toLat", to.getLat())
                    .addParameter("fromLng", from.getLng())
                    .addParameter("toLng", to.getLng())
                    .addParameter("fromDate", from.getDate())
                    .addParameter("toDate", to.getDate());

            List<Crime> results = query.executeAndFetch(Crime.class);
            return results;
        } catch (Sql2oException e) {
            logger.error("Failed to get crimes", e);
            return null;
        }
    }

    public DangerZone getDangerZone(Coordinate from, Coordinate to, String table) throws UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
        try (Connection conn = db.open()) {
            int[] red = getLinkIds(conn, from, to, "alarm > 2000", table);
            int[] yellow = getLinkIds(conn, from, to, "alarm <= 2000 AND alarm >1000 ", table);
            return new DangerZone(red, yellow);
        } catch (Sql2oException e) {
            logger.error("Failed to fetch linkIds", e);
            return null;
        } catch (NullPointerException e) {
            logger.error("Null pointer, failed to fetch linkIds", e);
            return null;
        }
    }

    private int[] getLinkIds(Connection conn, Coordinate from, Coordinate to, String predicate, String table)
            throws Sql2oException, NullPointerException {
        Table fromGrid = new Table(from.getLatitude(), from.getLongitude());
        Table toGrid = new Table(to.getLatitude(), to.getLongitude());

        String sqlGetAvoidLindIds = "SELECT DISTINCT linkId FROM "
                + table
                + " WHERE "
                + "x >= :fromX AND x <= :toX AND "
                + "y <= :fromY AND y >= :toY AND "
                + predicate + " ORDER BY alarm DESC LIMIT 20";
        Query queryGetAvoidLindIds = conn.createQuery(sqlGetAvoidLindIds);

        List<Integer> avoidLindIds = queryGetAvoidLindIds
                .addParameter("fromX", fromGrid.getX())
                .addParameter("toX", toGrid.getX())
                .addParameter("fromY", fromGrid.getY())
                .addParameter("toY", toGrid.getY())
                .executeAndFetch(Integer.class);

        int size = avoidLindIds.size();
        int[] linkIds = new int[size];
        for (int i = 0; i < size; i++) {
            linkIds[i] = avoidLindIds.get(i);
        }
        return linkIds;

    }
}
