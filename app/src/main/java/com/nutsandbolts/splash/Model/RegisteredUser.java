package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Deb Banerji on 12-Feb-17.
 * <p>
 * Represents a single user in the model
 */

public class RegisteredUser implements Parcelable {

    private static List<String> legalTypes = Arrays.asList(
            "UR", "WK", "MG", "AD");

    private String displayName;
    private String id;
    private String emailAddress;
    private String homeAddress;

    /**
     * Make a new student
     *
     * @param displayName  The user's display name
     * @param id           The user's id
     * @param emailAddress The user's email address
     * @param homeAddress  The user's home address
     */
    public RegisteredUser(String displayName, String id, String emailAddress,
                          String homeAddress) {
        this.id = id;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.homeAddress = homeAddress;
    }

    /**
     * Write's the user's data to the database
     */
    public void writeToDatabase() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserRef = mRootRef.child("registered-users").child(id);
        mUserRef.child("display-name").setValue(displayName);
        mUserRef.child("email-address").setValue(emailAddress);
        mUserRef.child("home-address").setValue(homeAddress);
    }

    /* **********************
     * Getters and setters
     */

    // RegisteredUser's ids cannot be changed as they are registered using Google
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    private List<String> getLegalTypes() {
        return new ArrayList<>(legalTypes);
    }

    /**
     * @return the display string representation
     */
    @Override
    public String toString() {
        return displayName;
    }

    /* *********************************
     * These methods are required by the parcelable interface
     *
     */

    private RegisteredUser(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        emailAddress = in.readString();
        homeAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(emailAddress);
        dest.writeString(homeAddress);
    }

    public static final Parcelable.Creator<RegisteredUser> CREATOR
            = new Parcelable.Creator<RegisteredUser>() {
        public RegisteredUser createFromParcel(Parcel in) {
            return new RegisteredUser(in);
        }

        public RegisteredUser[] newArray(int size) {
            return new RegisteredUser[size];
        }
    };
}
