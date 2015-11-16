package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.TwitterApplication;
import com.codepath.apps.twitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitterclient.lib.NetworkHelper;
import com.codepath.apps.twitterclient.lib.PreferenceManager;

public class TimelineActivity extends AppCompatActivity {

    private static final int COMPOSE_TWEET = 69;

    public class ViewHolder {
        public ViewPager vpPager;
        public PagerSlidingTabStrip tabStrip;
    }
    public ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewHolder = new ViewHolder();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            // logout
            case R.id.mnuLogout:
                TwitterApplication.getRestClient().clearAccessToken();
                removeCachedScreenName();
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
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }


    // todo: move this somewhere more generic
    public void removeCachedScreenName() {
        PreferenceManager.initializeInstance(this);
        PreferenceManager prefs = PreferenceManager.getInstance();
        prefs.removeUser();
    }


    // private member class
    // return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = { "Home", "Mentions" };

        // adapter gets the fragment manager insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
