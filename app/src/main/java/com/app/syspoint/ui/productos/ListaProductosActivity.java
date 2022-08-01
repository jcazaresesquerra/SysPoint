package com.app.syspoint.ui.productos;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.db.bean.PersistenciaPrecioBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.dao.PersistenciaPrecioDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.ui.ventas.CantidadActivity;
import com.app.syspoint.utils.Actividades;

import java.util.ArrayList;
import java.util.List;

public class ListaProductosActivity extends AppCompatActivity {

    private AdapterListaProductosVentas mAdapter;
    private List<ProductoBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;
    public static String articuloSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        lyt_productos = findViewById(R.id.lyt_productos);
        rlprogress = findViewById(R.id.rlprogress_productos);

        this.initToolBar();
        this.initRecyclerView();
        this.initControls();
    }

    private void initControls() {
        FloatingActionButton button = findViewById(R.id.fab_add_barcoder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
            }
        });
    }

    private String obtienePersistencia(){

        String persistencia = "all";

        PersistenciaPrecioDao dao = new PersistenciaPrecioDao();
        PersistenciaPrecioBean precioBean = dao.getPersistencia();
        if (precioBean != null){
            if (precioBean.getMostrar().compareToIgnoreCase("all") == 0){
                persistencia = "all";
            }else {
                persistencia = "existencia";
            }
        }
        return  persistencia;
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_productos);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista_productos, menu);



        final MenuItem searchMenuItem = menu.findItem(R.id.buscarProducto);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);

                }
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
                final PersistenciaPrecioDao dao1 = new PersistenciaPrecioDao();
                final PersistenciaPrecioBean bean1 = dao1.getPersistencia();

                if (bean1 != null){
                    bean1.setMostrar("all");
                    dao1.save(bean1);
                }
                mData = (List<ProductoBean>) (List<?>) new ProductoDao().list();
                refreshData(mData);
                return true;

            case R.id.by_disponibles:

                final PersistenciaPrecioDao dao = new PersistenciaPrecioDao();
                final PersistenciaPrecioBean bean = dao.getPersistencia();

                if (bean != null){
                    bean.setMostrar("existencia");
                    dao.save(bean);
                }

                mData = new ArrayList<>();
                mData = (List<ProductoBean>) (List<?>) new ProductoDao().getProductosInventario();
                refreshData(mData);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initRecyclerView() {
        String persistencia =  obtienePersistencia();
        mData = new ArrayList<>();

        if (persistencia.compareToIgnoreCase("all") == 0){
            mData = (List<ProductoBean>) (List<?>) new ProductoDao().list();
        }else {
            mData = (List<ProductoBean>) (List<?>) new ProductoDao().getProductosInventario();
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

        mAdapter = new AdapterListaProductosVentas(mData, new AdapterListaProductosVentas.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ProductoBean producto = mData.get(position);
                articuloSeleccionado = producto.getArticulo();
                Actividades.getSingleton(ListaProductosActivity.this, CantidadActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);

            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    private void refreshData(List<ProductoBean> data){
        mAdapter.setData(data);
        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED)
            return;

        String cantidad = data.getStringExtra(Actividades.PARAM_1);

        //Establece el resultado que debe de regresar
        Intent intent = new Intent();
        intent.putExtra(Actividades.PARAM_1, cantidad);
        intent.putExtra(Actividades.PARAM_2, articuloSeleccionado);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }
}