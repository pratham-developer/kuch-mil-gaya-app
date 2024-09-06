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
import android.net.Uri;

public class DeveloperTeam extends AppCompatActivity {
    // Initialize variable

    Button dev1,dev2,dev3;
    Button inst1,inst2,inst3;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_team);
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
        dev1 = findViewById(R.id.linkedin_1);
        dev2 = findViewById(R.id.linkedin_2);
        dev3 = findViewById(R.id.linkedin_3);

        inst1 = findViewById(R.id.insta_1);
        inst2 = findViewById(R.id.insta_2);
        inst3 = findViewById(R.id.insta_3);
        btn_back=findViewById(R.id.back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dev1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.linkedin.com/in/pratham-khanduja/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

        inst1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.instagram.com/say.pratham/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

        dev2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.linkedin.com/in/keshav-dadhich-2652611a9/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

        inst2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.instagram.com/keshavdb/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

        dev3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.linkedin.com/in/rudra-gupta-36827828b/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

        inst3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinUrl = "https://www.instagram.com/rudra.gupta_12/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(intent);
                overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
            }
        });

    }
}
