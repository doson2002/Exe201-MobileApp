package app.foodpt.exe201.Fragment.Customer;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.BannerAdapter;
import app.foodpt.exe201.Adapter.BannerAdapter1;
import app.foodpt.exe201.Adapter.FoodItemTopSoldAdapter;
import app.foodpt.exe201.Adapter.SupplierInfoAdapter;
import app.foodpt.exe201.Adapter.SupplierTypeAdapter;

import app.foodpt.exe201.Adapter.TopSupplierRatingAdapter;
import app.foodpt.exe201.DTO.Banner;
import app.foodpt.exe201.DTO.FoodItemTopSold;
import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.DTO.SupplierType;
import app.foodpt.exe201.FoodItemGroupedBySupplierActivity;
import app.foodpt.exe201.ProfileActivity;
import app.foodpt.exe201.R;
import app.foodpt.exe201.ShowFoodItemActivity;
import app.foodpt.exe201.SupplierForCustomer;
import app.foodpt.exe201.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;

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

    private boolean isLocationPermissionGranted = false;
    private RecyclerView recyclerBanner;
    private BannerAdapter bannerAdapter;
    private BannerAdapter1 bannerAdapter1;
    private List<Banner> bannerList;
    private List<Banner> bannerList1;

    private ViewPager2 viewPager, viewPager1;
    private Handler handler, handler1;
    private Runnable runnable, runnable1;

    private SupplierInfoAdapter supplierInfoAdapter;
    private List<SupplierInfo> supplierInfoList = new ArrayList<>();
    private RecyclerView recyclerViewSuppliers;

    private int currentPage = 0;
    private int pageSize = 5; // Kích thước mỗi trang
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_page, container, false);
        // Khởi tạo fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Find the ImageView by ID
        ImageView imageViewIcon1 = view.findViewById(R.id.imageViewIcon1);
        ImageView imageViewIcon2 = view.findViewById(R.id.imageViewIcon2);
        ImageView imageViewIcon3 = view.findViewById(R.id.imageViewIcon3);
        ImageView imageViewIcon4 = view.findViewById(R.id.imageViewIcon4);
        // Load the scale animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale);
        // Start the animation
        imageViewIcon1.startAnimation(scaleAnimation);
        imageViewIcon2.startAnimation(scaleAnimation);
        imageViewIcon3.startAnimation(scaleAnimation);
        imageViewIcon4.startAnimation(scaleAnimation);

        // handle load banner
        viewPager = view.findViewById(R.id.viewPager);
        bannerList = new ArrayList<>();
        // Giả sử bạn đã gọi API và có dữ liệu trong bannerList
        loadBanners(1);
        // Hàm để tải dữ liệu từ API
        bannerAdapter = new BannerAdapter(requireContext(), bannerList);
        viewPager.setAdapter(bannerAdapter);

        // Tự động chuyển đổi banner
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem == bannerList.size() - 1) {
                    viewPager.setCurrentItem(0, false);
                } else {
                    viewPager.setCurrentItem(currentItem + 1, true);
                }
                handler.postDelayed(this, 5000); // Thay đổi 3000 thành khoảng thời gian bạn muốn
            }
        };
        handler.postDelayed(runnable, 5000); // Bắt đầu tự động chuyển đổi sau 3 giây


        viewPager1 = view.findViewById(R.id.viewPager1);
        bannerList1 = new ArrayList<>();
        // Giả sử bạn đã gọi API và có dữ liệu trong bannerList
        loadBanners(2);
        // Hàm để tải dữ liệu từ API
        bannerAdapter1 = new BannerAdapter1(requireContext(), bannerList1);
        viewPager1.setAdapter(bannerAdapter1);

        // Tự động chuyển đổi banner
        handler1 = new Handler(Looper.getMainLooper());
        runnable1 = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager1.getCurrentItem();
                if (currentItem == bannerList1.size() - 1) {
                    viewPager1.setCurrentItem(0, false);
                } else {
                    viewPager1.setCurrentItem(currentItem + 1, true);
                }
                handler1.postDelayed(this, 5000); // Thay đổi 3000 thành khoảng thời gian bạn muốn
            }
        };
        handler1.postDelayed(runnable1, 5000); // Bắt đầu tự động chuyển đổi sau 3 giây


        recyclerViewTopSold = view.findViewById(R.id.recyclerBestFood);
        recyclerViewTopSold.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Gọi hàm để lấy gợi ý từ API
                getSuggestions(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
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


        ImageView favoriteIcon = view.findViewById(R.id.favorite_icon);
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class); // Chuyển Activity
                startActivity(intent);
            }
        });

        loadAllSupplierTypes();

        recyclerViewSuppliers = view.findViewById(R.id.recyclerViewSupplier);

        supplierInfoAdapter = new SupplierInfoAdapter(supplierInfoList, requireContext(), new SupplierInfoAdapter.OnSupplierInfoClickListener() {
            @Override
            public void onSupplierInfoClick(SupplierInfo supplierInfo) {
                // Lưu supplierInfo vào SharedPreferences
                Utils.saveSupplierInfo(requireContext(), supplierInfo);
                Intent intent = new Intent(requireActivity(), ShowFoodItemActivity.class);
                intent.putExtra("supplier", supplierInfo); // Truyền Supplier qua Intent
                startActivity(intent);
            }
        });
        recyclerViewSuppliers.setAdapter(supplierInfoAdapter);


        return view;
    }



    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa cấp quyền, yêu cầu quyền truy cập vị trí
            requestLocationPermission();

        } else {
            // Nếu đã có quyền, tiếp tục lấy vị trí
            getCurrentLocation();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Nếu người dùng trước đó đã từ chối quyền, hiển thị dialog giải thích
            showPermissionExplanationDialog();
        } else {
            // Yêu cầu quyền truy cập vị trí
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, set biến trạng thái và reload Fragment
                isLocationPermissionGranted = true;
                reloadFragment();
            } else {
                // Quyền bị từ chối, hiển thị dialog cảnh báo
                showPermissionDeniedDialog();
            }


        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isLocationPermissionGranted) {
            // Gọi hàm lấy vị trí khi quyền đã được cấp
            getCurrentLocation();
            // Đặt lại biến trạng thái để tránh gọi lại khi không cần thiết
            isLocationPermissionGranted = false;
        }
    }

    private void reloadFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Yêu cầu quyền truy cập vị trí")
                .setMessage("Ứng dụng yêu cầu quyền truy cập vị trí để tiếp tục sử dụng. Vui lòng cấp quyền.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Người dùng từ chối, hiển thị dialog không cho sử dụng app
                    showPermissionDeniedDialog();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Bạn phải cấp quyền truy cập vị trí để sử dụng ứng dụng. Nếu không cấp quyền, bạn sẽ không thể sử dụng ứng dụng.")
                .setPositiveButton("Cài đặt", (dialog, which) -> {
                    // Chuyển đến cài đặt ứng dụng
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false) // Không cho phép hủy dialog
                .show();
    }
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000); // Thời gian giữa các lần cập nhật vị trí (ms)
            locationRequest.setFastestInterval(5000); // Thời gian nhanh nhất giữa các lần cập nhật vị trí (ms)

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("latitude", (float) latitude);
                        editor.putFloat("longitude", (float) longitude);
                        editor.apply();

                        // Gọi hàm load dữ liệu các nhà cung cấp sau khi có tọa độ
                        loadAllSuppliers(currentPage, pageSize);

                        // Ngừng cập nhật vị trí sau khi đã lấy tọa độ
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                }
            };

            // Yêu cầu cập nhật vị trí
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            // Yêu cầu quyền truy cập vị trí nếu chưa được cấp
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    private void loadBanners(int bannerType) {
        String url = ApiEndpoints.GET_ALL_BANNER_ACTIVED + "?bannerType=" + bannerType; // Thay đổi thành URL của API
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (bannerType == 1) {
                            // Nếu bannerType là 1, xử lý với BannerAdapter
                            bannerList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    int id = response.getJSONObject(i).getInt("id");
                                    String imageUrl = response.getJSONObject(i).getString("imageUrl");
                                    int position = response.getJSONObject(i).getInt("i");

                                    Banner banner = new Banner(id, imageUrl, position);
                                    bannerList.add(banner);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            bannerAdapter.notifyDataSetChanged();
                        } else {
                            // Nếu bannerType không phải 1, xử lý với BannerAdapter1
                            bannerList1.clear();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    int id = response.getJSONObject(i).getInt("id");
                                    String imageUrl = response.getJSONObject(i).getString("imageUrl");
                                    int position = response.getJSONObject(i).getInt("i");

                                    Banner banner1 = new Banner(id, imageUrl, position);
                                    bannerList1.add(banner1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            bannerAdapter1.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        requestQueue.add(jsonArrayRequest);
    }
    private void fetchSuppliers(int page, int size) {
        isLoading = true;
        String url = ApiEndpoints.GET_TOP_RATING+ "?page=" + page + "&size=" + size;  // Địa chỉ API của bạn
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        double userLatitude = sharedPreferences.getFloat("latitude", 0.0f);
        double userLongitude = sharedPreferences.getFloat("longitude", 0.0f);
        // Tạo một RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

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
                                double latitude = supplierObject.getDouble("latitude");
                                double longitude = supplierObject.getDouble("longitude");

                                supplier.setLatitude(latitude);
                                supplier.setLongitude(longitude);

                                // Tính khoảng cách
                                double distance = calculateDistance(userLatitude, userLongitude, latitude, longitude);
                                supplier.setDistance(distance);
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
        double userLatitude = sharedPreferences.getFloat("latitude", 0.0f);
        double userLongitude = sharedPreferences.getFloat("longitude", 0.0f);
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
                            foodItemTopSold.setImgUrl(foodItemJson.getString("imgUrl"));


                            JSONObject supplierInfoJson = foodItemJson.getJSONObject("supplierInfo");
                            SupplierInfo supplierInfo = new SupplierInfo();
                            supplierInfo.setId(supplierInfoJson.getInt("id"));
                            supplierInfo.setRestaurantName(supplierInfoJson.getString("restaurant_name"));
                            supplierInfo.setImgUrl(supplierInfoJson.getString("img_url"));
                            supplierInfo.setTotalStarRating(supplierInfoJson.getDouble("total_star_rating"));
                            supplierInfo.setTotalReviewCount(supplierInfoJson.getInt("total_review_count"));
                            double latitude = supplierInfoJson.getDouble("latitude");
                            double longitude = supplierInfoJson.getDouble("longitude");

                            supplierInfo.setLatitude(latitude);
                            supplierInfo.setLongitude(longitude);

                            // Tính khoảng cách
                            double distance = calculateDistance(userLatitude, userLongitude, latitude, longitude);
                            supplierInfo.setDistance(distance);
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
        String url = ApiEndpoints.GET_ALL_SUPPLIER_TYPES +"/" +1; // Thay thế bằng URL API của bạn

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

    private void loadAllSuppliers(int page, int size) {
        String url = ApiEndpoints.GET_ALL_SUPPLIERS + "?page=" + page + "&size=" + size; // Thêm phân trang vào URL
        isLoading = true;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        double userLatitude = sharedPreferences.getFloat("latitude", 0.0f);
        double userLongitude = sharedPreferences.getFloat("longitude", 0.0f);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy danh sách suppliers từ JSON response
                            JSONArray suppliersArray = response.getJSONArray("suppliers");
                            supplierInfoList.clear();

                            for (int i = 0; i < suppliersArray.length(); i++) {
                                JSONObject suppliersJson = suppliersArray.getJSONObject(i);
                                int id = suppliersJson.getInt("id");
                                String restaurantName = suppliersJson.getString("restaurant_name");
                                String imageUrl = suppliersJson.optString("img_url", ""); // sử dụng optString để tránh lỗi null
                                double totalStarRating = suppliersJson.getDouble("total_star_rating");
                                int totalReviewCount = suppliersJson.getInt("total_review_count");
                                double latitude = suppliersJson.getDouble("latitude");
                                double longitude = suppliersJson.getDouble("longitude");
                                // Lấy close_time và open_time từ JSON
                                JSONArray closeTimeArray = suppliersJson.getJSONArray("close_time");
                                JSONArray openTimeArray = suppliersJson.getJSONArray("open_time");

                                // Chuyển đổi mảng thời gian thành chuỗi theo định dạng hh:mm:ss
                                String closeTime = formatTime(closeTimeArray);
                                String openTime = formatTime(openTimeArray);

                                // Tính khoảng cách
                                double distance = calculateDistance(userLatitude, userLongitude, latitude, longitude);
                                // Lấy thông tin SupplierType
                                JSONObject supplierTypeJson = suppliersJson.getJSONObject("supplier_type");
                                int supplierTypeId = supplierTypeJson.getInt("id");
                                String typeName = supplierTypeJson.getString("typeName");
                                String typeImgUrl = supplierTypeJson.getString("imgUrl");

                                SupplierType supplierType = new SupplierType(supplierTypeId, typeName, typeImgUrl);

                                // Thêm SupplierInfo vào danh sách
                                SupplierInfo supplierInfo = new SupplierInfo(id, restaurantName, imageUrl, totalStarRating, totalReviewCount, supplierType);
                                supplierInfo.setLatitude(latitude);
                                supplierInfo.setLongitude(longitude);
                                supplierInfo.setDistance(distance);
                                supplierInfo.setOpenTime(openTime);
                                supplierInfo.setCloseTime(closeTime);

                                supplierInfoList.add(supplierInfo);

                            }

                            // Cập nhật danh sách trong RecyclerView
                            supplierInfoAdapter.updateSupplierInfoList(supplierInfoList);

                            // Kiểm tra xem đã đạt đến trang cuối chưa
                            int totalPages = response.getInt("totalPages");
                            isLastPage = page >= totalPages - 1;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            isLoading = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching suppliers", error);
                        isLoading = false;
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

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kiểm tra quyền ngay khi View đã được tạo
        checkLocationPermission();
    }

    // Hàm để chuyển đổi mảng [giờ, phút] thành chuỗi hh:mm:ss
    private String formatTime(JSONArray timeArray) throws JSONException {
        int hour = timeArray.getInt(0);
        int minute = timeArray.getInt(1);

        // Trả về chuỗi theo định dạng hh:mm:ss
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute); // Thêm giây là 0 nếu không có giây
    }
    private double calculateDistance(double userLat, double userLng, double supplierLat, double supplierLng) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(supplierLat - userLat);
        double lonDistance = Math.toRadians(supplierLng - userLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(supplierLat)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to km

        return distance;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Dừng tự động chuyển đổi khi Activity bị hủy
    }

}