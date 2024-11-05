package app.foodpt.exe201.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.R;

import java.util.ArrayList;
import java.util.List;

public class SupplierInfoAdapter extends RecyclerView.Adapter<SupplierInfoAdapter.SupplierInfoViewHolder> {

    private List<SupplierInfo> supplierInfoList;
    private Context context;
    private int selectedPosition = -1;
    private OnSupplierInfoClickListener listener; // Interface lắng nghe sự kiện click

    public SupplierInfoAdapter(List<SupplierInfo> supplierInfoList, Context context, OnSupplierInfoClickListener listener) {
        this.supplierInfoList = supplierInfoList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplierInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_food_item_customer.xml cho ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_info, parent, false);
        return new SupplierInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierInfoViewHolder holder, int position) {
        // Lấy FoodItem từ danh sách
        SupplierInfo supplierInfo = supplierInfoList.get(position);

//        // Gán sự kiện click cho item
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Khi người dùng click vào item, gọi interface listener
//                if (listener != null) {
//                    listener.onSupplierInfoClick(supplierInfo.getId());
//                }
//            }
//        });
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            if (listener != null) {
            listener.onSupplierInfoClick(supplierInfo);
            }
            //notifyDataSetChanged();
            notifyItemChanged(selectedPosition);

        });
        // Thiết lập dữ liệu cho các TextView và ImageView
        holder.textViewRestaurantName.setText(supplierInfo.getRestaurantName());
        holder.textViewTotalStar.setText(String.format("%.1f", supplierInfo.getTotalStarRating()));
//        holder.textViewTotalStar.setText(String.format("%,.0fđ", supplierInfo.getTotalStarRating()));
        holder.textViewTotalStar.setText(String.valueOf(supplierInfo.getTotalStarRating()));
        holder.textViewCountReview.setText("("+ String.valueOf(supplierInfo.getTotalReviewCount()) + ")");
        holder.textViewRestaurantType.setText(supplierInfo.getSupplierType().getTypeName());
        holder.textViewDistance.setText(String.format("%.1f km", supplierInfo.getDistance()));
        holder.textViewTimeOpen.setText(supplierInfo.getOpenTime()+ " - ");
        holder.textViewTimeClose.setText(supplierInfo.getCloseTime());
        Glide.with(context).load(supplierInfo.getImgUrl()).into(holder.imageViewSupplier);
    }


    @Override
    public int getItemCount() {
        return supplierInfoList.size();
    }

    public interface OnSupplierInfoClickListener {
        void onSupplierInfoClick(SupplierInfo supplierInfo);
    }


    // Cập nhật danh sách món ăn trong Adapter
    public void updateSupplierInfoList(List<SupplierInfo> newSupplierInfoList) {
        this.supplierInfoList = new ArrayList<>(newSupplierInfoList);
        notifyDataSetChanged();
    }

    // ViewHolder ánh xạ các thành phần UI từ layout item_food_item_customer.xml
    public static class SupplierInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSupplier,imgStarIcon;
        TextView textViewRestaurantName;
        TextView textViewTotalStar;
        TextView textViewCountReview;
        TextView textViewRestaurantType;
        TextView textViewDistance;
        TextView textViewTimeOpen;
        TextView textViewTimeClose;

        public SupplierInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSupplier = itemView.findViewById(R.id.imageViewSupplier);
            imgStarIcon = itemView.findViewById(R.id.imgStarIcon);
            textViewRestaurantName = itemView.findViewById(R.id.textViewRestaurantName);
            textViewTotalStar = itemView.findViewById(R.id.textViewTotalStar);
            textViewCountReview = itemView.findViewById(R.id.textViewCountReview);
            textViewRestaurantType = itemView.findViewById(R.id.textViewRestaurantType);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
            textViewTimeOpen = itemView.findViewById(R.id.textViewTimeOpen);
            textViewTimeClose = itemView.findViewById(R.id.textViewTimeClose);
        }
    }
}
