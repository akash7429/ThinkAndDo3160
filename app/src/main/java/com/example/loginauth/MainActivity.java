package com.example.loginauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    public static final String TAG1 = "TAG";
    public static final int GOOGLE_SIGN_IN_CODE=10005;
    LoginButton fbloginButton;
    CallbackManager callbackManager;
    TextView tvlogin;
    EditText etFullname, etEmail, etPassword;
    Button btnRegister;
    FirebaseAuth fAuth;

    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    SignInButton GsignIn;
    GoogleSignInOptions gso;
    private CallbackManager mCallbackManager;
    GoogleSignInClient signInClient;
    private static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbloginButton = findViewById(R.id.fb_login_button);
        tvlogin = findViewById(R.id.tvLogin);
        callbackManager = CallbackManager.Factory.create();
        etEmail = findViewById(R.id.etEmail);
        etFullname = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        fStore = FirebaseFirestore.getInstance();
        GsignIn = findViewById(R.id.GoogleSignin);


        // Facebook Login
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("692846027185-fkpght7kmd0tkv41a4phmv2211gqns5p.apps.googleusercontent.com")
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,gso);

        FirebaseUser currentFirebaseUser = fAuth.getCurrentUser() ;
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // Check if the user had already logged in or not and email is verified or not.
        if(signInAccount!=null || fAuth.getCurrentUser()!=null && currentFirebaseUser!=null && currentFirebaseUser.isEmailVerified()){

            startActivity(new Intent(this,HomeActivity.class));
            finish();

            Toast.makeText(this, "User is logged in Already " + currentFirebaseUser.getEmail(), Toast.LENGTH_LONG).show();
        }

        // Takes User to another activity for login with google id.
        GsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show user the existing google accounts.
                Intent sign = signInClient.getSignInIntent();
                startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
            }
        });




        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String fullname = etFullname.getText().toString();


                if(TextUtils.isEmpty(email)){

                    etEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){

                    etEmail.setError("Password is Required.");
                    return;
                }

                if(password.length()<6){

                    etEmail.setError("Password Must be >= 6 Characters");
                    return;

                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            // send verification email

                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Verification email has been send", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG,"On Failure: Email not sent"+e.getMessage());
                                }
                            });

                            //Toast.makeText(MainActivity.this,"User Created.", Toast.LENGTH_SHORT).show();
                            
                            // Get the information of the current user.
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            user.put("password",password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: "+ e.toString());
                                }
                            });

                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));


                        }
                        else{
                            Toast.makeText(MainActivity.this,"Error !"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));

            }
        });

    }

    public void displayUserInfo(JSONObject object){

        String first_name, last_name, email, id;
        try {
            first_name = object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id = object.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Facebook Login
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Google Login
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GOOGLE_SIGN_IN_CODE){

            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);


                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(), null);

                fAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this,"Your Google Account is Connected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (fbloginButton.isSelected() && currentUser != null) {

            updateUI();
        }
    }

    public void updateUI(){

        Toast.makeText(MainActivity.this, "Facebook Logged in success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        //FirebaseUser user = fAuth.getCurrentUser();
        //userID = user.getEmail();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

}
