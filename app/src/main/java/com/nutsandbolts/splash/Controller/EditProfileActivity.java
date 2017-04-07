package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nutsandbolts.splash.Model.RegisteredUser;
import com.nutsandbolts.splash.Model.UserType;
import com.nutsandbolts.splash.R;

/**
 * This activity allows users to edit their profile information
 *
 * @author Jinni Xia
 */
public class EditProfileActivity extends AppCompatActivity {

    /*
    Widgets we will need to define listeners for
    */
    private TextView welcomeText;
    private TextView userTypeText;
    private EditText emailText;
    private EditText displayNameText;
    private EditText homeAddressText;
    private Spinner userTypeSpinner;
    private Button registerButton;

    /*
    RegisteredUser model
     */
    private RegisteredUser registeredUser;

    /*
    Display Name, Email and Home Address variables
     */
    private String emailAddress;
    private String displayName;
    private String homeAddress;
    private UserType userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /*
        Get widgets from view
         */
        userTypeText = (TextView) findViewById(R.id.user_type_label);
        emailText = (EditText) findViewById(R.id.email_edit_text);
        displayNameText = (EditText) findViewById(R.id.display_name_edit_text);
        homeAddressText = (EditText) findViewById(R.id.home_address_edit_text);
        registerButton = (Button) findViewById(R.id.register_button);
        welcomeText = (TextView) findViewById(R.id.edit_welcome_textview);
        userTypeSpinner = (Spinner) findViewById(R.id.user_type_spinner);

        /*
          Set up the adapter to display the allowable user types in the spinner
         */
        ArrayAdapter<UserType> adapter = new ArrayAdapter<>(this,android.R.layout
                .simple_spinner_item, UserType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);


        /*
        Get RegisteredUser Data
         */
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        displayName = firebaseUser.getDisplayName();
        if (displayName == null) {
            displayName = "";
        }
        displayNameText.setText(displayName);

        emailText.setText(firebaseUser.getEmail());


        registeredUser = new RegisteredUser(displayName, firebaseUser.getUid(),
                firebaseUser.getEmail(), homeAddress, UserType.CONTRIBUTOR);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable eText = emailText.getText();
                emailAddress = eText.toString();

                Editable nText = displayNameText.getText();
                displayName = nText.toString();

                Editable aText = homeAddressText.getText();
                homeAddress = aText.toString();
                userType = (UserType) userTypeSpinner.getSelectedItem();
                if (displayName.isEmpty()) {
                    Toast tText = Toast.makeText(getApplicationContext(),
                            "Enter Display Name", Toast.LENGTH_SHORT);
                    tText.show();
                } else if (homeAddress.isEmpty()) {
                    Toast tText = Toast.makeText(getApplicationContext(),
                            "Enter Home Address", Toast.LENGTH_SHORT);
                    tText.show();
                } else if (emailAddress.isEmpty()) {
                    Toast tText = Toast.makeText(getApplicationContext(),
                            "Enter Email Address", Toast.LENGTH_SHORT);
                    tText.show();
                } else if (registeredUser != null) {
//                    registeredUser.setEmailAddress(emailAddress);
//                    registeredUser.setDisplayName(displayName);
//                    registeredUser.setHomeAddress(homeAddress);
//                    registeredUser.setUserType(userType);
//                    registeredUser.writeToDatabase();
                    registeredUser.updateValues(emailAddress, displayName, homeAddress, userType);
                    Intent homeIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            }
        });
        updateUI(auth);


    }

    /**
     * alter the UI if the user is already registered
     * @param auth FirebaseAuth object according to which UI is altered
     */
    private void updateUI(FirebaseAuth auth) {

        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            Boolean isEdit = extras.getBoolean("isEdit");
            if (isEdit) {
                // user is already registered; editing profile
                welcomeText.setText(R.string.edit_profile_welcome_text);
                registerButton.setText(R.string.edit_profile_button_text);

                FirebaseUser currUser = auth.getCurrentUser();
                assert currUser != null;
                String uid = currUser.getUid();

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference();
                DatabaseReference mRegisteredUserRef = ref.child("registered-users");
                DatabaseReference mThisUserRef = mRegisteredUserRef.child(uid);
                mThisUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    //DataSnapshot contains data from the Firebase Database
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                        if (value != null) {
                            //Load current userdata into view
                            DataSnapshot email = dataSnapshot.child("email-address");
                            emailText.setText((CharSequence) email.getValue());

                            DataSnapshot name = dataSnapshot.child("display-name");
                            displayNameText.setText((CharSequence) name.getValue());

                            DataSnapshot address = dataSnapshot.child("home-address");
                            homeAddressText.setText((CharSequence) address.getValue());
                            //DataSnapshot returns a native type -> cast it to string, parse as
                            // enum, and get ordinal position

                            DataSnapshot type = dataSnapshot.child("user-type");
                            UserType typeValue = UserType.valueOf((String)type.getValue());
                            userTypeSpinner.setSelection(typeValue.ordinal());

                            // Hide widgets so users cannot edit their type
                            userTypeText.setVisibility(View.GONE);
                            userTypeSpinner.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            Log.d("ISEDIT", Boolean.toString(isEdit));
        }
    }

}
