package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.lib.NetworkHelper;

public class TimelineActivity extends AppCompatActivity {

    private static final int COMPOSE_TWEET = 69;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Return false to allow normal menu processing to proceed, true to consume it here.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            // logout
            case R.id.mnuLogout:
                TwitterApplication.getRestClient().clearAccessToken();
                Toast.makeText(this, R.string.logout_success, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            // compose new tweet
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
                // todo: implement the below, refresh whatever fragment we are using.
//              4  client.getNewTimelineEntries(handlerToBeginning, newest_id, 1);
            } else {
                message = getString(R.string.tweet_post_failure);
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show   ();
        }
    }
}
