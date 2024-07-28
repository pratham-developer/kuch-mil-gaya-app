package com.ultikhopdi.kuchmilgaya;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewLostItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostItemAdapter adapter;
    private List<LostItem> lostItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_items);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lostItemList = new ArrayList<>();
        adapter = new LostItemAdapter(lostItemList);
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lostItemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LostItem lostItem = snapshot.getValue(LostItem.class);
                        if (lostItem != null) {
                            lostItemList.add(lostItem);
                            Log.d("ViewLostItemsActivity", "Lost Item: " + lostItem.getItemName());
                        } else {
                            Log.d("ViewLostItemsActivity", "LostItem is null");
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ViewLostItemsActivity", "No data found in database");
                    Toast.makeText(ViewLostItemsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewLostItemsActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(ViewLostItemsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
