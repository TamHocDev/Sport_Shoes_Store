package com.example.shopgiaythethao.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopgiaythethao.Domain.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserModel> userData = new MutableLiveData<>();

    public LiveData<UserModel> getCurrentUser() {
        // Lấy ID người dùng hiện tại (đã đăng nhập)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Truy vấn thông tin từ Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    userData.setValue(userModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return userData;
    }
}