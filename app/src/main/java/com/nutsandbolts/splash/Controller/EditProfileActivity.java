package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.nutsandbolts.splash.R;

public class EditProfileActivity extends AppCompatActivity {

    /*
    Widgets we will need to define listeners for
    */
    private EditText emailText;
    private EditText displayNameText;
    private EditText homeAddressText;
    private Button registerButton;
    private TextView welcomeText;

    /*
    RegisteredUser model
     */
    private RegisteredUser registeredUser;

    /*
    Display Name and Home Address variables
     */
    private String emailAddress;
    private String displayName;
    private String homeAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /*
        Get widgets from view
         */
        emailText = (EditText) findViewById(R.id.email_edit_text);
        displayNameText = (EditText) findViewById(R.id.display_name_edit_text);
        homeAddressText = (EditText) findViewById(R.id.home_address_edit_text);
        registerButton = (Button) findViewById(R.id.register_button);
        welcomeText = (TextView) findViewById(R.id.edit_welcome_textview);

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
                firebaseUser.getEmail(), homeAddress);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailText.getText().toString();
                String displayName = displayNameText.getText().toString();
                String homeAddress = homeAddressText.getText().toString();
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
                    registeredUser.writeToDatabase();
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
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object value = dataSnapshot.getValue();
                        if (value != null) {
                            emailText.setText((CharSequence) dataSnapshot.child("email-address").getValue());
                            displayNameText.setText((CharSequence) dataSnapshot.child("display-name").getValue());
                            homeAddressText.setText((CharSequence) dataSnapshot.child("home-address").getValue());
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
