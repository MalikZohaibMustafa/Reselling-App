package com.bscs.resellingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.Adaptor.ItemRVAdapter;
import com.bscs.resellingapp.model.ItemDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<ItemDetail> itemList;
    private ItemRVAdapter itemRVAdapter;
    private TextView helloUser;
    private String selectedCategory = "";

    private static final int SEARCH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initView();
        initData();
        initRecyclerView();
        initClickListeners();
        fetchItems();
        fetchUsername();
    }

    private void initView() {
        helloUser = findViewById(R.id.HelloTextView);
        recyclerView = findViewById(R.id.historyRecyclerView);
        ImageView likedBtnBottom = findViewById(R.id.LikedBtn);
        ImageView searchBtnBottom = findViewById(R.id.SearchBtn);
        ImageView addBtnBottom = findViewById(R.id.AddBtn);
        ImageView chatBtnBottom = findViewById(R.id.ChatBtn);
        ImageView myProfileBtnBottom = findViewById(R.id.MyProfileBtn);

        // Set click listeners for the bottom navigation buttons
        likedBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLikeScreen = new Intent(HomeScreen.this, LikedScreen.class);
                intentLikeScreen.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intentLikeScreen);
            }
        });

        searchBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearchScreen = new Intent(HomeScreen.this, searchscreen.class);
                intentSearchScreen.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intentSearchScreen, SEARCH_REQUEST_CODE);
            }
        });

        addBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddScreen = new Intent(HomeScreen.this, additemscreen.class);
                intentAddScreen.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intentAddScreen);
            }
        });

        chatBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChatScreen = new Intent(HomeScreen.this, ProductChat.class);
                intentChatScreen.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intentChatScreen);
            }
        });
        myProfileBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileScreen = new Intent(HomeScreen.this, profileScreen.class);
                intentProfileScreen.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intentProfileScreen);
            }
        });
    }

    private void initData() {
        itemList = new ArrayList<>();
        itemRVAdapter = new ItemRVAdapter(itemList, this,false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemRVAdapter);
    }

    private void initClickListeners() {
        ImageView luxuryIcon = findViewById(R.id.imageView19);
        ImageView techsIcon = findViewById(R.id.imageView20);
        ImageView clothesIcon = findViewById(R.id.imageView21);
        ImageView furnitureIcon = findViewById(R.id.imageView22);

        luxuryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory.equals("Luxury")) {
                    selectedCategory = ""; // Deselect the category
                } else {
                    selectedCategory = "Luxury";
                }
                fetchItems();
            }

        });

        techsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory.equals("Techs")) {
                    selectedCategory = ""; // Deselect the category
                } else {
                    selectedCategory = "Techs";
                }
                fetchItems();

            }
        });

        clothesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory.equals("Clothes")) {
                    selectedCategory = ""; // Deselect the category
                } else {
                    selectedCategory = "Clothes";
                }
                fetchItems();
            }
        });

        furnitureIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategory.equals("Furniture")) {
                    selectedCategory = ""; // Deselect the category
                } else {
                    selectedCategory = "Furniture";
                }
                fetchItems();
            }
        });
    }

    private void fetchItems() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        Query query;
        if (selectedCategory.isEmpty()) {
            query = itemsRef.orderByChild("productPrice");
        } else {
            query = itemsRef.orderByChild("productCategory").equalTo(selectedCategory);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Clear the previous items
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve item details
                    String itemImage = itemSnapshot.child("imageUrl1").getValue(String.class);
                    String itemImage2 = itemSnapshot.child("imageUrl2").getValue(String.class);
                    String itemImage3 = itemSnapshot.child("imageUrl3").getValue(String.class);
                    String productName = itemSnapshot.child("productName").getValue(String.class);
                    String productPrice = itemSnapshot.child("productPrice").getValue(String.class);
                    String productBrand = itemSnapshot.child("productBrand").getValue(String.class);
                    String productCategory = itemSnapshot.child("productCategory").getValue(String.class);
                    String productDescription = itemSnapshot.child("productDescription").getValue(String.class);
                    String productCondition = itemSnapshot.child("productCondition").getValue(String.class);
                    String userId = itemSnapshot.child("userId").getValue(String.class);
                    String itemId = itemSnapshot.child("itemId").getValue(String.class);

                    // Create ItemDetail object and add it to the itemList
                    ItemDetail itemDetail = new ItemDetail(userId, itemId, productName, productPrice, productBrand,
                            productCondition, productCategory, productDescription, itemImage,
                            itemImage2, itemImage3, R.drawable.heartnofill);

                    itemList.add(itemDetail);
                }
                itemRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        });
    }

    private void fetchUsername() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        helloUser.setText("Hello, " + username);
                        System.out.println("Username: " + username);
                    }
                } else {
                    System.out.println("User data doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.historyRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        itemRVAdapter = new ItemRVAdapter(itemList, this,false);
        recyclerView.setAdapter(itemRVAdapter);
        itemRVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String searchQuery = data.getStringExtra("searchQuery");
                performSearch(searchQuery);
            }
        }
    }

    private void performSearch(String searchQuery) {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        Query query = itemsRef.orderByChild("productName").startAt(searchQuery).endAt(searchQuery + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Clear the previous items
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve item details
                    String itemImage = itemSnapshot.child("imageUrl1").getValue(String.class);
                    String itemImage2 = itemSnapshot.child("imageUrl2").getValue(String.class);
                    String itemImage3 = itemSnapshot.child("imageUrl3").getValue(String.class);
                    String productName = itemSnapshot.child("productName").getValue(String.class);
                    String productPrice = itemSnapshot.child("productPrice").getValue(String.class);
                    String productBrand = itemSnapshot.child("productBrand").getValue(String.class);
                    String productCategory = itemSnapshot.child("productCategory").getValue(String.class);
                    String productDescription = itemSnapshot.child("productDescription").getValue(String.class);
                    String productCondition = itemSnapshot.child("productCondition").getValue(String.class);
                    String userId = itemSnapshot.child("userId").getValue(String.class);
                    String itemId = itemSnapshot.child("itemId").getValue(String.class);

                    // Create ItemDetail object and add it to the itemList
                    ItemDetail itemDetail = new ItemDetail(userId, itemId, productName, productPrice, productBrand,
                            productCondition, productCategory, productDescription, itemImage,
                            itemImage2, itemImage3, R.drawable.heartnofill);

                    itemList.add(itemDetail);
                }
                itemRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        });
    }
}