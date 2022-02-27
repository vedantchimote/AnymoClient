package com.optimus.anymo.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.constants.Constants;

public class ChatItem extends Application implements Constants, Parcelable {

    private long fromUserId, stickerId;
    private int id, createAt, seenAt, listId = 0;
    private String message, imgUrl, timeAgo, date, stickerImgUrl;

    public ChatItem() {

    }

    public ChatItem(JSONObject jsonData) {

        try {

            this.setId(jsonData.getInt("id"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setMessage(jsonData.getString("message"));
            this.setImgUrl(jsonData.getString("imgUrl"));
            this.setCreateAt(jsonData.getInt("createAt"));
            this.setSeenAt(jsonData.getInt("seenAt"));
            this.setDate(jsonData.getString("date"));
            this.setTimeAgo(jsonData.getString("timeAgo"));

            if (jsonData.has("stickerId")) {

                this.setStickerId(jsonData.getInt("stickerId"));
                this.setStickerImgUrl(jsonData.getString("stickerImgUrl"));

            } else {

                this.setStickerId(0);
                this.setStickerImgUrl("");
            }

        } catch (Throwable t) {

            Log.e("ChatItem", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("ChatItem", jsonData.toString());
        }
    }

    public void setListId(int listId) {

        this.listId = listId;
    }

    public int getListId() {

        return this.listId;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getId() {

        return this.id;
    }

    public void setFromUserId(long fromUserId) {

        this.fromUserId = fromUserId;
    }

    public long getFromUserId() {

        return this.fromUserId;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getMessage() {

        if (this.message == null) {

            this.message = "";
        }

        return this.message;
    }

    public void setImgUrl(String imgUrl) {

        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {

        if (this.imgUrl == null) {

            this.imgUrl = "";
        }

        return this.imgUrl;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {

        return this.date;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        return this.timeAgo;
    }

    public void setCreateAt(int createAt) {

        this.createAt = createAt;
    }

    public int getCreateAt() {

        return this.createAt;
    }

    public void setSeenAt(int seenAt) {

        this.seenAt = seenAt;
    }

    public int getSeenAt() {

        return this.seenAt;
    }

    public void setStickerId(long stickerId) {

        this.stickerId = stickerId;
    }

    public long getStickerId() {

        return this.stickerId;
    }

    public void setStickerImgUrl(String stickerImgUrl) {

        this.stickerImgUrl = stickerImgUrl;
    }

    public String getStickerImgUrl() {

        if (this.stickerImgUrl == null) {

            this.stickerImgUrl = "";
        }

        return this.stickerImgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.fromUserId);
        dest.writeLong(this.stickerId);
        dest.writeInt(this.id);
        dest.writeInt(this.createAt);
        dest.writeInt(this.seenAt);
        dest.writeInt(this.listId);
        dest.writeString(this.message);
        dest.writeString(this.imgUrl);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.stickerImgUrl);
    }

    protected ChatItem(Parcel in) {
        this.fromUserId = in.readLong();
        this.stickerId = in.readLong();
        this.id = in.readInt();
        this.createAt = in.readInt();
        this.seenAt = in.readInt();
        this.listId = in.readInt();
        this.message = in.readString();
        this.imgUrl = in.readString();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.stickerImgUrl = in.readString();
    }

    public static final Creator<ChatItem> CREATOR = new Creator<ChatItem>() {
        @Override
        public ChatItem createFromParcel(Parcel source) {
            return new ChatItem(source);
        }

        @Override
        public ChatItem[] newArray(int size) {
            return new ChatItem[size];
        }
    };
}
