package com.app.syspoint.ui.home.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.models.enums.RoleType;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.dao.RuteClientDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.repository.objectBox.entities.RuteClientBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
import com.app.syspoint.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.app.syspoint.R;
import com.app.syspoint.ui.dialogs.DialogOptionsClients;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class MapsRuteoActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private final static String TAG = "MapsRuteoActivity";
    List<RuteClientBox> mData = null;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private DialogOptionsClients dialogOptionsClients;

    private boolean markerClick = false;

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

        FloatingActionButton publicSale = findViewById(R.id.fb_public_sale);
        publicSale.setOnClickListener(v -> {
            makePublicSale();
        });

    }

    private void makePublicSale() {
        ClientDao clientDao = new ClientDao();
        ClientBox client = clientDao.getClientGeneralPublic();
        if (client == null) {
            client = new ClientBox(1L, "Publico General",
                    "Industrias del Valle", "1", "Parque Canacintra",
                    "Culiacán Rosales", 80150, "22-08-2021",
                    "000000", true, "000001", "01", 0,0,0,0,
                    0,0,0, 0,0,0,0,0,
                    0,0, 0, "24.777435983809422",
                    "-107.437107128804", null, null,
                    false, 0, false, 0.0,
                    0.0, null, "2022-11-08 00:00:00", Utils.fechaActualHMS(), 0, "", "", "");
            try {
                clientDao.insertBox(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, client.getCuenta());

        Timber.tag(TAG).d("Public Sale -> click -> open VentasActivity -> %s", client.getCuenta());

        Actividades.getSingleton(this, VentasActivity.class).muestraActividad(parametros);
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
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

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
            mData = (List<RuteClientBox>) new RuteClientDao().getAllRutaClientes(ruteoBean.getRuta(), ruteoBean.getDia());
        }

        gMap.clear();
        for (int i = 1; i < mData.size(); i++) {
            RuteClientBox item = mData.get(i);
            if (item.getLatitud() != null && item.getLongitud() != null) {
                LatLng position = new LatLng(Double.parseDouble(item.getLatitud()), Double.parseDouble(item.getLongitud()));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(position);
                markerOptions.title(item.getNombre_comercial());
                Bitmap bitmap = getBitmapMarker(i);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                markerOptions.snippet(item.getCuenta());
                markerOptions.draggable(true);
                gMap.addMarker(markerOptions);

            }
        }

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
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SysPoint", "Location, permission denied");
            return;
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        if (lastKnownLocation != null) {
            double userLat = lastKnownLocation.getLatitude();
            double userLong = lastKnownLocation.getLongitude();
            position = new LatLng(userLat, userLong);
            CameraPosition camera = new CameraPosition.Builder()
                    .target(position)
                    .zoom(15)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        } else if (lastKnownPosition != null) {
            double userLat = lastKnownPosition.latitude;
            double userLong = lastKnownPosition.longitude;
            position = new LatLng(userLat, userLong);
            CameraPosition camera = new CameraPosition.Builder()
                    .target(position)
                    .zoom(15)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        } else if (position != null) {
            CameraPosition camera  = new CameraPosition.Builder()
                    .target(position)
                    .zoom(15)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        } else {
            onMapReady(gMap);
        }*/
    }

    void getDataRuteo(){
        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

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

            mData = (List<RuteClientBox>)new RuteClientDao().getAllRutaClientes(ruteoBean.getRuta(), ruteoBean.getDia());
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

        if (!markerClick) {
            markerClick = true;
            Timber.tag(TAG).d("onMarkerClick");
            ClientDao clientDao = new ClientDao();
            ClientBox clienteBean = clientDao.getClientByAccount(marker.getSnippet());
            RoutingDao routingDao = new RoutingDao();
            RoutingBox ruteoBean = routingDao.getRutaEstablecida();

            boolean isOrderRute = false;
            EmployeeBox vendedoresBean = getEmployee();
            String consecAccount = "";
            if (vendedoresBean !=  null) {
                RolesBox rutasRol = new RolesDao().getRolByEmpleado(vendedoresBean.getIdentificador(), RoleType.ORDER_RUTES.getValue());
                isOrderRute = rutasRol != null && rutasRol.getActive();
                if (ruteoBean != null && ruteoBean.getDia() > 0) {
                    String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();

                    List<RuteClientBox> clients = new RuteClientDao().getAllRutaClientes(ruta, ruteoBean.getDia());
                    if (clients != null && !clients.isEmpty()) {
                        consecAccount = clients.get(0).getCuenta();
                    }
                }
            }


            boolean finalIsOrderRute = isOrderRute;
            String finalConsecAccount = consecAccount;

            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle("" + clienteBean.getNombre_comercial())
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Adeudo: $" + clienteBean.getSaldo_credito() + "0")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            markerClick = false;
                        }
                    })
                    .addButton("Realizar venta", R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            Timber.tag(TAG).d("onMarkerClick -> doSell -> click");
                            boolean canSell = true;
                            if (finalIsOrderRute) {
                                if (!clienteBean.getCuenta().equals(finalConsecAccount)) {
                                    canSell = false;
                                    showOrderRuteMessage();
                                }
                            }

                            if (canSell) {
                                HashMap<String, String> parametros = new HashMap<>();
                                parametros.put(Actividades.PARAM_1, marker.getSnippet());
                                Actividades.getSingleton(MapsRuteoActivity.this, VentasActivity.class).muestraActividad(parametros);
                                dialog.dismiss();
                                markerClick = false;
                            }
                            markerClick = false;
                            dialog.dismiss();
                        }
                    })
                    .addButton("Llamar al cliente", R.color.pdlg_color_white, R.color.purple_500, () -> {

                        Timber.tag(TAG).d("onMarkerClick -> call client -> click");
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+52" + clienteBean.getContacto_phone()));
                        startActivity(intent);
                        markerClick = false;
                    })
                    .addButton("¿Como llegar?", R.color.pdlg_color_white, R.color.purple_500, () -> {
                        Timber.tag(TAG).d("onMarkerClick -> show rute -> click");

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + clienteBean.getLatitud() + "," + clienteBean.getLongitud()));
                        startActivity(intent);
                        dialog.dismiss();
                        markerClick = false;
                    })
                    .addButton("Cancelar", R.color.white, R.color.red_900, () -> {
                        Timber.tag(TAG).d("onMarkerClick -> cancel -> click");
                        dialog.dismiss();
                        markerClick = false;
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
        return false;
    }

    private Bitmap getBitmapMarker(int order) {
        RelativeLayout marker = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.maps_marker, null, false);
        marker.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        marker.layout(0, 0, marker.getMeasuredWidth(), marker.getMeasuredHeight());

        TextView text = marker.findViewById(R.id.marker_number);
        text.setText(String.valueOf(order));

        ImageView iv = marker.findViewById(R.id.marker);
        if (order == 1) {
            iv.setImageResource(R.drawable.gm_mark_orange);
        } else {
            iv.setImageResource(R.drawable.gm_mark_red);
        }

        marker.setDrawingCacheEnabled(true);
        marker.buildDrawingCache();
        Bitmap bm = marker.getDrawingCache();
        return bm;
    }

    private void showOrderRuteMessage() {
        Timber.tag(TAG).d("showOrderRuteMessage");
        Toast.makeText(this, "Es obligatorio seguir la secuencia del listado", Toast.LENGTH_SHORT).show();
    }

    private EmployeeBox getEmployee() {
        EmployeeBox vendedoresBean = AppBundle.getUserBox();
        if (vendedoresBean == null) {
            SessionBox sessionBox = new SessionDao().getUserSession();
            if (sessionBox != null) {
                vendedoresBean = new EmployeeDao().getEmployeeByID(sessionBox.getEmpleadoId());
            } else {
                vendedoresBean = new CacheInteractor().getSeller();
            }
        }
        return vendedoresBean;
    }
}