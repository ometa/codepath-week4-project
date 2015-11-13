package com.codepath.apps.twitterclient.lib;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;

/**
 * Created by devin on 11/13/15.
 */
public class Database {
    public static void reset() {
        Tweet.deleteAll();
        User.deleteAll();
    }
}
