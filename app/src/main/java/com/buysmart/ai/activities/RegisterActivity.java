package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityRegisterBinding;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private FirebaseManager firebaseManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        sessionManager = new SessionManager(this);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.tvLogin.setOnClickListener(v -> finish());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError(getString(R.string.error_required_field));
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.error_invalid_email));
            return;
        }
        if (password.length() < 6) {
            binding.etPassword.setError(getString(R.string.error_invalid_password));
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        showLoading(true);

        firebaseManager.signUpWithEmail(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    createUserProfile(firebaseUser, name, phone);
                }
            } else {
                showLoading(false);
                String error = task.getException() != null ? task.getException().getMessage() : "Registration Failed";
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Auth registration failed", task.getException());
            }
        });
    }

    private void createUserProfile(FirebaseUser firebaseUser, String name, String phone) {
        User user = new User(firebaseUser.getUid(), name, firebaseUser.getEmail());
        user.setPhone(phone);

        firebaseManager.createUser(user, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                sessionManager.createSession(user);
                startMainActivity();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Permission Denied";
                Toast.makeText(this, "Profile Error: " + error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Firestore profile creation failed", task.getException());
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
        binding.btnRegister.setEnabled(!show);
    }
}
