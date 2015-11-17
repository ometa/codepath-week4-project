package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.lib.DatePresenter;
import com.codepath.apps.twitterclient.lib.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
@Table(name = "Tweets")
public class Tweet extends Model {

    final String TWITTER_DATE_FORMAT ="EEE MMM dd HH:mm:ss ZZZZ yyyy";   // Sat Nov 07 17:23:59 +0000 2015

    @Column(name = "Body")
    private String body;

    @Column(name = "Uid")
    private Long uid;

    @Column(name = "User")
    private User user;

    @Column(name = "CreatedAt")
    private String createdAt;

    public Tweet() {
		super();
	}

    public String getTimeAgo() {
        String result = "";
        try {
            SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT);
            sf.setLenient(true);

            Date date = sf.parse(createdAt);
            result = DatePresenter.shortRelativeElapsedFrom(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Tweet> getAllBy(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return new Select().from(Tweet.class).where("User = ?", user.getId()).orderBy("uid DESC").execute();
    }

    public static void deleteAllBy(User user) {
        for (Tweet t : Tweet.getAllBy(user)) {
            t.delete();
        }
    }

    public static List<Tweet> getAll() {
        return new Select().all().from(Tweet.class).orderBy("uid DESC").execute();
    }

    public static void deleteAll() {
        new Delete().from(Tweet.class).execute();
    }

    // Decodes array of tweet json objects into objects
    public static List<Tweet> fromJson(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Tweet tweet = Tweet.fromJson(tweetJson);

            if (tweet != null) {
                tweets.add(tweet);
            }
        }
        return tweets;
    }


    // Returns a Tweet given the expected JSON
    public static Tweet fromJson(JSONObject obj) {
        try {
            Tweet tweet = new Tweet();
            tweet.body = JsonHelper.findOrBlank("text", obj);
            tweet.uid = JsonHelper.findLongOrZero("id", obj);
            tweet.createdAt = JsonHelper.findOrBlank("created_at", obj);
            tweet.user = User.fromJSON(obj.getJSONObject("user"));
            tweet.save();
            return tweet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long oldestIdFrom(JSONArray json) {
        try {
            if (json.length() == 0) {
                return 0;
            }
            JSONObject last = json.getJSONObject(json.length() - 1);
            return last.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long newestIdFrom(JSONArray json) {
        if (json.length() == 0) {
            return 0;
        }
        try {
            JSONObject first = json.getJSONObject(0);
            return first.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }



    public String getBody() {
        return body;
    }

    public Long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }
}
