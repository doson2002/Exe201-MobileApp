package app.foodpt.exe201.Fragment.Partner;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.FoodItemActivity;
import app.foodpt.exe201.OrderForPartnerActivity;
import app.foodpt.exe201.ProfileActivity;
import app.foodpt.exe201.PromotionActivity;
import app.foodpt.exe201.R;
import app.foodpt.exe201.RatingChartActivity;
import app.foodpt.exe201.ReportForPartnerActivity;
import app.foodpt.exe201.UpdateAddressActivity;
import app.foodpt.exe201.UpdatePartnerStep1Activity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PartnerHomeFragment extends Fragment {
    private TextView openTimeTextView, closeTimeTextView, orderCountTextView;
    private LinearLayout timeIcon, reportIcon, menuIcon, addressIcon, ratingIcon, partnerInfoIcon, promotion_icon, orderManageIcon;
    private ImageView accountIcon;
    ImageView imageView;
    FloatingActionButton buttonUploadImage;
    Button submitButton, buttonSaveSupplierInfo;
    EditText editTextPartnerName, editTextPartnerDescription;
    Uri imageUri;
    String imageUrl = "";
    int totalOrderCount= 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_partner_home_page, container, false);

        View rootView = view.findViewById(R.id.main_layout);
        // Thiết lập WindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                // Áp dụng padding để tránh bị thanh hệ thống che
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), 0);
                return insets.consumeSystemWindowInsets();
            }
        });
        // Enable edge-to-edge (if necessary, or move it to the activity)
        EdgeToEdge.enable(getActivity());

        // Initialize shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        int supplierId = sharedPreferences.getInt("supplier_id", 0);

        // Fetch supplier data
        fetchSupplierByUserId(userId);


        // Bind views
//        accountIcon = view.findViewById(R.id.account_icon);
        menuIcon = view.findViewById(R.id.menu_icon);
        reportIcon = view.findViewById(R.id.reportIcon);
        addressIcon = view.findViewById(R.id.address_icon);
        timeIcon = view.findViewById(R.id.time_icon);
        ratingIcon = view.findViewById(R.id.ratingIcon);
        partnerInfoIcon = view.findViewById(R.id.partnerInfoIcon);
        promotion_icon = view.findViewById(R.id.promotion_icon);
        orderManageIcon = view.findViewById(R.id.orderManageIcon);
        orderCountTextView = view.findViewById(R.id.orderCountTextView);
        // Gọi hàm getOrderCount khi cần
        getOrderCount(supplierId, "đang giao");
        // Cập nhật TextView với số lượng đơn hàng



        startBlinkingEffect();
        // Bắt đầu hiệu ứng nhấp nháy cho order count

//        // Account icon click listener
//        accountIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ProfileActivity.class);
//                startActivity(intent);
//            }
//        });

        // Menu icon click listener
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodItemActivity.class);
                startActivity(intent);
            }
        });

        // Report icon click listener
        reportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportForPartnerActivity.class);
                startActivity(intent);
            }
        });

        // Address icon click listener
        addressIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateAddressActivity.class);
                startActivity(intent);
            }
        });

        // Time picker click listener
        timeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        // Address icon click listener
        ratingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RatingChartActivity.class);
                startActivity(intent);
            }
        });
        // Click listener cho partnerInfoIcon để mở dialog
        partnerInfoIcon.setOnClickListener(view1 -> {

            showPartnerInfoDialog(supplierId, userId);
        });

        promotion_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PromotionActivity.class);
                startActivity(intent);
            }
        });
        orderManageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderForPartnerActivity.class);
                startActivity(intent);
            }
        });



        // Setup save button in time picker
        LayoutInflater dialogInflater = LayoutInflater.from(getActivity());
        View timePickerView = dialogInflater.inflate(R.layout.activity_time_picker, null);
        Button saveButton = timePickerView.findViewById(R.id.save_button_time_picker);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String openTime = openTimeTextView.getText().toString();
                String closeTime = closeTimeTextView.getText().toString();

                if (openTime.isEmpty() || closeTime.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
                    saveButton.setText("LƯU");
                    saveButton.setTextColor(Color.WHITE);
                    // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                    saveButton.setEnabled(true);
                    return;
                }
                updateRestaurantTime(supplierId, openTime, closeTime);
            }
        });

        return view;
    }
    private void startBlinkingEffect() {
        Animation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(500); // Thời gian một lần nhấp nháy
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        orderCountTextView.startAnimation(blinkAnimation);
    }
    // Phương thức để gọi API và cập nhật TextView
    private void getOrderCount(int supplierId, String status) {
        String url =  ApiEndpoints.GET_ORDER_COUNT_BY_SUPPLIER_ID_AND_STATUS +"/"+ supplierId + "?status=" + status;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        // Tạo RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        // Tạo JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy số lượng đơn hàng từ phản hồi
                            totalOrderCount = response.getInt("totalOrderCount");
                            Log.d("OrderCount", "Total Order Count: " + totalOrderCount);
                            if(totalOrderCount>0){
                                orderCountTextView.setVisibility(View.VISIBLE);
                                orderCountTextView.setText(String.valueOf(totalOrderCount));

                            }else {
                                orderCountTextView.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Log.e("VolleyError", "Error: " + error.getMessage());
                    }
                }
        ){
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

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(jsonObjectRequest);
    }

    private void uploadImageToFirebase(Uri uri, int supplierId, OnUploadCompleteListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        imageUrl = downloadUri.toString();
                        sendDataToApi(supplierId); // Gửi dữ liệu đến API khi đã có URL ảnh

                        // Gọi callback sau khi quá trình tải lên và gửi dữ liệu hoàn tất
                        listener.onUploadComplete();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Upload failed: " + e.getMessage());
                });
    }
    // Hàm hiển thị Dialog chỉnh sửa thời gian
    private void showTimePickerDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_time_picker); // Gán layout của dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Ánh xạ các thành phần trong layout của dialog
        Button openTimeButton = dialog.findViewById(R.id.open_time_button);
        Button closeTimeButton = dialog.findViewById(R.id.close_time_button);
        Button saveButton = dialog.findViewById(R.id.save_button_time_picker);
        ImageView closeButton = dialog.findViewById(R.id.close_button);
        TextView openTimeTextView = dialog.findViewById(R.id.open_time_text_view); // Thêm TextView hiển thị giờ mở cửa
        TextView closeTimeTextView = dialog.findViewById(R.id.close_time_text_view); // Thêm TextView hiển thị giờ đóng cửa

        // Thiết lập sự kiện cho các nút trong dialog
        openTimeButton.setOnClickListener(v -> showTimePicker(true, openTimeTextView));
        closeTimeButton.setOnClickListener(v -> showTimePicker(false, closeTimeTextView));
        closeButton.setOnClickListener(v -> dialog.dismiss()); // Đóng dialog khi nhấn nút X

        saveButton.setOnClickListener(v -> {
            saveButton.setText("ĐANG CẬP NHẬT");
            saveButton.setTextColor(Color.BLACK);
            // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
            saveButton.setEnabled(false);
            // Lấy thông tin giờ mở và đóng cửa từ các TextView hoặc EditText
            String openTime = openTimeTextView.getText().toString();
            String closeTime = closeTimeTextView.getText().toString();
            if (openTime.isEmpty() || closeTime.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng chọn giờ mở cửa và đóng cửa", Toast.LENGTH_SHORT).show();
                saveButton.setText("LƯU");
                saveButton.setTextColor(Color.WHITE);
                // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                saveButton.setEnabled(true);
            } else {
                Toast.makeText(getActivity(), "Giờ mở cửa: " + openTimeTextView.getText() + "\nGiờ đóng cửa: " + closeTimeTextView.getText(), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                int supplierId = sharedPreferences.getInt("supplier_id", 0);
                // Kiểm tra nếu người dùng chưa chọn giờ mở và đóng cửa
                updateRestaurantTime(supplierId, openTime, closeTime);
                dialog.dismiss(); // Đóng dialog sau khi lưu
            }
        });

        // Thiết lập kích thước và vị trí của dialog
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER; // Hiển thị giữa màn hình
            params.width = WindowManager.LayoutParams.MATCH_PARENT; // Đặt chiều rộng tự động điều chỉnh
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Đặt chiều cao tự động điều chỉnh
            window.setAttributes(params);
        }

        dialog.show();
    }

    private void showPartnerInfoDialog(int supplierId, int userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_partner_info, null);
        builder.setView(dialogView);

        // Khai báo các thành phần trong dialog
        editTextPartnerName = dialogView.findViewById(R.id.editTextPartnerName);
        editTextPartnerDescription = dialogView.findViewById(R.id.editTextPartnerDescription);
        imageView = dialogView.findViewById(R.id.imageView);
        buttonUploadImage = dialogView.findViewById(R.id.floatingActionButton);
        ImageView closeButton = dialogView.findViewById(R.id.close_button);
        buttonSaveSupplierInfo = dialogView.findViewById(R.id.buttonSave);


        fetchSupplierByUser(userId, editTextPartnerName, editTextPartnerDescription, imageView);

        AlertDialog dialog = builder.create();
        closeButton.setOnClickListener(v -> dialog.dismiss()); // Đóng dialog khi nhấn nút X

        // Xử lý chọn ảnh bằng ImagePicker
        buttonUploadImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop() // Crop ảnh
                    .compress(1024) // Giảm kích thước ảnh
                    .maxResultSize(1080, 1080) // Kích thước ảnh tối đa
                    .start();
        });

        // Xử lý lưu thông tin
        buttonSaveSupplierInfo.setOnClickListener(v -> {
    // Đổi text thành "Đang Lưu"
            buttonSaveSupplierInfo.setText("ĐANG CẬP NHẬT");
            buttonSaveSupplierInfo.setTextColor(Color.BLACK);
            // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
            buttonSaveSupplierInfo.setEnabled(false);
            if (imageUri != null) {
                // Upload ảnh lên Firebase và chỉ đóng dialog sau khi hoàn tất
                uploadImageToFirebase(imageUri, supplierId, () -> {
                    // Đóng dialog sau khi upload và gửi dữ liệu hoàn thành
                    dialog.dismiss();
                });
            } else {
                // Không có ảnh, chỉ gọi API và đóng dialog
                sendDataToApi(supplierId);
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    // Hiển thị TimePickerDialog
    private void showTimePicker(boolean isOpenTime, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                (view, selectedHour, selectedMinute) -> {
                    timeTextView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                },
                hour, minute, true);

        timePickerDialog.show();
    }
    private void fetchSupplierByUserId(int userId) {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID+"/" + userId; // Thay thế bằng URL API của bạn
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy supplierId từ phản hồi
                            int supplierId = response.getInt("id"); // Lấy id của restaurant
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("supplier_id", supplierId);
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi gọi API
                Toast.makeText(getActivity(), "Failed to fetch supplier data", Toast.LENGTH_SHORT).show();
                Intent goToProfile = new Intent(requireContext(), UpdatePartnerStep1Activity.class);
                startActivity(goToProfile);
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }
    private void fetchSupplierByUser(int userId, EditText editTextPartnerName, EditText editTextPartnerDescription, ImageView imageViewProfile) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);

        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID + "/" + userId; // Thay thế bằng URL API của bạn
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy supplierId từ phản hồi
                            int supplierId = response.getInt("id"); // Lấy id của restaurant
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("supplier_id", supplierId);
                            editor.apply();

                            // Lấy restaurant_name và description
                            String restaurantName = response.getString("restaurant_name");
                            String description = response.getString("description");
                             imageUrl = response.getString("img_url");

                            // Gán giá trị vào EditText
                            editTextPartnerName.setText(restaurantName);
                            editTextPartnerDescription.setText(description);

                            // Kiểm tra xem img_url có rỗng không
                            if (!imageUrl.isEmpty()) {
                                // Dùng Glide để load ảnh vào ImageView
                                Glide.with(PartnerHomeFragment.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi đang tải
                                        .error(R.drawable.ic_launcher_foreground) // Ảnh hiển thị khi tải thất bại
                                        .fitCenter()
                                        .transform(new RoundedCorners(30))
                                        .into(imageViewProfile);
                            } else {
                                imageViewProfile.setImageResource(R.drawable.ic_launcher_foreground); // Giữ ảnh mặc định
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Xử lý lỗi khi gọi API
                Toast.makeText(getActivity(), "Failed to fetch supplier data", Toast.LENGTH_SHORT).show();
            }
        }) {
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    // Gửi dữ liệu đến API
    private void sendDataToApi(int supplierId) {
        String url = ApiEndpoints.UPDATE_SUPPLIER + "/" + supplierId; ; // API URL
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Lấy token và userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id
        // Tạo JSONObject cho body request
        JSONObject jsonBody = new JSONObject();
        try {
            String partnerName = editTextPartnerName.getText().toString();
            String partnerDescription = editTextPartnerDescription.getText().toString();

            jsonBody.put("restaurant_name", partnerName);
            jsonBody.put("description", partnerDescription);
            jsonBody.put("img_url", imageUrl);
            jsonBody.put("user_id", userId); // Giá trị user_id dạng int
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Thay đổi từ StringRequest sang JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    // Xử lý response từ API khi request thành công
                    Log.d("API Response", response.toString());
                    // Hiển thị thông báo thành công:
                    Toast.makeText(requireContext(), "Cập nhật thông tin đối tác thành công", Toast.LENGTH_LONG).show();

                },
                error -> {
                    // Xử lý lỗi khi request thất bại
                    Log.e("API Error", error.toString());
                    Toast.makeText(requireContext(), "Opsssss!! Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                    buttonSaveSupplierInfo.setText("LƯU THAY ĐỔI");
                    buttonSaveSupplierInfo.setTextColor(Color.WHITE);
                    // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                    buttonSaveSupplierInfo.setEnabled(true);

                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Content-Type", "application/json"); // Thêm header Content-Type
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Thêm request vào queue
        queue.add(jsonObjectRequest);
    }
    private void updateRestaurantTime(int supplierId, String openTime, String closeTime) {
        // URL của API
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        String url = ApiEndpoints.UPDATE_OPEN_CLOSE_TIME +"/" +supplierId; // Thay thế bằng URL thực tế của bạn


        // Tạo đối tượng JSON để gửi yêu cầu
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("open_time", openTime); // Thời gian mở cửa
            jsonRequest.put("close_time", closeTime); // Thời gian đóng cửa
        } catch (JSONException e) {
            e.printStackTrace();

            return;
        }

        // Tạo yêu cầu PUT với thư viện Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý phản hồi khi thành công
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(getActivity(), "Status: " + status + "\nMessage: " + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi khi gọi API

                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Thêm header nếu cần thiết, ví dụ như Authorization
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + jwtToken); // Thay thế bằng JWT Token thực tế của bạn
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Thêm yêu cầu vào hàng đợi của Volley
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra kết quả trả về từ ImagePicker
        if (resultCode == RESULT_OK) {
            if (data != null) {
                // Lấy URI của ảnh đã chọn
                 imageUri = data.getData(); // Lấy URI của ảnh đã chọn
                if (imageUri != null) {
                    // Sử dụng Glide để tải ảnh và bo tròn các góc
                    Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(30))) // Bo tròn góc với bán kính 30
                            .into(imageView); // Hiển thị ảnh trong imageView
                    // TODO: Gửi URI đến backend hoặc Firebase nếu cần
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            // Xử lý lỗi khi chọn ảnh
            Toast.makeText(getActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            // Người dùng hủy chọn ảnh
            Toast.makeText(getActivity(), "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }
    // Interface để callback khi upload hoàn thành
    interface OnUploadCompleteListener {
        void onUploadComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", 0);
        // Gọi lại hàm getOrderCount khi Fragment trở lại foreground
        getOrderCount(supplierId, "đang giao");
    }

}