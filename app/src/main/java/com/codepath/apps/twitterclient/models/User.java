package com.codepath.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by devin on 11/3/15.
 */
public class User {
    private String name;
    private Long id;
    private String screenName;
    private String profileImageUrl;
    private String profileImageUrlHttps;

    // Returns a User given the expected JSON
    public static User fromJSON(JSONObject obj) {
        try {
            User u = new User();
            u.name = obj.getString("name");
            u.id = obj.getLong("id");
            u.screenName = obj.getString("screen_name");
            u.profileImageUrl = obj.getString("profile_image_url");
            u.profileImageUrlHttps = obj.getString("profile_image_url_https");
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileImageUrlHttps() {
        return profileImageUrlHttps;
    }
}
