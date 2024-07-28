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

public class AccountActivity extends AppCompatActivity {
    // Initialize variables
    private TextView uName;
    private TextView email;
    private Button btActivity;
    private Button btAbtus;
    private Button btTnc;
    private Button btLogout;
    private ImageView btn_back;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Assign variables
        uName = findViewById(R.id.userName);
        email = findViewById(R.id.mailId);
        btActivity = findViewById(R.id.btn_ur_activity);
        btAbtus = findViewById(R.id.btn_abtus);
        btTnc = findViewById(R.id.btn_tnc);
        btLogout = findViewById(R.id.btn_signout);
        btn_back = findViewById(R.id.back);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Firebase User
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Check if the user is logged in
        if (firebaseUser != null) {
            // Set user's name
            String name = firebaseUser.getDisplayName();
            if (name == null || name.isEmpty()) {
                uName.setText("Unknown");
            } else {
                int lastInd = name.lastIndexOf(' ');
                if (lastInd == -1) {
                    uName.setText(name); // Fallback to full name if no space found
                } else {
                    uName.setText(name.substring(0, lastInd));
                }
            }

            // Set user's email
            String mail = firebaseUser.getEmail();
            if (mail != null && !mail.isEmpty()) {
                email.setText(mail);
            }
        }

        // Initialize Google Sign-In Client
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        // Logout button click listener
        btLogout.setOnClickListener(view -> googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign out from Firebase
                firebaseAuth.signOut();
                // Display Toast
                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                // Redirect to MainActivity
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // Finish activity
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Logout unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
