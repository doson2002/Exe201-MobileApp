package com.example.exe201;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodItemAdapter;
import com.example.exe201.Adapter.FoodItemCustomerAdapter;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.FoodType;
import com.example.exe201.Decorations.CustomDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowFoodItemActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    private List<FoodType> foodTypeList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_food_item);

        // Nhận SupplierId từ Intent
        int supplierId = getIntent().getIntExtra("supplier_id", -1);
        if(supplierId != -1){
            getFoodTypeSupplierId(supplierId);
        }
        parentLinearLayout = findViewById(R.id.parentLinearLayout);


    }

    private void getFoodTypeSupplierId(int supplierId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        String url = ApiEndpoints.GET_FOOD_TYPES_BY_SUPPLIER_ID + "/"+ supplierId; // Thay thế bằng URL của bạn

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String typeName = jsonObject.getString("typeName");
                                int order = jsonObject.getInt("i");

                                // Tạo đối tượng FoodType từ JSON
                                FoodType foodType = new FoodType();
                                foodType.setId(id);
                                foodType.setTypeName(typeName);
                                foodType.setOrder(order);
                                // Bạn cũng có thể thêm các thuộc tính khác từ JSON vào FoodType

                                foodTypeList.add(foodType);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        displayFoodTypes();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonArrayRequest);    }

    private void displayFoodTypes() {
        for (final FoodType foodType : foodTypeList) {
            // Tạo TextView cho FoodType
            TextView textView = new TextView(this);
            textView.setText(foodType.getTypeName());
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK); // Đặt màu đen cho text
            textView.setTypeface(null, Typeface.BOLD);
            textView.setAllCaps(true); // Đặt tất cả chữ viết hoa
            // Thêm marginBottom cho TextView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 30, 0, 30);
            textView.setLayoutParams(params);
            // Thêm TextView vào LinearLayout chính
            parentLinearLayout.addView(textView);

            // Tạo RecyclerView cho FoodType
            final RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            // Sử dụng CustomDividerItemDecoration
            CustomDividerItemDecoration itemDecoration = new CustomDividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
            itemDecoration.setDrawable(dividerDrawable);
            recyclerView.addItemDecoration(itemDecoration);
            parentLinearLayout.addView(recyclerView);

            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String jwtToken = sharedPreferences.getString("JwtToken", null);
            // Gọi API để lấy danh sách FoodItem theo FoodTypeId
            String url = ApiEndpoints.GET_FOOD_ITEM_BY_FOOD_TYPE_ID +"/"+ foodType.getId();
            JsonArrayRequest foodItemRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            List<FoodItem> foodItemList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("food_name");
                                    double price = jsonObject.getDouble("price");
                                    String imageUrl = jsonObject.getString("image_url");
                                    String description = jsonObject.getString("description");

                                    FoodItem foodItem = new FoodItem(id, name, price, imageUrl,description);
                                    foodItemList.add(foodItem);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            // Đặt Adapter cho RecyclerView
                            FoodItemCustomerAdapter adapter = new FoodItemCustomerAdapter(foodItemList,ShowFoodItemActivity.this);
                            recyclerView.setAdapter(adapter);
                            // Đặt chiều cao cho RecyclerView dựa trên số lượng mục
                            setRecyclerViewHeight(recyclerView, foodItemList.size());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(foodItemRequest);        }
    }

    private void setRecyclerViewHeight(RecyclerView recyclerView, int itemCount) {
        int itemHeight = 100; // Chiều cao trung bình của một mục, đơn vị: dp
        float density = getResources().getDisplayMetrics().density;
        int totalHeight = (int) (itemCount * itemHeight * density);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight;
        recyclerView.setLayoutParams(params);
    }

}