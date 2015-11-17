package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.adapters.TweetsArrayAdapter;
import com.codepath.apps.twitterclient.lib.EndlessScrollListener;
import com.codepath.apps.twitterclient.lib.LogHelper;
import com.codepath.apps.twitterclient.lib.NetworkHelper;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class TweetsListFragment extends Fragment {

    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;

    public class ViewHolder {
        public ListView lvTweets;
        public SwipeRefreshLayout swipeContainer;
    }
    public ViewHolder viewHolder;

    private long newest_id = 0;
    private long oldest_id = 0;

    private JsonHttpResponseHandler handlerToEnd;
    private JsonHttpResponseHandler handlerToBeginning;
    private JsonHttpResponseHandler handlerSwipeUp;

    protected abstract void onSwipeUp(JsonHttpResponseHandler handler);
    protected abstract void onSwipeDown(JsonHttpResponseHandler handler);
    protected abstract void initialLoadWithInternet(JsonHttpResponseHandler handler);
    protected abstract void initialLoadNoInternet(TweetsArrayAdapter aTweets);

    public TweetsListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup response handlers
        handlerToBeginning = buildJsonHandler(true);
        handlerToEnd = buildJsonHandler(false);
        handlerSwipeUp = buildJsonSwipeUpHandler();

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    // helper method to trigger the child class's onSwipeUp method.
    // this gets called from outside the fragment.
    public void swipeUp() {
        onSwipeUp(handlerSwipeUp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweets_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        viewHolder = new ViewHolder();
        viewHolder.lvTweets = (ListView) view.findViewById(R.id.lvTimeline);
        viewHolder.swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Configure the refreshing colors
        viewHolder.swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light
        );

        viewHolder.lvTweets.setAdapter(aTweets);
        viewHolder.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                onSwipeDown(handlerToEnd);
                return true;
            }
        });

        viewHolder.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkHelper.isUp(getActivity())) {
                    onSwipeUp(handlerSwipeUp);
                } else {
                    viewHolder.swipeContainer.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load initial Data (has to happen after we instantiate the client in the child onCreate()
        if (NetworkHelper.isUp(getActivity())) {
            initialLoadWithInternet(handlerToEnd);
        } else {
            Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
            initialLoadNoInternet(aTweets);
            load_since_and_max_from_db();
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

    // generate from whatever is in the db
    private void load_since_and_max_from_db() {
        Tweet newest = (Tweet) new Select().from(Tweet.class).orderBy("uid DESC").limit(1).execute().get(0);
        Tweet oldest = (Tweet) new Select().from(Tweet.class).orderBy("uid ASC").limit(1).execute().get(0);
        newest_id = newest.getUid();
        oldest_id = oldest.getUid();
    }

    private JsonHttpResponseHandler buildJsonSwipeUpHandler() {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // aTweets.clear();
                aTweets.addAllToBeginning(Tweet.fromJson(json));
                aTweets.notifyDataSetChanged();
                modify_since_and_max(json);
                // Now we call setRefreshing(false) to signal refresh has finished
                viewHolder.swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = LogHelper.logJsonFailure(errorResponse);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                viewHolder.swipeContainer.setRefreshing(false);
            }
        };
    }

    // This builds 2 types of JsonHttpResponseHandler objects:
    // - Add to the beginning of the aTweets
    // - Add to the end of the aTweets
    private JsonHttpResponseHandler buildJsonHandler(final boolean addToBeginning) {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                if (addToBeginning) {
                    aTweets.addAllToBeginning(Tweet.fromJson(json));
                } else {
                    aTweets.addAll(Tweet.fromJson(json));
                }
                aTweets.notifyDataSetChanged();
                modify_since_and_max(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = LogHelper.logJsonFailure(errorResponse);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        };
    }

    public long getOldest_id() {
        return oldest_id;
    }

    public long getNewest_id() {
        return newest_id;
    }
}