package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by Deb Banerji on 22-Feb-17.
 */

public class WaterSourceReport implements Parcelable {
    private Date dateTime;
    private long reportID;
    private String reporterName;
    private String reporterUID;
    private double latitude;
    private double longitude;

    private WaterType waterType;
    private WaterCondition waterCondition;

    public static final int MAX_LATITUDE = 90;
    public static final int MAX_LONGITUDE = 180;

    /**
     * Write's the water report to the database
     */
    public void writeToDatabase() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String key = mRootRef.child("posts").push().getKey();
        final DatabaseReference mReportRef = mRootRef.child("water-source-reports").child(key);
        mReportRef.child("date-time").setValue(dateTime.getTime());
        mReportRef.child("latitude").setValue(latitude);
        mReportRef.child("longitude").setValue(longitude);
        mReportRef.child("reporter-name").setValue(reporterName);
        mReportRef.child("reporter-uid").setValue(reporterUID);
        mReportRef.child("water-condition").setValue(waterCondition);
        mReportRef.child("water-type").setValue(waterType);
        mReportRef.child("report-id").setValue(dateTime.getTime());

        DatabaseReference mCountRef = mRootRef.child("total-source-reports");
        mCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long nextReportID = 0;
                if (dataSnapshot.getValue() != null) {
                    nextReportID = (long) dataSnapshot.getValue();
                }
                nextReportID = nextReportID + 1;
                dataSnapshot.getRef().setValue(nextReportID);
                mReportRef.child("report-id").setValue(nextReportID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mReporterRef = mRootRef.child("registered-users").child(reporterUID).child("display-name");
        mReporterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = (String) dataSnapshot.getValue();
                mReportRef.child("reporter-name").setValue(displayName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Create a new WaterReport
     *
     * @param dateTime       time the report was created
     * @param reportID       id of the report
     * @param reporterName   the name of the user who created this report
     * @param reporterUID    the UID of the user who created this report
     * @param latitude       latitude of the location of the water
     * @param longitude      longitude of the location of the water
     * @param waterType      type of water being reported
     * @param waterCondition condition of water being reported
     */
    public WaterSourceReport(Date dateTime, long reportID, String reporterName, String reporterUID, double latitude, double longitude, WaterType waterType, WaterCondition waterCondition) {
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
     * Create a new WaterReport from a data snapshot
     *
     * @param dataSnapshot Firebase data snapshot from which to create water report
     * @return Water Source Report built from snapshot
     */
    public static WaterSourceReport buildWaterSourceReportFromSnapShot(DataSnapshot dataSnapshot) {
        //Get current report information
        Date date = new Date((long) dataSnapshot.child("date-time").getValue());
        String reporterName = (String) dataSnapshot.child("reporter-name").getValue();
        String reporterUID = (String) dataSnapshot.child("reporter-uid").getValue();
        long reportID = (long) dataSnapshot.child("report-id").getValue();
        double latitude = convertDouble(dataSnapshot.child("latitude").getValue());
        double longitude = convertDouble(dataSnapshot.child("longitude").getValue());
        WaterType type = WaterType.valueOf((String) dataSnapshot.child("water-type").getValue());
        WaterCondition condition = WaterCondition.valueOf((String) dataSnapshot.child("water-condition").getValue());
        return new WaterSourceReport(date, reportID, reporterName, reporterUID, latitude, longitude, type, condition);
    }

    /**
     * Getters and Setters
     */

    /**
     * Sets the Date and Time of the Water Source Report
     *
     * @param dateTime a Date Object that contains the current date and time
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the Date and Time of the Water Source Report
     *
     * @return Date. returns the date and time of the water source report
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Gets the name of the User that wrote the Water Source Report
     *
     * @return String of reporterName returns the name of the user that submitted the report
     */
    public String getReporterName() {
        return reporterName;
    }

    /**
     * returns the UID of the user that submitted the report
     *
     * @return String of reporterUID
     */
    public String getReporterUID() {
        return reporterUID;
    }

    /**
     * Gets the name of the latitude of the location
     *
     * @return double latitude of the location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the name of the longitude of the location
     *
     * @return double of longitude of the location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the type of water
     *
     * @return WaterType the type of water reported
     */
    public WaterType getWaterType() {
        return waterType;
    }

    /**
     * Gets the condition of water
     *
     * @return WaterCondition returns the condition of the water reported
     */
    public WaterCondition getWaterCondition() {
        return waterCondition;
    }

    /**
     * Sets the latitude of the location
     *
     * @param latitude Set a double as the latitude of the location
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude of the location
     *
     * @param longitude Set a double as the longitude of the location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the water type put in the report
     *
     * @param waterType Sets the waterType put in the report
     */
    public void setWaterType(WaterType waterType) {
        this.waterType = waterType;
    }

    /**
     * Sets the water condition put in the report
     *
     * @param waterCondition Sets the water condition put in the report
     */
    public void setWaterCondition(WaterCondition waterCondition) {
        this.waterCondition = waterCondition;
    }

    /**
     * gets the id of the report
     *
     * @return long  returns the id of the report
     */
    public long getReportID() {
        return reportID;
    }

    /**
     * sets the id of the report
     *
     * @param reportID the id of the report
     */
    public void setReportID(long reportID) {
        this.reportID = reportID;
    }

    /**
     * sets the name of the reporter of the report
     *
     * @param reporterName the name of the user that wrote the report
     */
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    /**
     * manually changes the report's recorded UID for reporter
     *
     * @param reporterName the UID of the user that wrote the report
     */
    public void setReporterUID(String reporterName) {
        this.reporterName = reporterName;
    }


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
        dest.writeString(reporterName);
        dest.writeString(reporterUID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(waterType.getType());
        dest.writeString(waterCondition.getCondition());
    }


    /**
     * Loads water source information from a parcel
     *
     * @param in Parceled data
     */
    private WaterSourceReport(Parcel in) {
        dateTime = new Date(in.readLong());
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

    /**
     * Checks if the value is a long -> if so, converts to double
     * This function is necessary due to the way Firebase stores data
     *
     * @param longValue - an object that is thought to be a long
     * @return double A double that is converted form the long param
     */
    public static double convertDouble(Object longValue) {
        double result; // return value

        if (longValue instanceof Long) {
            result = ((Long) longValue).doubleValue();
        } else if (longValue instanceof Double) {
            result = (double) longValue;
        } else {
            throw new IllegalArgumentException("Object passed in must be either a double or a long");
        }

        return result;
    }
}