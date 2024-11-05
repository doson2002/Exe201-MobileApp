package app.foodpt.exe201.Fragment.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import app.foodpt.exe201.API.ApiEndpoints;
import app.foodpt.exe201.DTO.FoodOrderItemResponse;
import app.foodpt.exe201.FAQListActivity;
import app.foodpt.exe201.PromotionActivity;
import app.foodpt.exe201.R;
import app.foodpt.exe201.SignInActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

   private TextView tvFAQ,points_text, textViewSelectBank, tvAddPayment;
    ShapeableImageView imageView;
    LinearLayout saveButton, linearAddPayment, linearClose;
    FloatingActionButton fbutton;
    private EditText nameInput, phoneInput, emailInput,  editTextSTK, editTextName;
    private Spinner genderSpinner;
    Button sign_out_btn;
    Uri imageUri;
    String  imageUrl, fullName, phoneNumber, currentEmail, role, paymentInformation;
    int gender, userId, supplierId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        // Khởi tạo các view
        sign_out_btn = view.findViewById(R.id.sign_out_btn);
        nameInput = view.findViewById(R.id.name_input);
        phoneInput = view.findViewById(R.id.phone_input);
        emailInput = view.findViewById(R.id.email_input);
        imageView = view.findViewById(R.id.imageView);
        genderSpinner = view.findViewById(R.id.gender_spinner);
        saveButton = view.findViewById(R.id.save_button);
        fbutton = view.findViewById(R.id.floatingActionButton);
        points_text = view.findViewById(R.id.points_text);
        tvAddPayment = view.findViewById(R.id.tvAddPayment);
        linearAddPayment = view.findViewById(R.id.linearAddPayment);
        linearAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPaymentDialog();
            }
        });

        tvFAQ = view.findViewById(R.id.tvFAQ);
        tvFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), FAQListActivity.class);
                startActivity(intent);
            }
        });

        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);

        userId = sharedPreferences.getInt("user_id", 0);
        // Lấy dữ liệu từ SharedPreferences
        fullName = sharedPreferences.getString("full_name", "");
        phoneNumber = sharedPreferences.getString("phone", "");
        currentEmail = sharedPreferences.getString("email", "");
        gender = sharedPreferences.getInt("gender", 0);
        imageUrl = sharedPreferences.getString("img_url", "");
        role = sharedPreferences.getString("role", "");
        supplierId = sharedPreferences.getInt("supplier_id", 0);
        if(role.equalsIgnoreCase("ROLE_PARTNER")){
            tvAddPayment.setVisibility(View.VISIBLE);
            linearAddPayment.setVisibility(View.VISIBLE);
            fetchCash(userId);
        }
        // Kiểm tra xem img_url có rỗng không
        if (!imageUrl.isEmpty()) {
            // Dùng Glide để load ảnh vào ShapeableImageView
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .fitCenter()
                    .transform(new RoundedCorners(30))
                    .override(imageView.getWidth(), imageView.getHeight())
                    .into(imageView);
        }
        if (imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Gán dữ liệu vào các EditText
        nameInput.setText(fullName);
        phoneInput.setText(phoneNumber);
        emailInput.setText(currentEmail);

        // Cập nhật Spinner nếu cần thiết
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(gender);

        // Set OnClickListener cho nút "Đăng xuất"
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserOut();
            }
        });

        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileFragment.this)
                        .cropSquare()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        // Khi người dùng nhấn nút "Submit"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {
                    uploadImageToFirebase(imageUri);
                } else {
                    sendDataToApi();
                }
            }
        });

        return view;
    }
    private void openAddPaymentDialog() {
        // Tạo dialog
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_payment_information);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // Lấy các thành phần từ dialog layout
         textViewSelectBank = dialog.findViewById(R.id.textViewSelectBank);
         editTextSTK = dialog.findViewById(R.id.editTextSTK);
         editTextName = dialog.findViewById(R.id.editTextName);
        linearClose = dialog.findViewById(R.id.linearClose);
        // Tách chuỗi paymentInformation và gán vào các TextView
        String[] paymentInfoParts = paymentInformation.split("-");
        if (paymentInfoParts.length == 3) {
            textViewSelectBank.setText(paymentInfoParts[0]);  // Ngân hàng
            editTextSTK.setText(paymentInfoParts[1]);         // Số tài khoản
            editTextName.setText(paymentInfoParts[2]);        // Tên người nhận
        } else {
            // Xử lý nếu chuỗi không đúng định dạng
            Toast.makeText(requireContext(), "Thông tin thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
        }
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);

        // Tạo PopupWindow với danh sách lựa chọn
        String[] bankTypes = getResources().getStringArray(R.array.bank_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.popup_item, bankTypes);

        // Tạo View cho PopupWindow
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_window_layout, null);
        ListView listViewPopup = popupView.findViewById(R.id.listViewPopup);
        listViewPopup.setAdapter(adapter);

        // Tạo PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        // Hiển thị PopupWindow khi người dùng click vào TextView
        textViewSelectBank.setOnClickListener(v -> popupWindow.showAsDropDown(textViewSelectBank));

        // Xử lý sự kiện khi người dùng chọn một item từ PopupWindow
        listViewPopup.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = bankTypes[position];
            textViewSelectBank.setText(selectedType); // Cập nhật TextView khi chọn



            // Đóng PopupWindow sau khi chọn
            popupWindow.dismiss();
        });

        // Xử lý khi người dùng bấm nút Cancel
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        // Xử lý khi người dùng bấm nút Cancel
        linearClose.setOnClickListener(v -> dialog.dismiss());

        // Xử lý khi người dùng bấm nút Confirm
        buttonConfirm.setOnClickListener(v -> {


            // Gọi hàm để thực hiện API tạo promotion
            addPaymentInformation();

            dialog.dismiss(); // Đóng dialog sau khi gọi API
        });
        // Hiển thị dialog
        dialog.show();
    }
    private void addPaymentInformation() {
        // Ghép dữ liệu từ các TextView và EditText với dấu "-"
        String bank = textViewSelectBank.getText().toString();
        String stk = editTextSTK.getText().toString();
        String name = editTextName.getText().toString();
        String paymentInformation = bank + "-" + stk + "-" + name;

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        // Mã hóa `paymentInformation` để truyền vào URL
        String encodedPaymentInfo = Uri.encode(paymentInformation);
        String url = ApiEndpoints.UPDATE_PAYMENT_INFORMATION + "/" + supplierId + "?paymentInformation=" + encodedPaymentInfo;

        // Tạo request PUT với Volley
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // Xử lý phản hồi thành công từ server
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            fetchCash(userId);
                        } else {
                            Toast.makeText(requireContext(), "Failed to update payment information", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Xử lý lỗi nếu request thất bại
                    Toast.makeText(requireContext(), "Failed to update payment information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

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

        // Thêm request vào hàng đợi của Volley
        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void fetchCash(int userId) {
        String url = ApiEndpoints.GET_SUPPLIER_BY_USER_ID + "/" + userId;
        // Call the API to fetch orders
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("JwtToken", null);

        // Tạo request để lấy dữ liệu từ API
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double cash = response.getDouble("cash");
                             paymentInformation = response.getString("payment_information");

                            //                          Định dạng số thành chuỗi với dấu phẩy và "đ"
                            DecimalFormat formatter = new DecimalFormat("#,###"); // Định dạng để thêm dấu phẩy
                            String formattedCash = "cash: " + formatter.format(cash) + " đ";
                            // Hiển thị chuỗi lên points_text
                            points_text.setText(formattedCash);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
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

        // Thêm request vào hàng đợi
        queue.add(jsonObjectRequest);
    }

    // Các phương thức còn lại sẽ giữ nguyên như trong ProfileActivity

    // Ví dụ phương thức uploadImageToFirebase
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

    // Còn lại các phương thức như sendDataToApi, signUserOut giữ nguyên từ Activity
    // Gửi dữ liệu đến API
    private void sendDataToApi() {
        // Lấy dữ liệu từ SharedPreferences
        // Lấy SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);        String token = sharedPreferences.getString("JwtToken", null);
        int userId = sharedPreferences.getInt("user_id", 0); // Lấy user_id
        // Lấy dữ liệu từ EditText
        String newEmail = emailInput.getText().toString();

        // Kiểm tra xem email có được thay đổi hay không
        if (!newEmail.equals(currentEmail)) {
            // Hiển thị hộp thoại cảnh báo khi email được thay đổi
            new AlertDialog.Builder(getActivity())
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());


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
            // Kiểm tra null trước khi thêm img_url
            if (!Objects.equals(imageUrl, "null")) {
                jsonBody.put("img_url", imageUrl);
            } else {
                // Nếu imageUrl là null, không thêm thuộc tính img_url vào JSON
                Log.d("API", "imageUrl is null, not adding to JSON");
            }
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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Kiểm tra nếu email được thay đổi, logout người dùng
                        if (response.has("emailUpdated") && response.getBoolean("emailUpdated")) {
                            String emailUpdateMessage = response.getString("emailUpdateMessage");
                            Toast.makeText(getActivity(), emailUpdateMessage, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Failed to parse response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data);
                        try {
                            JSONObject jsonError = new JSONObject(responseBody);
                            String errorMessage = jsonError.getString("error");
                            Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Error: " + responseBody, Toast.LENGTH_LONG).show();
                        }
                        Log.e("API Error", responseBody);
                    } else {
                        Toast.makeText(getActivity(), "Update your profile failed", Toast.LENGTH_LONG).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(imageView);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUserOut(){

        // Set Text View Profile Values:
        nameInput.setText(null);
        phoneInput.setText(null);
        emailInput.setText(null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
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
        editor.remove("longitude");
        editor.remove("latitude");
        editor.remove("distance");
        editor.apply();

        // Return User Back To Home:
        Intent goToHome = new Intent(getActivity(), SignInActivity.class);
        startActivity(goToHome);
        getActivity().finish();

    }
}
