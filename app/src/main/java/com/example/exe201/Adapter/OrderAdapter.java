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
import com.example.exe201.DTO.FoodOrder;
import com.example.exe201.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<FoodOrder> orderList;

    public OrderAdapter(Context context, List<FoodOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FoodOrder order = orderList.get(position);

        holder.orderTime.setText(order.getFoodImage());
        holder.restaurantName.setText(order.getRestaurantName());
        holder.price.setText("$" + order.getTotalPrice());
        holder.quantity.setText("Quantity: " + order.getTotalItems());
        holder.orderStatus.setText("Status: " + order.getOrderStatus());

        // Load image using Glide
        Glide.with(context).load(order.getFoodImage()).into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        public ImageView foodImage;
        public TextView orderTime, restaurantName, price, quantity, orderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.ivFoodImage);
            orderTime = itemView.findViewById(R.id.tvOrderTime);
            restaurantName = itemView.findViewById(R.id.tvRestaurantName);
            price = itemView.findViewById(R.id.tvTotalPrice);
            quantity = itemView.findViewById(R.id.tvTotalItems);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
