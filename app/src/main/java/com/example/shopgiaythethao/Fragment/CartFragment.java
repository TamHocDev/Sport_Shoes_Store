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
        // Khởi tạo view binding
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo quản lý giỏ hàng
        managmentCart = new ManagmentCart(requireContext());

        // Khởi tạo giao diện người dùng
        updateCartUI();
        setVariable();
        setupSearchFunctionality();

        // Thiết lập sự kiện khi nhấn nút quay lại
        binding.btnBack.setOnClickListener(v -> {
            navigateToHome();
        });
    }

    private void setupSearchFunctionality() {
        binding.edtSearching.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc các sản phẩm trong giỏ khi văn bản thay đổi
                if (cartAdapter != null && !managmentCart.getListCart().isEmpty()) {
                    cartAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Xử lý hành động tìm kiếm (khi người dùng nhấn enter/tìm trên bàn phím)
        binding.edtSearching.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Ẩn bàn phím
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Áp dụng bộ lọc
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
            // Hiển thị thông báo giỏ hàng trống
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.emptyTxt.setText("Giỏ hàng của bạn đang trống");

            // Căn giữa thông báo giỏ hàng trống trên màn hình
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = android.view.Gravity.CENTER;
            binding.emptyTxt.setLayoutParams(params);

            // Ẩn nội dung giỏ hàng
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            // Ẩn thông báo giỏ hàng trống
            binding.emptyTxt.setVisibility(View.GONE);

            // Hiển thị nội dung giỏ hàng
            binding.scrollViewCart.setVisibility(View.VISIBLE);

            // Thiết lập RecyclerView
            setupCartRecyclerView();

            // Tính toán và hiển thị tổng tiền giỏ hàng
            calculatorCart();
        }
    }

    private void setupCartRecyclerView() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(managmentCart.getListCart(), requireContext(), () -> {
            // Đây là callback khi các mục trong giỏ thay đổi
            calculatorCart();

            // Kiểm tra xem giỏ hàng có rỗng sau khi xoá mục không
            if (managmentCart.getListCart().isEmpty()) {
                updateCartUI();
            } else {
                // Cập nhật adapter với danh sách giỏ hàng mới nhất
                cartAdapter.updateCartItems(managmentCart.getListCart());
            }
        });
        binding.cartView.setAdapter(cartAdapter);
    }

    private void setVariable() {
        binding.btnOrder.setOnClickListener(v -> {
            if (!managmentCart.getListCart().isEmpty()) {
                // Xoá giỏ hàng sau khi đặt hàng
                clearCart();

                // Hiển thị thông báo thành công
                Toast.makeText(requireContext(), "Đặt hàng thành công! Cảm ơn bạn đã mua sắm.", Toast.LENGTH_SHORT).show();

                // Quay về màn hình chính
                navigateToHome();
            } else {
                Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            // Chọn tab trang chủ trong thanh điều hướng dưới
            mainActivity.selectTab(R.id.home);
        }
    }

    private void clearCart() {
        // Lấy các mục hiện tại trong giỏ hàng
        if (managmentCart != null) {
            // Xoá từng mục trong giỏ hàng
            while (!managmentCart.getListCart().isEmpty()) {
                managmentCart.minusItem(managmentCart.getListCart(), 0, () -> {

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
        binding = null; // Tránh rò rỉ bộ nhớ
    }
}