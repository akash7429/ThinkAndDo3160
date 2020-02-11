package com.example.loginauth;


        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Toast;

        import com.example.loginauth.rest.ApiClient;
        import com.example.loginauth.rest.services.UserInterface;
        import com.google.android.gms.auth.api.signin.GoogleSignIn;
        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
        import com.google.android.gms.auth.api.signin.GoogleSignInClient;
        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
        import com.google.android.gms.common.SignInButton;
        import com.google.android.gms.common.api.ApiException;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.GoogleAuthProvider;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.InstanceIdResult;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;


public class NewLogIn extends AppCompatActivity {

    private static final String TAG = "GOOGLEACTIVITY";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private SignInButton signInButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log_in);

        signInButton = findViewById(R.id.sign_in_button);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mFirebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(NewLogIn.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Signing you in... Please Wait");

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null){
            Log.d(TAG, "User is already logged in");
            startActivity(new Intent(NewLogIn.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnSuccessListener(NewLogIn.this, new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String userToken = instanceIdResult.getToken();
                                            String uid = user.getUid();
                                            String name = user.getDisplayName();
                                            String email = user.getEmail();
                                            String profileUrl = user.getPhotoUrl().toString();
                                            final String coverUrl = "";

                                            UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);

                                            Call<Integer> call = userInterface.signin(new NewLogIn.UserInfo(uid,name,email,profileUrl
                                                    ,coverUrl, userToken));
                                            call.enqueue(new Callback<Integer>() {
                                                @Override
                                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                    progressDialog.dismiss();
                                                    if(response.body() == 1) {
                                                        Toast.makeText(NewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(NewLogIn.this, HomeActivity.class));
                                                        finish();
                                                    }else{
                                                        Toast.makeText(NewLogIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                                        FirebaseAuth.getInstance().signOut();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Integer> call, Throwable t) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    progressDialog.dismiss();
                                                }
                                            });


                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }


    public class UserInfo{
        String uid, name, email, profileUrl, coverUrl, userToken;

        public UserInfo(String uid, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }


    }


































}
