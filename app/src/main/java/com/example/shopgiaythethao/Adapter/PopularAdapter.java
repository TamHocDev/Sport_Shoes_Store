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

    private ArrayList<ItemsModel> items;
    private ArrayList<ItemsModel> originalItems; // Keep original list for resetting
    private Context context;

    public PopularAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
        this.originalItems = new ArrayList<>(items); // Make a copy of the original list
    }

    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.priceTxt.setText(nf.format(items.get(position).getPrice()) + "₫");
        holder.binding.ratingTxt.setText(" (" + items.get(position).getReview() + ")");
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent() + " OFF");
        holder.binding.oldPriceTxt.setText(nf.format(items.get(position).getOldPrice()) + "₫");
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        RequestOptions options = new RequestOptions();
        options = options.transform(new CenterInside());

        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0))
                .apply(options)
                .into(holder.binding.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Add this method to your existing PopularAdapter class
    public void updateData(List<ItemsModel> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    /**
     * Update the adapter with a new list of items
     * @param newList New list of items to display
     */
    public void updateList(ArrayList<ItemsModel> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    /**
     * Reset the list to the original items
     */
    public void resetList() {
        this.items = new ArrayList<>(originalItems);
        notifyDataSetChanged();
    }

    /**
     * Filter the list based on search query with Vietnamese support
     * @param query Search query to filter by
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();

        // If query is empty, reset to original list
        if (query.isEmpty()) {
            resetList();
            return;
        }

        // Normalize the query (remove accents)
        String normalizedQuery = normalizeVietnamese(query);

        // Create a new filtered list
        ArrayList<ItemsModel> filteredList = new ArrayList<>();

        // Add items that match the search query
        for (ItemsModel item : originalItems) {
            String normalizedTitle = normalizeVietnamese(item.getTitle().toLowerCase());

            // Check both original text and normalized text
            if (item.getTitle().toLowerCase().contains(query) ||
                    normalizedTitle.contains(normalizedQuery)) {
                filteredList.add(item);
            }
        }

        // Update with filtered list
        updateList(filteredList);
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
        ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}