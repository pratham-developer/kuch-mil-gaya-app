package com.ultikhopdi.kuchmilgaya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddLostItemActivity extends AppCompatActivity {
    private static final String TAG = "AddLostItemActivity";
    private EditText itemNameEditText, dateEditText, timeEditText, placeEditText;
    private Button addButton, selectImageButton;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost_item);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        itemNameEditText = findViewById(R.id.item_name);
        dateEditText = findViewById(R.id.date);
        timeEditText = findViewById(R.id.time);
        placeEditText = findViewById(R.id.place);
        addButton = findViewById(R.id.add_button);
        selectImageButton = findViewById(R.id.select_image_button);
        imageView = findViewById(R.id.image_view);

        // Update the database URL here
        databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        storageReference = FirebaseStorage.getInstance().getReference("lostItems");

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    compressAndUploadImage();
                } else {
                    Toast.makeText(AddLostItemActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void compressAndUploadImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            final String id = databaseReference.push().getKey();
            StorageReference fileReference = storageReference.child(id + ".jpg");

            UploadTask uploadTask = fileReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Image uploaded successfully");
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Log.d(TAG, "Download URL: " + imageUrl);
                            addLostItem(imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get download URL", e);
                            Toast.makeText(AddLostItemActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Image upload failed", e);
                    Toast.makeText(AddLostItemActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to compress image", e);
            Toast.makeText(this, "Failed to compress image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addLostItem(String imageUrl) {
        String itemName = itemNameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String place = placeEditText.getText().toString().trim();

        if (!itemName.isEmpty() && !date.isEmpty() && !time.isEmpty() && !place.isEmpty()) {
            String id = databaseReference.push().getKey();
            LostItem lostItem = new LostItem(id, itemName, date, time, place, imageUrl);
            Log.d(TAG, "Adding item to database with ID: " + id);
            databaseReference.child(id).setValue(lostItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Item added successfully");
                        Toast.makeText(AddLostItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to add item", task.getException());
                        Toast.makeText(AddLostItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
