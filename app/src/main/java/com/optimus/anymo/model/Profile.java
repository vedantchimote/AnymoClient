package com.optimus.anymo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import com.optimus.anymo.constants.Constants;


public class Profile implements Constants, Parcelable {

    private long id = 0;

    private String accessToken = "";

    private int state = 0, sex, year, month, day, verified, emailVerified, otpVerified, itemsCount, likesCount, commentsCount, allowComments, allowMessages, lastAuthorize;

    private double distance = 0;

    private String phoneNumber = "", email = "", username = "", fullname = "", lowPhotoUrl, bigPhotoUrl, normalPhotoUrl, normalCoverUrl, lastAuthorizeDate, lastAuthorizeTimeAgo;

    private String google_oauth_id = "";

    private Double lat = 0.000000, lng = 0.000000;

    private Boolean online = false;

    public Profile() {


    }

    public Profile(JSONObject jsonData) {

        try {

            if (!jsonData.getBoolean("error")) {

                this.setId(jsonData.getLong("id"));
                this.setState(jsonData.getInt("state"));

                this.setSex(jsonData.getInt("sex"));
                this.setYear(jsonData.getInt("year"));
                this.setMonth(jsonData.getInt("month"));
                this.setDay(jsonData.getInt("day"));

                this.setEmail(jsonData.getString("email"));
                this.setPhoneNumber(jsonData.getString("otpPhone"));

                this.setVerified(jsonData.getInt("verified"));
                this.setOtpVerified(jsonData.getInt("otpVerified"));
                this.setEmailVerified(jsonData.getInt("emailVerified"));

                this.setLowPhotoUrl(jsonData.getString("lowPhotoUrl"));
                this.setNormalPhotoUrl(jsonData.getString("normalPhotoUrl"));
                this.setBigPhotoUrl(jsonData.getString("bigPhotoUrl"));

                this.setNormalCoverUrl(jsonData.getString("normalCoverUrl"));

                this.setItemsCount(jsonData.getInt("items_count"));
                this.setLikesCount(jsonData.getInt("likes_count"));
                this.setCommentsCount(jsonData.getInt("comments_count"));

                if (jsonData.has("allowComments")) {

                    this.setAllowComments(jsonData.getInt("allowComments"));
                }

                if (jsonData.has("allowMessages")) {

                    this.setAllowMessages(jsonData.getInt("allowMessages"));
                }

                if (jsonData.has("username")) {

                    this.setUsername(jsonData.getString("username"));
                }

                if (jsonData.has("fullname")) {

                    this.setFullname(jsonData.getString("fullname"));
                }

                if (jsonData.has("lat")) {

                    this.setLat(jsonData.getDouble("lat"));
                }

                if (jsonData.has("lng")) {

                    this.setLng(jsonData.getDouble("lng"));
                }

                if (jsonData.has("gl_id")) {

                    this.setGoogleOauthId(jsonData.getString("gl_id"));
                }

                this.setLastActive(jsonData.getInt("lastAuthorize"));
                this.setLastActiveDate(jsonData.getString("lastAuthorizeDate"));
                this.setLastActiveTimeAgo(jsonData.getString("lastAuthorizeTimeAgo"));

                if (jsonData.has("distance")) {

                    this.setDistance(jsonData.getDouble("distance"));
                }
            }

        } catch (Throwable t) {

            Log.e("Profile", "Could not parse malformed JSON: \"" + jsonData.toString() + "\"");

        } finally {

            Log.d("Profile", jsonData.toString());
        }
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public String getAccessToken() {

        if (accessToken == null) {

            accessToken = "";
        }

        return this.accessToken;
    }

    public void setDistance(double distance) {

        this.distance = distance;
    }

    public double getDistance() {

        return this.distance;
    }

    public void setGoogleOauthId(String google_oauth_id) {

        this.google_oauth_id = google_oauth_id;
    }

    public String getGoogleOauthId() {

        if (this.google_oauth_id == null) {

            this.google_oauth_id = "";
        }

        return this.google_oauth_id;
    }

    public void setId(long profile_id) {

        this.id = profile_id;
    }

    public long getId() {

        return this.id;
    }

    public void setState(int profileState) {

        this.state = profileState;
    }

    public int getState() {

        return this.state;
    }

    public void setSex(int sex) {

        this.sex = sex;
    }

    public int getSex() {

        return this.sex;
    }

    public void setYear(int year) {

        this.year = year;
    }

    public int getYear() {

        return this.year;
    }

    public void setMonth(int month) {

        this.month = month;
    }

    public int getMonth() {

        return this.month;
    }

    public void setDay(int day) {

        this.day = day;
    }

    public int getDay() {

        return this.day;
    }

    public void setVerified(int verified) {

        this.verified = verified;
    }

    public int getVerified() {

        return this.verified;
    }

    public Boolean isVerified() {

        if (this.verified > 0) {

            return true;
        }

        return false;
    }

    public void setOtpVerified(int otpVerified) {

        this.otpVerified = otpVerified;
    }

    public int getOtpVerified() {

        return this.otpVerified;
    }

    public Boolean isOtpVerified() {

        if (this.otpVerified > 0) {

            return true;
        }

        return false;
    }

    public void setEmailVerified(int emailVerified) {

        this.emailVerified = emailVerified;
    }

    public int getEmailVerified() {

        return this.emailVerified;
    }

    public Boolean isEmailVerified() {

        if (this.emailVerified > 0) {

            return true;
        }

        return false;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getEmail() {

        if (email == null) {

            email = "";
        }

        return this.email;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {

        if (phoneNumber == null) {

            phoneNumber = "";
        }

        return this.phoneNumber;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getUsername() {

        if (username == null) {

            username = "";
        }

        return this.username;
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }

    public String getFullname() {

        if (fullname == null) {

            fullname = "";
        }

        return this.fullname;
    }

    public void setLowPhotoUrl(String lowPhotoUrl) {

        if (API_DOMAIN.equals("http://10.0.2.2/")) {

            this.lowPhotoUrl = lowPhotoUrl.replace("http://localhost/","http://10.0.2.2/");

        } else {

            this.lowPhotoUrl = lowPhotoUrl;
        }
    }

    public String getLowPhotoUrl() {

        if (this.lowPhotoUrl == null) {

            this.lowPhotoUrl = "";
        }

        return this.lowPhotoUrl;
    }

    public void setBigPhotoUrl(String bigPhotoUrl) {

        this.bigPhotoUrl = bigPhotoUrl;
    }

    public String getBigPhotoUrl() {

        return this.bigPhotoUrl;
    }

    public void setNormalPhotoUrl(String normalPhotoUrl) {

        this.normalPhotoUrl = normalPhotoUrl;
    }

    public String getNormalPhotoUrl() {

        return this.normalPhotoUrl;
    }

    public void setNormalCoverUrl(String normalCoverUrl) {

        this.normalCoverUrl = normalCoverUrl;
    }

    public String getNormalCoverUrl() {

        return this.normalCoverUrl;
    }

    public void setCommentsCount(int commentsCount) {

        this.commentsCount = commentsCount;
    }

    public int getCommentsCount() {

        return this.commentsCount;
    }

    public void setItemsCount(int itemsCount) {

        this.itemsCount = itemsCount;
    }

    public int getItemsCount() {

        return this.itemsCount;
    }

    public void setLikesCount(int likesCount) {

        this.likesCount = likesCount;
    }

    public int getLikesCount() {

        return this.likesCount;
    }

    public void setAllowComments(int allowComments) {

        this.allowComments = allowComments;
    }

    public int getAllowComments() {

        return this.allowComments;
    }

    public void setAllowMessages(int allowMessages) {

        this.allowMessages = allowMessages;
    }

    public int getAllowMessages() {

        return this.allowMessages;
    }

    public void setLastActive(int lastAuthorize) {

        this.lastAuthorize = lastAuthorize;
    }

    public int getLastActive() {

        return this.lastAuthorize;
    }

    public void setLastActiveDate(String lastAuthorizeDate) {

        this.lastAuthorizeDate = lastAuthorizeDate;
    }

    public String getLastActiveDate() {

        return this.lastAuthorizeDate;
    }

    public void setLastActiveTimeAgo(String lastAuthorizeTimeAgo) {

        this.lastAuthorizeTimeAgo = lastAuthorizeTimeAgo;
    }

    public String getLastActiveTimeAgo() {

        return this.lastAuthorizeTimeAgo;
    }

    public void setOnline(Boolean online) {

        this.online = online;
    }

    public Boolean isOnline() {

        return this.online;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public Double getLat() {

        if (this.lat == null) {

            this.lat = 0.000000;
        }

        return this.lat;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public Double getLng() {

        if (this.lng == null) {

            this.lng = 0.000000;
        }

        return this.lng;
    }

    protected Profile(Parcel in) {

        id = in.readLong();
        accessToken = in.readString();
        state = in.readInt();
        sex = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        verified = in.readInt();
        emailVerified = in.readInt();
        otpVerified = in.readInt();
        itemsCount = in.readInt();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        allowComments = in.readInt();
        allowMessages = in.readInt();
        lastAuthorize = in.readInt();
        distance = in.readDouble();
        phoneNumber = in.readString();
        email = in.readString();
        username = in.readString();
        fullname = in.readString();
        lowPhotoUrl = in.readString();
        bigPhotoUrl = in.readString();
        normalPhotoUrl = in.readString();
        normalCoverUrl = in.readString();
        lastAuthorizeDate = in.readString();
        lastAuthorizeTimeAgo = in.readString();
        google_oauth_id = in.readString();

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

        byte tmpOnline = in.readByte();
        online = tmpOnline == 0 ? null : tmpOnline == 1;
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {

        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(accessToken);
        dest.writeInt(state);
        dest.writeInt(sex);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(verified);
        dest.writeInt(emailVerified);
        dest.writeInt(otpVerified);
        dest.writeInt(itemsCount);
        dest.writeInt(likesCount);
        dest.writeInt(commentsCount);
        dest.writeInt(allowComments);
        dest.writeInt(allowMessages);
        dest.writeInt(lastAuthorize);
        dest.writeDouble(distance);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(fullname);
        dest.writeString(lowPhotoUrl);
        dest.writeString(bigPhotoUrl);
        dest.writeString(normalPhotoUrl);
        dest.writeString(normalCoverUrl);
        dest.writeString(lastAuthorizeDate);
        dest.writeString(lastAuthorizeTimeAgo);
        dest.writeString(google_oauth_id);

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

        dest.writeByte((byte) (online == null ? 0 : online ? 1 : 2));
    }
}
