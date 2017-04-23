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
 * Class to represent a WaterQualityReport
 */
public class WaterQualityReport implements Parcelable {
    private final Date dateTime;
    private final long reportID;
    private final String reporterName;
    private final String reporterUID;
    private final double latitude;
    private final double longitude;

    private final int virusPPM;
    private final int contaminantPPM;
    private final WaterQuality waterQuality;

    private static final int MAX_LATITUDE = 90;
    private static final int MAX_LONGITUDE = 180;

    /**
     * Writes this water report to the database.
     */
    public void writeToDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = firebaseDatabase.getReference();
        DatabaseReference postsChild = mRootRef.child("posts");
        DatabaseReference push = postsChild.push();
        String key = push.getKey();
        DatabaseReference waterQualityReportsChild = mRootRef.child("water-quality-reports");
        final DatabaseReference mReportRef = waterQualityReportsChild.child(key);
        DatabaseReference dateChild = mReportRef.child("date-time");
        dateChild.setValue(dateTime.getTime());
        DatabaseReference latitudeChild = mReportRef.child("latitude");
        latitudeChild.setValue(this.latitude);
        DatabaseReference longitudeChild = mReportRef.child("longitude");
        longitudeChild.setValue(this.longitude);
        DatabaseReference reporterNameChild = mReportRef.child("reporter-name");
        reporterNameChild.setValue(reporterName);
        DatabaseReference reporterUIDChild = mReportRef.child("reporter-uid");
        reporterUIDChild.setValue(reporterUID);
        final DatabaseReference reportIdChild = mReportRef.child("report-id");
        reportIdChild.setValue(dateTime.getTime());

        DatabaseReference virusPPMChild = mReportRef.child("virus-ppm");
        virusPPMChild.setValue(virusPPM);
        DatabaseReference contaminantPPMChild = mReportRef.child("contaminant-ppm");
        contaminantPPMChild.setValue(contaminantPPM);
        DatabaseReference waterQualityChild = mReportRef.child("water-quality");
        waterQualityChild.setValue(waterQuality);

        DatabaseReference mCountRef = mRootRef.child("total-quality-reports");
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
                reportIdChild.setValue(nextReportID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Create a new WaterReport from a data snapshot
     *
     * @param dataSnap Firebase data snapshot from which to create water report
     * @return Water Quality Report built from snapshot
     */
    public static WaterQualityReport buildWaterQualityReportFromSnapShot(DataSnapshot dataSnap) {
        //Get current report information
        DataSnapshot dateChild = dataSnap.child("date-time");
        Date dateTime = new Date((long) dateChild.getValue());
        DataSnapshot reporterNameChild = dataSnap.child("reporter-name");
        String reporterName = (String) reporterNameChild.getValue();
        DataSnapshot reporterUIDChild = dataSnap.child("reporter-uid");
        String reporterUID = (String) reporterUIDChild.getValue();
        DataSnapshot reportIdChild = dataSnap.child("report-id");
        long reportID = (long) reportIdChild.getValue();
        DataSnapshot latitudeChild = dataSnap.child("latitude");
        double latitude = convertDouble(latitudeChild.getValue());
        DataSnapshot longitudeChild = dataSnap.child("longitude");
        double longitude = convertDouble(longitudeChild.getValue());
        DataSnapshot virusPPMChild = dataSnap.child("virus-ppm");
        int virusPPM = ((Long) virusPPMChild.getValue()).intValue();
        DataSnapshot contaminantPPMChild = dataSnap.child("contaminant-ppm");
        int contaminantPPM = ((Long) contaminantPPMChild.getValue()).intValue();
        DataSnapshot waterQualityChild = dataSnap.child("water-quality");
        WaterQuality waterQuality = WaterQuality.valueOf((String) waterQualityChild.getValue());
        return new WaterQualityReport(dateTime, reportID, reporterName,
                reporterUID, latitude, longitude, virusPPM,
                contaminantPPM, waterQuality);
    }

    /**
     * Create a new WaterQualityReport
     *
     * This constructor must have 9 params in order to represent all the data in WaterQualityReport
     *
     * @param dateTime time the report was created
     * @param reportID report number
     * @param reporterName type of person who submits the report
     * @param reporterUID id of person who submits report
     * @param latitude location of water
     * @param longitude location of water
     * @param virusPPM parts per million of viruses
     * @param contaminantPPM parts per million of contaminants
     * @param waterQuality quality of water (Safe, treatable, unsafe)
     */
    public WaterQualityReport(Date dateTime, long reportID, String reporterName, String reporterUID,
                              double latitude, double longitude, int virusPPM, int contaminantPPM,
                              WaterQuality waterQuality) {
        // The water quality report has to be instantiated
        // in this way as this information is required
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
     * @param in parcel to create the Report from
     */
    private WaterQualityReport(Parcel in) {
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

    /**
     * @param year bound for year
     * @param latitude bound for latitude
     * @param longitude bound for longitude
     * @param radius bound for radius
     * @return true if report is within bounds
     */
    public boolean isWithinBounds(int year, double latitude, double longitude, double radius) {
        if ((radius <= 0)
                || (Math.abs(latitude) > MAX_LATITUDE)
                || (Math.abs(longitude) > MAX_LONGITUDE)) {
            return false;
        }
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
        double sinDeltaLat = Math.sin(dLat / 2);
        double sinDeltaLong = Math.sin(dLng / 2);
        double a = Math.pow(sinDeltaLat, 2) + (Math.pow(sinDeltaLong, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
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
     * Gets the type of the User that wrote the Water Quality Report
     *
     * @return String of reporterName returns the type of the user that submitted the report
     */
    public String getReporterName() {
        return reporterName;
    }

    /**
     * Gets the type of the latitude of the location
     *
     * @return double latitude of the location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the type of the longitude of the location
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
