package app.foodpt.exe201.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.FoodOrder;
import app.foodpt.exe201.OrderDetailForPartner;
import app.foodpt.exe201.OrderForPartnerActivity;
import app.foodpt.exe201.R;

public class OrderForPartnerAdapter extends RecyclerView.Adapter<OrderForPartnerAdapter.OrderViewHolder> {

    private List<FoodOrder> orderList;
    private Context context;

    public OrderForPartnerAdapter(Context context,List<FoodOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list_for_partner, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FoodOrder order = orderList.get(position);

        holder.textOrderID.setText("Mã đơn: #" + order.getId());
        holder.textViewCustomerName.setText("Người đặt: " + order.getCustomerName());
        holder.tvOrderStatus.setText(order.getOrderStatus());
        holder.textViewPrice.setText(String.format(Locale.getDefault(), "Tổng tiền: %.0f VNĐ", order.getTotalPrice()));
        holder.textViewCreatedDate.setText(order.getOrderTime());
        // Xử lý click vào TextView trạng thái để hiển thị PopupWindow
        holder.tvOrderStatus.setOnClickListener(v -> showPopupWindow(v, order, holder));

        // Xử lý click vào toàn bộ item để chuyển đến trang OrderDetail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderDetailForPartner.class);
            intent.putExtra("orderId", order.getId());  // Gửi orderId qua Intent
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView textOrderID, textViewCustomerName, tvOrderStatus, textViewPrice, textViewCreatedDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderID = itemView.findViewById(R.id.textOrderID);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewCreatedDate = itemView.findViewById(R.id.textViewCreatedDate);
        }
    }
    // Hiển thị PopupWindow để chọn trạng thái đơn hàng
    private void showPopupWindow(View anchorView, FoodOrder order, OrderViewHolder holder) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_order_status, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchorView);

        // Các lựa chọn trạng thái
        TextView tvStatusInProgress = popupView.findViewById(R.id.tvStatusInProgress);
        TextView tvStatusCompleted = popupView.findViewById(R.id.tvStatusCompleted);
        TextView tvStatusFailed = popupView.findViewById(R.id.tvStatusFailed);

        // Khi người dùng chọn trạng thái "Đang giao"
        tvStatusInProgress.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "đang giao", holder);
            popupWindow.dismiss();
        });

        // Khi người dùng chọn trạng thái "Hoàn thành"
        tvStatusCompleted.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "hoàn thành", holder);
            popupWindow.dismiss();
        });

        // Khi người dùng chọn trạng thái "Thất bại"
        tvStatusFailed.setOnClickListener(v -> {
            updateOrderStatus(order.getId(), "thất bại", holder);
            popupWindow.dismiss();
        });
    }

    // Gọi API để cập nhật trạng thái đơn hàng
    private void updateOrderStatus(int orderId, String newStatus, OrderViewHolder holder) {
        String url = ApiEndpoints.UPDATE_ORDER_STATUS + "/" + orderId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("orderStatus", newStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                requestBody,
                response -> {
                    // Cập nhật giao diện sau khi thay đổi trạng thái thành công
                    holder.tvOrderStatus.setText(newStatus);
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(context, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                String jwtToken = sharedPreferences.getString("JwtToken", null);
                if (jwtToken != null) {
                    headers.put("Authorization", "Bearer " + jwtToken);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
}