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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

public class AddLostItemActivity extends AppCompatActivity {
    private static final String TAG = "AddLostItemActivity";
    private EditText itemNameEditText, dateEditText, timeEditText, placeEditText,descEditText,contactEditText;
    private Button addButton, selectImageButton;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private FirebaseAuth firebaseAuth;

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
        descEditText = findViewById(R.id.desc);
        contactEditText = findViewById(R.id.contact);
        addButton = findViewById(R.id.add_button);
        imageView = findViewById(R.id.image_view);


        // Update the database URL here
        databaseReference = FirebaseDatabase.getInstance("https://kuch-mil-gaya-c28fd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("lostItems");
        storageReference = FirebaseStorage.getInstance().getReference("lostItems");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    addButton.setEnabled(false);
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
            imageView.setBackground(null);
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
                            addLostItem(imageUrl, id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get download URL", e);
                            Toast.makeText(AddLostItemActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            // Enable the button if getting the URL fails
                            addButton.setEnabled(true);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Image upload failed", e);
                    Toast.makeText(AddLostItemActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Enable the button if the upload fails
                    addButton.setEnabled(true);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to compress image", e);
            Toast.makeText(this, "Failed to compress image", Toast.LENGTH_SHORT).show();
            // Enable the button if compression fails
            addButton.setEnabled(true);
        }
    }

    private void addLostItem(String imageUrl, String id) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userReg = "";
        String userId = "";

        if (firebaseUser != null) {
            userId = firebaseUser.getEmail();
            String name = firebaseUser.getDisplayName();
            String[] words = name.split(" ");
            userReg = words[words.length - 1];
        }
        String itemName = itemNameEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String place = placeEditText.getText().toString().trim();
        String desc = descEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();

        ZonedDateTime istDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dateReg = istDateTime.format(dateFormatter);
        String timeReg = istDateTime.format(timeFormatter);
        Boolean claimed = false;

        if (!itemName.isEmpty() && !date.isEmpty() && !time.isEmpty() && !place.isEmpty() && !desc.isEmpty() && !contact.isEmpty() && !userId.isEmpty() && !userReg.isEmpty() && !dateReg.isEmpty() && !timeReg.isEmpty()) {
            LostItem lostItem = new LostItem(id, itemName, date, time, place, desc, contact, imageUrl, userId, userReg, dateReg, timeReg, claimed);
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
                    // Always re-enable the button after the entire process
                    addButton.setEnabled(true);
                }
            });
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            // Enable the button if input validation fails
            addButton.setEnabled(true);
        }
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date="";
                    if((month+1)>=10){
                    date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    }
                    else{
                        date = dayOfMonth + "/0" + (month + 1) + "/" + year;
                    }
                    dateEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay < 12 ? "AM" : "PM";
                    int hour = hourOfDay % 12;
                    if (hour == 0) hour = 12;
                    String time = String.format("%02d:%02d %s", hour, minute, amPm);
                    timeEditText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false  // 12-hour format
        );
        timePickerDialog.show();
    }
}
