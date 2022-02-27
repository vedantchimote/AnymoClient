package com.optimus.anymo.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.constants.Constants;


public class Item extends Application implements Constants, Parcelable {

    private long id = 0, fromUserId = 0;
    private int createAt, moderateAt, likesCount = 0, commentsCount = 0, reportsCount, allowComments = 1, allowMessages = 1;
    private String timeAgo = "", date = "", post = "", imgUrl = "", area = "", country = "", city = "";
    private Double lat = 0.000000, lng = 0.000000;
    private Boolean like = false, follow = false, pinned = false;

    private String previewVideoImgUrl = "", videoUrl = "";

    private String textColor = "", bgColor = "";
    private int imgBlur = 0, imgAlpha = 0;

    private int ad = 0;

    public Item() {

    }

    public Item(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setFromUserId(jsonData.getLong("fromUserId"));
                this.setPost(jsonData.getString("post"));
                this.setImgUrl(jsonData.getString("imgUrl"));
                this.setArea(jsonData.getString("area"));
                this.setCountry(jsonData.getString("country"));
                this.setCity(jsonData.getString("city"));
                this.setAllowComments(jsonData.getInt("allowComments"));
                this.setAllowMessages(jsonData.getInt("allowMessages"));
                this.setCommentsCount(jsonData.getInt("commentsCount"));
                this.setLikesCount(jsonData.getInt("likesCount"));
                this.setReportsCount(jsonData.getInt("reportsCount"));
                this.setLat(jsonData.getDouble("lat"));
                this.setLng(jsonData.getDouble("lng"));
                this.setCreateAt(jsonData.getInt("createAt"));
                this.setModerateAt(jsonData.getInt("moderateAt"));
                this.setDate(jsonData.getString("date"));
                this.setTimeAgo(jsonData.getString("timeAgo"));

                this.setTextColor(jsonData.getString("textColor"));
                this.setBgColor(jsonData.getString("bgColor"));
                this.setImgBlur(jsonData.getInt("imgBlur"));
                this.setImgAlpha(jsonData.getInt("imgAlpha"));

                this.setVideoUrl(jsonData.getString("videoUrl"));
                this.setPreviewVideoImgUrl(jsonData.getString("previewVideoImgUrl"));

                if (jsonData.has("like")) {

                    this.setLike(jsonData.getBoolean("like"));
                }

                if (jsonData.has("follow")) {

                    this.setFollow(jsonData.getBoolean("follow"));
                }

                if (jsonData.has("pinned")) {

                    this.setPinned(jsonData.getBoolean("pinned"));
                }
            }

        } catch (Throwable t) {

            Log.e("Item", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Item", jsonData.toString());
        }
    }

    public int getAd() {

        return this.ad;
    }

    public void setAd(int ad) {

        this.ad = ad;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromUserId() {

        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public int getAllowComments() {

        return allowComments;
    }

    public void setAllowComments(int allowComments) {

        this.allowComments = allowComments;
    }

    public int getAllowMessages() {

        return this.allowMessages;
    }

    public void setAllowMessages(int allowMessages) {

        this.allowMessages = allowMessages;
    }

    public int getCommentsCount() {

        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getReportsCount() {

        return this.reportsCount;
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return createAt;
    }

    public int getModerateAt() {

        return moderateAt;
    }

    public void setModerateAt(int moderateAt) {

        this.moderateAt = moderateAt;
    }

    public String getTimeAgo() {

        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getVideoUrl() {

        if (this.videoUrl == null) {

            this.videoUrl = "";
        }

        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {

        this.videoUrl = videoUrl;
    }

    public String getPreviewVideoImgUrl() {

        if (this.previewVideoImgUrl == null) {

            this.previewVideoImgUrl = "";
        }

        return this.previewVideoImgUrl;
    }

    public void setPreviewVideoImgUrl(String previewVideoImgUrl) {

        this.previewVideoImgUrl = previewVideoImgUrl;
    }

    public int getImgBlur() {

        return this.imgBlur;
    }

    public void setImgBlur(int imgBlur) {

        this.imgBlur = imgBlur;
    }

    public int getImgAlpha() {

        return this.imgAlpha;
    }

    public void setImgAlpha(int imgAlpha) {

        this.imgAlpha = imgAlpha;
    }

    public String getTextColor() {

        if (this.textColor == null) {

            this.textColor = "";
        }

        return this.textColor;
    }

    public void setTextColor(String textColor) {

        this.textColor = textColor;
    }

    public String getBgColor() {

        if (this.bgColor == null) {

            this.bgColor = "";
        }

        return this.bgColor;
    }

    public void setBgColor(String bgColor) {

        this.bgColor = bgColor;
    }

    public String getPost() {

        if (this.post == null) {

            this.post = "";
        }

        return this.post;
    }

    public void setPost(String post) {

        this.post = post;
    }

    public Boolean isPinned() {

        return this.pinned;
    }

    public void setPinned(Boolean pinned) {

        this.pinned = pinned;
    }

    public Boolean getPinned() {

        return this.pinned;
    }


    public Boolean isLike() {

        return like;
    }

    public void setLike(Boolean like) {

        this.like = like;
    }

    public Boolean getLike() {

        return this.like;
    }

    public Boolean isFollow() {

        return this.follow;
    }

    public void setFollow(Boolean follow) {

        this.follow = follow;
    }

    public Boolean getFollow() {

        return this.follow;
    }

    public String getImgUrl() {

        if (this.imgUrl == null) {

            this.imgUrl = "";
        }

        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArea() {

        if (this.area == null) {

            this.area = "";
        }

        return this.area;
    }

    public void setArea(String area) {

        this.area = area;
    }

    public String getCountry() {

        if (this.country == null) {

            this.country = "";
        }

        return this.country;
    }

    public void setCountry(String country) {

        this.country = country;
    }

    public String getCity() {

        if (this.city == null) {

            this.city = "";
        }

        return this.city;
    }

    public void setCity(String city) {

        this.city = city;
    }

    public Double getLat() {

        return this.lat;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public Double getLng() {

        return this.lng;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public String getLink() {

        return WEB_SITE + "/item/" + Long.toString(this.getId());
    }

    protected Item(Parcel in) {
        id = in.readLong();
        fromUserId = in.readLong();
        createAt = in.readInt();
        moderateAt = in.readInt();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        reportsCount = in.readInt();
        allowComments = in.readInt();
        allowMessages = in.readInt();
        timeAgo = in.readString();
        date = in.readString();
        post = in.readString();
        imgUrl = in.readString();
        area = in.readString();
        country = in.readString();
        city = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readDouble();
        }

        byte tmpMyLike = in.readByte();
        like = tmpMyLike == 0 ? null : tmpMyLike == 1;

        byte tmpFollow = in.readByte();
        follow = tmpFollow == 0 ? null : tmpFollow == 1;

        byte tmpPinned = in.readByte();
        pinned = tmpPinned == 0 ? null : tmpPinned == 1;

        previewVideoImgUrl = in.readString();
        videoUrl = in.readString();
        textColor = in.readString();
        bgColor = in.readString();
        imgBlur = in.readInt();
        imgAlpha = in.readInt();
        ad = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(fromUserId);
        dest.writeInt(createAt);
        dest.writeInt(moderateAt);
        dest.writeInt(likesCount);
        dest.writeInt(commentsCount);
        dest.writeInt(reportsCount);
        dest.writeInt(allowComments);
        dest.writeInt(allowMessages);
        dest.writeString(timeAgo);
        dest.writeString(date);
        dest.writeString(post);
        dest.writeString(imgUrl);
        dest.writeString(area);
        dest.writeString(country);
        dest.writeString(city);
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lng);
        }
        dest.writeByte((byte) (like == null ? 0 : like ? 1 : 2));
        dest.writeByte((byte) (follow == null ? 0 : follow ? 1 : 2));
        dest.writeByte((byte) (pinned == null ? 0 : pinned ? 1 : 2));
        dest.writeString(previewVideoImgUrl);
        dest.writeString(videoUrl);
        dest.writeString(textColor);
        dest.writeString(bgColor);
        dest.writeInt(imgBlur);
        dest.writeInt(imgAlpha);
        dest.writeInt(ad);
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
