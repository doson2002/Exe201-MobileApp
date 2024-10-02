package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

public class AddFoodItemActivity extends AppCompatActivity {

    private Spinner statusSpinner, categorySpinner, typeSpinner;
    ImageView backArrow,imageView;
    private TypeSpinnerAdapter TypeAdapter;
    private LinearLayout linearAddFoodType ;
    FloatingActionButton fbutton;
    Button saveButton;
    Uri imageUri;
    String imageUrl;
    private List<FoodType> foodTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_food_item);
        statusSpinner = findViewById(R.id.status_spinner);
        categorySpinner = findViewById(R.id.category_spinner);
        typeSpinner = findViewById(R.id.food_type_spinner);
        imageView = findViewById(R.id.imageView);
        fbutton = findViewById(R.id.floatingActionButton);
        saveButton = findViewById(R.id.save_button);
        linearAddFoodType = findViewById(R.id.linearAddFoodType);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        linearAddFoodType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(AddFoodItemActivity.this);
                View dialogView = inflater.inflate(R.layout.dialog_add_food_type, null);


                // Create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodItemActivity.this);
                builder.setView(dialogView);

                // Get references to the input fields
                EditText editTextTypeName = dialogView.findViewById(R.id.editTextTypeName);
                EditText editTextPosition = dialogView.findViewById(R.id.editTextPosition);

                // Set up the dialog buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the entered data
                        String typeName = editTextTypeName.getText().toString().trim();
                        int position = Integer.parseInt(editTextPosition.getText().toString().trim());
                        // Get supplierInfoId from SharedPreferences

                        // Check if supplierInfoId is valid
                        if (supplierId != -1) {
                            // Create a JSON object for the request
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("typeName", typeName);
                                jsonBody.put("i", position);
                                jsonBody.put("supplierInfoId", supplierId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Send the API request
                            sendCreateFoodTypeRequest(jsonBody,jwtToken);
                        } else {
                            Toast.makeText(AddFoodItemActivity.this, "Error: Supplier ID not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                builder.create().show();
            }
        });
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

        loadAllFoodTypes(supplierId);

        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddFoodItemActivity.this)
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
                if (event.getAction() == MotionEvent.ACTION_UP) {
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
    private void sendCreateFoodTypeRequest(JSONObject jsonBody, String jwtToken) {
        String url = ApiEndpoints.CREATE_FOOD_TYPE;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from API
                        Toast.makeText(AddFoodItemActivity.this, "Food type created successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(AddFoodItemActivity.this, "Error creating food type", Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(AddFoodItemActivity.this).add(jsonObjectRequest);
    }

    private void loadAllFoodTypes(int supplierId) {
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
                            updateTypeSpinner(foodTypes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddFoodItemActivity.this, "Error parsing food types data", Toast.LENGTH_SHORT).show();
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

    private void updateTypeSpinner(List<FoodType> allFoodTypes) {
        List<String> typeNames = new ArrayList<>();
        boolean[] selectedItems = new boolean[allFoodTypes.size()];

        // Duyệt qua danh sách tất cả loại thực phẩm
        for (int i = 0; i < allFoodTypes.size(); i++) {
            FoodType foodType = allFoodTypes.get(i);
            typeNames.add(foodType.getTypeName());
            // Bạn có thể sử dụng trạng thái mặc định (false) cho các mục không chọn
            selectedItems[i] = false; // Mặc định là không được chọn
        }

        // Cập nhật Adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TypeAdapter = new TypeSpinnerAdapter(AddFoodItemActivity.this, typeNames, selectedItems);
                typeSpinner.setAdapter(TypeAdapter);
            }
        });


    }
    // Overloaded hàm sendDataToApi() khi không có imageUrl
    private void sendDataToApi() {
        sendDataToApi(""); // Gọi hàm chính với imageUrl trống hoặc mặc định
    }

    // Hàm để gửi dữ liệu đến API
    private void sendDataToApi(String imgUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        // Lấy dữ liệu từ các thành phần giao diện
        String foodName = ((EditText) findViewById(R.id.etFoodName)).getText().toString();
        String description = ((EditText) findViewById(R.id.etDescription)).getText().toString();
        int quantity = Integer.parseInt(((EditText) findViewById(R.id.etInventoryQuantity)).getText().toString());
        double price = Double.parseDouble(((EditText) findViewById(R.id.etPrice)).getText().toString());
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
            foodItemJson.put("inventory_quantity", quantity);
            foodItemJson.put("description", description);
            foodItemJson.put("price", price);
            foodItemJson.put("status", status);
            foodItemJson.put("image_url", imgUrl); // URL ảnh từ Firebase
            foodItemJson.put("category", category);
            foodItemJson.put("supplier_id", supplierId);


            // Tạo JSONArray cho food_type_ids
            JSONArray foodTypeIdsJsonArray = new JSONArray(foodTypeIds);
            foodItemJson.put("food_type_ids", foodTypeIdsJsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi request PUT hoặc POST để cập nhật food item
        String createUrl = ApiEndpoints.CREATE_FOOD_ITEM; // Thay thế bằng URL API cập nhật
        createFoodItem(createUrl, foodItemJson);
    }
    // Hàm để gửi request cập nhật food item
    private void createFoodItem(String url, JSONObject foodItemJson) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, // Hoặc Request.Method.POST tùy theo API của bạn
                url,
                foodItemJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý kết quả thành công
                        Toast.makeText(AddFoodItemActivity.this, "Tạo thực đơn mới thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Log.e(TAG, "Error updating food item", error);
                        Toast.makeText(AddFoodItemActivity.this, "Tạo thực đơn mới thất bại", Toast.LENGTH_SHORT).show();
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