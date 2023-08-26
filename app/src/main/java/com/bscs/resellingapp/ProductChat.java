package com.bscs.resellingapp;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bscs.resellingapp.Adaptor.UserAdapter;
import com.bscs.resellingapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
public class ProductChat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_chat);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the userList
        userList = new ArrayList<>();

        // Initialize the UserAdapter
        userAdapter = new UserAdapter(this, userList);

        // Set the adapter on the RecyclerView
        recyclerView.setAdapter(userAdapter);

        // Fetch user data from the database
        fetchUsers();
    }

    private void fetchUsers() {
        // Get the reference to the "users" node in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Query to retrieve the users
        Query query = usersRef.orderByChild("username");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear the previous user list

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    if(FirebaseAuth.getInstance().getCurrentUser().getUid()!= userId) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        String userName = userSnapshot.child("username").getValue(String.class);
                        String imageUrl = userSnapshot.child("imageUrl").getValue(String.class);
                        // Create UserModel object and add it to the userList
                        UserModel user = new UserModel(userId, email, userName, imageUrl);
                        userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        });
    }
}
