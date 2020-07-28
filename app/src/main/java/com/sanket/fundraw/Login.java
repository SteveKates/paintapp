package com.sanket.fundraw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    EditText otp,contact;
    Button login,getOtp;
    String mobile;
    private FirebaseAuth mAuth;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        contact = findViewById(R.id.contact1);
        otp = findViewById(R.id.otp1);
        login = findViewById(R.id.login1);
        getOtp = findViewById(R.id.getotp1);
        mAuth = FirebaseAuth.getInstance();
        login.setVisibility(View.GONE);
        getOtp.setVisibility(View.VISIBLE);
        otp.setEnabled(false);

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = contact.getText().toString();
                if (mobile.isEmpty() || mobile.length() < 10)
                {
                    Toast.makeText(Login.this, "Enter Correct Contact Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    login.setVisibility(View.VISIBLE);
                    getOtp.setVisibility(View.GONE);
                    otp.setEnabled(true);
                    sendVerificationCode(mobile);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp1 = otp.getText().toString();
                if (otp1.isEmpty() || otp1.length() < 6){
                    Toast.makeText(Login.this, "Enter Correct OTP", Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyVerificationCode(otp1);
                }
            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(Login.this, "OTP has been sent, please check your mobile", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences sp1 = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sp1.edit();
                            editor1.putString("mobile",mobile);
                            editor1.apply();
                            editor1.commit();

                            SharedPreferences sp2 = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sp2.edit();
                            editor2.putString("logged","1");
                            editor2.apply();
                            editor2.commit();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Login.this,Welcome.class));
    }

}