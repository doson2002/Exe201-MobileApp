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
            holder.itemView.setBackgroundResource(R.drawable.circle_with_check); // Thay thế bằng background của bạn
        } else {
            holder.itemView.setBackgroundResource(R.drawable.image_view_background); // Thay thế bằng background của bạn

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

        public SupplierTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSupplierType = itemView.findViewById(R.id.imageViewSupplierType);
            textViewSupplierTypeName = itemView.findViewById(R.id.textViewSupplierTypeName);
        }
    }
}
