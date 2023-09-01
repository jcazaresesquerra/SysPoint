package com.app.syspoint.ui.ventas;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.models.enums.RoleType;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.PricePersistenceDao;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.PersistancePricesBox;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;
import com.app.syspoint.ui.products.activities.ScannerActivity;
import com.app.syspoint.ui.ventas.adapter.AdapterListaProductosVentas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.utils.Actividades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListaProductosActivity extends AppCompatActivity {

    private AdapterListaProductosVentas mAdapter;
    private List<ProductBox> mData;
    private LinearLayout lyt_productos;
    public static String articuloSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        lyt_productos = findViewById(R.id.lyt_productos);
        RelativeLayout rlprogress = findViewById(R.id.rlprogress_productos);

        this.initToolBar();
        this.initRecyclerView();
        this.initControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED) return;

        if (resultCode == ScannerActivity.SCANNER_RESULT) {
            String barCode = data.getStringExtra(Actividades.PARAM_1);
            HashMap<String, String> parametros = new HashMap<String, String>();
            parametros.put(Actividades.PARAM_1, barCode);

            ProductBox productBox = new ProductDao().getProductoByBarCode(barCode);
            if (productBox != null) {
                articuloSeleccionado = productBox.getArticulo();
            }

            Actividades.getSingleton(ListaProductosActivity.this, CantidadActivity.class)
                    .muestraActividadForResultAndParams(Actividades.PARAM_INT_1, parametros);
            return;
        }


        String cantidad = data.getStringExtra(Actividades.PARAM_1);

        String barcode = data.getStringExtra(Actividades.PARAM_2);
        if (barcode != null && !barcode.isEmpty()) {
            ProductBox productBox = new ProductDao().getProductoByBarCode(barcode);
            if (productBox != null) {
                articuloSeleccionado = productBox.getArticulo();
            }
        }
        String returns = data.getStringExtra(Actividades.PARAM_3);


        //Establece el resultado que debe de regresar
        Intent intent = new Intent();
        intent.putExtra(Actividades.PARAM_1, cantidad);
        intent.putExtra(Actividades.PARAM_2, articuloSeleccionado);
        if (returns != null) {
            intent.putExtra(Actividades.PARAM_3, returns);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_productos, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.buscarProducto);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        menu.findItem(R.id.all).setVisible(false);
        menu.findItem(R.id.by_disponibles).setVisible(false);

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                //Intent intent = new Intent(MainActivity.this, ActivitySearchVideo.class);
                //intent.putExtra("search", arg0);
                //startActivity(intent);
                //searchView.clearFocus();
                mAdapter.getFilter().filter(arg0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                mAdapter.getFilter().filter(arg0);
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

            case R.id.all:
                mData = new ArrayList<>();
                final PricePersistenceDao dao1 = new PricePersistenceDao();
                final PersistancePricesBox bean1 = dao1.getPersistence();

                if (bean1 != null){
                    bean1.setMostrar("all");
                    dao1.inserBox(bean1);
                }
                mData = new ProductDao().getActiveProducts();
                refreshData(mData);
                return true;

            case R.id.by_disponibles:

                final PricePersistenceDao dao = new PricePersistenceDao();
                final PersistancePricesBox bean = dao.getPersistence();

                if (bean != null){
                    bean.setMostrar("existencia");
                    dao.inserBox(bean);
                }

                mData = new ArrayList<>();
                mData = (List<ProductBox>) new ProductDao().getProductosInventario();
                refreshData(mData);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initControls() {
        FloatingActionButton button = findViewById(R.id.fab_add_barcoder);
        button.setOnClickListener(v ->
                startActivityForResult(new Intent(getApplicationContext(), ScannerActivity.class), 0x2)
        );
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_productos);
        toolbar.setTitle("Productos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }

    private void initRecyclerView() {
        String persistencia =  obtienePersistencia();
        mData = new ArrayList<>();

        if (canSelectProducts()){
            mData = new ProductDao().getActiveProducts();
        }else {
            mData = new ProductDao().getProductosInventario();
        }

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_productos);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaProductosVentas(mData, productBox -> {
            articuloSeleccionado = productBox.getArticulo();
            Actividades.getSingleton(ListaProductosActivity.this, CantidadActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void refreshData(List<ProductBox> data){
        mAdapter.setData(data);
        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }
    }

    private String obtienePersistencia(){
        String persistencia = "all";

        PricePersistenceDao dao = new PricePersistenceDao();
        PersistancePricesBox precioBean = dao.getPersistence();
        if (precioBean != null){
            if (precioBean.getMostrar().compareToIgnoreCase("all") == 0){
                persistencia = "all";
            }else {
                persistencia = "existencia";
            }
        }
        return  persistencia;
    }

    private boolean canSelectProducts() {
        // get seller
        EmployeeBox employeeBox = getEmployee();

        // save seller in cache
        CacheInteractor cacheInteractor = new CacheInteractor();
        cacheInteractor.saveSeller(employeeBox);

        String identificador = employeeBox != null ? employeeBox.getIdentificador() : "";

        RolesDao rolesDao = new RolesDao();
        RolesBox rolesBean = rolesDao.getRolByEmpleado(identificador, RoleType.STOCK.getValue());

        return rolesBean != null && rolesBean.getActive();
    }

    private EmployeeBox getEmployee() {
        EmployeeBox vendedoresBean = AppBundle.getUserBox();
        if (vendedoresBean == null) {
            SessionBox sessionBox = new SessionDao().getUserSession();
            if (sessionBox != null) {
                vendedoresBean = new EmployeeDao().getEmployeeByID(sessionBox.getEmpleadoId());
            } else {
                vendedoresBean = new CacheInteractor().getSeller();
            }
        }
        return vendedoresBean;
    }
}