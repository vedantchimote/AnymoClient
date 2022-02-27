package com.optimus.anymo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.optimus.anymo.app.App;
import com.optimus.anymo.common.ActivityBase;
import com.optimus.anymo.util.CustomRequest;
import com.optimus.anymo.util.Helper;

public class AppActivity extends ActivityBase {

    private ProgressBar mProgressBar;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    private ViewPager mViewPager;
    private WizardViewPagerAdapter myWizardPagerAdapter;
    private Button mNextButton;
    private LinearLayout mDotsLayout;

    private int about_images_array[] = {

            R.drawable.ic_start_welcome,
            R.drawable.ic_start_items,
            R.drawable.ic_start_chats,
            R.drawable.ic_start_likes,
            R.drawable.ic_start_geo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);

        // Get Firebase token

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (!task.isSuccessful()) {

                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());

                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                App.getInstance().getSettings().setFcmToken(token);
            }
        });


        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    if (task.isSuccessful() && task.getResult() != null) {

                        mLastLocation = task.getResult();

                        // Set geo data to App class

                        App.getInstance().getSettings().setLat(mLastLocation.getLatitude());
                        App.getInstance().getSettings().setLng(mLastLocation.getLongitude());

                        App.getInstance().getSettings().getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                        // Save data

                        App.getInstance().saveData();

                        // Send location data to server

                        App.getInstance().setLocation();

                        Log.e("lat", Double.toString(mLastLocation.getLatitude()));
                        Log.e("lng", Double.toString(mLastLocation.getLongitude()));

                    } else {

                        Log.d("GPS", "AppActivity getLastLocation:exception", task.getException());
                    }
                }
            });
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mDotsLayout = (LinearLayout) findViewById(R.id.layout_dots);

        mNextButton = (Button) findViewById(R.id.next_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = mViewPager.getCurrentItem() + 1;

                if (current < getResources().getStringArray(R.array.welcome_wizard_titles).length) {

                    mViewPager.setCurrentItem(current);

                } else {

                    App.getInstance().getSettings().setShowStartupWizard(0);
                    App.getInstance().saveData();

                    showMainActivity();
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        myWizardPagerAdapter = new WizardViewPagerAdapter();

        mViewPager.setAdapter(myWizardPagerAdapter);
        mViewPager.addOnPageChangeListener(wizardPageChangeListener);

        showWizardDots(0);

        Helper.setSystemBarColor(this, R.color.grey_5);
        Helper.setSystemBarLight(this);

        App.getInstance().loadSettings();

        showLoadingScreen();
    }

    @Override
    protected void onStart() {

        super.onStart();

        if (App.getInstance().isConnected() && App.getInstance().getAccount().getId() != 0) {

            showLoadingScreen();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_AUTHORIZE, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (App.getInstance().authorize(response)) {

                                if (App.getInstance().getAccount().getState() == ACCOUNT_STATE_ENABLED) {

                                    App.getInstance().updateGeoLocation();

                                    showMainActivity();

                                } else {

                                    App.getInstance().removeData();
                                    App.getInstance().readData();

                                    showMainActivity();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (App.getInstance().getSettings().getShowStartupWizard() == 1) {

                        showContentScreen();

                    } else {

                        showMainActivity();
                    }
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("client_id", CLIENT_ID);

                    params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                    params.put("access_token", App.getInstance().getAccount().getAccessToken());

                    params.put("app_type", Integer.toString(APP_TYPE_ANDROID));
                    params.put("fcm_regId", App.getInstance().getSettings().getFcmToken());

                    return params;
                }
            };

            RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);

        } else {

            if (App.getInstance().getSettings().getShowStartupWizard() == 1) {

                showContentScreen();

            } else {

                showMainActivity();
            }
        }
    }

    private void showMainActivity() {

        Intent intent = new Intent(AppActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showLoadingScreen() {

        mProgressBar.setVisibility(View.VISIBLE);

        mNextButton.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mDotsLayout.setVisibility(View.GONE);
    }

    private void showContentScreen() {

        mProgressBar.setVisibility(View.GONE);

        mNextButton.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mDotsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void showWizardDots(int current_index) {

        ImageView[] dots = new ImageView[getResources().getStringArray(R.array.welcome_wizard_titles).length];

        mDotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            mDotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {

            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    ViewPager.OnPageChangeListener wizardPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {

            showWizardDots(position);

            if (position == getResources().getStringArray(R.array.welcome_wizard_titles).length - 1) {

                mNextButton.setText(getString(R.string.action_start));
                mNextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mNextButton.setTextColor(Color.WHITE);

            } else {

                mNextButton.setText(getString(R.string.action_next));
                mNextButton.setBackgroundColor(getResources().getColor(R.color.grey_10));
                mNextButton.setTextColor(getResources().getColor(R.color.grey_90));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class WizardViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public WizardViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.start_wizard, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(getResources().getStringArray(R.array.welcome_wizard_titles)[position]);
            ((TextView) view.findViewById(R.id.description)).setText(getResources().getStringArray(R.array.welcome_wizard_descriptions)[position]);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(about_images_array[position]);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {

            return getResources().getStringArray(R.array.welcome_wizard_titles).length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {

            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            View view = (View) object;
            container.removeView(view);
        }
    }
}
