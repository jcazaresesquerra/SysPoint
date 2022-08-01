package com.app.syspoint.ui.ventas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.R;
import com.app.syspoint.utils.Actividades;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class CantidadActivity extends AppCompatActivity {

    private EditText editTextCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantidad);
        initControls();
    }

    private void initControls() {

        this.editTextCantidad = findViewById(R.id.edittext_cantidad_venta_seleccionada);

        Button buttonAceptar = (Button) findViewById(R.id.button_seleccionar_cantidad_venta);

        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cantidad = editTextCantidad.getText().toString();
                if (cantidad.isEmpty()){
                    final PrettyDialog dialog = new PrettyDialog(CantidadActivity.this);
                    dialog.setTitle("Cantidad")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe ingresar la cantidad a vender")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });

                    dialog.setCancelable(false);
                    dialog.show();
                    return;
                }

                if (cantidad != null ){

                    //Establece el resultado que debe de regresar
                    Intent intent = new Intent();
                    intent.putExtra(Actividades.PARAM_1, cantidad);
                    setResult(Activity.RESULT_OK, intent);

                    //Cierra la actividad
                    finish();
                }else{
                    final PrettyDialog dialog = new PrettyDialog(CantidadActivity.this);
                    dialog.setTitle("Cantidad")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe ingresar la cantidad a vender")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });

                    dialog.setCancelable(false);
                    dialog.show();
                    return;
                }
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextCantidad.getWindowToken(), 0);
        editTextCantidad.requestFocus();
        showKeyboards(this);
    }


    public static void showKeyboards(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}