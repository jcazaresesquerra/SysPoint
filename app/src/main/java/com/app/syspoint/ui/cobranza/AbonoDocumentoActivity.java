package com.app.syspoint.ui.cobranza;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.app.syspoint.R;
import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.entities.ChargeBox;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
import com.app.syspoint.utils.Utils;

public class AbonoDocumentoActivity extends AppCompatActivity {
    private EditText editTextCantidad;
    private TextView textView_importe_cobranza_documento;
    private double saldoDocumento;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abono_documetnto);
        this.init();
    }

    private void init() {

        this.editTextCantidad = findViewById(R.id.ti_importe_documento);

        this.textView_importe_cobranza_documento = findViewById(R.id.tv_importe_cobranza_documento);


        this.initButtons();

        this.initParametros();
        /**
         * Muestra el teclado
         * **/

        aSwitch = findViewById(R.id.switch_liquidar);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (aSwitch.isChecked()){
                    editTextCantidad.setText(textView_importe_cobranza_documento.getText().toString().replace("$", "").replace(",", "").trim());
                    editTextCantidad.setEnabled(false);
                }else{
                    editTextCantidad.setSelectAllOnFocus(true);
                    editTextCantidad.setFocusable(true);
                    editTextCantidad.setText("0.00");
                    editTextCantidad.setEnabled(true);
                }
            }
        });
    }

    private void initParametros() {
        try{
            ChargeDao chargeDao = new ChargeDao();
            ChargeBox cobranzaBean = chargeDao.getByCobranza(ListaDocumentosCobranzaActivity.documentoSeleccionado);
            textView_importe_cobranza_documento.setText(Utils.formatMoneyMX(cobranzaBean.getSaldo()));
            saldoDocumento = Double.parseDouble(textView_importe_cobranza_documento.getText().toString().replace("$","").replace(",","").trim());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initButtons() {
        Button buttonAceptar = (Button) findViewById(R.id.btn_abonar);

        buttonAceptar.setOnClickListener(v -> {

            String cantidad = editTextCantidad.getText().toString().replace(",", "");


            if (cantidad.isEmpty()){
                final PrettyDialog dialogo = new PrettyDialog(AbonoDocumentoActivity.this);
                dialogo.setTitle("Importe")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe de indicar el importe a abonar")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();
                return;
            }

            if (cantidad != null ){

                double importe = Double.parseDouble(cantidad);

                if (importe > saldoDocumento){


                    final PrettyDialog dialogo = new PrettyDialog(AbonoDocumentoActivity.this);
                    dialogo.setTitle("Importe")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("El importe es mayor al saldo del documento")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                    return;
                }

                //Establece el resultado que debe de regresar
                Intent intent = new Intent();
                intent.putExtra(Actividades.PARAM_1, cantidad);
                setResult(Activity.RESULT_OK, intent);

                //Cierra la actividad
                finish();
            }else{
               // dialogo = new Dialogo(activityGlobal);

                final PrettyDialog dialogo = new PrettyDialog(AbonoDocumentoActivity.this);
                dialogo.setTitle("Importe")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe de indicar el importe del documento")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();


                return;
            }
        });
    }



}