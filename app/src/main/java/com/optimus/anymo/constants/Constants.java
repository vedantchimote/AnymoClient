package com.optimus.anymo.constants;

public interface Constants {

    // Attention! You can only change the values of the following constants:

    // YOUTUBE_API_KEY, EMOJI_KEYBOARD, WEB_SITE_AVAILABLE, GOOGLE_PAY_TEST_BUTTON, MY_AD_AFTER_ITEM_NUMBER,
    // APP_TEMP_FOLDER, VIDEO_FILE_MAX_SIZE, WEB_SITE, CLIENT_ID, API_DOMAIN,
    // POST_CHARACTERS_LIMIT, HASHTAGS_COLOR

    // It is forbidden to change the value of constants, which are not indicated above !!!

    public static final int VOLLEY_REQUEST_SECONDS = 15; //SECONDS TO REQUEST

    public static final Boolean EMOJI_KEYBOARD = true; // false = Do not display your own Emoji keyboard | true = allow display your own Emoji keyboard

    public static final Boolean WEB_SITE_AVAILABLE = true; // false = Do not show menu items (Open in browser, Copy profile link) in profile page | true = show menu items (Open in browser, Copy profile link) in profile page

    public static final String APP_TEMP_FOLDER = "anymo"; //directory for temporary storage of images from the camera

    // Nagpur,Maharashtra

    public static final double DEFAULT_LAT = 21.1466; // Latitude
    public static final double DEFAULT_LNG = 79.088860; // Longitude

    public static final int VIDEO_FILE_MAX_SIZE = 50340035; //Max size for video file in bytes | For example 7mb = 7*1024*1024

    public static final int MAX_ITEM_TEXT_SIZE = 150; //Text length for new item
    public static final int MIN_ITEM_TEXT_SIZE = 1;  //Minimum text length for new item

    public static final String WEB_SITE = "https://ioptimus.me";  //web site url address

    // Client ID For identify the application | Must be the same with CLIENT_ID from server config: db.inc.php

    public static final String CLIENT_ID = "5";  // Correct example: 12567 | Incorrect example: 0987

    // Client Secret | Text constant | Must be the same with CLIENT_SECRET from server config: db.inc.php

    String CLIENT_SECRET = "f*Hk86&_Hrfv7cjnf-I=yT";    // Example: "f*Hk86&_Hrfv7cjnf-I=yT"

    public static final String API_DOMAIN = "https://ioptimus.me/";  // url address to which the application sends requests || http://10.0.2.2/ - for test on emulator in localhost [XAMPP]

    public static final String API_FILE_EXTENSION = "";     // Attention! Do not change the value for this constant!
    public static final String API_VERSION = "v2";          // Attention! Do not change the value for this constant!

    // Attention! Do not change values for next constants!

    public static final String METHOD_ACCOUNT_GET_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/account.getSettings" + API_FILE_EXTENSION;
    public static final String METHOD_DIALOGS_NEW_GET = API_DOMAIN + "api/" + API_VERSION + "/method/dialogs_new.get" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_UPDATE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.update" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_LOGIN = API_DOMAIN + "api/" + API_VERSION + "/method/account.signIn" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SIGNUP = API_DOMAIN + "api/" + API_VERSION + "/method/account.signUp" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/account.authorize" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_UPDATE_FCM_TOKEN = API_DOMAIN + "api/" + API_VERSION + "/method/account.updateFcmToken" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_RECOVERY = API_DOMAIN + "api/" + API_VERSION + "/method/account.recovery" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SETPASSWORD = API_DOMAIN + "api/" + API_VERSION + "/method/account.setPassword" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_DEACTIVATE = API_DOMAIN + "api/" + API_VERSION + "/method/account.deactivate" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/account.logOut" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SET_ALLOW_MESSAGES = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowMessages" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_SET_GEO_LOCATION = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGeoLocation" + API_FILE_EXTENSION;

    public static final String METHOD_SUPPORT_SEND_TICKET = API_DOMAIN + "api/" + API_VERSION + "/method/support.sendTicket" + API_FILE_EXTENSION;

    public static final String METHOD_BLACKLIST_GET = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.get" + API_FILE_EXTENSION;
    public static final String METHOD_BLACKLIST_ADD = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.add" + API_FILE_EXTENSION;
    public static final String METHOD_BLACKLIST_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.remove" + API_FILE_EXTENSION;

    public static final String METHOD_ITEM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/item.get" + API_FILE_EXTENSION;

    public static final String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms" + API_FILE_EXTENSION;
    public static final String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks" + API_FILE_EXTENSION;

    public static final String METHOD_ITEMS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/items.new" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/items.uploadImg" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_FLOW = API_DOMAIN + "api/" + API_VERSION + "/method/items.flow" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_FOLLOW = API_DOMAIN + "api/" + API_VERSION + "/method/items.follow" + API_FILE_EXTENSION;
    public static final String METHOD_ITEMS_FAVORITES = API_DOMAIN + "api/" + API_VERSION + "/method/items.favorites" + API_FILE_EXTENSION;

    public static final String METHOD_CHAT_GET = API_DOMAIN + "api/" + API_VERSION + "/method/chat.get" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.remove" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_GET_PREVIOUS = API_DOMAIN + "api/" + API_VERSION + "/method/chat.getPrevious" + API_FILE_EXTENSION;
    public static final String METHOD_CHAT_NOTIFY = API_DOMAIN + "api/" + API_VERSION + "/method/chat.notify" + API_FILE_EXTENSION;

    public static final String METHOD_MSG_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/msg.new" + API_FILE_EXTENSION;
    public static final String METHOD_MSG_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/msg.uploadImg" + API_FILE_EXTENSION;

    public static final String METHOD_GET_STICKERS = API_DOMAIN + "api/" + API_VERSION + "/method/stickers.get" + API_FILE_EXTENSION;

    public static final String METHOD_REPORT_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/reports.new" + API_FILE_EXTENSION;

    public static final String METHOD_COMMENTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/comments.remove" + API_FILE_EXTENSION;
    public static final String METHOD_COMMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/comments.new" + API_FILE_EXTENSION;

    public static final String METHOD_LIKES_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/likes.like" + API_FILE_EXTENSION;
    public static final String METHOD_LIKES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/likes.get" + API_FILE_EXTENSION;


    public static final String METHOD_ACCOUNT_OTP = API_DOMAIN + "api/" + API_VERSION + "/method/account.otp" + API_FILE_EXTENSION;
    public static final String METHOD_APP_CHECK_PHONE_NUMBER = API_DOMAIN + "api/" + API_VERSION + "/method/app.checkPhoneNumber" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_GOOGLE_AUTH = API_DOMAIN + "api/" + API_VERSION + "/method/account.google" + API_FILE_EXTENSION;

    // Other Constants

    public static final int PAGE_ANY = 0;
    int PAGE_PROFILE = 1;
    int PAGE_GALLERY = 2;
    int PAGE_FRIENDS = 3;
    int PAGE_MATCHES = 4;
    int PAGE_MESSAGES = 5;
    int PAGE_NOTIFICATIONS = 6;
    int PAGE_GUESTS = 7;
    int PAGE_LIKES = 8;
    int PAGE_LIKED = 9;
    int PAGE_UPGRADES = 10;
    int PAGE_NEARBY = 11;
    int PAGE_MEDIA_STREAM = 12;
    int PAGE_MEDIA_FEED = 13;
    int PAGE_SEARCH = 14;
    int PAGE_SETTINGS = 15;
    int PAGE_HOTGAME = 16;
    int PAGE_FINDER = 17;
    int PAGE_MENU = 18;
    int PAGE_MAIN = 19;
    int PAGE_FAVORITES = 20;

    public static final int SECTION_ANY = PAGE_ANY;
    public static final int SECTION_PROFILE = PAGE_PROFILE;

    //

    public static final int SIGNIN_EMAIL = 0;
    public static final int SIGNIN_OTP = 1;
    public static final int SIGNIN_FACEBOOK = 2;
    public static final int SIGNIN_GOOGLE = 3;
    public static final int SIGNIN_APPLE = 4;
    public static final int SIGNIN_TWITTER = 5;

    public static final int OAUTH_TYPE_FACEBOOK = 0;
    public static final int OAUTH_TYPE_GOOGLE = 1;

    //

    public static final int APP_TYPE_ALL = -1;
    public static final int APP_TYPE_MANAGER = 0;
    public static final int APP_TYPE_WEB = 1;
    public static final int APP_TYPE_ANDROID = 2;
    public static final int APP_TYPE_IOS = 3;


    public static final int IMAGE_TYPE_PROFILE_PHOTO = 0;
    public static final int IMAGE_TYPE_PROFILE_COVER = 1;

    public static final int GALLERY_ITEM_TYPE_IMAGE = 0;
    public static final int GALLERY_ITEM_TYPE_VIDEO = 1;

    public static final int REPORT_TYPE_ITEM = 0;
    public static final int REPORT_TYPE_PROFILE = 1;
    public static final int REPORT_TYPE_MESSAGE = 2;
    public static final int REPORT_TYPE_COMMENT = 3;
    public static final int REPORT_TYPE_GALLERY_ITEM = 4;
    public static final int REPORT_TYPE_MARKET_ITEM = 5;
    public static final int REPORT_TYPE_COMMUNITY = 6;

    public static final int POST_TYPE_DEFAULT = 0;
    public static final int POST_TYPE_PHOTO_UPDATE = 1;
    public static final int POST_TYPE_COVER_UPDATE = 2;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 1;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER = 2;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;                               //ACCESS_COARSE_LOCATION
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE = 4;            //WRITE_EXTERNAL_STORAGE

    public static final int LIST_ITEMS = 20;

    public static final int POST_CHARACTERS_LIMIT = 1000;

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    public static final int GCM_ENABLED = 1;
    public static final int GCM_DISABLED = 0;

    //here
    public static final int ADMOB_ENABLED = 0;
    public static final int ADMOB_DISABLED = 0;

    public static final int COMMENTS_ENABLED = 1;
    public static final int COMMENTS_DISABLED = 0;

    public static final int MESSAGES_ENABLED = 1;
    public static final int MESSAGES_DISABLED = 0;

    public static final int ERROR_SUCCESS = 0;

    public static final int SEX_UNKNOWN = 0;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public static final int NOTIFY_TYPE_LIKE = 0;
    public static final int NOTIFY_TYPE_FOLLOWER = 1;
    public static final int NOTIFY_TYPE_MESSAGE = 2;
    public static final int NOTIFY_TYPE_COMMENT = 3;
    public static final int NOTIFY_TYPE_COMMENT_REPLY = 4;
    public static final int NOTIFY_TYPE_FRIEND_REQUEST_ACCEPTED = 5;
    public static final int NOTIFY_TYPE_GIFT = 6;

    public static final int NOTIFY_TYPE_IMAGE_COMMENT = 7;
    public static final int NOTIFY_TYPE_IMAGE_COMMENT_REPLY = 8;
    public static final int NOTIFY_TYPE_IMAGE_LIKE = 9;

    public static final int NOTIFY_TYPE_VIDEO_COMMENT = 10;
    public static final int NOTIFY_TYPE_VIDEO_COMMENT_REPLY = 11;
    public static final int NOTIFY_TYPE_VIDEO_LIKE = 12;

    public static final int NOTIFY_TYPE_PROFILE_PHOTO_APPROVE = 2003;
    public static final int NOTIFY_TYPE_PROFILE_PHOTO_REJECT = 2004;
    public static final int NOTIFY_TYPE_PROFILE_COVER_APPROVE = 2007;
    public static final int NOTIFY_TYPE_PROFILE_COVER_REJECT = 2008;

    public static final int NOTIFY_TYPE_REFERRAL = 14;

    public static final int GCM_NOTIFY_CONFIG = 0;
    public static final int GCM_NOTIFY_SYSTEM = 1;
    public static final int GCM_NOTIFY_CUSTOM = 2;
    public static final int GCM_NOTIFY_LIKE = 3;
    public static final int GCM_NOTIFY_ANSWER = 4;
    public static final int GCM_NOTIFY_QUESTION = 5;
    public static final int GCM_NOTIFY_COMMENT = 6;
    public static final int GCM_NOTIFY_FOLLOWER = 7;
    public static final int GCM_NOTIFY_PERSONAL = 8;
    public static final int GCM_NOTIFY_MESSAGE = 9;
    public static final int GCM_NOTIFY_COMMENT_REPLY = 10;
    public static final int GCM_FRIEND_REQUEST_INBOX = 11;
    public static final int GCM_FRIEND_REQUEST_ACCEPTED = 12;
    public static final int GCM_NOTIFY_GIFT = 14;
    public static final int GCM_NOTIFY_SEEN = 15;
    public static final int GCM_NOTIFY_TYPING = 16;
    public static final int GCM_NOTIFY_URL = 17;

    public static final int GCM_NOTIFY_IMAGE_COMMENT_REPLY = 18;
    public static final int GCM_NOTIFY_IMAGE_COMMENT = 19;
    public static final int GCM_NOTIFY_IMAGE_LIKE = 20;

    public static final int GCM_NOTIFY_VIDEO_COMMENT_REPLY = 21;
    public static final int GCM_NOTIFY_VIDEO_COMMENT = 22;
    public static final int GCM_NOTIFY_VIDEO_LIKE = 23;

    public static final int GCM_NOTIFY_REFERRAL = 24;

    public static final int GCM_NOTIFY_TYPING_START = 27;
    public static final int GCM_NOTIFY_TYPING_END = 28;

    public static final int GCM_NOTIFY_PROFILE_PHOTO_APPROVE = 1003;
    public static final int GCM_NOTIFY_PROFILE_PHOTO_REJECT = 1004;
    public static final int GCM_NOTIFY_PROFILE_COVER_APPROVE = 1007;
    public static final int GCM_NOTIFY_PROFILE_COVER_REJECT = 1008;


    public static final int ERROR_LOGIN_TAKEN = 300;
    public static final int ERROR_EMAIL_TAKEN = 301;
    public static final int ERROR_FACEBOOK_ID_TAKEN = 302;

    int ERROR_OTP_VERIFICATION = 506;
    int ERROR_OTP_PHONE_NUMBER_TAKEN = 507;
    int ERROR_LIMIT_EXCEEDED = 508;

    int ERROR_MULTI_ACCOUNT = 500;

    int ERROR_CLIENT_ID = 19100;
    int ERROR_CLIENT_SECRET = 19101;

    public static final int ACCOUNT_STATE_ENABLED = 0;
    public static final int ACCOUNT_STATE_DISABLED = 1;
    public static final int ACCOUNT_STATE_BLOCKED = 2;
    public static final int ACCOUNT_STATE_DEACTIVATED = 3;

    public static final int ACCOUNT_TYPE_USER = 0;
    public static final int ACCOUNT_TYPE_GROUP = 1;

    public static final int ERROR_UNKNOWN = 100;
    public static final int ERROR_ACCESS_TOKEN = 101;

    public static final int ERROR_ACCOUNT_ID = 400;

    public static final int UPLOAD_TYPE_PHOTO = 0;
    public static final int UPLOAD_TYPE_COVER = 1;

    public static final int ACTION_NEW = 1;
    public static final int ACTION_EDIT = 2;
    public static final int SELECT_POST_IMG = 3;
    public static final int VIEW_CHAT = 4;
    public static final int CREATE_POST_IMG = 5;
    public static final int SELECT_CHAT_IMG = 6;
    public static final int CREATE_CHAT_IMG = 7;
    public static final int FEED_NEW_POST = 8;
    public static final int FRIENDS_SEARCH = 9;
    public static final int ITEM_EDIT = 10;
    public static final int STREAM_NEW_POST = 11;
    public static final int ITEM_REPOST = 12;
    public static final int ITEM_ACTIONS_MENU = 14;
    public static final int ITEM_ACTION_REPOST = 15;

    public static final int ITEM_TYPE_IMAGE = 0;
    public static final int ITEM_TYPE_VIDEO = 1;
    public static final int ITEM_TYPE_POST = 2;
    public static final int ITEM_TYPE_COMMENT = 3;
    public static final int ITEM_TYPE_GALLERY = 4;

    public static final int PA_BUY_CREDITS = 0;
    public static final int PA_BUY_GIFT = 1;
    public static final int PA_BUY_VERIFIED_BADGE = 2;
    public static final int PA_BUY_GHOST_MODE = 3;
    public static final int PA_BUY_DISABLE_ADS = 4;
    public static final int PA_BUY_REGISTRATION_BONUS = 5;
    public static final int PA_BUY_REFERRAL_BONUS = 6;
    public static final int PA_BUY_MANUAL_BONUS = 7;
    public static final int PA_BUY_PRO_MODE = 8;
    public static final int PA_BUY_SPOTLIGHT = 9;
    public static final int PA_BUY_MESSAGE_PACKAGE = 10;
    public static final int PA_BUY_OTP_VERIFICATION = 11;

    public static final int PT_UNKNOWN = 0;
    public static final int PT_CREDITS = 1;
    public static final int PT_CARD = 2;
    public static final int PT_GOOGLE_PURCHASE = 3;
    public static final int PT_APPLE_PURCHASE = 4;
    public static final int PT_ADMOB_REWARDED_ADS = 5;
    public static final int PT_BONUS = 6;

    public static final String TAG = "TAG";

    public static final String HASHTAGS_COLOR = "#5BCFF2";

    public static final String TAG_UPDATE_BADGES = "update_badges";
}