package com.bscs.resellingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.Adaptor.ItemRVAdapter;
import com.bscs.resellingapp.model.ItemDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchscreen extends AppCompatActivity {
    private RecyclerView searchRecyclerView;
    private ItemRVAdapter adapter;
    private ArrayList<ItemDetail> itemList;
    private EditText searchEditText;

    private DatabaseReference itemsRef;
    private ValueEventListener searchValueEventListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchscreen);

        searchRecyclerView = findViewById(R.id.seachRecyclerView);
        searchEditText = findViewById(R.id.searchView);
        View searchBtn = findViewById(R.id.searchBtn);

        itemList = new ArrayList<>();
        adapter = new ItemRVAdapter(itemList, this,false);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(adapter);
        itemsRef = FirebaseDatabase.getInstance().getReference().child("items");

        // Set click listener for the search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                performSearch(query);
            }
        });
    }

    private void performSearch(String query) {
        // Remove the previous ValueEventListener to avoid duplicate callbacks
        if (searchValueEventListener != null) {
            itemsRef.removeEventListener(searchValueEventListener);
        }

        // Clear the item list before performing a new search
        itemList.clear();
        adapter.notifyDataSetChanged();

        // Perform the search query
        if (!query.isEmpty()) {
            Query searchQuery = itemsRef.orderByChild("productName")
                    .startAt(query)
                    .endAt(query + "\uf8ff");

            searchValueEventListener = searchQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        ItemDetail item = itemSnapshot.getValue(ItemDetail.class);
                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if retrieval is unsuccessful
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener to avoid memory leaks
        if (searchValueEventListener != null) {
            itemsRef.removeEventListener(searchValueEventListener);
        }
    }
}