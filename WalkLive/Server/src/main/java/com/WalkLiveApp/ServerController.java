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
             * Update User Contact PUT
             * ================================================================
             */

            //update user contact information
            put(API_CONTEXT + "/users/:username/contact_info", "application/json", (request, response) -> {
                try {
                    return walkLiveService.updateUserContact(request.params(":username"), request.body());
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
                 } catch (WalkLiveService.DuplicateException e) {
                     logger.error("Request already exists.");
                     response.status(402);
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

             /**
              * ----------------------------------------------------------------------
              * Trip part
              * ----------------------------------------------------------------------
              * */

            //Start Trip
            post(API_CONTEXT+"/trips", "application/json", (request, response) -> {
                try {
                    Trip t = walkLiveService.startTrip(request.body());
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


            //getAllTrip:
            get(API_CONTEXT + "/trips/:username/allTrips", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getAllTrips(request.params(":username"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //getTripHistory:
            get(API_CONTEXT + "/trips/:username/tripHistory", "application/json", (request, response) -> {
                try {
                    return walkLiveService.getTripHistory(request.params(":username"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());



            //updateTrip:
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

            /**
             * ----------------------------------------------------------------------
             * Crime data part
             * better if do it in updateTrip?
             * easier to pass in username or trip id?
             * FE ever store tripid?
             * do we even need trip id?
             *  walkLiveService.respondToFriendRequest(request.params(":username"), request.params(":requestid"), request.params(":response"));

             * ----------------------------------------------------------------------
             * */

            get(API_CONTEXT + "/crime/:curLat/:curLong", "application/json", (request, response) -> {
                try {
                    //return walkLiveService.getDangerZone(request.params(":tripId"),request.body() );
                    return walkLiveService.getDangerZone(request.params(":curLat"), request.params(":curLong"));
                } catch (WalkLiveService.InvalidTargetID e) {
                    logger.error("Invalid user id.");
                    response.status(402);
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());


        }
}
