package com.codepath.apps.twitterclient;

import android.content.Context;

import com.codepath.apps.twitterclient.lib.PreferenceManager;
import com.codepath.apps.twitterclient.network.TwitterClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {

        // for development
//        this.deleteDatabase("RestClient.db");

		super.onCreate();
		TwitterApplication.context = this;
		PreferenceManager.initializeInstance(context);
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}