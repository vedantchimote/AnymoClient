package com.optimus.anymo.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.optimus.anymo.OtpVerificationActivity;
import com.optimus.anymo.PhotoViewActivity;
import com.optimus.anymo.SignupActivity;
import com.optimus.anymo.ViewItemActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optimus.anymo.LoginActivity;
import com.optimus.anymo.R;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Item;
import com.optimus.anymo.util.CustomRequest;
import com.optimus.anymo.util.TagClick;
import com.optimus.anymo.util.TagSelectingTextview;
import com.vanniktech.emoji.EmojiTextView;


public class AdvancedItemListAdapter extends RecyclerView.Adapter<AdvancedItemListAdapter.ViewHolder> implements Constants, TagClick {

    private long replyToUserId = 0;

    private int pageId = 0;

    private List<Item> items = new ArrayList<>();

    private Context context;

    TagSelectingTextview mTagSelectingTextview;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    private OnItemMenuButtonClickListener onItemMenuButtonClickListener;

    public interface OnItemMenuButtonClickListener {

        void onItemClick(View view, Item obj, int actionId, int position);
    }

    public void setOnMoreButtonClickListener(final OnItemMenuButtonClickListener onItemMenuButtonClickListener) {

        this.onItemMenuButtonClickListener = onItemMenuButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPinImg;
        public ImageView mItemImg;
        public ImageView mItemLikeImg, mItemCommentImg;
        public EmojiTextView mItemDescription;
        public MaterialRippleLayout mItemLikeButton, mItemCommentButton;
        public ImageButton mMoreButton;

        public CardView mAdCard;
        public NativeAdView mAdView;
        public ProgressBar mAdProgressBar;

        public LinearLayout mTimeContainer, mLocationContainer;
        public TextView mItemTime, mItemLocation;

        public TextView mItemLikesCountText, mItemCommentsCountText;



        public ViewHolder(View v, int itemType) {

            super(v);

            if (itemType == 0) {

                mPinImg = (ImageView) v.findViewById(R.id.pin_image_view);

                mItemImg = (ImageView) v.findViewById(R.id.image_view);

                mItemDescription = (EmojiTextView) v.findViewById(R.id.text_view);

                mItemLikeImg = (ImageView) v.findViewById(R.id.itemLikeImg);
                mItemCommentImg = (ImageView) v.findViewById(R.id.itemCommentImg);

                mItemLikeButton = (MaterialRippleLayout) v.findViewById(R.id.itemLikeButton);
                mItemCommentButton = (MaterialRippleLayout) v.findViewById(R.id.itemCommentButton);

                // Counters

                mItemLikesCountText = (TextView) v.findViewById(R.id.item_likes_count);
                mItemCommentsCountText = (TextView) v.findViewById(R.id.item_comments_count);

                //

                mItemTime = (TextView) v.findViewById(R.id.item_time);
                mItemLocation = (TextView) v.findViewById(R.id.item_location);

                mLocationContainer = (LinearLayout) v.findViewById(R.id.location_container);
                mTimeContainer = (LinearLayout) v.findViewById(R.id.time_container);

                //

                mMoreButton = (ImageButton) v.findViewById(R.id.more_image_button);

            } else if (itemType == 1) {

                mAdCard = (CardView) v.findViewById(R.id.adCard);
                mAdView = (NativeAdView) v.findViewById(R.id.ad_native_view);
                mAdProgressBar = (ProgressBar) v.findViewById(R.id.ad_progress_bar);
            }
        }

    }

    public AdvancedItemListAdapter(Context ctx, List<Item> items) {

        this.context = ctx;
        this.items = items;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        mTagSelectingTextview = new TagSelectingTextview();
    }

    public AdvancedItemListAdapter(Context ctx, List<Item> items, int pageId) {

        this.context = ctx;
        this.items = items;
        this.pageId = pageId;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        mTagSelectingTextview = new TagSelectingTextview();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);

            return new ViewHolder(v, viewType);

        } else if (viewType == 1) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);

            return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            onBindItem(holder, position);

        } else {

            holder.mAdProgressBar.setVisibility(View.VISIBLE);

            holder.mAdView.setVisibility(View.GONE);

            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.banner_native_ad_unit_id));

            // OnUnifiedNativeAdLoadedListener implementation.
            builder.forNativeAd(

                    (NativeAd.OnNativeAdLoadedListener) nativeAd -> {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.

                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.

                        holder.mAdView.setMediaView((MediaView) holder.mAdView.findViewById(R.id.ad_media));

                        // Set other ad assets.
                        holder.mAdView.setHeadlineView(holder.mAdView.findViewById(R.id.ad_headline));
                        holder.mAdView.setBodyView(holder.mAdView.findViewById(R.id.ad_body));
                        holder.mAdView.setCallToActionView(holder.mAdView.findViewById(R.id.ad_call_to_action));
                        holder.mAdView.setIconView(holder.mAdView.findViewById(R.id.ad_app_icon));
                        holder.mAdView.setPriceView(holder.mAdView.findViewById(R.id.ad_price));
                        holder.mAdView.setStarRatingView(holder.mAdView.findViewById(R.id.ad_stars));
                        holder.mAdView.setStoreView(holder.mAdView.findViewById(R.id.ad_store));
                        holder.mAdView.setAdvertiserView(holder.mAdView.findViewById(R.id.ad_advertiser));

                        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
                        ((TextView) holder.mAdView.getHeadlineView()).setText(nativeAd.getHeadline());
                        holder.mAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

                        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                        // check before trying to display them.
                        if (nativeAd.getBody() == null) {

                            holder.mAdView.getBodyView().setVisibility(View.INVISIBLE);

                        } else {

                            holder.mAdView.getBodyView().setVisibility(View.VISIBLE);
                            ((TextView) holder.mAdView.getBodyView()).setText(nativeAd.getBody());
                        }

                        if (nativeAd.getCallToAction() == null) {

                            holder.mAdView.getCallToActionView().setVisibility(View.INVISIBLE);

                        } else {

                            holder.mAdView.getCallToActionView().setVisibility(View.VISIBLE);
                            ((Button) holder.mAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
                        }

                        if (nativeAd.getIcon() == null) {

                            holder.mAdView.getIconView().setVisibility(View.GONE);

                        } else {

                            ((ImageView) holder.mAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                            holder.mAdView.getIconView().setVisibility(View.VISIBLE);
                        }

                        if (nativeAd.getPrice() == null) {

                            holder.mAdView.getPriceView().setVisibility(View.INVISIBLE);

                        } else {

                            holder.mAdView.getPriceView().setVisibility(View.VISIBLE);
                            ((TextView) holder.mAdView.getPriceView()).setText(nativeAd.getPrice());
                        }

                        if (nativeAd.getStore() == null) {

                            holder.mAdView.getStoreView().setVisibility(View.INVISIBLE);

                        } else {

                            holder.mAdView.getStoreView().setVisibility(View.VISIBLE);
                            ((TextView) holder.mAdView.getStoreView()).setText(nativeAd.getStore());
                        }

                        if (nativeAd.getStarRating() == null) {

                            holder.mAdView.getStarRatingView().setVisibility(View.INVISIBLE);

                        } else {

                            ((RatingBar) holder.mAdView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                            holder.mAdView.getStarRatingView().setVisibility(View.VISIBLE);
                        }

                        if (nativeAd.getAdvertiser() == null) {

                            holder.mAdView.getAdvertiserView().setVisibility(View.INVISIBLE);

                        } else {

                            ((TextView) holder.mAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                            holder.mAdView.getAdvertiserView().setVisibility(View.VISIBLE);
                        }

                        // This method tells the Google Mobile Ads SDK that you have finished populating your
                        // native ad view with this native ad.
                        holder.mAdView.setNativeAd(nativeAd);

                        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
                        // have a video asset.
                        VideoController vc = nativeAd.getMediaContent().getVideoController();

                        // Updates the UI to say whether or not this ad has a video asset.
                        if (vc.hasVideoContent()) {

                            Log.e("admob", "Video status: Ad contains a %.2f:1 video asset.");

                            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                            // VideoController will call methods on this object when events occur in the video
                            // lifecycle.

                            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                                @Override
                                public void onVideoEnd() {
                                    // Publishers should allow native ads to complete video playback before
                                    // refreshing or replacing them with another ad in the same UI location.

                                    Log.e("admob", "Video status: Video playback has ended.");
                                    super.onVideoEnd();
                                }
                            });

                        } else {

                            Log.e("admob", "Video status: Ad does not contain a video asset.");
                        }
                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {

                    String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                    Log.e("admob","Failed to load native ad with error " + error);

                    holder.mAdView.setVisibility(View.GONE);
                    holder.mAdProgressBar.setVisibility(View.GONE);

                    AdRequest adRequest = new AdRequest.Builder().build();

                }

                @Override
                public void onAdLoaded() {

                    Log.e("admob","Ad loaded");

                    holder.mAdView.setVisibility(View.VISIBLE);
                    holder.mAdProgressBar.setVisibility(View.GONE);
                }

            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

            holder.mAdCard.setVisibility(View.VISIBLE);
        }
    }

    public void onBindItem(ViewHolder holder, final int position) {

        final Item p = items.get(position);

        holder.mPinImg.setVisibility(View.GONE);

        if (p.isPinned()) {

            holder.mPinImg.setVisibility(View.VISIBLE);
        }

        holder.mItemDescription.setTextColor(Color.parseColor(p.getTextColor()));
        holder.mItemImg.setBackgroundColor(Color.parseColor(p.getBgColor()));
        holder.mItemImg.invalidate();

        if (p.getImgUrl().length() != 0){

            holder.mItemImg.setVisibility(View.VISIBLE);

            final ImageView imageView = holder.mItemImg;

            Picasso.with(context)
                    .load(p.getImgUrl())
                    .into(holder.mItemImg, new Callback() {

                        @Override
                        public void onSuccess() {

                            holder.mItemImg.getDrawable().setAlpha(p.getImgAlpha());
                            holder.mItemImg.invalidate();
                        }

                        @Override
                        public void onError() {


                        }
                    });

        } else {

            holder.mItemImg.setImageResource(0);
        }

        holder.mItemImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                previewImage(p);
            }
        });

        holder.mItemDescription.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                previewImage(p);
            }
        });

        if (p.getPost().length() != 0) {

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getPost().replaceAll("<br>", "\n"));

            holder.mItemDescription.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", p.getPost().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, context.getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                }
            });

        }

        holder.mLocationContainer.setVisibility(View.GONE);

        if (getLocation(p).length() != 0) {

            holder.mItemLocation.setText(getLocation(p));
            holder.mLocationContainer.setVisibility(View.VISIBLE);
        }

        holder.mItemTime.setVisibility(View.VISIBLE);
        holder.mItemTime.setText(p.getTimeAgo());

        //

        holder.mItemCommentsCountText.setVisibility(View.GONE);

        if (p.getCommentsCount() > 0) {

            holder.mItemCommentsCountText.setVisibility(View.VISIBLE);

            holder.mItemCommentsCountText.setText(Integer.toString(p.getCommentsCount()));
        }

        //

        holder.mItemLikesCountText.setVisibility(View.GONE);

        if (p.getLikesCount() > 0) {

            holder.mItemLikesCountText.setVisibility(View.VISIBLE);

            holder.mItemLikesCountText.setText(Integer.toString(p.getLikesCount()));
        }

        if (p.isLike()) {

            holder.mItemLikeImg.setImageResource(R.drawable.ic_like_active);

        } else {

            holder.mItemLikeImg.setImageResource(R.drawable.ic_like);
        }

        final ImageView imgLike = holder.mItemLikeImg;

        holder.mItemLikeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (App.getInstance().getAccount().getId() == 0) {

                    showAuthorizeDlg();

                    return;
                }

                if (p.isLike()) {

                    p.setLike(false);
                    p.setLikesCount(p.getLikesCount() - 1);

                } else {

                    p.setLike(true);
                    p.setLikesCount(p.getLikesCount() + 1);

                    imgLike.setImageResource(R.drawable.ic_like_active);
                }

                animateIcon(imgLike);

                CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_LIKES_LIKE, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if (!response.getBoolean("error")) {

                                        p.setLikesCount(response.getInt("likesCount"));
                                        p.setLike(response.getBoolean("like"));

                                    } else {

                                        if (response.has("error_code")) {

                                            if (response.getInt("error_code") == ERROR_LIMIT_EXCEEDED) {

                                                if (p.isLike()) {

                                                    imgLike.setImageResource(R.drawable.ic_like_active);

                                                    p.setLike(false);

                                                    p.setLikesCount(p.getLikesCount() - 1);

                                                } else {

                                                    imgLike.setImageResource(R.drawable.ic_like);

                                                    p.setLike(true);

                                                    p.setLikesCount(p.getLikesCount() + 1);
                                                }

                                                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                                                alertDialog.setTitle(context.getString(R.string.app_name));
                                                alertDialog.setMessage(context.getString(R.string.msg_limit_exceeded));

                                                alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.action_link_number), new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dialog.dismiss();

                                                        Intent i = new Intent(context, OtpVerificationActivity.class);
                                                        context.startActivity(i);
                                                    }
                                                });

                                                alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, context.getString(R.string.action_close), new DialogInterface.OnClickListener() {

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

                                    notifyItemChanged(position);

                                    Log.e("Item.Like", response.toString());

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

                        Log.e("Item.Like", error.toString());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                        params.put("access_token", App.getInstance().getAccount().getAccessToken());

                        params.put("item_type", Integer.toString(ITEM_TYPE_POST));
                        params.put("item_id", Long.toString(p.getId()));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(jsonReq);
            }
        });

        holder.mItemCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (App.getInstance().getAccount().getId() == 0) {

                    showAuthorizeDlg();

                } else {

                    Intent i = new Intent(context, ViewItemActivity.class);
                    i.putExtra("itemId", p.getId());
                    context.startActivity(i);
                }

                // viewItem(p);
            }
        });

        holder.mMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                onItemMenuButtonClickListener.onItemClick(view, p,  ITEM_ACTIONS_MENU, position);
            }
        });
    }


    private void animateIcon(ImageView icon) {

        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
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

    public void swapItem(int fromPosition,int toPosition){

        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void previewImage(Item p) {

        if (p.getImgUrl() != null && p.getImgUrl().length() != 0) {

            Intent i = new Intent(context, PhotoViewActivity.class);
            i.putExtra("imgUrl", p.getImgUrl());
            context.startActivity(i);
        }
    }

    public void showAuthorizeDlg() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
        alertDialog.setTitle(context.getText(R.string.dlg_authorization_title));

        alertDialog.setMessage(context.getText(R.string.dlg_authorization_msg));
        alertDialog.setCancelable(true);

        alertDialog.setNegativeButton(context.getText(R.string.action_login), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(context, LoginActivity.class);
                context.startActivity(i);

                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(context.getText(R.string.action_signup), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(context, SignupActivity.class);
                context.startActivity(i);

                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton(context.getText(R.string.action_cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void clickedTag(CharSequence tag) {

        if (App.getInstance().getAccount().getId() != 0) {

            //
        }
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        final Item p = items.get(position);

        if (p.getAd() == 0) {

            return 0;

        } else {

            return 1;
        }
    }
}