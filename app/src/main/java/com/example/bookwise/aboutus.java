package com.example.bookwise;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class aboutus extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Kastamonu Üniversitesi koordinatları
        LatLng kastamonuUniv = new LatLng(41.43826838059944, 33.76345923361683);

        mMap.addMarker(new MarkerOptions().position(kastamonuUniv).title("Kastamonu Üniversitesi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kastamonuUniv, 15));
    }



//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLng location = new LatLng(41.3887, 33.7827); // Kastamonu Üni
//        googleMap.addMarker(new MarkerOptions().position(location).title("Kastamonu Üniversitesi"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
//        googleMap.getUiSettings().setZoomControlsEnabled(true); // butonlar da gelsin
//    }

}