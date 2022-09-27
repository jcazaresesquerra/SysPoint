package com.app.syspoint.ui.cobranza;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.syspoint.R;
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.PaymentModelDao;
import com.app.syspoint.ui.cobranza.adapter.AdapterListaDocumentosCobranza;
import com.app.syspoint.utils.Actividades;

import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ListaDocumentosCobranzaActivity extends AppCompatActivity {

    private AdapterListaDocumentosCobranza mAdapter;
    private List<CobranzaBean> lista;
    private LinearLayout lyt_lista_documentos;
    public static String documentoSeleccionado;
    private SwipeRefreshLayout refreshLayout;
    private ImageView img_add_documents;

    private String clienteGlobal;

    private int countItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        this.clienteGlobal = intent.getStringExtra(Actividades.PARAM_1);
        init();
    }

    private void init(){
        this.setContentView(R.layout.activity_lista_documentos_cobranza);
        lyt_lista_documentos = findViewById(R.id.lyt_lista_documentos);
        img_add_documents = findViewById(R.id.img_add_documents);
        img_add_documents.setOnClickListener(v -> {
            if (countItems > 1) {
                addItemsCobranza();
            }
        });
        countItems = 0;
        this.initToolBar();
        this.initRecyclerViews();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_lista_documentos);
        toolbar.setTitle("Lista documentos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    private void initRecyclerViews(){
        new ChargeInteractorImp().executeGetChargeByClient(clienteGlobal, new ChargeInteractor.OnGetChargeByClientListener() {
            @Override
            public void onGetChargeByClientSuccess(@NonNull List<? extends CobranzaBean> chargeByClientList) {
                runOnUiThread(() -> {
                    lista = (List<CobranzaBean>) chargeByClientList;
                    if (mAdapter != null) {
                        mAdapter.setData(lista);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onGetChargeByClientError() {

            }
        });

        lista = new PaymentDao().getByCobranzaByCliente(clienteGlobal);

        if (lista.size() > 0){
            lyt_lista_documentos.setVisibility(View.GONE);
        }else {
            lyt_lista_documentos.setVisibility(View.VISIBLE);
        }

        /*** ----- Obtiene el recyclador ------ ****/
        final RecyclerView recyclerView = findViewById(R.id.recyclerView_lista_documentos);
        recyclerView.setHasFixedSize(true);

        /*** ----- Manejador ------ ****/
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaDocumentosCobranza(lista,
                position -> {
            if (countItems == 0) {
                CobranzaBean cobranzaBean = lista.get(position);
                ListaDocumentosCobranzaActivity.documentoSeleccionado = cobranzaBean.getCobranza();
                Actividades.getSingleton(ListaDocumentosCobranzaActivity.this, AbonoDocumentoActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
            }else {
                final PrettyDialog dialogo = new PrettyDialog(ListaDocumentosCobranzaActivity.this);
                dialogo.setTitle("Opción multiple")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Ha seleccionado la opción de pago multiple, no puede abonor a un documento")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.quantum_orange500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();
                return;
            }
        }, position -> {

             CobranzaBean cobranzaBean = lista.get(position);
             PaymentDao paymentDao = new PaymentDao();

             if (cobranzaBean.getIsCheck() == false){
                 countItems++;
                 cobranzaBean.setIsCheck(true);
                 paymentDao.save(cobranzaBean);
                 if (countItems > 1 ){
                     img_add_documents.setVisibility(View.VISIBLE);
                 }else {
                     img_add_documents.setVisibility(View.GONE);
                 }
             }else {
                 cobranzaBean.setIsCheck(false);
                 countItems--;
                 if (countItems > 1 ){
                     img_add_documents.setVisibility(View.VISIBLE);
                 }else {
                     img_add_documents.setVisibility(View.GONE);
                 }
                 paymentDao.save(cobranzaBean);
             }
            setData();
            return false;
        });
        recyclerView.setAdapter(mAdapter);
    }


    private void setData(){
        lista = (List<CobranzaBean>)(List<?>) new PaymentDao().getByCobranzaByCliente(clienteGlobal);
        mAdapter.setData(lista);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Sio cancelo no hay por que seguir
        if(resultCode == Activity.RESULT_CANCELED)
            return;

        super.onActivityResult(requestCode, resultCode, data);

        //Obtiene la cantidad a vender
        String importe = data.getStringExtra(Actividades.PARAM_1);

        //Establece el resultado que debe de regresar
        Intent intent = new Intent();
        intent.putExtra(Actividades.PARAM_1, ListaDocumentosCobranzaActivity.documentoSeleccionado);
        intent.putExtra(Actividades.PARAM_2, importe);
        this.setResult(Activity.RESULT_OK, intent);

        //Cierra la actividad
        this.finish();

    }


    private void addItemsCobranza(){

        try{

            final PaymentModelDao dao = new PaymentModelDao();
            dao.clear();

            List<CobranzaBean> listaDocumentosSeleccionados = new PaymentDao().getDocumentosSeleccionados(clienteGlobal);
            final PaymentDao paymentDao = new PaymentDao();

            for (CobranzaBean cobranzaItems : listaDocumentosSeleccionados) {
                final CobranzaBean cobranzaBean = paymentDao.getByCobranza(cobranzaItems.getCobranza());
                int venta = cobranzaBean.getVenta();
                String cobranza = cobranzaBean.getCobranza();
                double importe = cobranzaBean.getImporte();
                double saldo = cobranzaBean.getSaldo();
                double acuenta = cobranzaBean.getSaldo();
                String no_referen = "";
                AddItems(venta, cobranza, importe, saldo, acuenta, no_referen);
            }

            finish();
        } catch (Exception e) {
            // Excepcion.getSingleton(e).procesaExcepcion(activityGlobal);
        }
    }
    private void AddItems(int venta, String cobranza, double importe, double saldo, double acuenta, String no_referen) {
        final CobranzaModel item = new CobranzaModel();
        final PaymentModelDao dao = new PaymentModelDao();
        item.setVenta(venta);
        item.setCobranza(cobranza);
        item.setImporte(importe);
        item.setSaldo(saldo);
        item.setAcuenta(acuenta);
        item.setNo_referen(no_referen);
        dao.insert(item);

    }

}