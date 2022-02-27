package com.optimus.anymo.app;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;

import com.optimus.anymo.constants.Constants;

public class Tooltips extends Application implements Constants, Parcelable {

    private Boolean show_otp_tooltip = true;
    private Boolean show_loc_tooltip = true;
    private Boolean show_follow_tooltip = true;

    public Tooltips() {

    }

    public void setShowOtpTooltip(Boolean show_otp_tooltip) {

        this.show_otp_tooltip = show_otp_tooltip;
    }

    public Boolean isAllowShowOtpTooltip() {

        return this.show_otp_tooltip;
    }

    public void setShowLocTooltip(Boolean show_loc_tooltip) {

        this.show_loc_tooltip = show_loc_tooltip;
    }

    public Boolean isAllowShowLocTooltip() {

        return this.show_loc_tooltip;
    }

    public void setShowFollowTooltip(Boolean show_follow_tooltip) {

        this.show_follow_tooltip = show_follow_tooltip;
    }

    public Boolean isAllowShowFollowTooltip() {

        return this.show_follow_tooltip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeValue(this.show_otp_tooltip);
        dest.writeValue(this.show_loc_tooltip);
        dest.writeValue(this.show_follow_tooltip);
    }

    protected Tooltips(Parcel in) {

        this.show_otp_tooltip = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.show_loc_tooltip = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.show_follow_tooltip = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Tooltips> CREATOR = new Creator<Tooltips>() {
        @Override
        public Tooltips createFromParcel(Parcel source) {
            return new Tooltips(source);
        }

        @Override
        public Tooltips[] newArray(int size) {
            return new Tooltips[size];
        }
    };
}
