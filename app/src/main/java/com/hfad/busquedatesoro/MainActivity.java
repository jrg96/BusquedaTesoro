package com.hfad.busquedatesoro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obteniendo el servicio fusedlocation
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Creando listeners
        this.crearListeners();

        // Si no hay access token o usuario, cerrar residuos de sesion, redirigir a logueo FB
        if ((AccessToken.getCurrentAccessToken() == null) || (mAuth.getCurrentUser() == null)) {
            this.cerrarSesion();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            mUser = mAuth.getCurrentUser();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mUser = mAuth.getCurrentUser();
        Toast.makeText(this, "Adquiriendo usuario despues de Auth", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Log.d("Dato", "Llamaada por haber permisos");
            this.executeLocationLogic();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    /*
     * Funcion: onRequestPermissionsResult datos
     *
     * Descripcion: Funcion para pedir permisos, en caso de obtener el permiso para obtener ubicacion
     * llama a executeLocationLogic para obtener la locacion del usuario
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                this.executeLocationLogic();
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        }
    }

    /*
     * Funcion: executeLocationLogic
     *
     * Descripcion: Funcion para obtener la localizacion exacta del usuario cada segundo
     */
    private void executeLocationLogic() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);

                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    //Place current location marker
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);

                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /*
     * Funcion: crearListeners
     *
     * Descripcion: Crear los callbacks para cada FAB que se muestra en la pantalla de inicio
     *
     */
    private void crearListeners(){
        FloatingActionButton fabLocalizar = (FloatingActionButton) findViewById(R.id.btn_localizar);
        fabLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrLocationMarker != null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocationMarker.getPosition(), 18));
                }
            }
        });

        FloatingActionButton fabCerrarSesion = (FloatingActionButton) findViewById(R.id.btn_cerrar_sesion);
        fabCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        FloatingActionButton fabCrearTesoro = (FloatingActionButton) findViewById(R.id.btn_agregar_tesoro);
        fabCrearTesoro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TesoroCrearActivity.class);
                intent.putExtra("latitud", "" + mCurrLocationMarker.getPosition().latitude);
                intent.putExtra("longitud", "" + mCurrLocationMarker.getPosition().longitude);
                startActivity(intent);
            }
        });
    }


    private void cerrarSesion(){
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e){
        }

        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e){
        }
    }
}
