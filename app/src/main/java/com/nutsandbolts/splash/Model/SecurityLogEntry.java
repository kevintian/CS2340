package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
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
        DatabaseReference logsChild = mRootRef.child("security-log");
        final DatabaseReference mReportRef = logsChild.child(key);
        DatabaseReference dateTimeChild = mReportRef.child("time");
        dateTimeChild.setValue(timestamp.getTime());

        DatabaseReference userUIDChild = mReportRef.child("user-uid");
        userUIDChild.setValue(userUID);
        DatabaseReference logDetailsChild = mReportRef.child("details");
        logDetailsChild.setValue(details);
        DatabaseReference logTypeChild = mReportRef.child("type");
        logTypeChild.setValue(type);
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

    public SecurityLogEntry(DataSnapshot snapshot) {
        DataSnapshot dateChild = snapshot.child("time");
        this.timestamp = new Date((long) dateChild.getValue());

        DataSnapshot typeChild = snapshot.child("type");
        this.type = SecurityLogType.valueOf((String) typeChild.getValue());

        DataSnapshot userUIDChild = snapshot.child("user-uid");
        this.userUID = (String) userUIDChild.getValue();

        DataSnapshot detailsChild = snapshot.child("details");
        this.details = (String) detailsChild.getValue();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public SecurityLogType getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public String getUserUID() {
        return userUID;
    }

    /**
     * Formats the date to a readable String
     * @return timestamp formatted as a string.
     */
    public String getTimestampString() {
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.LONG);
        return formatter.format(timestamp);
    }

    /**
     * Formats the log type as a String
     * @return log type as a String
     */
    public String getTypeString() {
        return type.getType();
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