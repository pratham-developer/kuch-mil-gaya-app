package com.ultikhopdi.kuchmilgaya;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ActivityInfo;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.OnBackPressedCallback;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DetailActivity extends AppCompatActivity {
    private Button btn_qr;
    private Button btn_del;
    private TextView txt_claimed_by;
    private TextView txt_clm;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final String FIREBASE_DB_URL = "https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Perform the back press actions and apply animations
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
                  // Apply reverse animation
            }
        });

        TextView itemName = findViewById(R.id.item_name);
        TextView itemDate = findViewById(R.id.item_date);
        TextView itemTime = findViewById(R.id.item_time);
        TextView itemPlace = findViewById(R.id.item_place);
        TextView itemDesc = findViewById(R.id.item_desc);
        TextView itemContact = findViewById(R.id.item_contact);
        ImageView itemImage = findViewById(R.id.item_image);
        btn_qr = findViewById(R.id.btn_show_qr);
        btn_del = findViewById(R.id.btn_delete);
        txt_claimed_by = findViewById(R.id.item_claimed_by);
        txt_clm = findViewById(R.id.clm);

        Intent intent = getIntent();
        String itemUid = intent.getStringExtra("item_uid");
        String uploadedBy = intent.getStringExtra("uploadedBy");
        Boolean viewAll = intent.getBooleanExtra("viewall", true);
        boolean isMyClaims = intent.getBooleanExtra("isMyClaims", false);

        itemName.setText(intent.getStringExtra("item_name"));
        itemDate.setText(intent.getStringExtra("item_date"));
        itemTime.setText(intent.getStringExtra("item_time"));
        itemPlace.setText(intent.getStringExtra("item_place"));
        itemDesc.setText(intent.getStringExtra("item_desc"));
        itemContact.setText(intent.getStringExtra("item_contact"));

        String imageUrl = intent.getStringExtra("item_image_url");
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this).load(imageUrl).into(itemImage);
        }

        if (viewAll) {
            
            txt_claimed_by.setText(uploadedBy);
            txt_clm.setText("Uploaded By");
            btn_qr.setText("Claim");
            btn_qr.setOnClickListener(v -> initiateQRScan());
        } else {
            checkIfItemClaimed(itemUid, isMyClaims, uploadedBy);
        }

        btn_del.setOnClickListener(v -> deleteItem(itemUid));
    }

    private void checkIfItemClaimed(String uid, boolean isMyClaims, String uploadedBy) {
        // Set default UI state before loading data to avoid visual delay
        btn_qr.setVisibility(View.GONE);
        txt_clm.setText("Claimed By");
        txt_claimed_by.setText("");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("lostItems")
                .child(uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean claimed = dataSnapshot.child("claimed").getValue(Boolean.class);
                String claimedBy = dataSnapshot.child("claimedBy").getValue(String.class);

                // Run UI updates on the main thread for responsiveness
                runOnUiThread(() -> {
                    if (Boolean.TRUE.equals(claimed)) {
                        btn_qr.setVisibility(View.GONE);
                        btn_del.setVisibility(View.GONE);
                        if (isMyClaims) {
                            txt_clm.setText("Uploaded By");
                            txt_claimed_by.setText(uploadedBy);
                        } else {
                            txt_clm.setText("Claimed By");
                            txt_claimed_by.setText(claimedBy);
                        }
                    } else {
                        btn_qr.setVisibility(View.VISIBLE);
                        btn_del.setVisibility(View.VISIBLE);
                        txt_clm.setText("Claimed By");
                        txt_claimed_by.setText("");
                        btn_qr.setText("Generate QR");
                        btn_qr.setOnClickListener(v -> {
                            Intent qrIntent = new Intent(DetailActivity.this, QrActivity.class);
                            qrIntent.putExtra("item_uid", uid);
                            qrIntent.putExtra("source", "my_activity");
                            startActivity(qrIntent);
                            overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to check item status. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteItem(String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DB_URL)
                .getReference("lostItems")
                .child(uid);

        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DetailActivity.this, "Item deleted successfully!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DetailActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
            } else {
                Toast.makeText(DetailActivity.this, "Failed to delete item. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initiateQRScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setMessage("Camera access is required to scan QR codes.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION))
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            startQRScanner();
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
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
                if (!TextUtils.isEmpty(scannedData) && scannedData.equals(itemUid)) {
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String name = firebaseUser.getDisplayName();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DB_URL)
                    .getReference("lostItems")
                    .child(itemUid);

            databaseReference.child("claimed").setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Item claimed successfully!", Toast.LENGTH_LONG).show();
                    databaseReference.child("claimedBy").setValue(name);
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to claim item. Please try again.", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(DetailActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
            });
        } else {
            Toast.makeText(DetailActivity.this, "User not authenticated!", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
