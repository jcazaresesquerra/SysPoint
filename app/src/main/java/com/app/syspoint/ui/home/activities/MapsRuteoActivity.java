package com.app.syspoint.ui.home.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.ui.dialogs.DialogOptionsClients;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class MapsRuteoActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {


    List<ClientesRutaBean> mData = null;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private MarkerOptions markerOptions;
    private DialogOptionsClients dialogOptionsClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_ruteo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getDataRuteo();


        dialogOptionsClients = new DialogOptionsClients(this);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewRuteo);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (mData != null) {
                if (mData.size() > 0) {
                    updateLastLocation(true);
                }
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mData != null) {
            if (mData.size() > 0) {
                updateLastLocation(true);
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mData != null) {
            if (mData.size() > 0) {
                updateLastLocation(true);
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mData != null) {
            if (mData.size() > 0) {
                updateLastLocation(true);
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMarkerClickListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
        gMap.setMyLocationEnabled(true);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 10, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 15f)
                );
            }
        });

        if (mData != null) {
            if (mData.size() > 0) {
                updateLastLocation(true);
            }
        }
    }

    private void updateLastLocation(boolean move) {

        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {

            //if (ruteoBean.getDia() == 1) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaLunes(ruteoBean.getRuta(), 1);
            //} else if (ruteoBean.getDia() == 2) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaMartes(ruteoBean.getRuta(), 1);
            //}
            //if (ruteoBean.getDia() == 3) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaMiercoles(ruteoBean.getRuta(), 1);
            //}
            //if (ruteoBean.getDia() == 4) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaJueves(ruteoBean.getRuta(), 1);
            //}
            //if (ruteoBean.getDia() == 5) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaViernes(ruteoBean.getRuta(), 1);
            //}
            //if (ruteoBean.getDia() == 6) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaSabado(ruteoBean.getRuta(), 1);
            //}
            //if (ruteoBean.getDia() == 7) {
            //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaDomingo(ruteoBean.getRuta(), 1);
            //}
            mData = (List<ClientesRutaBean>) (List<?>) new RuteClientDao().getAllRutaClientes();


        }

        for (ClientesRutaBean item : mData) {
            if (item.getLatitud() != null && item.getLongitud() != null ) {
               LatLng clients = new LatLng(Double.parseDouble(item.getLatitud()), Double.parseDouble(item.getLongitud()));

                markerOptions = new MarkerOptions();
                markerOptions.position(clients);
                markerOptions.title(item.getNombre_comercial());
                markerOptions.snippet(item.getCuenta());
                markerOptions.draggable(true);
                gMap.addMarker(markerOptions);

                CameraPosition camera  = new CameraPosition.Builder()
                        .target(clients)
                        .zoom(15)
                        .build();
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            }
        }
    }

    void getDataRuteo(){
        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {

           //if (ruteoBean.getDia() == 1) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaLunes(ruteoBean.getRuta(), 1);
           //} else if (ruteoBean.getDia() == 2) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaMartes(ruteoBean.getRuta(), 1);
           //}
           //if (ruteoBean.getDia() == 3) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaMiercoles(ruteoBean.getRuta(), 1);
           //}
           //if (ruteoBean.getDia() == 4) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaJueves(ruteoBean.getRuta(), 1);
           //}
           //if (ruteoBean.getDia() == 5) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaViernes(ruteoBean.getRuta(), 1);
           //}
           //if (ruteoBean.getDia() == 6) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaSabado(ruteoBean.getRuta(), 1);
           //}
           //if (ruteoBean.getDia() == 7) {
           //    mData = (List<ClientesRutaBean>) (List<?>) new ClientesRutaDao().getListaClientesRutaDomingo(ruteoBean.getRuta(), 1);
           //}

            mData = (List<ClientesRutaBean>) (List<?>) new RuteClientDao().getAllRutaClientes();
        }

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataRuteo();
        if (mData != null) {
            if (mData.size() > 0) {
                if (gMap != null) {
                    gMap.clear();
                    updateLastLocation(true);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        ClientDao clientDao = new ClientDao();
        ClienteBean clienteBean = clientDao.getClientByAccount( marker.getSnippet());


        final PrettyDialog dialog = new PrettyDialog(this);
        dialog.setTitle("" + clienteBean.getNombre_comercial())
                .setTitleColor(R.color.purple_500)
                .setMessage("Adeudo: $"+clienteBean.getSaldo_credito()+"0")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                })
                .addButton("Realizar venta", R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        HashMap<String, String> parametros = new HashMap<>();
                        parametros.put(Actividades.PARAM_1, marker.getSnippet());
                        Actividades.getSingleton(MapsRuteoActivity.this, VentasActivity.class).muestraActividad(parametros);
                        dialog.dismiss();
                    }
                })


                .addButton("Llamar al cliente", R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+52" + clienteBean.getContacto_phone()));
                            startActivity(intent);
                    }



                })
                 .addButton("¿Como llegar?", R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + clienteBean.getLatitud() + "," + clienteBean.getLongitud()));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
        .addButton("Cancelar", R.color.white, R.color.red_900, new PrettyDialogCallback() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        })
        ;
        dialog.setCancelable(false);
        dialog.show();
        return false;
    }
}