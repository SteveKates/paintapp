package com.sanket.fundraw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {

    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        login = findViewById(R.id.btn_login);

        SharedPreferences sp1 = getSharedPreferences("login", Context.MODE_PRIVATE);
        String check1 = sp1.getString("logged","0");

        if (check1.equals("0")){
            login.setVisibility(View.VISIBLE);
        }
        else{
            startActivity(new Intent(Welcome.this,MainActivity.class));
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,Login.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
        finishAndRemoveTask();
    }
}