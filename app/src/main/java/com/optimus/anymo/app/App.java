package com.optimus.anymo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.optimus.anymo.R;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Profile;
import com.optimus.anymo.util.CustomRequest;
import com.optimus.anymo.util.LruBitmapCache;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class App extends MultiDexApplication implements Constants {

	public static final String TAG = App.class.getSimpleName();

    private AdmobAdsSettings mAdmobAdsSettings;
    private Tooltips mTooltips;
    private FcmSettings mFcmSettings;
    private Settings mSettings;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static App mInstance;

    private List<Map<String, String>> languages = new ArrayList<>();;

    private SharedPreferences sharedPref;

    private String language = "";

    private InterstitialAd mInterstitialAd;

    private Profile account;

	@Override
	public void onCreate() {

		super.onCreate();
        mInstance = this;

        FirebaseApp.initializeApp(this);

        EmojiManager.install(new GoogleEmojiProvider());

        sharedPref = this.getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);

        // Ads

        mAdmobAdsSettings = new AdmobAdsSettings();

        // Get Tooltips settings

        mTooltips = new Tooltips();
        this.readTooltipsSettings();

        // Get FCM (Push Notifications) settings

        mFcmSettings = new FcmSettings();
        this.readFcmSettings();

        //

        mSettings = new Settings();

        //

        account = new Profile();

        //

        this.readData();

        // Get app languages

        initLanguages();

        // Set App language by locale

        setLocale(getLanguage());

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {


            try {

                ProviderInstaller.installIfNeeded(this);

            } catch (Exception e) {

                e.getMessage();
            }
        }
	}

    private void initLanguages() {

        Map<String, String> map = new HashMap<String, String>();

        map.put("lang_id", "");
        map.put("lang_name", getString(R.string.language_default));

        this.languages.add(map);

        DisplayMetrics metrics = new DisplayMetrics();

        Resources r = getResources();
        Configuration c = r.getConfiguration();
        String[] loc = r.getAssets().getLocales();

        for (String s : loc) {

            String sz_lang_id = "id"; // id and in the same for indonesian language. id must be deleted from list

            c.locale = new Locale(s);
            Resources res = new Resources(getAssets(), metrics, c);
            String s1 = res.getString(R.string.app_lang_code);

            String language = c.locale.getDisplayLanguage();

            c.locale = new Locale("");
            Resources res2 = new Resources(getAssets(), metrics, c);
            String s2 = res2.getString(R.string.app_lang_code);

            if (!s1.equals(s2) && !s.equals(sz_lang_id)) {

                map = new HashMap<String, String>();

                map.put("lang_id", s);
                map.put("lang_name", language);

                this.languages.add(map);
            }
        }
    }

    public List<Map<String, String>> getLanguages() {

        return this.languages;
    }

    public void setLocale(String lang) {

        Locale myLocale;

        if (lang.length() == 0) {

            myLocale = new Locale("");

        } else {

            myLocale = new Locale(lang);
        }

        Resources res = getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();

        conf.setLocale(myLocale);
        conf.setLayoutDirection(myLocale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            getApplicationContext().createConfigurationContext(conf);

        } else {

            res.updateConfiguration(conf, dm);
        }
    }

    public void setLanguage(String language) {

        this.language = language;
    }

    public String getLanguage() {

        if (this.language == null) {

            this.setLanguage("");
        }

        return this.language;
    }

    public String getLanguageNameByCode(String langCode) {

        String language = getString(R.string.language_default);

        for (int i = 1; i < App.getInstance().getLanguages().size(); i++) {

            if (App.getInstance().getLanguages().get(i).get("lang_id").equals(langCode)) {

                language = App.getInstance().getLanguages().get(i).get("lang_name");
            }
        }

        return language;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        setLocale(getLanguage());
    }

    public void setLocation() {

        if (App.getInstance().isConnected() && App.getInstance().getAccount().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_GEO_LOCATION, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

//                                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                Log.d("Set GEO Success", response.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("Set GEO Error", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("accountId", Long.toString(App.getInstance().getAccount().getId()));
                    params.put("accessToken", App.getInstance().getAccount().getAccessToken());
                    params.put("lat", Double.toString(App.getInstance().getAccount().getLat()));
                    params.put("lng", Double.toString(App.getInstance().getAccount().getLng()));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public void showInterstitialAd(Activity activity) {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest,

                new InterstitialAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        Log.i("admob", "onAdLoaded");

                        if (mInterstitialAd != null) {

                           // mInterstitialAd.show(activity);
                            Toast.makeText(getApplicationContext(),"Welcome to Anymo",Toast.LENGTH_SHORT).show();
                        }

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {

                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.

                                        mInterstitialAd = null;

                                        Log.d("admob", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {

                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.

                                        mInterstitialAd = null;

                                        Log.d("admob", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                        // Called when fullscreen content is shown.

                                        Log.d("admob", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        // Handle the error

                        Log.i("admob", loadAdError.getMessage());

                        mInterstitialAd = null;

                        String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.e("admob", "onAdFailedToLoad() with error: " + error);
                    }
                });
    }
    
    public boolean isConnected() {
    	
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	NetworkInfo netInfo = cm.getActiveNetworkInfo();

    	if (netInfo != null && netInfo.isConnectedOrConnecting()) {

    		return true;
    	}

    	return false;
    }

    public void loadSettings() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_GET_SETTINGS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                // Read Interstitial ads settings

                                App.getInstance().getAdmobAdSettings().read_from_json(response);

                                //

                                if (response.has("allowGoogleAuth")) {

                                    App.getInstance().getSettings().setAllowGoogleAuth(response.getInt("allowGoogleAuth"));
                                }

                                //

                                if (response.has("allowOtpVerification")) {

                                    App.getInstance().getSettings().setAllowOtpVerification(response.getInt("allowOtpVerification"));
                                }

                                //

                                if (response.has("messagesCount")) {

                                    App.getInstance().getSettings().setMessagesCount(response.getInt("messagesCount"));
                                }

                                if (response.has("notificationsCount")) {

                                    App.getInstance().getSettings().setNotificationsCount(response.getInt("notificationsCount"));
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.d("App loadSettings()", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("App loadSettings()", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("clientId", CLIENT_ID);

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("lat", Double.toString(App.getInstance().getAccount().getLat()));
                params.put("lng", Double.toString(App.getInstance().getAccount().getLng()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void updateGeoLocation() {

        if (App.getInstance().isConnected() && App.getInstance().getAccount().getId() != 0 && App.getInstance().getAccount().getLat() == 0.000000 && App.getInstance().getAccount().getLat() == 0.000000) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_GEO_LOCATION, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (!response.getBoolean("error")) {

//                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("updateGeoLocation()", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("accountId", Long.toString(App.getInstance().getAccount().getId()));
                    params.put("accessToken", App.getInstance().getAccount().getAccessToken());
                    params.put("lat", Double.toString(App.getInstance().getAccount().getLat()));
                    params.put("lng", Double.toString(App.getInstance().getAccount().getLng()));

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public Boolean authorize(JSONObject authObj) {

        try {

            if (!authObj.has("error")) {

                return false;
            }

            if (authObj.getBoolean("error")) {

                return false;
            }

            if (!authObj.has("account")) {

                return false;
            }

            JSONArray accountArray = authObj.getJSONArray("account");

            if (accountArray.length() > 0) {

                JSONObject accountObj = (JSONObject) accountArray.get(0);

                App.getInstance().setAccount(new Profile(accountObj));

            }

            this.getAccount().setAccessToken(authObj.getString("accessToken"));

            this.saveData();

            return true;

        } catch (JSONException e) {

            e.printStackTrace();

            return false;
        }
    }

    public void setAccount(Profile account) {

        this.account = account;
    }

    public Profile getAccount() {

        return this.account;
    }

    public void setAdmobAdSettings(AdmobAdsSettings admobAdsSettings) {

        this.mAdmobAdsSettings = admobAdsSettings;
    }

    public AdmobAdsSettings getAdmobAdSettings() {

        return this.mAdmobAdsSettings;
    }

    public Settings getSettings() {

        return this.mSettings;
    }

    public FcmSettings getFcmSettings() {

        return this.mFcmSettings;
    }

    public void readFcmSettings() {

        this.getFcmSettings().setNewMessages(sharedPref.getInt(getString(R.string.settings_account_allow_messages_gcm), 1));
        this.getFcmSettings().setNewComments(sharedPref.getInt(getString(R.string.settings_account_allow_comments_gcm), 1));
        this.getFcmSettings().setNewLikes(sharedPref.getInt(getString(R.string.settings_account_allow_likes_gcm), 1));

        Log.e("readFcmSettings", "readFcmSettings");
    }

    public void saveFcmSettings() {

        sharedPref.edit().putInt(getString(R.string.settings_account_allow_messages_gcm), this.getFcmSettings().getNewMessages()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_account_allow_comments_gcm), this.getFcmSettings().getNewComments()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_account_allow_likes_gcm), this.getFcmSettings().getNewLikes()).apply();

        Log.e("saveFcmSettings", "saveFcmSettings");
    }

    public Tooltips getTooltipsSettings() {

        return this.mTooltips;
    }

    public void readTooltipsSettings() {

        this.mTooltips.setShowOtpTooltip(sharedPref.getBoolean(getString(R.string.settings_account_tooltip_otp_verification), true));
        this.mTooltips.setShowLocTooltip(sharedPref.getBoolean(getString(R.string.settings_account_tooltip_loc_access), true));
        this.mTooltips.setShowFollowTooltip(sharedPref.getBoolean(getString(R.string.settings_follow_prompt_tooltip), true));
    }

    public void saveTooltipsSettings() {

        sharedPref.edit().putBoolean(getString(R.string.settings_account_tooltip_otp_verification), this.mTooltips.isAllowShowOtpTooltip()).apply();
        sharedPref.edit().putBoolean(getString(R.string.settings_account_tooltip_loc_access), this.mTooltips.isAllowShowLocTooltip()).apply();
        sharedPref.edit().putBoolean(getString(R.string.settings_follow_prompt_tooltip), this.mTooltips.isAllowShowFollowTooltip()).apply();
    }

    public void readData() {

        this.getAccount().setId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.getAccount().setAccessToken(sharedPref.getString(getString(R.string.settings_account_access_token), ""));

        this.getAccount().setVerified(sharedPref.getInt(getString(R.string.settings_verified_barge), 0));

        this.getSettings().setShowStartupWizard(sharedPref.getInt(getString(R.string.settings_show_startup_wizard), 1));
        this.getSettings().setAllowGoogleAuth(sharedPref.getInt(getString(R.string.settings_allow_google_authorization), 1));
        this.getSettings().setAllowOtpVerification(sharedPref.getInt(getString(R.string.settings_allow_otp_verification), 1));

        this.setLanguage(sharedPref.getString(getString(R.string.settings_language), ""));

        if (this.getAdmobAdSettings() != null) {

            if (sharedPref.contains(getString(R.string.settings_interstitial_ad_after_new_item))) {

                this.getAdmobAdSettings().setAdmobAdAfterItem(sharedPref.getInt(getString(R.string.settings_admob_ad_after_item), 0));
                this.getAdmobAdSettings().setInterstitialAdAfterNewItem(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_item), 0));
                this.getAdmobAdSettings().setInterstitialAdAfterNewLike(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_like), 0));

                this.getAdmobAdSettings().setCurrentInterstitialAdAfterNewItem(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_item_current_val), 0));
                this.getAdmobAdSettings().setCurrentInterstitialAdAfterNewLike(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_like_current_val), 0));
            }
        }

        if (this.getSettings() != null) {

            this.getSettings().setLat(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lat), "0.000000")));
            this.getSettings().setLng(Double.parseDouble(sharedPref.getString(getString(R.string.settings_account_lng), "0.000000")));

            this.getSettings().setArea(sharedPref.getString(getString(R.string.settings_account_area), ""));
            this.getSettings().setCountry(sharedPref.getString(getString(R.string.settings_account_country), ""));
            this.getSettings().setCity(sharedPref.getString(getString(R.string.settings_account_city), ""));

            this.getSettings().setFlowFiltersImages(sharedPref.getInt(getString(R.string.settings_flow_filters_images), 0));
            this.getSettings().setFlowFiltersReports(sharedPref.getInt(getString(R.string.settings_flow_filters_reports), 0));
            this.getSettings().setFlowFiltersDistance(sharedPref.getInt(getString(R.string.settings_flow_filters_distance), 1000));
        }

        this.getAccount().setOtpVerified(sharedPref.getInt(getString(R.string.settings_account_otp_verification), 0));
        this.getAccount().setPhoneNumber(sharedPref.getString(getString(R.string.settings_account_otp_phone_number), ""));
    }

    public void saveData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), this.getAccount().getId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), this.getAccount().getAccessToken()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_verified_barge), this.getAccount().getVerified()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_show_startup_wizard), this.getSettings().getShowStartupWizard()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_allow_google_authorization), this.getSettings().getAllowGoogleAuth()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_allow_otp_verification), this.getSettings().getAllowOtpVerification()).apply();

        sharedPref.edit().putString(getString(R.string.settings_account_lat), Double.toString(this.getSettings().getLat())).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_lng), Double.toString(this.getSettings().getLng())).apply();

        sharedPref.edit().putString(getString(R.string.settings_account_area), this.getSettings().getArea()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_country), this.getSettings().getCountry()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_city), this.getSettings().getCity()).apply();

        sharedPref.edit().putString(getString(R.string.settings_language), this.getLanguage()).apply();

        //

        sharedPref.edit().putInt(getString(R.string.settings_admob_ad_after_item), this.getAdmobAdSettings().getAdmobAdAfterItem()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_item), this.getAdmobAdSettings().getInterstitialAdAfterNewItem()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_like), this.getAdmobAdSettings().getInterstitialAdAfterNewLike()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_item_current_val), this.getAdmobAdSettings().getCurrentInterstitialAdAfterNewItem()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_like_current_val), this.getAdmobAdSettings().getCurrentInterstitialAdAfterNewLike()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_account_otp_verification), this.getAccount().getOtpVerified()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_otp_phone_number), this.getAccount().getPhoneNumber()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_images), this.getSettings().getFlowFiltersImages()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_reports), this.getSettings().getFlowFiltersReports()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_distance), this.getSettings().getFlowFiltersDistance()).apply();
    }

    public void removeData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), "").apply();

        sharedPref.edit().putInt(getString(R.string.settings_verified_barge), 0).apply();
        sharedPref.edit().putInt(getString(R.string.settings_show_startup_wizard), 0).apply();

        //sharedPref.edit().putString(getString(R.string.settings_account_lat), "0.000000").apply();
        //sharedPref.edit().putString(getString(R.string.settings_account_lng), "0.000000").apply();

        sharedPref.edit().putInt(getString(R.string.settings_account_otp_verification), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_otp_phone_number), "").apply();

        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_images), 0).apply();
        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_reports), 0).apply();
        sharedPref.edit().putInt(getString(R.string.settings_flow_filters_distance), 1000).apply();

        // Restore tooltips settings

        App.getInstance().getTooltipsSettings().setShowOtpTooltip(true);
        App.getInstance().getTooltipsSettings().setShowLocTooltip(true);
        App.getInstance().getTooltipsSettings().setShowFollowTooltip(true);
        App.getInstance().saveTooltipsSettings();

        // Restore fcm settings
        App.getInstance().getFcmSettings().setNewMessages(1);
        App.getInstance().getFcmSettings().setNewComments(1);
        App.getInstance().getFcmSettings().setNewLikes(1);
        App.getInstance().saveFcmSettings();
    }

    public static void updateMainActivityBadges(Context context, String message) {

        Intent intent = new Intent(TAG_UPDATE_BADGES);
        intent.putExtra("message", message); // if need message
        context.sendBroadcast(intent);
    }

    public static synchronized App getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {

		getRequestQueue();

		if (mImageLoader == null) {

			mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
		}

		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}