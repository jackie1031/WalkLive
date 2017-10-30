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

        public ServerController(ServerService appService) {
            this.ServerService = ServerService;
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
                    ServerService.createNew(request.body());
                    return response.status(201);
                } catch (ServerService.ServerServiceExceptionException e) {
                    logger.error("Failed to create new User");
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //existing user login
            get(API_CONTEXT + "/user", "application/json", (request, response) -> {
                try {
                    return ServerService.login(request.body());
                } catch (ServerService.AuthenticationException e) {
                    logger.error("Failed to authenticate user.");
                    response.status(404);
                    return Collections.EMPTY_MAP;
                } //catch (HareAndHoundsService.HareAndHoundsUserExistsException e) {
                // 	logger.error("Failed to add new user: second player already joined");
                // 	response.status(410);
                // }
                //return Collections.EMPTY_MAP;
            }, new JsonTransformer());


            //Get method to start a new game.
            post(API_CONTEXT+"/user", "application/json", (request, response) -> {
                try {
                    response.status(201);
                }catch (ServerService.ServerServiceInvalidPieceType ex) {
                    response.status(409);
                    return Collections.EMPTY_MAP;
                }
                return new ()
            }, new JsonTransformer());

            response.status(200);
            gameService.move(request.params(":id"), request.body());

            startTrip:
            Method: POST
            URL: /WalkLive/api/<userId>
                    Content: { startTime: <string>, destination: <string> }
            Failure Response:
            InvalidDestination		Code 409
            Success Response:			Code 200
            {}

        }
}

