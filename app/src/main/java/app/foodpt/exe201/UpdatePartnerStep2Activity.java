package app.foodpt.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.PhuongXa;
import app.foodpt.exe201.DTO.Quan;
import app.foodpt.exe201.DTO.SupplierType;
import app.foodpt.exe201.DTO.TinhThanh;
import app.foodpt.exe201.Fragment.Partner.PartnerHomeFragment;
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

public class UpdatePartnerStep2Activity extends AppCompatActivity {

     String restaurantName, description;
     ImageView imageView;
     FloatingActionButton button;
     Button submitButton;
     EditText addressInput;
     Uri imageUri;
     String imageUrl = "";
     Spinner spinnerSupplierType;
    private ArrayList<SupplierType> supplierTypes = new ArrayList<>();
    private Spinner spinnerTinh, spinnerQuan, spinnerPhuong;
    private ArrayAdapter<TinhThanh> adapterTinh;
    private ArrayAdapter<Quan>       adapterQuan;
    private ArrayAdapter<PhuongXa> adapterPhuong;
    private ArrayList<TinhThanh> listTinh = new ArrayList<>();
    private ArrayList<Quan> listQuan = new ArrayList<>();
    private ArrayList<PhuongXa> listPhuong = new ArrayList<>();
    private boolean isTinhDataLoaded = false;  // Biến cờ để kiểm tra dữ liệu đã tải chưa

    private TinhThanh selectedTinh ;
    private Quan selectedQuan;
    private PhuongXa selectedPhuong;

    private String fullAddress;
    private int selectedSupplierTypeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_partner_step2);

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
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });


        addressInput = findViewById(R.id.address_input);
        submitButton = findViewById(R.id.submit_button);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.floatingActionButton);

        // Get the data passed from the first Activity
        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("restaurantName");
        description = intent.getStringExtra("description");

        spinnerSupplierType = findViewById(R.id.type_spinner);
        // Gọi API để lấy danh sách Supplier Types
        getAllSupplierTypes();

        spinnerSupplierType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy SupplierType đã chọn
                SupplierType selectedSupplierType = (SupplierType) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Selected: " + selectedSupplierType.getTypeName(), Toast.LENGTH_SHORT).show();

                // Lưu id hoặc thực hiện thao tác khác với supplierTypeId
                selectedSupplierTypeId = selectedSupplierType.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý nếu không có gì được chọn (tuỳ chọn)
            }
        });

        // Ánh xạ Spinner
        spinnerTinh = findViewById(R.id.spinner_tinh);
        spinnerQuan = findViewById(R.id.spinner_quan);
        spinnerPhuong = findViewById(R.id.spinner_phuong);

        // Set up adapter
        adapterTinh = new ArrayAdapter<>(this, R.layout.spinner_item, listTinh);
        adapterTinh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTinh.setAdapter(adapterTinh);
        getTinhThanh();
        // Đặt thông điệp hiển thị mặc định (Prompt) cho Spinner


        adapterQuan = new ArrayAdapter<>(this, R.layout.spinner_item, listQuan);
        adapterQuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuan.setAdapter(adapterQuan);

        adapterPhuong = new ArrayAdapter<>(this, R.layout.spinner_item, listPhuong);
        adapterPhuong.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhuong.setAdapter(adapterPhuong);

        // Khi click vào spinnerTinh, chỉ lúc đó mới gọi API để tải dữ liệu Tỉnh
        spinnerTinh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isTinhDataLoaded) {  // Kiểm tra nếu dữ liệu chưa được tải
                      // Gọi API để lấy dữ liệu Tỉnh
                }
                return false;  // Trả về false để spinner vẫn hoạt động (hiển thị danh sách)
            }
        });

        // Khi chọn Tỉnh, lấy dữ liệu Quận/Huyện
        spinnerTinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                 selectedTinh = listTinh.get(position);
                // Kiểm tra nếu người dùng chọn mục mặc định
                if (selectedTinh.getId().isEmpty()) {
                    // Không làm gì nếu chọn "Chọn Tỉnh"
                    return;
                }

                String idTinh = selectedTinh.getId();
                getQuanHuyen(idTinh);  // Lấy id Tỉnh (hoặc có thể là một id khác từ API)

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Khi chọn Quận, lấy dữ liệu Phường/Xã
        spinnerQuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Lấy đối tượng TinhThanh từ vị trí được chọn
                 selectedQuan = listQuan.get(position);
                // Kiểm tra nếu người dùng chọn mục mặc định
                if (selectedQuan.getFullName().equals("Chọn Quận")) {
                    return;  // Không làm gì
                }
                String idQuan = selectedQuan.getId(); // Lấy id của tỉnh
                getPhuongXa(idQuan);  // Truyền id của tỉnh vào hàm getQuanHuyen  // Lấy id Quận
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Khi chọn Phường/Xã
        spinnerPhuong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPhuong = listPhuong.get(position);
                // Kiểm tra nếu người dùng chọn mục mặc định

                if (selectedPhuong.getFullName().equals("Chọn Phường")) {
                    return;  // Không làm gì
                }
                String address = addressInput.getText().toString();
                    // Tạo chuỗi hoàn chỉnh
                    fullAddress = selectedTinh + " - " + selectedQuan + " - " + selectedPhuong + "-" + address;
                    Toast.makeText(UpdatePartnerStep2Activity.this, "Địa chỉ: " + fullAddress, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UpdatePartnerStep2Activity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        // Khi người dùng nhấn nút "Submit"
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButton.setText("ĐANG CẬP NHẬT");
                submitButton.setTextColor(Color.BLACK);
                // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                submitButton.setEnabled(false);
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri); // Upload ảnh lên Firebase
                }else{
                    sendDataToApi();
                    submitButton.setText("CẬP NHẬT");
                    submitButton.setEnabled(true);

                }
            }
        });



    }
    private void getAllSupplierTypes() {
        String url = ApiEndpoints.GET_ALL_SUPPLIER_TYPES + "/" + 1; // URL của API
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);
        // Tạo RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Tạo JsonArrayRequest để nhận danh sách các loại nhà cung cấp
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Tạo danh sách để lưu các SupplierType


                        // Duyệt qua mảng JSON và chuyển đổi thành các đối tượng SupplierType
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String typeName = jsonObject.getString("typeName");
                                String imgUrl = jsonObject.getString("imgUrl");

                                SupplierType supplierType = new SupplierType(id, typeName, imgUrl);
                                supplierTypes.add(supplierType);
                            }

                            // Gán danh sách vào Spinner
                            populateSpinner(supplierTypes);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi nếu có
                        Log.e("Volley Error", error.toString());
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

        // Thêm yêu cầu vào hàng đợi
        queue.add(jsonArrayRequest);
    }
    private void populateSpinner(List<SupplierType> supplierTypeList) {
        // Tạo ArrayAdapter để hiển thị danh sách lên Spinner
        ArrayAdapter<SupplierType> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, supplierTypeList
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerSupplierType.setAdapter(adapter);
        // In log để kiểm tra adapter
        Log.d("Spinner Adapter", adapter.toString());

    }


    private void getTinhThanh() {
        String url = "https://esgoo.net/api-tinhthanh/1/0.htm";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("error") == 0) {
                                JSONArray dataArray = response.getJSONArray("data");
                                listTinh.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject tinh = dataArray.getJSONObject(i);
                                    String id = tinh.getString("id");
                                    String fullName = tinh.getString("full_name");

                                    // Thêm đối tượng TinhThanh vào listTinh
                                    listTinh.add(new TinhThanh(id, fullName));
                                }
                                adapterTinh.notifyDataSetChanged();
                                // Đánh dấu là dữ liệu đã được tải
                                isTinhDataLoaded = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void getQuanHuyen(String idTinh) {
              String url = "https://esgoo.net/api-tinhthanh/2/" + idTinh + ".htm";



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("error") == 0) {
                                JSONArray dataArray = response.getJSONArray("data");
                                listQuan.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject quan = dataArray.getJSONObject(i);
                                    String id = quan.getString("id");
                                    String fullName = quan.getString("full_name");
                                    listQuan.add(new Quan(id, fullName));

                                }
                                adapterQuan.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void getPhuongXa(String idQuan) {

        String url = "https://esgoo.net/api-tinhthanh/3/" + idQuan + ".htm";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("error") == 0) {
                                JSONArray dataArray = response.getJSONArray("data");
                                listPhuong.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject phuong = dataArray.getJSONObject(i);
                                    String id = phuong.getString("id");
                                    String fullName = phuong.getString("full_name");

                                    // Thêm đối tượng TinhThanh vào listTinh
                                    listPhuong.add(new PhuongXa(id, fullName));
                                }
                                adapterPhuong.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    // Upload ảnh lên Firebase Storage và lấy URL
    private void uploadImageToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

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
        // Kiểm tra các trường dữ liệu trước khi gửi request
        if (restaurantName == null || restaurantName.isEmpty()) {
            Toast.makeText(UpdatePartnerStep2Activity.this, "Tên nhà hàng không được để trống", Toast.LENGTH_LONG).show();
            return;
        }
        if (description == null || description.isEmpty()) {
            Toast.makeText(UpdatePartnerStep2Activity.this, "Mô tả không được để trống", Toast.LENGTH_LONG).show();
            return;
        }
        if (fullAddress == null || fullAddress.isEmpty()) {
            Toast.makeText(UpdatePartnerStep2Activity.this, "Địa chỉ không được để trống", Toast.LENGTH_LONG).show();
            return;
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(UpdatePartnerStep2Activity.this, "Vui lòng chọn hình ảnh cho nhà hàng", Toast.LENGTH_LONG).show();
            return;
        }
        if (selectedSupplierTypeId <= 0) {
            Toast.makeText(UpdatePartnerStep2Activity.this, "Vui lòng chọn loại đối tác", Toast.LENGTH_LONG).show();
            return;
        }

        String url = ApiEndpoints.CREATE_SUPPLIER_INFO; ; // API URL
        RequestQueue queue = Volley.newRequestQueue(UpdatePartnerStep2Activity.this);

        // Lấy token và userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id
        // Tạo JSONObject cho body request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("restaurant_name", restaurantName);
            jsonBody.put("description", description);
            jsonBody.put("address", fullAddress);
            jsonBody.put("img_url", imageUrl); // URL của ảnh đã upload
            jsonBody.put("supplier_type_id", selectedSupplierTypeId);
            jsonBody.put("user_id", userId); // Giá trị user_id dạng int
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Thay đổi từ StringRequest sang JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Xử lý response từ API khi request thành công
                    Log.d("API Response", response.toString());
                    // Hiển thị thông báo thành công:
                    Toast.makeText(UpdatePartnerStep2Activity.this, "Tạo thông tin đối tác thành công", Toast.LENGTH_LONG).show();
                    // Set Intent for go to profile
                    Intent goToProfile = new Intent(UpdatePartnerStep2Activity.this, BottomNavPartnerHomeActivity.class);
                    startActivity(goToProfile);
                    },
                error -> {
                    // Xử lý lỗi khi request thất bại
                    Log.e("API Error", error.toString());
                    Toast.makeText(UpdatePartnerStep2Activity.this, "Lỗi: Thông tin đối tác đã được tạo", Toast.LENGTH_LONG).show();


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


        // Thêm request vào queue
        queue.add(jsonObjectRequest);
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