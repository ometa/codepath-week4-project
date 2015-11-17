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
 * Created by devin on 11/16/15.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private User user;

    public UserTimelineFragment() {}

    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment frag = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        user = getArguments().getParcelable("user");
    }

    @Override
    protected void initialLoadWithInternet(JsonHttpResponseHandler handler) {
        client.getNewUserTimelineEntries(user, handler);
    }

    @Override
    protected void initialLoadNoInternet(TweetsArrayAdapter aTweets) {
        aTweets.addAll(Tweet.getAllBy(user));
        aTweets.notifyDataSetChanged();
    }

    @Override
    protected void onSwipeUp(JsonHttpResponseHandler handler) {
        client.getNewUserTimelineEntries(user, handler, getNewest_id());
    }

    @Override
    protected void onSwipeDown(JsonHttpResponseHandler handler) {
        client.getOlderUserTimelineEntries(user, handler, getOldest_id());
    }
}
