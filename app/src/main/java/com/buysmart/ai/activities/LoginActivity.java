package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityLoginBinding;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private ActivityLoginBinding binding;
    private FirebaseManager firebaseManager;
    private SessionManager sessionManager;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startMainActivity();
            return;
        }

        // Configure Google Sign In
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, gso);
        } catch (Exception e) {
            Log.e(TAG, "Google Sign-In configuration failed", e);
            binding.btnGoogleSignIn.setEnabled(false);
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        binding.tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.error_required_field));
            return;
        }

        showLoading(true);

        firebaseManager.signInWithEmail(email, password, task -> {
            if (task.isSuccessful()) {
                handleLoginSuccess(task.getResult());
            } else {
                showLoading(false);
                String errorMessage = task.getException() != null ? 
                        task.getException().getMessage() : "Login Failed";
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Auth Login failed", task.getException());
            }
        });
    }

    private void signInWithGoogle() {
        if (googleSignInClient != null) {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showLoading(true);
        firebaseManager.signInWithGoogle(idToken, task -> {
            if (task.isSuccessful()) {
                handleLoginSuccess(task.getResult());
            } else {
                showLoading(false);
                String msg = task.getException() != null ? task.getException().getMessage() : "Auth Failed";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginSuccess(AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        if (firebaseUser == null) {
            showLoading(false);
            return;
        }

        // Diagnostic: Check if profile exists in Firestore
        firebaseManager.getUser(firebaseUser.getUid(), task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    // Profile exists
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        sessionManager.createSession(user);
                        showLoading(false);
                        startMainActivity();
                    }
                } else {
                    // Profile missing - Create it
                    createNewProfile(firebaseUser);
                }
            } else {
                showLoading(false);
                String error = task.getException() != null ? task.getException().getMessage() : "Permission Denied";
                Toast.makeText(this, "Firestore Permission Denied: " + error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Firestore Profile Read failed", task.getException());
            }
        });
    }

    private void createNewProfile(FirebaseUser firebaseUser) {
        User newUser = new User(firebaseUser.getUid(), 
                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Smart Shopper", 
                firebaseUser.getEmail());
        
        firebaseManager.createUser(newUser, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                sessionManager.createSession(newUser);
                startMainActivity();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Creation Failed";
                Toast.makeText(this, "Profile Creation Blocked: " + error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Firestore Profile Write failed", task.getException());
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);
        binding.btnGoogleSignIn.setEnabled(!show);
    }
}
