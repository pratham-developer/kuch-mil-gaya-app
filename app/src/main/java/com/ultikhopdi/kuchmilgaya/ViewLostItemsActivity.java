package com.ultikhopdi.kuchmilgaya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import android.widget.EditText;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

public class ViewLostItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostItemAdapter adapter;
    private List<LostItem> lostItemList;
    private List<LostItem> filteredLostItemList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_items);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gold));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gold));



        // Get the search icon drawable
        int searchIconId = androidx.appcompat.R.id.search_mag_icon;
        ImageView searchIcon = searchView.findViewById(searchIconId);

        // Apply color filter to the search icon
        searchIcon.setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_IN);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lostItemList = new ArrayList<>();
        filteredLostItemList = new ArrayList<>();
        adapter = new LostItemAdapter(filteredLostItemList, this);
        recyclerView.setAdapter(adapter);

        loadDataFromDatabase();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return false;
            }
        });
    }

    private void loadDataFromDatabase() {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        databaseReference.orderByChild("claimed").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lostItemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LostItem lostItem = snapshot.getValue(LostItem.class);
                        if (lostItem != null && !lostItem.getUserId().equalsIgnoreCase(currentEmail)) {
                            lostItemList.add(lostItem);
                            Log.d("ViewLostItemsActivity", "Lost Item: " + lostItem.getItemName());
                        } else {
                            Log.d("ViewLostItemsActivity", "LostItem is  or email matches");
                        }
                    }
                    if (lostItemList.isEmpty()) {
                        Toast.makeText(ViewLostItemsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                    filteredLostItemList.clear();
                    filteredLostItemList.addAll(lostItemList);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ViewLostItemsActivity", "No data found in database");
                    // Only show toast if no data was previously loaded
                    if (lostItemList.isEmpty()) {
                        Toast.makeText(ViewLostItemsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewLostItemsActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(ViewLostItemsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItems(String query) {
        filteredLostItemList.clear();
        if (query.isEmpty()) {
            filteredLostItemList.addAll(lostItemList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredLostItemList.addAll(lostItemList.stream()
                    .filter(item -> item.getItemName().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }
}
