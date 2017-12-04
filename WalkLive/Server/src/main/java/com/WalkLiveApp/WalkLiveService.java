package com.WalkLiveApp;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.sql.DataSource;

import org.json.simple.parser.JSONParser;
import sun.security.util.Password;


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
    public WalkLiveService(DataSource dataSource) throws WalkLiveService.UserServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP )";
            String sql2 = "CREATE TABLE IF NOT EXISTS friendRequests (sender TEXT, recipient TEXT, sent_on TIMESTAMP)";
            conn.createQuery(sql).executeUpdate();
            conn.createQuery(sql2).executeUpdate();
        } catch (Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new WalkLiveService.UserServiceException("Failed to create schema at startup");
        }
    }

    /**
     * ================================================================
     * User SignUp/Login/Query
     * ================================================================
     */

    /*
    * Create a new User and add to database.
    * Should check database if already exists or not, and then if it doesnt exist then create and push into
    * the user database (with username and password information) and then have the remaining fields as null.
    * In the case that the username already exists, it should throw an exception. In the case that the
    * username or password is an invalid string, it should throw an exception. (In the case that we are logging
    * in through Facebook or Google, we should still create a new user in the case that its their first time
    * logging in, so may have to create a user simple only with the email information
    */
    public void createNew(String body) throws UserServiceException, ParseException, Sql2oException {
        User user = new Gson().fromJson(body, User.class);

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String password = object.get("password").toString();

        //check length of password and other security measures

        //debugging
        System.out.println("USERNAME:" + username);

        String sql = "SELECT * FROM users WHERE username = :username LIMIT 1";
        //if the query != null then username already exists. - set response code to 401 (invalid UserId)

        try (Connection conn = db.open()) {

            List<User> found = conn.createQuery(sql)
                    .addParameter("username", username)
                    .executeAndFetch(User.class);

            if (!found.isEmpty()) { //the fact that something exists here means that the username exists
                //which means that we should stop the process and throw an error
                logger.error("WalkLiveService.createNew: Failed to create new entry - duplicate username");
                throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - duplicate username");
            }

        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry - query error", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry - query error", ex);
        }

        PasswordEncryptor enc = new PasswordEncryptor();
        int salt = enc.getNewSalt();
        String hashed = enc.encryptPassword(password, salt);

        sql = "INSERT INTO users (username, password, nickname, friendId, createdOn) " +
                "             VALUES (:username, :password, :nickname, :friendId, :createdOn)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(user)
                    .executeUpdate();
            //System.out.println("SUCCESSFULLY ADDED.");
        } catch(Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        }

        sql = "INSERT INTO tokens (username, hash, salt) " +
                "             VALUES (:username, :hash, :salt)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .addParameter("username", username)
                    .addParameter("hash", hashed)
                    .addParameter("salt", salt)
                    .executeUpdate();
            //System.out.println("SUCCESSFULLY ADDED.");
        } catch(Sql2oException ex) {
            logger.error("WalkLiveService.createNew: Failed to create new entry", ex);
            throw new UserServiceException("WalkLiveService.createNew: Failed to create new entry", ex);
        }
    }

    //authentication
    public boolean checkPassword(String input, String target) {
        String encrypted = "";

        if (encrypted != target) {
            return false;
        }

        return true;
    }

    /*
     * Finds all users and returns all in user database
     */
    public List<User> findAllUsers() throws UserServiceException {
        try (Connection conn = db.open()) {
            List<User> users = conn.createQuery("SELECT * FROM users")
                    .executeAndFetch(User.class);
            return users;
        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
            throw new UserServiceException("WalkLiveService.findAllUsers: Failed to fetch user entries", ex);
        }
    }

    /*
     * Returns a URI to access the specific users profile info
     */
    public String login(String body) throws UserServiceException, ParseException {
        User user = new Gson().fromJson(body, User.class);

        JSONObject object = (JSONObject) new JSONParser().parse(body);
        String username = object.get("username").toString();
        String password = object.get("password").toString();

        String sql = "SELECT * FROM users WHERE username = :username LIMIT 1";

        try (Connection conn = db.open()) {
            User u = conn.createQuery(sql)
                    .addParameter("username", username)
                    .executeAndFetchFirst(User.class);
            String targetName = u.getUsername();
            String targetPassword = u.getPassword();

            //simple authentication
            if (username.equals(targetName) && password.equals(targetPassword)) {
                //then allow access, and create login instance

                return ""; //return uri for this user aka uri: WalkLive/api/users/:username
                //return session token
            }
        } catch(Sql2oException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", username), ex);
        }
        return "";
    }

    /*
     * Returns a single User being queried
    */
    public User getUser(String body) throws UserServiceException {
        String sql = "SELECT * FROM users WHERE username = :username LIMIT 1";

        try (Connection conn = db.open()) { //find user by username
            User u = conn.createQuery(sql)
                    .addParameter("username", body)
                    .executeAndFetchFirst(User.class);

            return u;
        } catch(Sql2oException ex) {
            logger.error(String.format("WalkLiveService.find: Failed to query database for username: %s", body), ex);
            throw new UserServiceException(String.format("WalkLiveService.find: Failed to query database for username: %s", body), ex);
        }
    }

    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */

    //create a new FriendRequest and store in database
    public void createFriendRequest(String username, String body) throws WalkLiveService.FriendRequestServiceException {
        FriendRequest fr = new Gson().fromJson(body, FriendRequest.class);
        System.out.println(fr.toString());

        String sql = "INSERT INTO friendRequests (sender, recipient, sent_on) " +
                "             VALUES (:sender, :recipient, :sent_on)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .bind(fr)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
            throw new FriendRequestServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
        }
    }

    //get my sent friend requests
    public List<FriendRequest> getOutgoingFriendRequests(String body) throws WalkLiveService.FriendRequestServiceException {
        //checks needed

        try (Connection conn = db.open()) {
            List<FriendRequest> requests = conn.createQuery("SELECT * FROM friendRequests WHERE sender = :sender")
                    .addParameter("sender", body)
                    .executeAndFetch(FriendRequest.class);
            return requests;
        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
            throw new FriendRequestServiceException("WalkLiveService.getOutgoingFriendRequests: Failed to fetch friend requests", ex);
        }
    }

    //delete select sent friend request (cancel request) - extended feature
    public void deleteFriendRequest(String username, String requestId) throws WalkLiveService.FriendRequestServiceException {
        //checks needed

        String sql = "DELETE FROM friendRequests WHERE requestId = :requestId" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql)
                    .addParameter("requestId", requestId)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("WalkLiveService.deleteFriendRequest: Failed to create new entry", ex);
            throw new FriendRequestServiceException("WalkLiveService.deleteFriendRequest: Failed to create new entry", ex);
        }
    }

    //get my received friend requests
    public List<FriendRequest> getIncomingFriendRequests(String body) throws WalkLiveService.FriendRequestServiceException {
        //checks needed

        try (Connection conn = db.open()) {
            List<FriendRequest> requests = conn.createQuery("SELECT * FROM friendRequests WHERE recipient = :recipient")
                    .addParameter("recipient", body)
                    .executeAndFetch(FriendRequest.class);
            return requests;
        } catch (Sql2oException ex) {
            logger.error("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
            throw new FriendRequestServiceException("WalkLiveService.getIncomingFriendRequests: Failed to fetch friend requests", ex);
        }
    }

    //respond to a friend request (update - should be a put) - receives in the body either "accept", or "decline"
    //if accept, then add to friends list for both - FIGURE OUT DETAILS
    //either way, dealt with friend requests should be deleted
    //delete select sent friend request
    public FriendRequest respondToFriendRequest(String username, String requestId, String body) throws WalkLiveService.FriendRequestServiceException {
        return null;
    }

    /**
     * ================================================================
     * Trip Handling
     * ================================================================
     * */

    public String startTrip(String body) throws WalkLiveService.UserServiceException {

        return "";

    }

    public Trip getTrip(String body) throws WalkLiveService.UserServiceException {
        //process body
        Trip thisTrip = new Trip();

        return thisTrip;

    }

    public Trip updateDestination(String body) throws WalkLiveService.UserServiceException {

        //{ tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }
        Trip thisTrip = new Trip();

        return thisTrip;
    }

    public String shareTrip(String body) throws WalkLiveService.UserServiceException {
        // to another user
        return "";

    }

    public String respondTripRequest(String body) throws WalkLiveService.UserServiceException {
        return "";

    }

    //Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
    public Trip addTimePoint(String body) throws WalkLiveService.UserServiceException {
        Trip addToTrip = new Trip();
        return addToTrip;

    }

    public String getTimePoint(String body) throws WalkLiveService.UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        return "";

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

    public Trip getLatestTimePoint(String body) throws WalkLiveService.UserServiceException {
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

        Trip addToTrip = new Trip();
        return addToTrip;
    }


    //=====================EXCEPTIONS============================
    public static class UserServiceException extends Exception {
        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public UserServiceException(String message) {
            super(message);
        }
    }

    public static class FriendRequestServiceException extends Exception {
        public FriendRequestServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public FriendRequestServiceException(String message) {
            super(message);
        }
    }

    public static class TripServiceException extends Exception {
        public TripServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public TripServiceException(String message) {
            super(message);
        }
    }

}
