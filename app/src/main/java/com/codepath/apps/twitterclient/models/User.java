package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by devin on 11/3/15.
 */
@Table(name = "Users")
public class User extends Model {

    @Column(name = "Name")
    private String name;

    @Column(name = "Uid")
    private Long uid;

    @Column(name = "ScreenName")
    private String screenName;

    @Column(name = "ProfileImageUrl")
    private String profileImageUrl;

    @Column(name = "ProfileImageUrlHttps")
    private String profileImageUrlHttps;

    // This method is optional, does not affect the foreign key creation.
    public List<Tweet> tweets() {
        return getMany(Tweet.class, "User");
    }

    public static void deleteAll() {
        new Delete().from(User.class).execute();
    }

    // Returns a User given the expected JSON
    public static User fromJSON(JSONObject obj) {
        try {
            User u = new User();
            u.name = obj.getString("name");
            u.uid = obj.getLong("id");
            u.screenName = obj.getString("screen_name");
            u.profileImageUrl = obj.getString("profile_image_url");
            u.profileImageUrlHttps = obj.getString("profile_image_url_https");
            u.save();
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public Long getUid() {
        return uid;
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
