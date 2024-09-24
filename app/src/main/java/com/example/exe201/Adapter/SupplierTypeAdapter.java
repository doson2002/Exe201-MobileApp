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
import com.example.exe201.DTO.SupplierType;
import com.example.exe201.R;

import java.util.List;

public class SupplierTypeAdapter extends RecyclerView.Adapter<SupplierTypeAdapter.SupplierTypeViewHolder> {

    private List<SupplierType> supplierTypeList;
    private Context context;
    private int selectedPosition = -1;
    private OnSupplierTypeClickListener listener;

    public SupplierTypeAdapter(List<SupplierType> supplierTypeList, Context context, OnSupplierTypeClickListener listener) {
        this.supplierTypeList = supplierTypeList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SupplierTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier_type, parent, false);
        return new SupplierTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierTypeViewHolder holder, int position) {
        SupplierType supplierType = supplierTypeList.get(position);
        holder.textViewSupplierTypeName.setText(supplierType.getTypeName());
        Glide.with(context).load(supplierType.getImgUrl()).into(holder.imageViewSupplierType);

        // Kiểm tra vị trí được chọn
        if (selectedPosition == holder.getAdapterPosition()) {
            // Hiển thị overlayBackground khi được chọn
            holder.overlayBackground.setVisibility(View.VISIBLE);

            // Đổi màu chữ thành màu cam khi được chọn
            holder.textViewSupplierTypeName.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            // Trở về background ban đầu
            // Ẩn overlayBackground khi không được chọn
            holder.overlayBackground.setVisibility(View.GONE);
            // Trở về màu chữ ban đầu
            holder.textViewSupplierTypeName.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            listener.onSupplierTypeClick(supplierType);
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return supplierTypeList.size();
    }

    public interface OnSupplierTypeClickListener {
        void onSupplierTypeClick(SupplierType supplierType);
    }

    public static class SupplierTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSupplierType;
        TextView textViewSupplierTypeName;
        View overlayBackground;

        public SupplierTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSupplierType = itemView.findViewById(R.id.imageViewSupplierType);
            textViewSupplierTypeName = itemView.findViewById(R.id.textViewSupplierTypeName);
            overlayBackground = itemView.findViewById(R.id.overlayBackground);

        }
    }
}
