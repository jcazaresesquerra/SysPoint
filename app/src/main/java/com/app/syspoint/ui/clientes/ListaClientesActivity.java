package com.app.syspoint.ui.clientes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.utils.Actividades;

import java.util.ArrayList;
import java.util.List;

public class ListaClientesActivity extends AppCompatActivity {

    AdapterListaClientes mAdapter;
    List<ClientBox> mData;
    private LinearLayout lyt_clientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);
        lyt_clientes = findViewById(R.id.lyt_clientes);

        initToolBar();
        initRecyclerView();
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_lista_clientes);
        toolbar.setTitle("Clientes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cliente_fragment, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search_ciente);
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
                //mAdapter.getFilter().filter(arg0);
                new ClientInteractorImp().executeFindClient(arg0, new ClientInteractor.FindClientListener() {
                    @Override
                    public void onFindClientSuccess(List<ClientBox> clientList) {
                        List<ClientBox> clientBeanList = (List<ClientBox> ) clientList;
                        mAdapter.setClients(clientBeanList);

                        if (clientBeanList.size() > 0) {
                            lyt_clientes.setVisibility(View.GONE);
                        } else {
                            lyt_clientes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFindClientError() {

                    }
                });
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
        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            mData = (List<ClientBox>) new ClientDao().getClientsByRute(ruteoBean.getRuta());
        } else {
            mData = new ArrayList<>();
        }

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        // remove inactive users
        mData.removeIf(item -> !item.getStatus());

        mAdapter = new AdapterListaClientes(
                mData,
                (view, obj, position, onDialogShownListener) -> {
                    Intent intent = new Intent();
                    intent.putExtra(Actividades.PARAM_1, obj.getCuenta());
                    setResult(Activity.RESULT_OK, intent);
                    onDialogShownListener.onDialogShown();
                    finish();
                },
                position -> false);
        recyclerView.setAdapter(mAdapter);
    }

}