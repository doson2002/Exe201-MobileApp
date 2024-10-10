package com.example.exe201.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe201.DTO.Menu;
import com.example.exe201.OrderActivity;
import com.example.exe201.R;

import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends ListAdapter<Menu, CartAdapter.CartViewHolder> {

    private Context context;
    private List<Menu> foodItemList;
    private CartUpdateListener cartUpdateListener;
    // Thêm biến này để lưu tham chiếu đến OrderActivity
    private OrderActivity orderActivity;



    public interface CartUpdateListener {
        void onCartUpdated(); // Giao diện cho phép Activity lắng nghe thay đổi
    }

    // Sử dụng DiffUtil để so sánh các phần tử trong danh sách
    public static final DiffUtil.ItemCallback<Menu> DIFF_CALLBACK = new DiffUtil.ItemCallback<Menu>() {
        @Override
        public boolean areItemsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            // So sánh theo ID của món ăn
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            // So sánh toàn bộ nội dung của item
            return oldItem.equals(newItem);
        }
    };

    // Constructor cho ListAdapter
    public CartAdapter(Context context, List<Menu> foodItemList, OrderActivity orderActivity) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.foodItemList = foodItemList;
        this.orderActivity = orderActivity; // Lưu lại tham chiếu đến OrderActivity
    }
    // Thiết lập lắng nghe sự kiện cập nhật giỏ hàng
    public void setCartUpdateListener(CartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout của mỗi item giỏ hàng
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        try {
        Menu foodItem = getItem(position); // Lấy item từ vị trí hiện tại

        // Hiển thị tên, giá và số lượng món ăn
        holder.itemName.setText(foodItem.getName());
        holder.itemPrice.setText(String.format("%,.0fđ", foodItem.getPrice()));
        holder.itemQuantity.setText(String.valueOf(foodItem.getQuantity()));


            // Sự kiện click cho nút tăng số lượng
            holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        Menu currentItem = getItem(adapterPosition);
                        currentItem.setQuantity(currentItem.getQuantity() + 1);
                        notifyItemChanged(adapterPosition);
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onCartUpdated(); // Thông báo cho Activity biết đã cập nhật
                        }
                        updateTotalPrice();
                    }
                }
            });

            holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        Menu currentItem = getItem(adapterPosition);
                        if (currentItem.getQuantity() > 1) {
                            currentItem.setQuantity(currentItem.getQuantity() - 1);
                            notifyItemChanged(adapterPosition);
                            updateTotalPrice(); // Cập nhật tổng giá
                        } else {
                            // Hiển thị hộp thoại xác nhận
                            new AlertDialog.Builder(context)
                                    .setTitle("Xóa món ăn")
                                    .setMessage("Bạn có chắc chắn muốn xóa " + foodItem.getName() + " khỏi giỏ hàng không?")
                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Xóa món ăn khỏi giỏ hàng
                                            removeItem(adapterPosition); // Sử dụng adapterPosition để xóa
                                            if (cartUpdateListener != null) {
                                                cartUpdateListener.onCartUpdated(); // Gọi listener để cập nhật tổng số tiền và số lượng
                                            }
                                        }
                                    })
                                    .setNegativeButton("Không", null)
                                    .show();
                        }
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onCartUpdated(); // Thông báo cho Activity biết đã cập nhật
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("CartAdapter", "Error in onBindViewHolder: ", e);
        }
    }

    // Hàm xóa item khỏi giỏ hàng
    public void removeItem(int position) {

        // Lưu lại món ăn bị xóa
        Menu removedItem = foodItemList.get(position);
        int supplierId = removedItem.getSupplierId();
        foodItemList.remove(position); // Xóa item khỏi danh sách
        notifyItemRemoved(position); // Thông báo cho RecyclerView biết item đã bị xóa
        updateTotalPrice(); // Cập nhật tổng giá
        // Cập nhật cartMap trong SharedPreferences
        orderActivity.updateCartMap(supplierId, foodItemList); // Gọi updateCartMap với danh sách hiện tại
    }

    // Phương thức tính toán và cập nhật tổng tiền
    private void updateTotalPrice() {
        double totalPrice = 0;
        // Kiểm tra xem currentList có phải là danh sách đã được cập nhật không
        Log.d("CartAdapter", "Updating total price, current list size: " + foodItemList.size());
        for (Menu item : foodItemList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        // Kiểm tra xem currentList có phải là danh sách đã được cập nhật không
        Log.d("CartAdapter", "Current List Size: " + foodItemList.size());
        // Bạn có thể truyền giá trị tổng tiền cho Activity/Fragment để cập nhật UI
        // Ví dụ: ((OrderActivity) context).updateTotalPrice(totalPrice);
        // Nếu bạn có một phương thức để cập nhật giá trị này trong Activity/Fragment
        if (context instanceof OrderActivity) {
            double shippingFee = orderActivity.getShippingFee(); // Phương thức để lấy phí ship từ OrderActivity
            // Cộng phí ship vào tổng tiền
            totalPrice += shippingFee;
            ((OrderActivity) context).updateTotalPrice(totalPrice);
        }
    }




    // ViewHolder cho CartAdapter
    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemQuantity;
        ImageView buttonIncrease, buttonDecrease;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewName);
            itemPrice = itemView.findViewById(R.id.textViewPrice);
            itemQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease); // Ánh xạ nút tăng
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease); // Ánh xạ nút giảm
        }
    }
}
