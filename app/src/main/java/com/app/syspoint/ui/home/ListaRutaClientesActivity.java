package com.app.syspoint.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.R;
import com.app.syspoint.db.bean.ClienteBean;

import java.util.List;

public class ListaRutaClientesActivity extends AppCompatActivity {

    AdapterRutaClientes mAdapter;
    List<ClienteBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ruta_clientes);
    }

}