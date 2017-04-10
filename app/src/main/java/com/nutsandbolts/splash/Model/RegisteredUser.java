package com.nutsandbolts.splash.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Represents a single user in the model
 * @author Deb Banerji
 */

public class RegisteredUser implements Parcelable {

    private String displayName;
    private final String id;
    private String emailAddress;
    private String homeAddress;
    private UserType userType;

    /**
     * Make a new user
     *
     * @param displayName  The user's display name
     * @param id The user's id
     * @param emailAddress The user's email address
     * @param homeAddress  The user's home address
     * @param userType     The user's authorization level/userType
     */
    public RegisteredUser(String displayName, String id, String emailAddress,
                          String homeAddress, UserType userType) {
        this.id = id;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.homeAddress = homeAddress;
        /*We keep this for clarity's sake and for future code edits -> don't want to just initialize
        this as "CONTRIBUTOR" even though it is always contributor right now.*/
        this.userType = userType;
    }

    /**
     * Writes the user's data to the database
     */
    private void writeToDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = db.getReference();

        DatabaseReference userRef = mRootRef.child("registered-users");
        DatabaseReference mUserRef = userRef.child(id);

        DatabaseReference displayNameChild = mUserRef.child("display-name");
        displayNameChild.setValue(displayName);

        DatabaseReference emailChild = mUserRef.child("email-address");
        emailChild.setValue(emailAddress);

        DatabaseReference addressChild = mUserRef.child("home-address");
        addressChild.setValue(homeAddress);

        DatabaseReference typeChild = mUserRef.child("user-type");
        typeChild.setValue(userType);
    }

    // RegisteredUser's ids cannot be changed as they
    // are registered using Google
    /** Updates values and writes report to database
     * @param emailAddress email address
     * @param displayName display name
     * @param homeAddress home address
     * @param userType user type
     */
    public void updateValues(String emailAddress, String displayName,
                             String homeAddress, UserType userType) {
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        this.homeAddress = homeAddress;
        this.userType = userType;
        writeToDatabase();
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
     * @param in Where the info of the registered user is written into
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
                @Override
                public RegisteredUser createFromParcel(Parcel in) {
                    return new RegisteredUser(in);
                }

                @Override
                public RegisteredUser[] newArray(int size) {
                    return new RegisteredUser[size];
                }
            };
}
