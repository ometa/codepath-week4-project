package com.codepath.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.lib.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
@Table(name = "items")
public class Tweet extends Model {
	// Define table fields
	@Column(name = "name")
	private String name;


    private String body;
    private Long uid;
    private User user;
    private String createdAt;

    public Tweet() {
		super();
	}

    // todo: change.

	// Record Finders
	public static Tweet byId(long id) {
		return new Select().from(Tweet.class).where("id = ?", id).executeSingle();
	}

	public static List<Tweet> recentItems() {
		return new Select().from(Tweet.class).orderBy("id DESC").limit("300").execute();
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
            tweet.uid = JsonHelper.findLongOrZero("uid", obj);
            tweet.createdAt = JsonHelper.findOrBlank("created_at", obj);
            tweet.user = User.fromJSON(obj.getJSONObject("user"));
            return tweet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getName() {
        return name;
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

    public String getCreatedAt() {
        return createdAt;
    }
}
