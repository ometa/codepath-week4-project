package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.UserActivity;
import com.codepath.apps.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by devin on 11/3/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvBody;
        public TextView tvName;
        public TextView tvScreenName;
        public TextView tvTimeago;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    public void addAllToBeginning(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            this.insert(tweet, 0);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Tweet tweet = getItem(position);

        // setup viewholder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tv_screenName);
            viewHolder.tvTimeago = (TextView) convertView.findViewById(R.id.tvTimeAgo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set objects

        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenNameWithAmpersand());
        viewHolder.tvTimeago.setText(tweet.getTimeAgo());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(5, 0))
                .into(viewHolder.ivProfileImage);

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UserActivity.class);
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }
}