package app.foodpt.exe201.Fragment.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.foodpt.exe201.DTO.SupplierInfo;
import app.foodpt.exe201.R;
import app.foodpt.exe201.helpers.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceFragment extends Fragment implements OnMapReadyCallback {

    private double latitudeSupplier = 0, longitudeSupplier = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        SupplierInfo supplierInfoChose = Utils.getSupplierInfo(requireContext());


        assert supplierInfoChose != null;
        latitudeSupplier = supplierInfoChose.getLatitude();
        longitudeSupplier = supplierInfoChose.getLongitude();


        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Load the map asynchronously
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Create a LatLng object for the store location
        LatLng storeLocation = new LatLng(latitudeSupplier, longitudeSupplier);

        // Move the camera to the store location and add a marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15)); // Zoom level 15
        googleMap.addMarker(new MarkerOptions().position(storeLocation).title("Vị trí cửa hàng"));
    }
}