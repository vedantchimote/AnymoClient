package com.optimus.anymo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.constants.Constants;

public class Comment implements Constants, Parcelable {

    private long id, itemId, fromUserId, itemFromUserId, replyToUserId;
    private int createAt;
    private String comment, timeAgo;
    private Profile owner;
    private String area = "", country = "", city = "";

    private String color = "", colorName = "", iconName = "", iconUrl = "";

    public Comment() {

    }

    public Comment(JSONObject jsonData) {

        try {

            this.setId(jsonData.getLong("id"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setReplyToUserId(jsonData.getLong("replyToUserId"));
            this.setText(jsonData.getString("comment"));
            this.setTimeAgo(jsonData.getString("timeAgo"));
            this.setCreateAt(jsonData.getInt("createAt"));

            if (jsonData.has("itemId")) {

                this.setItemId(jsonData.getLong("itemId"));
            }

            if (jsonData.has("itemFromUserId")) {

                this.setItemFromUserId(jsonData.getLong("itemFromUserId"));
            }

            this.setArea(jsonData.getString("area"));
            this.setCountry(jsonData.getString("country"));
            this.setCity(jsonData.getString("city"));

            if (jsonData.has("color")) {

                this.setColor(jsonData.getString("color"));
            }

            if (jsonData.has("colorName")) {

                this.setColorName(jsonData.getString("colorName"));
            }

            if (jsonData.has("icon")) {

                this.setIconName(jsonData.getString("icon"));
            }

            if (jsonData.has("iconUrl")) {

                this.setIconUrl(jsonData.getString("iconUrl"));
            }

        } catch (Throwable t) {

            Log.e("Comment", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Comment", jsonData.toString());
        }
    }

    public void setId(long id) {

        this.id = id;
    }

    public long getId() {

        return this.id;
    }

    public void setItemId(long itemId) {

        this.itemId = itemId;
    }

    public long getItemId() {

        return this.itemId;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setItemFromUserId(long itemFromUserId) {

        this.itemFromUserId = itemFromUserId;
    }

    public long getItemFromUserId() {

        return this.itemFromUserId;
    }

    public void setReplyToUserId(long replyToUserId) {

        this.replyToUserId = replyToUserId;
    }

    public long getReplyToUserId() {

        return this.replyToUserId;
    }

    public void setText(String comment) {

        this.comment = comment;
    }

    public String getText() {

        if (this.comment == null) {

            this.comment = "";
        }

        return this.comment;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
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

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return this.createAt;
    }

    public String getColor() {

        if (this.color == null) {

            this.color = "";
        }

        return this.color;
    }

    public void setColor(String color) {

        this.color = color;
    }

    public String getColorName() {

        if (this.colorName == null) {

            this.colorName = "";
        }

        return this.colorName;
    }

    public void setColorName(String colorName) {

        this.colorName = colorName;
    }

    public String getIconName() {

        if (this.iconName == null) {

            this.iconName = "";
        }

        return this.iconName;
    }

    public void setIconName(String iconName) {

        this.iconName = iconName;
    }

    public String getIconUrl() {

        if (this.iconUrl == null) {

            this.iconUrl = "";
        }

        if (this.iconUrl.length() != 0) {

            return API_DOMAIN + this.iconUrl;

        } else {

            return this.iconUrl;
        }
    }

    public void setIconUrl(String iconUrl) {

        this.iconUrl = iconUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.itemId);
        dest.writeLong(this.fromUserId);
        dest.writeLong(this.replyToUserId);
        dest.writeInt(this.createAt);
        dest.writeString(this.comment);
        dest.writeString(this.timeAgo);
        dest.writeParcelable(this.owner, flags);
        dest.writeLong(this.itemFromUserId);
        dest.writeString(area);
        dest.writeString(country);
        dest.writeString(city);

        dest.writeString(color);
        dest.writeString(colorName);
        dest.writeString(iconName);
        dest.writeString(iconUrl);
    }

    protected Comment(Parcel in) {
        this.id = in.readLong();
        this.itemId = in.readLong();
        this.fromUserId = in.readLong();
        this.replyToUserId = in.readLong();
        this.createAt = in.readInt();
        this.comment = in.readString();
        this.timeAgo = in.readString();
        this.owner = (Profile) in.readParcelable(Profile.class.getClassLoader());
        this.itemFromUserId = in.readLong();
        this.area = in.readString();
        this.country = in.readString();
        this.city = in.readString();

        this.color = in.readString();
        this.colorName = in.readString();
        this.iconName = in.readString();
        this.iconUrl = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
