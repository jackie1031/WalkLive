package com.WalkLiveApp;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class UserController {

        private static final String API_CONTEXT = "/WalkLive/api";

        private final UserService userService;

        private final Logger logger = LoggerFactory.getLogger(UserController.class);

        public UserController(UserService appService) {
            this.userService = userService;
            setupEndpoints();
        }

        private void setupEndpoints() {
            //add new user (signup)
            post(API_CONTEXT + "/user", "application/json", (request, response) -> {
                try {
                    userService.createNew(request.body());
                    return response.status(201);
                } catch (userService.UserServiceException e) {
                    logger.error("Failed to create new User");
                }
                return Collections.EMPTY_MAP;
            }, new JsonTransformer());

            //existing user login
            get(API_CONTEXT + "/user", "application/json", (request, response) -> {
                try {
                    return userService.login(request.body());
                } catch (userService.AuthenticationException e) {
                    logger.error("Failed to authenticate user.");
                    response.status(404);
                    return Collections.EMPTY_MAP;
                } //catch (HareAndHoundsService.HareAndHoundsUserExistsException e) {
                // 	logger.error("Failed to add new user: second player already joined");
                // 	response.status(410);
                // }
                //return Collections.EMPTY_MAP;
            }, new JsonTransformer());
        }
    }
}
