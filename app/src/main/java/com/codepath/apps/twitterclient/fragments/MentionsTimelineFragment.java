package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by devin on 11/13/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Override
    protected void initialLoadWithInternet(JsonHttpResponseHandler handler) {
        client.getNewMentionsEntries(handler);
    }

    @Override
    protected void onSwipeUp(JsonHttpResponseHandler handler) {
        client.getNewMentionEntries(handler, getNewest_id());
    }

    @Override
    protected void onSwipeDown(JsonHttpResponseHandler handler) {
        client.getOlderMentionsEntries(handler, getOldest_id());
    }
}
