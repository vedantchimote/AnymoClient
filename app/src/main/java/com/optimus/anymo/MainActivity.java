package com.optimus.anymo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimus.anymo.app.App;
import com.optimus.anymo.common.ActivityBase;
import com.optimus.anymo.constants.Constants;

public class MainActivity extends ActivityBase {

    Toolbar mToolbar;

    private AppBarLayout mAppBarLayout;

    ViewPager mViewPager;
    TabLayout mTabLayout;

    private ImageView mNavMain, mNavFavorites, mNavMessages, mNavSettings;
    private TextView mTitleLabel, mMessagesCounter;

    public ImageButton mFiltersButton;

    private FloatingActionButton mNewItemButton;

    Boolean action = false;

    int pageId = PAGE_MAIN;

    private View messenger_badge;

    private Boolean restore = false;

    private Fragment fragment;
    private String mTitle = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // get intent

        Intent i = getIntent();

        pageId = i.getIntExtra("pageId", PAGE_MAIN);

        // Initialize Google Admob

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");

            pageId = savedInstanceState.getInt("pageId", PAGE_MAIN);

        } else {

            fragment = new Fragment();

            restore = false;
            mTitle = getString(R.string.app_name);

            pageId = PAGE_MAIN;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        mTitleLabel = (TextView) findViewById(R.id.title_label);
        mMessagesCounter = (TextView) findViewById(R.id.nav_messages_counter);
        mFiltersButton = (ImageButton) findViewById(R.id.filters_button);

        mNewItemButton = (FloatingActionButton) findViewById(R.id.new_item_button);

        mNewItemButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (App.getInstance().getAccount().getId() == 0) {

                    displayAuthMessage();

                } else {

                    if (App.getInstance().getSettings().getLat() == 0.0 || App.getInstance().getSettings().getLng() == 0.0) {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle(getString(R.string.app_name));
                        alertDialog.setMessage(getString(R.string.msg_geo_not_found));

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_continue), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                App.getInstance().getSettings().setLat(Constants.DEFAULT_LAT);
                                App.getInstance().getSettings().setLng(Constants.DEFAULT_LNG);

                                App.getInstance().getSettings().setGeolocationUpdated(true);

                                App.getInstance().getSettings().getAddress(App.getInstance().getSettings().getLat(), App.getInstance().getSettings().getLng());

                                App.getInstance().saveData();

                                Intent i = new Intent(MainActivity.this, ItemNewActivity.class);
                                startActivity(i);
                            }
                        });

                        alertDialog.show();

                    } else {

                        Intent i = new Intent(MainActivity.this, ItemNewActivity.class);
                        startActivity(i);
                    }
                }
            }
        });

        mNavSettings = (ImageView) findViewById(R.id.nav_settings);

        mNavSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (pageId != PAGE_SETTINGS) {

                    displayFragment(PAGE_SETTINGS, getString(R.string.title_activity_settings));
                }
            }
        });

        mNavMain = (ImageView) findViewById(R.id.nav_main);

        mNavMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (pageId != PAGE_MAIN) {

                    displayFragment(PAGE_MAIN, getString(R.string.app_name));
                }
            }
        });

        mNavFavorites = (ImageView) findViewById(R.id.nav_favorites);

        mNavFavorites.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (App.getInstance().getAccount().getId() == 0) {

                    displayAuthMessage();

                } else {

                    if (pageId != PAGE_FAVORITES) {

                        displayFragment(PAGE_FAVORITES, getString(R.string.title_activity_favorites));
                    }
                }
            }
        });

        mNavMessages = (ImageView) findViewById(R.id.nav_messages);

        mNavMessages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (App.getInstance().getAccount().getId() == 0) {

                    displayAuthMessage();

                } else {

                    if (pageId != PAGE_MESSAGES) {

                        displayFragment(PAGE_MESSAGES, getString(R.string.title_activity_dialogs));
                    }
                }
            }
        });

        if (!restore) {

            switch (pageId) {

                case PAGE_SETTINGS: {

                    displayFragment(PAGE_SETTINGS, getString(R.string.title_activity_settings));

                    break;
                }

                case PAGE_MESSAGES: {

                    displayFragment(PAGE_MESSAGES, getString(R.string.title_activity_settings));

                    break;
                }

                default: {

                    // Show default section "Media"

                    displayFragment(PAGE_MAIN, getString(R.string.app_name));

                    break;
                }
            }
        }

        updateView();

        refreshBadges();
    }

    private void displayAuthMessage() {

        AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialog.setTitle(getText(R.string.dlg_authorization_title));

        alertDialog.setMessage(getText(R.string.dlg_authorization_msg));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_login), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_signup), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);

                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void displayFragment(int id, String title) {

        action = false;

        mTitle = title;

        switch (id) {

            case PAGE_MAIN: {

                pageId = PAGE_MAIN;

                fragment = new FlowFragment();

                action = true;

                break;
            }

            case PAGE_FAVORITES: {

                pageId = PAGE_FAVORITES;

                fragment = new FavoritesFragment();

                action = true;

                break;
            }

            case PAGE_MESSAGES: {

                pageId = PAGE_MESSAGES;

                fragment = new DialogsFragment();

                action = true;

                break;
            }

            case PAGE_SETTINGS: {

                pageId = PAGE_SETTINGS;

                fragment = new SettingsFragment();

                mNavSettings.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                action = true;

                break;
            }
        }

        updateView();

        if (action && fragment != null) {

            //getSupportActionBar().setTitle(title);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }
    }

    private void updateView() {

        mTitleLabel.setText(mTitle);

        mFiltersButton.setVisibility(View.GONE);

        mNavMain.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));
        mNavFavorites.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));
        mNavMessages.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));
        mNavSettings.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint));

        mMessagesCounter.setVisibility(View.GONE);

        if (App.getInstance().getSettings().getMessagesCount() != 0) {

            mMessagesCounter.setVisibility(View.VISIBLE);
        }

        switch (pageId) {

            case PAGE_MAIN: {

                mFiltersButton.setVisibility(View.VISIBLE);

                mNavMain.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint_active));

                break;
            }

            case PAGE_FAVORITES: {

                mNavFavorites.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint_active));

                break;
            }

            case PAGE_MESSAGES: {

                mNavMessages.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint_active));

                break;
            }

            case PAGE_SETTINGS: {

                mNavSettings.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.icon_tint_active));

                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("pageId", pageId);
        outState.putString("mTitle", mTitle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void refreshBadges() {

        //invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onResume() {

        super.onResume();

        //refreshBadges();

        registerReceiver(mMessageReceiver, new IntentFilter(TAG_UPDATE_BADGES));
    }

    @Override
    public void onPause() {

        super.onPause();

        unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            // String message = intent.getStringExtra("message");

            refreshBadges();
        }
    };
}
