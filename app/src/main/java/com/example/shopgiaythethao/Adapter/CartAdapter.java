package com.example.shopgiaythethao.Adapter;

import android.content.Context;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopgiaythethao.Domain.ItemsModel;
import com.example.shopgiaythethao.Helper.ChangeNumberItemsListener;
import com.example.shopgiaythethao.Helper.ManagmentCart;
import com.example.shopgiaythethao.databinding.ViewholderCartBinding;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {

    private ArrayList<ItemsModel> listItemSelected;
    private ArrayList<ItemsModel> originalList;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

    // Constructor khởi tạo adapter với danh sách sản phẩm, context và listener
    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = new ArrayList<>(listItemSelected);
        this.originalList = new ArrayList<>(listItemSelected);
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo viewholder từ layout viewholder_cart.xml bằng ViewBinding
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        // Format tiền tệ theo locale hiện tại
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

        // Gán dữ liệu cho các view trong viewholder
        holder.binding.titleTxt.setText(listItemSelected.get(position).getTitle());
        holder.binding.feeEachItem.setText(nf.format(listItemSelected.get(position).getPrice()) + "₫");
        holder.binding.totalEachItem.setText(nf.format(Math.round((listItemSelected.get(position).getNumberinCart() * listItemSelected.get(position).getPrice()))) + "₫");
        holder.binding.numberItemTxt.setText(String.valueOf(listItemSelected.get(position).getNumberinCart()));

        // Hiển thị hình ảnh sản phẩm bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicUrl().get(0))
                .into(holder.binding.pic);

        // Xử lý khi nhấn nút tăng số lượng
        holder.binding.plsuCartBtn.setOnClickListener(v -> managmentCart.plusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed(); // Gọi callback sau khi thay đổi
        }));

        // Xử lý khi nhấn nút giảm số lượng
        holder.binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed(); // Gọi callback sau khi thay đổi
        }));
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng mục trong giỏ hàng
        return listItemSelected.size();
    }

    /**
     * Lọc các sản phẩm trong giỏ hàng dựa trên từ khóa tìm kiếm, có hỗ trợ tiếng Việt có dấu và không dấu
     * @param query Từ khóa tìm kiếm
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();

        // Nếu từ khóa trống, khôi phục lại danh sách ban đầu
        if (query.isEmpty()) {
            listItemSelected = new ArrayList<>(originalList);
            notifyDataSetChanged();
            return;
        }

        // Chuẩn hóa từ khóa (loại bỏ dấu tiếng Việt)
        String normalizedQuery = normalizeVietnamese(query);

        // Tạo danh sách chứa kết quả lọc
        ArrayList<ItemsModel> filteredList = new ArrayList<>();

        // Duyệt qua danh sách ban đầu để lọc các sản phẩm phù hợp
        for (ItemsModel item : originalList) {
            String normalizedTitle = normalizeVietnamese(item.getTitle().toLowerCase());

            // So sánh cả tên gốc và tên đã chuẩn hóa
            if (item.getTitle().toLowerCase().contains(query) ||
                    normalizedTitle.contains(normalizedQuery)) {
                filteredList.add(item);
            }
        }

        // Cập nhật lại danh sách hiển thị
        listItemSelected = filteredList;
        notifyDataSetChanged();
    }

    /**
     * Cập nhật lại adapter với danh sách sản phẩm mới trong giỏ hàng
     * @param cartItems Danh sách sản phẩm hiện tại
     */
    public void updateCartItems(ArrayList<ItemsModel> cartItems) {
        this.originalList = new ArrayList<>(cartItems);
        this.listItemSelected = new ArrayList<>(cartItems);
        notifyDataSetChanged();
    }

    /**
     * Chuẩn hóa văn bản tiếng Việt bằng cách loại bỏ dấu (thanh, mũ, móc, ...)
     * @param text Văn bản cần chuẩn hóa
     * @return Chuỗi đã được loại bỏ dấu
     */
    private String normalizeVietnamese(String text) {
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    // Lớp ViewHolder đại diện cho mỗi item trong RecyclerView
    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
