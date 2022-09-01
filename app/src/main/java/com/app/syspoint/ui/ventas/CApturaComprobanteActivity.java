package com.app.syspoint.ui.ventas;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.repository.request.http.ApiServices;
import com.app.syspoint.repository.request.http.PointApi;
import com.app.syspoint.models.ResponseVenta;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CApturaComprobanteActivity extends AppCompatActivity {


    private ImageView mPhotoImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    FloatingActionButton fb_open_camera;
    Button btn_load_imagen;
    private String ventaID;
    protected RelativeLayout rlprogress_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_comprobante);
        initToolBar();
        initControls();


        Intent intent = getIntent();
        ventaID = intent.getStringExtra(Actividades.PARAM_1);
        rlprogress_image = findViewById(R.id.rlprogress_image);
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_comprobante);
        toolbar.setTitle("Captura comprobante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_500));
        }
    }

    private void initControls(){

        mPhotoImage = findViewById(R.id.imageView4);

        fb_open_camera = findViewById(R.id.fb_open_camera);
        fb_open_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraApp();
            }
        });

        btn_load_imagen = findViewById(R.id.btn_load_imagen);
        btn_load_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File image = getPicture();
                MultipartBody.Part imagen = null;
                RequestBody cobranza = RequestBody.create(MultipartBody.FORM, ventaID);
                if (image  != null){
                    RequestBody imagenBody = RequestBody.create(MediaType.parse("image/jpg"), image);
                    imagen = MultipartBody.Part.createFormData("imagen", image.getName(), imagenBody);
                }

                Call<ResponseVenta> sendFile = ApiServices.getClientRestrofit().create(PointApi.class).postFile(cobranza, imagen);
                showProgressIndicator(true);
                sendFile.enqueue(new Callback<ResponseVenta>() {

                    @Override
                    public void onResponse(Call<ResponseVenta> call, Response<ResponseVenta> response) {

                        if (response.isSuccessful()){
                            hidenProgress();
                            Toast.makeText(CApturaComprobanteActivity.this, "El comprobante se subio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            hidenProgress();
                            Toast.makeText(CApturaComprobanteActivity.this, "El comprobante se subio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseVenta> call, Throwable t) {
                        hidenProgress();
                    }
                });
            }
        });

    }


    private void showCameraApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //Log.d(TAG, "Error ocurrido cuando se estaba creando el archivo de la imagen. Detalle: " + ex.toString());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.app.syspoint.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = Utils.formatDateForFileName(new Date());

        String prefix = "JPEG_" + timeStamp + "_";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                prefix,
                ".jpg",
                directory
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                handleCameraPhoto();
            }
        }
    }

    private void handleCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            showPhoto();
        }
    }

    private void showPhoto() {
        Glide.with(this).
                load(mCurrentPhotoPath).
                into(mPhotoImage);
        mPhotoImage.setVisibility(View.VISIBLE);
    }


    private File getPicture() {
        File file = null;

        if (mCurrentPhotoPath != null) {
            file = new File(mCurrentPhotoPath);
        }
        return file;
    }

    private void showProgressIndicator(boolean show) {
        rlprogress_image.setVisibility(View.VISIBLE);
    }


    private void hidenProgress(){
        rlprogress_image.setVisibility(View.GONE);
    }

}