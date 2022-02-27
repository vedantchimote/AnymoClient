package com.optimus.anymo;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.util.CustomRequest;
import com.optimus.anymo.util.Helper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.vanniktech.emoji.EmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ItemNewFragment extends Fragment implements Constants {

    public static final int RESULT_OK = -1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ProgressDialog pDialog;

    private EmojiEditText mTextView;
    private ImageView mImageView;
    private ImageButton mDeleteImage, mAddImage;
    private LinearLayout mChoiceTextColorPanel, mChoiceBgColorPanel, mChoiceBgBlurPanel, mChoiceBgAlphaPanel;
    private SwitchCompat mAllowCommentsSwitch, mAllowMessagesSwitch;
    private TextView mTextColorPreview, mBgColorPreview, mBlurPreview, mAlphaPreview;
    private AppCompatSeekBar mBlurSeekBar, mAlphaSeekBar;
    private Button mPostButton;

    private String text = "", imgUrl = "";
    private String textColor = "", bgColor = "";

    private int text_red = 255, text_green = 255, text_blue = 255;
    private int bg_red = 254, bg_green = 120, bg_blue = 10;

    private int allowComments = 1, allowMessages = 1;
    private int imgBlur = 5, imgAlpha = 90;

    private Uri selectedImage;

    private String selectedImagePath = "", newImageFileName = "";

    private ActivityResultLauncher<Intent> imgFromGalleryActivityResultLauncher;
    private ActivityResultLauncher<Intent> imgFromCameraActivityResultLauncher;
    private ActivityResultLauncher<String[]> storagePermissionLauncher;

    private Boolean loading = false;

    public ItemNewFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        initpDialog();

        int random = ThreadLocalRandom.current().nextInt(0, 6);

        switch (random) {

            case 0: {

                // orange

                bg_red = 254;
                bg_green = 120;
                bg_blue = 10;

                break;
            }

            case 1: {

                // green

                bg_red = 56;
                bg_green = 120;
                bg_blue = 10;

                break;
            }

            case 2: {

                // red

                bg_red = 215;
                bg_green = 86;
                bg_blue = 65;

                break;
            }

            case 3: {

                // purple

                bg_red = 166;
                bg_green = 43;
                bg_blue = 236;

                break;
            }

            case 4: {

                // blue

                bg_red = 77;
                bg_green = 84;
                bg_blue = 236;

                break;
            }

            default: {

                // pink

                bg_red = 255;
                bg_green = 97;
                bg_blue = 134;

                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_new, container, false);

        if (loading) {

            showpDialog();
        }

        //

        imgFromGalleryActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

                    if (result.getData() != null) {

                        selectedImage = result.getData().getData();

                        newImageFileName = Helper.randomString(6) + ".jpg";

                        Helper helper = new Helper(getContext());
                        helper.saveImg(selectedImage, newImageFileName);

                        selectedImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + newImageFileName;

                        mImageView.setImageURI(null);
                        mImageView.setImageURI(FileProvider.getUriForFile(App.getInstance().getApplicationContext(), App.getInstance().getPackageName() + ".provider", new File(selectedImagePath)));

                        updateView();
                    }
                }
            }
        });

        imgFromCameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    if (result.getData() != null) {

                        selectedImage = result.getData().getData();

                        selectedImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + newImageFileName;

                        mImageView.setImageURI(null);
                        mImageView.setImageURI(FileProvider.getUriForFile(App.getInstance().getApplicationContext(), App.getInstance().getPackageName() + ".provider", new File(selectedImagePath)));

                        updateView();
                    }
                }
            }
        });

        //

        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> isGranted) -> {

            boolean granted = false;

            for (Map.Entry<String, Boolean> x : isGranted.entrySet()) {

                if (x.getKey().equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    if (x.getValue()) {

                        granted = true;
                    }
                }
            }

            if (granted) {

                Log.e("Permissions", "granted");

                showChooseImageDialog();

            } else {

                Log.e("Permissions", "denied");

                Snackbar.make(getView(), getString(R.string.label_no_storage_permission) , Snackbar.LENGTH_LONG).setAction(getString(R.string.action_settings), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + App.getInstance().getPackageName()));
                        startActivity(appSettingsIntent);

                        Toast.makeText(getActivity(), getString(R.string.label_grant_storage_permission), Toast.LENGTH_SHORT).show();
                    }

                }).show();
            }

        });

        //

        mPostButton = rootView.findViewById(R.id.post_button);

        mPostButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedImagePath != null && selectedImagePath.length() != 0) {

                    File f = new File(selectedImagePath);

                    uploadFile(METHOD_ITEMS_UPLOAD_IMG, f);

                } else {

                    sendPost();
                }
            }
        });

        //

        mAlphaPreview = rootView.findViewById(R.id.tv_alpha);
        mAlphaSeekBar = rootView.findViewById(R.id.seekbar_alpha);

        //

        mBlurPreview = rootView.findViewById(R.id.tv_blur);
        mBlurSeekBar = rootView.findViewById(R.id.seekbar_blur);

        //

        mImageView = rootView.findViewById(R.id.image_view);

        mTextView = rootView.findViewById(R.id.text_view);

        mTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_ITEM_TEXT_SIZE) });
        mTextView.setHorizontallyScrolling(false);
        mTextView.setMaxLines(5);

        mTextView.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    mTextView.clearFocus();

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }

                return false;
            }
        });

        mTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {

                    text = mTextView.getText().toString().trim();

                    updateView();
                }
            }
        });

        mTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //
            }
        });

        mAddImage = rootView.findViewById(R.id.add_image_button);
        mDeleteImage = rootView.findViewById(R.id.delete_image_button);

        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!checkPermission()) {

                    requestPermission();

                } else {

                    showChooseImageDialog();
                }
            }
        });

        mDeleteImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                newImageFileName = "";
                selectedImagePath = "";
                selectedImage = null;

                mImageView.setImageURI(null);
                mImageView.setImageDrawable(null);
                mImageView.invalidate();

                updateView();
            }
        });

        mAllowCommentsSwitch = rootView.findViewById(R.id.allow_comments_switch);
        mAllowMessagesSwitch = rootView.findViewById(R.id.allow_messages_switch);

        mTextColorPreview = rootView.findViewById(R.id.text_color_preview);
        mBgColorPreview = rootView.findViewById(R.id.bg_color_preview);

        mChoiceTextColorPanel = rootView.findViewById(R.id.text_color_panel);
        mChoiceBgColorPanel = rootView.findViewById(R.id.bg_color_panel);
        mChoiceBgBlurPanel = rootView.findViewById(R.id.bg_blur_panel);
        mChoiceBgAlphaPanel = rootView.findViewById(R.id.bg_alpha_panel);

        mChoiceTextColorPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showColorPickerDialog(0, text_red, text_green, text_blue);
            }
        });

        mChoiceBgColorPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showColorPickerDialog(1, bg_red, bg_green, bg_blue);
            }
        });

        updateView();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getActivity().getCurrentFocus();

            if (v instanceof EmojiEditText) {

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {

                    v.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }

    private void updateView() {

        //

        if (text.length() >= MIN_ITEM_TEXT_SIZE) {

            mPostButton.setEnabled(true);

            mPostButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mPostButton.setTextColor(Color.WHITE);

        } else {

            mPostButton.setEnabled(false);

            mPostButton.setBackgroundColor(getResources().getColor(R.color.grey_10));
            mPostButton.setTextColor(getResources().getColor(R.color.grey_90));
        }

        //

        mChoiceBgBlurPanel.setVisibility(View.GONE);

        if (selectedImagePath.length() != 0) {

            mChoiceBgAlphaPanel.setVisibility(View.VISIBLE);
            mDeleteImage.setVisibility(View.VISIBLE);
            mChoiceBgColorPanel.setVisibility(View.VISIBLE);

            mAlphaSeekBar.setProgress(imgAlpha);

            mAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    mAlphaPreview.setText(imgAlpha + "");
                    imgAlpha = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    updateView();
                }
            });

            mAlphaPreview.setText(imgAlpha + "");

            mImageView.setImageURI(FileProvider.getUriForFile(App.getInstance().getApplicationContext(), App.getInstance().getPackageName() + ".provider", new File(selectedImagePath)));

        } else {

            mChoiceBgAlphaPanel.setVisibility(View.GONE);
            mDeleteImage.setVisibility(View.GONE);
            mChoiceBgColorPanel.setVisibility(View.VISIBLE);

            //mImageView.setImageURI(null);
        }

        //

        if (text.trim().length() != 0) {

            mTextView.setText(text);
        }

        //

        textColor = String.format("#%02x%02x%02x", text_red, text_green, text_blue);

        mTextView.setTextColor(Color.parseColor(textColor));
        mTextColorPreview.setBackgroundColor(Color.parseColor(textColor));

        //

        bgColor = String.format("#%02x%02x%02x", bg_red, bg_green, bg_blue);
        mBgColorPreview.setBackgroundColor(Color.parseColor(bgColor));

        mImageView.setBackgroundColor(Color.parseColor(bgColor));

        //

        mAllowCommentsSwitch.setOnCheckedChangeListener(null);

        mAllowCommentsSwitch.setChecked(false);

        if (allowComments == 1) {

            mAllowCommentsSwitch.setChecked(true);
        }

        mAllowCommentsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    allowComments = 1;

                } else {

                    allowComments = 0;
                }
            }
        });

        //

        mAllowMessagesSwitch.setOnCheckedChangeListener(null);

        mAllowMessagesSwitch.setChecked(false);
        mAllowMessagesSwitch.setEnabled(true);

//        if (App.getInstance().getAccount().getAllowMessages() == 0) {
//
//            allowMessages = 0;
//
//            mAllowMessagesSwitch.setEnabled(false);
//        }

        if (allowMessages == 1) {

            mAllowMessagesSwitch.setChecked(true);
        }

        mAllowMessagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    allowMessages = 1;

                } else {

                    allowMessages = 0;
                }
            }
        });

        //

        if (selectedImagePath.length() != 0) {

            if (imgBlur != 0) {

                mImageView.getDrawable().setAlpha(imgAlpha);
                mImageView.invalidate();

//                Blurry.with(App.getInstance().getApplicationContext())
//                        .radius(imgBlur)
//                        .sampling(3)
//                        .color(Color.argb(30, bg_red, bg_green, bg_blue))
//                        .async()
//                        .capture(mImageView)
//                        .into(mImageView);
            }
        }
    }

    private void showChooseImageDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.action_gallery));
        arrayAdapter.add(getString(R.string.action_camera));

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case 0: {

                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/jpeg");

                        imgFromGalleryActivityResultLauncher.launch(intent);

                        break;
                    }

                    default: {

                        try {

                            newImageFileName = Helper.randomString(6) + ".jpg";

                            selectedImage = FileProvider.getUriForFile(App.getInstance().getApplicationContext(), App.getInstance().getPackageName() + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newImageFileName));

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedImage);
                            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            imgFromCameraActivityResultLauncher.launch(cameraIntent);

                        } catch (Exception e) {

                            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }
                }

            }
        });

        AlertDialog d = builderSingle.create();
        d.show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            default: {

                break;
            }
        }

        return false;
    }

    public void sendPost() {

        showpDialog();

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_NEW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("itemId")) {

                                    sendSuccess(response.getLong("itemId"));
                                }

                            } else {

                                if (response.has("error_code")) {

                                    hidepDialog();

                                    if (response.getInt("error_code") == ERROR_LIMIT_EXCEEDED) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle(getString(R.string.app_name));
                                        alertDialog.setMessage(getString(R.string.msg_limit_exceeded));

                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_link_number), new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();

                                                Intent i = new Intent(getActivity(), OtpVerificationActivity.class);
                                                startActivity(i);
                                            }
                                        });

                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close), new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        });

                                        alertDialog.show();
                                    }

                                } else {

                                    sendSuccess(response.getLong("itemId"));
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                            sendSuccess(0);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                sendSuccess(0);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("client_id", CLIENT_ID);

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("text", text);

                params.put("text_color", textColor);
                params.put("bg_color", bgColor);

                params.put("img_blur", Integer.toString(imgBlur));
                params.put("img_alpha", Integer.toString(imgAlpha));

                params.put("allow_comments", Integer.toString(allowComments));
                params.put("allow_messages", Integer.toString(allowMessages));

                params.put("img_url", imgUrl);

                params.put("area", App.getInstance().getSettings().getArea());
                params.put("country", App.getInstance().getSettings().getCountry());
                params.put("city", App.getInstance().getSettings().getCity());

                params.put("lat", Double.toString(App.getInstance().getSettings().getLat()));
                params.put("lng", Double.toString(App.getInstance().getSettings().getLng()));

                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(15), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonReq.setRetryPolicy(policy);

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void sendSuccess(long itemId) {

        loading = false;

        hidepDialog();

        if (itemId != 0) {

            Intent intent = new Intent(getActivity(), ViewItemActivity.class);
            intent.putExtra("itemId", itemId);
            startActivity(intent);

            // Interstitial ad

            if (App.getInstance().getAdmobAdSettings().getInterstitialAdAfterNewItem() != 0) {

                App.getInstance().getAdmobAdSettings().setCurrentInterstitialAdAfterNewItem(App.getInstance().getAdmobAdSettings().getCurrentInterstitialAdAfterNewItem() + 1);

                if (App.getInstance().getAdmobAdSettings().getCurrentInterstitialAdAfterNewItem() >= App.getInstance().getAdmobAdSettings().getInterstitialAdAfterNewItem()) {

                    App.getInstance().getAdmobAdSettings().setCurrentInterstitialAdAfterNewItem(0);

                    App.getInstance().showInterstitialAd(null);
                }

                App.getInstance().saveData();
            }
        }

        Intent i = new Intent();
        getActivity().setResult(RESULT_OK, i);

        //Toast.makeText(getActivity(), getText(R.string.msg_gallery_item_added), Toast.LENGTH_SHORT).show();

        getActivity().finish();
    }

    private void showTextEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Title");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_input_dialog, (ViewGroup) getView(), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        final TextView counter = (TextView) viewInflated.findViewById(R.id.counter);

        builder.setView(viewInflated);

        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_ITEM_TEXT_SIZE) });

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                counter.setText(getString(R.string.placeholder_new_item_text_counter, input.getText().toString().trim().length(), Constants.MAX_ITEM_TEXT_SIZE, Constants.MIN_ITEM_TEXT_SIZE));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input.setText(text);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                text = input.getText().toString();

                updateView();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showColorPickerDialog(int mode, int red, int green, int blue) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_color_picker);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final View view_result = (View) dialog.findViewById(R.id.view_result);
        final AppCompatSeekBar seekbar_red = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_red);
        final AppCompatSeekBar seekbar_green = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_green);
        final AppCompatSeekBar seekbar_blue = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_blue);

        final TextView tv_red = (TextView) dialog.findViewById(R.id.tv_red);
        final TextView tv_green = (TextView) dialog.findViewById(R.id.tv_green);
        final TextView tv_blue = (TextView) dialog.findViewById(R.id.tv_blue);

        tv_red.setText(red + "");
        tv_green.setText(green + "");
        tv_blue.setText(blue + "");

        seekbar_red.setProgress(red);
        seekbar_green.setProgress(green);
        seekbar_blue.setProgress(blue);

        view_result.setBackgroundColor(Color.rgb(red, green, blue));

        seekbar_red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv_red.setText(progress + "");
                view_result.setBackgroundColor(Color.rgb(seekbar_red.getProgress(), seekbar_green.getProgress(), seekbar_blue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv_green.setText(progress + "");
                view_result.setBackgroundColor(Color.rgb(seekbar_red.getProgress(), seekbar_green.getProgress(), seekbar_blue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv_blue.setText(progress + "");
                view_result.setBackgroundColor(Color.rgb(seekbar_red.getProgress(), seekbar_green.getProgress(), seekbar_blue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((Button) dialog.findViewById(R.id.bt_ok)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (mode == 0) {

                    text_red = seekbar_red.getProgress();
                    text_green = seekbar_green.getProgress();
                    text_blue = seekbar_blue.getProgress();

                } else {

                    bg_red = seekbar_red.getProgress();
                    bg_green = seekbar_green.getProgress();
                    bg_blue = seekbar_blue.getProgress();
                }

                updateView();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public Boolean uploadFile(String serverURL, File file) {

        showpDialog();

        final OkHttpClient client = new OkHttpClient();

        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        try {

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uploaded_file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .addFormDataPart("account_id", Long.toString(App.getInstance().getAccount().getId()))
                    .addFormDataPart("access_token", App.getInstance().getAccount().getAccessToken())
                    .build();

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(serverURL)
                    .addHeader("Accept", "application/json;")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                    loading = false;

                    hidepDialog();

                    Log.e("failure", request.toString() + "|" + e.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    Log.e("response", jsonData);

                    try {

                        JSONObject result = new JSONObject(jsonData);

                        if (!result.getBoolean("error")) {

                            imgUrl = result.getString("imgUrl");
                        }

                        Log.d("My App", response.toString());

                    } catch (Throwable t) {

                        Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e("response", jsonData);

                        sendPost();
                    }

                }
            });

            return true;

        } catch (Exception ex) {
            // Handle the error

            loading = false;

            hidepDialog();
        }

        return false;
    }

    private boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            return true;
        }

        return false;
    }

    private void requestPermission() {

        storagePermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }
}