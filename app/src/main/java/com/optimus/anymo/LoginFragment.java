package com.optimus.anymo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Profile;
import com.optimus.anymo.util.CustomRequest;
import com.optimus.anymo.util.Helper;

public class LoginFragment extends Fragment implements Constants {

    SignInButton mGoogleSignInButton;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private ActivityResultLauncher<Intent> googleSigninActivityResultLauncher;

    private ProgressDialog pDialog;

    TextView mForgotPassword;
    Button mSigninButton;
    EditText signinEmail, signinPassword;
    String email, password;
    String oauth_id = "", oauth_name = "", oauth_email = "";
    private int oauth_type = 0;

    private Boolean loading = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            // User is signed in

            FirebaseAuth.getInstance().signOut();
        }

        googleSigninActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    // There are no request codes
                    Intent data = result.getData();

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {

                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        // Signed in successfully, show authenticated UI.

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            // Sign in success, update UI with the signed-in user's information

                                            FirebaseUser user = mAuth.getCurrentUser();

                                            oauth_id = user.getUid();
                                            oauth_name = user.getDisplayName();
                                            oauth_email = user.getEmail();
                                            oauth_type = OAUTH_TYPE_GOOGLE;

                                            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_GOOGLE_AUTH, null,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {

                                                            if (App.getInstance().authorize(response)) {

                                                                if (App.getInstance().getAccount().getState() == ACCOUNT_STATE_ENABLED) {

                                                                    App.getInstance().updateGeoLocation();

                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);

                                                                } else {

                                                                    App.getInstance().setAccount(new Profile());

                                                                    Toast.makeText(getActivity(), getText(R.string.msg_account_blocked), Toast.LENGTH_SHORT).show();
                                                                }

                                                            } else {

                                                                if (oauth_id.length() != 0) {

                                                                    Intent i = new Intent(getActivity(), SignupActivity.class);
                                                                    i.putExtra("oauth_id", oauth_id);
                                                                    i.putExtra("oauth_name", oauth_name);
                                                                    i.putExtra("oauth_email", oauth_email);
                                                                    i.putExtra("oauth_type", oauth_type);
                                                                    startActivity(i);

                                                                } else {

                                                                    Toast.makeText(getActivity(), getString(R.string.error_signin), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            loading = false;

                                                            hidepDialog();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                    Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                                                    loading = false;

                                                    hidepDialog();
                                                }
                                            }) {

                                                @Override
                                                protected Map<String, String> getParams() {
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("client_id", CLIENT_ID);
                                                    params.put("uid", oauth_id);
                                                    params.put("app_type", Integer.toString(APP_TYPE_ANDROID));
                                                    params.put("fcm_regId", App.getInstance().getSettings().getFcmToken());

                                                    return params;
                                                }
                                            };

                                            App.getInstance().addToRequestQueue(jsonReq);

                                        } else {

                                            // If sign in fails, display a message to the user.
                                            Log.e("Google", "signInWithCredential:failure", task.getException());
                                            Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    } catch (ApiException e) {

                        // The ApiException status code indicates the detailed failure reason.
                        // Please refer to the GoogleSignInStatusCodes class reference for more information.
                        Log.e("Google", "Google sign in failed", e);
                    }
                }
            }
        });

        //

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        if (loading) {

            showpDialog();
        }

        // Google Button

        mGoogleSignInButton = rootView.findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);

        setGooglePlusButtonText(mGoogleSignInButton, getString(R.string.action_login_with_google));

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSigninActivityResultLauncher.launch(signInIntent);
            }
        });

        if (App.getInstance().getSettings().getAllowGoogleAuth() == DISABLED) {

            mGoogleSignInButton.setVisibility(View.GONE);
        }

        signinEmail = (EditText) rootView.findViewById(R.id.signinEmail);
        signinPassword = (EditText) rootView.findViewById(R.id.signinPassword);

        mForgotPassword = (TextView) rootView.findViewById(R.id.forgotPassword);

        mForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), RecoveryActivity.class);
                startActivity(i);
            }
        });

        mSigninButton = (Button) rootView.findViewById(R.id.signinBtn);

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = signinEmail.getText().toString();
                password = signinPassword.getText().toString();

                if (!App.getInstance().isConnected()) {

                    Toast.makeText(getActivity(), R.string.msg_network_error, Toast.LENGTH_SHORT).show();

                } else if (!checkEmail() || !checkPassword()) {


                } else {

                    signin();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {

        for (int i = 0; i < signInButton.getChildCount(); i++) {

            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {

                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);

                return;
            }
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    public void signin() {

        loading = true;

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (App.getInstance().authorize(response)) {

                            if (App.getInstance().getAccount().getState() == ACCOUNT_STATE_ENABLED) {

                                App.getInstance().updateGeoLocation();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                if (App.getInstance().getAccount().getState() == ACCOUNT_STATE_BLOCKED) {

                                    App.getInstance().setAccount(new Profile());

                                    Toast.makeText(getActivity(), getText(R.string.msg_account_blocked), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_signin), Toast.LENGTH_SHORT).show();
                        }

                        loading = false;

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), getText(R.string.error_data_loading), Toast.LENGTH_LONG).show();

                loading = false;

                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("client_id", CLIENT_ID);
                params.put("hash", Helper.md5(Helper.md5(email) + CLIENT_SECRET));
                params.put("app_type", Integer.toString(APP_TYPE_ANDROID));
                params.put("fcm_regId", App.getInstance().getSettings().getFcmToken());

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public Boolean checkEmail() {

        email = signinEmail.getText().toString();

        Helper helper = new Helper();

        if (email.length() == 0) {

            signinEmail.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (!helper.isValidEmail(email)) {

            signinEmail.setError(getString(R.string.error_wrong_format));

            return false;
        }

        signinEmail.setError(null);

        return true;
    }

    public Boolean checkPassword() {

        password = signinPassword.getText().toString();

        signinPassword.setError(null);

        Helper helper = new Helper();

        if (password.length() == 0) {

            signinPassword.setError(getString(R.string.error_field_empty));

            return false;
        }

        if (password.length() < 6) {

            signinPassword.setError(getString(R.string.error_small_password));

            return false;
        }

        if (!helper.isValidPassword(password)) {

            signinPassword.setError(getString(R.string.error_wrong_format));

            return false;
        }

        return  true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}