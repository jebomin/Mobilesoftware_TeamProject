package com.jihyun.mobilesoftwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private ImageButton checkButton;
    private GoogleMap mMap;
    private String address = null;
    private Geocoder geocoder;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        geocoder = new Geocoder(this, Locale.KOREA);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 확인 버튼
        checkButton = findViewById(R.id.map_check);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    MarkerOptions markerOptions = new MarkerOptions();

    @Override
    public void onMapReady (GoogleMap googleMap) {
        mMap = googleMap;
        LatLng start = new LatLng(37.55827, 126.998425);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();

        latitude = point.latitude;
        longitude = point.longitude;

        // 주소로 변환
        geocoderthread thread = new geocoderthread();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        markerOptions.title("식사 위치");
        markerOptions.snippet(address);
        markerOptions.position(new LatLng(latitude, longitude));

        mMap.addMarker(markerOptions);
    }

    class geocoderthread extends Thread {
        public void run() {
            List<Address> addlist = null;
            try {
                addlist = geocoder.getFromLocation(latitude, longitude, 10);
            } catch (IOException e) {
            }

            if (addlist != null) {
                if (addlist.size() == 0)
                {
                    address = "해당되는 주소 정보가 없습니다";
                }
                else
                {
                    address = addlist.get(0).getAddressLine(0);
                }
            }
        }
    }
}