package com.example.shopgiaythethao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.Domain.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private ImageView ivBack;
    private TextInputLayout tilName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnSignup;
    private View loadingOverlay;
    private TextView tvLoginPrompt;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Khởi tạo Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ánh xạ các view từ layout
        ivBack = findViewById(R.id.iv_back);
        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnSignup = findViewById(R.id.btn_signup);
        tvLoginPrompt = findViewById(R.id.tv_login_prompt);
        loadingOverlay = findViewById(R.id.loading_overlay);

        // Xử lý sự kiện khi nhấn nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn dòng "Đã có tài khoản?"
        tvLoginPrompt.setOnClickListener(v -> {
            // Chuyển sang màn hình đăng nhập
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Xử lý sự kiện khi nhấn nút Đăng ký
        btnSignup.setOnClickListener(v -> signUp());
    }

    // Phương thức xử lý đăng ký tài khoản
    private void signUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Họ và tên không được để trống");
            return;
        } else {
            tilName.setError(null); // Xóa thông báo lỗi nếu có
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email không được để trống");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return;
        } else {
            tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Số điện thoại không được để trống");
            return;
        } else {
            tilPhone.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Mật khẩu không được để trống");
            return;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        } else {
            tilPassword.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
            return;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            return;
        } else {
            tilConfirmPassword.setError(null);
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản và Điều kiện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị overlay loading
        showLoading();
        btnSignup.setEnabled(false);

        // Tạo người dùng với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng ký thành công
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                            // Lưu thông tin người dùng vào Realtime Database
                            saveUserInfo(user, name, email, phone);

                            updateUI(user);
                        } else {
                            // Đăng ký thất bại
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // Ẩn overlay loading
                        hideLoading();
                    }
                });
    }

    // Phương thức lưu thông tin người dùng vào Realtime Database
    private void saveUserInfo(FirebaseUser user, String name, String email, String phone) {
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            // Tạo đối tượng người dùng
            UserModel newUser = new UserModel(userId, name, email, phone);

            userRef.setValue(newUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Lưu thông tin người dùng thành công");
                            } else {
                                Log.w(TAG, "Lưu thông tin người dùng thất bại", task.getException());
                                Toast.makeText(SignUpActivity.this, "Lỗi khi lưu thông tin người dùng.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Cập nhật giao diện sau khi đăng ký thành công
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Chuyển đến màn hình chính
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    // Hiển thị lớp phủ loading với hiệu ứng mờ dần
    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.setAlpha(0f);
        loadingOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
    }

    // Ẩn lớp phủ loading với hiệu ứng mờ dần
    private void hideLoading() {
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        loadingOverlay.setVisibility(View.GONE);
                        btnSignup.setEnabled(true);
                    }
                })
                .start();
    }

    // Gọi khi kết thúc Activity, kèm hiệu ứng chuyển cảnh
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
