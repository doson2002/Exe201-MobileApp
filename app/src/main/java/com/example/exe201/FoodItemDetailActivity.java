package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.exe201.API.ApiEndpoints;
import com.example.exe201.Adapter.TypeSpinnerAdapter;
import com.example.exe201.DTO.FoodType;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemDetailActivity extends AppCompatActivity {

    private int foodItemId; // Biến để lưu foodItemId
    private Spinner statusSpinner, categorySpinner, typeSpinner;
    ImageView backArrow,imageView;
    private TypeSpinnerAdapter TypeAdapter;
    FloatingActionButton fbutton;
    LinearLayout saveButton;
    Uri imageUri;
    String imageUrl;
    private List<FoodType> foodTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_item_detail);

        // Nhận ID từ Intent
        foodItemId = getIntent().getIntExtra("foodItemId", -1);
        // Kiểm tra ID hợp lệ
        if (foodItemId != -1) {
            // Gọi hàm để lấy thông tin chi tiết món ăn
            getFoodItemById(foodItemId);
        } else {
            Toast.makeText(this, "Invalid food item ID", Toast.LENGTH_SHORT).show();
        }
        statusSpinner = findViewById(R.id.status_spinner);
        categorySpinner = findViewById(R.id.category_spinner);
        typeSpinner = findViewById(R.id.type_spinner);
        imageView = findViewById(R.id.imageView);
        fbutton = findViewById(R.id.floatingActionButton);
        saveButton = findViewById(R.id.save_button);
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc Activity hiện tại và quay về Activity trước đó
                finish();
            }
        });
        if (typeSpinner == null) {
            Log.e("Spinner", "typeSpinner không thể tìm thấy trong layout.");
            return;
        }


        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(FoodItemDetailActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        // Cập nhật Adapter cho statusSpinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.status_array));
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Cập nhật Adapter cho categorySpinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.category_array));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Lấy giá trị khi người dùng chọn
        findViewById(R.id.save_button).setOnClickListener(view -> {
            String selectedStatus = statusSpinner.getSelectedItem().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String selectedType = typeSpinner.getSelectedItem().toString();

            // Hiển thị kết quả
            Toast.makeText(this, "Status: " + selectedStatus + ", Category: " + selectedCategory + ", Type: " + selectedType, Toast.LENGTH_SHORT).show();
        });
        typeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Sự kiện khi người dùng chạm vào Spinner (trước khi mở danh sách)
                    Log.d("Spinner", "Spinner được chạm vào, chuẩn bị hiển thị danh sách.");
                    // Nếu cần làm việc gì đó trước khi mở Spinner, thực hiện ở đây.
                }
                return false; // Trả về false để tiếp tục mở danh sách Spinner bình thường.
            }
        });
        // Khi người dùng nhấn nút "Submit"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {
                    uploadImageToFirebase(imageUri); // Upload ảnh lên Firebase
                }else{
                    sendDataToApi();
                }
            }
        });

    }
    // Hàm để hiển thị ảnh từ URL với Glide
    private void displayImage(String imageUrl) {
        ImageView imageView = findViewById(R.id.imageView); // Thay thế bằng ID của ImageView bạn muốn hiển thị

        // Kiểm tra xem imageUrl có rỗng không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng Glide để tải ảnh vào ImageView
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi đang tải
                    .error(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi tải thất bại
                    .into(imageView);
        } else {
            // Nếu imageUrl rỗng hoặc null, hiển thị ảnh mặc định
            imageView.setImageResource(R.drawable.ic_launcher_foreground); // Ảnh mặc định
        }
    }
    private void uploadImageToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/foodItems" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                imageUrl = downloadUri.toString();
                                sendDataToApi(imageUrl); // Gửi dữ liệu đến API khi đã có URL ảnh
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firebase", "Upload failed: " + e.getMessage());
                    }
                });
    }

    private void getFoodItemById(int foodItemId) {
        String url = ApiEndpoints.GET_FOOD_ITEM_BY_ID + "/" + foodItemId; // Thay thế bằng URL API của bạn
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Cập nhật các trường
                            String foodName = response.getString("food_name");
                            int quantity = response.getInt("quantity");
                            double price = response.getDouble("price");
                            String status = response.getString("status");
                            String category = response.getString("category");
                              imageUrl =  response.getString("image_url");

                            // Hiển thị ảnh từ imgUrl sau khi nhận phản hồi từ API
                            displayImage(imageUrl);
                            // Cập nhật vào EditText
                            ((EditText) findViewById(R.id.name_input)).setText(foodName);
                            ((EditText) findViewById(R.id.quantity)).setText(String.valueOf(quantity));
                            ((EditText) findViewById(R.id.email_input)).setText(String.valueOf(price));
                            setStatusToSpinner(status);
                            setCategoryToSpinner(category);
                            // Cập nhật loại món ăn
                            JSONArray foodTypesJsonArray = response.getJSONArray("food_types");
                            List<FoodType> selectedFoodTypes = new ArrayList<>();
                            for (int i = 0; i < foodTypesJsonArray.length(); i++) {
                                JSONObject foodTypeJson = foodTypesJsonArray.getJSONObject(i);
                                selectedFoodTypes.add(new FoodType(foodTypeJson.getInt("id"), foodTypeJson.getString("typeName")));
                            }
                            // Gọi API để lấy tất cả các loại FoodType và cập nhật Spinner
                            loadAllFoodTypes(supplierId, selectedFoodTypes); // Cập nhật spinner loại món ăn

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FoodItemDetailActivity.this, "Error parsing food item data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching food item", error);
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

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void loadAllFoodTypes(int supplierId, List<FoodType> selectedFoodTypes) {
        String url = ApiEndpoints.GET_FOOD_TYPES_BY_SUPPLIER_ID +"/" + supplierId; // Thay thế bằng URL API của bạn

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                             foodTypes = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject foodTypeJson = response.getJSONObject(i);
                                int id = foodTypeJson.getInt("id");
                                String typeName = foodTypeJson.getString("typeName");
                                int order = foodTypeJson.getInt("i");
                                // Tạo đối tượng FoodType từ JSON
                                FoodType foodType = new FoodType(id, typeName);
                                foodType.setOrder(order); // Gán thêm thuộc tính order nếu có

                                foodTypes.add(foodType);

                            }
                            // Cập nhật dữ liệu cho spinner
                            updateTypeSpinner(foodTypes, selectedFoodTypes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FoodItemDetailActivity.this, "Error parsing food types data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching food types", error);
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

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void setStatusToSpinner(String status) {
        String[] statuses = getResources().getStringArray(R.array.status_array);

        int position = 0; // Mặc định là vị trí đầu tiên
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                position = i; // Cập nhật vị trí nếu tìm thấy
                break;
            }
        }

        // Thiết lập vị trí cho status_spinner
        // Thiết lập vị trí cho statusSpinner
        statusSpinner.setSelection(position);    }
    private void setCategoryToSpinner(String category) {
        String[] categories = getResources().getStringArray(R.array.category_array);

        int position = 0; // Mặc định là vị trí đầu tiên
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                position = i; // Cập nhật vị trí nếu tìm thấy
                break;
            }
        }

        // Thiết lập vị trí cho category_spinner
        categorySpinner.setSelection(position);
    }

    private void updateTypeSpinner(List<FoodType> allFoodTypes, List<FoodType> selectedFoodTypes) {
        List<String> typeNames = new ArrayList<>();
        boolean[] selectedItems = new boolean[allFoodTypes.size()];

        // Nếu danh sách rất lớn, hãy sử dụng Background Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Xử lý dữ liệu lớn trong Background Thread
                for (int i = 0; i < allFoodTypes.size(); i++) {
                    FoodType foodType = allFoodTypes.get(i);
                    typeNames.add(foodType.getTypeName());

                    for (FoodType selected : selectedFoodTypes) {
                        if (selected.getId() == foodType.getId()) {
                            selectedItems[i] = true; // Đánh dấu là đã được chọn
                            break;
                        }
                    }

                }
                for (int i = 0; i < selectedItems.length; i++) {
                    Log.d("SelectedItems", "Item: " + typeNames.get(i) + " - Selected: " + selectedItems[i]);
                }

                // Cập nhật UI trên Main Thr    ead
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cập nhật Adapter sau khi hoàn thành xử lý
                        if (typeNames.isEmpty()) {
                            Log.e("updateTypeSpinner", "Dữ liệu typeNames trống.");
                            return;
                        }
                        TypeAdapter = new TypeSpinnerAdapter(FoodItemDetailActivity.this, typeNames, selectedItems);
                        typeSpinner.setAdapter(TypeAdapter);
                        // Thiết lập đúng vị trí lựa chọn của Spinner nếu cần
                        for (int i = 0; i < selectedItems.length; i++) {
                            if (selectedItems[i]) {
                                typeSpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                });
            }
        }).start();
    }

    // Hàm để gửi dữ liệu đến API
    private void sendDataToApi(String imgUrl) {
        // Lấy dữ liệu từ các thành phần giao diện
        String foodName = ((EditText) findViewById(R.id.name_input)).getText().toString();
        int quantity = Integer.parseInt(((EditText) findViewById(R.id.quantity)).getText().toString());
        double price = Double.parseDouble(((EditText) findViewById(R.id.email_input)).getText().toString());
        String status = statusSpinner.getSelectedItem().toString();
        String category = categorySpinner.getSelectedItem().toString();


        // Lấy trạng thái các mục đã chọn từ Adapter
        boolean[] selectedItems = TypeAdapter.getSelectedItems();
        // Lấy các food_type_ids từ selectedItems trong Adapter
        List<Integer> foodTypeIds = new ArrayList<>();
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                foodTypeIds.add(foodTypes.get(i).getId());
            }
        }

        // Tạo JSONObject để gửi lên API
        JSONObject foodItemJson = new JSONObject();
        try {
            foodItemJson.put("food_name", foodName);
            foodItemJson.put("quantity_sold", quantity);
            foodItemJson.put("price", price);
            foodItemJson.put("status", status);
            foodItemJson.put("image_url", imgUrl); // URL ảnh từ Firebase
            foodItemJson.put("category", category);

            // Tạo JSONArray cho food_type_ids
            JSONArray foodTypeIdsJsonArray = new JSONArray(foodTypeIds);
            foodItemJson.put("food_type_ids", foodTypeIdsJsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi request PUT hoặc POST để cập nhật food item
        String updateUrl = ApiEndpoints.UPDATE_FOOD_ITEM + "/" + foodItemId; // Thay thế bằng URL API cập nhật
        updateFoodItem(updateUrl, foodItemJson);
    }

    // Overloaded hàm sendDataToApi() khi không có imageUrl
    private void sendDataToApi() {
        sendDataToApi(""); // Gọi hàm chính với imageUrl trống hoặc mặc định
    }

    // Hàm để gửi request cập nhật food item
    private void updateFoodItem(String url, JSONObject foodItemJson) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, // Hoặc Request.Method.POST tùy theo API của bạn
                url,
                foodItemJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý kết quả thành công
                        Toast.makeText(FoodItemDetailActivity.this, "Food item updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Log.e(TAG, "Error updating food item", error);
                        Toast.makeText(FoodItemDetailActivity.this, "Failed to update food item", Toast.LENGTH_SHORT).show();
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

        // Thiết lập RetryPolicy với thời gian chờ là 10 giây và thử lại 1 lần
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, // Thời gian chờ (10 giây)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Số lần thử lại mặc định
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Hệ số backoff mặc định
        ));
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri); // Hiển thị ảnh
        }
    }


}