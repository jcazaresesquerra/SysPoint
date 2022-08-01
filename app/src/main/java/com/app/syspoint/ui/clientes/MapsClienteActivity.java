package com.app.syspoint.ui.clientes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.MapDirectionAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsClienteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {

    private static final int REQUEST_PERMISSION_LOCATION = 991;

    Geocoder geocoder;
    Address addresss;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private boolean isMapReady = false;
    private ImageView imageView;
    private FloatingActionButton imageViewSearch;
    private TextView pickUpText;
    Button setPickUpButton;
    Button bnt_direcction_client;
    String numero = "";
    String calle = "";
    String localidad = "";
    String colonia = "";
    String estado = "";
    String pais = "";
    String cp = "";
    String address = "";
    String lng = "";
    String lat = "";
    double latitudeMarker;
    double longitudMarket;
    String lngMarker = "";
    String latMarker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_cliente);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        setPickUpButton = findViewById(R.id.pickUpButton);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUp();
            }
        });

        bnt_direcction_client = findViewById(R.id.bnt_direcction_client);
        bnt_direcction_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Actividades.PARAM_1, numero);
                intent.putExtra(Actividades.PARAM_2, calle);
                intent.putExtra(Actividades.PARAM_3, localidad);
                intent.putExtra(Actividades.PARAM_4, colonia);
                intent.putExtra(Actividades.PARAM_5, estado);
                intent.putExtra(Actividades.PARAM_6, pais);
                intent.putExtra(Actividades.PARAM_7, cp);
                intent.putExtra(Actividades.PARAM_8, latMarker);
                intent.putExtra(Actividades.PARAM_9, lngMarker);
                setResult(Activity.RESULT_OK, intent);

                //Cierra la actividad
                finish();
            }
        });

        imageView = findViewById(R.id.back_btn);

        imageViewSearch = findViewById(R.id.fb_search_map);

        pickUpText = findViewById(R.id.pickUpText);

        pickUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity(1);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity(1);
            }
        });

       // updateLastLocation(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickUpText.setText(place.getAddress());
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latLng.latitude, latLng.longitude), 15f)
                    );
                    onPickUp();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, Objects.requireNonNull(status.getStatusMessage()));
            }
        }
    }

    private void onPickUp() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpLatLang = centerPos;
        requestAddress(centerPos, pickUpText);
    }


    private void requestAddress(LatLng latlang, final TextView textView) {
        if (latlang != null) {
            MapDirectionAPI.getAddress(latlang).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull final okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String json = Objects.requireNonNull(response.body()).string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject Jobject = new JSONObject(json);

                                    JSONArray ResultsJarray = Jobject.getJSONArray("results");
                                    JSONObject userdata = ResultsJarray.getJSONObject(0);
                                    JSONArray componentes =  userdata.getJSONArray("address_components");

                                   //
                                    JSONObject geometry =  userdata.optJSONObject("geometry");

                                    JSONObject geometriString  = geometry.optJSONObject("location");

                                 
                                    JSONObject number = componentes.getJSONObject(0);
                                    JSONObject number1 = componentes.getJSONObject(1);
                                    JSONObject number2 = componentes.getJSONObject(2);
                                    JSONObject number3 = componentes.getJSONObject(3);
                                    JSONObject number4 = componentes.getJSONObject(4);

                                    JSONObject number5 = componentes.getJSONObject(5);
                                    JSONObject number6 = null;
                                    if (componentes.length() == 7){
                                        number6 = componentes.getJSONObject(6);
                                    }else if (componentes.length() == 8){
                                        number6 = componentes.getJSONObject(7);
                                    }

                                     numero = number.getString("short_name");
                                     calle = number1.getString("short_name");
                                     localidad = number2.getString("short_name");
                                     colonia = number3.getString("short_name");
                                     estado = number4.getString("short_name");
                                     pais = number5.getString("short_name");
                                     cp = number6.getString("short_name");
                                     address = userdata.getString("formatted_address");

                                     lat = geometriString.get("lat").toString();
                                     lng = geometriString.get("lng").toString();

                                    textView.setText(address);
                                    Log.e("Json", userdata.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        updateLastLocation(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(true);

        gMap.setOnMarkerDragListener(this);
        isMapReady = true;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        updateLastLocation(true);

    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
 
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);
        int height = 150;
        int width = 160;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.market);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        if (lastKnownLocation != null) {
            LatLng place =  new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 20f)

                );
                gMap.addMarker(new MarkerOptions().position(place).title("Aqui estoy").draggable(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(20f));
            }


           // fetchNearDriver(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            onPickUp();
        }
    }

    private void openAutocompleteActivity(int request_code) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, request_code);

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
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

         latitudeMarker = marker.getPosition().latitude;
         longitudMarket = marker.getPosition().longitude;

         latMarker= String.valueOf(latitudeMarker);
         lngMarker = String.valueOf(longitudMarket);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(
                    marker.getPosition().latitude, marker.getPosition().longitude, 1);

                  String direccion = list.get(0).getAddressLine(0);
                  String cityName = list.get(0).getCountryName();
                  String stateName = list.get(0).getAdminArea();
                  String codigo = list.get(0).getPostalCode();
                 numero = list.get(0).getFeatureName();
                 calle = list.get(0).getThoroughfare();
                 localidad = list.get(0).getLocality();
                 colonia = list.get(0).getLocality();
                 estado = stateName;
                 pais = cityName;
                 cp = codigo;
                 address = direccion;

        } catch (IOException e) {
            e.printStackTrace();
        }
        pickUpText.setText(address);
    }
}