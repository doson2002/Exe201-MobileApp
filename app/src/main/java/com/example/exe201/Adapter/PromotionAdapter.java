package com.example.exe201.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.DTO.PromotionResponse;
import com.example.exe201.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {
    private List<PromotionResponse> promotions;
    private Context context;
    private int selectedPosition = -1; // Vị trí được chọn


    public PromotionAdapter(Context context, List<PromotionResponse> promotions) {
        this.context = context;
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        PromotionResponse promotion = promotions.get(position);
        holder.code.setText(promotion.getCode());
        holder.description.setText(promotion.getDescription());

        if (promotion.getDiscountPercentage() > 0) {
            holder.discountAmount.setText(String.format("Giảm %.0f",promotion.getDiscountPercentage()));
        } else if (promotion.getFixedDiscountAmount() > 0) {
            holder.discountAmount.setText(String.format("Giảm %.0f đ",promotion.getFixedDiscountAmount()));
        } else {
            holder.discountAmount.setText("Không có giảm giá");
        }
        // Hiển thị ngôi sao dựa vào isOffered
        holder.starIcon.setImageResource(
                promotion.isStatus() ? R.drawable.baseline_star_24_filled : R.drawable.baseline_star_border_24
        );
        // Khi người dùng nhấn vào ngôi sao
        holder.starIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đổi trạng thái status (false -> true, true -> false)
                boolean newStatus = !promotion.isStatus();  // Đảo ngược giá trị của status
                promotion.setStatus(newStatus);  // Cập nhật giá trị mới vào promotion
                // Cập nhật lại giao diện ngôi sao
                holder.starIcon.setImageResource(
                        newStatus ? R.drawable.baseline_star_24_filled : R.drawable.baseline_star_border_24
                );

                // Gọi API cập nhật trạng thái isOffered
                updateStatus(promotion.getId(), newStatus);
            }
        });
    }
    private void updateStatus(final int promotionId, final boolean status) {
        // URL với query parameter isOffered
        String url = ApiEndpoints.UPDATE_STATUS+"/" + promotionId + "?status=" + status;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Cập nhật thành công
                        Toast.makeText(context, "Trạng thái mã giảm giá đã được cập nhật", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(context, "Lỗi khi cập nhật trạng thái mã giảm giá", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // Thêm JWT Token vào header nếu cần
                headers.put("Authorization", "Bearer " + jwtToken); // Thay jwtToken bằng token thực tế của bạn
                return headers;
            }
        };

        // Thêm yêu cầu vào RequestQueue
        Volley.newRequestQueue(context).add(putRequest);
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        TextView code, discountAmount, description;
        ImageView deleteIcon, starIcon;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            discountAmount = itemView.findViewById(R.id.discountAmount);
            description = itemView.findViewById(R.id.description);
            deleteIcon = itemView.findViewById(R.id.imgRemoveFood);
            starIcon = itemView.findViewById(R.id.starIcon);
        }
    }


}
