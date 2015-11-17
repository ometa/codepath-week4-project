package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by devin on 11/13/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Override
    protected void initialLoadWithInternet(JsonHttpResponseHandler handler) {
        // we have internet, clear out old tweets
        Tweet.deleteAllBy(User.getCurrentUser());
        client.getNewTimelineEntries(handler);
    }

    @Override
    protected void initialLoadNoInternet(TweetsArrayAdapter aTweets) {
        aTweets.addAll(Tweet.getAllBy(User.getCurrentUser()));
        aTweets.notifyDataSetChanged();
    }

    @Override
    protected void onSwipeUp(JsonHttpResponseHandler handler) {
        client.getNewTimelineEntries(handler, getNewest_id());
    }

    @Override
    protected void onSwipeDown(JsonHttpResponseHandler handler) {
        client.getOlderTimelineEntries(handler, getOldest_id());
    }
}