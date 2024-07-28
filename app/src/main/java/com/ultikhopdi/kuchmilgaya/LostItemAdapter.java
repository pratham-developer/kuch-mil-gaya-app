package com.ultikhopdi.kuchmilgaya;

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

    public LostItemAdapter(List<LostItem> lostItemList) {
        this.lostItemList = lostItemList;
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
        if (lostItem.getImageUrl() != null && !lostItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(lostItem.getImageUrl()).into(holder.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return lostItemList.size();
    }

    public static class LostItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemDate, itemTime, itemPlace;
        public ImageView itemImage;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemDate = itemView.findViewById(R.id.item_date);
            itemTime = itemView.findViewById(R.id.item_time);
            itemPlace = itemView.findViewById(R.id.item_place);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
