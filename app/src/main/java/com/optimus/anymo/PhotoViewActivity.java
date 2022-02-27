package com.optimus.anymo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import com.optimus.anymo.app.App;
import com.optimus.anymo.common.ActivityBase;
import com.optimus.anymo.util.Helper;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoViewActivity extends ActivityBase {

    private static final String TAG = "photo_view_activity";

    Toolbar toolbar;

    ImageView photoView;

    LinearLayout mContentScreen;
    RelativeLayout mLoadingScreen;

    PhotoViewAttacher mAttacher;
    ImageLoader imageLoader;

    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_view);

        Intent i = getIntent();

        imgUrl = i.getStringExtra("imgUrl");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);

        mContentScreen = (LinearLayout) findViewById(R.id.PhotoViewContentScreen);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.PhotoViewLoadingScreen);

        photoView = (ImageView) findViewById(R.id.photoImageView);
//
//        toolbar.getBackground().setAlpha(100);
        getSupportActionBar().setTitle("");

        showLoadingScreen();

        imageLoader = App.getInstance().getImageLoader();

        imageLoader.get(imgUrl, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean isImmediate) {

                photoView.setImageBitmap(imageContainer.getBitmap());
                mAttacher = new PhotoViewAttacher(photoView);

                showContentScreen();
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_photo_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

                return true;
            }

            case R.id.action_download: {

                Toast.makeText(this, getString(R.string.msg_download_started), Toast.LENGTH_SHORT).show();

                String filename = Helper.randomString(6) + ".jpg";
                String downloadUrlOfImage = imgUrl;

                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(downloadUrlOfImage);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(filename)
                        .setMimeType("image/jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename);

                dm.enqueue(request);

                return true;
            }


            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void showLoadingScreen() {

        mContentScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mLoadingScreen.setVisibility(View.GONE);
        mContentScreen.setVisibility(View.VISIBLE);
    }
}
