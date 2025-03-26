package com.example.shopgiaythethao.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopgiaythethao.Domain.BannerModel;
import com.example.shopgiaythethao.Domain.CategoryModel;
import com.example.shopgiaythethao.Domain.ItemsModel;
import com.example.shopgiaythethao.Domain.UserModel;
import com.example.shopgiaythethao.Repository.MainRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRepository repository = new MainRepository();
    private final MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();

    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return repository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner(){
        return repository.loadBanner();
    }

    public LiveData<ArrayList<ItemsModel>> loadPopular(){
        return repository.loadPopular();
    }

    // Phương thức lấy thông tin người dùng hiện tại
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
                    userLiveData.setValue(userModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return userLiveData;
    }
}
