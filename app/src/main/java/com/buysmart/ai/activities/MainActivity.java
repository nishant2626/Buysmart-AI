package com.buysmart.ai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityMainBinding;
import com.buysmart.ai.fragments.AlertsFragment;
import com.buysmart.ai.fragments.CompareFragment;
import com.buysmart.ai.fragments.HomeFragment;
import com.buysmart.ai.fragments.ProfileFragment;
import com.buysmart.ai.fragments.SearchFragment;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.SessionManager;
import com.google.android.material.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private CartManager cartManager;

    // Permission Launcher for Android 13+ Notifications
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted
                } else {
                    Toast.makeText(this, "Notifications are disabled. You won't get price alerts!", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation, (v, insets) -> {
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            binding.fragmentContainer.setPadding(0, 0, 0, v.getHeight() + bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        cartManager = new CartManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupBottomNavigation();
        updateCartBadge();
        askNotificationPermission();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), "home");
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String tag = "";
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                tag = "home";
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
                tag = "search";
            } else if (itemId == R.id.nav_compare) {
                selectedFragment = new CompareFragment();
                tag = "compare";
            } else if (itemId == R.id.nav_alerts) {
                selectedFragment = new AlertsFragment();
                tag = "alerts";
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                tag = "profile";
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, tag);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);
        
        if (current != null && current.getClass().equals(fragment.getClass()) && fragment.getArguments() == null) {
            return; 
        }

        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    public void navigateToTab(int menuId, Bundle args) {
        Fragment fragment;
        String tag;
        
        if (menuId == R.id.nav_search) { fragment = new SearchFragment(); tag = "search"; }
        else if (menuId == R.id.nav_compare) { fragment = new CompareFragment(); tag = "compare"; }
        else if (menuId == R.id.nav_alerts) { fragment = new AlertsFragment(); tag = "alerts"; }
        else {
            binding.bottomNavigation.setSelectedItemId(menuId);
            return;
        }

        if (args != null) fragment.setArguments(args);
        binding.bottomNavigation.getMenu().findItem(menuId).setChecked(true);
        loadFragment(fragment, tag);
    }

    public void navigateToTab(int menuId) {
        navigateToTab(menuId, null);
    }

    private void updateCartBadge() {
        int cartCount = cartManager.getCartCount();
        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_compare);
        if (cartCount > 0) {
            badge.setVisible(true);
            badge.setNumber(cartCount);
        } else {
            badge.setVisible(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}
