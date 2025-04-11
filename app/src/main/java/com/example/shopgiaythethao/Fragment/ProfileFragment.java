package com.example.shopgiaythethao.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.shopgiaythethao.Activity.LoginActivity;
import com.example.shopgiaythethao.Domain.UserModel;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.ViewModel.ProfileViewModel;
import com.example.shopgiaythethao.databinding.DialogEditProfileBinding;
import com.example.shopgiaythethao.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    // Khai báo view binding
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo ViewModel
        viewModel = new ProfileViewModel();

        // Tải thông tin người dùng
        loadUserInfo();

        // Cài đặt các sự kiện click
        setupClickListeners();
    }

    private void loadUserInfo() {
        // Lấy dữ liệu người dùng
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                binding.userName.setText(userModel.getName());
                binding.userEmail.setText(userModel.getEmail());
            }
        });

        // Lắng nghe trạng thái cập nhật thành công
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            }
        });

        // Lắng nghe thông báo lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListeners() {
        // Sự kiện click nút Chỉnh sửa hồ sơ
        binding.btnEditProfile.setOnClickListener(v -> {
            showEditProfileDialog();
        });

        // Sự kiện click vào mục Lịch sử đơn hàng
        binding.orderHistoryLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã nhấn vào Lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click vào mục Yêu thích
        binding.wishlistLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã nhấn vào Danh sách yêu thích", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click vào mục Địa chỉ giao hàng
        binding.addressesLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã nhấn vào Địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click vào mục Thông báo
        binding.notificationsLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã nhấn vào Thông báo", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click vào mục Hỗ trợ
        binding.supportLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã nhấn vào Hỗ trợ", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện click nút Đăng xuất
        binding.btnLogOut.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogEditProfileBinding dialogBinding = DialogEditProfileBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Lấy thông tin người dùng hiện tại
        UserModel currentUser = viewModel.getCurrentUser().getValue();
        if (currentUser != null) {
            // Điền thông tin hiện tại vào các trường nhập
            dialogBinding.nameEditText.setText(currentUser.getName());
            dialogBinding.emailEditText.setText(currentUser.getEmail());
            dialogBinding.phoneEditText.setText(currentUser.getPhoneNumber());
            // Không hiển thị mật khẩu vì lý do bảo mật
        }

        // Sự kiện click nút Hủy
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Sự kiện click nút Lưu
        dialogBinding.btnSave.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String name = dialogBinding.nameEditText.getText().toString().trim();
            String email = dialogBinding.emailEditText.getText().toString().trim();
            String phone = dialogBinding.phoneEditText.getText().toString().trim();
            String password = dialogBinding.passwordEditText.getText().toString().trim();

            // Kiểm tra dữ liệu nhập vào
            if (name.isEmpty()) {
                dialogBinding.nameInputLayout.setError("Vui lòng nhập họ tên");
                return;
            }

            if (email.isEmpty()) {
                dialogBinding.emailInputLayout.setError("Vui lòng nhập email");
                return;
            }

            // Cập nhật hồ sơ người dùng
            viewModel.updateUserProfile(name, email, phone, password.isEmpty() ? null : password);

            // Đóng dialog
            dialog.dismiss();

            // Hiển thị hộp thoại loading
            showLoadingDialog();
        });

        dialog.show();
    }

    private AlertDialog loadingDialog;

    private void showLoadingDialog() {
        // Hiển thị dialog loading khi đang cập nhật thông tin
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(R.layout.dialog_loading);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();

        // Tắt dialog khi cập nhật xong (dù thành công hay thất bại)
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        // Hiển thị hộp thoại xác nhận đăng xuất
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            // Đăng xuất khỏi Firebase
            mAuth.signOut();

            // Chuyển về màn hình đăng nhập và xóa stack hoạt động
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
        binding = null; // Tránh rò rỉ bộ nhớ
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}