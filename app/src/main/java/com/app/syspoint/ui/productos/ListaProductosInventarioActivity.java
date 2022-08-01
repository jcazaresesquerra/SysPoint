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

import com.app.syspoint.R;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.ui.inventarios.CantidadInventarioActivity;
import com.app.syspoint.utils.Actividades;

import java.util.ArrayList;
import java.util.List;

public class ListaProductosInventarioActivity extends AppCompatActivity {

    private AdapterListaProductosInv mAdapter;
    private List<ProductoBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;
    public static String articuloSeleccionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos_inventario);

        lyt_productos = findViewById(R.id.lyt_productos_inv);
        rlprogress = findViewById(R.id.rlprogress_productos_inv);

        this.initToolBar();
        this.initRecyclerView();

    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_productos_inventarios);
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
        inflater.inflate(R.menu.menu_inventario, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.buscarProductoInv);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initRecyclerView() {

        mData = new ArrayList<>();
        mData = (List<ProductoBean>) (List<?>) new ProductoDao().list();

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_productos_inventario);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);


        mAdapter = new AdapterListaProductosInv(mData, new AdapterListaProductos.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ProductoBean producto = mData.get(position);
                articuloSeleccionado = producto.getArticulo();
                //Muestra el dialogo para seleccion de cantidades
                Actividades.getSingleton(ListaProductosInventarioActivity.this, CantidadInventarioActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);

            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED)
            return;

        String cantidad = data.getStringExtra(Actividades.PARAM_1);

        //Establece el resultado que debe de regresar
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

}