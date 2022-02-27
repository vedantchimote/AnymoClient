package com.optimus.anymo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.optimus.anymo.adapter.CommentsListAdapter;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Comment;
import com.optimus.anymo.model.Item;
import com.optimus.anymo.util.Api;
import com.optimus.anymo.util.CustomRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewItemFragment extends Fragment implements Constants, SwipeRefreshLayout.OnRefreshListener {

    private ProgressDialog pDialog;

    private Toolbar mToolbar;

    private ProgressBar mProgressBar;
    private TextView mMessage;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View mBottomSheet;

    private SwipeRefreshLayout mContentContainer;
    private LinearLayout mCommentFormContainer;

    private EmojiEditText mCommentText;

    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedView;

    private LinearLayout mSendComment;

    public ImageView mPinImg;
    public ImageView mItemImg;
    public ImageView mItemLikeImg, mItemCommentImg;
    public EmojiTextView mItemDescription;
    public TextView mItemTime, mItemLocation;
    public LinearLayout mTimeContainer, mLocationContainer;
    public MaterialRippleLayout mItemLikeButton, mItemCommentButton;
    public ImageButton mMoreButton;

    public TextView mItemLikesCountText, mItemCommentsCountText;

    private CardView mFollowTooltip;
    private ImageButton mCloseFollowTooltip;

    ImageLoader imageLoader = App.getInstance().getImageLoader();


    private ArrayList<Comment> itemsList;
    private CommentsListAdapter itemsAdapter;

    public Item item = new Item();

    long itemId = 0, replyToUserId = 0;
    int arrayLength = 0;
    String commentText;

    private Boolean loading = false;
    private Boolean restore = false;
    private Boolean preload = false;

    private Boolean loadingComplete = false;

    public ViewItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(false);

        initpDialog();

        Intent i = getActivity().getIntent();

        itemId = i.getLongExtra("itemId", 0);

        itemsList = new ArrayList<Comment>();
        itemsAdapter = new CommentsListAdapter(getActivity(), itemsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_item, container, false);

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");
            loading = savedInstanceState.getBoolean("loading");
            preload = savedInstanceState.getBoolean("preload");

            replyToUserId = savedInstanceState.getLong("replyToUserId");

        } else {

            restore = false;
            loading = false;
            preload = false;

            replyToUserId = 0;
        }

        if (loading) {

            showpDialog();
        }

        mToolbar = rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        //

        mMessage = (TextView) rootView.findViewById(R.id.message);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        // Prepare bottom sheet

        mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        //

        mPinImg = (ImageView) rootView.findViewById(R.id.pin_image_view);
        mItemImg = (ImageView) rootView.findViewById(R.id.image_view);

        mItemDescription = (EmojiTextView) rootView.findViewById(R.id.text_view);

        mItemLikeImg = (ImageView) rootView.findViewById(R.id.itemLikeImg);
        mItemCommentImg = (ImageView) rootView.findViewById(R.id.itemCommentImg);

        mItemLikeButton = (MaterialRippleLayout) rootView.findViewById(R.id.itemLikeButton);
        mItemCommentButton = (MaterialRippleLayout) rootView.findViewById(R.id.itemCommentButton);

        // Counters

        mItemLikesCountText = (TextView) rootView.findViewById(R.id.item_likes_count);
        mItemCommentsCountText = (TextView) rootView.findViewById(R.id.item_comments_count);

        //

        mItemTime = (TextView) rootView.findViewById(R.id.item_time);
        mItemLocation = (TextView) rootView.findViewById(R.id.item_location);

        mLocationContainer = (LinearLayout) rootView.findViewById(R.id.location_container);
        mTimeContainer = (LinearLayout) rootView.findViewById(R.id.time_container);

        //

        mMoreButton = (ImageButton) rootView.findViewById(R.id.more_image_button);

        mMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showItemActionDialog();
            }
        });

        //

        mFollowTooltip = rootView.findViewById(R.id.follow_tooltip);
        mFollowTooltip.setVisibility(View.GONE);

        if (App.getInstance().getTooltipsSettings().isAllowShowFollowTooltip() && App.getInstance().getAccount().getId() != 0) {

            mFollowTooltip.setVisibility(View.VISIBLE);
        }

        mCloseFollowTooltip = rootView.findViewById(R.id.follow_close_tooltip_button);

        mCloseFollowTooltip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                App.getInstance().getTooltipsSettings().setShowFollowTooltip(false);
                App.getInstance().saveTooltipsSettings();

                mFollowTooltip.setVisibility(View.GONE);
            }
        });

        //

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mNestedView = rootView.findViewById(R.id.nested_view);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mLayoutManager);

        itemsAdapter.setOnMoreButtonClickListener(new CommentsListAdapter.OnItemMenuButtonClickListener() {

            @Override
            public void onItemClick(View v, final Comment obj, int actionId, final int position) {

                switch (actionId){

                    case R.id.action_remove: {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(getText(R.string.label_delete));

                        alertDialog.setMessage(getText(R.string.label_delete_comment));
                        alertDialog.setCancelable(true);

                        alertDialog.setNegativeButton(getText(R.string.action_no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                        alertDialog.setPositiveButton(getText(R.string.action_yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                itemsList.remove(position);
                                itemsAdapter.notifyDataSetChanged();

//                                Api api = new Api(getActivity());
//
//                                api.commentDelete(obj.getId(), ITEM_TYPE_GALLERY);

                                item.setCommentsCount(item.getCommentsCount() - 1);
                            }
                        });

                        alertDialog.show();

                        break;
                    }

                    case R.id.action_reply: {

//                        if (App.getInstance().getId() != 0) {
//
//                            replyToUserId = obj.getFromUserId();
//
//                            mCommentText.setText("@" + obj.getOwner().getUsername() + ", ");
//                            mCommentText.setSelection(mCommentText.getText().length());
//
//                            mCommentText.requestFocus();
//
//                        }

                        break;
                    }
                }
            }
        });

        mRecyclerView.setAdapter(itemsAdapter);

        mRecyclerView.setNestedScrollingEnabled(false);

        mItemLikeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (item.isLike()) {

                    mItemLikeImg.setImageResource(R.drawable.ic_like_active);

                    item.setLike(false);

                    item.setLikesCount(item.getLikesCount() - 1);

                } else {

                    mItemLikeImg.setImageResource(R.drawable.ic_like);

                    item.setLike(true);

                    item.setLikesCount(item.getLikesCount() + 1);
                }

                like();
            }
        });

        mContentContainer = rootView.findViewById(R.id.refresh_view);
        mContentContainer.setOnRefreshListener(this);

        mCommentFormContainer = rootView.findViewById(R.id.commentFormContainer);

        mCommentText = rootView.findViewById(R.id.commentText);
        mSendComment = rootView.findViewById(R.id.sendButton);

        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send();
            }
        });

        if (!restore) {

            showLoadingScreen();

            getItem();

        } else {

            if (!preload) {

                loadingComplete();

            } else {

                showLoadingScreen();
            }
        }

        // Inflate the layout for this fragment
        return rootView;
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

        outState.putBoolean("restore", true);
        outState.putBoolean("loading", loading);
        outState.putBoolean("preload", preload);

        outState.putLong("replyToUserId", replyToUserId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {

        if (App.getInstance().isConnected()) {

            mContentContainer.setRefreshing(true);
            getItem();

        } else {

            mContentContainer.setRefreshing(false);
        }
    }

    public void updateItem() {

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        mPinImg.setVisibility(View.GONE);

        if (item.isPinned()) {

            mPinImg.setVisibility(View.VISIBLE);
        }

        mItemDescription.setTextColor(Color.parseColor(item.getTextColor()));
        mItemImg.setBackgroundColor(Color.parseColor(item.getBgColor()));
        mItemImg.invalidate();

        if (item.getImgUrl().length() != 0){

            mItemImg.setVisibility(View.VISIBLE);

            final ImageView imageView = mItemImg;

            Picasso.with(getActivity())
                    .load(item.getImgUrl())
                    .into(mItemImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            mItemImg.getDrawable().setAlpha(item.getImgAlpha());
                            mItemImg.invalidate();
                        }

                        @Override
                        public void onError() {


                        }
                    });

        } else {

            mItemImg.setImageResource(0);
        }

        mItemImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                previewImage(item);
            }
        });

        mItemDescription.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                previewImage(item);
            }
        });

        if (item.getPost().length() != 0) {

            mItemDescription.setVisibility(View.VISIBLE);
            mItemDescription.setText(item.getPost().replaceAll("<br>", "\n"));

            mItemDescription.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", item.getPost().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getActivity(), getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                }
            });
        }

        mLocationContainer.setVisibility(View.GONE);

        if (getLocation(item).length() != 0) {

            mItemLocation.setText(getLocation(item));
            mLocationContainer.setVisibility(View.VISIBLE);
        }

        mItemTime.setVisibility(View.VISIBLE);
        mItemTime.setText(item.getTimeAgo());

        //

        mItemCommentsCountText.setVisibility(View.GONE);

        if (item.getCommentsCount() > 0) {

            mItemCommentsCountText.setVisibility(View.VISIBLE);

            mItemCommentsCountText.setText(Integer.toString(item.getCommentsCount()));
        }

        //

        if (item.isLike()) {

            mItemLikeImg.setImageResource(R.drawable.ic_like_active);

        } else {

            mItemLikeImg.setImageResource(R.drawable.ic_like);
        }

        mItemLikesCountText.setVisibility(View.GONE);

        if (item.getLikesCount() > 0) {

            mItemLikesCountText.setVisibility(View.VISIBLE);

            mItemLikesCountText.setText(Integer.toString(item.getLikesCount()));
        }
    }

    private String getLocation(Item item) {

        String location = "";

        if (item.getCountry().length() > 0) {

            location = item.getCountry();
        }

        if (item.getCity().length() > 0) {

            location = location + ", " + item.getCity();

        } else {

            if (item.getArea().length() != 0) {

                location = location + ", " + item.getArea();
            }
        }

        return location;
    }

    public void previewImage(Item p) {

        if (p.getImgUrl() != null && p.getImgUrl().length() != 0) {

            Intent i = new Intent(getActivity(), PhotoViewActivity.class);
            i.putExtra("imgUrl", p.getImgUrl());
            getActivity().startActivity(i);
        }
    }

    public void getItem() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEM_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                            return;
                        }

                        try {

                            arrayLength = 0;

                            if (!response.getBoolean("error")) {

                                itemsList.clear();

                                itemId = response.getInt("itemId");

                                if (response.has("items")) {

                                    JSONArray itemsArray = response.getJSONArray("items");

                                    arrayLength = itemsArray.length();

                                    if (arrayLength > 0) {

                                        for (int i = 0; i < itemsArray.length(); i++) {

                                            JSONObject itemObj = (JSONObject) itemsArray.get(i);

                                            item = new Item(itemObj);

                                            updateItem();
                                        }
                                    }
                                }

                                if (response.has("comments")) {

                                    JSONArray commentsArray2 = response.getJSONArray("comments");

                                    if (commentsArray2.length() > 0) {

                                        JSONObject commentsObject = commentsArray2.getJSONObject(0);

                                        if (commentsObject.has("comments")) {

                                            JSONArray commentsArray = commentsObject.getJSONArray("comments");

                                            arrayLength = commentsArray.length();

                                            if (arrayLength > 0) {

                                                for (int i = commentsArray.length() - 1; i > -1 ; i--) {

                                                    JSONObject itemObj = (JSONObject) commentsArray.get(i);

                                                    Comment comment = new Comment(itemObj);

                                                    itemsList.add(comment);
                                                }
                                            }
                                        }
                                    }
                                }

                                loadingComplete();

                            } else {

                                showErrorScreen();
                            }

                        } catch (JSONException e) {

                            showErrorScreen();

                            e.printStackTrace();

                        } finally {

                            Log.e("ViewImageFragment", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                    return;
                }

                showErrorScreen();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("item_id", Long.toString(itemId));
                params.put("language", "en");

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void send() {

        commentText = mCommentText.getText().toString();
        commentText = commentText.trim();

        if (App.getInstance().isConnected() && App.getInstance().getAccount().getId() != 0 && commentText.length() > 0) {

            loading = true;

            showpDialog();

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_COMMENTS_NEW, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (!isAdded() || getActivity() == null) {

                                Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                                return;
                            }

                            try {

                                if (!response.getBoolean("error")) {

                                    if (response.has("comment")) {

                                        JSONObject commentObj = response.getJSONObject("comment");

                                        Comment comment = new Comment(commentObj);

                                        itemsList.add(comment);

                                        itemsAdapter.notifyDataSetChanged();

                                        mCommentText.setText("");
                                        replyToUserId = 0;

                                        mNestedView.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                // Select the last row so it will scroll into view...
                                                mNestedView.fullScroll(View.FOCUS_DOWN);

                                                item.setCommentsCount(item.getCommentsCount() + 1);

                                                updateItem();
                                            }
                                        });
                                    }

                                    Toast.makeText(getActivity(), getString(R.string.msg_comment_has_been_added), Toast.LENGTH_SHORT).show();

                                } else {

                                    if (response.has("error_code")) {

                                        if (response.getInt("error_code") == ERROR_LIMIT_EXCEEDED) {

                                            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()).create();
                                            alertDialog.setTitle(getString(R.string.app_name));
                                            alertDialog.setMessage(getString(R.string.msg_limit_exceeded));

                                            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_link_number), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();

                                                    Intent i = new Intent(getActivity(), OtpVerificationActivity.class);
                                                    startActivity(i);
                                                }
                                            });

                                            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close), new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();
                                                }
                                            });

                                            alertDialog.show();

                                        }
                                    }
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            } finally {

                                loading = false;

                                hidepDialog();

                                InputMethodManager imm = (InputMethodManager) App.getInstance().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (!isAdded() || getActivity() == null) {

                        Log.e("ERROR", "ViewItemFragment Not Added to Activity");

                        return;
                    }

                    loading = false;

                    hidepDialog();

                    InputMethodManager imm = (InputMethodManager) App.getInstance().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                    params.put("access_token", App.getInstance().getAccount().getAccessToken());

                    params.put("item_id", Long.toString(item.getId()));
                    params.put("item_type", Integer.toString(ITEM_TYPE_POST));
                    params.put("comment_text", commentText);

                    params.put("area", App.getInstance().getSettings().getArea());
                    params.put("country", App.getInstance().getSettings().getCountry());
                    params.put("city", App.getInstance().getSettings().getCity());

                    params.put("replyToUserId", Long.toString(replyToUserId));

                    return params;
                }
            };

            int socketTimeout = 0;//0 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            jsonReq.setRetryPolicy(policy);

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    public void loadingComplete() {

        itemsAdapter.notifyDataSetChanged();

        if (item.getId() == 0) {

            showErrorScreen();

        } else {

            showContentScreen();
        }

        if (mContentContainer.isRefreshing()) {

            mContentContainer.setRefreshing(false);
        }
    }

    public void showContentScreen() {

        preload = false;

        mProgressBar.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);

        mContentContainer.setVisibility(View.VISIBLE);

        if (item.getAllowComments() == COMMENTS_DISABLED) {

            mCommentFormContainer.setVisibility(View.GONE);

        } else {

            mCommentFormContainer.setVisibility(View.VISIBLE);
        }

        loadingComplete = true;

        updateItem();

        getActivity().invalidateOptionsMenu();
    }

    public void showLoadingScreen() {

        mContentContainer.setVisibility(View.GONE);
        mCommentFormContainer.setVisibility(View.GONE);

        mMessage.setVisibility(mNestedView.VISIBLE);
        mMessage.setText(getText(R.string.msg_loading_2).toString());

        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mContentContainer.setVisibility(View.GONE);
        mCommentFormContainer.setVisibility(View.GONE);

        mMessage.setVisibility(mNestedView.VISIBLE);
        mMessage.setText(getText(R.string.error_data_loading).toString());

        mProgressBar.setVisibility(View.GONE);
    }

    // Item action

    private void showItemActionDialog() {

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

                follow(item);
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();

                report();
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

    // Prevent dialog dismiss when orientation changes
    private static void doKeepDialog(Dialog dialog){

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void report() {

        String[] profile_report_categories = new String[] {

                getText(R.string.label_profile_report_0).toString(),
                getText(R.string.label_profile_report_1).toString(),
                getText(R.string.label_profile_report_2).toString(),
                getText(R.string.label_profile_report_3).toString(),

        };

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
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

                androidx.appcompat.app.AlertDialog alert = (androidx.appcompat.app.AlertDialog) dialog;
                int reason = alert.getListView().getCheckedItemPosition();

                Api api = new Api(getActivity());

                api.newReport(item.getId(), REPORT_TYPE_ITEM, reason);

                Toast.makeText(getActivity(), getActivity().getString(R.string.label_post_reported), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    public void like() {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (!isAdded() || getActivity() == null) {

                            Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                            return;
                        }

                        try {

                            if (!response.getBoolean("error")) {

                                item.setLikesCount(response.getInt("likesCount"));
                                item.setLike(response.getBoolean("like"));

                            } else {

                                if (response.has("error_code")) {

                                    if (response.getInt("error_code") == ERROR_LIMIT_EXCEEDED) {

                                        if (item.isLike()) {

                                            mItemLikeImg.setImageResource(R.drawable.ic_like_active);

                                            item.setLike(false);

                                            item.setLikesCount(item.getLikesCount() - 1);

                                        } else {

                                            mItemLikeImg.setImageResource(R.drawable.ic_like);

                                            item.setLike(true);

                                            item.setLikesCount(item.getLikesCount() + 1);
                                        }

                                        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle(getString(R.string.app_name));
                                        alertDialog.setMessage(getString(R.string.msg_limit_exceeded));

                                        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_link_number), new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();

                                                Intent i = new Intent(getActivity(), OtpVerificationActivity.class);
                                                startActivity(i);
                                            }
                                        });

                                        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close), new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        });

                                        alertDialog.show();

                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            updateItem();

                            // Interstitial ad

                            if (App.getInstance().getAdmobAdSettings().getInterstitialAdAfterNewLike() != 0) {

                                App.getInstance().getAdmobAdSettings().setCurrentInterstitialAdAfterNewLike(App.getInstance().getAdmobAdSettings().getCurrentInterstitialAdAfterNewLike() + 1);

                                if (App.getInstance().getAdmobAdSettings().getCurrentInterstitialAdAfterNewLike() >= App.getInstance().getAdmobAdSettings().getInterstitialAdAfterNewLike()) {

                                    App.getInstance().getAdmobAdSettings().setCurrentInterstitialAdAfterNewLike(0);

                                    App.getInstance().showInterstitialAd(null);
                                }

                                App.getInstance().saveData();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!isAdded() || getActivity() == null) {

                    Log.e("ERROR", "ViewImageFragment Not Added to Activity");

                    return;
                }

                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("item_id", Long.toString(item.getId()));
                params.put("item_type", Integer.toString(ITEM_TYPE_GALLERY));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void follow(Item item) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("follow")) {

                                    item.setFollow(response.getBoolean("follow"));
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            if (item.getFollow()) {

                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_follow_true), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_follow_false), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("item_id", Long.toString(item.getId()));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }
}