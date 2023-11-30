package com.app.syspoint.ui.products.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.utils.Actividades;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView lector;
    public static String valorCodigoDeBarras;

    public static final int SCANNER_RESULT = 0x400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lector = new ZXingScannerView(this);
        setContentView(lector);
    }

    @Override
    public void handleResult(Result result) {

        ScannerActivity.valorCodigoDeBarras = result.getText();

        //Creamos el inten para poder retornar la informacion a la actividad anterior
        Intent intent = new Intent();
        intent.putExtra(Actividades.PARAM_1, result.getText());
        setResult(SCANNER_RESULT, intent);

        onBackPressed();
    }

    //Sobre escribimos los metos para el control de los statos de la actividad
    @Override
    protected void onPause() {
        super.onPause();
        lector.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lector.setResultHandler(this);
        lector.startCamera();
        lector.setFlash(true);
        lector.setAutoFocus(true);
    }
}