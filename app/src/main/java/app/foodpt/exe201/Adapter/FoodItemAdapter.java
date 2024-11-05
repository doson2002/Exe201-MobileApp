package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.FoodItem;
import app.foodpt.exe201.DTO.FoodType;
import app.foodpt.exe201.FoodItemDetailActivity;
import app.foodpt.exe201.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> foodItems;
    private Context context;

    public FoodItemAdapter(List<FoodItem> foodItems,Context context) {
        this.foodItems = foodItems;
        this.context = context;

    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodImage;
        public TextView foodName;
        public TextView price;
        public TextView quantity;
        public TextView foodTypes;
        public Button btnViewDetail;
        public ImageView deleteIcon, starIcon;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            foodTypes = itemView.findViewById(R.id.foodTypes); // Thêm TextView cho foodTypes
            btnViewDetail = itemView.findViewById(R.id.btnViewDetails);
            deleteIcon = itemView.findViewById(R.id.imgRemoveFood);
            starIcon = itemView.findViewById(R.id.starIcon);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getFoodName());
        holder.price.setText(foodItem.getPrice() + " VND");
        holder.quantity.setText("Số lượng: " + foodItem.getQuantity());


        // Hiển thị các loại món ăn (nếu cần)
        StringBuilder types = new StringBuilder();
        for (FoodType type : foodItem.getFoodTypes()) {
            types.append(type.getTypeName()).append(", ");
        }
        // Xóa dấu phẩy thừa
        if (types.length() > 0) {
            types.setLength(types.length() - 2); // Xóa dấu phẩy và khoảng trắng cuối
        }
        holder.foodTypes.setText(types.toString());

        // Sử dụng Glide để tải hình ảnh
        Glide.with(holder.foodImage.getContext()).load(foodItem.getImageUrl()).into(holder.foodImage);
        // Xử lý sự kiện nhấn vào nút "Chi tiết"
        holder.btnViewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodItemDetailActivity.class);
            intent.putExtra("foodItemId", foodItem.getId()); // Truyền ID của FoodItem
            context.startActivity(intent);
        });

        // Hiển thị ngôi sao dựa vào isOffered
        holder.starIcon.setImageResource(
                foodItem.getIsOffered() == 1 ? R.drawable.baseline_star_24_filled : R.drawable.baseline_star_border_24
        );
        // Khi người dùng nhấn vào ngôi sao
        holder.starIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đổi trạng thái isOffered (0 -> 1, 1 -> 0)
                int newOfferedStatus = foodItem.getIsOffered() == 1 ? 0 : 1;
                foodItem.setIsOffered(newOfferedStatus);

                // Cập nhật lại giao diện ngôi sao
                holder.starIcon.setImageResource(
                        newOfferedStatus == 1 ? R.drawable.baseline_star_24_filled : R.drawable.baseline_star_border_24
                );

                // Gọi API cập nhật trạng thái isOffered
                updateOfferedStatus(foodItem.getId(), newOfferedStatus);
            }
        });
        // Xử lý sự kiện nhấn vào icon xóa
        holder.deleteIcon.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận trước khi xóa
            new AlertDialog.Builder(context)
                    .setTitle("Xóa món")
                    .setMessage("Bạn có chắc chắn muốn xóa món này không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Gọi hàm xóa món ăn
                            deleteFoodItem(foodItem.getId(), holder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    // Phương thức cập nhật danh sách FoodItem và thông báo cho Adapter
    public void updateFoodItemList(List<FoodItem> newFoodItemList) {
        if (newFoodItemList != null) { // Kiểm tra null trước khi cập nhật
            this.foodItems.clear(); // Xóa danh sách cũ
            this.foodItems.addAll(newFoodItemList); // Thêm danh sách mới
            notifyDataSetChanged(); // Cập nhật lại RecyclerView với danh sách mới
        }
    }
    // Phương thức xóa món ăn
    private void deleteFoodItem(int foodItemId, int position) {

        // Lấy jwtToken từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        // Gọi API xóa món ăn bằng Volley hoặc Retrofit
        String url = ApiEndpoints.DELETE_FOOD_ITEM+ "/" + foodItemId;

        // Ví dụ với Volley
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Xóa thành công, cập nhật giao diện
                    foodItems.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Xử lý lỗi
                    Toast.makeText(context, "Lỗi khi xóa món ăn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateOfferedStatus(final int foodItemId, final int isOffered) {
        // URL với query parameter isOffered
        String url = ApiEndpoints.UPDATE_OFFERED_STATUS+"/" + foodItemId + "?isOffered=" + isOffered;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Cập nhật thành công
                        Toast.makeText(context, "Trạng thái ưu tiên được cập nhật", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(context, "Lỗi khi cập nhật trạng thái ưu tiên", Toast.LENGTH_SHORT).show();
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

}