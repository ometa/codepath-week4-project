package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;

import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.network.TwitterClient;

/**
 * Created by devin on 11/14/15.
 */
public class LoggedInUserFragment extends UserDetailsFragment {

    protected TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }
}
