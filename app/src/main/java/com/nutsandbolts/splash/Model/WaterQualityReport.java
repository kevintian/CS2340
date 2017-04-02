package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
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
    private int contaminantPPM;
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
        mReportRef.child("reporter-uid").setValue(reporterUID);
        mReportRef.child("report-id").setValue(dateTime.getTime());

        mReportRef.child("virus-ppm").setValue(virusPPM);
        mReportRef.child("contaminant-ppm").setValue(contaminantPPM);
        mReportRef.child("water-quality").setValue(waterQuality);

        DatabaseReference mCountRef = mRootRef.child("total-quality-reports");
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
     * @return Water Quality Report built from snapshot
     */
    public static WaterQualityReport buildWaterQualityReportFromSnapShot(DataSnapshot dataSnapshot) {
        //Get current report information
        Date dateTime = new Date((long) dataSnapshot.child("date-time").getValue());
        String reporterName = (String) dataSnapshot.child("reporter-name").getValue();
        String reporterUID = (String) dataSnapshot.child("reporter-uid").getValue();
        long reportID = (long) dataSnapshot.child("report-id").getValue();
        double latitude = convertDouble(dataSnapshot.child("latitude").getValue());
        double longitude = convertDouble(dataSnapshot.child("longitude").getValue());
        int virusPPM = ((Long) dataSnapshot.child("virus-ppm").getValue()).intValue();
        int contaminantPPM = ((Long) dataSnapshot.child("contaminant-ppm").getValue()).intValue();
        WaterQuality waterQuality = WaterQuality.valueOf((String) dataSnapshot.child("water-quality").getValue());
        return new WaterQualityReport(dateTime, reportID, reporterName,
                reporterUID, latitude, longitude, virusPPM,
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
     * @param contaminantPPM parts per million of contaminants
     * @param waterQuality quality of water (Safe, treatable, unsafe)
     */
    public WaterQualityReport(Date dateTime, long reportID, String reporterName, String reporterUID, double latitude, double longitude, int virusPPM, int contaminantPPM, WaterQuality waterQuality) {
        this.dateTime = dateTime;
        this.reportID = reportID;
        this.reporterName = reporterName;
        this.reporterUID = reporterUID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.virusPPM = virusPPM;
        this.contaminantPPM = contaminantPPM;
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
        contaminantPPM = in.readInt();
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
        dest.writeInt(contaminantPPM);
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

    public boolean isWithinBounds(int year, double latitude, double longitude, double radius) {
        //Create a calendar
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int reportYear = cal.get(Calendar.YEAR);

        return (reportYear == year)
                && (distFrom(this.latitude, this.longitude, latitude, longitude) <= radius);
    }

    //Haversine formula to calculate distance - distance will be returned in miles
    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        final double EARTH_RADIUS = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + (Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * Sets the latitude for this Water Quality Report
     * @param latitude latitude as a double
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude for this Water Quality Report
     * @param longitude longitude as a double
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the virus parts per million for this Water Quality Report
     * @param virusPPM int in parts per million
     */
    public void setVirusPPM(int virusPPM) {
        this.virusPPM = virusPPM;
    }

    /**
     * Sets the contaminant parts per million for the Water Quality Report
     * @param contaminantPPM int in parts per million
     */
    public void setContaminantPPM(int contaminantPPM) {
        this.contaminantPPM = contaminantPPM;
    }

    /**
     * Sets the water quality for this Water Quality Report
     * @param waterQuality as enum (SAFE, TREATABLE, UNSAFE)
     */
    public void setWaterQuality(WaterQuality waterQuality) {
        this.waterQuality = waterQuality;
    }

    /**
     * Gets the Date and Time of the Water Quality Report
     *
     * @return Date. returns the date and time of the water quality report
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Gets the name of the User that wrote the Water Quality Report
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
     * Gets the quality of water
     *
     * @return WaterQuality the quality of water reported
     */
    public WaterQuality getWaterQuality() {
        return waterQuality;
    }

    /**
     * Gets the virus ppm of water
     *
     * @return int returns the virus ppm of the water reported
     */
    public int getVirusPPM() {
        return virusPPM;
    }

    /**
     * Gets the contaminant ppm of water
     *
     * @return int returns the contaminant ppm of the water reported
     */
    public int getContaminantPPM() {
        return contaminantPPM;
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
        return "WaterQualityReport{" +
                "dateTime=" + dateTime +
                ", reportID=" + reportID +
                ", reporterName='" + reporterName + '\'' +
                ", reporterUID='" + reporterUID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", virusPPM=" + virusPPM +
                ", contaminantPPM=" + contaminantPPM +
                ", waterQuality=" + waterQuality +
                '}';
    }
}
