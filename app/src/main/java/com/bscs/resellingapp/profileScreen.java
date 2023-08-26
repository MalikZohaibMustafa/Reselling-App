package com.bscs.resellingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.Adaptor.profileItemAdapter;
import com.bscs.resellingapp.model.ItemDetail;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profileScreen extends AppCompatActivity implements profileItemAdapter.OnItemClickListener{
    private TextView emailTextView;
    private TextView usernameTextView;
    private ImageView profileImage;
    private DatabaseReference userRef;
    RecyclerView recyclerView;
    ArrayList<ItemDetail> itemList;
    profileItemAdapter profileItemAdapter;
    TextView totalListings;

    @Override
    public void onItemClick(ItemDetail item) {
        // Show a popup asking to edit the product details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Product Details");
        builder.setMessage("Do you want to edit this product details?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Open the EditActivity and pass the item data
            Intent intent = new Intent(profileScreen.this, EditProduct.class);
            intent.putExtra("itemId", item.getItemId());
            intent.putExtra("productName", item.getProductName());
            // Pass other item details as needed
            startActivity(intent);
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.profileName);
        profileImage=findViewById(R.id.profileImage);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        totalListings = findViewById(R.id.TotalListings);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            System.out.println("User " + userId + "\n\n" + currentUser.getTenantId() + "\n" + currentUser.getProviderId() );
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        }
        fetchUserData();
        initData();
        initRecyclerView();

    }
    private void initData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        itemList = new ArrayList<>();

        itemsRef.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemId = itemSnapshot.getKey();
                    String itemImage = itemSnapshot.child("imageUrl1").getValue(String.class);
                    String itemName = itemSnapshot.child("productName").getValue(String.class);
                    String itemPrice = itemSnapshot.child("productPrice").getValue(String.class);
                    itemList.add(new ItemDetail(itemId, itemImage, itemName, itemPrice, R.drawable.fillheart));

                }
                profileItemAdapter.notifyDataSetChanged();
                totalListings.setText("My Total Listings: "+ itemList.size() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.ListingRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        profileItemAdapter = new profileItemAdapter(itemList, this,true);
        recyclerView.setAdapter(profileItemAdapter);
        profileItemAdapter.notifyDataSetChanged();
    }


    private void fetchUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String imageUrl= dataSnapshot.child("imageUrl").getValue(String.class);

                    if (email != null) {
                        emailTextView.setText(email);
                    }

                    if (username != null) {
                        usernameTextView.setText(username);
                    }
                    if (imageUrl!=null) {
                        RequestOptions requestOptions = new RequestOptions()
                                .transform(new RoundedCorners(20));

                        Glide.with(profileScreen.this)
                                .load(imageUrl)
                                .apply(requestOptions)
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
            }
        });
    }


}
