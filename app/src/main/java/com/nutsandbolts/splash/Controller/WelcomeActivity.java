package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nutsandbolts.splash.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /*
    Widgets we will need to define listeners for
    */
    private SignInButton googleButton;

    /*
    Google API Client
     */
    private GoogleApiClient mGoogleApiClient;

    /*
    Firebase Authentication
     */
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /*
    Firebase Database Reference
     */
    DatabaseReference mUserRef;

    /*
    Request Code
     */
    private static final int RC_SIGN_IN = 1337;

    /*
    Reference to self in order to allow for Toast messages within listeners
     */
    private WelcomeActivity self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /*
        Get widgets from view
         */
        googleButton = (SignInButton) findViewById(R.id.sign_in_with_google_button);

        // Configure Google Sign In
        String webAuth = getWebOAuth();
        Log.d("WEBAUTH", webAuth);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /*
        Get Firebase mAuth instance
         */
        mAuth = FirebaseAuth.getInstance();

        /*
        Get Firebase Database 'Users' reference
         */
        mUserRef = FirebaseDatabase.getInstance().getReference("users");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Authentication", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection", "Connection using Google API client failed");
    }

    /**
     * Signs the user in using Google
     */
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Authentication", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            // Signed out, show unauthenticated UI.
            Log.d("Authentication", result.getStatus().toString());
            Toast.makeText(this, "Connectivity problem - sign in with Google Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Authentication", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Authentication", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Authentication", "signInWithCredential", task.getException());
                            Toast.makeText(self, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            Toast.makeText()
                        } else {
                            authenticationDone();
                        }
                        // ...
                    }
                });
    }

    /**
     * Method that is run after user has logged in
     */
    private void authenticationDone() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("UID", uid);
        DatabaseReference mThisUserRef = mUserRef.child(uid);
        mThisUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                Intent loggedInIntent;
                if (value == null) { // User has not been registered
                    loggedInIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                } else { // User has already been registered
                    loggedInIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
                }
                startActivity(loggedInIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getWebOAuth() {
        String json = null;
        String oauth = null;
        try {
            InputStream is = getAssets().open("OAuth.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            oauth = jsonObject.getString("WebOauth");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oauth;
    }

}
