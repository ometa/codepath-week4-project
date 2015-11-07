package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.lib.LoggingHelper;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeTweetActivity extends AppCompatActivity {

    TwitterClient client;
    User currentUser;

    private class ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvScreenName;
        public TextView etBody;
    }
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        viewHolder = new ViewHolder();
        viewHolder.ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        viewHolder.tvName = (TextView) findViewById(R.id.tvName);
        viewHolder.tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        viewHolder.etBody = (EditText) findViewById(R.id.etBody);

        client = TwitterApplication.getRestClient();

        populateCurrentUserDetails();
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
                String msg = LoggingHelper.logJsonFailure(errorResponse);
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Return false to allow normal menu processing to proceed, true to consume it here.
        /*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnuSubmitNewTweet) {

        }
        return true;
        //return super.onOptionsItemSelected(item);
    }
*/

    private void populateCurrentUserDetails() {
        client.getLoggedInUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                currentUser = User.fromJSON(json);
                viewHolder.tvName.setText(currentUser.getName());
                viewHolder.tvScreenName.setText(currentUser.getScreenName());
                Picasso.with(getBaseContext())
                        .load(currentUser.getProfileImageUrl())
                        .into(viewHolder.ivProfileImage);
            }
        });
    }
}