package com.ultikhopdi.kuchmilgaya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrActivity extends AppCompatActivity {

    private ImageView qr;
    private DatabaseReference databaseReference;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Perform the back press actions and apply animations
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
                // Apply reverse animation
            }
        });
        qr = findViewById(R.id.qrIm);
        Intent intent = getIntent();
        itemId = intent.getStringExtra("item_uid");

        if (itemId != null && !itemId.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(itemId, BarcodeFormat.QR_CODE, 800, 800);
                qr.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Item ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("lostItems").child(itemId);

        // Add a listener to check if the item has been claimed
        databaseReference.child("claimed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean claimed = snapshot.getValue(Boolean.class);
                if (claimed != null && claimed) {
                    Toast.makeText(QrActivity.this, "Item has been claimed", Toast.LENGTH_SHORT).show();
                    finishAllActivitiesAndGoToProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Toast.makeText(QrActivity.this, "Failed to check item status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finishAllActivitiesAndGoToProfile() {
        Intent intent = new Intent(QrActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
        finishAffinity(); // Finish all activities in the task stack
        overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
    }
}
