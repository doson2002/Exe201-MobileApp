package app.foodpt.exe201;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.PhuongXa;
import app.foodpt.exe201.DTO.Quan;
import app.foodpt.exe201.DTO.TinhThanh;
import app.foodpt.exe201.Fragment.Partner.PartnerHomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateAddressActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_MAP = 1;
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
    EditText addressInput;
    private String fullAddress;
    private Button submitButton;
    private ImageView backArrow;
    private TextView tvAddressMap, tvAddressMapChose;

    private double latitude = 0;
    private double longitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_address);
        View rootView = findViewById(R.id.main);
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
        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại trang trước
                onBackPressed();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int supplierId = sharedPreferences.getInt("supplier_id", -1);




        addressInput = findViewById(R.id.address_input);
        submitButton = findViewById(R.id.submit_button);
        tvAddressMap = findViewById(R.id.tvAddressMap);
        tvAddressMapChose = findViewById(R.id.tvAddressMapChose);
        // Get the data passed from the first Activity

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


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        tvAddressMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateAddressActivity.this, SetupMapForPartnerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAP); // Gọi Activity thứ hai
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButton.setText("ĐANG CẬP NHẬT");
                submitButton.setTextColor(Color.BLACK);
                // Vô hiệu hóa nút để ngăn người dùng nhấp thêm lần nữa
                submitButton.setEnabled(false);
                // Kiểm tra trước khi gửi dữ liệu
                if (tvAddressMapChose.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateAddressActivity.this, "Vui lòng chọn vị trí trên bản đồ", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu người dùng đã chọn vị trí, gọi phương thức sendDataToApi
                    sendDataToApi(supplierId, latitude, longitude);
                }
                submitButton.setText("CẬP NHẬT");
                submitButton.setTextColor(Color.WHITE);
                submitButton.setEnabled(true);
            }
        });
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

    // Gửi dữ liệu đến API
    private void sendDataToApi(int supplierId,double latitude, double longitude) {
        String url = ApiEndpoints.UPDATE_SUPPLIER + "/" + supplierId; ; // API URL
        RequestQueue queue = Volley.newRequestQueue(UpdateAddressActivity.this);

        // Lấy token và userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id
        // Tạo JSONObject cho body request
        JSONObject jsonBody = new JSONObject();
        try {

            String address = addressInput.getText().toString();
            // Tạo chuỗi hoàn chỉnh
            fullAddress = selectedTinh + " - " + selectedQuan + " - " + selectedPhuong + "-" + address;
            jsonBody.put("address", fullAddress);
            jsonBody.put("user_id", userId); // Giá trị user_id dạng int
            jsonBody.put("latitude", latitude); // Gửi vĩ độ
            jsonBody.put("longitude", longitude); // Gửi kinh độ
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Thay đổi từ StringRequest sang JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    // Xử lý response từ API khi request thành công
                    Log.d("API Response", response.toString());
                    // Hiển thị thông báo thành công:
                    Toast.makeText(UpdateAddressActivity.this, "Cập nhật địa chỉ nhà hàng thành công", Toast.LENGTH_LONG).show();
                    // Đóng Activity hiện tại
                    finish();
                },
                error -> {
                    // Xử lý lỗi khi request thất bại
                    Log.e("API Error", error.toString());
                    Toast.makeText(UpdateAddressActivity.this, "Opsssss!! Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra requestCode và resultCode
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            // Lấy địa chỉ được chọn từ MapsActivity
            if (data != null) {
                String selectedAddress = data.getStringExtra("selected_address");
                 latitude = data.getDoubleExtra("selected_latitude", 0.0);
                 longitude = data.getDoubleExtra("selected_longitude", 0.0);
                tvAddressMapChose.setText(selectedAddress);

            }
        }
    }
}