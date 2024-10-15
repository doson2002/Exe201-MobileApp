package com.example.exe201;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import java.util.Arrays;
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
    TextView selectedFoodTypesTextView;
    private List<FoodType> foodTypes = new ArrayList<>();
    private boolean[] selectedItems;
    private List<String> typeNames;
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
         selectedFoodTypesTextView = findViewById(R.id.selected_food_types_text_view);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);
        loadAllFoodTypes(supplierId);

        linearAddFoodType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo dialog
                final Dialog dialog = new Dialog(AddFoodItemActivity.this);
                dialog.setContentView(R.layout.dialog_add_food_type);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // Lấy các thành phần từ dialog layout
                EditText editTextTypeName = dialog.findViewById(R.id.editTextTypeName);
                EditText editTextPosition = dialog.findViewById(R.id.editTextPosition);
                ImageView closeButton = dialog.findViewById(R.id.close_button);
                Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
                Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                // Xử lý nút đóng (nút X)
                closeButton.setOnClickListener(v1 -> dialog.dismiss());

                // Xử lý khi bấm nút Confirm
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String typeName = editTextTypeName.getText().toString().trim();
                        int position = Integer.parseInt(editTextPosition.getText().toString().trim());

                        // Check if supplierId is valid
                        if (supplierId != -1) {
                            // Tạo JSON cho request
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("typeName", typeName);
                                jsonBody.put("i", position);
                                jsonBody.put("supplierInfoId", supplierId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Gửi request API
                            sendCreateFoodTypeRequest(jsonBody, jwtToken, supplierId);

                            dialog.dismiss(); // Đóng dialog sau khi gọi API
                        } else {
                            Toast.makeText(AddFoodItemActivity.this, "Error: Supplier ID not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Xử lý khi bấm nút Cancel
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); // Đóng dialog
                    }
                });

                // Hiển thị dialog
                dialog.show();
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

        typeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Nếu "Chọn loại thức ăn" là mục đầu tiên trong spinner, bạn có thể xóa nó tạm thời

                }
                return false; // Trả về false để cho phép Spinner xử lý sự kiện
            }
        });
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Nếu người dùng chọn một loại thực phẩm khác
                if (position >= 0) { // Kiểm tra nếu vị trí hợp lệ
                    // Cập nhật UI của Spinner để hiển thị các loại thực phẩm đã chọn
                    updateSpinnerText();

                    // Cập nhật Adapter để hiển thị thay đổi
                    TypeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Khi không có gì được chọn, có thể không cần xử lý gì
            }
        });



        // Khi người dùng nhấn nút "Submit"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy vị trí được chọn trong spinner
                int selectedPosition = typeSpinner.getSelectedItemPosition();

                // Kiểm tra nếu người dùng chưa chọn loại thức ăn
                if (selectedPosition < 0) {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn loại thực phẩm", Toast.LENGTH_SHORT).show();
                    return; // Không thực hiện tiếp nếu chưa chọn loại thức ăn
                }
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri); // Upload ảnh lên Firebase
                }else{
                    sendDataToApi();
                }
            }
        });
    }

    // Phương thức để cập nhật hiển thị của Spinner
    private void updateSpinnerText() {
        StringBuilder selectedFoodTypes = new StringBuilder();
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                selectedFoodTypes.append(typeNames.get(i)).append(", "); // typeNames là danh sách tên loại thực phẩm
            }
        }

        // Xóa dấu phẩy và khoảng trắng ở cuối nếu có
        if (selectedFoodTypes.length() > 0) {
            selectedFoodTypes.setLength(selectedFoodTypes.length() - 2); // Xóa dấu phẩy và khoảng trắng
        }

        // Cập nhật TextView
        if (selectedFoodTypes.length() > 0) {
            selectedFoodTypesTextView.setText("Loại thực phẩm đã chọn: " + selectedFoodTypes.toString());
            selectedFoodTypesTextView.setVisibility(View.VISIBLE); // Hiện TextView nếu có item đã chọn
        } else {
            selectedFoodTypesTextView.setVisibility(View.GONE); // Ẩn TextView nếu không có item nào được chọn
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
    private void sendCreateFoodTypeRequest(JSONObject jsonBody, String jwtToken, int supplierId) {
        String url = ApiEndpoints.CREATE_FOOD_TYPE;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from API
                        Toast.makeText(AddFoodItemActivity.this, "Food type created successfully", Toast.LENGTH_SHORT).show();
                        loadAllFoodTypes(supplierId);
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
        // Tạo danh sách tên loại thực phẩm
         typeNames = new ArrayList<>();
         selectedItems = new boolean[allFoodTypes.size()];
        Arrays.fill(selectedItems, false); // Đảm bảo tất cả đều là false
        // Duyệt qua danh sách tất cả loại thực phẩm và thêm vào danh sách
        for (int i = 0; i < allFoodTypes.size(); i++) {
            FoodType foodType = allFoodTypes.get(i);
            typeNames.add(foodType.getTypeName());
            selectedItems[i] = false;
        }

        // Cập nhật Adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TypeAdapter = new TypeSpinnerAdapter(AddFoodItemActivity.this, typeNames, selectedItems, new TypeSpinnerAdapter.OnSelectionChangeListener() {
                    @Override
                    public void onSelectionChanged(boolean[] selectedItems) {
                        // Cập nhật văn bản hiển thị trong Spinner
                        updateSpinnerText();
                    }
                });

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

                        // Trả kết quả về cho Activity trước
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("isNewItemCreated", true); // Đánh dấu có item mới
                        setResult(RESULT_OK, resultIntent);

                        // Kết thúc Activity và quay lại trang trước đó
                        finish();
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