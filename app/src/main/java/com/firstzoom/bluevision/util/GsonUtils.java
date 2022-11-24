package com.firstzoom.bluevision.util;

import com.firstzoom.bluevision.model.User;
import com.google.gson.Gson;

public class GsonUtils {


    public static String getGsonObject(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }
    public static User getModelObjectUser(String user) {
        Gson gson = new Gson();
        return gson.fromJson(user, User.class);
    }

}
