package com.example.shopgiaythethao.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.example.shopgiaythethao.Activity.MainActivity;
import com.example.shopgiaythethao.Activity.SearchResultsActivity;
import com.example.shopgiaythethao.Adapter.CategoryAdapter;
import com.example.shopgiaythethao.Adapter.PopularAdapter;
import com.example.shopgiaythethao.Adapter.SliderAdapter;
import com.example.shopgiaythethao.Domain.BannerModel;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.ViewModel.MainViewModel;
import com.example.shopgiaythethao.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainViewModel viewModel;
    private PopularAdapter popularAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new MainViewModel();

        // Khởi tạo các thành phần giao diện người dùng
        loadUserInfo();
        initCategory();
        initSlider();
        initPopular();
        setupClickListeners();
        setupSearchFunctionality();
    }

    private void loadUserInfo() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), userModel -> {
            if (userModel != null) {
                binding.tvNameUser.setText(userModel.getName());
            }
        });
    }

    private void setupClickListeners() {

        // Xử lý khi bấm vào nút thông báo
        binding.btnNotification.setOnClickListener(v -> {
        });

        // Xử lý khi bấm vào nút cài đặt
        binding.btnSetting.setOnClickListener(v -> {
        });

        // Xử lý khi bấm vào avatar người dùng để chuyển đến trang cá nhân
        binding.imUser.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).getBinding().bottomNavigation.setItemSelected(R.id.profile, true);
            }
        });
    }

    private void setupSearchFunctionality() {
        // Xử lý sự kiện khi người dùng nhấn nút tìm kiếm trên bàn phím
        binding.edtSearching.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Lấy nội dung tìm kiếm từ EditText
                String query = binding.edtSearching.getText().toString().trim();

                if (!query.isEmpty()) {
                    // Ẩn bàn phím
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Log giá trị tìm kiếm để kiểm tra
                    Log.d("HomeFragment", "Search query: " + query);

                    // Chuyển sang màn hình kết quả tìm kiếm và truyền query qua Intent
                    Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });
    }

    private void initPopular() {
        // Hiển thị progress bar trong khi chờ dữ liệu
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        // Quan sát dữ liệu sản phẩm phổ biến
        viewModel.loadPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (!itemsModels.isEmpty()) {
                // Hiển thị danh sách theo chiều ngang
                binding.popularView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                popularAdapter = new PopularAdapter(itemsModels);
                binding.popularView.setAdapter(popularAdapter);
                binding.popularView.setNestedScrollingEnabled(true); // Cho phép scroll lồng nhau
            }
            binding.progressBarPopular.setVisibility(View.GONE); // Ẩn progress bar sau khi tải xong
        });
    }

    private void initSlider() {
        // Hiển thị progress bar khi tải banner
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        // Quan sát dữ liệu banner
        viewModel.loadBanner().observe(getViewLifecycleOwner(), bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                setupBannerSlider(bannerModels); // Thiết lập slider
                binding.progressBarSlider.setVisibility(View.GONE); // Ẩn progress bar sau khi xong
            }
        });
    }

    private void setupBannerSlider(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels, binding.viewPagerSlider));

        // Thiết lập hiệu ứng trình chiếu
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // Tạo hiệu ứng chuyển trang có khoảng cách giữa các banner
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    private void initCategory() {
        // Hiển thị progress bar khi tải danh mục
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        // Quan sát dữ liệu danh mục
        viewModel.loadCategory().observe(getViewLifecycleOwner(), categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE); // Ẩn progress bar sau khi xong
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng bộ nhớ để tránh rò rỉ
    }
}
