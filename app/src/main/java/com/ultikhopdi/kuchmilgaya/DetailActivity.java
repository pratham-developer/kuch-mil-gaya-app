package com.ultikhopdi.kuchmilgaya;
import android.content.Intent;

import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView itemName = findViewById(R.id.item_name);
        TextView itemDate = findViewById( R.id.item_date);
        TextView itemTime = findViewById(R.id.item_time);
        TextView itemPlace = findViewById(R.id.item_place);
        TextView itemDesc = findViewById(R.id.item_desc);
        TextView itemContact = findViewById(R.id.item_contact);
        ImageView itemImage = findViewById(R.id.item_image);

        Intent intent = getIntent();
        itemName.setText(intent.getStringExtra("item_name"));
        itemDate.setText(intent.getStringExtra("item_date"));
        itemTime.setText(intent.getStringExtra("item_time"));
        itemPlace.setText(intent.getStringExtra("item_place"));
        itemDesc.setText(intent.getStringExtra("item_desc"));
        itemContact.setText(intent.getStringExtra("item_contact"));

        String imageUrl = intent.getStringExtra("item_image_url");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(itemImage);
        }
    }
}
