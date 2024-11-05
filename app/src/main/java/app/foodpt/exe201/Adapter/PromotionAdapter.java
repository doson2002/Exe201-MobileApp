package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.PromotionResponse;
import app.foodpt.exe201.R;

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
            holder.discountAmount.setText(String.format("Giảm %.0f%%",promotion.getDiscountPercentage()*100));
            holder.textViewDiscountValue.setText(String.format("%.0f%% OFF", promotion.getDiscountPercentage()*100));
        } else if (promotion.getFixedDiscountAmount() > 0) {
            holder.discountAmount.setText(String.format("Giảm %.0f đ",promotion.getFixedDiscountAmount()));
            holder.textViewDiscountValue.setText(String.format("%s OFF", formatDiscountAmount(promotion.getFixedDiscountAmount())));

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
        // Xử lý sự kiện nhấn vào icon xóa
        holder.deleteIcon.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận trước khi xóa
            new AlertDialog.Builder(context)
                    .setTitle("Xóa món")
                    .setMessage("Bạn có chắc chắn muốn xóa mã này không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Gọi hàm xóa món ăn
                            deletePromotion(promotion.getId(), holder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }
    private void deletePromotion(int promotionId, int position) {

        // Lấy jwtToken từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        // Gọi API xóa món ăn bằng Volley hoặc Retrofit
        String url = ApiEndpoints.DELETE_PROMOTION+ "/" + promotionId;

        // Ví dụ với Volley
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Xóa thành công, cập nhật giao diện
                    promotions.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Xóa mã khuyến mãi thành công", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Xử lý lỗi
                    Toast.makeText(context, "Lỗi khi xóa mã khuyến mãi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // Thêm JWT Token vào header nếu cần
                headers.put("Authorization", "Bearer " + jwtToken); // Thay jwtToken bằng token thực tế của bạn
                return headers;
            }
        };

        // Thêm request vào hàng đợi của Volley
        Volley.newRequestQueue(context).add(deleteRequest);
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
    // Phương thức định dạng số tiền
    private String formatDiscountAmount(double amount) {
        if (amount >= 1000) {
            return String.format("%.0fk", amount / 1000); // Chia cho 1000 và định dạng lại
        }
        return String.format("%.0f", amount); // Trả về giá trị nguyên nếu dưới 1000
    }


    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        TextView code, discountAmount, description, textViewDiscountValue;
        ImageView deleteIcon, starIcon, imageViewVoucherIcon;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            discountAmount = itemView.findViewById(R.id.discountAmount);
            description = itemView.findViewById(R.id.description);
            deleteIcon = itemView.findViewById(R.id.imgRemovePromotion);
            starIcon = itemView.findViewById(R.id.starIcon);
            imageViewVoucherIcon = itemView.findViewById(R.id.imageViewVoucherIcon);
            textViewDiscountValue = itemView.findViewById(R.id.textViewDiscountValue);


        }
    }


}
