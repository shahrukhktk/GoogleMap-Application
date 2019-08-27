package com.ir_tech.googlemapexample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQ_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProvider;
    protected GoogleMap googleMap;
    protected GoogleApiClient mGoogleApiClient;
    Location myLocation;
    String lat , lng ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fetchLastLocation();
        }

    }

//    private void getLocationPermission()
//    {
//        String [] permissions = {FINE_LOCATION, COARSE_LOCATION};
//        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//            {
//                mLocationPermissionGranted = true;
//            }
//            else
//            {
//                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQ_CODE);
//            }
//        }
//        else
//        {
//            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQ_CODE);
//        }
//    }

    private void fetchLastLocation ()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, LOCATION_PERMISSION_REQ_CODE);
                return;
            }
        }
        Task<Location> task = mFusedLocationProvider.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    Toast.makeText(MapsActivity.this, myLocation.getLatitude() + "" + myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    private void buildGoogleApiClient ()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady (GoogleMap googleMap)
    {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, LOCATION_PERMISSION_REQ_CODE);
                return;
            }
        }

        if (myLocation != null) {
            mMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //add this here:
//            try {
//                // Customise the styling of the base map using a JSON object defined
//                // in a raw resource file.
//                boolean success = googleMap.setMapStyle(
//                        MapStyleOptions.loadRawResourceStyle(
//                                this, R.raw.mapstyle));
//
//                if (!success) {
//                    Log.e("MapActivity", "Style parsing failed.");
//                }
//            } catch (Resources.NotFoundException e) {
//                Log.e("MapActivity", "Can't find style. Error: ", e);
//            }

//        // Add a marker in Sydney and move the camera
//        LatLng kohat = new LatLng(33.571396, 71.445649);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I Am Here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            googleMap.addMarker(markerOptions);
        }

    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

}
