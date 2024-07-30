package com.ultikhopdi.kuchmilgaya;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // Correct import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyItems;
    private LostItemAdapter adapter;
    private List<LostItem> myItemList;
    private List<LostItem> filteredList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        recyclerViewMyItems = findViewById(R.id.recycler_view_my_items);
        recyclerViewMyItems.setLayoutManager(new LinearLayoutManager(this));
        myItemList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Pass context and list to adapter
        adapter = new LostItemAdapter(filteredList, this);
        recyclerViewMyItems.setAdapter(adapter);
        searchView = findViewById(R.id.search_view); // Make sure your layout uses androidx.appcompat.widget.SearchView
        fetchUserItems();

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

    private void fetchUserItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(MyActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getEmail();  // Prefer using UID instead of email
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myItemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LostItem lostItem = snapshot.getValue(LostItem.class);
                        if (lostItem != null && lostItem.getItemName() != null) {
                            myItemList.add(lostItem);
                            Log.d("MyActivity", "My Item: " + lostItem.getItemName());
                        } else {
                            Log.d("MyActivity", "LostItem is null or has null itemName");
                        }
                    }
                    filteredList.clear();
                    filteredList.addAll(myItemList);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("MyActivity", "No items found for this user");
                    Toast.makeText(MyActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(MyActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItems(String query) {
        filteredList.clear();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(myItemList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredList.addAll(myItemList.stream()
                    .filter(item -> item.getItemName() != null && item.getItemName().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }
}
