package com.buysmart.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.activities.AlertsActivity;
import com.buysmart.ai.activities.CouponsActivity;
import com.buysmart.ai.activities.EditProfileActivity;
import com.buysmart.ai.activities.LegalActivity;
import com.buysmart.ai.activities.LoginActivity;
import com.buysmart.ai.activities.NearbyStoresActivity;
import com.buysmart.ai.activities.OrdersActivity;
import com.buysmart.ai.activities.SavedProductsActivity;
import com.buysmart.ai.activities.SettingsActivity;
import com.buysmart.ai.databinding.FragmentProfileBinding;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        firebaseManager = FirebaseManager.getInstance();

        loadUserProfile();
        setupClickListeners();
    }

    private void loadUserProfile() {
        if (binding == null) return;

        User user = sessionManager.getUser();
        if (user != null) {
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getEmail());
            
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(binding.ivProfileImage);
            }
            updateStats(user);
        }

        String userId = firebaseManager.getCurrentUserId();
        if (userId != null) {
            firebaseManager.getUser(userId, task -> {
                if (binding == null) return;
                if (task.isSuccessful() && task.getResult() != null) {
                    User freshUser = task.getResult().toObject(User.class);
                    if (freshUser != null) {
                        sessionManager.updateUser(freshUser);
                        updateStats(freshUser);
                    }
                }
            });

            firebaseManager.getUserAlerts(userId, task -> {
                if (binding != null && task.isSuccessful()) {
                    binding.tvAlertsCount.setText(String.valueOf(task.getResult().size()));
                }
            });

            firebaseManager.getUserOrders(userId, task -> {
                if (binding != null && task.isSuccessful()) {
                    binding.tvOrdersCount.setText(String.valueOf(task.getResult().size()));
                }
            });
        }
    }

    private void updateStats(User user) {
        if (binding == null) return;
        int savedCount = user.getSavedProducts() != null ? user.getSavedProducts().size() : 0;
        binding.tvSavedCount.setText(String.valueOf(savedCount));
    }

    private void setupClickListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        binding.menuMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), OrdersActivity.class));
        });

        binding.menuSavedProducts.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SavedProductsActivity.class));
        });

        binding.menuMyAlerts.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AlertsActivity.class));
        });

        binding.menuCoupons.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CouponsActivity.class));
        });

        binding.menuNearbyStores.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NearbyStoresActivity.class));
        });

        binding.menuSettings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });

        binding.menuHelp.setOnClickListener(v -> openLegalContent(LegalActivity.TYPE_HELP));
        binding.menuPrivacy.setOnClickListener(v -> openLegalContent(LegalActivity.TYPE_PRIVACY));
        binding.menuTerms.setOnClickListener(v -> openLegalContent(LegalActivity.TYPE_TERMS));

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void openLegalContent(int type) {
        Intent intent = new Intent(requireContext(), LegalActivity.class);
        intent.putExtra(LegalActivity.EXTRA_TYPE, type);
        startActivity(intent);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        firebaseManager.signOut();
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
