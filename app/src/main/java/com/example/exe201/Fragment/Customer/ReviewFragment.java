package com.example.exe201.Fragment.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.ReviewAdapter;
import com.example.exe201.CustomerReviewActivity;
import com.example.exe201.DTO.Rating;
import com.example.exe201.DTO.SupplierInfo;
import com.example.exe201.R;
import com.example.exe201.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Rating> ratingList;
    private RequestQueue requestQueue;
    private SupplierInfo supplierInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        reviewRecyclerView = rootView.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RatingBar ratingBarInput = rootView.findViewById(R.id.ratingBarInput);
        ratingBarInput.setRating(0);

        EditText messageInput = rootView.findViewById(R.id.editReviewText);

        ratingList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(ratingList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        SupplierInfo supplierInfoChose = Utils.getSupplierInfo(requireContext());
        int supplierId =  supplierInfoChose.getId();
                getRatingsBySupplierId(supplierId);
//        if (getArguments() != null) {
//            supplierInfo = getArguments().getParcelable("supplier");
//            int supplierId = supplierInfo.getId();
//            getRatingsBySupplierId(supplierId);
//        }

        Button submitReviewButton = rootView.findViewById(R.id.submitReviewButton);
        submitReviewButton.setOnClickListener(v -> {
            float ratingStar = ratingBarInput.getRating();
            if (ratingStar == 0.0f) {
                Toast.makeText(getContext(), "Vui lòng chọn số sao trước khi gửi đánh giá!", Toast.LENGTH_SHORT).show();
            } else {
                String responseMessage = messageInput.getText().toString();
                addRating(userId, supplierInfo.getId(), ratingStar, responseMessage, new ReviewAdapter.AddRatingCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "Bình luận thành công!", Toast.LENGTH_SHORT).show();
                        messageInput.setText("");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return rootView;
    }

    private void getRatingsBySupplierId(int supplierId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_RATING_BY_SUPPLIER_ID + "/" +supplierId; // Replace with your API URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject ratingObject = response.getJSONObject(i);
                                int id = ratingObject.getInt("id");
                                JSONObject userObject = ratingObject.getJSONObject("users");
                                String fullName = userObject.getString("fullName");
                                String imgUrl = userObject.getString("imgUrl");
                                int ratingStar = ratingObject.getInt("rating_star");
                                String responseMessage = ratingObject.getString("response_message");

                                // Add to rating list
                                ratingList.add(new Rating(id, fullName, ratingStar, responseMessage, imgUrl));
                            }

                            // Notify adapter that data has changed
                            reviewAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
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

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }


    private void addRating(int userId, int supplierId, float ratingStar, String responseMessage, final ReviewAdapter.AddRatingCallback callback) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.CREATE_RATING;

        JSONObject ratingData = new JSONObject();
        try {
            ratingData.put("user_id", userId);
            ratingData.put("supplier_id", supplierId);
            ratingData.put("rating_star", ratingStar);
            ratingData.put("response_message", responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                ratingData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse response to get new rating details
                            int id = response.getInt("id");
                            JSONObject userObject = response.getJSONObject("users");
                            String fullName = userObject.getString("fullName");
                            String imgUrl = userObject.getString("imgUrl");
                            int ratingStar = response.getInt("ratingStar");
                            String responseMessage = response.getString("responseMessage");

                            // Tạo đối tượng Rating mới và thêm vào adapter
                            Rating newRating = new Rating(id, fullName, ratingStar, responseMessage, imgUrl);
                            reviewAdapter.addRating(newRating); // Thêm rating mới vào adapter

                            // Thông báo thành công
                            if (callback != null) {
                                callback.onSuccess();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (callback != null) {
                                callback.onError("Error parsing response");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.onError(error.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
