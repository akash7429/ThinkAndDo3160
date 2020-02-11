package com.example.loginauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView tvCreateAccount,tvForgotPassword,verifyMsg;

    EditText etEmailL, etPasswordL;
    Button btnLoginL;
    FirebaseAuth fAuth;
    ProgressBar progressBar2;
    Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        etEmailL = findViewById(R.id.etEmailL);
        etPasswordL = findViewById(R.id.etPasswordL);
        btnLoginL = findViewById(R.id.btnLogin);
        fAuth = FirebaseAuth.getInstance();

        progressBar2 = findViewById(R.id.progressBar2);



        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        btnLoginL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailL.getText().toString().trim();
                String password = etPasswordL.getText().toString().trim();

                if(TextUtils.isEmpty(email)){

                    etEmailL.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){

                    etEmailL.setError("Password is Required.");
                    return;
                }

                if(password.length()<6){

                    etEmailL.setError("Password Must be >= 6 Characters");
                    return;

                }

                progressBar2.setVisibility(View.VISIBLE);


                // Authenticate the user
                final FirebaseUser firebaseUser = fAuth.getCurrentUser();
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {


                            // Checking if the email is verified or not.
                            if(firebaseUser!=null && firebaseUser.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            }
                            else
                            {

                                Toast.makeText(LoginActivity.this,"Please verify email to access the app",Toast.LENGTH_LONG).show();
                                progressBar2.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Error !"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // Forgot password link.
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetmail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter Your email to reset password");
                passwordResetDialog.setView(resetmail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract email and send reset link
                        String mail = resetmail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this,"Reset Link Sent to your email.",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,"Error! Link is Not Sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close Dialog
                        Toast.makeText(LoginActivity.this,"Operation Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }



}
