package com.bscs.resellingapp.Adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.EditProduct;
import com.bscs.resellingapp.ProductScreen;
import com.bscs.resellingapp.R;
import com.bscs.resellingapp.model.ItemDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class LikedItemAdapter extends RecyclerView.Adapter<LikedItemAdapter.ItemViewHolder> {
    private ArrayList<ItemDetail> itemList;
    private Context context;
    private boolean isProfileScreen;

    public LikedItemAdapter(ArrayList<ItemDetail> itemList, Context context, boolean isProfileScreen) {
        this.itemList = itemList;
        this.context = context;
        this.isProfileScreen = isProfileScreen;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liked, parent, false);
        return new ItemViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(ItemDetail item);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final ItemDetail currentItem = itemList.get(position);
        System.out.println("onBindViewHolder"+currentItem.getItemId());
        System.out.println("onBindViewHolder"+currentItem.getUserId());
        System.out.println("onBindViewHolder"+currentItem.getProductName());
        holder.itemNameTextView.setText(currentItem.getProductName());
        holder.itemPriceTextView.setText(currentItem.getProductPrice());
        Picasso.get().load(currentItem.getImageUrl1()).into(holder.itemImageView);

        holder.heartImageView.setOnClickListener(v -> {
            // Toggle the heart icon and update the heartImage value in the ItemDetail object
            if (currentItem.getHeartImage() == R.drawable.heartnofill) {
                holder.heartImageView.setImageResource(R.drawable.fillheart);
                currentItem.setHeartImage(R.drawable.fillheart);
                saveToFavorites(position,holder.heartImageView);
            } else {
                holder.heartImageView.setImageResource(R.drawable.heartnofill);
                currentItem.setHeartImage(R.drawable.heartnofill);
                removeItemFromFavorites(position);
            }
        });

        holder.prodScreen.setOnClickListener(v->{
            Intent intent = new Intent(context, ProductScreen.class);
            intent.putExtra("itemId", currentItem.getItemId()); // Pass the itemId
            context.startActivity(intent);
        });




        holder.itemImageView.setOnClickListener(v -> {
            if (isProfileScreen) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setTitle("Edit Product Details");
                builder.setMessage("Do you want to edit this product details?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Item Id is: " + currentItem.getItemId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, EditProduct.class);
                        intent.putExtra("itemId", currentItem.getItemId()); // Pass the itemId to the intent
                        context.startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Intent intent = new Intent(context, ProductScreen.class);
                Toast.makeText(context, "Item Id is: " + currentItem.getItemId(), Toast.LENGTH_SHORT).show();

                intent.putExtra("userId", currentItem.getUserId());
                intent.putExtra("itemId", currentItem.getItemId());
                intent.putExtra("itemImageUrl1", currentItem.getImageUrl1());
                intent.putExtra("itemImageUrl2", currentItem.getImageUrl2());
                intent.putExtra("itemImageUrl3", currentItem.getImageUrl3());
                intent.putExtra("productName", currentItem.getProductName());
                intent.putExtra("productPrice", currentItem.getProductPrice());
                intent.putExtra("productBrand", currentItem.getProductBrand());
                intent.putExtra("productCondition", currentItem.getProductCondition());
                intent.putExtra("productCategory", currentItem.getProductCategory());
                intent.putExtra("productDescription", currentItem.getProductDescription());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void removeItemFromFavorites(int position) {
        ItemDetail currentItem = itemList.get(position);
        // Get the current user's ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Remove the item from the "Liked" node under the user's ID
        DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference().child("Liked").child(uid).child(currentItem.getItemId());
        likedRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToFavorites(int position,ImageView heartImageView) {
        ItemDetail currentItem = itemList.get(position);
        // Get the current user's ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Store the product details in the "Liked" node under the user's ID
        DatabaseReference likedRef = FirebaseDatabase.getInstance().getReference().child("Liked").child(uid).child(currentItem.getItemId());
        likedRef.setValue(currentItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                        heartImageView.setImageResource(R.drawable.fillheart);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        ImageView heartImageView;
        LinearLayout prodScreen;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImage);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            heartImageView = itemView.findViewById(R.id.heartImage);
            prodScreen = itemView.findViewById(R.id.llItem);

        }
    }
}
