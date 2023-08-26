package com.bscs.resellingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bscs.resellingapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private Button buttonSignup;
    private FirebaseAuth firebaseAuth;
    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView profileImageView;
    private Button uploadButton;
    private Uri selectedImageUri;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImageView.setImageURI(selectedImageUri);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent signInIntent=new Intent(MainActivity.this,signinactivity.class);
        Button  btn1=findViewById(R.id.button);
        editTextEmail = findViewById(R.id.editTextTextPersonName2);
        editTextPassword = findViewById(R.id.editTextTextPersonName4);
        editTextUsername=findViewById(R.id.editTextTextPersonName3);
        buttonSignup = findViewById(R.id.SignupButton);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        uploadButton = findViewById(R.id.buttonupload);
        profileImageView = findViewById(R.id.imageView5);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Signup successful
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String userId = user.getUid();
                                Toast.makeText(MainActivity.this, "Signed up as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                uploadDataAndImage(userId);
                                startActivity(new Intent(MainActivity.this, signinactivity.class));
                                finish();
                            } else {
                                // Signup failed
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Log.e("SignupActivity", "Invalid email format", e);
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Log.e("SignupActivity", "Invalid email or password", e);
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Log.e("SignupActivity", "User with this email already exists", e);
                                } catch (Exception e) {
                                    Log.e("SignupActivity", "Signup failed", e);
                                }
                                Toast.makeText(MainActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(signInIntent);
            }
        });
    }

    private void uploadDataAndImage(String userId) {
        // Get the input values
        String email = editTextEmail.getText().toString().trim();
        String username= editTextUsername.getText().toString().trim();
        // Validate the input fields
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user object with the input values
        UserModel newUser = new UserModel(userId, email,username);
        // Generate a unique key for the new user
        newUser.setUserId(userId);

        // Store the new user object in the Firebase Realtime Database
        mDatabase.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    // Display success message
                    Toast.makeText(MainActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    // Upload the image to Firebase Storage
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(userId);
                    } else {
                        Toast.makeText(MainActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Display failure message
                    Toast.makeText(MainActivity.this, "Failed to add user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageToFirebase(String itemId) {
        StorageReference fileReference = storageReference.child(itemId);
        UploadTask uploadTask = fileReference.putFile(selectedImageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return fileReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    String imageUrl = downloadUri.toString();
                    // Update the user object with the image URL in the database
                    mDatabase.child(itemId).child("imageUrl").setValue(imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputFields() {
        editTextEmail.setText("");
        // Clear additional fields as required
        profileImageView.setImageDrawable(null);
        selectedImageUri = null;
    }
}
