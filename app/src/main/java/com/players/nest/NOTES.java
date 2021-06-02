package com.players.nest;

public class NOTES {
    /**
     * Home Fragment has only 1 Value Event Listeners, Which should be removed when the fragment is replaced.
     *      - The Realtime listener will keep listening to the no of posts. And will show a popup
     *      if the total no  of posts gets  changed.
     *
     * ViewProfileFragment needs a USER class Object to start. If it gets the user Object it will show
     * the user profile successfully.
     *
     * Chat Fragment requires a User Object, So any activity that want to host the chat Fragment need to
     * supply User Object to successfully Attach the Fragment.
     * Chat Fragment has 2 Value Event Listeners, one for live messaging and one for getting real time
     * status of the user. The status Listener is removed successfully as soon as the fragment is
     * destroyed.
     *
     * Value Event Listener added in Alert Fragment and successfully removed on onDestroyView().
     * Value Event Listener added in Match Detail Activity and successfully removed on onStop().
     *
     * MatchStarted Activity requires UserJoinedMatch Object to run. Once it get any activity can call the
     * MatchStarted activity.
     *
     * Comment Activity requires two Objects to start
     *         1. UserPosts Object
     *         2. User Object
     *
     * ViewPostFragment takes only one UsersPosts Object to start. Any activity with userPost Object can start it.
     *
     * View Profile Activity has view profile fragment Attached to it. Any Activity that want to use
     * view profile Fragment has to pass USER OBJECT to View Profile Activity.
     *
     * Match Detail has one value Event Listener for RealTime Match Status update.
     */
}
