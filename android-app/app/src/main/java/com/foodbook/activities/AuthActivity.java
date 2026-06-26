package com.foodbook.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.foodbook.databinding.ActivityAuthBinding;
import com.foodbook.utils.SessionManager;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (SessionManager.getInstance(this).isLoggedIn()) {
            navigateToMain();
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.authContainer.getId(), new LoginFragment())
                    .commit();
        }
    }

    public void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
