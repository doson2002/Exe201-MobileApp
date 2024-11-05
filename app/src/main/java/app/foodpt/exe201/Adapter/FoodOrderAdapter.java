package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import app.foodpt.exe201.DTO.FoodOrder;
import app.foodpt.exe201.FoodOrderDetailActivity;
import app.foodpt.exe201.R;

import java.text.DecimalFormat;
import java.util.List;

public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.FoodOrderViewHolder> {

    private List<FoodOrder> foodOrderList;
    private Context context;


    public FoodOrderAdapter(Context context, List<FoodOrder> foodOrderList) {
        this.context = context;
        this.foodOrderList = foodOrderList;
    }

    @NonNull
    @Override
    public FoodOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_order, parent, false);
        return new FoodOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodOrderViewHolder holder, int position) {
        FoodOrder foodOrder = foodOrderList.get(position);

        // Sử dụng Glide để tải ảnh từ URL và hiển thị vào ImageView
        Glide.with(holder.itemView.getContext())
                .load(foodOrder.getFoodImage())  // URL của ảnh
                .placeholder(R.drawable.baseline_downloading_24)  // Hình ảnh mặc định khi đang tải
                .error(R.drawable.baseline_downloading_24)  // Hình ảnh hiển thị khi có lỗi
                .into(holder.ivFoodImage);  // Đặt ảnh vào ImageView
        holder.tvRestaurantName.setText(foodOrder.getRestaurantName());
        holder.tvTotalItems.setText("Số món: " + foodOrder.getTotalItems());
        // Định dạng totalPrice
        String formattedTotalPrice = formatTotalPrice(foodOrder.getTotalPrice());
        holder.tvTotalPrice.setText(formattedTotalPrice + " VND");
        holder.tvOrderStatus.setText("Trạng thái: " + foodOrder.getOrderStatus());
        holder.tvOrderTime.setText( foodOrder.getOrderTime());
        // Set sự kiện click vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi click vào item, chuyển sang màn hình FoodOrderDetailActivity
                Intent intent = new Intent(context, FoodOrderDetailActivity.class);
                intent.putExtra("orderId", foodOrder.getId()); // Gửi đối tượng FoodOrder sang Activity khác
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodOrderList.size();
    }

    public static class FoodOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvRestaurantName, tvTotalItems, tvTotalPrice, tvOrderStatus, tvOrderTime;

        public FoodOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
        }
    }
    // Phương thức để định dạng totalPrice
    private String formatTotalPrice(double totalPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // Định dạng không có phần thập phân, dấu phẩy chia các nghìn
        return decimalFormat.format(totalPrice);
    }
}
