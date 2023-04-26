package com.shorno.hikerswatchv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView details;
    Location currentlocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        details = findViewById(R.id.details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if (location!= null){
                            currentlocation= location;
                            LatLng userLocation = new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(userLocation).title("You are here");
                            googleMap.addMarker(markerOptions);

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,12));
                        }else {
                            Toast.makeText(MainActivity.this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                        }

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        try {
                            List<Address> addressList =geocoder.getFromLocation(currentlocation.getLatitude(),currentlocation.getLongitude(),1);
                        if (addressList!=null){
                        String address= " ";
                        if (addressList.get(0).getCountryName()!= null){
                            address +=addressList.get(0).getCountryName()+ "\n" + " ";
                        }
                            if (addressList.get(0).getThoroughfare() != null) {
                                address += "Locality : " + addressList.get(0).getThoroughfare() + "\n" + " ";
                            }
                            if (addressList.get(0).getAdminArea() != null) {
                                address += "State : " + addressList.get(0).getAdminArea() + "\n" + " ";
                            }
                            if (addressList.get(0).getCountryName() != null) {
                                address += "Country : " + addressList.get(0).getCountryName() + "\n" + " ";
                            }
                            if (addressList.get(0).getPostalCode() != null) {
                                address += "PIN code : " + addressList.get(0).getPostalCode() + "\n" + " ";
                            }
                            if (addressList.get(0).hasLatitude() != false) {
                                address += "latitude : " + addressList.get(0).getLatitude() + "\n" + " ";
                            }
                            if (addressList.get(0).hasLongitude() != false) {
                                address += "longitude : " + addressList.get(0).getLongitude() ;
                            }



                         details.setText(address);
                        }


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    }
                });
            }
        });
    }

    public void getMe(View view){
        getCurrentLocation();


    }

}