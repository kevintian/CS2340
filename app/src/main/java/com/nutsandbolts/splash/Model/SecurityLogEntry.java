package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Class to hold security log entry information.
 */
public class SecurityLogEntry implements Parcelable{
    private Date timestamp;
    private SecurityLogType type;
    private String details;
    private String userUID;

    /**
     * Instantiates a SecurityLogEntry
     * @param timestamp when the event occurred
     * @param type of event
     * @param details about the event
     * @param userUID of the user
     */
    public SecurityLogEntry(Date timestamp, SecurityLogType type, String userUID, String details) {
        this.timestamp = timestamp;
        this.type = type;
        this.details = details;
        this.userUID = userUID;
    }

    /**
     * Sets the details of this log entry.
     * @param details of this log entry.
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Write's the water report to the database
     */
    public void writeToDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = firebaseDatabase.getReference();
        DatabaseReference posts = mRootRef.child("posts");
        DatabaseReference push = posts.push();
        String key = push.getKey();
        DatabaseReference waterSourceReportsChild = mRootRef.child("security-log");
        final DatabaseReference mReportRef = waterSourceReportsChild.child(key);
        DatabaseReference dateTimeChild = mReportRef.child("time");
        dateTimeChild.setValue(timestamp.getTime());

        DatabaseReference reporterUidChild = mReportRef.child("user-uid");
        reporterUidChild.setValue(userUID);
        DatabaseReference waterConditionChild = mReportRef.child("details");
        waterConditionChild.setValue(details);
        DatabaseReference waterTypeChild = mReportRef.child("type");
        waterTypeChild.setValue(type);
    }

    /**
     * Loads water source information from a parcel
     *
     * @param in Parceled data
     */
    private SecurityLogEntry(Parcel in) {
        timestamp = new Date(in.readLong());
        details = in.readString();
        userUID = in.readString();
        type = SecurityLogType.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp.getTime());
        dest.writeString(details);
        dest.writeString(userUID);
        dest.writeString(type.getType());
    }

    public static final Creator<SecurityLogEntry> CREATOR = new Creator<SecurityLogEntry>() {
        @Override
        public SecurityLogEntry createFromParcel(Parcel in) {
            return new SecurityLogEntry(in);
        }

        @Override
        public SecurityLogEntry[] newArray(int size) {
            return new SecurityLogEntry[size];
        }
    };
}