package com.buysmart.ai.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityForgotPasswordBinding;
import com.buysmart.ai.utils.FirebaseManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();

        setupListeners();
    }

    private void setupListeners() {
        binding.btnSendResetLink.setOnClickListener(v -> attemptSendResetLink());
        
        binding.tvBackToLogin.setOnClickListener(v -> finish());
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void attemptSendResetLink() {
        String email = binding.etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etEmail.setError(getString(R.string.error_required_field));
            binding.etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError(getString(R.string.error_invalid_email));
            binding.etEmail.requestFocus();
            return;
        }

        showLoading(true);

        firebaseManager.sendPasswordResetEmail(email, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to send reset link. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSendResetLink.setEnabled(!show);
    }
}
