package com.bscs.resellingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductScreen extends AppCompatActivity {
    SliderPagerAdapter sliderPagerAdapter;
    ViewPager imageViewPager;
    List<String> imageUrls;
    TextView prodNameTextView;
    TextView prodPriceTextView;
    TextView prodBrandTextView;
    TextView conditionTextView;
    TextView prodDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_screen);
        prodNameTextView = findViewById(R.id.prodNameTextView);
        prodPriceTextView = findViewById(R.id.prodPriceTextView);
        prodBrandTextView = findViewById(R.id.prodBrandTextView);
        conditionTextView = findViewById(R.id.conditionTextView);
        prodDescriptionTextView = findViewById(R.id.prodDescriptionTextView);
        Intent chatIntent = new Intent(ProductScreen.this, ChatActivity.class);
        Intent payIntent = new Intent(ProductScreen.this, PayScreen.class);
        ImageView backbutton = findViewById(R.id.imageView27);
        Button buttonChat = findViewById(R.id.buttonLogin5);
        Button buttonBuy = findViewById(R.id.buttonLogin3);

        imageViewPager = findViewById(R.id.imageViewPager);
        sliderPagerAdapter = new SliderPagerAdapter(this);
        imageViewPager.setAdapter(sliderPagerAdapter);

        String itemId = getIntent().getStringExtra("itemId");
        fetchProductDetails(itemId);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
                itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Retrieve the user ID for the seller
                            String sellerId = dataSnapshot.child("userId").getValue(String.class);

                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(sellerId);
                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        // Retrieve the image URL and username for the seller
                                        String imageUrl = userSnapshot.child("imageUrl").getValue(String.class);
                                        String username = userSnapshot.child("username").getValue(String.class);

                                        // Get the current user ID
                                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        // Check if the current user is the seller
                                        if (currentUserId.equals(sellerId)) {
                                            // Display a message that the user cannot chat with themselves
                                            Toast.makeText(ProductScreen.this, "You cannot chat with yourself.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Create an intent to launch the ChatActivity
                                            Intent intent = new Intent(ProductScreen.this, ChatActivity.class);
                                            // Pass the receiver ID (seller's ID), sender ID (current user's ID), image URL, and username as intent extras
                                            intent.putExtra("receiverId", sellerId);
                                            intent.putExtra("senderId", currentUserId);
                                            intent.putExtra("imageUrl", imageUrl);
                                            intent.putExtra("username", username);
                                            startActivity(intent);
                                        }
                                    } else {
                                        // Handle the case where the user does not exist in the database
                                        Toast.makeText(ProductScreen.this, "User not found.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle database error if needed
                                }
                            });
                        } else {
                            // Handle the case where the item does not exist in the database
                            Toast.makeText(ProductScreen.this, "Item not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error if needed
                    }
                });
            }
        });


        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(payIntent);
            }
        });
    }

    // Define the PagerAdapter class
    class SliderPagerAdapter extends PagerAdapter {
        private Context context;
        private List<String> imageUrls;

        public SliderPagerAdapter(Context context) {
            this.context = context;
            this.imageUrls = new ArrayList<>();
        }

        public void setImages(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.get().load(imageUrls.get(position)).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }

    private void fetchProductDetails(String itemId) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the product details from the dataSnapshot
                    String productName = dataSnapshot.child("productName").getValue(String.class);
                    String productPrice = dataSnapshot.child("productPrice").getValue(String.class);
                    String productBrand = dataSnapshot.child("productBrand").getValue(String.class);
                    String productDescription = dataSnapshot.child("productDescription").getValue(String.class);
                    String productCondition = dataSnapshot.child("productCondition").getValue(String.class);

                    // Update the TextViews in the layout with the retrieved values
                    prodNameTextView.setText(productName);
                    prodPriceTextView.setText(productPrice);
                    prodBrandTextView.setText(productBrand);
                    prodDescriptionTextView.setText(productDescription);
                    conditionTextView.setText(productCondition);

                    List<String> imageUrls = new ArrayList<>();
                    for (int i = 1; i <= 3; i++) {
                        String imageUrl = dataSnapshot.child("imageUrl" + i).getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrls.add(imageUrl);
                        }
                    }
                    sliderPagerAdapter.setImages(imageUrls);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }
}