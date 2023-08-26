package com.bscs.resellingapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProduct extends AppCompatActivity {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Uri selectedImageUri1;
    private Uri selectedImageUri2;
    private Uri selectedImageUri3;
    private EditText titleEditText;
    private Spinner categorySpinner;
    private EditText brandEditText;
    private EditText priceEditText;
    private Spinner conditionSpinner;
    private EditText descriptionEditText;
    private Button updateBtn;
    private String itemId;
    private DatabaseReference itemRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        itemId = getIntent().getStringExtra("itemId");
        imageView1 = findViewById(R.id.imageView49);
        imageView2 = findViewById(R.id.imageView50);
        imageView3 = findViewById(R.id.imageView51);
        titleEditText = findViewById(R.id.titleEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        brandEditText = findViewById(R.id.brandEditText);
        priceEditText = findViewById(R.id.priceEditText);
        conditionSpinner = findViewById(R.id.conditionSpinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        updateBtn = findViewById(R.id.UpdateBtn);
        // Initialize storage reference
        storageReference = FirebaseStorage.getInstance().getReference("items").child(itemId);;

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

        if (itemId != null) {
            // Fetch data from Firebase based on itemId
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ItemModel item = dataSnapshot.getValue(ItemModel.class);
                        if (item != null) {
                            // Populate views with data
                            Glide.with(EditProduct.this).load(item.getImageUrl1()).into(imageView1);
                            Glide.with(EditProduct.this).load(item.getImageUrl2()).into(imageView2);
                            Glide.with(EditProduct.this).load(item.getImageUrl3()).into(imageView3);
                            titleEditText.setText(item.getProductName());
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
                            // Set the selected item in the category spinner
                            ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(EditProduct.this,
                                    R.array.category_items, android.R.layout.simple_spinner_item);
                            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(categoryAdapter);
                            int categoryPosition = categoryAdapter.getPosition(item.getProductCategory());
                            categorySpinner.setSelection(categoryPosition);

                            brandEditText.setText(item.getProductBrand());
                            priceEditText.setText(item.getProductPrice());

                            // Set the selected item in the condition spinner
                            ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(EditProduct.this,
                                    R.array.condition_items, android.R.layout.simple_spinner_item);
                            conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            conditionSpinner.setAdapter(conditionAdapter);
                            int conditionPosition = conditionAdapter.getPosition(item.getProductCondition());
                            conditionSpinner.setSelection(conditionPosition);
                            descriptionEditText.setText(item.getProductDescription());
                        }
                    } else {
                        // Handle the case when itemId is null
                        //Toast.makeText(EditProduct.this, "Item Id is null", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTitle = titleEditText.getText().toString().trim();
                String updatedCategory = categorySpinner.getSelectedItem().toString();
                String updatedBrand = brandEditText.getText().toString().trim();
                String priceText = priceEditText.getText().toString().trim();
                String updatedCondition = conditionSpinner.getSelectedItem().toString();
                String updatedDescription = descriptionEditText.getText().toString().trim();
                String updatedPrice="";
                //Validation for Price
                if (!priceText.startsWith("$ ")) {
                    // Remove any non-digit characters from the price text
                    String numericPrice = priceText.replaceAll("[^\\d.]", "");
                    // Convert the numeric price to an integer
                    int priceValue = Integer.parseInt(numericPrice);
                    // Add the "$ " prefix to the price value
                    updatedPrice = "$ " + priceValue;
                } else {
                    updatedPrice = priceText;
                }
                if (TextUtils.isEmpty(updatedTitle) || updatedCategory.equals(getString(R.string.category_hint)) ||
                        TextUtils.isEmpty(updatedBrand) || updatedCondition.equals(getString(R.string.condition_hint)) ||
                        TextUtils.isEmpty(updatedPrice) || TextUtils.isEmpty(updatedDescription)) {
                    Toast.makeText(EditProduct.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    updateItemInFirebase(updatedTitle, updatedCategory, updatedBrand, updatedPrice, updatedCondition, updatedDescription);
                }
            }
        });
    }


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


    private void updateItemInFirebase(String updatedTitle, String updatedCategory, String updatedBrand, String updatedPrice, String updatedCondition, String updatedDescription) {
        itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        if (itemRef != null) {
            // Update the item details with the new values
            itemRef.child("productName").setValue(updatedTitle);
            itemRef.child("productCategory").setValue(updatedCategory);
            itemRef.child("productBrand").setValue(updatedBrand);
            itemRef.child("productPrice").setValue(updatedPrice);
            itemRef.child("productCondition").setValue(updatedCondition);
            itemRef.child("productDescription").setValue(updatedDescription);
            uploadUpdatedImagesToFirebase();

            Toast.makeText(EditProduct.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Handle the case when itemRef is null
            Toast.makeText(EditProduct.this, "Failed to update item. Database reference is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadUpdatedImagesToFirebase() {

        StorageReference fileReference1 = storageReference.child(itemId + "_image1");
        StorageReference fileReference2 = storageReference.child(itemId + "_image2");
        StorageReference fileReference3 = storageReference.child(itemId + "_image3");

        if(selectedImageUri1!=null) {
            UploadTask uploadTask1 = fileReference1.putFile(selectedImageUri1);
            uploadTask1.addOnSuccessListener(taskSnapshot -> {
                fileReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl1 = uri.toString();
                    itemRef.child("imageUrl1").setValue(imageUrl1);
                    Toast.makeText(EditProduct.this, "Image 1 uploaded successfully", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(EditProduct.this, "Failed to upload Image 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        if(selectedImageUri2!=null) {
            UploadTask uploadTask2 = fileReference2.putFile(selectedImageUri2);
            uploadTask2.addOnSuccessListener(taskSnapshot -> {
                fileReference2.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl2 = uri.toString();
                    itemRef.child("imageUrl2").setValue(imageUrl2);
                    Toast.makeText(EditProduct.this, "Image 2 uploaded successfully", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(EditProduct.this, "Failed to upload Image 2: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        if(selectedImageUri3!=null) {
            UploadTask uploadTask3 = fileReference3.putFile(selectedImageUri3);
            uploadTask3.addOnSuccessListener(taskSnapshot -> {
                fileReference3.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl3 = uri.toString();
                    itemRef.child("imageUrl3").setValue(imageUrl3);
                    Toast.makeText(EditProduct.this, "Image 3 uploaded successfully", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(EditProduct.this, "Failed to upload Image 3: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }






    }
}