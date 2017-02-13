package com.nutsandbolts.splash.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.Model.User;
import com.nutsandbolts.splash.R;

public class RegisterActivity extends AppCompatActivity {

    /*
    Widgets we will need to define listeners for
    */
    private TextView emailText;
    private EditText displayNameText;
    private EditText homeAddressText;
    private Button registerButton;

    /*
    User model
     */
    private User user;

    /*
    Display Name and Home Address variables
     */
    private String displayName;
    private String homeAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*
        Get widgets from view
         */
        emailText = (TextView) findViewById(R.id.email_text);
        displayNameText = (EditText) findViewById(R.id.display_name_edit_text);
        homeAddressText = (EditText) findViewById(R.id.home_address_edit_text);
        registerButton = (Button) findViewById(R.id.register_button);

        /*
        Get User Data
         */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        displayName = firebaseUser.getDisplayName();
        if (displayName == null) {
            displayName = "";
        }
        displayNameText.setText(displayName);

        emailText.setText(firebaseUser.getEmail());

        user = new User(displayName, firebaseUser.getUid(), firebaseUser.getEmail(), homeAddress);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = displayNameText.getText().toString();
                String homeAddress = homeAddressText.getText().toString();
                if (displayName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter Display Name"
                            , Toast.LENGTH_SHORT).show();
                } else if (homeAddress.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter Home Address"
                            , Toast.LENGTH_SHORT).show();
                } else if (user != null) {
                    user.setDisplayName(displayName);
                    user.setHomeAddress(homeAddress);
                    user.writeToDatabase();
                }
            }
        });
    }

}
