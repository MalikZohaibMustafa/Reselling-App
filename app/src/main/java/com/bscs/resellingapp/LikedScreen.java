package com.bscs.resellingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.Adaptor.LikedItemAdapter;
import com.bscs.resellingapp.model.ItemDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LikedScreen extends AppCompatActivity {
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    ArrayList<ItemDetail> itemList;
    LikedItemAdapter LikedItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_screen);

        initData();
        initRecyclerView();
        ImageView homeButton = findViewById(R.id.imageView34);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Go back to the previous screen
            }
        });
    }

    private void initData() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference likedItemsRef = FirebaseDatabase.getInstance().getReference().child("Liked").child(userId);
        itemList = new ArrayList<>();
        likedItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemImage = itemSnapshot.child("imageUrl1").getValue(String.class);
                    String itemImage2 = itemSnapshot.child("imageUrl2").getValue(String.class);
                    String itemImage3 = itemSnapshot.child("imageUrl3").getValue(String.class);
                    String productName = itemSnapshot.child("productName").getValue(String.class);
                    String productPrice = itemSnapshot.child("productPrice").getValue(String.class);
                    String productBrand = itemSnapshot.child("productBrand").getValue(String.class);
                    String productCategory = itemSnapshot.child("productCategory").getValue(String.class);
                    String productDescription = itemSnapshot.child("productDescription").getValue(String.class);
                    String productCondition = itemSnapshot.child("productCondition").getValue(String.class);
                    String itemId = itemSnapshot.child("itemId").getValue(String.class);
                    itemList.add(new ItemDetail(userId, itemId,productName, productPrice,productBrand,
                            productCondition,productCategory,productDescription,itemImage,
                            itemImage2,itemImage3,R.drawable.fillheart));
                }
                LikedItemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        });
    }
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.ListingRecyclerView);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        LikedItemAdapter = new LikedItemAdapter(itemList, this,false);
        recyclerView.setAdapter(LikedItemAdapter);
        LikedItemAdapter.notifyDataSetChanged();
    }
}
