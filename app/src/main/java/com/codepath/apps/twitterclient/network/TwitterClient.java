package com.codepath.apps.twitterclient.network;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitterclient.config.TwitterConfig;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = TwitterConfig.TWITTER_KEY;
	public static final String REST_CONSUMER_SECRET = TwitterConfig.TWITTER_SECRET;
    public static final String REST_CALLBACK_URL = "oauth://cptwitterclient";

    public final int DEFAULT_QTY = 50;

    public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    // user

    public void getLoggedInUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }


    public void getUser(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        Log.d("client", "GET: " + apiUrl);
        Log.d("client", "Params: " + params.toString());
        getClient().get(apiUrl, params, handler);
    }

    // home timeline

    public void getNewTimelineEntries(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        getHomeTimeline(params, handler);
    }

    public void getNewTimelineEntries(AsyncHttpResponseHandler handler, long since_id) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        params.put("since_id", since_id);
        getHomeTimeline(params, handler);
    }

    public void getOlderTimelineEntries(AsyncHttpResponseHandler handler, long max_id) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        params.put("max_id", max_id);
        getHomeTimeline(params, handler);
    }

    // user timeline

    public void getNewUserTimelineEntries(User user, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("screen_name", user.getScreenName());
        params.put("count", DEFAULT_QTY);
        getUserTimeline(params, handler);
    }

    public void getNewUserTimelineEntries(User user, AsyncHttpResponseHandler handler, long since_id) {
        RequestParams params = new RequestParams();
        params.put("screen_name", user.getScreenName());
        params.put("count", DEFAULT_QTY);
        params.put("since_id", since_id);
        getUserTimeline(params, handler);
    }

    public void getOlderUserTimelineEntries(User user, AsyncHttpResponseHandler handler, long max_id) {
        RequestParams params = new RequestParams();
        params.put("screen_name", user.getScreenName());
        params.put("count", DEFAULT_QTY);
        params.put("max_id", max_id);
        getUserTimeline(params, handler);
    }

    // mentions

    public void getOlderMentionsEntries(JsonHttpResponseHandler handler, long max_id) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        params.put("max_id", max_id);
        getMentionsTimeline(params, handler);
    }

    public void getNewMentionEntries(JsonHttpResponseHandler handler, long since_id) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        params.put("since_id", since_id);
        getMentionsTimeline(params, handler);
    }

    public void getNewMentionsEntries(JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_QTY);
        getMentionsTimeline(params, handler);
    }

    // post new tweet

    public void postNewTweet(String status, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();

        params.put("status", status);

        Log.d("client", "POST: " + apiUrl);
        Log.d("client", "Params: " + params.toString());
        getClient().post(apiUrl, params, handler);
    }

    // private

    private void getMentionsTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        Log.d("client", "GET: " + apiUrl);
        Log.d("client", "Params: " + params.toString());
        getClient().get(apiUrl, params, handler);
    }

    private void getHomeTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        Log.d("client", "GET: " + apiUrl);
        Log.d("client", "Params: " + params.toString());
        getClient().get(apiUrl, params, handler);
    }

    private void getUserTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        Log.d("client", "GET: " + apiUrl);
        Log.d("client", "Params: " + params.toString());
        getClient().get(apiUrl, params, handler);
    }
}