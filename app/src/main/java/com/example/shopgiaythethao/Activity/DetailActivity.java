package com.example.shopgiaythethao.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.shopgiaythethao.Adapter.PicListAdapter;
import com.example.shopgiaythethao.Adapter.SizeAdapter;
import com.example.shopgiaythethao.Domain.ItemsModel;
import com.example.shopgiaythethao.Helper.ManagmentCart;
import com.example.shopgiaythethao.R;
import com.example.shopgiaythethao.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        getBundles();
        initPicList();
        initSize();
    }

    private void initSize() {
        binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initPicList() {
        ArrayList<String> picList = new ArrayList<>(object.getPicUrl());

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.pic);

        binding.picList.setAdapter(new PicListAdapter(picList, binding.pic));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getBundles() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedPrice = nf.format(object.getPrice());
        String formattedOldPrice = nf.format(object.getOldPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText(formattedPrice + "₫");
        binding.oldPriceTxt.setText(formattedOldPrice + "₫");
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.descriptionTxt.setText(object.getDescription());

        binding.addToCartBtn.setOnClickListener(v -> {

                object.setNumberinCart(numberOrder);
                managmentCart.insertItem(object);
        });
        binding.imgBack.setOnClickListener(v -> finish());
    }
}