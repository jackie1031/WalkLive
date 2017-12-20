package com.WalkLiveApp;

import com.google.gson.Gson;
import spark.Response;
import spark.ResponseTransformer;

import java.util.HashMap;

/**
 * json transformer class
 */
public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    /**
     * render json to hashmap
     * @param model: object
     * @return: string
     */
    @Override
    public String render(Object model) {
        if (model instanceof Response) {
            return gson.toJson(new HashMap<>());
        }
        return gson.toJson(model);
    }

}
