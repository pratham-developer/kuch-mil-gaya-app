package com.ultikhopdi.kuchmilgaya;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Perform the back press actions and apply animations
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
                // Apply reverse animation
            }
        });
        recyclerViewMyItems = findViewById(R.id.recycler_view_my_items);
        recyclerViewMyItems.setLayoutManager(new LinearLayoutManager(this));
        myItemList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Pass context and list to adapter
        boolean isMyClaims = getIntent().getBooleanExtra("isMyClaims", false);  // Retrieve the flag
        adapter = new LostItemAdapter(filteredList, this,isMyClaims);
        recyclerViewMyItems.setAdapter(adapter);
        searchView = findViewById(R.id.search_view);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gold));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gold));



        // Get the search icon drawable
        int searchIconId = androidx.appcompat.R.id.search_mag_icon;
        ImageView searchIcon = searchView.findViewById(searchIconId);

        // Apply color filter to the search icon
        searchIcon.setColorFilter(getResources().getColor(R.color.gold), PorterDuff.Mode.SRC_IN);
        // Make sure your layout uses androidx.appcompat.widget.SearchView
        String source = getIntent().getStringExtra("source");
        fetchUserItems(source);
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

    private void fetchUserItems(String source) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(MyActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId;
        if(source.equals("userId")){
            userId = user.getEmail();
        }
        else{
            userId = user.getDisplayName();
        }
         // Prefer using UID instead of email
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        databaseReference.orderByChild(source).equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
