package com.example.shopgiaythethao.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopgiaythethao.Domain.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";
    private MutableLiveData<UserModel> userData = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private DatabaseReference usersRef;

    public ProfileViewModel() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        userData.setValue(userModel);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Error loading user data: " + error.getMessage());
                    errorMessage.setValue("Không thể tải thông tin người dùng: " + error.getMessage());
                }
            });
        }
    }

    public LiveData<UserModel> getCurrentUser() {
        return userData;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Cập nhật thông tin người dùng
     * @param name Tên mới của người dùng
     * @param email Email mới của người dùng
     * @param phoneNumber Số điện thoại mới
     * @param newPassword Mật khẩu mới (null hoặc rỗng nếu không thay đổi)
     */
    public void updateUserProfile(String name, String email, String phoneNumber, String newPassword) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            errorMessage.setValue("Người dùng chưa đăng nhập");
            return;
        }

        String userId = currentUser.getUid();

        // Kiểm tra email hiện tại và cập nhật nếu thay đổi
        if (!email.equals(currentUser.getEmail())) {
            currentUser.updateEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Email updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update email", e);
                        errorMessage.setValue("Không thể cập nhật email: " + e.getMessage());
                    });
        }

        // Cập nhật mật khẩu nếu có
        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Password updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update password", e);
                        errorMessage.setValue("Không thể cập nhật mật khẩu: " + e.getMessage());
                    });
        }

        // Tạo đối tượng UserModel mới với thông tin cập nhật
        UserModel updatedUser = new UserModel();
        updatedUser.setUserId(userId);
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhoneNumber(phoneNumber);

        // Giữ lại thời gian tạo tài khoản nếu có
        UserModel currentUserData = userData.getValue();
        if (currentUserData != null) {
            updatedUser.setCreatedAt(currentUserData.getCreatedAt());
        } else {
            updatedUser.setCreatedAt(System.currentTimeMillis());
        }

        // Cập nhật thông tin lên Realtime Database
        usersRef.child(userId)
                .setValue(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    updateSuccess.setValue(true);
                    Log.d(TAG, "User profile updated successfully");
                })
                .addOnFailureListener(e -> {
                    updateSuccess.setValue(false);
                    errorMessage.setValue("Không thể cập nhật thông tin: " + e.getMessage());
                    Log.e(TAG, "Failed to update user profile", e);
                });
    }
}