package com.bscs.resellingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class signinactivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button signupp;
    private TextView signup;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinactivity);
        Intent signUpIntent=new Intent(signinactivity.this,MainActivity.class);
        Button btn3=findViewById(R.id.button3);
        editTextEmail = findViewById(R.id.editTextTextPersonName2);
        editTextPassword = findViewById(R.id.editTextTextPersonName4);
        buttonLogin = findViewById(R.id.buttonLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signinactivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Login successful
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                Toast.makeText(signinactivity.this, "Logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signinactivity.this,HomeScreen.class));
                                finish();
                            } else {
                                // Login failed
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Log.e("LoginActivity", "Invalid email format", e);
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Log.e("LoginActivity", "Invalid email or password", e);
                                } catch (Exception e) {
                                    Log.e("LoginActivity", "Login failed", e);
                                }
                                Toast.makeText(signinactivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(signUpIntent);
            }
        });
    }
}