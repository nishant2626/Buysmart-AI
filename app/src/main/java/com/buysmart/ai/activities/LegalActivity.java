package com.buysmart.ai.activities;

import android.os.Bundle;
import android.text.Html;
import androidx.appcompat.app.AppCompatActivity;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ActivityLegalBinding;

public class LegalActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_PRIVACY = 1;
    public static final int TYPE_TERMS = 2;
    public static final int TYPE_ABOUT = 3;
    public static final int TYPE_HELP = 4;

    private ActivityLegalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLegalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_ABOUT);
        setupContent(type);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupContent(int type) {
        switch (type) {
            case TYPE_PRIVACY:
                binding.toolbar.setTitle("Privacy Policy 2026");
                binding.tvContent.setText(Html.fromHtml(getString(R.string.privacy_policy_content), Html.FROM_HTML_MODE_COMPACT));
                break;
            case TYPE_TERMS:
                binding.toolbar.setTitle("Terms & Conditions");
                binding.tvContent.setText(Html.fromHtml(getString(R.string.terms_content), Html.FROM_HTML_MODE_COMPACT));
                break;
            case TYPE_HELP:
                binding.toolbar.setTitle("Help & Support");
                binding.tvContent.setText(Html.fromHtml(getString(R.string.help_content), Html.FROM_HTML_MODE_COMPACT));
                break;
            default:
                binding.toolbar.setTitle("About BuySmart AI");
                binding.tvContent.setText(Html.fromHtml(getString(R.string.about_content), Html.FROM_HTML_MODE_COMPACT));
                break;
        }
    }
}
