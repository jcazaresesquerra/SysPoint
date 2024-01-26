package com.app.syspoint.ui.clientes.PreciosEspeciales;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.models.Price;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
import com.app.syspoint.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreciosEspecialesActivity extends AppCompatActivity {


    private AdapterListaPreciosEspeciales mAdapter;
    private List<SpecialPricesBox> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;
    private String idCliente;
    private String cuentaCliente;
    private String clienteID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precios_especiales);


        rlprogress = findViewById(R.id.rlprogress_precios_especiales);

        Intent intent = getIntent();
        idCliente = intent.getStringExtra(Actividades.PARAM_1);

        ClientDao clientDao = new ClientDao();
        ClientBox clienteBean = clientDao.getClientByAccount(idCliente);

        if (clienteBean != null) {
            cuentaCliente = clienteBean.getCuenta();
            clienteID = clienteBean.getCuenta();
        }



        lyt_productos = findViewById(R.id.lyt_productos_especiales);
        rlprogress = findViewById(R.id.rlprogress_precios_especiales);

        initToolBar();
        initControls();
        initRecyclerView();
    }


    private void initControls() {
        FloatingActionButton button = findViewById(R.id.fab_add_precioEspecial);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, cuentaCliente);
                Actividades.getSingleton(PreciosEspecialesActivity.this, ListadoProductosActivity.class ).muestraActividad(parametros);
            }
        });

        progressshow();
        new PriceInteractorImp().executeGetPricesByClient(cuentaCliente, new PriceInteractor.GetPricesByClientListener() {
            @Override
            public void onGetPricesByClientSuccess(@NonNull List<SpecialPricesBox> pricesByClientList) {
                mData = (List<SpecialPricesBox>) pricesByClientList;

                initRecyclerView();
                progresshide();
            }

            @Override
            public void onGGetPricesByClientError() {
                progresshide();
                // Toast.makeText(getBaseContext(), "Ha ocurrido un problema al obtener precios", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_precios_especiales_cliente);
        toolbar.setTitle("Precios especiales");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_precios_especiales_cliente, menu);


        final MenuItem searchMenuitem = menu.findItem(R.id.busPreciosEspeciales);
        final SearchView searchView = (SearchView) searchMenuitem.getActionView();


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){
                    searchMenuitem.collapseActionView();
                    searchView.setQuery("", false);
                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });




        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initRecyclerView(){

        if (mData == null) {
            mData = new ArrayList<>();
            mData = new SpecialPricesDao().getListaPrecioPorCliente(cuentaCliente);
        }

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_precios_especiales);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaPreciosEspeciales(mData, new AdapterListaPreciosEspeciales.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                SpecialPricesBox bean = mData.get(position);
                SpecialPricesDao dao = new SpecialPricesDao();
                //Cremos laa pregunta

                final PrettyDialog dialog = new PrettyDialog(PreciosEspecialesActivity.this);
                dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("¿Desea eliminar el producto?")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_600, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                bean.setActive(false);
                                bean.setFecha_sync(Utils.fechaActual());
                                bean.setUpdatedAt(Utils.fechaActualHMS());
                                dao.inserBox(bean);

                                if (!Utils.isNetworkAvailable(getApplication())) {
                                } else {
                                    enviaPrecioServidor();
                                }
                                mData = new SpecialPricesDao().getListaPrecioPorCliente(clienteID);
                                mData.removeIf(item -> !item.getActive());
                                mAdapter.setPrecios(mData);
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_700, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        });

                dialog.setCancelable(false);
                dialog.show();

            }
        });
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mData = new SpecialPricesDao().getListaPrecioPorCliente(clienteID);
        mData.removeIf(item -> !item.getActive());
        mAdapter.setPrecios(mData);
        goneBackground();
    }

    private void goneBackground(){
        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }
    }


    private void enviaPrecioServidor(){


        progressshow();

        //Instancia la base de datos
        final SpecialPricesDao dao =  new SpecialPricesDao();

        //Contiene la lista de precios de la db local
        List<SpecialPricesBox> listaDB = new ArrayList<>();

        //Obtenemos la lista por id cliente
        listaDB = dao.getListaPrecioPorClienteUpdate(clienteID);


        //Contiene la lista de lo que se envia al servidor
        final List<Price> listaPreciosServidor = new ArrayList<>();

        //Contien la lista de precios especiales locales
        for (SpecialPricesBox items : listaDB){

            final Price precio = new Price();
            if (items.getActive()){
                precio.setActive(1);
            }else{
                precio.setActive(0);
            }

            precio.setArticulo(items.getArticulo());
            precio.setCliente(items.getCliente());
            precio.setPrecio(items.getPrecio());
            precio.setUpdatedAt(items.getUpdatedAt());
            listaPreciosServidor.add(precio);
        }

        new PriceInteractorImp().executeSendPrices(listaPreciosServidor, new PriceInteractor.SendPricesListener() {
            @Override
            public void onSendPricesSuccess() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSendPricesError() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }
}
