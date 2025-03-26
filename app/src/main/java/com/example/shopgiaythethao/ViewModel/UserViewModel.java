package com.example.shopgiaythethao.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.shopgiaythethao.Repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<FirebaseUser> userLiveData;
    private LiveData<Boolean> loggedOutLiveData;
    private LiveData<String> errorMessageLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        userLiveData = userRepository.getUserLiveData();
        loggedOutLiveData = userRepository.getLoggedOutLiveData();
        errorMessageLiveData = userRepository.getErrorMessageLiveData();
    }

    public void register(String email, String password, String name, String phoneNumber) {
        userRepository.register(email, password, name, phoneNumber);
    }

    public void login(String email, String password) {
        userRepository.login(email, password);
    }

    public void logOut() {
        userRepository.logOut();
    }

    public void setUserData(FirebaseUser user) {
        userRepository.setUserData(user);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }
}
