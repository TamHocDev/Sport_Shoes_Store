package com.example.shopgiaythethao.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopgiaythethao.Adapter.SearchResultsAdapter;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.ViewModel.SearchViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private SearchViewModel viewModel;
    private SearchResultsAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private ImageView backBtn, filterBtn;
    private ProgressBar loadingIndicator;
    private LinearLayout emptyStateLayout;
    private TextView resultCountTxt, sortByTxt;
    private ChipGroup filterChipGroup;
    private Chip chipPopular, chipNewest, chipPriceAsc, chipPriceDesc, chipDiscount;
    private ConstraintLayout resultInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        initView();
        initViewModel();
        setupListeners();

        // Kiểm tra nếu có query được truyền từ intent
        String searchQuery = getIntent().getStringExtra("searchQuery");
        Log.d("SearchResultsActivity", "Received search query: " + searchQuery);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchEditText.setText(searchQuery);
            performSearch(searchQuery);
        } else {
            // Nếu không có query, tải tất cả sản phẩm
            viewModel.fetchAllItems();
        }
    }

    private void initView() {
        // Ánh xạ các thành phần chính
        recyclerView = findViewById(R.id.searchRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        backBtn = findViewById(R.id.backBtn);
        filterBtn = findViewById(R.id.filterBtn);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Ánh xạ các thành phần trong phần thông tin kết quả
        resultInfoLayout = findViewById(R.id.resultInfoLayout);
        resultCountTxt = findViewById(R.id.resultCountTxt);
        sortByTxt = findViewById(R.id.sortByTxt);

        // Ánh xạ các chip lọc
        filterChipGroup = findViewById(R.id.filterChipGroup);
        chipPopular = findViewById(R.id.chipPopular);
        chipNewest = findViewById(R.id.chipNewest);
        chipPriceAsc = findViewById(R.id.chipPriceAsc);
        chipPriceDesc = findViewById(R.id.chipPriceDesc);
        chipDiscount = findViewById(R.id.chipDiscount);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SearchResultsAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Quan sát kết quả tìm kiếm
        viewModel.getSearchResults().observe(this, items -> {
            adapter.setItems(items);

            // Hiển thị emptyState nếu không có kết quả
            if (items == null || items.isEmpty()) {
                emptyStateLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyStateLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Quan sát số lượng kết quả
        viewModel.getResultCount().observe(this, count -> {
            resultCountTxt.setText(count + " kết quả");
        });

        // Quan sát trạng thái loading
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                loadingIndicator.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.GONE);
            } else {
                loadingIndicator.setVisibility(View.GONE);
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Xử lý nút quay lại
        backBtn.setOnClickListener(v -> onBackPressed());

        // Xử lý tìm kiếm khi nhấn Enter trên bàn phím
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        // Xử lý các chip lọc
        chipPopular.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.sortItems(SearchViewModel.SortType.POPULAR);
                uncheckOtherChips(chipPopular.getId());
            }
        });

        chipNewest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.sortItems(SearchViewModel.SortType.NEWEST);
                uncheckOtherChips(chipNewest.getId());
            }
        });

        chipPriceAsc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.sortItems(SearchViewModel.SortType.PRICE_ASC);
                uncheckOtherChips(chipPriceAsc.getId());
            }
        });

        chipPriceDesc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.sortItems(SearchViewModel.SortType.PRICE_DESC);
                uncheckOtherChips(chipPriceDesc.getId());
            }
        });

        chipDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.filterByDiscount();
                uncheckOtherChips(chipDiscount.getId());
            } else {
                // Khi bỏ chọn, tải lại tất cả sản phẩm
                viewModel.fetchAllItems();
            }
        });

        // Xử lý nút sắp xếp
        sortByTxt.setOnClickListener(v -> {
            showSortOptionsDialog();
        });

        // Xử lý nút lọc
        filterBtn.setOnClickListener(v -> {
            showFilterDialog();
        });
    }

    private void uncheckOtherChips(int selectedChipId) {
        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) filterChipGroup.getChildAt(i);
            if (chip.getId() != selectedChipId) {
                chip.setChecked(false);
            }
        }
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        // Đặt lại các chip lọc
        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            ((Chip) filterChipGroup.getChildAt(i)).setChecked(false);
        }

        // Hiển thị loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        // Thực hiện tìm kiếm
        viewModel.searchItems(query);
    }

    private void showSortOptionsDialog() {
        String[] sortOptions = new String[]{
                "Phổ biến",
                "Mới nhất",
                "Giá thấp đến cao",
                "Giá cao đến thấp",
                "Khuyến mãi"
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp theo")
                .setItems(sortOptions, (dialog, which) -> {
                    // Đặt lại các chip
                    for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
                        ((Chip) filterChipGroup.getChildAt(i)).setChecked(false);
                    }

                    // Áp dụng sắp xếp dựa trên lựa chọn
                    switch (which) {
                        case 0:
                            chipPopular.setChecked(true);
                            break;
                        case 1:
                            chipNewest.setChecked(true);
                            break;
                        case 2:
                            chipPriceAsc.setChecked(true);
                            break;
                        case 3:
                            chipPriceDesc.setChecked(true);
                            break;
                        case 4:
                            chipDiscount.setChecked(true);
                            break;
                    }
                });
        builder.create().show();
    }

    private void showFilterDialog() {
        View filterView = getLayoutInflater().inflate(R.layout.dialog_filter, null);

        // Tìm các thành phần trong dialog
        com.google.android.material.slider.RangeSlider priceRangeSlider = filterView.findViewById(R.id.priceRangeSlider);
        TextView minPriceTxt = filterView.findViewById(R.id.minPriceTxt);
        TextView maxPriceTxt = filterView.findViewById(R.id.maxPriceTxt);
        TextView applyBtn = filterView.findViewById(R.id.applyFilterBtn);
        TextView resetBtn = filterView.findViewById(R.id.resetFilterBtn);

        // Thiết lập giá trị ban đầu
        priceRangeSlider.setValues(0f, 50f);
        minPriceTxt.setText("0đ");
        maxPriceTxt.setText("5.000.000đ");

        // Cập nhật text khi di chuyển slider
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            float minPrice = values.get(0);
            float maxPrice = values.get(1);

            // Cập nhật text hiển thị
            minPriceTxt.setText(formatPrice(minPrice * 100000));
            maxPriceTxt.setText(formatPrice(maxPrice * 100000));
        });

        // Tạo dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(filterView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Xử lý nút áp dụng
        applyBtn.setOnClickListener(v -> {
            List<Float> values = priceRangeSlider.getValues();
            float minPrice = values.get(0) * 100000; // Chuyển đổi từ giá trị slider sang giá tiền
            float maxPrice = values.get(1) * 100000;

            viewModel.filterByPriceRange(minPrice, maxPrice);
            dialog.dismiss();
        });

        // Xử lý nút đặt lại
        resetBtn.setOnClickListener(v -> {
            viewModel.fetchAllItems();
            dialog.dismiss();
        });

        dialog.show();
    }

    private String formatPrice(float price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        return formatter.format(price);
    }
}