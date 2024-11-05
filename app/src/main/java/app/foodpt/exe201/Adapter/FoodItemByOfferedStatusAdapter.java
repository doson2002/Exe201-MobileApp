//package com.example.exe201.Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.exe201.DTO.FoodItem;
//import com.example.exe201.R;
//
//import java.util.List;
//
//public class FoodItemByOfferedStatusAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {
//
//    private List<FoodItem> foodItemList;
//    private Context context;
//
//    public FoodItemByOfferedStatusAdapter(Context context, List<FoodItem> foodItemList) {
//        this.context = context;
//        this.foodItemList = foodItemList;
//    }
//
//    @NonNull
//    @Override
//    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_food_grouped_by_supplier, parent, false);
//        return new FoodItemViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
//        FoodItem foodItem = foodItemList.get(position);
//        holder.foodName.setText(foodItem.getFoodName());
//        holder.foodPrice.setText(String.valueOf(foodItem.getPrice()));
//
//        // Load hình ảnh nếu có link imgUrl (nếu bạn dùng thư viện Glide hoặc Picasso)
//        // Glide.with(context).load(foodItem.getImgUrl()).into(holder.foodImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return foodItemList.size();
//    }
//
//    public static class FoodItemViewHolder extends RecyclerView.ViewHolder {
//        TextView foodName, foodPrice;
//        ImageView foodImage;
//
//        public FoodItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            foodName = itemView.findViewById(R.id.foodName);
//            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
//            foodImage = itemView.findViewById(R.id.foodImage);
//        }
//    }
//}
