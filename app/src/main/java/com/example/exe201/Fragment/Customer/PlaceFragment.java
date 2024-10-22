package com.example.exe201.Fragment.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exe201.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceFragment extends Fragment implements OnMapReadyCallback {

    private double latitude, longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        // Get latitude and longitude from Intent
        if (getActivity() != null && getActivity().getIntent() != null) {
            latitude = getActivity().getIntent().getDoubleExtra("latitude", 0.0);
            longitude = getActivity().getIntent().getDoubleExtra("longitude", 0.0);
        }

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
        LatLng storeLocation = new LatLng(latitude, longitude);

        // Move the camera to the store location and add a marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15)); // Zoom level 15
        googleMap.addMarker(new MarkerOptions().position(storeLocation).title("Vị trí cửa hàng"));
    }
}