package com.ultikhopdi.kuchmilgaya;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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


public class ProfileActivity extends AppCompatActivity {
    // Initialize variable
    TextView tvName;
    ImageView accountBut;
    ImageView notifBut;
    Button fndBut;
    Button repBut;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Assign variable
        tvName = findViewById(R.id.iv_name);
        accountBut = findViewById(R.id.iv_profile);
        notifBut = findViewById(R.id.iv_notification);
        fndBut = findViewById(R.id.btn_find_lost);
        repBut = findViewById(R.id.btn_report_found);
        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Check condition
        if (firebaseUser != null) {
            // When firebase user is not equal to null set image on image view
            // set name on text view
            String name = firebaseUser.getDisplayName();
            String[] words = name.split(" ");
            tvName.setText(words[0] + "!");
        }


        accountBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        fndBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ViewLostItemsActivity.class);
                startActivity(intent);
            }
        });
        repBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AddLostItemActivity.class);
                startActivity(intent);
            }
        });

    }
}
