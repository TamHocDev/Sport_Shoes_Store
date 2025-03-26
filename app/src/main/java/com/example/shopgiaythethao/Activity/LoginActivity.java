package com.example.shopgiaythethao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.ViewModel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private View tvForgotPassword, tvSignupPrompt;
    private View ivGoogle, ivFacebook, ivApple;

    private FirebaseAuth mAuth;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Ánh xạ các view
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignupPrompt = findViewById(R.id.tv_signup_prompt);
        ivGoogle = findViewById(R.id.iv_google);
        ivFacebook = findViewById(R.id.iv_facebook);
        ivApple = findViewById(R.id.iv_apple);

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmailPassword();
            }
        });

        // Xử lý quên mật khẩu
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Phần xử lý quên mật khẩu sẽ được phát triển sau
                Toast.makeText(LoginActivity.this, "Chức năng quên mật khẩu đang được phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý chuyển đến màn hình đăng ký
        tvSignupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Xử lý đăng nhập với Google (sẽ phát triển sau)
        ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Đăng nhập Google sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý đăng nhập với Facebook (sẽ phát triển sau)
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Đăng nhập Facebook sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý đăng nhập với Apple (sẽ phát triển sau)
        ivApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Đăng nhập Apple sẽ được phát triển sau", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát thay đổi trạng thái đăng nhập từ ViewModel
        userViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Người dùng đã đăng nhập thành công
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                // TODO: Chuyển đến màn hình chính
                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                 startActivity(intent);
                 finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra nếu người dùng đã đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userViewModel.setUserData(currentUser);
        }
    }

    private void loginWithEmailPassword() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Xác thực dữ liệu đầu vào
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email không được để trống");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Mật khẩu không được để trống");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!isValid) {
            return;
        }

        // Hiển thị loading hoặc disable nút đăng nhập
        btnLogin.setEnabled(false);

        // Sử dụng ViewModel để đăng nhập
        userViewModel.login(email, password);

        // Quan sát lỗi đăng nhập
        userViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }


}