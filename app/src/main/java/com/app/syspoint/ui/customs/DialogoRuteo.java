package com.app.syspoint.ui.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class DialogoRuteo extends Dialog {

    Spinner spinnerDias;
    Spinner spinnerRuta;
    private DialogListener mReadyListener;
    String rutaSeleccionada, diaSeleccionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ruteo_condional);

        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            rutaSeleccionada = ruteoBean.getRuta();

            if (ruteoBean.getDia() == 1){
                diaSeleccionado = "Luneas";
            }else if(ruteoBean.getDia() == 2){
                diaSeleccionado = "Martes";
            }if(ruteoBean.getDia() == 3){
                diaSeleccionado = "Miercoles";
            }if(ruteoBean.getDia() == 4){
                diaSeleccionado = "Jueves";
            }if(ruteoBean.getDia() == 5){
                diaSeleccionado = "Viernes";
            }if(ruteoBean.getDia() == 6){
                diaSeleccionado = "Sabado";
            }if(ruteoBean.getDia() == 7){
                diaSeleccionado = "Domingo";
            }
        }

        loadDias();
        loadRuta();

        Button buttonConfirmar =  findViewById(R.id.bt_establece_ruteo);
        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReadyListener.ready(diaSeleccionado, rutaSeleccionada);
                DialogoRuteo.this.dismiss();
            }
        });
    }

    private void loadDias(){

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.dias);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_status_producto, arrayList);
        spinnerDias = findViewById(R.id.spinner_dias_ruteo);
        spinnerDias.setAdapter(adapter);
        spinnerDias.setSelection(arrayList.indexOf(diaSeleccionado));
        spinnerDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                diaSeleccionado = spinnerDias.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadRuta(){
        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.ruteo_rango_rutas);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_status_producto, arrayList);
        spinnerRuta = findViewById(R.id.spinner_ruta_ruteo);
        spinnerRuta.setAdapter(adapter);
        spinnerRuta.setSelection(arrayList.indexOf(rutaSeleccionada));
        spinnerRuta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rutaSeleccionada = spinnerRuta.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected String[] getArrayString(final int id) {
        return getContext().getResources().getStringArray(id);
    }
    public DialogoRuteo(@NonNull Context context, DialogListener ready) {
        super(context);
        this.mReadyListener = ready;
    }

    public interface DialogListener {
        public void ready(String dia, String ruta);
        public void cancelled();
    }
}
