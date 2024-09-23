package com.example.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.exe201.API.ApiEndpoints;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageView  backArrow;
    ShapeableImageView imageView;
    LinearLayout saveButton;
    FloatingActionButton fbutton;
    private EditText nameInput, phoneInput, emailInput;
    private Spinner genderSpinner;
    Button sign_out_btn ;
    Uri imageUri;
    String imageUrl,fullName, phoneNumber, currentEmail;
    int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Khởi tạo các view
        sign_out_btn =findViewById(R.id.sign_out_btn);
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        emailInput = findViewById(R.id.email_input);
        imageView = findViewById(R.id.imageView);
        genderSpinner = findViewById(R.id.gender_spinner);
        saveButton = findViewById(R.id.save_button);
        fbutton = findViewById(R.id.floatingActionButton);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc Activity hiện tại và quay về Activity trước đó
                finish();
            }
        });

        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Lấy dữ liệu từ SharedPreferences
         fullName = sharedPreferences.getString("full_name", "");
         phoneNumber = sharedPreferences.getString("phone", "");
         currentEmail = sharedPreferences.getString("email", "");
        gender = sharedPreferences.getInt("gender", 0);
        // Lấy img_url từ SharedPreferences
        imageUrl = sharedPreferences.getString("img_url", "");


        // Kiểm tra xem img_url có rỗng không
        if (!imageUrl.isEmpty()) {
            // Dùng Glide để load ảnh vào ShapeableImageView
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi đang tải
                    .error(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi tải thất bại
                    .fitCenter()
                    .transform(new RoundedCorners(30))
                    .override(imageView.getWidth(), imageView.getHeight()) // Cố định kích thước ImageView
                    .into(imageView);
        }
        if (imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground); // Giữ ảnh mặc định
        }
        // Gán dữ liệu vào các EditText
        nameInput.setText(fullName);
        phoneInput.setText(phoneNumber);
        emailInput.setText(currentEmail);


        // Cập nhật Spinner nếu cần thiết
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(gender); // Giả sử bạn đã lưu vị trí đã chọn

        // Set On Click Listener:
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserOut();
            }
        });

        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileActivity.this)
                        .cropSquare() // Cắt ảnh hình tròn    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
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

    // Hàm thiết lập tùy chọn cho uCrop
    private UCrop.Options getUCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true); // Bo tròn ảnh
        options.setShowCropGrid(false); // Ẩn lưới cắt ảnh
        options.setShowCropFrame(true); // Hiển thị khung cắt ảnh
        options.setCropFrameColor(Color.parseColor("#FFFFFF")); // Màu khung cắt
        options.setCropFrameStrokeWidth(5); // Độ dày khung cắt
        return options;
    }
    // Upload ảnh lên Firebase Storage và lấy URL
    private void uploadImageToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/users" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                imageUrl = downloadUri.toString();
                                sendDataToApi(); // Gửi dữ liệu đến API khi đã có URL ảnh
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

// Gửi dữ liệu đến API
    private void sendDataToApi() {
        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id
        // Lấy dữ liệu từ EditText
        String newEmail = emailInput.getText().toString();

        // Kiểm tra xem email có được thay đổi hay không
        if (!newEmail.equals(currentEmail)) {
            // Hiển thị hộp thoại cảnh báo khi email được thay đổi
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Xác nhận thay đổi email")
                    .setMessage("Bạn có chắc chắn muốn thay đổi email không? Bạn sẽ cần đăng nhập lại sau khi thay đổi.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Người dùng chọn Yes, tiếp tục gửi yêu cầu cập nhật email
                        proceedWithApiCall(sharedPreferences, token, userId, newEmail);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Người dùng chọn No, không làm gì và giữ nguyên email hiện tại
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        } else {
            // Nếu email không thay đổi, tiếp tục với các thông tin khác
            proceedWithApiCall(sharedPreferences, token, userId, newEmail);
        }

    }
    private void proceedWithApiCall(SharedPreferences sharedPreferences, String token, int userId, String newEmail) {
        String url = ApiEndpoints.UPDATE_USER_INFO + userId; // API URL
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);


        // Giả sử gender là Spinner đã được khởi tạo và gán giá trị
        String selectedGender = genderSpinner.getSelectedItem().toString();
        int genderValue;

        switch (selectedGender) {
            case "Male":
                genderValue = 0;
                break;
            case "Female":
                genderValue = 1;
                break;
            case "Other":
                genderValue = 2;
                break;
            default:
                genderValue = 0; // Hoặc xử lý trường hợp không xác định
                break;
        }


        // Tạo JSONObject cho body request
        JSONObject jsonBody = new JSONObject();
        try {
            fullName = nameInput.getText().toString();
            phoneNumber = phoneInput.getText().toString();
            jsonBody.put("full_name", fullName);
            jsonBody.put("email", newEmail);
            jsonBody.put("phone_number", phoneNumber);
            jsonBody.put("gender", genderValue);
            jsonBody.put("img_url", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Sử dụng JsonObjectRequest để gửi yêu cầu PUT và nhận JSON response
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();

                        // Kiểm tra nếu email được thay đổi, logout người dùng
                        if (response.has("emailUpdated") && response.getBoolean("emailUpdated")) {
                            String emailUpdateMessage = response.getString("emailUpdateMessage");
                            Toast.makeText(ProfileActivity.this, emailUpdateMessage, Toast.LENGTH_LONG).show();
                            this.signUserOut();
                        } else {
                            // Lưu các thông tin khác vào SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("user_id", userId);
                            editor.putString("email", newEmail);
                            editor.putString("phone", phoneNumber);
                            editor.putString("full_name", fullName);
                            editor.putString("img_url", imageUrl);
                            editor.putInt("gender", genderValue);
                            editor.apply();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data);
                        try {
                            JSONObject jsonError = new JSONObject(responseBody);
                            String errorMessage = jsonError.getString("error");
                            Toast.makeText(ProfileActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(ProfileActivity.this, "Error: " + responseBody, Toast.LENGTH_LONG).show();
                        }
                        Log.e("API Error", responseBody);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Update your profile failed", Toast.LENGTH_LONG).show();
                        Log.e("API Error", error.toString());
                    }
                }
        ) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            // Sử dụng Glide để tải ảnh và bo tròn các góc
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30))) // Bo tròn góc với bán kính 30
                    .into(imageView);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            // Xử lý lỗi khi chọn ảnh
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            // Người dùng hủy chọn ảnh
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUserOut(){

        // Set Text View Profile Values:
        nameInput.setText(null);
        phoneInput.setText(null);
        emailInput.setText(null);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

// Xóa trạng thái đăng nhập
        editor.putBoolean("isLoggedIn", false);
        editor.remove("JwtToken");  // Xóa token nếu có
        editor.remove("email");
        editor.remove("full_name");
        editor.remove("user_id");
        editor.remove("img_url");
        editor.remove("gender");
        editor.remove("phone");
        editor.apply();

        // Return User Back To Home:
        Intent goToHome = new Intent(ProfileActivity.this, SignInActivity.class);
        startActivity(goToHome);
        finish();

    }
}