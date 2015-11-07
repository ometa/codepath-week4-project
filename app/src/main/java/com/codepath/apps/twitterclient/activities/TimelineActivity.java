package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.lib.EndlessScrollListener;
import com.codepath.apps.twitterclient.lib.LoggingHelper;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private static final int COMPOSE_TWEET = 69;

    private TwitterClient client;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;

    private class ViewHolder {
        public ListView lvTweets;
    }
    private ViewHolder viewHolder;

    private long newest_id = 0;
    private long oldest_id = 0;

    private JsonHttpResponseHandler jsonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        buildJsonHandler();

        viewHolder = new ViewHolder();
        viewHolder.lvTweets = (ListView) findViewById(R.id.lvTimeline);

        client = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(this, tweets);
        viewHolder.lvTweets.setAdapter(adapter);

        viewHolder.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                client.getOlderTimelineEntries(jsonHandler, oldest_id);
                return true;
            }
        });

        client.getNewTimelineEntries(jsonHandler);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Listen for clicked menu items
    // Return false to allow normal menu processing to proceed, true to consume it here.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.mnuLogout:
                client.clearAccessToken();
                Toast.makeText(this, "User logged out", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case R.id.mnuCompose:
                startActivityForResult(new Intent(this, ComposeTweetActivity.class), COMPOSE_TWEET);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Actions when returning from other activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET && resultCode == RESULT_OK) {
            boolean success = data.getBooleanExtra("success", false);
            String message;
            if (success) {
                message = "Successfully posted new tweet";
            } else {
                message = "Failed to post new tweet";
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }


    // This updates the max and minimum tweet ids we know about
    // so we can continually paginate up or down.
    private void modify_since_and_max(JSONArray json) {
        long payload_oldest_id = Tweet.oldestIdFrom(json);
        long payload_newest_id = Tweet.newestIdFrom(json);
        if ((newest_id == 0) || (payload_newest_id > newest_id)) {
            newest_id = payload_newest_id;
        }
        if ((oldest_id == 0) || (payload_oldest_id < oldest_id)) {
            oldest_id = payload_oldest_id;
        }
    }

    private void buildJsonHandler() {
        jsonHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                adapter.addAll(Tweet.fromJson(json));
                adapter.notifyDataSetChanged();
                modify_since_and_max(json);
                Log.d("activity", adapter.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = LoggingHelper.logJsonFailure(errorResponse);
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        };
    }
}
