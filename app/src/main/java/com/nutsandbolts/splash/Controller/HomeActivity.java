package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.R;

public class HomeActivity extends AppCompatActivity {


    /*
    Widgets we will need to define listeners for
    */
    private Button signOutButton;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signOutButton = (Button) findViewById(R.id.sign_out_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                returnToWelcomeScreen();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // RegisteredUser is signed in
                    Log.d("Authentication", "onAuthStateChanged:signed_in:"
                        + user.getUid());
                    returnToWelcomeScreen();
                } else {
                    // RegisteredUser is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    /**
     * Brings user back to welcome screen
     */
    private void returnToWelcomeScreen() {
        Intent welcomeIntent  = new Intent(HomeActivity
            .this, WelcomeActivity.class);
        startActivity(welcomeIntent);
    }
}
