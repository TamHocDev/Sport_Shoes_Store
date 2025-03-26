package com.example.shopgiaythethao.Fragment;

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

import com.example.shopgiaythethao.Activity.LoginActivity;
import com.example.shopgiaythethao.ViewModel.ProfileViewModel;
import com.example.shopgiaythethao.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        viewModel = new ProfileViewModel();

        // Load user information
        loadUserInfo();

        // Setup click listeners
        setupClickListeners();
    }

    private void loadUserInfo() {
        // Load user data
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                binding.userName.setText(userModel.getName());
                if (mAuth.getCurrentUser() != null) {
                    binding.userEmail.setText(mAuth.getCurrentUser().getEmail());
                }
            }
        });
    }

    private void setupClickListeners() {
        // Settings button click
        binding.btnSetting.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
        });

        // Order History click
        binding.orderHistoryLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Order History clicked", Toast.LENGTH_SHORT).show();
        });

        // Wishlist click
        binding.wishlistLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Wishlist clicked", Toast.LENGTH_SHORT).show();
        });

        // Shipping Addresses click
        binding.addressesLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Shipping Addresses clicked", Toast.LENGTH_SHORT).show();
        });

        // Notifications click
        binding.notificationsLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

        // Support click
        binding.supportLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Support clicked", Toast.LENGTH_SHORT).show();
        });

        // Logout button click
        binding.btnLogOut.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Navigate back to login screen and clear activity stack
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}