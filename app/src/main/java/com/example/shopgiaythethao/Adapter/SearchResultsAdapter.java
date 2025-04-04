package com.example.shopgiaythethao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopgiaythethao.Activity.DetailActivity;
import com.example.shopgiaythethao.Domain.ItemsModel;
import com.example.shopgiaythethao.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<ItemsModel> items;
    private DecimalFormat formatter;
    private Context context;

    public SearchResultsAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        formatter = new DecimalFormat("###,###,###đ");
    }

    public void setItems(List<ItemsModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search_result, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel currentItem = items.get(position);

        holder.titleTxt.setText(currentItem.getTitle());
        holder.priceTxt.setText(formatter.format(currentItem.getPrice()));

        // Hiển thị giá cũ nếu có
        if (currentItem.getOldPrice() > 0) {
            holder.oldPriceTxt.setVisibility(View.VISIBLE);
            holder.oldPriceTxt.setText(formatter.format(currentItem.getOldPrice()));
            holder.oldPriceTxt.setPaintFlags(holder.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.oldPriceTxt.setVisibility(View.GONE);
        }

        // Hiển thị phần trăm giảm giá nếu có
        if (currentItem.getOffPercent() != null && !currentItem.getOffPercent().isEmpty()) {
            holder.offPercentTxt.setVisibility(View.VISIBLE);
            holder.offPercentTxt.setText(currentItem.getOffPercent());
        } else {
            holder.offPercentTxt.setVisibility(View.GONE);
        }

        // Hiển thị đánh giá
        holder.ratingTxt.setText(String.valueOf(currentItem.getRating()));

        // Hiển thị hình ảnh sản phẩm
        if (currentItem.getPicUrl() != null && !currentItem.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentItem.getPicUrl().get(0))
                    .into(holder.pic);
        }

        // Xử lý sự kiện khi nhấp vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", currentItem);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, oldPriceTxt, offPercentTxt, ratingTxt;
        ShapeableImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            oldPriceTxt = itemView.findViewById(R.id.oldPriceTxt);
            offPercentTxt = itemView.findViewById(R.id.offPercentTxt);
            ratingTxt = itemView.findViewById(R.id.ratingTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}