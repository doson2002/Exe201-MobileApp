package com.example.exe201.Fragment.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.FoodItemTopSoldAdapter;
import com.example.exe201.Adapter.SupplierTypeAdapter;

import com.example.exe201.AddFoodItemActivity;
import com.example.exe201.Adapter.TopSupplierRatingAdapter;
import com.example.exe201.ChatActivity;
import com.example.exe201.DTO.FoodItemTopSold;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.DTO.SupplierType;
import com.example.exe201.FoodItemGroupedBySupplierActivity;
import com.example.exe201.ProfileActivity;
import com.example.exe201.R;
import com.example.exe201.SupplierForCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewTopSold;
    private FoodItemTopSoldAdapter foodItemTopSoldAdapter;
    private List<FoodItemTopSold> foodItemTopSoldList = new ArrayList<>();

    private AutoCompleteTextView searchAutoComplete;
    private Button btnSearch;
    private RecyclerView recyclerViewSupplierTypes;
    private SupplierTypeAdapter supplierTypeAdapter;
    private List<String> suggestions = new ArrayList<>();
    private List<SupplierType> supplierTypeList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private RecyclerView topSupplierRecyclerView;
    private TopSupplierRatingAdapter topSupplierAdapter;
    private List<SupplierInfo> supplierList;
    private int currentPage = 0;
    private int pageSize = 5; // Kích thước mỗi trang
    private boolean isLoading = false;
    private boolean isLastPage = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_page, container, false);

        recyclerViewTopSold = view.findViewById(R.id.recyclerBestFood);
        recyclerViewTopSold.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));

        // Call API
        fetchFoodItemsTopSold();
        topSupplierRecyclerView = view.findViewById(R.id.recyclerViewSuppliers);
        // Thiết lập LayoutManager với hướng nằm ngang
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topSupplierRecyclerView.setLayoutManager(layoutManager);

        supplierList = new ArrayList<>();
        topSupplierAdapter = new TopSupplierRatingAdapter(supplierList, getActivity());
        topSupplierRecyclerView.setAdapter(topSupplierAdapter);
        // Gọi API để lấy dữ liệu
        fetchSuppliers(currentPage, pageSize);
        // Thiết lập listener cho việc cuộn
        topSupplierRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !isLoading && !isLastPage) {
                    currentPage++;
                    fetchSuppliers(currentPage, pageSize);
                }
            }
        });

        // Ánh xạ các thành phần UI
        searchAutoComplete = view.findViewById(R.id.searchAutoComplete);
        btnSearch = view.findViewById(R.id.btnSearch);
        // Khởi tạo ArrayAdapter để hiển thị gợi ý

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
        searchAutoComplete.setAdapter(adapter);

        // Lắng nghe sự thay đổi text trong AutoCompleteTextView để hiển thị gợi ý
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Gọi hàm để lấy gợi ý từ API
                getSuggestions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Xử lý khi người dùng chọn một gợi ý
        searchAutoComplete.setOnItemClickListener((adapterView, view1, position, id) -> {
            String selectedKeyword = adapter.getItem(position);
            // Chuyển sang FoodItemGroupedBySupplierActivity với từ khóa đã chọn
            startSearchActivity(selectedKeyword);
        });

        searchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Nhận từ khóa từ AutoCompleteTextView
                    String keyword = searchAutoComplete.getText().toString();

                    // Chuyển sang FoodItemGroupedBySupplierActivity với từ khóa người dùng nhập
                    startSearchActivity(keyword);
                    return true;
                }
                return false;
            }
        });

        // Xử lý khi người dùng nhấn nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String keyword = searchAutoComplete.getText().toString();
            // Chuyển sang FoodItemGroupedBySupplierActivity với từ khóa người dùng nhập
            startSearchActivity(keyword);
        });

        // Tìm RecyclerView từ view
        recyclerViewSupplierTypes = view.findViewById(R.id.recyclerSupplierTypes);
        // Cài đặt Layout Manager cho RecyclerView
        LinearLayoutManager supplierTypeLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSupplierTypes.setLayoutManager(supplierTypeLayoutManager);

        // Truyền listener cho SupplierTypeAdapter
        supplierTypeAdapter = new SupplierTypeAdapter(supplierTypeList, getContext(), new SupplierTypeAdapter.OnSupplierTypeClickListener() {
            @Override
            public void onSupplierTypeClick(SupplierType supplierType) {
                // Lấy supplierTypeId được chọn và gọi phương thức loadFoodItemsBySupplierTypeId
                int supplierTypeId = supplierType.getId(); // Lấy ID của SupplierType được chọn
                Intent intent = new Intent(getActivity(), SupplierForCustomer.class); // Thay bằng activity của bạn
                intent.putExtra("supplierTypeId", supplierTypeId);
                startActivity(intent);
                // Áp dụng hoạt ảnh chuyển trang
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            }
        });

        recyclerViewSupplierTypes.setAdapter(supplierTypeAdapter);

        // Tìm các thành phần UI như Button, ImageView ở đây
        Button test = view.findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChatActivity.class); // Thay bằng activity của bạn
                startActivity(intent);
            }
        });

        ImageView favoriteIcon = view.findViewById(R.id.favorite_icon);
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class); // Chuyển Activity
                startActivity(intent);
            }
        });

        loadAllSupplierTypes();


        return view;
    }
    private void fetchSuppliers(int page, int size) {
        isLoading = true;
        String url = ApiEndpoints.GET_TOP_RATING+ "?page=" + page + "&size=" + size;  // Địa chỉ API của bạn
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Tạo một JsonObjectRequest để gọi API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray contentArray = response.getJSONArray("content");

                            for (int i = 0; i < contentArray.length(); i++) {
                                JSONObject supplierObject = contentArray.getJSONObject(i).getJSONObject("supplier");
                                SupplierInfo supplier = new SupplierInfo();

                                supplier.setId(supplierObject.getInt("id"));
                                supplier.setRestaurantName(supplierObject.getString("restaurantName"));
                                supplier.setImgUrl(supplierObject.getString("imgUrl"));
                                supplier.setTotalStarRating(supplierObject.getDouble("totalStarRating"));
                                supplier.setTotalReviewCount(supplierObject.getInt("totalReviewCount"));

                                supplierList.add(supplier);
                            }

                            // Cập nhật adapter
                            topSupplierAdapter.notifyDataSetChanged();

                            // Kiểm tra xem đã đạt đến trang cuối chưa
                            isLastPage = currentPage == response.getInt("totalPages") - 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            isLoading = false;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Log.e("API Error", error.toString());
                        isLoading = false;
                    }
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
        // Thêm request vào queue
        queue.add(jsonObjectRequest);
    }
    private void fetchFoodItemsTopSold() {
        String url = ApiEndpoints.GET_FOOD_ITEMS_TOP_SOLD;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray contentArray = response.getJSONArray("content");
                        for (int i = 0; i < contentArray.length(); i++) {
                            JSONObject foodItemJson = contentArray.getJSONObject(i);
                            FoodItemTopSold foodItemTopSold = new FoodItemTopSold();
                            foodItemTopSold.setFoodItemId(foodItemJson.getInt("foodId"));
                            foodItemTopSold.setName(foodItemJson.getString("name"));
                            foodItemTopSold.setQuantitySold(foodItemJson.getInt("quantitySold"));

                            JSONObject supplierInfoJson = foodItemJson.getJSONObject("supplierInfo");
                            SupplierInfo supplierInfo = new SupplierInfo();
                            supplierInfo.setId(supplierInfoJson.getInt("id"));
                            supplierInfo.setRestaurantName(supplierInfoJson.getString("restaurant_name"));
                            supplierInfo.setImgUrl(supplierInfoJson.getString("img_url"));

                            foodItemTopSold.setSupplierInfo(supplierInfo);
                            foodItemTopSoldList.add(foodItemTopSold);
                        }

                        foodItemTopSoldAdapter = new FoodItemTopSoldAdapter(getContext(), foodItemTopSoldList);
                        recyclerViewTopSold.setAdapter(foodItemTopSoldAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                    error.printStackTrace();
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

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }
    // Hàm chuyển sang FoodItemGroupedBySupplierActivity với từ khóa tìm kiếm
    private void startSearchActivity(String keyword) {
        Intent intent = new Intent(getActivity(), FoodItemGroupedBySupplierActivity.class);
        intent.putExtra("keyword", keyword); // Truyền từ khóa tìm kiếm
        startActivity(intent);
    }

    private void getSuggestions(String query) {
        String url = ApiEndpoints.GET_FOOD_ITEM_NAME_FOR_SUGGESTION + query;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        // Sử dụng Volley hoặc thư viện khác để gửi request
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // In ra dữ liệu trả về từ API
                        Log.d("API Response", response.toString());

                        try {
                            suggestions.clear();  // Xóa gợi ý cũ
                            for (int i = 0; i < response.length(); i++) {
                                String suggestion = response.getString(i);
                                suggestions.add(suggestion);  // Thêm gợi ý mới
                            }

                            // Loại bỏ phần tử trùng lặp
                            Set<String> uniqueSuggestions = new HashSet<>(suggestions);
                            suggestions.clear();
                            suggestions.addAll(uniqueSuggestions);
                            Log.d("Suggestions", suggestions.toString());
                            adapter.notifyDataSetChanged();  // Cập nhật Adapter
                            searchAutoComplete.showDropDown();  // Hiển thị danh sách gợi ý
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi nếu có
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        // Thêm request vào hàng đợi
        queue.add(jsonArrayRequest);
    }


    private void loadAllSupplierTypes() {
        String url = ApiEndpoints.GET_ALL_SUPPLIER_TYPES; // Thay thế bằng URL API của bạn

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            supplierTypeList.clear(); // Xóa dữ liệu cũ trong danh sách mà Adapter đang sử dụng                            for (int i = 0; i < response.length(); i++) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject foodTypeJson = response.getJSONObject(i);
                                int id = foodTypeJson.getInt("id");
                                String typeName = foodTypeJson.getString("typeName");
                                String imgUrl = foodTypeJson.getString("imgUrl");
                                supplierTypeList.add(new SupplierType(id, typeName, imgUrl)); // Thêm trực tiếp vào foodTypeList
                            }
                            supplierTypeAdapter.notifyDataSetChanged(); // Thông báo cho Adapter rằng dữ liệu đã thay đổi
                            // Cập nhật dữ liệu cho spinner
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing supplier types data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Log.e("VolleyError", "Timeout Error: " + error.toString());
                        } else if (error instanceof NoConnectionError) {
                            Log.e("VolleyError", "No Connection Error: " + error.toString());
                        } else if (error instanceof AuthFailureError) {
                            Log.e("VolleyError", "Authentication Failure Error: " + error.toString());
                        } else if (error instanceof ServerError) {
                            Log.e("VolleyError", "Server Error: " + error.toString());
                        } else if (error instanceof NetworkError) {
                            Log.e("VolleyError", "Network Error: " + error.toString());
                        } else if (error instanceof ParseError) {
                            Log.e("VolleyError", "Parse Error: " + error.toString());
                        } else {
                            Log.e("VolleyError", "Unknown Error: " + error.toString());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Thêm token vào header
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000, // Thời gian chờ tối đa (tính bằng milliseconds)
                1, // Số lần thử lại
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(jsonArrayRequest);
    }



}