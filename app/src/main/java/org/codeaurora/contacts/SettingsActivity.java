package org.codeaurora.contacts;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.codeaurora.contacts.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(
                v -> {
                    onBackPressed();
                });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsPreference())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
