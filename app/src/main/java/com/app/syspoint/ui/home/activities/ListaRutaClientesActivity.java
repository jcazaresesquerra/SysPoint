package com.app.syspoint.ui.home.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.R;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.ui.home.adapter.AdapterRutaClientes;

import java.util.List;

public class ListaRutaClientesActivity extends AppCompatActivity {

    AdapterRutaClientes mAdapter;
    List<ClientBox> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ruta_clientes);
    }

}