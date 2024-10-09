package com.example.exe201.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.exe201.DTO.FoodItemReport;
import com.example.exe201.R;

import java.util.List;

public class TopFoodItemAdapter extends RecyclerView.Adapter<TopFoodItemAdapter.MyViewHolder> {

    private List<FoodItemReport> productList;
    private Context context;

    public TopFoodItemAdapter(List<FoodItemReport> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public void updateFoodItemList(List<FoodItemReport> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_food_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FoodItemReport product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvQuantitySold.setText("Đã bán: " + product.getQuantitySold());
        // Load image using your preferred image loader library like Glide or Picasso
        // Example: Glide.with(context).load(product.getImageUrl()).into(holder.imgProduct);
        // Thiết lập Glide để bo tròn 4 góc
        Glide.with(holder.imgProduct.getContext())
                .load(product.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading) // Ảnh mặc định khi đang tải
                        .error(R.drawable.defaultavatar) // Ảnh mặc định khi URL rỗng hoặc lỗi
                        .centerCrop() // Cắt ảnh cho vừa vặn với ImageView hình vuông
                        .transform(new RoundedCorners(30))) // Bo tròn 4 góc
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvQuantitySold;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
        }
    }
}
