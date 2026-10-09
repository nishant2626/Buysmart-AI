package com.buysmart.ai.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.NearbyStoreAdapter;
import com.buysmart.ai.databinding.ActivityNearbyStoresBinding;
import com.buysmart.ai.models.NearbyStore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NearbyStoresActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private ActivityNearbyStoresBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private NearbyStoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearbyStoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupToolbar();
        setupRecyclerView();
        setupMap();
        setupClickListeners();

        checkLocationPermission();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        storeAdapter = new NearbyStoreAdapter(new ArrayList<>(), store -> {
            showStoreOnMap(store);
        });

        binding.rvStores.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStores.setAdapter(storeAdapter);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        binding.btnFindStores.setOnClickListener(v -> {
            getCurrentLocation();
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateMapToCurrentLocation(location);
                        findNearbyStoresInBangalore(location);
                    } else {
                        // Fallback to central Bangalore if location is null (emulator/sim)
                        Location bangalore = new Location("");
                        bangalore.setLatitude(12.9716);
                        bangalore.setLongitude(77.5946);
                        updateMapToCurrentLocation(bangalore);
                        findNearbyStoresInBangalore(bangalore);
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void updateMapToCurrentLocation(Location location) {
        if (googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }
    }

    private void findNearbyStoresInBangalore(Location currentLoc) {
        // Generate actual Bangalore locations
        List<NearbyStore> stores = getBangaloreStores();
        storeAdapter.updateStores(stores);
        
        if (googleMap != null) {
            googleMap.clear();
            // Current Location Marker
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()))
                    .title("Your Location"));

            for (NearbyStore store : stores) {
                if (store.getLocation() != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(store.getLocation())
                            .title(store.getName())
                            .snippet(store.getAddress()));
                }
            }
        }
    }

    private void showStoreOnMap(NearbyStore store) {
        if (googleMap != null && store.getLocation() != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(store.getLocation(), 15));
        }
    }

    private List<NearbyStore> getBangaloreStores() {
        List<NearbyStore> stores = new ArrayList<>();
        
        // 1. Croma - MG Road
        stores.add(createStore("s1", "Croma MG Road", "MG Road, Bangalore", 12.9716, 77.5946, 0.8));
        
        // 2. Reliance Digital - Koramangala
        stores.add(createStore("s2", "Reliance Digital", "Koramangala 5th Block", 12.9352, 77.6245, 1.5));
        
        // 3. Apple Imagine - Indiranagar
        stores.add(createStore("s3", "Imagine Store", "100 Ft Rd, Indiranagar", 12.9719, 77.6412, 2.2));
        
        // 4. Vijay Sales - Whitefield
        stores.add(createStore("s4", "Vijay Sales", "ITPL Main Rd, Whitefield", 12.9845, 77.7214, 5.4));
        
        // 5. Poorvika Mobiles - Jayanagar
        stores.add(createStore("s5", "Poorvika Mobiles", "4th Block, Jayanagar", 12.9285, 77.5834, 3.1));

        return stores;
    }

    private NearbyStore createStore(String id, String name, String addr, double lat, double lng, double dist) {
        NearbyStore s = new NearbyStore();
        s.setStoreId(id);
        s.setName(name);
        s.setAddress(addr);
        s.setLocation(new LatLng(lat, lng));
        s.setDistance(dist);
        s.setRating(4.0 + (Math.random() * 1.0));
        s.setOpen(true);
        return s;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
