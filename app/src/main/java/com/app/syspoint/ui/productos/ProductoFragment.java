package com.app.syspoint.ui.productos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.utils.cache.CacheInteractor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.AppBundle;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.dao.ProductoDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.models.Product;
import com.app.syspoint.models.json.ProductoJson;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoFragment extends Fragment {

    private AdapterListaProductos mAdapter;
    private List<ProductoBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_productos;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_producto, container, false);
        setHasOptionsMenu(true);
        lyt_productos = root.findViewById(R.id.lyt_productos);
        rlprogress = root.findViewById(R.id.rlprogress_productos);

        FloatingActionButton fb = root.findViewById(R.id.fab_add_producto);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegistrarProductoActivity.class);
                startActivity(intent);
            }
        });


        initRecyclerView(root);
        return root;
    }

    private void initRecyclerView(View root) {

        mData = new ArrayList<>();
        mData = (List<ProductoBean>) (List<?>) new ProductoDao().list();

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_productos);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaProductos(mData, new AdapterListaProductos.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showSelecctionFunction(position);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    private String single_choice_selected = null;
    private void showSelecctionFunction(int position){

        final ProductoBean productoBean = mData.get(position);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Editar");

        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                String identificador = "";

                //Obtiene el nombre del vendedor
                EmpleadoBean vendedoresBean = AppBundle.getUserBean();

                if (vendedoresBean == null && getContext() != null) {
                    vendedoresBean = new CacheInteractor(getContext()).getSeller();
                }

                if (vendedoresBean != null){
                    identificador = vendedoresBean.getIdentificador();
                }
                final RolesDao rolesDao = new RolesDao();
                final RolesBean rolesBean = rolesDao.getRolByEmpleado(identificador, "Productos");


                if (strName == null || strName.compareToIgnoreCase("Editar") == 0 ){

                    if (rolesBean != null){

                        if (rolesBean.getActive()){
                            editProducto(productoBean.getArticulo());
                        }else {
                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                }

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void editProducto(String articulo){
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, articulo);
        Actividades.getSingleton(getActivity(), ActualizaProductoActivity.class).muestraActividad(parametros);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_producto_fragment, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
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

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.syncProductos:

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (!connected) {
                        showDialogNotConnectionInternet();
                    } else {
                        getData();
                    }
                }, getActivity()).execute(), 100);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogNotConnectionInternet() {

        final Dialog dialog = new Dialog(getActivity());
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
                getData();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void getData() {
        progressshow();

        Call<ProductoJson> getProducto = ApiServices.getClientRestrofit().create(PointApi.class).getAllProductos();


        getProducto.enqueue(new Callback<ProductoJson>() {
            @Override
            public void onResponse(Call<ProductoJson> call, Response<ProductoJson> response) {

                if (response.isSuccessful()) {
                    progresshide();

                    for (Product items : response.body().getProductos()) {

                        final ProductoDao productoDao = new ProductoDao();
                        final ProductoBean productoBean = productoDao.getProductoByArticulo(items.getArticulo());

                        if (productoBean == null) {
                            //Creamos el producto
                            ProductoBean producto = new ProductoBean();
                            ProductoDao dao = new ProductoDao();
                            producto.setArticulo(items.getArticulo());
                            producto.setDescripcion(items.getDescripcion());
                            producto.setStatus(items.getStatus());
                            producto.setUnidad_medida(items.getUnidadMedida());
                            producto.setClave_sat(items.getClaveSat());
                            producto.setUnidad_sat(items.getUnidadSat());
                            producto.setPrecio(items.getPrecio());
                            producto.setCosto(items.getCosto());
                            producto.setIva(items.getIva());
                            producto.setIeps(items.getIeps());
                            producto.setPrioridad(items.getPrioridad());
                            producto.setRegion(items.getRegion());
                            producto.setCodigo_alfa(items.getCodigoAlfa());
                            producto.setCodigo_barras(items.getCodigoBarras());
                            producto.setPath_img(items.getPathImage());
                            dao.insert(producto);
                            mData.add(producto);
                        }

                    }

                    mAdapter.setProducts(mData);
                    if (mAdapter.getItemCount() > 0) {
                        lyt_productos.setVisibility(View.GONE);
                    } else {
                        lyt_productos.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onFailure(Call<ProductoJson> call, Throwable t) {
                progresshide();
            }
        });

    }


    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        mData = (List<ProductoBean>) (List<?>) new ProductoDao().list();
        mAdapter.setProducts(mData);

        if (mData.size() > 0) {
            lyt_productos.setVisibility(View.GONE);
        } else {
            lyt_productos.setVisibility(View.VISIBLE);
        }
    }
}