package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by Deb Banerji on 22-Feb-17.
 */

public class WaterSourceReport implements Parcelable {
    private Date dateTime;
    private int reportID;
    private String reporterName;
    private String reporterUID;
    private double latitude;
    private double longitude;

    private WaterType waterType;
    private WaterCondition waterCondition;

    /**
     * Write's the water report to the database
     */
    public void writeToDatabase() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String key = mRootRef.child("posts").push().getKey();
        DatabaseReference mReportRef = mRootRef.child("water-source-reports").child(key);
        mReportRef.child("date-time").setValue(dateTime.getTime());
        mReportRef.child("latitude").setValue(latitude);
        mReportRef.child("longitude").setValue(longitude);
        mReportRef.child("report-id").setValue(reportID);
        mReportRef.child("reporter-name").setValue(reporterName);
        mReportRef.child("water-condition").setValue(waterCondition);
        mReportRef.child("water-type").setValue(waterType);
    }

    /**
     * Create a new WaterReport
     * @param dateTime time the report was created
     * @param reportID a unique identifying number for the report
     * @param reporterName the name of the user who created this report
     * @param reporterUID the UID of the user who created this report
     * @param latitude latitude of the location of the water
     * @param longitude longitude of the location of the water
     * @param waterType type of water being reported
     * @param waterCondition condition of water being reported
     */
    public WaterSourceReport(Date dateTime, int reportID, String reporterName, String reporterUID, double latitude, double longitude, WaterType waterType, WaterCondition waterCondition) {
        this.dateTime = dateTime;
        this.reportID = reportID;
        this.reporterName = reporterName;
        this.reporterUID = reporterUID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waterType = waterType;
        this.waterCondition = waterCondition;
    }

    /**
     * Getters and Setters
     */
    public Date getDateTime() {
        return dateTime;
    }

    public int getReportID() {
        return reportID;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getReporterUID() {
        return reporterUID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public WaterType getWaterType() {
        return waterType;
    }

    public WaterCondition getWaterCondition() {
        return waterCondition;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setWaterType(WaterType waterType) {
        this.waterType = waterType;
    }

    public void setWaterCondition(WaterCondition waterCondition) {
        this.waterCondition = waterCondition;
    }

    /**
     * @return the display string representation
     */
    @Override
    public String toString() {
        return "Water Report submitted by " + reporterName + " on " + dateTime;
    }

    /**
     * Parcelable interface methods
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateTime.getTime());
        dest.writeInt(reportID);
        dest.writeString(reporterName);
        dest.writeString(reporterUID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(waterType.getType());
        dest.writeString(waterCondition.getCondition());
    }

    private WaterSourceReport(Parcel in) {
        dateTime = new Date(in.readLong());
        reportID = in.readInt();
        reporterName = in.readString();
        reporterUID = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        waterType = WaterType.valueOf(in.readString());
        waterCondition = WaterCondition.valueOf(in.readString());
    }

    public static final Parcelable.Creator<WaterSourceReport> CREATOR
            = new Parcelable.Creator<WaterSourceReport>() {
        public WaterSourceReport createFromParcel(Parcel in) {
            return new WaterSourceReport(in);
        }

        public WaterSourceReport[] newArray(int size) {
            return new WaterSourceReport[size];
        }
    };
}