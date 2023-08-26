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
import androidx.viewpager.widget.ViewPager;

import com.bscs.resellingapp.EditProduct;
import com.bscs.resellingapp.ProductScreen;
import com.bscs.resellingapp.R;
import com.bscs.resellingapp.model.ItemDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class profileItemAdapter extends RecyclerView.Adapter<profileItemAdapter.ItemViewHolder> {
    private ArrayList<ItemDetail> itemList;
    private Context context;
    private boolean isProfileScreen;




    public profileItemAdapter(ArrayList<ItemDetail> itemList, Context context, boolean isProfileScreen) {
        this.itemList = itemList;
        this.context = context;
        this.isProfileScreen = isProfileScreen;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylisting, parent, false);
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
        holder.itemImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.get().load(currentItem.getImageUrl1()).into(holder.itemImageView);

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


    @Override
    public int getItemCount() {
        return itemList.size();
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemPriceTextView;
        LinearLayout prodScreen;
        ViewPager imageViewPager;


        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImage);
            itemImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            itemNameTextView = itemView.findViewById(R.id.itemName);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            prodScreen = itemView.findViewById(R.id.llItem);


        }
    }
}
