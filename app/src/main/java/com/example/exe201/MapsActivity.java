package com.example.exe201;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.exe201.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Thu Duc move the camera
        LatLng ThuDucLocation = new LatLng(10.85142, 106.74727);
        mMap.addMarker(new MarkerOptions().position(ThuDucLocation).title("Thu Duc"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ThuDucLocation,12));
        // Bắt sự kiện khi click vào bản đồ
        mMap.setOnMapClickListener(latLng -> {


            // Chuyển đổi tọa độ thành địa chỉ
            getAddressFromLatLng(latLng);
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Lấy tọa độ được click
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            // Lấy danh sách địa chỉ tương ứng với tọa độ
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Tạo chuỗi địa chỉ đầy đủ
                String fullAddress = address.getAddressLine(0);

                // Hiển thị địa chỉ lên TextView
                TextView textViewAddress = findViewById(R.id.location_details);
                textViewAddress.setText(fullAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}