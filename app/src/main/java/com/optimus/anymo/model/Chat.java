package com.optimus.anymo.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.constants.Constants;

public class Chat extends Application implements Constants, Parcelable {

    private long fromUserId, toUserId, itemId;
    private int id, newMessagesCount, createAt;
    private String timeAgo, date, lastMessage, lastMessageAgo;

    private String color = "", colorName = "", iconName = "", iconUrl = "";

    public Chat() {

    }

    public Chat(JSONObject jsonData) {

        try {

            this.setId(jsonData.getInt("id"));
            this.setFromUserId(jsonData.getLong("fromUserId"));
            this.setToUserId(jsonData.getLong("toUserId"));
            this.setLastMessage(jsonData.getString("lastMessage"));
            this.setLastMessageAgo(jsonData.getString("lastMessageAgo"));
            this.setNewMessagesCount(jsonData.getInt("newMessagesCount"));
            this.setDate(jsonData.getString("date"));
            this.setCreateAt(jsonData.getInt("createAt"));
            this.setTimeAgo(jsonData.getString("timeAgo"));

            if (jsonData.has("itemId")) {

                this.setItemId(jsonData.getInt("itemId"));
            }

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

            Log.e("Chat", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Chat", jsonData.toString());
        }
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getId() {

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

    public void setToUserId(long toUserId) {

        this.toUserId = toUserId;
    }

    public long getToUserId() {

        return this.toUserId;
    }

    public void setLastMessage(String lastMessage) {

        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {

        if (this.lastMessage == null) {

            this.lastMessage = "";
        }

        return this.lastMessage;
    }

    public void setLastMessageAgo(String lastMessageAgo) {

        this.lastMessageAgo = lastMessageAgo;
    }

    public String getLastMessageAgo() {

        return this.lastMessageAgo;
    }

    public void setNewMessagesCount(int newMessagesCount) {

        this.newMessagesCount = newMessagesCount;
    }

    public int getNewMessagesCount() {

        return this.newMessagesCount;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {

        if (this.date == null) {

            this.date = "";
        }

        return this.date;
    }

    public void setTimeAgo(String timeAgo) {

        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {

        if (this.timeAgo == null) {

            this.timeAgo = "";
        }

        return this.timeAgo;
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
        dest.writeLong(this.fromUserId);
        dest.writeLong(this.toUserId);
        dest.writeInt(this.id);
        dest.writeInt(this.newMessagesCount);
        dest.writeInt(this.createAt);
        dest.writeString(this.timeAgo);
        dest.writeString(this.date);
        dest.writeString(this.lastMessage);
        dest.writeString(this.lastMessageAgo);
        dest.writeString(color);
        dest.writeString(colorName);
        dest.writeString(iconName);
        dest.writeString(iconUrl);
        dest.writeLong(this.itemId);
    }

    protected Chat(Parcel in) {
        this.fromUserId = in.readLong();
        this.toUserId = in.readLong();
        this.id = in.readInt();
        this.newMessagesCount = in.readInt();
        this.createAt = in.readInt();
        this.timeAgo = in.readString();
        this.date = in.readString();
        this.lastMessage = in.readString();
        this.lastMessageAgo = in.readString();
        this.color = in.readString();
        this.colorName = in.readString();
        this.iconName = in.readString();
        this.iconUrl = in.readString();
        this.itemId = in.readLong();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
