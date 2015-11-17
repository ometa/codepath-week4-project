# Project 4 - *Tweetr*

**Tweetr** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **22** hours spent in total.

## User Stories


The following **required** functionality is completed:

* [x] The app includes **all required user stories** from Week 3 Twitter Client
* [x] User can **switch between Timeline and Mention views using tabs**
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to **view their own profile**
  * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [x] Profile view includes that user's timeline
* [x] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [x] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases (like null response from twitter api), network failures, etc.
* [x] Improve the user interface and theme the app to feel twitter branded
* [ ] User can view following / followers list through the profile
* [ ] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [ ] User can **"reply" to any tweet on their home timeline**
  * [ ] The user that wrote the original tweet is automatically "@" replied in compose
* [ ] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [ ] User can take favorite (and unfavorite) or retweet actions on a tweet
* [ ] User can **search for tweets matching a particular query** and see results

The following **bonus** features are implemented:

* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [x] User objects are stored in the database, and only fetched from the API if needed. This reduces the liklihood of API rate-limiting.
* [x] Added the ability to log out
* [x] Explain to the end-user if they are rate-limited
* [x] Images use slightly rounded corners

Here are the remaining **optional** features from week 3, for posterity: 

* [ ] Improve the user interface and theme the app to feel "twitter branded"
* [ ] User can tap a tweet to **open a detailed tweet view**
* [ ] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [ ] User can **select "reply" from detail view to respond to a tweet**

Here are the remaining **bonus** features from week 3, for posterity:

* [ ] User can see embedded image media within the tweet detail view
* [ ] Compose tweet functionality is build using modal overlay

## Video Walkthroughs

Here is a walkthrough of implemented user stories:

<img src='https://github.com/ometa/codepath-week4-project/blob/master/demo/walkthrough.gif' title='Video Walkthrough 1' width='' alt='Video Walkthrough 1' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Moving the TimelineActivity into a Fragment was not too difficult. The more difficult and time-consuming aspect of this week's homework was modeling and implementing the shared user details fragment and ensuring that everything got hooked in correctly.  It was also challenging to figure out the best logic when the internet is not available.  The app will pull items out of the database if the internet is down.  It also caches the User objects to reduce API calls.  I am also pretty proud of the abstract class TweetsListFragment and the two fragments that implement it -- HomeTimelineFragment and MentionsTimelineFragment.

One issue that pops up is that the `mentions_timeline.json` API endpoint gets called multiple times when it should just get called once, and I am not sure why.  I would love some feedback on that.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright 2015 Devin Breen
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
