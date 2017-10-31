package com.WalkLiveApp;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


import static spark.Spark.*;

public class ServerController {

        private static final String API_CONTEXT = "/WalkLive/api";

        private final ServerService ServerService;

        private final Logger logger = LoggerFactory.getLogger(ServerController.class);

        public ServerController(ServerService serverService) {
            this.ServerService = serverService;
            setupEndpoints();
        }

        private void setupEndpoints() {

            before((request, response) -> {
                response.header("Access-Control-Allow-Origin", "*");
            });

//            /**
//             * Retrieve avoidID
//             */
//            get(API_CONTEXT + "/avoidLinkIds", "application/json", (request, response) -> {
//                try {
//                    double fromLat = Double.parseDouble(request.queryParams("fromLat"));
//                    double fromLng = Double.parseDouble(request.queryParams("fromLng"));
//                    double toLat = Double.parseDouble(request.queryParams("toLat"));
//                    double toLng = Double.parseDouble(request.queryParams("toLng"));
//                    //int timeOfDay = Integer.parseInt(request.queryParams("timeOfDay"));
//                    Coordinate from = new Coordinate(fromLat, fromLng);
//                    Coordinate to = new Coordinate(toLat, toLng);
//                    Coordinate.sortAndExpand(from, to);
//                    response.status(200);
//                    return ServerService.getAvoidLinkIds(from, to, "grids");
//                } catch (Exception e) {
//                    logger.info("Invalid request", e);
//                    response.status(400);
//                    return Collections.EMPTY_MAP;
//                }
//            }, new JsonTransformer());



            //add new user (signup)
            post(API_CONTEXT + "/user", "application/json", (request, response) -> {
                try {
                    response.status(201);
                    ServerService.createNew(request.body());
                } catch (UserServiceException e) {
                    logger.error("Failed to create new User");
                    return Collections.EMPTY_MAP;
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //existing user login
            get(API_CONTEXT + "/user", "application/json", (request, response) -> {
                try {
                    return ServerService.login(request.body());
                } catch (UserServiceException e) {
                    logger.error("Failed to authenticate user.");
                    response.status(404);
                    return Collections.EMPTY_MAP;
                } //catch (HareAndHoundsService.HareAndHoundsUserExistsException e) {
                // 	logger.error("Failed to add new user: second player already joined");
                // 	response.status(410);
                // }
                //return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            /** Start the trip part -------------------------------**/
            //Start Trip
            post(API_CONTEXT+"/user", "application/json", (request, response) -> {
                try {
                    response.status(201);
                    return ServerService.startTrip(request.body());
                } // InvalidDestination		Code 409
                catch (UserServiceException e) {
                    response.status(409);
                }

                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

//            getTrip:
//            Method: GET
//            URL: /WalkLive/api/<userId>/friendTrips
//            Content: { targetId: <string> } //find by friend’s username
//            Failure Response:
//            InvalidTargetId			Code 402
//            Success Response:			Code 200
//            { tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }

            //get Trip
            get(API_CONTEXT + "/user/friendTrips", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.getTrip(request.params(":destination"));
                } catch (UserServiceException e) {
                    logger.error("Invalid user id.");
                    response.status(404);
                    return Collections.EMPTY_MAP;
                }
            }, new JsonTransformer());


//            updateTripDestination:
//            Method: PUT
//            URL: /WalkLive/api/<userId>
//                    Content: { destination: <string> }
//            Failure Response:
//            InvalidDestination		Code 409
//            Success Response:			Code 200
//            { tripId: <string>, startTime: <string>, endTime: <string>, destination: <string>, complete: <boolean> }

            //Update trip destination
            put(API_CONTEXT+"/user", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.updateDestination(request.params(":destination"));
                } catch (UserServiceException e) {
                logger.error("Invalid destination.");
                response.status(409);
                return Collections.EMPTY_MAP;
            }
            }, new JsonTransformer());

//            shareTrip:
//            Method: PUT
//            URL: /WalkLive/api/<userId>
//                    Content: { targetId: <string>, shareTime: <string> }
//            Failure Response:
//            InvalidTargetId			Code 402
//            Success Response:			Code 200
//            { }
            put(API_CONTEXT+"/user", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.shareTrip(request.body());
                } catch (UserServiceException e) {
                    logger.error("Invalid target id.");
                    response.status(402);
                    return Collections.EMPTY_MAP;
                }
            }, new JsonTransformer());




//            respondTripRequest:
//            Method: PUT
//            URL: /WalkLive/api/<userId>/shareRequests
//            Content: { sourceId: <string>, response: <boolean> }
//            Failure Response:
//            InvalidTargetId			Code 402
//            InvalidResponse		Code 408
//            ResponseNotFound		Code 411
//            Success Response:			Code 200
//            { }
            put(API_CONTEXT+"/user", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.respondTripRequest(request.body());
                } catch (UserServiceException e) {
                    logger.error("Invalid target id.");
                    response.status(402);
                    return Collections.EMPTY_MAP;
                }
//                //more exceptions
//                catch (UserServiceExceptionInvalidResponse e) {
//                    logger.error("Invalid response.");
//                    response.status(408);
//                    return Collections.EMPTY_MAP;
//                } catch (UserServiceException e) {
//                    logger.error("Response not found.");
//                    response.status(411);
//                    return Collections.EMPTY_MAP;
//                }
            }, new JsonTransformer());


//            addTimePoint:
//            Method: POST
//            URL: /WalkLive/api/<userId>/timepoints
//            Content: { tripId: <int>, time: <string>, location: <string>, coordinates: <float> }
//            Failure Response:
//            InvalidTripId						Code 412
//            InvalidUserId						Code 402
//            MismatchedLocationAndCoordinates			Code 420
//            Success Response:						Code 200
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
            //addTimePoint
            post(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.addTimePoint(request.body());
                } // InvalidDestination		Code 409
                catch (UserServiceException e) {
                    logger.error("Invalid trip id.");
                    response.status(412);
                    return Collections.EMPTY_MAP;
                }
//                catch (UserServiceException e) {
//                    logger.error("Invalid user id.");
//                    response.status(402);
//                    return Collections.EMPTY_MAP;
//                }
//                catch (UserServiceException e) {
//                    logger.error("Mismatched Location And Coordinates.");
//                    response.status(420);
//                    return Collections.EMPTY_MAP;
//                }

            }, new JsonTransformer());

//            getTimePoint:
//            Method: GET
//            URL: /WalkLive/api/<userId>/timepoints
//            Content: { tripId: <string>, timepointId: <string> }
//            Failure Response:
//            InvalidTripId						Code 412
//            InvalidTimePointId					Code 413
//            Success Response:						Code 200
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }
            get(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.getTimePoint(request.body());
                } // InvalidDestination		Code 409
                catch (UserServiceException e) {
                    logger.error("Invalid trip id.");
                    response.status(412);
                    return Collections.EMPTY_MAP;
                }
//                catch (UserServiceException e) {
//                    logger.error("Invalid time point id.");
//                    response.status(413);
//                    return Collections.EMPTY_MAP;
//                }
            }, new JsonTransformer());

//            getLatestTimePoint:
//            Method: GET
//            URL: /WalkLive/api/<userId>/timepoints
//            Content: { tripId: <string> }
//            Failure Response:
//            NoTripFound						Code 414
//            NoTimePointFound					Code 415
//            Success Response:						Code 200
//            Content: { tripId: <string>, userId: <string>, timepointId: <string>, location: <string>, coordinates: <float> }

            get(API_CONTEXT+"/user/timepoints", "application/json", (request, response) -> {
                try {
                    response.status(200);
                    return ServerService.getLatestTimePoint(request.body());
                } // InvalidDestination		Code 409
                catch (UserServiceException e) {
                    logger.error("No trip found.");
                    response.status(414);
                    return Collections.EMPTY_MAP;
                }
//                catch (UserServiceException e) {
//                    logger.error("No Time Point Found.");
//                    response.status(415);
//                    return Collections.EMPTY_MAP;
//                }
            }, new JsonTransformer());

        }
}

