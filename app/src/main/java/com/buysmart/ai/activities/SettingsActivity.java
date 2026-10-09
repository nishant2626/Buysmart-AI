package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.buysmart.ai.BuySmartApplication;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivitySettingsBinding;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.SessionManager;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SessionManager sessionManager;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        cartManager = new CartManager(this);

        setupToolbar();
        loadSettings();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSettings() {
        binding.switchNotifications.setChecked(sessionManager.isNotificationsEnabled());
        binding.switchDarkMode.setChecked(sessionManager.isDarkMode());
        
        String language = BuySmartApplication.getInstance().getCurrentLanguage();
        binding.tvLanguageValue.setText(language.equals("hi") ? R.string.hindi : R.string.english);
    }

    private void setupClickListeners() {
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationsEnabled(isChecked);
        });

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.layoutLanguage.setOnClickListener(v -> showLanguageDialog());

        binding.layoutClearCache.setOnClickListener(v -> clearCache());

        // New: Delete all data listener
        binding.layoutDeleteData.setOnClickListener(v -> showDeleteDataConfirmation());

        binding.layoutAbout.setOnClickListener(v -> {
            openLegalContent(LegalActivity.TYPE_ABOUT);
        });

        binding.layoutPrivacy.setOnClickListener(v -> {
            openLegalContent(LegalActivity.TYPE_PRIVACY);
        });

        binding.layoutTerms.setOnClickListener(v -> {
            openLegalContent(LegalActivity.TYPE_TERMS);
        });
    }

    private void showDeleteDataConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All App Data?")
                .setMessage("This will permanently delete your session, cart, and all local settings. You will be logged out. This cannot be undone.")
                .setPositiveButton("Delete Everything", (dialog, which) -> deleteAllData())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllData() {
        // 1. Clear SharedPreferences (Session, preferences)
        sessionManager.clearSession();
        
        // 2. Clear Cart
        cartManager.clearCart();
        
        // 3. Clear Cache
        BuySmartApplication.getInstance().clearCache();

        Toast.makeText(this, "All app data deleted successfully.", Toast.LENGTH_LONG).show();

        // 4. Redirect to Login Screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openLegalContent(int type) {
        Intent intent = new Intent(this, LegalActivity.class);
        intent.putExtra(LegalActivity.EXTRA_TYPE, type);
        startActivity(intent);
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Hindi"};
        String[] languageCodes = {"en", "hi"};
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.language)
                .setItems(languages, (dialog, which) -> {
                    String selectedLanguage = languageCodes[which];
                    BuySmartApplication.getInstance().setLanguage(selectedLanguage);
                    binding.tvLanguageValue.setText(languages[which]);
                    
                    finish();
                    startActivity(getIntent());
                })
                .show();
    }

    private void clearCache() {
        BuySmartApplication.getInstance().clearCache();
        Toast.makeText(this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
