package com.optimus.anymo.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.optimus.anymo.adapter.AdvancedItemListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.optimus.anymo.BuildConfig;
import com.optimus.anymo.R;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Item;

public class Api extends Application implements Constants {

    Context context;

    public Api (Context context) {

        this.context = context;
    }

    public void followItem(AdvancedItemListAdapter itemsAdapter, ArrayList<Item> itemsList, int position) {

        final Item item = itemsList.get(position);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ITEMS_FOLLOW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                if (response.has("follow")) {

                                    item.setFollow(response.getBoolean("follow"));

                                    itemsAdapter.notifyItemChanged(position);
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            if (item.getFollow()) {

                                Toast.makeText(context, context.getResources().getString(R.string.msg_follow_true), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(context, context.getResources().getString(R.string.msg_follow_false), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
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

    public void newReport(final long itemId, final int itemType, final int abuseId) {

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_REPORT_NEW, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {


                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        } finally {

                            Log.d("newReport", response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("newReport", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("account_id", Long.toString(App.getInstance().getAccount().getId()));
                params.put("access_token", App.getInstance().getAccount().getAccessToken());

                params.put("item_id", Long.toString(itemId));
                params.put("item_type", Integer.toString(itemType));
                params.put("abuse_id", Integer.toString(abuseId));

                return params;
            }
        };

        App.getInstance().addToRequestQueue(jsonReq);
    }

    public void postShare(Item item) {

        String shareText = "";

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        if (item.getPost().length() > 0) {

            shareText = item.getPost();

        } else {

            if (item.getImgUrl().length() == 0) {

                shareText = item.getLink();
            }
        }

        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, (String) context.getString(R.string.app_name));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);

        Log.e("Share","Share without Image");

        if ((item.getImgUrl().length() > 0) && (ContextCompat.checkSelfPermission(this.context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

            Log.e("Share","Share with Image");

            shareIntent.setType("image/*");

            final ImageView image;

            image = new ImageView(context);

            Picasso.with(context)
                    .load(item.getImgUrl())
                    .into(image, new Callback() {

                        @Override
                        public void onSuccess() {

                            Log.e("Share", "Image Load Success");
                        }

                        @Override
                        public void onError() {

                            Log.e("Share", "Image Load Error");

                            image.setImageResource(R.drawable.profile_default_photo);
                        }
                    });

            Drawable mDrawable = image.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

            String file_path = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER;

            File dir = new File(file_path);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "share.jpg");

            try {

                FileOutputStream fos = new FileOutputStream(file);

                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {

                Toast.makeText(context, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {

                e.printStackTrace();
            }

            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "share.jpg"));

            shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Share post"));
    }
}
