package com.buysmart.ai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityEditProfileBinding;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(binding.ivProfileImage);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        firebaseManager = FirebaseManager.getInstance();

        setupToolbar();
        loadUserData();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserData() {
        User user = sessionManager.getUser();
        if (user != null) {
            binding.etName.setText(user.getName());
            binding.etEmail.setText(user.getEmail());
            binding.etPhone.setText(user.getPhone());

            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(binding.ivProfileImage);
            }
        }
    }

    private void setupClickListeners() {
        binding.ivProfileImage.setOnClickListener(v -> openImagePicker());
        
        binding.btnChangePhoto.setOnClickListener(v -> openImagePicker());

        binding.btnSave.setOnClickListener(v -> saveProfile());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError(getString(R.string.error_required_field));
            binding.etName.requestFocus();
            return;
        }

        showLoading(true);

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            showLoading(false);
            return;
        }

        // Upload image if selected
        if (selectedImageUri != null) {
            firebaseManager.uploadProfileImage(userId, selectedImageUri, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult().toString();
                    updateProfile(userId, name, phone, imageUrl);
                } else {
                    updateProfile(userId, name, phone, null);
                }
            });
        } else {
            updateProfile(userId, name, phone, null);
        }
    }

    private void updateProfile(String userId, String name, String phone, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        if (imageUrl != null) {
            updates.put("profileImage", imageUrl);
        }

        firebaseManager.updateUser(userId, updates, task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                // Update session
                User user = sessionManager.getUser();
                if (user != null) {
                    user.setName(name);
                    user.setPhone(phone);
                    if (imageUrl != null) {
                        user.setProfileImage(imageUrl);
                    }
                    sessionManager.updateUser(user);
                }
                
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
