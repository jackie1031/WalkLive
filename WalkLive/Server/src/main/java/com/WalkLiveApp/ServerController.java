package com.WalkLiveApp;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import static spark.Spark.*;

public class ServerController {

        private static final String API_CONTEXT = "/WalkLive/api";

        private final WalkLiveService walkLiveService;

        private final Logger logger = LoggerFactory.getLogger(ServerController.class);

        public ServerController(WalkLiveService walkLiveService) {
            this.walkLiveService = walkLiveService;
            setupEndpoints();
            logger.info("finished setting up endpoints");

        }

        private void setupEndpoints() {

            before((request, response) -> {
                response.header("Access-Control-Allow-Origin", "*");
            });

//            /** lallaallala
//             * tests
//             */
//            get(API_CONTEXT + "/tests", "application/json", (request, response) -> {
//                try {
//                    return walkLiveService.test();
//                } catch (WalkLiveService.UserServiceException e) {
//                    logger.error("Failed to fetch user entries");
//                }
//                return Collections.EMPTY_MAP;
//            }, new JsonTransformer());
//
            /**
             * ================================================================
             * User Setup Handling
             * ================================================================
             */

            //get list of users
            get(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    return walkLiveService.findAllUsers();
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to fetch user entries");
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer()); //get list of users

            //add new user (signup)
            post(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    User u = walkLiveService.createNew(request.body());
                    response.status(201);
                    return u;
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to create new User");
                    response.status(401);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //existing user login
            post(API_CONTEXT + "/users/login", "application/json", (request, response) -> {
                try {
                    return walkLiveService.login(request.body());
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to authenticate user.");
                    response.status(404);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //get user information
            get(API_CONTEXT + "/users/:username", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getUser(request.params(":username"));
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed find user.");
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


            /**
             * ================================================================
             * Friend Request Handling
             * ================================================================
             */

             //Make a friend request - receives sender username (from) and recipient username (to) and sent time in request body
             //returns 201 at successful creation
             post(API_CONTEXT + "/users/:username/friend_requests", "application/json", (request, response) -> {
                 try {
                     walkLiveService.createFriendRequest(request.params(":username"), request.body());
                     response.status(201);
                 } catch (WalkLiveService.FriendRequestServiceException e) {
                     logger.error("Failed to create friend request.");
                     response.status(404);
                 }
                 return Collections.EMPTY_MAP;
             }, new JsonTransformer());

//             //get my sent friend requests
//             get(API_CONTEXT + "/users/:username/friend_requests", "application/json", (request, response) -> {
//                 try {
//                     return walkLiveService.getOutgoingFriendRequests(request.params(":username"));
//                 } catch (WalkLiveService.FriendRequestServiceException e) {
//                     logger.error("Failed to find list of sent friend requests.");
//                 }
//                 return Collections.EMPTY_MAP;
//             }, new JsonTransformer());

//             //delete select sent friend request
//             delete(API_CONTEXT + "/users/:username/friend_requests/:requestid", "application/json", (request, response) -> {
//                 try {
//                     walkLiveService.deleteFriendRequest(request.params(":username"), request.params(":requestid"));
//                     response.status(200);
//                 } catch (WalkLiveService.FriendRequestServiceException e) {
//                     logger.error("Failed to delete friend request with id: %s", request.params(":requestid"));
//                     response.status(500);
//                 }
//                 return Collections.EMPTY_MAP;
//             }, new JsonTransformer());

//             //get my received friend requests
//             get(API_CONTEXT + "/users/:username/my_requests","application/json", (request, response) -> {
//                 try {
//                     return walkLiveService.getIncomingFriendRequests(request.params(":username"));
//                 } catch (WalkLiveService.FriendRequestServiceException e) {
//                     logger.error("Failed to find list of incoming friend requests.");
//                 }
//                 return Collections.EMPTY_MAP;
//             }, new JsonTransformer());

//             //respond to a friend request (update - should be a put) - receives in the body either "accept", or "decline"
//             //if accept, then add to friends list for both - FIGURE OUT DETAILS
//             //either way, dealt with friend requests should be deleted
//             //delete select sent friend request
//             put(API_CONTEXT + "/users/:username/friend_requests/:requestid", "application/json", (request, response) -> {
//                 try {
//                     return walkLiveService.respondToFriendRequest(request.params(":username"), request.params(":requestid"), request.body());
//                 } catch (WalkLiveService.FriendRequestServiceException e) {
//                     logger.error("Failed to respond to friendRequest with id: %s", request.params(":requestid"));
//                     response.status(500);
//                     return Collections.EMPTY_MAP;
//                 }
//             }, new JsonTransformer());

            //add new user (signup)
            post(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    User u = walkLiveService.createNew(request.body());
                    response.status(201);
                    return u;
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to create new User");
                    response.status(401);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

//             /**
//              * ----------------------------------------------------------------------
//              * Trip part
//              * ----------------------------------------------------------------------
//              * */
//             //Start Trip
//             post(API_CONTEXT+"/user", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.startTrip(request.body());
//                 } // InvalidDestination     Code 409
//                 catch (WalkLiveService.InvalidDestination e) {
//                     response.status(409);
//                 }
//                 catch (WalkLiveService.UserServiceException e) {
//                     logger.error("Failed to create new User");
//                     response.status(401);
//                 }
//                 return Collections.EMPTY_MAP;
//             }, new JsonTransformer());

// //            getTrip:
// //            Method: GET
// //            URL: /WalkLive/api/<userId>/friendTrips
// //            Content: { targetId: <string> } //find by friend’s username
// //            Failure Response:
// //            InvalidTargetId           Code 402
// //            Success Response:         Code 200
// //            { tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }

//             //get Trip
//             get(API_CONTEXT + "/user/friendTrips", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.getTrip(request.params(":destination"));
//                 } catch (WalkLiveService.InvalidTargetID e) {
//                     logger.error("Invalid user id.");
//                     response.status(402);
//                     return Collections.EMPTY_MAP;
//                 }
//             }, new JsonTransformer());


// //            updateTripDestination:
// //            Method: PUT
// //            URL: /WalkLive/api/<userId>
// //                    Content: { destination: <string> }
// //            Failure Response:
// //            InvalidDestination        Code 409
// //            Success Response:         Code 200
// //            { tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }

//             //Update trip destination
//             put(API_CONTEXT+"/user", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.updateDestination(request.params(":destination"));
//                 } catch (WalkLiveService.UserServiceException e) {
//                 logger.error("Invalid destination.");
//                 response.status(409);
//                 return Collections.EMPTY_MAP;
//             }
//             }, new JsonTransformer());

// //            shareTrip:
// //            Method: PUT
// //            URL: /WalkLive/api/<userId>
// //                    Content: { targetId: <string>, shareTime: <string> }
// //            Failure Response:
// //            InvalidTargetId           Code 402
// //            Success Response:         Code 200
// //            { }
//             put(API_CONTEXT+"/user", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.shareTrip(request.body());
//                 } catch (WalkLiveService.UserServiceException e) {
//                     logger.error("Invalid target id.");
//                     response.status(402);
//                     return Collections.EMPTY_MAP;
//                 }
//             }, new JsonTransformer());


// //            respondTripRequest:
// //            Method: PUT
// //            URL: /WalkLive/api/<userId>/shareRequests
// //            Content: { sourceId: <string>, response: <boolean> }
// //            Failure Response:
// //            InvalidTargetId           Code 402
// //            InvalidResponse       Code 408
// //            ResponseNotFound      Code 411
// //            Success Response:         Code 200
// //            { }
//             put(API_CONTEXT+"/user", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.respondTripRequest(request.body());
//                 } catch (WalkLiveService.UserServiceException e) {
//                     logger.error("Invalid target id.");
//                     response.status(402);
//                     return Collections.EMPTY_MAP;
//                 }
// //                //more exceptions
// //                catch (UserServiceExceptionInvalidResponse e) {
// //                    logger.error("Invalid response.");
// //                    response.status(408);
// //                    return Collections.EMPTY_MAP;
// //                } catch (UserServiceException e) {
// //                    logger.error("Response not found.");
// //                    response.status(411);
// //                    return Collections.EMPTY_MAP;
// //                }
//             }, new JsonTransformer());

//                 /**
// //            addTimePoint:
// //            Method: POST
// //            URL: /WalkLive/api/<userId>/timepoints
// //            Content: { tripId: <int>, time: <string>, location: <string>, coordinates: <float> }
// //            Failure Response:
// //            InvalidTripId                     Code 412
// //            InvalidUserId                     Code 402
// //            MismatchedLocationAndCoordinates          Code 420
// //            Success Response:                     Code 200
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//             //addTimePoint
//             post(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.addTimePoint(request.body());
//                 } // InvalidDestination     Code 409
//                 catch (WalkLiveService.UserServiceException e) {
//                     logger.error("Invalid trip id.");
//                     response.status(412);
//                     return Collections.EMPTY_MAP;
//                 }
// //                catch (UserServiceException e) {
// //                    logger.error("Invalid user id.");
// //                    response.status(402);
// //                    return Collections.EMPTY_MAP;
// //                }
// //                catch (UserServiceException e) {
// //                    logger.error("Mismatched Location And Coordinates.");
// //                    response.status(420);
// //                    return Collections.EMPTY_MAP;
// //                }

//             }, new JsonTransformer());
//             **/

// //            getTimePoint:
// //            Method: GET
// //            URL: /WalkLive/api/<userId>/timepoints
// //            Content: { tripId: <string>, timepointId: <string> }
// //            Failure Response:
// //            InvalidTripId                     Code 412
// //            InvalidTimePointId                    Code 413
// //            Success Response:                     Code 200
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
//             get(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.getTimePoint(request.body());
//                 } // InvalidDestination     Code 409
//                 catch (WalkLiveService.UserServiceException e) {
//                     logger.error("Invalid trip id.");
//                     response.status(412);
//                     return Collections.EMPTY_MAP;
//                 }
// //                catch (UserServiceException e) {
// //                    logger.error("Invalid time point id.");
// //                    response.status(413);
// //                    return Collections.EMPTY_MAP;
// //                }
//             }, new JsonTransformer());

// //            getLatestTimePoint:
// //            Method: GET
// //            URL: /WalkLive/api/<userId>/timepoints
// //            Content: { tripId: <string> }
// //            Failure Response:
// //            NoTripFound                       Code 414
// //            NoTimePointFound                  Code 415
// //            Success Response:                     Code 200
// //            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

//             get(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
//                 try {
//                     response.status(200);
//                     return walkLiveService.getLatestTimePoint(request.body());
//                 } // InvalidDestination     Code 409
//                 catch (WalkLiveService.UserServiceException e) {
//                     logger.error("No trip found.");
//                     response.status(414);
//                     return Collections.EMPTY_MAP;
//                 }
// //                catch (UserServiceException e) {
// //                    logger.error("No Time Point Found.");
// //                    response.status(415);
// //                    return Collections.EMPTY_MAP;
// //                }
//             }, new JsonTransformer());

//              // get linkIDs to Avoid
//             get(API_CONTEXT + "/getdangerzone", "application/json", (request, response) -> {
//                 try {
//                     double fromLat = Double.parseDouble(request.queryParams("fromLat"));
//                     double fromLng = Double.parseDouble(request.queryParams("fromLng"));
//                     double toLat = Double.parseDouble(request.queryParams("toLat"));
//                     double toLng = Double.parseDouble(request.queryParams("toLng"));
//                     int timeOfDay = Integer.parseInt(request.queryParams("timeOfDay"));
//                     Coordinate from = new Coordinate(fromLat, fromLng);
//                     Coordinate to = new Coordinate(toLat, toLng);
//                     Coordinate.sortAndExpand(from, to);
//                     response.status(200);
//                     return walkLiveService.getDangerZone(from, to, "Table");
//                 } catch (Exception e) {
//                     logger.info("Invalid request", e);
//                     response.status(400);
//                     return Collections.EMPTY_MAP;
//                 }
//             }, new JsonTransformer());


//             //CANCEL TRIP
//             get(API_CONTEXT + "/cancel", "application/json", (request, response) -> {
//                 try {
//                     double fromLat = Double.parseDouble(request.queryParams("fromLat"));
//                     double fromLng = Double.parseDouble(request.queryParams("fromLng"));
//                     double toLat = Double.parseDouble(request.queryParams("toLat"));
//                     double toLng = Double.parseDouble(request.queryParams("toLng"));
//                     int timeOfDay = Integer.parseInt(request.queryParams("timeOfDay"));
//                     Coordinate from = new Coordinate(fromLat, fromLng);
//                     Coordinate to = new Coordinate(toLat, toLng);
//                     Coordinate.sortAndExpand(from, to);
//                     response.status(200);
//                     return walkLiveService.getDangerZone(from, to, "Table");
//                 } catch (Exception e) {
//                     logger.info("Invalid request", e);
//                     response.status(400);
//                     return Collections.EMPTY_MAP;
//                 }
//             }, new JsonTransformer());

        }
}
