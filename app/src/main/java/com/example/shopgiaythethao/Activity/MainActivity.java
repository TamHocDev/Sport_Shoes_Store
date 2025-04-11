package com.example.shopgiaythethao.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.shopgiaythethao.Fragment.CartFragment;
import com.example.shopgiaythethao.Fragment.HomeFragment;
import com.example.shopgiaythethao.Fragment.ProfileFragment;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            binding.bottomNavigation.setItemSelected(R.id.home, true);
            setFragment(new HomeFragment());
        }

        setupBottomNavigation();

    }

    public ActivityMainBinding getBinding() {
        return binding;
    }
    public void selectTab(int tabId) {
        binding.bottomNavigation.setItemSelected(tabId, true);
    }


    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                setFragment(new HomeFragment());
            } else if (id == R.id.profile) {
                setFragment(new ProfileFragment());
            }else if (id == R.id.cart) {
                setFragment(new CartFragment());
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}