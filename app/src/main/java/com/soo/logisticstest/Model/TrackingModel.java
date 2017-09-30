package com.soo.logisticstest.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 이수연 on 2017-08-20.
 */

public class TrackingModel implements Parcelable{


    String ARRIVEDATE = "";
    String ARRIVETIME = "";
    String LOCATE = "";
    String STATUS = "";

    public TrackingModel(){

    }

    public TrackingModel(Parcel in){
        readFromParcel(in);
    }

    public String getARRIVEDATE() {
        return ARRIVEDATE;
    }

    public void setARRIVEDATE(String ARRIVEDATE) {
        this.ARRIVEDATE = ARRIVEDATE;
    }

    public String getARRIVETIME() {
        return ARRIVETIME;
    }

    public void setARRIVETIME(String ARRIVETIME) {
        this.ARRIVETIME = ARRIVETIME;
    }

    public String getLOCATE() {
        return LOCATE;
    }

    public void setLOCATE(String LOCATE) {
        this.LOCATE = LOCATE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ARRIVEDATE);
        dest.writeString(this.ARRIVETIME);
        dest.writeString(this.LOCATE);
        dest.writeString(this.STATUS);
    }

    private void readFromParcel(Parcel in){
        ARRIVEDATE = in.readString();
        ARRIVETIME = in.readString();
        LOCATE = in.readString();
        STATUS = in.readString();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TrackingModel createFromParcel(Parcel in) {
            return new TrackingModel(in);
        }

        public TrackingModel[] newArray(int size) {
            return new TrackingModel[size];
        }
    };
}
