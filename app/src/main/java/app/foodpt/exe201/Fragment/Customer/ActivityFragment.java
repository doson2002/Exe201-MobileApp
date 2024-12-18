package app.foodpt.exe201.Fragment.Customer;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.Adapter.FoodOrderAdapter;
import app.foodpt.exe201.DTO.FoodOrder;
import app.foodpt.exe201.Decorations.ItemDecorationDividerForActivity;
import app.foodpt.exe201.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityFragment extends Fragment {


    private RecyclerView recyclerView;
    private FoodOrderAdapter adapter;
    private List<FoodOrder> foodOrderList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        View rootView = view.findViewById(R.id.root_view);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), 0);
                return insets.consumeSystemWindowInsets();
            }
        });
        // Initialize the list and adapter
        foodOrderList = new ArrayList<>();
        adapter = new FoodOrderAdapter(getActivity(), foodOrderList);

        // Call the API to fetch orders
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        fetchOrdersFromApi(userId, jwtToken);
        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
// Add a divider between RecyclerView items
        // Add a custom divider with margin
        int marginStart = 50; // Ví dụ: bạn có thể đặt giá trị này phù hợp với yêu cầu
        int marginEnd = 50; // Ví dụ: bạn có thể đặt giá trị này phù hợp với yêu cầu
        recyclerView.addItemDecoration(new ItemDecorationDividerForActivity(getActivity(), R.drawable.divider, marginStart, marginEnd));
        return view;
    }

    private void fetchOrdersFromApi(int userId, String jwtToken) {
        String url = ApiEndpoints.GET_ORDER_BY_USER_ID + "/" + userId;  // Thay thế bằng URL API của bạn

        // Initialize a request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Create a JsonArrayRequest
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject order = response.getJSONObject(i);

                                // Parse the JSON object
                                FoodOrder foodOrder = new FoodOrder(
                                        order.getInt("id"),
                                        order.getString("supplier_image_url"),        // URL ảnh của món ăn
                                        order.getString("supplier_name"),   // Tên nhà hàng
                                        order.getDouble("total_price"),       // Tổng giá trị đơn hàng
                                        order.getInt("total_items"),          // Tổng số món
                                        order.getString("status"),      // Trạng thái đơn hàng
                                        formatOrderTime(order.getLong("order_time"))         // Thời gian đặt hàng
                                );


                                // Add to the list
                                foodOrderList.add(foodOrder);
                            }

                            // Notify the adapter that data has changed
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (jwtToken != null) {
                    headers.put("Authorization", "Bearer " + jwtToken);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }
    // Hàm định dạng lại thời gian từ timestamp
    private String formatOrderTime(long timestamp) {
        // Định dạng ngày/tháng/năm, giờ:phút
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date(timestamp); // Chuyển đổi từ giây sang milliseconds
        return sdf.format(date);
    }

}