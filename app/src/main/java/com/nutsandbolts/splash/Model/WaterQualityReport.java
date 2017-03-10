package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.nutsandbolts.splash.Model.WaterSourceReport.convertDouble;

/**
 * Created by Suprabhat Gurrala on 3/8/17.
 */

public class WaterQualityReport implements Parcelable {
    private Date dateTime;
    private long reportID;
    private String reporterName;
    private String reporterUID;
    private double latitude;
    private double longitude;

    private int virusPPM;
    private int contaminantPPm;
    private WaterQuality waterQuality;

    /**
     * Writes this water report to the database.
     */
    public void writeToDatabase() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String key = mRootRef.child("posts").push().getKey();
        final DatabaseReference mReportRef = mRootRef.child("water-quality-reports").child(key);
        mReportRef.child("date-time").setValue(dateTime.getTime());
        mReportRef.child("latitude").setValue(latitude);
        mReportRef.child("longitude").setValue(longitude);
        mReportRef.child("reporter-name").setValue(reporterName);
        mReportRef.child("virus-ppm").setValue(virusPPM);
        mReportRef.child("contaminant-ppm").setValue(contaminantPPm);
        mReportRef.child("water-quality").setValue(waterQuality);
        mReportRef.child("report-id").setValue(reportID);

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
    }

    /**
     * Create a new WaterReport from a data snapshot
     *
     * @param dataSnapshot Firebase data snapshot from which to create water report
     * @return Water Source Report built from snapshot
     */
    public static WaterQualityReport buildWaterQualityReportFromSnapShot(DataSnapshot dataSnapshot) {
        //Get current report information
        Date dateTime = new Date((long) dataSnapshot.child("date-time").getValue());
        String reporterName = (String) dataSnapshot.child("reporter-name").getValue();
        String reporterUID = (String) dataSnapshot.child("reporter-uid").getValue();
        long reportID = (long) dataSnapshot.child("report-id").getValue();
        double latitude = convertDouble(dataSnapshot.child("latitude").getValue());
        double longitude = convertDouble(dataSnapshot.child("longitude").getValue());
        int virusPPM = (int) dataSnapshot.child("virus-ppm").getValue();
        int contaminantPPM = (int) dataSnapshot.child("contaminant-ppm").getValue();
        WaterQuality waterQuality = WaterQuality.valueOf((String) dataSnapshot.child("water-quality").getValue());
        return new WaterQualityReport(dateTime, reportID, reporterName,
                String reporterUID, latitude, longitude, virusPPM,
                contaminantPPM, waterQuality);
    }

    /**
     * Create a new WaterQualityReport
     * @param dateTime time the report was created
     * @param reportID report number
     * @param reporterName name of person who submits the report
     * @param reporterUID id of person who submits report
     * @param latitude location of water
     * @param longitude location of water
     * @param virusPPM parts per million of viruses
     * @param contaminantPPm parts per million of contaminants
     * @param waterQuality quality of water (Safe, treatable, unsafe)
     */
    public WaterQualityReport(Date dateTime, long reportID, String reporterName, String reporterUID, double latitude, double longitude, int virusPPM, int contaminantPPm, WaterQuality waterQuality) {
        this.dateTime = dateTime;
        this.reportID = dateTime.getTime();
        this.reporterName = reporterName;
        this.reporterUID = reporterUID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.virusPPM = virusPPM;
        this.contaminantPPm = contaminantPPm;
        this.waterQuality = waterQuality;
    }

    /**
     * Creates a WaterQualityReport object from a Parcel
     * @param in
     */
    protected WaterQualityReport(Parcel in) {
        dateTime = new Date(in.readLong());
        reportID = in.readLong();
        reporterName = in.readString();
        reporterUID = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        virusPPM = in.readInt();
        contaminantPPm = in.readInt();
        waterQuality = WaterQuality.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateTime.getTime());
        dest.writeLong(reportID);
        dest.writeString(reporterName);
        dest.writeString(reporterUID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(virusPPM);
        dest.writeInt(contaminantPPm);
        dest.writeString(waterQuality.getQuality());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WaterQualityReport> CREATOR = new Creator<WaterQualityReport>() {
        @Override
        public WaterQualityReport createFromParcel(Parcel in) {
            return new WaterQualityReport(in);
        }

        @Override
        public WaterQualityReport[] newArray(int size) {
            return new WaterQualityReport[size];
        }
    };
}
