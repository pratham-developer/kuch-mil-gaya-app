package com.ultikhopdi.kuchmilgaya;

import android.content.Intent;

import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class QrActivity extends AppCompatActivity {
    private ImageView qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("item_uid");
        qr = findViewById(R.id.qrIm);

        if (uid != null && !uid.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                BitMatrix bitMatrix = barcodeEncoder.encode(uid, BarcodeFormat.QR_CODE, 700, 700);
                Bitmap bitmap = Bitmap.createBitmap(700, 700, Bitmap.Config.RGB_565);
                for (int x = 0; x < 700; x++) {
                    for (int y = 0; y <700; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }
                qr.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }
}
