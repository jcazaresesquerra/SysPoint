package com.app.syspoint.ui.stock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;

import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.documents.StockTicket;
import com.app.syspoint.repository.objectBox.dao.StockDao;
import com.app.syspoint.repository.objectBox.entities.StockBox;
import com.app.syspoint.ui.stock.adapter.AdapterInventario;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ConfirmaInventarioActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmaInventarioActivity";
    private List<StockBox> mData;
    private AdapterInventario mAdapter;
    private TextView tv_total_inventario_confirmar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_inventario);
        tv_total_inventario_confirmar = findViewById(R.id.tv_total_inventario_confirmar);

        initToolBar();
        initRecyclerView();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_confirma_inventario);
        toolbar.setTitle("Confirma Inventario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }


    private void initRecyclerView(){
        mData = new ArrayList<>();
        mData = new StockDao().list();

        if (mData.size() > 0){
            // lyt_empleados.setVisibility(View.GONE);
        }else {
            // lyt_empleados.setVisibility(View.VISIBLE);
        }
        final RecyclerView recyclerView = findViewById(R.id.rv_inventario_confirma);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterInventario(mData, position -> false);
        recyclerView.setAdapter(mAdapter);
        calcular();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_confirma_inventario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Timber.tag(TAG).d("home -> click");

                finish();
                return true;

            case R.id.item_menu_confirma_inventario:
                Timber.tag(TAG).d("confirm stock -> click");
                final PrettyDialog dialog = new PrettyDialog(ConfirmaInventarioActivity.this);
                dialog.setTitle("Confirmar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea confirmar la entrada al inventario ")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, () -> dialog.dismiss())
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, () -> {

                            Utils.addActivity2Stack(ConfirmaInventarioActivity.this);

                            final StockBox inventarioBean = new StockBox();

                            StockTicket stockTicket = new StockTicket();
                            stockTicket.setBox(inventarioBean);
                            stockTicket.template();

                            String ticket = stockTicket.getDocument();
                            Log.d("ConfirmarInventarioActivity", ticket);

                            int loadId = new CacheInteractor().getCurrentLoadId() + 1;
                            new CacheInteractor().setLoadId(loadId);

                            Intent intent = new Intent(ConfirmaInventarioActivity.this, FinalizaInventarioActivity.class);
                            intent.putExtra("ticket", ticket);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, () -> dialog.dismiss());
                dialog.setCancelable(false);
                dialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void calcular(){
        double total = 0;
        for (int i = 0; i < mData.size(); i++) {
            total += (mData.get(i).getPrecio() * mData.get(i).getCantidad());
        }

        tv_total_inventario_confirmar.setText("" + Utils.FDinero(total));
    }
}