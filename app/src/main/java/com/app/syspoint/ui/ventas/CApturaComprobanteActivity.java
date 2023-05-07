package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
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

import com.app.syspoint.interactor.file.FileInteractor;
import com.app.syspoint.interactor.file.FileInteractorImp;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_500));
    }

    private void initControls(){

        mPhotoImage = findViewById(R.id.imageView4);

        fb_open_camera = findViewById(R.id.fb_open_camera);
        fb_open_camera.setOnClickListener(v -> showCameraApp());

        btn_load_imagen = findViewById(R.id.btn_load_imagen);
        btn_load_imagen.setOnClickListener(v -> {
            File image = getPicture();
            if (image== null) {
                Toast.makeText(CApturaComprobanteActivity.this, "Se necesita tomar primero la foto del comprobante", Toast.LENGTH_SHORT).show();

                return;
            }

            new FileInteractorImp().executePostFile(image, ventaID, new FileInteractor.OnPostFileListener() {
                @Override
                public void onPostFileSuccess() {
                    hidenProgress();
                    Toast.makeText(CApturaComprobanteActivity.this, "El comprobante se subio correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onPostFileError() {
                    hidenProgress();
                    Toast.makeText(CApturaComprobanteActivity.this, "El comprobante no se pudo subir correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });

    }


    private void showCameraApp() {
        if (!(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH)
                || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
            requestPermissions(
                   new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                     }, 100
            );
        }
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
                        "com.app.syspoint.provider",
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