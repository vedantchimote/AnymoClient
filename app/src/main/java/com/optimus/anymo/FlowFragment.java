package com.optimus.anymo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.optimus.anymo.adapter.AdvancedItemListAdapter;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Item;
import com.optimus.anymo.util.Api;
import com.optimus.anymo.util.CustomRequest;

public class FlowFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LIST = "State Adapter Data";

    private static final int PROFILE_NEW_POST = 4;

    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private LocationManager lm;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    private CardView mOtpTooltip, mLocTooltip;
    private ImageButton mOtpTooltipCloseButton;
    private Button mOtpTooltipActionButton, mLocTooltipActionButton;

    private RecyclerView mRecyclerView;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View mBottomSheet;

    private TextView mMessage;
    private ImageView mSplash;

    private SwipeRefreshLayout mItemsContainer;

    private ArrayList<Item> itemsList;
    private AdvancedItemListAdapter itemsAdapter;

    private int itemId = 0;
    private int arrayLength = 0;
    private Boolean loadingMore = false;
    private Boolean viewMore = false;
    private Boolean restore = false;

    int pastVisiblesItems = 0, visibleItemCount = 0, totalItemCount = 0;

    public FlowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            itemsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            restore = savedInstanceState.getBoolean("restore");
            itemId = savedInstanceState.getInt("itemId");

        } else {

            itemsList = new ArrayList<>();
            itemsAdapter = new AdvancedItemListAdapter(getActivity(), itemsList);

            restore = false;
            itemId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_flow, container, false);

        //

        locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> isGranted) -> {

            boolean granted = true;

            for (Map.Entry<String, Boolean> x : isGranted.entrySet())

                if (!x.getValue()) granted = false;

            if (granted) {

                Log.e("Permissions", "granted");

                updateLocation();

            } else {

                Log.e("Permissions", "denied");

                updateLocation();
            }
        });

        lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        //

        ((MainActivity)getActivity()).mFiltersButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getFiltersDialog();
            }
        });

        //

        mItemsContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.container_items);
        mItemsContainer.setOnRefreshListener(this);

        //

        mOtpTooltip = (CardView) rootView.findViewById(R.id.otp_tooltip);
        mOtpTooltipActionButton = (Button) rootView.findViewById(R.id.otp_action_button);
        mOtpTooltipCloseButton = (ImageButton) rootView.findViewById(R.id.otp_close_tooltip_button);

        mOtpTooltip.setVisibility(View.GONE);

        mOtpTooltipCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                App.getInstance().getTooltipsSettings().setShowOtpTooltip(false);
                App.getInstance().saveTooltipsSettings();

                mOtpTooltip.setVisibility(View.GONE);
            }
        });

        mOtpTooltipActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                App.getInstance().getTooltipsSettings().setShowOtpTooltip(false);

                Intent i = new Intent(getActivity(), OtpVerificationActivity.class);
                startActivity(i);
            }
        });

        mLocTooltip = (CardView) rootView.findViewById(R.id.loc_tooltip);
        mLocTooltipActionButton = (Button) rootView.findViewById(R.id.loc_action_button);

        mLocTooltip.setVisibility(View.GONE);

        mLocTooltipActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mLocTooltip.setVisibility(View.GONE);

                if (checkPermission()) {

                    requestPermission();
                }
            }
        });

        //

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mSplash = (ImageView) rootView.findViewById(R.id.splash);

        // Prepare bottom sheet

        mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        //

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        itemsAdapter.setOnMoreButtonClickListener(new AdvancedItemListAdapter.OnItemMenuButtonClickListener() {

            @Override
            public void onItemClick(View v, Item obj, int actionId, int position) {

                switch (actionId){

                    case ITEM_ACTIONS_MENU: {

                        showItemActionDialog(position);

                        break;
                    }
                }
            }
        });

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //check for scroll down

                if (dy > 0) {

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore) {

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && (viewMore) && !(mItemsContainer.isRefreshing())) {

                            loadingMore = true;

                            getItems();
                        }
                    }
                }
            }
        });

        if (itemsAdapter.getItemCount() == 0) {

            if (App.getInstance().getSettings().getLat() == 0.000000 || App.getInstance().getSettings().getLng() == 0.000000) {

                hideMessage();

            } else {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        if (!restore) {

            showMessage(getText(R.string.msg_loading_2).toString());

            getItems();
        }

        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();

        if (App.getInstance().getSettings().getLat() == 0.000000 || App.getInstance().getSettings().getLng() == 0.000000) {

            if (checkPermission()) {

                mLocTooltip.setVisibility(View.VISIBLE);
            }

        } else {

            mLocTooltip.setVisibility(View.GONE);

            if (App.getInstance().getSettings().getGeolocationUpdated()) {

                App.getInstance().getSettings().setGeolocationUpdated(false);

                loadingMore = false;

                itemId = 0;
                getItems();
            }

            //

            if (App.getInstance().getAccount().getId() != 0 && App.getInstance().getAccount().getOtpVerified() == 0 && App.getInstance().getSettings().getAllowOtpVerification() == ENABLED) {

                if (App.getInstance().getTooltipsSettings().isAllowShowOtpTooltip()) {

                    mOtpTooltip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void updateLocation() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {

                    // Got last known location. In some rare situations this can be null.

                    if (location != null) {

                        // Logic to handle location object

                        mLastLocation = location;

                        Log.d("GPS", "PeopleNearby onComplete" + mLastLocation.getLatitude());
                        Log.d("GPS", "PeopleNearby onComplete" + mLastLocation.getLongitude());

                        App.getInstance().getSettings().setLat(mLastLocation.getLatitude());
                        App.getInstance().getSettings().setLng(mLastLocation.getLongitude());

                        App.getInstance().getSettings().getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                        // Save data

                        App.getInstance().saveData();

                        // Send location data to server

                        App.getInstance().setLocation();

                        // hide tooltip

                        App.getInstance().getTooltipsSettings().setShowLocTooltip(false);
                        App.getInstance().saveTooltipsSettings();

                        // load items

                        if (itemsList.size() == 0) {

                            getItems();
                        }

                    } else {

                        Log.e("GPS", "getLastLocation:exception");

                        setDefaultLocation();
                    }
                }
            });

        } else {

            Log.e("GPS", "denied");

            setDefaultLocation();
        }
    }

    private void setDefaultLocation() {

        if (App.getInstance().getSettings().getLat() == 0.000000 || App.getInstance().getSettings().getLng() == 0.000000) {

            App.getInstance().getSettings().setLat(Constants.DEFAULT_LAT);
            App.getInstance().getSettings().setLng(Constants.DEFAULT_LNG);

            App.getInstance().getSettings().getAddress(App.getInstance().getSettings().getLat(), App.getInstance().getSettings().getLng());

            App.getInstance().saveData();

            // hide tooltip

            App.getInstance().getTooltipsSettings().setShowLocTooltip(false);
            App.getInstance().saveTooltipsSettings();

            //

            if (itemsList.size() == 0) {

                getItems();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getText(R.string.app_name));

            alertDialog.setMessage(getText(R.string.msg_location_detect_error));
            alertDialog.setCancelable(true);

            alertDialog.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putInt("itemId", itemId);
        outState.putParcelableArrayList(STATE_LIST, itemsList);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            itemId = 0;
            getItems();

        } else {

            mItemsContainer.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getItems() {

        mItemsContainer.setRefreshing(true);

        if (App.getInstance().getSettings().getLat() == 0.000000 || App.getInstance().getSettings().getLng() == 0.000000) {

            mItemsContainer.setRefreshing(false);

            hideMessage();

            return;
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_FLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "FlowFragment Not Added to Activity");

                            return;
                        }

                        if (!loadingMore) {

                            itemsList.clear();
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            Item item = new Item(itemObj);

                                            item.setAd(0);

                                            itemsList.add(item);

                                            // Ad

                                            if (App.getInstance().getAdmobAdSettings().getAdmobAdAfterItem() != 0) {

                                                if (i == (App.getInstance().getAdmobAdSettings().getAdmobAdAfterItem() - 1)) {

                                                    Item ad = new Item(itemObj);

                                                    ad.setAd(1);

                                                    itemsList.add(ad);
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            loadingComplete();

                            Log.d("Flow success", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "FlowFragment Not Added to Activity");

                    return;
                }

                loadingComplete();

                Log.e("Flow error", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("item_id", Integer.toString(itemId));

                params.put("image", Integer.toString(App.getInstance().getSettings().getFlowFiltersImages()));
                params.put("reports", Integer.toString(App.getInstance().getSettings().getFlowFiltersReports()));
                params.put("distance", Integer.toString(App.getInstance().getSettings().getFlowFiltersDistance()));

                params.put("lat", Double.toString(App.getInstance().getSettings().getLat()));
                params.put("lng", Double.toString(App.getInstance().getSettings().getLng()));

                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void loadingComplete() {

        if (arrayLength >= LIST_ITEMS) {

            viewMore = true;

        } else {

            viewMore = false;
        }

        itemsAdapter.notifyDataSetChanged();

        if (itemsAdapter.getItemCount() == 0) {

            if (FlowFragment.this.isVisible()) {

                showMessage(getText(R.string.label_empty_list).toString());
            }

        } else {

            hideMessage();
        }

        loadingMore = false;
        mItemsContainer.setRefreshing(false);
    }

    // Item action


    private void showItemActionDialog(final int position) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.item_action_sheet_list, null);

        MaterialRippleLayout mLoginButton = (MaterialRippleLayout) view.findViewById(R.id.login_button);
        MaterialRippleLayout mSignupButton = (MaterialRippleLayout) view.findViewById(R.id.signup_button);

        mLoginButton.setVisibility(View.GONE);
        mSignupButton.setVisibility(View.GONE);

        TextView mFollowButtonText = (TextView) view.findViewById(R.id.follow_button_text);

        MaterialRippleLayout mFollowButton = (MaterialRippleLayout) view.findViewById(R.id.follow_button);
        MaterialRippleLayout mChatButton = (MaterialRippleLayout) view.findViewById(R.id.chat_button);
        MaterialRippleLayout mDeleteButton = (MaterialRippleLayout) view.findViewById(R.id.delete_button);
        MaterialRippleLayout mShareButton = (MaterialRippleLayout) view.findViewById(R.id.share_button);
        MaterialRippleLayout mReportButton = (MaterialRippleLayout) view.findViewById(R.id.report_button);
        MaterialRippleLayout mOpenUrlButton = (MaterialRippleLayout) view.findViewById(R.id.open_url_button);
        MaterialRippleLayout mCopyUrlButton = (MaterialRippleLayout) view.findViewById(R.id.copy_url_button);

        if (!WEB_SITE_AVAILABLE) {

            mOpenUrlButton.setVisibility(View.GONE);
            mCopyUrlButton.setVisibility(View.GONE);
        }

        final Item item = itemsList.get(position);

        if (App.getInstance().getAccount().getId() == 0) {

            mLoginButton.setVisibility(View.VISIBLE);
            mSignupButton.setVisibility(View.VISIBLE);

            mFollowButton.setVisibility(View.GONE);
        }

        mChatButton.setVisibility(View.GONE);

        mDeleteButton.setVisibility(View.GONE);
        mOpenUrlButton.setVisibility(View.GONE);
        mCopyUrlButton.setVisibility(View.GONE);
        mShareButton.setVisibility(View.GONE);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(getActivity(), SignupActivity.class);
                startActivity(i);
            }
        });

        //

        if (App.getInstance().getAccount().getId() != 0 && item.getAllowMessages() != 0 && App.getInstance().getAccount().getId() != item.getFromUserId()) {

            mChatButton.setVisibility(View.VISIBLE);
        }

        mChatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("chatId", 0);
                i.putExtra("profileId", item.getFromUserId());
                i.putExtra("itemId", item.getId());
                i.putExtra("itemType", ITEM_TYPE_POST);
                startActivity(i);
            }
        });

        //

        if (item.getFollow()) {

            mFollowButtonText.setText(getText(R.string.action_unfollow));

        } else {

            mFollowButtonText.setText(getText(R.string.action_follow));
        }

        mFollowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Api api = new Api(getActivity());

                api.followItem(itemsAdapter, itemsList, position);
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                share(position);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                report(position);
            }
        });

        mCopyUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("post url", item.getLink());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getText(R.string.msg_post_link_copied), Toast.LENGTH_SHORT).show();
            }
        });

        mOpenUrlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.getLink()));
                startActivity(i);
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());

        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();

        doKeepDialog(mBottomSheetDialog);

        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mBottomSheetDialog = null;
            }
        });
    }

    public void getFiltersDialog() {

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getText(R.string.label_filters));

        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_flow_filters, null);

        b.setView(view);

        final CheckBox mImagesCheckBox = view.findViewById(R.id.imagesCheckBox);
        final CheckBox mReportsCheckBox = view.findViewById(R.id.reportsCheckBox);

        final TextView mDistanceLabel = view.findViewById(R.id.distance_label);

        final AppCompatSeekBar mDistanceSeekBar = view.findViewById(R.id.choice_distance);

        mDistanceSeekBar.setProgress(App.getInstance().getSettings().getFlowFiltersDistance());
        mDistanceLabel.setText(String.format(Locale.getDefault(), getString(R.string.label_filters_distance), App.getInstance().getSettings().getFlowFiltersDistance()));

        mDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mDistanceLabel.setText(String.format(Locale.getDefault(), getString(R.string.label_filters_distance), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (App.getInstance().getSettings().getFlowFiltersImages() == 1) {

            mImagesCheckBox.setChecked(true);

        } else {

            mImagesCheckBox.setChecked(false);
        }

        if (App.getInstance().getSettings().getFlowFiltersReports() == 1) {

            mReportsCheckBox.setChecked(true);

        } else {

            mReportsCheckBox.setChecked(false);
        }

        b.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // get distance

                App.getInstance().getSettings().setFlowFiltersDistance(mDistanceSeekBar.getProgress());

                if (mImagesCheckBox.isChecked()) {

                    App.getInstance().getSettings().setFlowFiltersImages(1);

                } else {

                    App.getInstance().getSettings().setFlowFiltersImages(0);
                }

                if (mReportsCheckBox.isChecked()) {

                    App.getInstance().getSettings().setFlowFiltersReports(1);

                } else {

                    App.getInstance().getSettings().setFlowFiltersReports(0);
                }

                App.getInstance().saveData();

                itemsList.clear();

                itemId = 0;

                getItems();
            }
        });

        b.setNegativeButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog d = b.create();

        d.setCanceledOnTouchOutside(false);
        d.setCancelable(false);
        d.show();
    }

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog){

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void share(final int position) {

        final Item item = itemsList.get(position);

        Api api = new Api(getActivity());
        api.postShare(item);
    }

    public void report(final int position) {

        String[] profile_report_categories = new String[] {

                getText(R.string.label_profile_report_0).toString(),
                getText(R.string.label_profile_report_1).toString(),
                getText(R.string.label_profile_report_2).toString(),
                getText(R.string.label_profile_report_3).toString(),

        };

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getText(R.string.label_post_report_title));

        alertDialog.setSingleChoiceItems(profile_report_categories, 0, null);
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                AlertDialog alert = (AlertDialog) dialog;
                int reason = alert.getListView().getCheckedItemPosition();

                final Item item = itemsList.get(position);

                Api api = new Api(getActivity());

                api.newReport(item.getId(), REPORT_TYPE_ITEM, reason);

                Toast.makeText(getActivity(), getActivity().getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    //

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        mSplash.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);

        mSplash.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return true;
        }

        return false;
    }

    private void requestPermission() {

        locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }
}