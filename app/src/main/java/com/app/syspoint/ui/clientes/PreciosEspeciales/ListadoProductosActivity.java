package com.app.syspoint.ui.clientes.PreciosEspeciales;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.models.Price;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox;
import com.app.syspoint.ui.products.adapters.AdapterListaProductos;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListadoProductosActivity extends AppCompatActivity {

    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;

    private AdapterListaProductos mAdapter;
    private List<ProductBox> mData;

    public static String articuloSeleccionado;
    private String idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_productos);

        Intent intent = getIntent();
        idCliente = intent.getStringExtra(Actividades.PARAM_1);

        lyt_productos = findViewById(R.id.lyt_producto_ec);
        rlprogress = findViewById(R.id.rlprogress_productos_ec);
        this.initToolBar();
        this.initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Si el usuario da click en la flecha regresa a la activity anterior
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_productos_especiales, menu);

        final MenuItem searchMMenuItem  = menu.findItem(R.id.buscaProductoListaEspecial);
        final SearchView searchView = (SearchView) searchMMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){
                    searchMMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_productos_ec);
        toolbar.setTitle("Productos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }

    private void initRecyclerView() {
        mData = new ArrayList<>();
        mData = (List<ProductBox>) new ProductDao().getActiveProducts();

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_productos_ec);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaProductos(mData, productoBean -> {
            articuloSeleccionado = productoBean.getArticulo();
            showDialogo();
        });
        recyclerView.setAdapter(mAdapter);
    }


    private void showDialogo() {
        Dialog dialogo = new Dialog(ListadoProductosActivity.this);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogo.setContentView(R.layout.dialogo_precios);

        EditText edittext_precio_especial = dialogo.findViewById(R.id.edittext_precio_especial);
        Button buttonConfirmar = dialogo.findViewById(R.id.bt_aplicar_precio);

        buttonConfirmar.setOnClickListener(v -> {
            if (edittext_precio_especial.getText().toString().isEmpty()){
                final PrettyDialog dialogos = new PrettyDialog(ListadoProductosActivity.this);
                dialogos.setTitle("Precio")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("El precio no puede quedar vacio")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> dialogos.dismiss())
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.red_700, () -> dialogos.dismiss());

                dialogos.setCancelable(false);
                dialogos.show();
                return;
            }

            final String precio = edittext_precio_especial.getText().toString();

            ProductDao productDao = new ProductDao();
            ProductBox productoBean = productDao.getProductoByArticulo(articuloSeleccionado);

            ClientDao clientDao = new ClientDao();
            ClientBox clienteBean = clientDao.getClientByAccount(idCliente);

            if (precio != null ){
                //Validamos si existe el precio
                SpecialPricesDao dao = new SpecialPricesDao();
                SpecialPricesBox bean = dao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                if (bean == null) {
                    //Crea
                    SpecialPricesDao specialPricesDao = new SpecialPricesDao();
                    SpecialPricesBox preciosEspecialesBean = new SpecialPricesBox();

                    preciosEspecialesBean.setArticulo(productoBean.getArticulo());
                    preciosEspecialesBean.setCliente(clienteBean.getCuenta());
                    preciosEspecialesBean.setPrecio(Double.parseDouble(precio));
                    preciosEspecialesBean.setActive(true);
                    preciosEspecialesBean.setFecha_sync(Utils.fechaActual());
                    specialPricesDao.inserBox(preciosEspecialesBean);
                    //Sincroniza la información con el servidor


                    if (!Utils.isNetworkAvailable(getApplication())) {
                        //showDialogNotConnectionInternet();
                    } else {
                        enviaPrecioServidor();
                    }
                    dialogo.dismiss();
                    finish();
                }else {
                    PrettyDialog dialog = new PrettyDialog(ListadoProductosActivity.this);
                    dialog.setTitle("Existe")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("¿El precio especial ya existe desea actualizar?")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> dialog.dismiss())
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_600, () -> {
                                bean.setPrecio(Double.parseDouble(precio));
                                dao.inserBox(bean);

                                if (!Utils.isNetworkAvailable(getApplication())) {
                                    //showDialogNotConnectionInternet();
                                } else {
                                    enviaPrecioServidor();
                                }
                                dialog.dismiss();
                                finish();
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_700, () ->
                                    dialog.dismiss()
                            );
                    dialog.setCancelable(false);
                    dialog.show();

                }
            }else{
                PrettyDialog dialog = new PrettyDialog(ListadoProductosActivity.this);
                dialog.setTitle("Precio")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe ingresar el precio especial")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> dialog.dismiss())
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, () -> dialog.dismiss());

                dialog.setCancelable(false);
                dialog.show();
            }
        });
        dialogo.show();
    }

    private void showDialogNotConnectionInternet() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            enviaPrecioServidor();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void enviaPrecioServidor(){
        progressshow();

        SpecialPricesDao dao =  new SpecialPricesDao();
        List<SpecialPricesBox> listaDB = dao.getListaPrecioPorCliente(idCliente);
        List<Price> listaPreciosServidor = new ArrayList<>();

        for (SpecialPricesBox items : listaDB){
            Price precio = new Price();
            precio.setActive(items.getActive()? 1 : 0);
            precio.setArticulo(items.getArticulo());
            precio.setCliente(items.getCliente());
            precio.setPrecio(items.getPrecio());
            listaPreciosServidor.add(precio);
        }

        new PriceInteractorImp().executeSendPrices(listaPreciosServidor, new PriceInteractor.SendPricesListener() {
            @Override
            public void onSendPricesSuccess() {
                progresshide();
                //Toast.makeText(ListadoProductosActivity.this, "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSendPricesError() {
                progresshide();
                //Toast.makeText(ListadoProductosActivity.this, "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void progressshow() { rlprogress.setVisibility(View.VISIBLE);}
    public void progresshide() { rlprogress.setVisibility(View.GONE);}
}