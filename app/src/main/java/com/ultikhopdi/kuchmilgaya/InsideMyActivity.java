package com.ultikhopdi.kuchmilgaya;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;


public class InsideMyActivity extends AppCompatActivity {
    // Initialize variable

    Button claimBut;
    Button repBut;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_my);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Perform the back press actions and apply animations
                finish();
                overridePendingTransition(R.anim.flip_in_reverse, R.anim.flip_out_reverse);
                // Apply reverse animation
            }
        });

        // Assign variable
        claimBut = findViewById(R.id.btn_myclaims);
        repBut = findViewById(R.id.btn_myrep);
        btn_back = findViewById(R.id.back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        claimBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsideMyActivity.this, MyActivity.class);
                intent.putExtra("source", "claimedBy");
                intent.putExtra("isMyClaims", true);  // This indicates it's "My Claims"
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });
        repBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsideMyActivity.this, MyActivity.class);
                intent.putExtra("source", "userId");
                intent.putExtra("isMyClaims", false);  // This indicates it's "My Reports"
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

    }
}
