package com.example.shopgiaythethao.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopgiaythethao.Domain.ItemsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private MutableLiveData<List<ItemsModel>> searchResults = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> resultCount = new MutableLiveData<>();
    private DatabaseReference databaseRef;
    private List<ItemsModel> allItems = new ArrayList<>();
    private List<ItemsModel> filteredItems = new ArrayList<>();

    public enum SortType {
        POPULAR, NEWEST, PRICE_ASC, PRICE_DESC, DISCOUNT
    }

    public SearchViewModel() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Items");
        isLoading.setValue(false);
        resultCount.setValue(0);
    }

    public LiveData<List<ItemsModel>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getResultCount() {
        return resultCount;
    }

    public void fetchAllItems() {
        // Nếu đang tìm kiếm, không tải lại tất cả sản phẩm
        if (isLoading.getValue() != null && isLoading.getValue()) {
            return;
        }

        isLoading.setValue(true);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allItems.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemsModel item = snapshot.getValue(ItemsModel.class);
                    if (item != null) {
                        allItems.add(item);
                    }
                }

                filteredItems = new ArrayList<>(allItems);
                searchResults.setValue(filteredItems);
                resultCount.setValue(filteredItems.size());
                isLoading.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi khi tải dữ liệu: " + databaseError.getMessage());
            }
        });
    }

    public void searchItems(String query) {
        isLoading.setValue(true);

        if (query == null || query.trim().isEmpty()) {
            // Nếu query trống, trả về tất cả sản phẩm
            fetchAllItems();
            return;
        }

        // Chuyển query về chữ thường để tìm kiếm không phân biệt hoa thường
        String lowercaseQuery = query.toLowerCase().trim();
        Log.d("SearchViewModel", "Searching for: " + lowercaseQuery);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemsModel> results = new ArrayList<>();
                boolean foundAny = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemsModel item = snapshot.getValue(ItemsModel.class);

                    if (item != null) {
                        String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
                        String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";

                        // Kiểm tra nếu title hoặc description chứa query
                        if (title.contains(lowercaseQuery) || description.contains(lowercaseQuery)) {
                            results.add(item);
                            foundAny = true;
                            Log.d("SearchViewModel", "Found matching item: " + item.getTitle());
                        }
                    }
                }

                Log.d("SearchViewModel", "Search results count: " + results.size());
                filteredItems = results;
                searchResults.setValue(results);
                resultCount.setValue(results.size());
                isLoading.setValue(false);

                if (!foundAny) {
                    errorMessage.setValue("Không tìm thấy sản phẩm nào phù hợp với '" + query + "'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi khi tìm kiếm: " + databaseError.getMessage());
            }
        });
    }

    public void searchByCategory(String category) {
        isLoading.setValue(true);

        databaseRef.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        filteredItems.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ItemsModel item = snapshot.getValue(ItemsModel.class);
                            if (item != null) {
                                filteredItems.add(item);
                            }
                        }

                        searchResults.setValue(filteredItems);
                        resultCount.setValue(filteredItems.size());
                        isLoading.setValue(false);

                        if (filteredItems.isEmpty()) {
                            errorMessage.setValue("Không tìm thấy sản phẩm nào trong danh mục này");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Lỗi khi tìm kiếm: " + databaseError.getMessage());
                    }
                });
    }

    public void filterByPriceRange(double minPrice, double maxPrice) {
        if (allItems.isEmpty()) return;

        filteredItems = new ArrayList<>();

        for (ItemsModel item : allItems) {
            if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                filteredItems.add(item);
            }
        }

        searchResults.setValue(filteredItems);
        resultCount.setValue(filteredItems.size());
    }

    public void sortItems(SortType sortType) {
        if (filteredItems.isEmpty()) return;

        List<ItemsModel> sortedItems = new ArrayList<>(filteredItems);

        switch (sortType) {
            case POPULAR:
                // Sắp xếp theo đánh giá cao nhất
                Collections.sort(sortedItems, (item1, item2) ->
                        Double.compare(item2.getRating(), item1.getRating()));
                break;

            case NEWEST:
                break;

            case PRICE_ASC:
                // Sắp xếp theo giá tăng dần
                Collections.sort(sortedItems, Comparator.comparingDouble(ItemsModel::getPrice));
                break;

            case PRICE_DESC:
                // Sắp xếp theo giá giảm dần
                Collections.sort(sortedItems, (item1, item2) ->
                        Double.compare(item2.getPrice(), item1.getPrice()));
                break;

            case DISCOUNT:
                // Sắp xếp theo phần trăm giảm giá
                Collections.sort(sortedItems, (item1, item2) -> {
                    // Nếu không có offPercent thì xem như 0%
                    String off1 = item1.getOffPercent() != null ? item1.getOffPercent() : "0%";
                    String off2 = item2.getOffPercent() != null ? item2.getOffPercent() : "0%";

                    // Trích xuất số phần trăm từ chuỗi
                    int percent1 = parseDiscountPercent(off1);
                    int percent2 = parseDiscountPercent(off2);

                    return Integer.compare(percent2, percent1);
                });
                break;
        }

        searchResults.setValue(sortedItems);
    }

    private int parseDiscountPercent(String percentStr) {
        try {
            return Integer.parseInt(percentStr.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void filterByDiscount() {
        if (allItems.isEmpty()) return;

        filteredItems = new ArrayList<>();

        for (ItemsModel item : allItems) {
            if (item.getOffPercent() != null && !item.getOffPercent().isEmpty() &&
                    !item.getOffPercent().equals("0%")) {
                filteredItems.add(item);
            }
        }

        searchResults.setValue(filteredItems);
        resultCount.setValue(filteredItems.size());
    }
}