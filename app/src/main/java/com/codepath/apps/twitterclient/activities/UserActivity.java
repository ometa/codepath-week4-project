package com.codepath.apps.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.fragments.UserDetailsFragment;
import com.codepath.apps.twitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.twitterclient.lib.NetworkHelper;
import com.codepath.apps.twitterclient.lib.PreferenceManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

// This rendering gets a screenname from the intent (or user prefs),
// uses the screen name to look up the User from the API or from the DB,
// then renders fragments related to that user
public class UserActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        client = TwitterApplication.getRestClient();

        String screenName = getScreenName();
        if (screenName != null) {
            setTitle("@" + screenName);
            fetchUserData(screenName);
        } else {
            Toast.makeText(this, "A user was not passed to this activity, and could not be loaded from user preferences.", Toast.LENGTH_LONG).show();
        }
    }


    // figure out which user we care about. assigns in this order:
    // 1. passed from the intent
    // 2. whatever is stored in "screen_name" user preferences (e.g. the logged-in user)
    // 3. null
    private String getScreenName() {
        PreferenceManager prefs = PreferenceManager.getInstance();

        // 1. try to get username from intent
        String screenName = getIntent().getStringExtra("screen_name");

        // 2. try to get username from stored preferences
        if (screenName == null) {
            screenName = prefs.getString("logged_in_screen_name");
        }

        return screenName;
    }


    // fetching user object from db (and do stuff), or call the api loader
    private void fetchUserData(String screenName) {
        User user = User.fromDbByScreenName(screenName);
        if (user != null) {
            Log.d("fetchUserData", "success loading user from db: " + user.toString());
            setupFragments(user);
        } else if (NetworkHelper.isUp(this)) { // getParent() ?
            fetchFromApi(screenName);
        } else {
            Toast.makeText(getParent(), "Could not load the user details from the DB or API", Toast.LENGTH_SHORT).show();
        }
    }

    // load user object from the api, then do stuff
    private void fetchFromApi(String screenName) {
        Log.d("api", "about to hit api");
        client.getUser(screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("api", "success");
                User user = User.fromJSON(json);
                setupFragments(user);
                Log.d("api", "success end");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("api", "failure");
                Toast.makeText(getBaseContext(), "Could not load the user details from the API", Toast.LENGTH_LONG).show();
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("api", "failure end");
            }
        });
    }


    // create and attach the fragments
    private void setupFragments(User user) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        UserDetailsFragment fragUserDetails = UserDetailsFragment.newInstance(user);
        ft.replace(R.id.frameUpper, fragUserDetails);

        UserTimelineFragment fragUserTimeline = UserTimelineFragment.newInstance(user);
        ft.replace(R.id.frameLower, fragUserTimeline);

        ft.commit();
    }
}