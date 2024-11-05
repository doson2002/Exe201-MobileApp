package app.foodpt.exe201.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.Menu;
import app.foodpt.exe201.OrderActivity;
import app.foodpt.exe201.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierCartAdapter extends RecyclerView.Adapter<SupplierCartAdapter.SupplierViewHolder> {

    private HashMap<Integer, List<Menu>> cartMap;
    private List<Integer> supplierIds;
    private Context context;

    public SupplierCartAdapter(HashMap<Integer, List<Menu>> cartMap) {
        this.cartMap = cartMap;
        this.supplierIds = new ArrayList<>(cartMap.keySet());
    }



    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier_cart, parent, false);
        context = parent.getContext();
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        int supplierId = supplierIds.get(position);
        List<Menu> cartList = cartMap.get(supplierId);

        if (cartList != null && !cartList.isEmpty()) {
            getSupplierById(supplierId, holder);
            // Đếm tổng số lượng món ăn của nhà cung cấp
            int totalItems = 0;
            for (Menu item : cartList) {
                totalItems += item.getQuantity();
            }

            // Định dạng số lượng món ăn
            String formattedItemCount = String.format("%,d", totalItems); // Định dạng với dấu phẩy cho hàng nghìn
            holder.textViewItemCount.setText("Số lượng món: " + formattedItemCount);
            // Khi người dùng nhấn vào nhà cung cấp, chuyển đến OrderActivity
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("supplier_id", supplierId);
                    intent.putExtra("cart_list", (Serializable) cartList);
                    context.startActivity(intent);
                }
            });
        }
    }
    // Phương thức gọi API để lấy thông tin nhà cung cấp
    private void getSupplierById(int supplierId, SupplierViewHolder holder) {
        String url = ApiEndpoints.GET_SUPPLIER_BY_ID +"/"+ supplierId; //
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        // Tạo yêu cầu GET
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String name = jsonObject.getString("restaurant_name");
                        String imgUrl = jsonObject.getString("img_url");
                        name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
                        // Cập nhật UI với thông tin nhà cung cấp
                        holder.textViewSupplierName.setText(name);

                        Glide.with(context)
                                .load(imgUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.baseline_downloading_24) // Ảnh mặc định khi đang tải
                                        .error(R.drawable.baseline_downloading_24) // Ảnh mặc định khi URL rỗng hoặc lỗi
                                        .fitCenter() // Cắt ảnh cho vừa vặn với ImageView hình vuông
                                        .transform(new RoundedCorners(30))) // Bo tròn 4 góc
                                .into(holder.imageViewSupplier);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // Xử lý lỗi nếu có
                    error.printStackTrace();
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                        return headers;
                    }
        };

        // Thêm yêu cầu vào queue
        Volley.newRequestQueue(context).add(stringRequest);
    }

    // Method to update the cartMap data and refresh the list
    public void updateCartMap(HashMap<Integer, List<Menu>> newCartMap) {
        this.cartMap = newCartMap;
        this.supplierIds = new ArrayList<>(newCartMap.keySet()); // Update supplierIds
        notifyDataSetChanged(); // Refresh RecyclerView
    }

    @Override
    public int getItemCount() {
        return supplierIds.size();
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSupplier;
        TextView textViewSupplierName, textViewItemCount;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSupplier = itemView.findViewById(R.id.imageViewSupplier);
            textViewSupplierName = itemView.findViewById(R.id.textViewSupplierName);
            textViewItemCount = itemView.findViewById(R.id.textViewItemCount);
        }
    }
}