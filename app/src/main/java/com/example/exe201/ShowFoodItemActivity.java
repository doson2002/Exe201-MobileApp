package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodItemAdapter;
import com.example.exe201.Adapter.FoodItemCustomerAdapter;
import com.example.exe201.Adapter.FoodItemGroupedBySupplierAdapter;
import com.example.exe201.DTO.CartFoodItem;
import com.example.exe201.DTO.FoodItem;
import com.example.exe201.DTO.FoodItemResponseWithSupplier;
import com.example.exe201.DTO.FoodType;
import com.example.exe201.DTO.Menu;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.Decorations.CustomDividerItemDecoration;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowFoodItemActivity extends AppCompatActivity{

    private LinearLayout parentLinearLayout, linearLayoutRating;
    private TextView basketItemCount, basketTotalPrice;
    // Khai báo cartMap để lưu các món ăn và số lượng
    private FoodItemCustomerAdapter foodAdapter;
    private FoodItemGroupedBySupplierAdapter foodItemOfferedAdapter;
    private LinearLayout searchBarContainer,topBarLayout ;
    private NestedScrollView nestedScrollView ;
    private HashMap<Integer, List<Menu>> cartMap = new HashMap<>();
    private  List<Menu> cartList = new ArrayList<>();
    private List<FoodType> foodTypeList = new ArrayList<>();
    private List<Menu> foodItemList = new ArrayList<>();
    private List<FoodItemResponseWithSupplier> foodItemOfferedList = new ArrayList<>();
    private ImageView backArrow, imageViewSupplier;
    private ImageView imgShowCart;
    private Button buttonChat;
    private TextView textViewRestaurantName, textViewReviews,textViewStarAverage
            ,textViewDeliveryInfo,textViewRestaurantName1;
    private EditText searchBar;
    private String keyword = "";
    private double latitude, longitude;
    // Map để lưu RecyclerView và Adapter theo foodTypeId
    private Map<Long, RecyclerView> recyclerViewMap = new HashMap<>();
    private Map<Long, FoodItemCustomerAdapter> adapterMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_food_item);
// Lấy root view
        View rootView = findViewById(R.id.root_view);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
        SupplierInfo supplierInfo = getIntent().getParcelableExtra("supplier");
        if(supplierInfo!=null){
            latitude = supplierInfo.getLatitude();
            longitude = supplierInfo.getLongitude();
        }else{
            Toast.makeText(ShowFoodItemActivity.this, "Opsssss! Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            return;
        }


        imgShowCart= findViewById(R.id.imgShowCart);
        imgShowCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowFoodItemActivity.this, SupplierCartActivity.class);
                intent.putExtra("cart_map",  cartMap); // Truyền cartMap
                startActivity(intent);
            }
        });



        searchBarContainer = findViewById(R.id.searchBarContainer);
        topBarLayout = findViewById(R.id.topBarLayout);
        nestedScrollView = findViewById(R.id.nestedScrollViewContent);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // Lấy giá trị từ EditText searchBar
                String searchText = searchBar.getText().toString().trim(); // Lấy giá trị và loại bỏ khoảng trắng
                if (scrollY > 100) {
                    imageViewSupplier.setVisibility(View.GONE);  // Hide the image
                    topBarLayout.setVisibility(View.VISIBLE);
                    buttonChat.setVisibility(View.GONE);
                    backArrow.setVisibility(View.GONE);
                } else if(searchText.isEmpty())
                {
                    imageViewSupplier.setVisibility(View.VISIBLE);  // Show the image
                    topBarLayout.setVisibility(View.GONE);  // Hide search bar
                    buttonChat.setVisibility(View.VISIBLE);
                    backArrow.setVisibility(View.VISIBLE);

                }
            }
        });
        if(supplierInfo!=null){
            setupRecyclerViewForFoodOffered(supplierInfo.getId());
        }

        linearLayoutRating = findViewById(R.id.linearLayoutRating);
        linearLayoutRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowFoodItemActivity.this, CustomerReviewActivity.class);
                intent.putExtra("supplier", supplierInfo);
                startActivity(intent);
            }
        });
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        // Bước 1: Lấy TextView cần hiển thị tên nhà cung cấp
         textViewRestaurantName = findViewById(R.id.textViewRestaurantName);
         textViewReviews = findViewById(R.id.textViewReviews);
         textViewStarAverage = findViewById(R.id.textViewStarAverage);
         textViewDeliveryInfo = findViewById(R.id.textViewDeliveryInfo);
         textViewRestaurantName1 = findViewById(R.id.textViewRestaurantName1);


        int supplierId = supplierInfo.getId();
        if(supplierId != -1){
            getFoodTypeSupplierId(supplierId,keyword);
        }

        imageViewSupplier = findViewById(R.id.imageViewSupplier);
        String supplierUrl = supplierInfo.getImgUrl();
        // Load the image using Glide
        Glide.with(this)
                .load(supplierUrl)
                .into(imageViewSupplier);

        // Nếu SupplierInfo không null, hiển thị tên nhà cung cấp
        if (supplierInfo != null) {
            // Bước 3: Lấy tên nhà cung cấp từ SupplierInfo và thiết lập cho TextView
            String supplierName = supplierInfo.getRestaurantName();
            int totalRating = supplierInfo.getTotalReviewCount();
            double starAverage = supplierInfo.getTotalStarRating();
            double distance = supplierInfo.getDistance();
            textViewReviews.setText(String.format("(%d đánh giá)", totalRating));
            textViewRestaurantName.setText(supplierName);
            textViewRestaurantName1.setText(supplierName);
            textViewStarAverage.setText(String.format("%.1f", starAverage));
            textViewDeliveryInfo.setText(String.format("%.1f km", distance));
        } else {
            // Nếu supplierInfo null, bạn có thể hiển thị thông báo lỗi hoặc tên mặc định
            textViewRestaurantName.setText("Unknown Supplier");
        }
        parentLinearLayout = findViewById(R.id.parentLinearLayout);


         // Tạo FoodItemCustomerAdapter và gán cartList cho nó
        foodAdapter = new FoodItemCustomerAdapter(foodItemList, this,cartMap, imgShowCart);

        // Thiết lập sự kiện OnClickListener cho basketLayout
        buttonChat = findViewById(R.id.buttonChat);
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowFoodItemActivity.this, ChatActivity.class);
                intent.putExtra("supplier", supplierInfo);
                startActivity(intent);
            }
        });
        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi lại displayFoodTypes với từ khóa tìm kiếm mới
                keyword = s.toString();
//                parentLinearLayout.removeAllViews(); // Xóa tất cả các view cũ
                displayFoodTypes(keyword); // Hiển thị lại danh sách với keyword m
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Gọi lại displayFoodTypes với từ khóa tìm kiếm mới
                keyword = s.toString();
//                parentLinearLayout.removeAllViews(); // Xóa tất cả các view cũ
                displayFoodTypes(keyword); // Hiển thị lại danh sách với keyword mới
                imageViewSupplier.setVisibility(View.GONE);  // Hide the image
                topBarLayout.setVisibility(View.VISIBLE);
                buttonChat.setVisibility(View.GONE);
                backArrow.setVisibility(View.GONE);
            }
        });

    }

    private void getFoodTypeSupplierId(int supplierId, String keyword) {
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
                        displayFoodTypes(keyword);
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

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void displayFoodTypes(String keyword) {
        for (final FoodType foodType : foodTypeList) {
            long foodTypeId = foodType.getId();

            // Kiểm tra nếu đã có RecyclerView cho FoodType này, nếu có thì không cần tạo lại
            RecyclerView recyclerView = recyclerViewMap.get(foodTypeId);
            if (recyclerView == null) {
                // Tạo TextView cho FoodType
                TextView textView = new TextView(this);
                textView.setText(foodType.getTypeName());
                textView.setTextSize(18);
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setAllCaps(true);

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
                recyclerView = new RecyclerView(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                // Sử dụng CustomDividerItemDecoration
                CustomDividerItemDecoration itemDecoration = new CustomDividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
                itemDecoration.setDrawable(dividerDrawable);
                recyclerView.addItemDecoration(itemDecoration);

                // Lưu RecyclerView vào Map
                recyclerViewMap.put(foodTypeId, recyclerView);
                parentLinearLayout.addView(recyclerView);
            }

            // Lấy token từ SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String jwtToken = sharedPreferences.getString("JwtToken", null);

            // Gọi API để lấy danh sách FoodItem theo FoodTypeId và keyword
            String url = ApiEndpoints.GET_FOOD_ITEM_BY_FOOD_TYPE_ID + "/" + foodTypeId + "?keyword=" + keyword;
            RecyclerView finalRecyclerView = recyclerView;
            JsonArrayRequest foodItemRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            List<Menu> foodItemList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("food_name");
                                    double price = jsonObject.getDouble("price");
                                    String imageUrl = jsonObject.getString("image_url");
                                    String description = jsonObject.getString("description");

                                    JSONObject supplierObject = jsonObject.getJSONObject("supplier_info");
                                    int supplierId = supplierObject.getInt("id");

                                    Menu foodItem = new Menu(id, name, description, price, imageUrl, supplierId);
                                    foodItemList.add(foodItem);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Lấy adapter từ Map, nếu chưa có adapter thì tạo mới
                            FoodItemCustomerAdapter adapter = adapterMap.get(foodTypeId);
                            if (adapter == null) {
                                adapter = new FoodItemCustomerAdapter(foodItemList, ShowFoodItemActivity.this, cartMap, imgShowCart);
                                adapterMap.put(foodTypeId, adapter);
                                finalRecyclerView.setAdapter(adapter);
                            } else {
                                // Cập nhật lại dữ liệu cho adapter hiện tại
                                adapter.updateData(foodItemList);
                            }

                            // Đặt chiều cao cho RecyclerView dựa trên số lượng mục
                            setRecyclerViewHeight(finalRecyclerView, foodItemList.size());

                            // Xử lý cuộn đến vị trí món ăn (nếu cần)
                            int selectedFoodItemId = getIntent().getIntExtra("selectedFoodItemId", -1);
                            if (selectedFoodItemId != -1) {
                                for (int i = 0; i < foodItemList.size(); i++) {
                                    if (foodItemList.get(i).getId() == selectedFoodItemId) {
                                        final int finalFoodItemIndex = i;
                                        finalRecyclerView.post(() -> finalRecyclerView.smoothScrollToPosition(finalFoodItemIndex));
                                        break;
                                    }
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + jwtToken);
                    return headers;
                }
            };

            // Thêm request vào hàng đợi
            Volley.newRequestQueue(this).add(foodItemRequest);
        }
    }

    private void updateFoodItems(String keyword) {
        for (int i = 0; i < parentLinearLayout.getChildCount(); i++) {
            View view = parentLinearLayout.getChildAt(i);

            // Kiểm tra nếu view là RecyclerView (của FoodItem)
            if (view instanceof RecyclerView) {
                final RecyclerView recyclerView = (RecyclerView) view;

                // Lấy lại FoodType tương ứng từ vị trí i
                final FoodType foodType = foodTypeList.get(i / 2); // vì có cả TextView cho FoodType
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String jwtToken = sharedPreferences.getString("JwtToken", null);
                // Gọi API để lấy danh sách FoodItem theo FoodTypeId và keyword
                String url = ApiEndpoints.GET_FOOD_ITEM_BY_FOOD_TYPE_ID + "/" + foodType.getId() + "?keyword=" + keyword;

                JsonArrayRequest foodItemRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                foodItemList = new ArrayList<>();
                                for (int j = 0; j < response.length(); j++) {
                                    try {
                                        JSONObject jsonObject = response.getJSONObject(j);
                                        int id = jsonObject.getInt("id");
                                        String name = jsonObject.getString("food_name");
                                        double price = jsonObject.getDouble("price");
                                        String imageUrl = jsonObject.getString("image_url");
                                        String description = jsonObject.getString("description");

                                        JSONObject supplierObject = jsonObject.getJSONObject("supplier_info");
                                        int supplierId = supplierObject.getInt("id");

                                        Menu foodItem = new Menu(id, name, description, price, imageUrl, supplierId);
                                        foodItemList.add(foodItem);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // Cập nhật Adapter của RecyclerView
                                FoodItemCustomerAdapter adapter = (FoodItemCustomerAdapter) recyclerView.getAdapter();
                                if (adapter != null) {
                                    adapter.updateData(foodItemList); // Thêm phương thức để cập nhật dữ liệu
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", error.toString());
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                        return headers;
                    }
                };

                Volley.newRequestQueue(this).add(foodItemRequest);
            }
        }
    }





    private void setRecyclerViewHeight(RecyclerView recyclerView, int itemCount) {
        int itemHeight = 150; // Chiều cao trung bình của một mục, đơn vị: dp
        float density = getResources().getDisplayMetrics().density;
        int totalHeight = (int) (itemCount * itemHeight * density);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight;
        recyclerView.setLayoutParams(params);
    }

    private void setupRecyclerViewForFoodOffered(int supplierId) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOffered);

        // Sử dụng LinearLayoutManager với chiều ngang
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        // Thiết lập Adapter
        foodItemOfferedAdapter = new FoodItemGroupedBySupplierAdapter(foodItemOfferedList,this,cartMap,imgShowCart);
        recyclerView.setAdapter(foodItemOfferedAdapter);

        // Fetch data và cập nhật vào RecyclerView
        fetchFoodItemsByOfferedStatus(supplierId, 1);  // Truyền 1 cho món ăn được ưu tiên
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("updated_cart_list")) {
                List<Menu> updatedCartList = (List<Menu>) data.getSerializableExtra("updated_cart_list"); // Nhận giỏ hàng đã cập nhật
                if (updatedCartList != null) {
                    cartList.clear();
                    cartList.addAll(updatedCartList); // Cập nhật giỏ hàng

                }
            }
        }
    }


    private void fetchFoodItemsByOfferedStatus(int supplierId, int isOffered) {
        String url = ApiEndpoints.GET_FOOD_ITEMS_BY_OFFERED_STATUS+ "/" + supplierId + "?isOffered=" + isOffered;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse response và cập nhật RecyclerView
                        foodItemOfferedList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject foodItemObj = response.getJSONObject(i);
                                int id = foodItemObj.getInt("id");
                                String foodName = foodItemObj.getString("food_name");
                                String imgUrl = foodItemObj.getString("image_url");
                                double price = foodItemObj.getDouble("price");
                                int quantity = foodItemObj.getInt("quantity");
                                int supplierId = foodItemObj.getInt("supplier_id");

                                FoodItemResponseWithSupplier foodItem = new FoodItemResponseWithSupplier(id, foodName, price, imgUrl, quantity, supplierId);
                                foodItemOfferedList.add(foodItem);
                            }

                            // Cập nhật adapter với danh sách món ăn
                            foodItemOfferedAdapter.updateFoodItemList(foodItemOfferedList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(ShowFoodItemActivity.this, "Lỗi khi lấy dữ liệu món ăn", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                        return headers;
                    }
                };

        // Thêm request vào hàng đợi
        Volley.newRequestQueue(this).add(request);
    }

}