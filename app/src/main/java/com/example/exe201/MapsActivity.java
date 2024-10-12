package com.example.exe201;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.exe201.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentMarker; // Biến để lưu trữ marker hiện tại
    private String selectedAddress = "";
    private LatLng selectedLatLng;
    private double latitudeSupplier = 0;
    private double longitudeSupplier = 0;
    private float distanceInKm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        latitudeSupplier = intent.getDoubleExtra("latitudeSupplier",0);
        longitudeSupplier = intent.getDoubleExtra("longitudeSupplier",0);

        // Khởi tạo FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Khởi tạo SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Button chooseLocationButton = findViewById(R.id.choose_location);
        chooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chứa dữ liệu trả về
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_address", selectedAddress); // Đưa địa chỉ vị trí hiện tại vào Intent
                resultIntent.putExtra("distance", distanceInKm);
                // Đặt kết quả và đóng Activity
                setResult(RESULT_OK, resultIntent);
                finish(); // Đóng Activity và trở về Activity trước
            }
        });
    }

    // Xử lý bản đồ khi đã sẵn sàng
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Di chuyển camera đến vị trí cố định
        LatLng ThuDucLocation = new LatLng(10.85142, 106.74727);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ThuDucLocation, 12));

        // Lấy vị trí hiện tại khi bản đồ đã sẵn sàng
        getCurrentLocation();

        // Bắt sự kiện khi click vào bản đồ
        mMap.setOnMapClickListener(latLng -> {
            // Nếu marker hiện tại đã tồn tại, xóa nó
            if (currentMarker != null) {
                currentMarker.remove();
            }

            // Thêm marker mới tại vị trí click
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã click"));
            // Giả sử tọa độ của supplier đã được lấy từ supplierInfoChose
            LatLng supplierLocation = new LatLng(latitudeSupplier, longitudeSupplier);
            selectedLatLng = latLng; // Lưu tọa độ được chọn

            // Tính khoảng cách theo kilomet
            distanceInKm = calculateDistanceInKm(selectedLatLng, supplierLocation);

            // Hiển thị khoảng cách
            Toast.makeText(this, "Khoảng cách tới supplier: " + distanceInKm + " km", Toast.LENGTH_LONG).show();
            // Hiển thị địa chỉ vị trí click vào TextView
            TextView locationDetailsTextView = findViewById(R.id.location_details);
            getAddressFromLatLng(latLng, locationDetailsTextView);
        });
    }

    // Hàm này để lấy vị trí hiện tại và hiển thị lên textView
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Hiển thị địa chỉ vị trí hiện tại lên TextView
                    TextView currentLocationTextView = findViewById(R.id.currentLocation);
                    getAddressFromLatLng(currentLocation, currentLocationTextView);
                }
            });
        }
    }
    private float calculateDistanceInKm(LatLng userLocation, LatLng supplierLocation) {
        float[] results = new float[1];

        // Tính khoảng cách giữa hai tọa độ
        Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,  // Tọa độ của người dùng
                supplierLocation.latitude, supplierLocation.longitude,  // Tọa độ của supplier
                results);

        return results[0] / 1000;  // Trả về kết quả khoảng cách theo km
    }


    // Chuyển tọa độ thành địa chỉ và hiển thị lên TextView
    private void getAddressFromLatLng(LatLng latLng, TextView textView) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                textView.setText(fullAddress);
                selectedAddress = fullAddress;

            } else {
                textView.setText("No address found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textView.setText("Unable to get address");
            selectedAddress = "";
        }
    }

    // Xử lý kết quả yêu cầu quyền từ người dùng
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu người dùng cấp quyền, lấy vị trí hiện tại
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied! App cannot access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}