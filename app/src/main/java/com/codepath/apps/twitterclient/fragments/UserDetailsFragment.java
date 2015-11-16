package com.codepath.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.User;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UserDetailsFragment extends Fragment {

    protected User user;

    private class ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvScreenName;
    }
    private ViewHolder viewHolder;

    public UserDetailsFragment() {}

    public static UserDetailsFragment newInstance(User user) {
        UserDetailsFragment frag = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // define the viewHolder
        viewHolder = new ViewHolder();
        viewHolder.ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
        viewHolder.tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);

        // populate the views
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvScreenName.setText(user.getScreenName());
        Picasso.with(getContext())
                .load(user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(5, 0))
                .fit().into(viewHolder.ivProfileImage);
    }
}