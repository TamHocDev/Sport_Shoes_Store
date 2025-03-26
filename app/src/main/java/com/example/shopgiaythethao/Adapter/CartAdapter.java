package com.example.shopgiaythethao.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
    private ArrayList<ItemsModel> originalList; // Keep original list for filtering
    private ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = new ArrayList<>(listItemSelected);
        this.originalList = new ArrayList<>(listItemSelected);
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public CartAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

        holder.binding.titleTxt.setText(listItemSelected.get(position).getTitle());
        holder.binding.feeEachItem.setText(nf.format(listItemSelected.get(position).getPrice()) + "₫");
        holder.binding.totalEachItem.setText(nf.format(Math.round((listItemSelected.get(position).getNumberinCart() * listItemSelected.get(position).getPrice()))) + "₫");
        holder.binding.numberItemTxt.setText(String.valueOf(listItemSelected.get(position).getNumberinCart()));

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicUrl().get(0))
                .into(holder.binding.pic);

        holder.binding.plsuCartBtn.setOnClickListener(v -> managmentCart.plusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));

        holder.binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    /**
     * Filter the cart items based on search query with Vietnamese support
     * @param query Search query to filter by
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();

        // If query is empty, reset to original list
        if (query.isEmpty()) {
            listItemSelected = new ArrayList<>(originalList);
            notifyDataSetChanged();
            return;
        }

        // Normalize the query (remove accents)
        String normalizedQuery = normalizeVietnamese(query);

        // Create a new filtered list
        ArrayList<ItemsModel> filteredList = new ArrayList<>();

        // Add items that match the search query
        for (ItemsModel item : originalList) {
            String normalizedTitle = normalizeVietnamese(item.getTitle().toLowerCase());

            // Check both original text and normalized text
            if (item.getTitle().toLowerCase().contains(query) ||
                    normalizedTitle.contains(normalizedQuery)) {
                filteredList.add(item);
            }
        }

        // Update with filtered list
        listItemSelected = filteredList;
        notifyDataSetChanged();
    }

    /**
     * Update the adapter with the latest cart items
     * @param cartItems Current cart items
     */
    public void updateCartItems(ArrayList<ItemsModel> cartItems) {
        this.originalList = new ArrayList<>(cartItems);
        this.listItemSelected = new ArrayList<>(cartItems);
        notifyDataSetChanged();
    }

    /**
     * Normalize Vietnamese text by removing diacritical marks
     * @param text Text to normalize
     * @return Normalized text without accents
     */
    private String normalizeVietnamese(String text) {
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}