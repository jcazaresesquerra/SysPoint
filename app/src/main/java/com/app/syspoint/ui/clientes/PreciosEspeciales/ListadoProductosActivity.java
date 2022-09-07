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
import com.app.syspoint.models.json.SpecialPriceJson;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.PreciosEspecialesBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.SpecialPricesDao;
import com.app.syspoint.repository.database.dao.ProductDao;
import com.app.syspoint.repository.request.http.ApiServices;
import com.app.syspoint.repository.request.http.PointApi;
import com.app.syspoint.models.Price;
import com.app.syspoint.ui.products.adapters.AdapterListaProductos;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListadoProductosActivity extends AppCompatActivity {


    private AdapterListaProductos mAdapter;
    private List<ProductoBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;
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

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_productos_ec);
        toolbar.setTitle("Productos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }

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

    private void initRecyclerView() {

        mData = new ArrayList<>();
        mData = (List<ProductoBean>) (List<?>) new ProductDao().list();

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_productos_ec);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaProductos(mData, new AdapterListaProductos.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoBean productoBean) {
                articuloSeleccionado = productoBean.getArticulo();
                showDialogo();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    private void showDialogo() {

        final Dialog dialogo = new Dialog(ListadoProductosActivity.this);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogo.setContentView(R.layout.dialogo_precios);

        final EditText edittext_precio_especial = dialogo.findViewById(R.id.edittext_precio_especial);
        final Button buttonConfirmar = dialogo.findViewById(R.id.bt_aplicar_precio);

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (edittext_precio_especial.getText().toString().isEmpty()){
                    final PrettyDialog dialogos = new PrettyDialog(ListadoProductosActivity.this);
                    dialogos.setTitle("Precio")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("El precio no puede quedar vacio")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogos.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.red_700, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogos.dismiss();

                                }
                            });

                    dialogos.setCancelable(false);
                    dialogos.show();
                    return;
                }

                final String precio = edittext_precio_especial.getText().toString();

                ProductDao productDao = new ProductDao();
                ProductoBean productoBean = productDao.getProductoByArticulo(articuloSeleccionado);

                ClientDao clientDao = new ClientDao();
                ClienteBean clienteBean = clientDao.getClientByAccount(idCliente);

                if (precio != null ){

                    //Validamos si existe el precio
                    final SpecialPricesDao dao = new SpecialPricesDao();
                    final PreciosEspecialesBean bean = dao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                    if (bean == null) {
                        //Crea
                        SpecialPricesDao specialPricesDao = new SpecialPricesDao();
                        PreciosEspecialesBean preciosEspecialesBean = new PreciosEspecialesBean();

                        preciosEspecialesBean.setArticulo(productoBean.getArticulo());
                        preciosEspecialesBean.setCliente(clienteBean.getCuenta());
                        preciosEspecialesBean.setPrecio(Double.parseDouble(precio));
                        preciosEspecialesBean.setActive(true);
                        preciosEspecialesBean.setFecha_sync(Utils.fechaActual());
                        specialPricesDao.save(preciosEspecialesBean);
                        //Sincroniza la información con el servidor


                        if (!Utils.isNetworkAvailable(getApplication())) {
                            showDialogNotConnectionInternet();
                        } else {
                            enviaPrecioServidor();
                        }
                        dialogo.dismiss();
                        finish();
                    }else {

                        final PrettyDialog dialog = new PrettyDialog(ListadoProductosActivity.this);
                        dialog.setTitle("Existe")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("¿El precio especial ya existe desea actualizar?")
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

                                        bean.setPrecio(Double.parseDouble(precio));
                                        dao.save(bean);

                                        if (!Utils.isNetworkAvailable(getApplication())) {
                                            showDialogNotConnectionInternet();
                                        } else {
                                            enviaPrecioServidor();
                                        }
                                        dialog.dismiss();
                                        finish();
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
                }else{
                    final PrettyDialog dialog = new PrettyDialog(ListadoProductosActivity.this);
                    dialog.setTitle("Precio")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe ingresar el precio especial")
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

                }
            }
        });
        dialogo.show();
    }

    private void showDialogNotConnectionInternet() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviaPrecioServidor();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void enviaPrecioServidor(){

        progressshow();

        //Instancia la base de datos
        final SpecialPricesDao dao =  new SpecialPricesDao();

        //Contiene la lista de precios de la db local
        List<PreciosEspecialesBean> listaDB = new ArrayList<>();

        //Obtenemos la lista por id cliente
        listaDB = dao.getListaPrecioPorCliente(idCliente);


        //Contiene la lista de lo que se envia al servidor
        final List<Price> listaPreciosServidor = new ArrayList<>();

        //Contien la lista de precios especiales locales
        for (PreciosEspecialesBean items : listaDB){

            final Price precio = new Price();
            if (items.getActive() == true){
                precio.setActive(1);
            }else{
                precio.setActive(0);
            }

            precio.setArticulo(items.getArticulo());
            precio.setCliente(items.getCliente());
            precio.setPrecio(items.getPrecio());
            listaPreciosServidor.add(precio);

        }

        new PriceInteractorImp().executeSendPrices(listaPreciosServidor, new PriceInteractor.SendPricesListener() {
            @Override
            public void onSendPricesSuccess() {
                progresshide();
                Toast.makeText(ListadoProductosActivity.this, "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSendPricesError() {
                progresshide();
                Toast.makeText(ListadoProductosActivity.this, "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
                finish();
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