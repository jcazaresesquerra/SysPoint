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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.dao.CobranzaDao;
import com.app.syspoint.repository.database.dao.CobranzaModelDao;
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

    private int countItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        this.setContentView(R.layout.activity_lista_documentos_cobranza);
        lyt_lista_documentos = findViewById(R.id.lyt_lista_documentos);
        img_add_documents = findViewById(R.id.img_add_documents);
        img_add_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countItems > 1) {
                    addItemsCobranza();
                }
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

        lista = (List<CobranzaBean>)(List<?>) new CobranzaDao().getByCobranzaByCliente(CobranzaActivity.id_cliente_seleccionado);

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

        mAdapter = new AdapterListaDocumentosCobranza(lista, new AdapterListaDocumentosCobranza.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
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
            }
        }, new AdapterListaDocumentosCobranza.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {

                 CobranzaBean cobranzaBean = lista.get(position);
                 CobranzaDao  cobranzaDao = new CobranzaDao();

                 if (cobranzaBean.getIsCheck() == false){
                     countItems++;
                     cobranzaBean.setIsCheck(true);
                     cobranzaDao.save(cobranzaBean);
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
                     cobranzaDao.save(cobranzaBean);
                 }
                setData();
                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    private void setData(){
        lista = (List<CobranzaBean>)(List<?>) new CobranzaDao().getByCobranzaByCliente(CobranzaActivity.id_cliente_seleccionado);
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

            final CobranzaModelDao dao = new CobranzaModelDao();
            dao.clear();

            List<CobranzaBean> listaDocumentosSeleccionados = new CobranzaDao().getDocumentosSeleccionados(CobranzaActivity.id_cliente_seleccionado);
            final CobranzaDao cobranzaDao = new CobranzaDao();

            for (CobranzaBean cobranzaItems : listaDocumentosSeleccionados) {
                final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(cobranzaItems.getCobranza());
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
        final com.app.syspoint.repository.database.dao.CobranzaModelDao dao = new CobranzaModelDao();
        item.setVenta(venta);
        item.setCobranza(cobranza);
        item.setImporte(importe);
        item.setSaldo(saldo);
        item.setAcuenta(acuenta);
        item.setNo_referen(no_referen);
        dao.insert(item);

    }

}