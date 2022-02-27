package com.optimus.anymo.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.R;
import com.optimus.anymo.constants.Constants;

public class AdmobAdsSettings extends Application implements Constants {

	public static final String TAG = AdmobAdsSettings.class.getSimpleName();

    private SharedPreferences sharedPref;
    private static Resources res;

    private int interstitialAdAfterNewItem = 0, interstitialAdAfterNewLike = 2, admobAdAfterItem = 1;
    private int currentInterstitialAdAfterNewItem = 0, currentInterstitialAdAfterNewLike = 0;

	@Override
	public void onCreate() {

		super.onCreate();

        this.res = getResources();

        sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);
	}

    public void read_from_json(JSONObject jsonData) {

        try {

            if (jsonData.has("admobAdAfterItem")) {

                this.setAdmobAdAfterItem(jsonData.getInt("admobAdAfterItem"));
            }

            if (jsonData.has("interstitialAdAfterNewItem")) {

                this.setInterstitialAdAfterNewItem(jsonData.getInt("interstitialAdAfterNewItem"));
            }

            if (jsonData.has("interstitialAdAfterNewLike")) {

                this.setInterstitialAdAfterNewLike(jsonData.getInt("interstitialAdAfterNewLike"));
            }

        } catch (Throwable t) {

            Log.e("AdmobAdsSettings", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.e("AdmobAdsSettings", "");
        }
    }

    public void read_settings() {

        if (sharedPref.contains(getString(R.string.settings_interstitial_ad_after_new_item))) {

            this.setInterstitialAdAfterNewItem(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_item), 0));
            this.setInterstitialAdAfterNewLike(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_like), 0));

            this.setCurrentInterstitialAdAfterNewItem(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_item_current_val), 0));
            this.setCurrentInterstitialAdAfterNewLike(sharedPref.getInt(getString(R.string.settings_interstitial_ad_after_new_like_current_val), 0));
        }

        Log.e("AdmobAdsSettings", "read settings");
    }

    public void save_settings() {

        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_item), this.getInterstitialAdAfterNewItem()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_like), this.getInterstitialAdAfterNewLike()).apply();

        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_item_current_val), this.getCurrentInterstitialAdAfterNewItem()).apply();
        sharedPref.edit().putInt(getString(R.string.settings_interstitial_ad_after_new_like_current_val), this.getCurrentInterstitialAdAfterNewLike()).apply();

        Log.e("AdmobAdsSettings", "save settings");
    }

    public void setAdmobAdAfterItem(int admobAdAfterItem) {

        this.admobAdAfterItem = admobAdAfterItem;
    }

    public int getAdmobAdAfterItem() {

        return this.admobAdAfterItem;
    }

    public void setInterstitialAdAfterNewItem(int interstitialAdAfterNewItem) {

        this.interstitialAdAfterNewItem = interstitialAdAfterNewItem;
    }

    public int getInterstitialAdAfterNewItem() {

        return this.interstitialAdAfterNewItem;
    }

    public void setCurrentInterstitialAdAfterNewItem(int currentInterstitialAdAfterNewItem) {

        this.currentInterstitialAdAfterNewItem = currentInterstitialAdAfterNewItem;
    }

    public int getCurrentInterstitialAdAfterNewItem() {

        return this.currentInterstitialAdAfterNewItem;
    }

    //

    public void setInterstitialAdAfterNewLike(int interstitialAdAfterNewLike) {

        this.interstitialAdAfterNewLike = interstitialAdAfterNewLike;
    }

    public int getInterstitialAdAfterNewLike() {

        return this.interstitialAdAfterNewLike;
    }

    public void setCurrentInterstitialAdAfterNewLike(int currentInterstitialAdAfterNewLike) {

        this.currentInterstitialAdAfterNewLike = currentInterstitialAdAfterNewLike;
    }

    public int getCurrentInterstitialAdAfterNewLike() {

        return this.currentInterstitialAdAfterNewLike;
    }
}