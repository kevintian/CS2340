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
     * Make a new user
     *
     * @param displayName  The user's display name
     * @param id The user's id
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
        DatabaseReference mRootRef = FirebaseDatabase.getInstance()
            .getReference();
        DatabaseReference mUserRef = mRootRef.child("registered-users")
            .child(id);
        mUserRef.child("display-name").setValue(displayName);
        mUserRef.child("email-address").setValue(emailAddress);
        mUserRef.child("home-address").setValue(homeAddress);
    }

    /* **********************
     * Getters and setters
     */

    // RegisteredUser's ids cannot be changed as they
    // are registered using Google
    
    /**
     * Get ID of user
     * @return String This returns the ID of the registered user
     */
    public String getId() {
        return id;
    }

    /**
     * Get display name of user
     * @return String This returns the user's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets display name of user
     * @param displayName The user's display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get email address of user
     * @return String This returns the user's email address
     */
    // RegisteredUser's emails cannot be changed 
    // as they are registered using Google
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Get home address of user
     * @return String This returns the user's home address
     */
    public String getHomeAddress() {
        return homeAddress;
    }

    /**
     * Sets home addres of user
     * @param homeAddress The user's home address
     */
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    /**
     * Gets Legal Types
     * @return List This returns a list of Strings
     * of the user's legal types
     */
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

    /**
     * Make a new user
     *
     * @param in 
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
