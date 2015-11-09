package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.lib.EndlessScrollListener;
import com.codepath.apps.twitterclient.lib.LoggingHelper;
import com.codepath.apps.twitterclient.lib.NetworkHelper;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
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
        public SwipeRefreshLayout swipeContainer;
    }
    private ViewHolder viewHolder;

    private long newest_id = 0;
    private long oldest_id = 0;

    private JsonHttpResponseHandler handlerToEnd;
    private JsonHttpResponseHandler handlerToBeginning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();

        handlerToBeginning = buildJsonHandler(true);
        handlerToEnd = buildJsonHandler(false);

        viewHolder = new ViewHolder();
        viewHolder.lvTweets = (ListView) findViewById(R.id.lvTimeline);
        viewHolder.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);


        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(this, tweets);
        viewHolder.lvTweets.setAdapter(adapter);

        viewHolder.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                client.getOlderTimelineEntries(handlerToEnd, oldest_id);
                return true;
            }
        });

        viewHolder.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        viewHolder.swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light
        );


        // Final step: Load initial Data
        // - if we have internet, load fresh
        // - otherwise, load from local cache
        if (NetworkHelper.isUp(this)) {
            Tweet.deleteAll();
            User.deleteAll();
            client.getNewTimelineEntries(handlerToEnd);
        } else {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
            adapter.addAll(Tweet.getAll());
            load_since_and_max_from_db();
        }
    }


    // Called by swipeContainer to refresh the newest status' at the top.
    public void fetchTimelineAsync() {

        if (NetworkHelper.isDown(getBaseContext())) {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
            viewHolder.swipeContainer.setRefreshing(false);

        } else {
            client.getNewTimelineEntries(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    // adapter.clear();
                    adapter.addAllToBeginning(Tweet.fromJson(json));
                    adapter.notifyDataSetChanged();
                    modify_since_and_max(json);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    viewHolder.swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    String msg = LoggingHelper.logJsonFailure(errorResponse);
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                }

            }, newest_id);
        }
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
                Toast.makeText(this, R.string.logout_success, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case R.id.mnuCompose:
                if (NetworkHelper.isUp(this)) {
                    startActivityForResult(new Intent(this, ComposeTweetActivity.class), COMPOSE_TWEET);
                } else {
                    Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Actions when we return from compose tweet activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET && resultCode == RESULT_OK) {
            boolean success = data.getBooleanExtra("success", false);
            String message;
            if (success) {
                message = getString(R.string.tweet_post_success);
                client.getNewTimelineEntries(handlerToBeginning, newest_id, 1);
            } else {
                message = getString(R.string.tweet_post_failure);
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show   ();
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

    private void load_since_and_max_from_db() {
        Tweet newest = (Tweet) new Select().from(Tweet.class).orderBy("uid DESC").limit(1).execute().get(0);
        Tweet oldest = (Tweet) new Select().from(Tweet.class).orderBy("uid ASC").limit(1).execute().get(0);
        newest_id = newest.getUid();
        oldest_id = oldest.getUid();
        Toast.makeText(this, "newest: " + newest.getUid(), Toast.LENGTH_LONG).show();
    }


    // This builds 2 types of JsonHttpResponseHandler objects:
    // - Add to the beginning of the adapter
    // - Add to the end of the adapter
    private JsonHttpResponseHandler buildJsonHandler(final boolean addToBeginning) {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                if (addToBeginning) {
                    adapter.addAllToBeginning(Tweet.fromJson(json));
                } else {
                    adapter.addAll(Tweet.fromJson(json));
                }
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
