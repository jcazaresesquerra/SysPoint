package com.app.syspoint.ui.customs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.syspoint.R;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.dao.RuteClientDao;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DialogoRuteo extends Dialog {

    Spinner spinnerDias;
    Spinner spinnerRuta;
    private DialogListener mReadyListener;
    String rutaSeleccionada, diaSeleccionado;

    private boolean editRuta = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ruteo_condional);

        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            rutaSeleccionada = ruteoBean.getRuta();

            if (ruteoBean.getDia() == 1){
                diaSeleccionado = "Lunes";
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
        if (editRuta) {
            loadRuta();
        } else {
            hideRuta();
        }

        Button buttonConfirmar =  findViewById(R.id.bt_establece_ruteo);
        buttonConfirmar.setOnClickListener(v -> {
            mReadyListener.ready(diaSeleccionado, rutaSeleccionada);
            DialogoRuteo.this.dismiss();
        });
    }

    private void loadDias(){
        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.dias);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();


        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_status_producto, arrayList);
        spinnerDias = findViewById(R.id.spinner_dias_ruteo);
        spinnerDias.setAdapter(adapter);
        String dia = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        if (dia.compareToIgnoreCase("Monday") == 0) {
            diaSeleccionado = "Lunes";
        } else if (dia.compareToIgnoreCase("Tuesday") == 0) {
            diaSeleccionado = "Martes";
        } else if (dia.compareToIgnoreCase("Wednesday") == 0) {
            diaSeleccionado = "Miercoles";
        } else if (dia.compareToIgnoreCase("Thursday") == 0) {
            diaSeleccionado = "Jueves";
        } else if (dia.compareToIgnoreCase("Friday") == 0) {
            diaSeleccionado = "Viernes";
        } else if (dia.compareToIgnoreCase("Saturday") == 0) {
            diaSeleccionado = "Sabado";
        } else if (dia.compareToIgnoreCase("Sunday") == 0) {
            diaSeleccionado = "Domingo";
        }
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

    private void hideRuta() {
        TextView tvRuta = findViewById(R.id.tv_ruta);
        tvRuta.setVisibility(View.GONE);
        spinnerRuta = findViewById(R.id.spinner_ruta_ruteo);
        spinnerRuta.setVisibility(View.GONE);
    }

    private void loadRuta(){
        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.ruteo_rango_rutas);

        final RuteClientDao dao = new RuteClientDao();

        //Obtiene la lista de Strings
        //List<String> arrayList = dao.getAllRutes();
        List<String> arrayList = Utils.convertArrayStringListString(array);
        /*if (arrayList.isEmpty()) {
            arrayList = Utils.convertArrayStringListString(array);
        }*/

        //Creamos el adaptador
        TextView tvRuta = findViewById(R.id.tv_ruta);
        tvRuta.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_status_producto, arrayList);
        spinnerRuta = findViewById(R.id.spinner_ruta_ruteo);
        spinnerRuta.setVisibility(View.VISIBLE);
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
    public DialogoRuteo(@NonNull Context context, boolean editRuta, DialogListener ready) {
        super(context);
        this.editRuta = editRuta;
        this.mReadyListener = ready;
    }

    public interface DialogListener {
        public void ready(String dia, String ruta);
        public void cancelled();
    }
}
