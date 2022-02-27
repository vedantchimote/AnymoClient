package com.optimus.anymo.app;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.util.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Settings extends Application implements Constants {

	public static final String TAG = Settings.class.getSimpleName();

    private int allowOtpVerification = 1;
    private int allowGoogleAuth = 1;
    private int showStartupWizard = 1;
    private int notificationsCount = 0, messagesCount = 0;
    private long currentChatId = 0;
    private String fcmToken = "";
    private Double lat = 0.000000, lng = 0.000000;
    private int flowFiltersImages = 0, flowFiltersReports = 0, flowFiltersDistance = 5000;
    private String area = "", country = "", city = "";
    private Boolean geolocation_updated = false;

	@Override
	public void onCreate() {

		super.onCreate();
	}

    public void setFlowFiltersImages(int flowFiltersImages) {

        this.flowFiltersImages = flowFiltersImages;
    }

    public int getFlowFiltersImages() {

        return this.flowFiltersImages;
    }

    public void setFlowFiltersReports(int flowFiltersReports) {

        this.flowFiltersReports = flowFiltersReports;
    }

    public int getFlowFiltersReports() {

        return this.flowFiltersReports;
    }

    public void setFlowFiltersDistance(int flowFiltersDistance) {

        this.flowFiltersDistance = flowFiltersDistance;
    }

    public int getFlowFiltersDistance() {

        return this.flowFiltersDistance;
    }

    public void setNotificationsCount(int notificationsCount) {

        this.notificationsCount = notificationsCount;
    }

    public int getNotificationsCount() {

        return this.notificationsCount;
    }

    public void setMessagesCount(int messagesCount) {

        this.messagesCount = messagesCount;
    }

    public int getMessagesCount() {

        return this.messagesCount;
    }

    public void setCurrentChatId(long currentChatId) {

        this.currentChatId = currentChatId;
    }

    public long getCurrentChatId() {

        return this.currentChatId;
    }

    public void setShowStartupWizard(int showStartupWizard) {

        this.showStartupWizard = showStartupWizard;
    }

    public int getShowStartupWizard() {

        return this.showStartupWizard;
    }

    public void setAllowGoogleAuth(int allowGoogleAuth) {

        this.allowGoogleAuth = allowGoogleAuth;
    }

    public int getAllowGoogleAuth() {

        return this.allowGoogleAuth;
    }

    public void setAllowOtpVerification(int allowOtpVerification) {

        this.allowOtpVerification = allowOtpVerification;
    }

    public int getAllowOtpVerification() {

        return this.allowOtpVerification;
    }

    public void setGeolocationUpdated(Boolean geolocation_updated) {

        this.geolocation_updated = geolocation_updated;
    }

    public Boolean getGeolocationUpdated() {

        return this.geolocation_updated;
    }

    public String getFcmToken() {

        if (this.fcmToken == null) {

            this.fcmToken = "";
        }

        return this.fcmToken;
    }

    public void setFcmToken(final String fcmToken) {

        if (App.getInstance().getAccount().getId() != 0 && App.getInstance().getAccount().getAccessToken().length() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_UPDATE_FCM_TOKEN, null,
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

                    Log.e("setGcmToken", error.toString());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("accountId", Long.toString(App.getInstance().getAccount().getId()));
                    params.put("accessToken", App.getInstance().getAccount().getAccessToken());

                    params.put("fcm_regId", fcmToken);

                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);
        }

        this.fcmToken = fcmToken;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public Double getLat() {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        return this.lat;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public Double getLng() {

        if (this.lng == null) {

            this.lng = 0.000000;
        }

        return this.lng;
    }

    public void getAddress(Double lat, Double lng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(App.getInstance().getApplicationContext(), Locale.US);

        try {

            addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && addresses.size() > 0) {

                this.setCity(addresses.get(0).getLocality());
                this.setArea(addresses.get(0).getAdminArea());
                this.setCountry(addresses.get(0).getCountryName());

                if (this.getCity().length() == 0) {

                    this.setCity("Unknown");
                }

                if (this.getArea().length() == 0) {

                    this.setArea("Unknown");
                }

                if (this.getCountry().length() == 0) {

                    this.setCountry("Unknown");
                }

            } else {

                this.setCity("Unknown");
                this.setArea("Unknown");
                this.setCountry("Unknown");
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCountry() {

        if (this.country == null) {

            this.setCountry("");
        }

        return this.country;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public String getCity() {

        if (this.city == null) {

            this.setCity("");
        }

        return this.city;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getArea() {

        if (this.area == null) {

            this.setArea("");
        }

        return this.area;
    }
}