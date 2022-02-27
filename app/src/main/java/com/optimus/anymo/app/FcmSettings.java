package com.optimus.anymo.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.optimus.anymo.R;
import com.optimus.anymo.constants.Constants;

public class FcmSettings extends Application implements Constants {

	public static final String TAG = FcmSettings.class.getSimpleName();

    private SharedPreferences sharedPref;
    private static Resources res;

    private int new_comments = 1, new_likes = 1, new_messages = 1;

	@Override
	public void onCreate() {

		super.onCreate();

        this.res = getResources();

        sharedPref = getSharedPreferences(getString(R.string.settings_file), Context.MODE_PRIVATE);
	}

    public void setNewComments(int new_comments) {

        this.new_comments = new_comments;
    }

    public int getNewComments() {

        return this.new_comments;
    }

    public void setNewLikes(int new_likes) {

        this.new_likes = new_likes;
    }

    public int getNewLikes() {

        return this.new_likes;
    }

    public void setNewMessages(int new_messages) {

        this.new_messages = new_messages;
    }

    public int getNewMessages() {

        return this.new_messages;
    }
}