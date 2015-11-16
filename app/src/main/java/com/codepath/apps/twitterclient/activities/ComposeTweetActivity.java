package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.fragments.UserDetailsFragment;
import com.codepath.apps.twitterclient.lib.LogHelper;
import com.codepath.apps.twitterclient.lib.NetworkHelper;
import com.codepath.apps.twitterclient.lib.PreferenceManager;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeTweetActivity extends AppCompatActivity {

    private TwitterClient client;

    private class ViewHolder {
        public TextView etBody;
    }
    private ViewHolder viewHolder;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        viewHolder = new ViewHolder();
        viewHolder.etBody = (EditText) findViewById(R.id.etBody);

        client = TwitterApplication.getRestClient();

        PreferenceManager prefs = PreferenceManager.getInstance();
        String screenName = prefs.getString("logged_in_screen_name");
        if (screenName != null) {
            fetchUserData(screenName);
        } else {
            Toast.makeText(this, "The user preference 'logged_in_screen_name' must be set to use this activity.", Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(getParent(), "Could not load the logged-in user details from the DB or API", Toast.LENGTH_SHORT).show();
        }
    }


    // load user object from the api, then do stuff
    private void fetchFromApi(String screenName) {
        Log.d("api", "about to fetch hit api");
        client.getUser(screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("composeTweetActivity", "on success start");
                User user = User.fromJSON(json);
                setupFragments(user);
                Log.d("composeTweetActivity", "on success end");
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
        UserDetailsFragment frag = UserDetailsFragment.newInstance(user);
        ft.replace(R.id.frameUpper, frag);
        ft.commit();
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose_tweet, menu);

        MenuItem mnuSubmitNewTweet = menu.findItem(R.id.mnuSubmitNewTweet);
        mnuSubmitNewTweet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            // return true if the event was handled, false otherwise.
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String status = viewHolder.etBody.getText().toString();
                if (status.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter some text.", Toast.LENGTH_SHORT).show();
                } else {
                    postTweet(status);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void postTweet(String status) {

        client.postNewTweet(status, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Intent intent = new Intent();
                intent.putExtra("success", true);
                setResult(RESULT_OK, intent);
                Log.d("composetweetactivity", "posted successfully");
                finish();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = LogHelper.logJsonFailure(errorResponse);
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}