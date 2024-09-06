package com.ultikhopdi.kuchmilgaya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.LostItemViewHolder> {

    private List<LostItem> lostItemList;
    private Context context;
    private Boolean isMyClaims;

    public LostItemAdapter(List<LostItem> lostItemList, Context context) {
        this.lostItemList = lostItemList;
        this.context = context;
        this.isMyClaims = false;
    }
    public LostItemAdapter(List<LostItem> lostItemList, Context context, Boolean isMyClaims) {
        this.lostItemList = lostItemList;
        this.context = context;
        this.isMyClaims = isMyClaims;
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
        holder.itemDate.setText("Date : "+lostItem.getDate());
        holder.itemTime.setText("Time : "+lostItem.getTime());
        holder.itemPlace.setText("Place : "+lostItem.getPlace());
        boolean claimed = lostItem.getClaimed();
        if(!claimed){
            holder.tickImage.setVisibility(View.GONE);
        }
        else{
            holder.tickImage.setVisibility(View.VISIBLE);
        }

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
            intent.putExtra("uploadedBy",lostItem.getUserReg());
            intent.putExtra("isMyClaims", isMyClaims);
            if (context instanceof ViewLostItemsActivity) {
                intent.putExtra("viewall", true);
            } else {
                intent.putExtra("viewall", false);

            }
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                    context, R.anim.flip_in, R.anim.flip_out);
            ActivityCompat.startActivity(context, intent, options.toBundle());

        });
    }

    @Override
    public int getItemCount() {
        return lostItemList.size();
    }

    public static class LostItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemDate, itemTime, itemPlace, itemDesc, itemContact;
        public ImageView itemImage, tickImage;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemDate = itemView.findViewById(R.id.item_date);
            itemTime = itemView.findViewById(R.id.item_time);
            itemPlace = itemView.findViewById(R.id.item_place);
            itemDesc = itemView.findViewById(R.id.item_desc);
            itemContact = itemView.findViewById(R.id.item_contact);
            itemImage = itemView.findViewById(R.id.item_image);
            tickImage = itemView.findViewById(R.id.img_tick);
        }
    }
}
