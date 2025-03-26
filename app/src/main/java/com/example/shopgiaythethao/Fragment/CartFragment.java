package com.example.shopgiaythethao.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shopgiaythethao.Activity.MainActivity;
import com.example.shopgiaythethao.Adapter.CartAdapter;
import com.example.shopgiaythethao.Helper.ManagmentCart;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.databinding.FragmentCartBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private ManagmentCart managmentCart;
    private CartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view binding
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize cart management
        managmentCart = new ManagmentCart(requireContext());

        // Initialize UI
        updateCartUI();
        setVariable();
        setupSearchFunctionality();

        // Set up back button click listener
        binding.btnBack.setOnClickListener(v -> {
            navigateToHome();
        });
    }

    private void setupSearchFunctionality() {
        binding.edtSearching.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter cart items when text changes
                if (cartAdapter != null && !managmentCart.getListCart().isEmpty()) {
                    cartAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Handle search action (when user presses enter/search on keyboard)
        binding.edtSearching.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Apply filter
                if (cartAdapter != null && !managmentCart.getListCart().isEmpty()) {
                    cartAdapter.filter(binding.edtSearching.getText().toString());
                }
                return true;
            }
            return false;
        });
    }

    private void updateCartUI() {
        if (managmentCart.getListCart().isEmpty()) {
            // Show empty cart message
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.emptyTxt.setText("Giỏ hàng của bạn đang trống");

            // Center the empty message in the screen
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = android.view.Gravity.CENTER;
            binding.emptyTxt.setLayoutParams(params);

            // Hide the cart content
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            // Hide empty cart message
            binding.emptyTxt.setVisibility(View.GONE);

            // Show the cart content
            binding.scrollViewCart.setVisibility(View.VISIBLE);

            // Setup recycler view
            setupCartRecyclerView();

            // Calculate and display cart totals
            calculatorCart();
        }
    }

    private void setupCartRecyclerView() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(managmentCart.getListCart(), requireContext(), () -> {
            // This is the callback for when cart items change
            calculatorCart();

            // Check if cart is now empty after item removal
            if (managmentCart.getListCart().isEmpty()) {
                updateCartUI();
            } else {
                // Update adapter with latest cart items
                cartAdapter.updateCartItems(managmentCart.getListCart());
            }
        });
        binding.cartView.setAdapter(cartAdapter);
    }

    private void setVariable() {
        binding.btnOrder.setOnClickListener(v -> {
            if (!managmentCart.getListCart().isEmpty()) {
                // Clear cart after checkout
                clearCart();

                // Show success message
                Toast.makeText(requireContext(), "Đặt hàng thành công! Cảm ơn bạn đã mua sắm.", Toast.LENGTH_SHORT).show();

                // Navigate to home screen
                navigateToHome();
            } else {
                Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            // Select the home tab in bottom navigation
            mainActivity.selectTab(R.id.home);
        }
    }

    private void clearCart() {
        // Get current cart items
        if (managmentCart != null) {
            // Clear all items one by one
            while (!managmentCart.getListCart().isEmpty()) {
                managmentCart.minusItem(managmentCart.getListCart(), 0, () -> {
                    // Do nothing in this callback
                });
            }
        }
    }

    private void calculatorCart() {
        double delivery = 40000;
        double itemTotal = managmentCart.getTotalFee();
        double total = itemTotal + delivery;

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        binding.totalFeeTxt.setText(nf.format(itemTotal) + "₫");
        binding.deliveryTxt.setText(nf.format(delivery) + "₫");
        binding.totalTxt.setText(nf.format(total) + "₫");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}