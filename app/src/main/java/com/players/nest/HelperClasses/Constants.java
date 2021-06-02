package com.players.nest.HelperClasses;

public class Constants {

    public static final String TAG = "APPLICATION_TAG";

    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";

    public static final String CHAT_FRAGMENT = "CHAT_FRAGMENT";
    public static final String POST_GRID_VIEW = "FROM_POST_FRAGMENT";
    public static final String PROFILE_GRID_VIEW = "FROM_PROFILE_FRAGMENT";
    public static final String FROM_HOME_FRAGMENT = "DATA_FROM_HOME_FRAGMENT";
    public static final String MAIN_CHAT_ACTIVITY_FRAGMENTS = "MAIN_CHAT_ACTIVITY_FRAGMENTS";


    //Matches Constants
    public static final String MATCH_DISPUTE = "DISPUTES";
    public static final String MATCH_STARTED = "STARTED";
    public static final String MATCH_WAITING = "WAITING";
    public static final String MATCH_FINISHED = "FINISHED";
    public static final String MATCH_CONNECTING = "CONNECTING";
    public static final String MATCH_INVITATION = "MATCH INVITATION";
    public static final String SUBMITTING_RESULTS = "SUBMITTING RESULTS";
    public static final String REQUEST_CANCEL = "REQUEST CANCEL";
    public static final String APPROVE_CANCEL = "CANCEL APPROVE";
    public static final String INVITED_MATCH_ID = "INVITED_MATCH_ID";
    public static final String APPROVED = "APPROVED";
    public static final String NOT_APPROVED = "NOT APPROVED";
    public static final String MATCH_WAITING_FOR_PLAYERS = "MATCH WAITING FOR PLAYERS";

    public static final String TOURNAMENT_CANCELED = "TOURNAMENT_CANCELED";
    //CONFIRM DIALOG
    public static final int HOST_ACCEPTED = 20;
    public static final int JOIN_MATCH = 7;
    public static final int CONFIRM_EXIT_MATCH = 0;
    public static final int MY_MATCHES_MENU = 2;
    public static final int SUBMIT_RESULT_DIALOG = 1;
    public static final int CREATE_MATCH_BUTTON = 3;
    public static final int ENTER_DISPUTE_DIALOG = 4;
    public static final int SEND_INVITATION = 5;
    public static final int CANCEL_INVITE = 6;


    //OBJECTS
    public static final String GAME_OBJECT = "GAME_OBJECT";
    public static final String USER_OBJECT = "USER_OBJECT";
    public static final String USER_POST_OBJECT = "USER_POST_OBJECT";
    public static final String OPPONENT_USER_OBJECT = "OPPONENT_USER_OBJECT";
    public static final String MATCH_DETAIL_OBJECT = "MATCH_DETAILS_USER_GAMES_OBJECT";


    //CONFIRM ALERT DIALOG (YES/NO NOT PRESENT)
    public static final int HOST_REJECTED = 2;
    public static final int EVIDENCE_SUBMITTED = 4;
    public static final int SCORES_ARE_CORRECT = 7;
    public static final int MATCH_STARTED_DIALOG = 3;


    //Unsent Dialog Constants
    public static final int DELETE_COMMENT = 5;
    public static final int UNSENT_CHAT = 6;
    public static final int UNSENT_IMAGE = 3;
    public static final int COPY_MESSAGE = 13;


    //Request Code for startActivityForResult()
    public static final int CAMERA_REQUEST_CODE = 12;
    public static final int GALLERY_REQUEST_CODE = 13;


    public static final String ELAPSED = "ELAPSED";


    //Notification
    public static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    public static final String OPEN_ALERT_FRAGMENT = "ALERT_FRAGMENT";
    public static final String NOTIFICATION_SOUND = "default";


    //User Object Constants
    public static final String USER_PROFILE_PIC = "profilePic";
    public static final String SHOW_CURRENT_USERS_FANS = "CURRENT_USER_FANS";
    public static final String SHOW_CURRENT_USERS_FOLLOWING = "CURRENT_USER_FOLLOWING";

    //Fragment Tags
    public static final String VIEW_POST_FRAGMENT = "VIEW_PROFILE_FRAGMENT";
    public static final String MATCH_REQUEST = "MATCH_REQUEST_FROM_ALERT_ADAPT";


    //Star Constants
    public static final String HALF_STAR = "LEVEL 1";
    public static final String ONE_STAR = "LEVEL 2";
    public static final String TWO_STAR = "LEVEL 3";
    public static final String TWO_N_HALF_STAR = "LEVEL 4";
    public static final String THREE_STAR = "LEVEL 5";
    public static final String FOUR_STAR = "LEVEL 6";
    public static final String FOUR_N_HALF_STAR = "LEVEL 7";
    public static final String FIVE_STAR = "LEVEL 8";


    //Chat Fragment Constants
    public static final String TEXT_MESSAGE_TYPE = "Text";
    public static final String IMAGE_MESSAGE_TYPE = "Image";
    public static final String POST_MESSAGE_TYPE = "User Post";


    //Some IMP Constants Values
    public static final int SHARE_BOTTOM_SHEET = 5;
    public static final int FANS_FOLLOWING_ACTIVITY = 6;
    public static final String DARK_MODE = "DARK_MODE";
    public static final String FANS_FRAGMENT = "FANS_FRAGMENT";
    public static final String VIDEO_FILE_FIREBASE = "videos";
    public static final String VIDEO_FILE_PATH = "VIDEO_FILE_PATH";
    public static final String FROM_CHAT_FRAGMENT = "FROM_CHAT_FRAGMENT";
    public static final String OPEN_FANS_FRAGMENT = "OPEN_FANS_FRAGMENT";
    public static final String MAIN_ACTIVITY_PROFILE = "PROFILE_FRAGMENT";
    public static final String OPEN_FOLLOWING_FRAGMENT = "OPEN_FOLLOWING_FRAGMENT";
    public static final String FROM_SEARCH_FRAGMENT = "SEARCH_FRAGMENT_OPEN_USERS_PROFILE";
    public static final String INVITATION_SENT = "Invitation sent";
    public static final String DARK_MODE_ENABLED = "DARK_MODE_ENABLED";


    /**
     * IMAGE AND VIDEO CONSTANTS
     **/
    public static final String VIDEO_POST_TYPE = "VIDEO";
    public static final String IMAGE_POST_TYPE = "IMAGE";
    public static final String VIDEO_FILE = "SELECTED_FILE_IS_VIDEO";
    public static final String IMAGE_FILE = "SELECTED_FILE_IS_IMAGE";
    public static final String SELECTED_FILE_URL = "SELECTED_FILE_URL";
    public static final String SELECTED_FILE_ABS_PATH = "ABSOLUTE_PATH_OF_SELECTED_FILE";
    public static final String POST_TYPE = "POST_TYPE";
}
