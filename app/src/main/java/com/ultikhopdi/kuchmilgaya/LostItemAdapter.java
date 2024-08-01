package com.ultikhopdi.kuchmilgaya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder> {

    private List<LostItem> lostItemList;
    private Context context;

    public LostItemAdapter(List<LostItem> lostItemList, Context context) {
        this.lostItemList = lostItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost, parent, false);
        return new LostItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostItemViewHolder holder, int position) {
        LostItem lostItem = lostItemList.get(position);
        holder.itemName.setText(lostItem.getItemName());
        holder.itemDate.setText(lostItem.getDate());
        holder.itemTime.setText(lostItem.getTime());
        holder.itemPlace.setText(lostItem.getPlace());
        holder.itemDesc.setText(lostItem.getDesc());
        holder.itemContact.setText(lostItem.getContact());

        if (lostItem.getImageUrl() != null && !lostItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(lostItem.getImageUrl()).into(holder.itemImage);
        }

        // Set OnClickListener to open new activity with item details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("item_name", lostItem.getItemName());
            intent.putExtra("item_date", lostItem.getDate());
            intent.putExtra("item_time", lostItem.getTime());
            intent.putExtra("item_place", lostItem.getPlace());
            intent.putExtra("item_desc", lostItem.getDesc());
            intent.putExtra("item_contact", lostItem.getContact());
            intent.putExtra("item_image_url", lostItem.getImageUrl());
            intent.putExtra("item_uid",lostItem.getId());
            if (context instanceof ViewLostItemsActivity) {
                intent.putExtra("viewall", true);
            } else {
                intent.putExtra("viewall", false);
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lostItemList.size();
    }

    public static class LostItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemDate, itemTime, itemPlace, itemDesc, itemContact;
        public ImageView itemImage;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemDate = itemView.findViewById(R.id.item_date);
            itemTime = itemView.findViewById(R.id.item_time);
            itemPlace = itemView.findViewById(R.id.item_place);
            itemDesc = itemView.findViewById(R.id.item_desc);
            itemContact = itemView.findViewById(R.id.item_contact);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
