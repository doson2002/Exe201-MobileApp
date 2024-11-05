package app.foodpt.exe201;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.CartAdapter;
import app.foodpt.exe201.DTO.FoodOrder;
import app.foodpt.exe201.DTO.FoodOrderItemResponse;
import app.foodpt.exe201.DTO.Menu;
import app.foodpt.exe201.DTO.OrderRequest;
import app.foodpt.exe201.DTO.PromotionResponse;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.helpers.GoogleSheetsService;
import app.foodpt.exe201.helpers.Utils;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_APPLY_PROMOTION = 2; // Có thể thay đổi theo nhu cầu
    private TextView textViewDeliveryAddress;
    private List<Menu> cartList;
    private List<FoodOrderItemResponse> reOrderList;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ImageView backArrow;
    private TextView textViewEditAddress, textViewRestaurantName, textViewShippingFee, textViewAddFoodItem;
    private Button createOrderButton ,buttonApplyOffers;
    private RequestQueue requestQueue;
    private String paymentMethod = "", address = "";
    private double totalPrice = 0;
    private String contentBank = "";
    private double distance = 0;
    private double shippingFee = 0 ;
    private double totalAmount = 0;
    private double voucherAmount = 0 ;
    private double totalPriceOrder =  0;
    private double latitudeSupplier = 0 ;
    private double longitudeSupplier = 0;
    private boolean isFromApplyPromotion = false;
    private PromotionResponse selectedPromotion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

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
        SupplierInfo supplierInfoChose = Utils.getSupplierInfo(this);
        if(supplierInfoChose==null){
            return;
        }else{
            latitudeSupplier = supplierInfoChose.getLatitude();
            longitudeSupplier = supplierInfoChose.getLongitude();
        }



        textViewRestaurantName = findViewById(R.id.textViewRestaurantName);
        textViewRestaurantName.setText(supplierInfoChose.getRestaurantName());
        createOrderButton = findViewById(R.id.createOrderButton);
        textViewShippingFee = findViewById(R.id.textViewShippingFee);
        textViewAddFoodItem = findViewById(R.id.textViewAddFoodItem);

        distance = supplierInfoChose.getDistance();
        shippingFee = calculateShippingFee(distance);
        textViewShippingFee.setText(String.format("%,.0fđ", shippingFee));



        // Tìm RadioGroup và các RadioButton
        RadioGroup paymentOptions = findViewById(R.id.paymentOptions);
        RadioButton radioCashPayment = findViewById(R.id.radioCashPayment);
        RadioButton radioVnpay = findViewById(R.id.radioVnpay);
        // Set sự kiện cho RadioGroup
        paymentOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Kiểm tra RadioButton nào được chọn
                if (checkedId == R.id.radioCashPayment) {
                    Log.d("RadioButton", "Cash payment selected");
                    // Thực hiện xử lý khi chọn Cash
                    paymentMethod= "cash";
                } else if (checkedId == R.id.radioVnpay) {
                    Log.d("RadioButton", "transfer payment selected");
                    // Thực hiện xử lý khi chọn chuyển khoản
                    paymentMethod= "transfer";
                }
            }
        });
        // Initialize the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        createOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createOrder();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        address = sharedPreferences.getString("address", null);
        textViewDeliveryAddress = findViewById(R.id.textViewDeliveryAddress);
        textViewDeliveryAddress.setText(address);

        textViewEditAddress= findViewById(R.id.textViewEditAddress);
        textViewEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, MapsActivity.class);
                intent.putExtra("latitudeSupplier", latitudeSupplier);
                intent.putExtra("longitudeSupplier", longitudeSupplier);
                startActivityForResult(intent, REQUEST_CODE_MAP); // Gọi MapsActivity
            }
        });


        textViewAddFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, ShowFoodItemActivity.class);
                intent.putExtra("supplier", supplierInfoChose);
                startActivity(intent); // Gọi MapsActivity
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

        buttonApplyOffers = findViewById(R.id.buttonApplyOffers);
        buttonApplyOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                int supplierId = intent1.getIntExtra("supplier_id",0);
                Intent intent = new Intent(OrderActivity.this, ApplyPromotionActivity.class);
                intent.putExtra("supplier_id",supplierId);
                startActivityForResult(intent, REQUEST_CODE_APPLY_PROMOTION);            }
        });

        // Khởi tạo hoặc lấy dữ liệu giỏ hàng ở đây
//        cartList = getCartItemsFromSharedPreferences();

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        // Lấy dữ liệu giỏ hàng từ Intent hoặc nguồn dữ liệu khác
        cartList = (ArrayList<Menu>) getIntent().getSerializableExtra("cart_list");
// Nhận danh sách `Parcelable`
        if (cartList != null && !cartList.isEmpty()) {
            // Thiết lập Adapter và truyền dữ liệu
            cartAdapter = new CartAdapter(this, cartList,this);
            recyclerView.setAdapter(cartAdapter);

            // Gửi danh sách giỏ hàng đến Adapter
            cartAdapter.submitList(cartList);

            // Thiết lập lắng nghe sự kiện thay đổi số lượng trong Adapter
            cartAdapter.setCartUpdateListener(new CartAdapter.CartUpdateListener() {
                @Override
                public void onCartUpdated() {
                    updateCartList(); // Gọi hàm cập nhật lại tổng số tiền và số lượng

                    // Cập nhật lại cartMap và lưu vào SharedPreferences cho nhà cung cấp hiện tại
                    if (!cartList.isEmpty()) {
                        int supplierId = cartList.get(0).getSupplierId(); // Lấy supplierId từ giỏ hàng
                        updateCartMap(supplierId, cartList); // Cập nhật giỏ hàng của supplierId vào SharedPreferences
                    }
                }
            });
            // Cập nhật giá và số lượng ban đầu
            updateCartList();
        } else {
            // Xử lý khi cartList rỗng hoặc null
            Log.d("CartList", "No items in cart");
        }
    }

    public double calculateShippingFee(double distance) {
        double baseFee = 20000; // Mức phí cơ bản cho khoảng cách ngắn
        double extraFeePerKm = 8000; // Phí thêm cho mỗi km sau một khoảng cách nhất định
        double thresholdDistance = 3; // Khoảng cách giới hạn cho mức phí cơ bản

        if (distance <= thresholdDistance) {
            return baseFee;
        } else {
            // Tính toán và làm tròn kết quả tới số nguyên gần nhất
            double result = baseFee + (distance - thresholdDistance) * extraFeePerKm;
            return Math.round(result);
        }
    }
    public double getShippingFee() {
        return shippingFee;
    }

    // Phương thức cập nhật lại tổng số tiền và số lượng sau khi thay đổi trong giỏ hàng
    private void updateCartList() {
        totalAmount = 0;
        int totalItems = 0;

        for (Menu item : cartList) {
            totalAmount += item.getPrice() * item.getQuantity();
            totalItems += item.getQuantity();
        }

        // Cập nhật UI của Activity với thông tin tổng tiền và tổng số lượng
        TextView totalAmountView = findViewById(R.id.textViewTotal);
        TextView totalItemsView = findViewById(R.id.textViewItemAmount);

        totalAmountView.setText(String.format("%,.0fđ", totalAmount));
        totalItemsView.setText(String.format("%d món", totalItems));

        // Cập nhật totalPrice với phí ship
        totalPrice = totalAmount + shippingFee - voucherAmount;
        updateTotalPrice(totalPrice); // Cập nhật giá trị mới của totalPrice vào UI
    }

    public void updateTotalPrice(double newTotalPrice) {
        Log.d("OrderActivity", "Updating total price in Activity: " + newTotalPrice);
        totalPrice = newTotalPrice;
        TextView totalPriceTextView = findViewById(R.id.textViewTotal);
        totalPriceTextView.setText(String.format("Tổng: %,.0fđ", totalPrice));
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Lưu dữ liệu giỏ hàng vào SharedPreferences khi Activity bị tạm dừng


    }
    // Phương thức cập nhật lại cartMap và lưu vào SharedPreferences cho một supplierId cụ thể
    public void updateCartMap(int supplierId, List<Menu> updatedCartList) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lấy cartMap hiện tại từ SharedPreferences
        String json = sharedPreferences.getString("cart_map", null);
        Type type = new TypeToken<HashMap<Integer, List<Menu>>>() {}.getType();
        HashMap<Integer, List<Menu>> cartMap = new Gson().fromJson(json, type);

        // Nếu cartMap chưa có dữ liệu, khởi tạo mới
        if (cartMap == null) {
            cartMap = new HashMap<>();
        }
// Cập nhật giỏ hàng cho supplierId hiện tại
        if (updatedCartList.isEmpty()) {
            // Nếu giỏ hàng trống, xóa supplierId khỏi cartMap
            cartMap.remove(supplierId);
        } else {
            // Cập nhật lại giỏ hàng cho supplierId hiện tại
            cartMap.put(supplierId, updatedCartList);
        }


        // Lưu cartMap đã được cập nhật vào SharedPreferences
        String updatedJson = new Gson().toJson(cartMap);
        editor.putString("cart_map", updatedJson);
        editor.apply();
    }


//    // Phương thức để lấy giỏ hàng từ SharedPreferences
//    private List<Menu> getCartItemsFromSharedPreferences() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
//        String json = sharedPreferences.getString("cart_map", null);
//
//        if (json != null) {
//            Type type = new TypeToken<HashMap<Integer, List<Menu>>>() {}.getType();
//            HashMap<Integer, List<Menu>> cartMap = new Gson().fromJson(json, type);
//
//            List<Menu> allItems = new ArrayList<>();
//            for (List<Menu> items : cartMap.values()) {
//                allItems.addAll(items);
//            }
//            return allItems;
//        } else {
//            return new ArrayList<>();
//        }
//    }
    private void createOrder() throws JSONException {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id

        // Lấy cartMap từ SharedPreferences
        Gson gson = new Gson();
        String cartMapJson = sharedPreferences.getString("cart_map", null);
        Type type = new TypeToken<HashMap<Integer, List<Menu>>>() {}.getType();
        HashMap<Integer, List<Menu>> cartMap = gson.fromJson(cartMapJson, type);

        // Lấy giá trị từ cartList và textViewDeliveryAddress
        List<OrderRequest> orderRequests = new ArrayList<>();
        for (Menu item : cartList) {
            OrderRequest orderRequest = new OrderRequest(item.getQuantity(), item.getId());
            orderRequests.add(orderRequest);
        }

        // Lấy pickup_location từ textViewDeliveryAddress
        String pickupLocation = textViewDeliveryAddress.getText().toString();

        if(Objects.equals(address, "")){
            Toast.makeText(this, "Vui lòng chọn địa chỉ nhận đơn", Toast.LENGTH_SHORT).show();
            return; // Ngưng thực hiện hàm nếu không có phương thức thanh toán
        }
        if (Objects.equals(paymentMethod, "")) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return; // Ngưng thực hiện hàm nếu không có phương thức thanh toán
        }
        // Lấy supplierId từ một item trong cartList
        int supplierId = cartList.get(0).getSupplierId();

        // Tạo đối tượng cho order
        JSONObject orderData = new JSONObject();
        JSONArray orderRequestsArray = new JSONArray();
        for (OrderRequest orderRequest : orderRequests) {
            JSONObject orderRequestObj = new JSONObject();
            orderRequestObj.put("quantity", orderRequest.getQuantity());
            orderRequestObj.put("food_item_id", orderRequest.getFoodItemId());
            orderRequestsArray.put(orderRequestObj);
        }

        JSONObject foodOrderDTO = new JSONObject();
        foodOrderDTO.put("order_time", getCurrentTime()); // Cập nhật lại thời gian thực
        foodOrderDTO.put("pickup_time", getCurrentTime()); // Cập nhật lại thời gian thực
        foodOrderDTO.put("pickup_location", pickupLocation);
        foodOrderDTO.put("status", "đang giao"); // Hoặc trạng thái khác nếu có
        foodOrderDTO.put("payment_method", paymentMethod);
        foodOrderDTO.put("payment_status", 2); // Thanh toán chưa hoàn thành
        foodOrderDTO.put("user_id", userId); // Cập nhật user_id thực tế
        foodOrderDTO.put("supplier_id", supplierId);
        foodOrderDTO.put("shipping_fee", shippingFee);
        foodOrderDTO.put("discount", voucherAmount);

        orderData.put("orderRequests", orderRequestsArray);
        orderData.put("foodOrderDTO", foodOrderDTO);


        // Gọi API tạo order bằng Volley
        String url = ApiEndpoints.CREATE_ORDER;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, orderData,
                response -> {

                    // Xử lý khi tạo order thành công
                    Toast.makeText(OrderActivity.this, "Order created successfully", Toast.LENGTH_SHORT).show();
                    updateSupplierCash(supplierId, totalPrice, token);
                    // Lấy id của order mới được tạo
                    JSONObject foodOrderJson = response.optJSONObject("foodOrder");

                    int id = foodOrderJson.optInt("id");
                    double totalPriceItem = foodOrderJson.optDouble("totalPrice");
                    int totalItems = foodOrderJson.optInt("totalItems");
                    String orderStatus = foodOrderJson.optString("status");
                    long orderTime = foodOrderJson.optLong("orderTime");
                    String formattedOrderTime = formatOrderTime(orderTime);
                    String foodImage = foodOrderJson.optString("imgUrl");
                    String restaurantName = foodOrderJson.optString("restaurantName");
                    double shippingFee = foodOrderJson.optDouble("shippingFee");

                    if (selectedPromotion != null) {
                        applyPromotion(id, selectedPromotion.getId(), token);
                    }
                    totalPriceOrder = totalPriceItem+ shippingFee - voucherAmount;

                    // Tạo đối tượng FoodOrder
                    FoodOrder foodOrder = new FoodOrder(id, foodImage, restaurantName, totalPriceItem, totalItems, orderStatus, formattedOrderTime);
                    foodOrder.setShippingFee(shippingFee);
// Lấy giá trị orderTime
                    long orderTimeMillis = foodOrderJson.optLong("orderTime");

                    int orderId = 0;
                    if (foodOrderJson != null) {
                        orderId = foodOrderJson.optInt("id"); // Lấy id từ foodOrder

                        contentBank = "FOODPT " + orderId; // Cập nhật contentBank với id của order mới
                    }



                    // Xóa các item trong cartMap dựa trên supplierId
                    if (cartMap.containsKey(supplierId)) {
                        cartMap.remove(supplierId);
                    }
                    // Cập nhật lại cartMap vào SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String updatedCartMapJson = gson.toJson(cartMap);
                    editor.putString("cart_map", updatedCartMapJson);
                    editor.apply();
                    if (paymentMethod.equals("cash")) {
                        Intent intent = new Intent(OrderActivity.this, CreateOrderSuccessActivity.class);
//                        intent.putExtra("foodOrder", foodOrder);
                        intent.putExtra("orderId", id);

                        startActivity(intent);
                    } else if (paymentMethod.equals("transfer")) {
                        Intent intent = new Intent(OrderActivity.this, PaymentDetailActivity.class);

                        updateGoogleSheet(totalPrice,contentBank);
                        intent.putExtra("totalPrice",totalPriceOrder);
                        intent.putExtra("contentBank", contentBank);
                        intent.putExtra("orderTime", orderTimeMillis);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }
                    },
                error -> {
                    // Xử lý khi có lỗi
                    Toast.makeText(OrderActivity.this, "Error creating order", Toast.LENGTH_SHORT).show();
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        if (token != null) {
                            headers.put("Authorization", "Bearer " + token);
                        }
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

        // Thêm request vào RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    public void updateSupplierCash(int supplierId, double cash, String token) {
        // URL API với tham số cash truyền qua URL
        String url = ApiEndpoints.UPDATE_SUPPLIER_CASH+ "/" + supplierId + "?cash=" + cash;

        // Tạo một request PUT
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Xử lý kết quả trả về từ server
                        Log.d("API_RESPONSE", "Response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi nếu có
                        Log.e("API_ERROR", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token); // Thêm token nếu có
                return headers;
            }
        };

        // Thêm request vào hàng đợi
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    private void applyPromotion(int foodOrderId, long promotionId, String token) {
        // Tạo đối tượng cho API apply promotion
        String url = ApiEndpoints.APPLY_PROMOTION + "?foodOrderId=" + foodOrderId + "&promotionId=" + promotionId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Xử lý khi áp dụng khuyến mãi thành công
                    Toast.makeText(OrderActivity.this, "Promotion applied successfully", Toast.LENGTH_SHORT).show();
                    // Xử lý phản hồi từ API nếu cần
                },
                error -> {
                    // Xử lý khi có lỗi
                    Toast.makeText(OrderActivity.this, "Error applying promotion", Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };


        // Thêm request vào RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void updateGoogleSheet(double totalPrice, String contentBank) {
        new Thread(() -> {
            try {
                // Lấy access token từ GoogleSheetsService
                String accessToken = new GoogleSheetsService(OrderActivity.this).getAccessToken();

                // Tạo ValueRange
                ValueRange valueRange = new ValueRange();
                valueRange.setRange("qr!B6:B7");  // Dải dữ liệu mà bạn muốn cập nhật
                valueRange.setValues(Arrays.asList(
                        Arrays.asList(totalPrice),   // Giá trị ở ô B6
                        Arrays.asList(contentBank)   // Giá trị ở ô B7
                ));

                // Chuyển đổi ValueRange thành JSONObject
                JSONObject body = new JSONObject();
                body.put("range", valueRange.getRange());
                body.put("values", new JSONArray(valueRange.getValues()));

                // URL API Google Sheets
                String url = "https://sheets.googleapis.com/v4/spreadsheets/1jwyONxe8utnzD9rgKu7urVQxkS0q78mr7QCbYJ9p9IM/values/qr!B6:B7?valueInputOption=RAW";

                // Gọi API sử dụng Volley
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, body,
                        response -> {
                            // Xử lý khi thành công
                            Log.d("API", "Success: " + response.toString());
                        },
                        error -> {
                            // Xử lý khi có lỗi
                            Log.e("API", "Error: " + error.toString());
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Bearer " + accessToken); // Thêm accessToken vào headers
                        return headers;
                    }
                };

                // Thêm request vào RequestQueue
                RequestQueue requestQueue = Volley.newRequestQueue(OrderActivity.this);
                requestQueue.add(jsonObjectRequest);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("API", "Error: " + e.getMessage());
            }
        }).start();
    }

    public double calculateVoucherAmount(PromotionResponse promotionResponse) {
        double voucherAmount = 0;
        voucherAmount = promotionResponse.getFixedDiscountAmount() + promotionResponse.getDiscountPercentage()*totalAmount;
        return voucherAmount;

    }

    // Hàm để lấy thời gian hiện tại dưới dạng chuỗi ISO-8601
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return sdf.format(new Date());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra requestCode và resultCode
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            // Lấy địa chỉ được chọn từ MapsActivity
            if (data != null) {
                String selectedAddress = data.getStringExtra("selected_address");
                // Cập nhật địa chỉ đã chọn vào textViewDeliveryAddress
                textViewDeliveryAddress.setText(selectedAddress);

                 distance = data.getDoubleExtra("distance", 0);
                // Tính lại phí vận chuyển dựa trên khoảng cách mới
                shippingFee = calculateShippingFee(distance);
                // Cập nhật phí vận chuyển vào textViewShippingFee
                textViewShippingFee.setText(String.format("%,.0fđ", shippingFee));

                updateCartList();
            }
        }
        if (requestCode == REQUEST_CODE_APPLY_PROMOTION && resultCode == RESULT_OK) {
            isFromApplyPromotion = true; // Đánh dấu rằng bạn đang quay lại từ trang ApplyPromotion
            if (data != null) {
                 selectedPromotion = data.getParcelableExtra("promotion");
                // Xử lý promotionId tại đây
                // Ví dụ: cập nhật giao diện hoặc thông báo cho người dùng
                voucherAmount = calculateVoucherAmount(selectedPromotion);
                updateCartList();
                Toast.makeText(this, "Voucher đã áp dụng: " + selectedPromotion.getCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm định dạng thời gian từ timestamp
    private String formatOrderTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_cart_list", (Serializable) cartList); // Trả lại giỏ hàng đã cập nhật
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }


}