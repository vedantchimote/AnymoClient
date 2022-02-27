package com.optimus.anymo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Profile;
import com.optimus.anymo.util.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends Fragment implements Constants {

    private NestedScrollView mNestedView;

    private LinearLayout mLanguagePanel, mMessagesPanel, mFcmPanel, mOthersPanel, mAboutPanel, mTermsPanel, mThanksPanel, mVersionPanel, mLogoutPanel, mSupportPanel, mPasswordPanel, mBlackListPanel, mServicesPanel, mOtpPanel;
    private TextView mLanguagePanelTitle, mLanguagePanelSubtitle, mMessagesTitle, mMessagesSubtitle, mFcmPanelTitle, mLogoutDetails;

    private SwitchCompat mMessagesSwitch, mFcmMessagesSwitch, mFcmLikesSwitch, mFcmCommentsSwitch;

    private ProgressDialog pDialog;
    private Boolean loading = false;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mNestedView = (NestedScrollView) rootView.findViewById(R.id.nested_view);

        //

        mPasswordPanel = (LinearLayout) rootView.findViewById(R.id.password_panel);

        mPasswordPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        mServicesPanel = (LinearLayout) rootView.findViewById(R.id.services_panel);

        mServicesPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ServicesActivity.class);
                startActivity(i);
            }
        });

        mBlackListPanel = (LinearLayout) rootView.findViewById(R.id.panel_black_list);

        mBlackListPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), BlackListActivity.class);
                startActivity(i);
            }
        });

        mOtpPanel = (LinearLayout) rootView.findViewById(R.id.otp_panel);

        if (App.getInstance().getSettings().getAllowOtpVerification() != ENABLED) {

            mOtpPanel.setVisibility(View.GONE);
        }

        mOtpPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), OtpVerificationActivity.class);
                startActivity(i);
            }
        });

        mTermsPanel = (LinearLayout) rootView.findViewById(R.id.terms_panel);

        mTermsPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);
            }
        });

        mThanksPanel = (LinearLayout) rootView.findViewById(R.id.thanks_panel);

        mThanksPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_THANKS);
                i.putExtra("title", getText(R.string.settings_thanks));
                startActivity(i);
            }
        });

        mVersionPanel = (LinearLayout) rootView.findViewById(R.id.version_panel);

        mVersionPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                // alertDialog.setTitle(getText(R.string.action_about));

                LinearLayout aboutDialogContent;
                TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

                aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                alertDialog.setView(aboutDialogContent);

                aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                aboutDialogAppName.setText(getString(R.string.app_name));
                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));
                aboutDialogAppCopyright.setText("Copyright © " + getString(R.string.app_year) + " " + getString(R.string.app_copyright));

                // alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton(getText(R.string.action_visit_site), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_site)));
                        startActivity(browserIntent);

                        dialog.cancel();
                    }
                });
                alertDialog.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        mLogoutDetails = (TextView) rootView.findViewById(R.id.logout_details);
        mLogoutDetails.setText(App.getInstance().getAccount().getEmail());

        mLogoutPanel = (LinearLayout) rootView.findViewById(R.id.logout_panel);

        mLogoutPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_logout));

                alertDialog.setMessage(getText(R.string.msg_action_logout));
                alertDialog.setCancelable(true);

                alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        loading = true;

                        showpDialog();

                        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            if (!response.getBoolean("error")) {

                                                Log.d("Logout", "Logout success");
                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();

                                        } finally {

                                            loading = false;

                                            hidepDialog();

                                            App.getInstance().removeData();
                                            App.getInstance().readData();

                                            App.getInstance().getSettings().setNotificationsCount(0);
                                            App.getInstance().getSettings().setMessagesCount(0);

                                            App.getInstance().setAccount(new Profile());

                                            updateView();

                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                loading = false;

                                hidepDialog();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {

                                Map<String, String> params = new HashMap<String, String>();

                                params.put("clientId", CLIENT_ID);
                                params.put("accountId", Long.toString(App.getInstance().getAccount().getId()));
                                params.put("accessToken", App.getInstance().getAccount().getAccessToken());

                                return params;
                            }
                        };

                        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(VOLLEY_REQUEST_SECONDS), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                        jsonReq.setRetryPolicy(policy);

                        App.getInstance().addToRequestQueue(jsonReq);
                    }
                });

                alertDialog.show();
            }
        });

        mSupportPanel = (LinearLayout) rootView.findViewById(R.id.support_panel);

        mSupportPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), SupportActivity.class);
                startActivity(i);
            }
        });

        // Language

        mLanguagePanel = (LinearLayout) rootView.findViewById(R.id.language_panel);

        mLanguagePanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<String> language_names = new ArrayList<String>();

                Resources r = getResources();
                Configuration c = r.getConfiguration();

                for (int i = 0; i < App.getInstance().getLanguages().size(); i++) {

                    language_names.add(App.getInstance().getLanguages().get(i).get("lang_name"));
                }

                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(getActivity());
                b.setTitle(getText(R.string.title_select_language));

                b.setItems(language_names.toArray(new CharSequence[language_names.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        App.getInstance().setLanguage(App.getInstance().getLanguages().get(which).get("lang_id"));

                        App.getInstance().saveData();

                        // Set App Language

                        App.getInstance().setLocale(App.getInstance().getLanguage());

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

                b.setNegativeButton(getText(R.string.action_cancel), null);

                AlertDialog d = b.create();
                d.show();
            }
        });

        mLanguagePanelTitle = (TextView) rootView.findViewById(R.id.language_panel_title);
        mLanguagePanelSubtitle = (TextView) rootView.findViewById(R.id.language_panel_subtitle);

        mLanguagePanelSubtitle.setText(App.getInstance().getLanguageNameByCode(App.getInstance().getLanguage()));

        // Messages

        mMessagesPanel = (LinearLayout) rootView.findViewById(R.id.allow_messages_panel);

        mMessagesTitle = (TextView) rootView.findViewById(R.id.allow_messages_title);
        mMessagesSubtitle = (TextView) rootView.findViewById(R.id.allow_messages_subtitle);

        mMessagesSwitch = (SwitchCompat) rootView.findViewById(R.id.allow_messages_switch);

        // Push notifications

        mFcmPanel = (LinearLayout) rootView.findViewById(R.id.push_notifications_panel);
        mFcmPanelTitle = (TextView) rootView.findViewById(R.id.push_notifications_panel_title);

        mFcmMessagesSwitch = (SwitchCompat) rootView.findViewById(R.id.fcm_messages_switch);
        mFcmLikesSwitch = (SwitchCompat) rootView.findViewById(R.id.fcm_likes_switch);
        mFcmCommentsSwitch = (SwitchCompat) rootView.findViewById(R.id.fcm_comments_switch);

        //

        mAboutPanel = (LinearLayout) rootView.findViewById(R.id.about_panel);
        mOthersPanel = (LinearLayout) rootView.findViewById(R.id.others_panel);

        //

        if (loading) {

            showpDialog();
        }

        updateView();

        return rootView;
    }

    private void updateView() {

        mOthersPanel.setVisibility(View.VISIBLE);
        mFcmPanel.setVisibility(View.VISIBLE);
        mMessagesPanel.setVisibility(View.VISIBLE);

        if (App.getInstance().getAccount().getId() == 0) {

            mOthersPanel.setVisibility(View.GONE);
            mFcmPanel.setVisibility(View.GONE);
            mMessagesPanel.setVisibility(View.GONE);
        }

        mMessagesSwitch.setOnCheckedChangeListener(null);

        mMessagesSwitch.setChecked(false);

        if (App.getInstance().getAccount().getAllowMessages() == Constants.ENABLED) {

            mMessagesSwitch.setChecked(true);
        }

        mMessagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    App.getInstance().getAccount().setAllowMessages(1);

                } else {

                    App.getInstance().getAccount().setAllowMessages(0);
                }

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SET_ALLOW_MESSAGES, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("onCheckedChanged", "Error");
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("clientId", CLIENT_ID);
                        params.put("accountId", Long.toString(App.getInstance().getAccount().getId()));
                        params.put("accessToken", App.getInstance().getAccount().getAccessToken());

                        params.put("allowMessages", Integer.toString(App.getInstance().getAccount().getAllowMessages()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });

        //

        mFcmMessagesSwitch.setOnCheckedChangeListener(null);

        mFcmMessagesSwitch.setChecked(false);

        if (App.getInstance().getFcmSettings().getNewMessages() == Constants.ENABLED) {

            mFcmMessagesSwitch.setChecked(true);
        }

        mFcmMessagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    App.getInstance().getFcmSettings().setNewMessages(1);

                } else {

                    App.getInstance().getFcmSettings().setNewMessages(0);
                }

                App.getInstance().saveFcmSettings();

                Log.e("onCheckedChanged", "mMessagesSwitch");
            }
        });

        //

        mFcmLikesSwitch.setOnCheckedChangeListener(null);

        mFcmLikesSwitch.setChecked(false);

        if (App.getInstance().getFcmSettings().getNewLikes() == Constants.ENABLED) {

            mFcmLikesSwitch.setChecked(true);
        }

        mFcmLikesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    App.getInstance().getFcmSettings().setNewLikes(1);

                } else {

                    App.getInstance().getFcmSettings().setNewLikes(0);
                }

                App.getInstance().saveFcmSettings();
            }
        });

        //

        mFcmCommentsSwitch.setOnCheckedChangeListener(null);

        mFcmCommentsSwitch.setChecked(false);

        if (App.getInstance().getFcmSettings().getNewComments() == Constants.ENABLED) {

            mFcmCommentsSwitch.setChecked(true);
        }

        mFcmCommentsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    App.getInstance().getFcmSettings().setNewComments(1);

                } else {

                    App.getInstance().getFcmSettings().setNewComments(0);
                }

                App.getInstance().saveFcmSettings();
            }
        });

        //

        if (App.getInstance().getSettings().getAllowGoogleAuth() == DISABLED) {

            mServicesPanel.setVisibility(View.GONE);
        }
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}