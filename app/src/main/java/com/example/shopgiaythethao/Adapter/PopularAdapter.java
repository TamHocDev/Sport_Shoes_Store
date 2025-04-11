package com.example.shopgiaythethao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import java.text.Normalizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopgiaythethao.Activity.DetailActivity;
import com.example.shopgiaythethao.Domain.ItemsModel;
import com.example.shopgiaythethao.databinding.ViewholderPopularBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {

    private ArrayList<ItemsModel> items; // Danh sách các sản phẩm hiện tại được hiển thị
    private ArrayList<ItemsModel> originalItems; // Danh sách gốc để reset khi cần
    private Context context;

    // Constructor nhận vào danh sách sản phẩm và sao chép danh sách gốc
    public PopularAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
        this.originalItems = new ArrayList<>(items); // Tạo bản sao của danh sách ban đầu
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Sử dụng View Binding để gắn layout viewholder
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {
        // Định dạng tiền tệ theo ngôn ngữ máy
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

        // Thiết lập các thông tin hiển thị cho sản phẩm
        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.priceTxt.setText(nf.format(items.get(position).getPrice()) + "₫");
        holder.binding.ratingTxt.setText(" (" + items.get(position).getReview() + ")");
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent() + " OFF");
        holder.binding.oldPriceTxt.setText(nf.format(items.get(position).getOldPrice()) + "₫");
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Thiết lập hình ảnh sản phẩm với Glide và hiệu ứng CenterInside
        RequestOptions options = new RequestOptions();
        options = options.transform(new CenterInside());

        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0)) // Lấy ảnh đầu tiên trong danh sách ảnh
                .apply(options)
                .into(holder.binding.pic);

        // Xử lý khi người dùng click vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position)); // Truyền dữ liệu sản phẩm sang trang chi tiết
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // Trả về số lượng sản phẩm
    }

    // Cập nhật dữ liệu trong adapter bằng danh sách mới
    public void updateData(List<ItemsModel> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged(); // Thông báo dữ liệu đã thay đổi
    }

    /**
     * Cập nhật danh sách hiển thị mới
     * @param newList Danh sách mới cần hiển thị
     */
    public void updateList(ArrayList<ItemsModel> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    /**
     * Reset danh sách về ban đầu (trước khi lọc)
     */
    public void resetList() {
        this.items = new ArrayList<>(originalItems);
        notifyDataSetChanged();
    }

    /**
     * Lọc danh sách sản phẩm theo từ khóa tìm kiếm (hỗ trợ tiếng Việt)
     * @param query từ khóa tìm kiếm
     */
    public void filter(String query) {
        query = query.toLowerCase().trim(); // Đưa về chữ thường và loại bỏ khoảng trắng

        // Nếu không nhập gì thì reset danh sách
        if (query.isEmpty()) {
            resetList();
            return;
        }

        // Chuẩn hóa chuỗi tìm kiếm (loại bỏ dấu tiếng Việt)
        String normalizedQuery = normalizeVietnamese(query);

        // Tạo danh sách lọc mới
        ArrayList<ItemsModel> filteredList = new ArrayList<>();

        // Duyệt từng sản phẩm để kiểm tra điều kiện
        for (ItemsModel item : originalItems) {
            String normalizedTitle = normalizeVietnamese(item.getTitle().toLowerCase());

            // Nếu tiêu đề chứa query (cả bản gốc và chuẩn hóa)
            if (item.getTitle().toLowerCase().contains(query) ||
                    normalizedTitle.contains(normalizedQuery)) {
                filteredList.add(item);
            }
        }

        // Cập nhật danh sách sau khi lọc
        updateList(filteredList);
    }

    /**
     * Chuẩn hóa chuỗi tiếng Việt bằng cách loại bỏ dấu
     * @param text văn bản cần chuẩn hóa
     * @return văn bản không có dấu
     */
    private String normalizeVietnamese(String text) {
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    // Lớp Viewholder chứa các view cho mỗi item trong RecyclerView
    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
