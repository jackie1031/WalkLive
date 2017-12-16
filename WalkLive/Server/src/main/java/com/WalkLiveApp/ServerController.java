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

            /**
             * ================================================================
             * User Setup Handling
             * ================================================================
             */

            //existing user login
            post(API_CONTEXT + "/users/login", "application/json", (request, response) -> {
                try {
                    return walkLiveService.login(request.body());
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to authenticate user.");
                    response.status(401);
                    //e.getMessage()
                    return "{ reason: NONEXISTENT_USER }";
                }
                //return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //add new user (signup)
            post(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    User u = walkLiveService.createNew(request.body());
                    response.status(201);
                    return u;
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to create new User");
                    response.status(401); //multiple error codes
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //get users
            get(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    return walkLiveService.findAllUsers();
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to fetch user entries");
                    response.status(410);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer()); //get list of users


            //get user information
            get(API_CONTEXT + "/users/:username", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getUser(request.params(":username"));
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to find user.");
                    response.status(404);
                    return "NONEXISTENT_USER";
                }
                //return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            /**
             * ================================================================
             * Emergency Contact PUT
             * ================================================================
             */

            /*
            Method: PUT
            URL: /WalkLive/api/users/[username]
            Content: { emergencyId: [string], emergencyNumber: [string] }
            Failure Response:
            InvalidEmergencyId	Code 406
            InvalidEmergencyNumber	Code 407
            Success Response:	Code 200
            Content: { emergencyId: [string], emergencyNumber: [string] }
            */

            //update emergency contact information
            put(API_CONTEXT + "/users/:username/emergency_info", "application/json", (request, response) -> {
                try {
                    return walkLiveService.updateEmergencyContact(request.params(":username"), request.body());
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to update emergency info for user:" + request.params(":username"));
                    response.status(406);
                    //add 407 response.
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
                 } catch (WalkLiveService.RelationshipServiceException e) {
                     logger.error("Failed to create friend request.");
                     response.status(410);
                 } catch (WalkLiveService.UserServiceException e) {
                     logger.error("Invalid username - does not exist.");
                     response.status(404);
                 } //add whe recipient name isnt valid
                 return Collections.EMPTY_MAP;
             }, new JsonTransformer());

             //get my sent friend requests
             get(API_CONTEXT + "/users/:username/sent_friend_requests", "application/json", (request, response) -> {
                 try {
                     return walkLiveService.getOutgoingFriendRequests(request.params(":username"));
                 } catch (WalkLiveService.RelationshipServiceException e) {
                     logger.error("Failed to find list of sent friend requests.");
                     response.status(401);
                 }
                 return Collections.EMPTY_MAP;
             }, new JsonTransformer());

             //get my received friend requests
             get(API_CONTEXT + "/users/:username/friend_requests","application/json", (request, response) -> {
                 try {
                     return walkLiveService.getIncomingFriendRequests(request.params(":username"));
                 } catch (WalkLiveService.RelationshipServiceException e) {
                     logger.error("Failed to find list of incoming friend requests.");
                     response.status(401);
                 }
                 return Collections.EMPTY_MAP;
             }, new JsonTransformer());

             //update select sent friend request
             put(API_CONTEXT + "/users/:username/friend_requests/:requestid/:response", "application/json", (request, response) -> {
                 try {
                     walkLiveService.respondToFriendRequest(request.params(":username"), request.params(":requestid"), request.params(":response"));
                     response.status(200);
                 } catch (WalkLiveService.UserServiceException e) {
                     logger.error("Failed to find user: %s", request.params(":username"));
                     response.status(401);
                 } catch (WalkLiveService.RelationshipServiceException e) {
                     logger.error("Failed to respond to id: %s", request.params(":requestid"));
                     response.status(400); //either unauthorized (404) or request id is invalid (400), add error status code
                 }
                 return Collections.EMPTY_MAP;
             }, new JsonTransformer());

            //get friend list
            get(API_CONTEXT + "/users/:username/friends", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getFriendList(request.params(":username"));
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to find user: %s", request.params(":username"));
                    response.status(401);
                } catch (WalkLiveService.RelationshipServiceException e) {
                    logger.error("Failed to get friend list for user: %s", request.params(":username"));
                    response.status(410);
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

             /**
              * ----------------------------------------------------------------------
              * Trip part
              * ----------------------------------------------------------------------
              * */


            //add new user (signup)
            post(API_CONTEXT + "/users", "application/json", (request, response) -> {
                try {
                    User u = walkLiveService.createNew(request.body());
                    response.status(201);
                    return u;
                } catch (WalkLiveService.UserServiceException e) {
                    logger.error("Failed to create new User");
                    response.status(401); //multiple error codes
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());



            //Start Trip
            post(API_CONTEXT+"/trips", "application/json", (request, response) -> {
                try {
                    Trip t = walkLiveService.startTrip( request.body());
                    response.status(200);
                    return t;
                    //return walkLiveService.startTrip(request.body());
                } // InvalidDestination     Code 409
                catch (WalkLiveService.InvalidDestination e) {
                    logger.error("Failed to create friend request.");
                    response.status(409);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

//             getTrip:
//             Method: GET
//             URL: /WalkLive/api/<userId>/friendTrips
//             Content: { targetId: <string> } //find by friend’s username
//             Failure Response:
//             InvalidTargetId           Code 402
//             Success Response:         Code 200
//             { tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }



            //get(API_CONTEXT + "/user/:username/trips/:tripId", "application/json", (request, response) -> {

            //get Trip by trip id
            get(API_CONTEXT + "/trips/getById/:tripId", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getTripById(request.params(":tripId"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


            //get Trip by username
            get(API_CONTEXT + "/trips/getByName/:username", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getTripByName(request.params(":username"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


            // end a trip
            put(API_CONTEXT+"/trips/:tripId/endtrip", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    walkLiveService.endTrip(request.params(":tripId"));
                } // InvalidDestination     Code 409
                catch (WalkLiveService.InvalidTargetID e) {
                    response.status(402);
                }

                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


//            getAllTrip:
//            Method: GET
//            URL: /WalkLive/api/trips/[username]/allTrips
//            Content: { tripId: [string], userId[string]}
//            Failure Response:
//            InvalidTargetId	Code 402
//            Success Response:	Code 200
//            Content: { <trip 1>, <trip 2>, <trip 3>, ... }


            get(API_CONTEXT + "/trips/:username/allTrips", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getAllTrips(request.params(":username"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


//            getTripHistory:
//
//            Method: GET
//            URL: /WalkLive/api/trips/[username]/tripHistory
//            Content: {}
//            Failure Response:
//            InvalidTargetId	Code 402
//            Success Response:	Code 200
//            Content: { <trip 1>, <trip 2>, <trip 3>, ... }

            get(API_CONTEXT + "/trips/:username/tripHistory", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getTripHistory(request.params(":username"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


//            updateTrip:
//
//            Method: PUT
//            URL: /WalkLive/api/trips/:tripId/update
//            Content: {curLong:[double],curLat:[double],timeSpent:[String]}
//            Failure Response:
//            InvalidTargetId	Code 402
//            Success Response:	Code 200
//            Content: {}

            put(API_CONTEXT+"/trips/:tripId/update", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    walkLiveService.updateTrip(request.params(":tripId"),request.body());
                } // InvalidDestination     Code 409
                catch (WalkLiveService.InvalidTargetID e) {
                    response.status(402);
                }

                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


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
