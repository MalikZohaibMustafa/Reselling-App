package com.bscs.resellingapp;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bscs.resellingapp.model.ItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class additemscreen extends AppCompatActivity {
    private EditText productNameEditText;
    private Spinner categorySpinner;
    private EditText brandEditText;
    private Spinner conditionSpinner;
    private EditText priceEditText;
    private EditText descriptionEditText;
    private Button btnAdd;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Uri selectedImageUri1;
    private Uri selectedImageUri2;
    private Uri selectedImageUri3;
    private DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri1 = data.getData();
            if (selectedImageUri1 != null) {
                imageView1.setImageURI(selectedImageUri1);
            }
        }
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
                selectedImageUri2 = data.getData();
                if (selectedImageUri2 != null) {
                    imageView2.setImageURI(selectedImageUri2);
                }
        }
        if (requestCode == 300 && resultCode == RESULT_OK && data != null) {
                selectedImageUri3 = data.getData();
                if (selectedImageUri3 != null) {
                    imageView3.setImageURI(selectedImageUri3);
                }
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additemscreen);
        productNameEditText = findViewById(R.id.titleEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        brandEditText = findViewById(R.id.brandEditText);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        btnAdd = findViewById(R.id.addbtn);
        imageView1 = findViewById(R.id.imageView49);
        imageView2 = findViewById(R.id.imageView50);
        imageView3 = findViewById(R.id.imageView51);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference("items");
        storageReference = FirebaseStorage.getInstance().getReference("item_images");

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) view).setTextColor(Color.parseColor("#A4A4A4"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setText("Select condition");
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#A4A4A4"));
            }
        });
        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) view).setTextColor(Color.parseColor("#A4A4A4"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setText("Select condition");
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#A4A4A4"));
            }
        });

        // Create a custom adapter for the categorySpinner and set the hint text as the first item
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getApplicationContext(),
                R.array.category_items, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(0, true); // Set the hint item as selected


        // Create a custom adapter for the conditionSpinner and set the hint text as the first item
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(this,
                R.array.condition_items, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
        conditionSpinner.setSelection(0, true); // Set the hint item as selected

        // Set click listeners for the Add button and image views
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Toast.makeText(additemscreen.this, "Adding Product. Please Wait", Toast.LENGTH_SHORT).show();

                    ValidateAndUploadProduct(user.getUid());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(additemscreen.this, HomeScreen.class));
                            finish();
                        }
                    }, 3000); // 3000 milliseconds = 3 seconds
                }
                else {
                    Toast.makeText(additemscreen.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 200);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 300);
            }
        });
    }

    private void ValidateAndUploadProduct(String userId) {
        // Get the input values
        String productName = productNameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String brand = brandEditText.getText().toString().trim();
        String condition = conditionSpinner.getSelectedItem().toString();
        String priceText = priceEditText.getText().toString().trim();
        String price = "";
        //Validation for Price
        if (!priceText.startsWith("$ ")) {
            // Remove any non-digit characters from the price text
            String numericPrice = priceText.replaceAll("[^\\d.]", "");
            // Convert the numeric price to an integer
            int priceValue = Integer.parseInt(numericPrice);
            // Add the "$ " prefix to the price value
            price = "$ " + priceValue;
        } else {
            price = priceText;
        }

        String description = descriptionEditText.getText().toString().trim();
        // Validate the input fields
        if (TextUtils.isEmpty(productName) || category.equals(getString(R.string.category_hint)) ||
                TextUtils.isEmpty(brand) || condition.equals(getString(R.string.condition_hint)) ||
                TextUtils.isEmpty(price) || TextUtils.isEmpty(description) ||
                selectedImageUri1 == null || selectedImageUri2 == null || selectedImageUri3 == null) {
            Toast.makeText(this, "Please fill in all the fields and select all three images", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a new item object with the input values
        ItemModel newItem = new ItemModel(userId, productName, price, brand, condition, category, description);

        // Generate a unique key for the new item
        String itemId = mDatabase.push().getKey();
        newItem.setItemId(itemId);

        // Upload the item details and images to Firebase
        uploadItemAndImagesToFirebase(newItem, itemId);
    }
    private void uploadItemAndImagesToFirebase(ItemModel item, String itemId) {
        // Store the new item object in the Firebase Realtime Database
        mDatabase.child(itemId).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    // Upload the images to Firebase Storage
                    if (selectedImageUri1 != null && selectedImageUri2 != null && selectedImageUri3 != null) {
                        uploadImageToFirebase(itemId);
                    } else {
                        Toast.makeText(additemscreen.this, "Some Images not selected", Toast.LENGTH_SHORT).show();
                    }
                    // Display success message
                    Toast.makeText(additemscreen.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Display failure message
                    Toast.makeText(additemscreen.this, "Failed to add Product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadImageToFirebase(String itemId) {
        StorageReference fileReference1 = storageReference.child(itemId + "_image1");
        StorageReference fileReference2 = storageReference.child(itemId + "_image2");
        StorageReference fileReference3 = storageReference.child(itemId + "_image3");

        UploadTask uploadTask1 = fileReference1.putFile(selectedImageUri1);
        UploadTask uploadTask2 = fileReference2.putFile(selectedImageUri2);
        UploadTask uploadTask3 = fileReference3.putFile(selectedImageUri3);

        uploadTask1.continueWithTask(task1 -> {
            if (!task1.isSuccessful()) {
                throw task1.getException();
            }
            return fileReference1.getDownloadUrl();
        }).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Uri downloadUri1 = task1.getResult();
                if (downloadUri1 != null) {
                    String imageUrl1 = downloadUri1.toString();
                    mDatabase.child(itemId).child("imageUrl1").setValue(imageUrl1)
                            .addOnSuccessListener(aVoid -> {
                               Toast.makeText(additemscreen.this, "Image 1 uploaded successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(additemscreen.this, "Failed to upload Image 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(additemscreen.this, "Failed to upload Image 1: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask2.continueWithTask(task2 -> {
            if (!task2.isSuccessful()) {
                throw task2.getException();
            }
            return fileReference2.getDownloadUrl();
        }).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                Uri downloadUri2 = task2.getResult();
                if (downloadUri2 != null) {
                    String imageUrl2 = downloadUri2.toString();
                    mDatabase.child(itemId).child("imageUrl2").setValue(imageUrl2)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(additemscreen.this, "Image 2 uploaded successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(additemscreen.this, "Failed to upload Image 2: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(additemscreen.this, "Failed to upload Image 2: " + task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadTask3.continueWithTask(task3 -> {
            if (!task3.isSuccessful()) {
                throw task3.getException();
            }
            return fileReference3.getDownloadUrl();
        }).addOnCompleteListener(task3 -> {
            if (task3.isSuccessful()) {
                Uri downloadUri3 = task3.getResult();
                if (downloadUri3 != null) {
                    String imageUrl3 = downloadUri3.toString();
                    mDatabase.child(itemId).child("imageUrl3").setValue(imageUrl3)
                            .addOnSuccessListener(aVoid -> {
                               Toast.makeText(additemscreen.this, "Image 3 uploaded successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(additemscreen.this, "Failed to upload Image 3: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(additemscreen.this, "Failed to upload Image 3: " + task3.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}