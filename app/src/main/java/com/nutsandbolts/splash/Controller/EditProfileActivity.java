package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, UserType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);


        /*
        Get RegisteredUser Data
         */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                emailAddress = emailText.getText().toString();
                displayName = displayNameText.getText().toString();
                homeAddress = homeAddressText.getText().toString();
                userType = (UserType) userTypeSpinner.getSelectedItem();
                if (displayName.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Display Name", Toast.LENGTH_SHORT).show();
                } else if (homeAddress.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Home Address", Toast.LENGTH_SHORT).show();
                } else if (emailAddress.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (registeredUser != null) {
                    registeredUser.setEmailAddress(emailAddress);
                    registeredUser.setDisplayName(displayName);
                    registeredUser.setHomeAddress(homeAddress);
                    registeredUser.setUserType(userType);
                    registeredUser.writeToDatabase();
                    Intent homeIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            }
        });

        /*
        Code for altering the UI if the user is already registered
         */
        Intent intent = this.getIntent();
        if (intent != null) {
            Boolean isEdit = intent.getExtras().getBoolean("isEdit");
            if (isEdit) {
                // user is already registered; editing profile
                welcomeText.setText("Editing Profile");
                registerButton.setText("Update Profile");
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference mRegisteredUserRef = FirebaseDatabase.getInstance().getReference().child("registered-users");
                DatabaseReference mThisUserRef = mRegisteredUserRef.child(uid);
                mThisUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    //DataSnapshot contains data from the Firebase Database
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                        if (value != null) {
                            //Load current userdata into view
                            emailText.setText((CharSequence) dataSnapshot.child("email-address").getValue());
                            displayNameText.setText((CharSequence) dataSnapshot.child("display-name").getValue());
                            homeAddressText.setText((CharSequence) dataSnapshot.child("home-address").getValue());
                            //DataSnapshot returns a native type -> cast it to string, parse as enum, and get ordinal position
                            userTypeSpinner.setSelection(UserType.valueOf((String)dataSnapshot.child("user-type").getValue()).ordinal());

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
