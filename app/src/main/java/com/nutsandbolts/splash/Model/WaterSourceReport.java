package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Class to represent a Water Source Report
 */
public class WaterSourceReport implements Parcelable {
    private final Date dateTime;
    private long reportID;
    private final String reporterName;
    private final String reporterUID;
    private final double latitude;
    private final double longitude;

    private final WaterType waterType;
    private final WaterCondition waterCondition;

    public static final int MAX_LATITUDE = 90;
    public static final int MAX_LONGITUDE = 180;

    /**
     * Write's the water report to the database
     */
    public void writeToDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = firebaseDatabase.getReference();
        DatabaseReference posts = mRootRef.child("posts");
        DatabaseReference push = posts.push();
        String key = push.getKey();
        DatabaseReference waterSourceReportsChild = mRootRef.child("water-source-reports");
        final DatabaseReference mReportRef = waterSourceReportsChild.child(key);
        DatabaseReference dateTimeChild = mReportRef.child("date-time");
        dateTimeChild.setValue(dateTime.getTime());
        DatabaseReference latitudeChild = mReportRef.child("latitude");
        latitudeChild.setValue(this.latitude);
        DatabaseReference longitudeChild = mReportRef.child("longitude");
        longitudeChild.setValue(this.longitude);
        DatabaseReference reporterNameChild = mReportRef.child("reporter-name");
        reporterNameChild.setValue(reporterName);
        DatabaseReference reporterUidChild = mReportRef.child("reporter-uid");
        reporterUidChild.setValue(reporterUID);
        DatabaseReference waterConditionChild = mReportRef.child("water-condition");
        waterConditionChild.setValue(waterCondition);
        DatabaseReference waterTypeChild = mReportRef.child("water-type");
        waterTypeChild.setValue(waterType);
        DatabaseReference reportIdChild = mReportRef.child("report-id");
        reportIdChild.setValue(dateTime.getTime());

        DatabaseReference mCountRef = mRootRef.child("total-source-reports");
        mCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long nextReportID = 0;
                if (dataSnapshot.getValue() != null) {
                    nextReportID = (long) dataSnapshot.getValue();
                }
                nextReportID = nextReportID + 1;
                DatabaseReference dataSnapshotRef = dataSnapshot.getRef();
                dataSnapshotRef.setValue(nextReportID);
                DatabaseReference reportIdChild = mReportRef.child("report-id");
                reportIdChild.setValue(nextReportID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference registeredUsersChild = mRootRef.child("registered-users");
        DatabaseReference reporterUIDChild = registeredUsersChild.child(reporterUID);
        DatabaseReference mReporterRef = reporterUIDChild
                .child("display-name");
        mReporterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = (String) dataSnapshot.getValue();
                DatabaseReference reporterNameChild = mReportRef.child("reporter-name");
                reporterNameChild.setValue(displayName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Create a new WaterReport
     * This constructor is necessary to input all the data a WaterReport holds.
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
    public WaterSourceReport(Date dateTime, long reportID, String reporterName, String reporterUID,
                             double latitude, double longitude, WaterType waterType,
                             WaterCondition waterCondition) {
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
        DataSnapshot dateChild = dataSnapshot.child("date-time");
        Date date = new Date((long) dateChild.getValue());
        DataSnapshot reporterNameChild = dataSnapshot.child("reporter-name");
        String reporterName = (String) reporterNameChild.getValue();
        DataSnapshot reporterUIDChild = dataSnapshot.child("reporter-uid");
        String reporterUID = (String) reporterUIDChild.getValue();
        DataSnapshot reportIdChild = dataSnapshot.child("report-id");
        long reportID = (long) reportIdChild.getValue();
        DataSnapshot latitudeChild = dataSnapshot.child("latitude");
        double latitude = convertDouble(latitudeChild.getValue());
        DataSnapshot longitudeChild = dataSnapshot.child("longitude");
        double longitude = convertDouble(longitudeChild.getValue());
        DataSnapshot waterTypeChild = dataSnapshot.child("water-type");
        WaterType type = WaterType.valueOf((String) waterTypeChild.getValue());
        DataSnapshot waterConditionChild = dataSnapshot.child("water-condition");
        WaterCondition condition = WaterCondition.valueOf(
                (String) waterConditionChild.getValue());
        return new WaterSourceReport(date, reportID, reporterName, reporterUID,
                latitude, longitude, type, condition);
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
     * Return a marker corresponding to this report
     * @return MarkerOptions object corresponding to this report
     */
    public MarkerOptions getMarkerOptions() {
        double latitude = getLatitude();
        double longitude = getLongitude();
        LatLng waterSourceReportPosition = new LatLng(latitude, longitude);

        //Create the marketOptions object and customize it
        MarkerOptions options = new MarkerOptions();
        options = options.position(waterSourceReportPosition);
        options = options.title(getWaterType()
                + ", " + getWaterCondition());
        return options;
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
     * gets the id of the report
     *
     * @return long  returns the id of the report
     */
    public long getReportID() {
        return reportID;
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
        @Override
        public WaterSourceReport createFromParcel(Parcel in) {
            return new WaterSourceReport(in);
        }

        @Override
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
            throw new IllegalArgumentException(
                    "Object passed in must be either a double or a long");
        }

        return result;
    }
}