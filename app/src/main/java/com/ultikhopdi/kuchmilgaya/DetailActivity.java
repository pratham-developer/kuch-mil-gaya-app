package com.ultikhopdi.kuchmilgaya;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DetailActivity extends AppCompatActivity {
    private Button btn_qr;
    private Button btn_del;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView itemName = findViewById(R.id.item_name);
        TextView itemDate = findViewById(R.id.item_date);
        TextView itemTime = findViewById(R.id.item_time);
        TextView itemPlace = findViewById(R.id.item_place);
        TextView itemDesc = findViewById(R.id.item_desc);
        TextView itemContact = findViewById(R.id.item_contact);
        ImageView itemImage = findViewById(R.id.item_image);
        btn_qr = findViewById(R.id.btn_show_qr);
        btn_del = findViewById(R.id.btn_delete);

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

        String uid2 = intent.getStringExtra("item_uid");
        Boolean viewall = intent.getBooleanExtra("viewall", true);
        if (viewall) {
            btn_del.setVisibility(View.GONE);
            btn_qr.setText("Claim");
            btn_qr.setOnClickListener(v -> initiateQRScan());
        } else {
            btn_del.setVisibility(View.VISIBLE);
            btn_qr.setText("Generate QR");
            btn_qr.setOnClickListener(v -> {
                Intent qrIntent = new Intent(DetailActivity.this, QrActivity.class);
                qrIntent.putExtra("item_uid", uid2);
                qrIntent.putExtra("source", "my_activity");
                startActivity(qrIntent);
            });
        }
        btn_del.setOnClickListener(v -> delete_item(uid2));
    }
    private void delete_item(String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("lostItems")
                .child(uid);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DetailActivity.this, "Item deleted successfully!", Toast.LENGTH_LONG).show();
                // Clear screen stack and move back to profile activity
                Intent intent = new Intent(DetailActivity.this, ProfileActivity.class); // Replace ProfileActivity with your actual activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(DetailActivity.this, "Failed to delete item. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }



    private void initiateQRScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startQRScanner();
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0); // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String scannedData = result.getContents();
                String itemUid = getIntent().getStringExtra("item_uid");
                if (scannedData.equals(itemUid)) {
                    // The scanned QR code matches the item UID
                    markItemAsClaimed(itemUid);
                } else {
                    Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void markItemAsClaimed(String itemUid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("lostItems")
                .child(itemUid);

        databaseReference.child("claimed").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DetailActivity.this, "Item claimed successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DetailActivity.this, "Failed to claim item. Please try again.", Toast.LENGTH_LONG).show();
            }
            //clear screen stack and move back to profile activity
            Intent intent = new Intent(DetailActivity.this, ProfileActivity.class); // Replace PreviousActivity with your actual activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
