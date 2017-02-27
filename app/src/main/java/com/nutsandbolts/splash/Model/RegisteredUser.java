package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Deb Banerji on 12-Feb-17.
 * <p>
 * Represents a single user in the model
 */

public class RegisteredUser implements Parcelable {

    private String displayName;
    private String id;
    private String emailAddress;
    private String homeAddress;
    private UserType userType;

    /**
     * Make a new user
     *
     * @param displayName  The user's display name
     * @param id           The user's id
     * @param emailAddress The user's email address
     * @param homeAddress  The user's home address
     */
    public RegisteredUser(String displayName, String id, String emailAddress,
                          String homeAddress, UserType userType) {
        this.id = id;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.homeAddress = homeAddress;
        this.userType = userType;
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
        mUserRef.child("user-type").setValue(userType);
    }

    /* **********************
     * Getters and setters
     */

    // RegisteredUser's ids cannot be changed as they
    // are registered using Google

    /**
     * Get ID of user
     *
     * @return String This returns the ID of the registered user
     */
    public String getId() {
        return id;
    }

    /**
     * Get display name of user
     *
     * @return String This returns the user's display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets display name of user
     *
     * @param displayName The user's display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get email address of user
     *
     * @return String This returns the user's email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Set email address of user
     *
     * @param emailAddress The user's email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Get home address of user
     *
     * @return String This returns the user's home address
     */
    public String getHomeAddress() {
        return homeAddress;
    }

    /**
     * Sets home address of user
     *
     * @param homeAddress The user's home address
     */
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    /**
     * Gets usertype of user
     *
     * @return The user's usertype
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Sets usertype of user
     *
     * @param userType  The user's usertype
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
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
        userType = UserType.valueOf(in.readString());
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
        dest.writeString(userType.name());
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
