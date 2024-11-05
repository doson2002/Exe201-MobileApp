package app.foodpt.exe201.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.foodpt.exe201.DTO.FoodOrderItemResponse;
import app.foodpt.exe201.R;

import java.util.List;

public class FoodOrderItemAdapter extends RecyclerView.Adapter<FoodOrderItemAdapter.FoodOrderItemViewHolder> {

    private List<FoodOrderItemResponse> foodOrderItemList;

    public FoodOrderItemAdapter(List<FoodOrderItemResponse> foodOrderItemList) {
        this.foodOrderItemList = foodOrderItemList;
    }

    @NonNull
    @Override
    public FoodOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_order_item, parent, false);
        return new FoodOrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodOrderItemViewHolder holder, int position) {
        FoodOrderItemResponse item = foodOrderItemList.get(position);
        holder.tvFoodName.setText(item.getFoodName());
        holder.tvQuantity.setText("x" + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return foodOrderItemList.size();
    }

    public static class FoodOrderItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFoodName, tvQuantity;

        public FoodOrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
