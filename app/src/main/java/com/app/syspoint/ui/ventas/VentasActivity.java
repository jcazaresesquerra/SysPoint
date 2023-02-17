package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.ui.ventas.adapter.AdapterItemsVenta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.VentasModelBean;
import com.app.syspoint.repository.database.bean.AppBundle;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.PartidasBean;
import com.app.syspoint.repository.database.bean.PreciosEspecialesBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.bean.VentasBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.SpecialPricesDao;
import com.app.syspoint.repository.database.dao.ProductDao;
import com.app.syspoint.repository.database.dao.SellsDao;
import com.app.syspoint.repository.database.dao.SellsModelDao;
import com.app.syspoint.documents.SellTicket;
import com.app.syspoint.models.Client;
import com.app.syspoint.ui.templates.ViewPDFActivity;
import com.app.syspoint.ui.precaptura.PrecaptureActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/*TODO refactor this class*/
/*public class VentasActivity extends AppCompatActivity {

    private AdapterItemsVenta mAdapter;
    private List<VentasModelBean> mData;
    private TextView textViewCliente;
    private TextView textViewNombre;
    private TextView textViewSubtotal;
    private TextView textViewImpuesto;
    private TextView textViewTotal;
    private String latidud;
    private String longitud;
    Activity activity;
    String tipoVenta = "SIN DEFINIR";

    String idCliente;

    RadioButton radio_contado;
    RadioButton radio_credito;
    private double saldo_credito = 0;
    private double credito = 0;
    private boolean is_credito = false;
    private double importe_venta = 0;
    private boolean isCreditMatriz = false;
    int countProducts = 0;
    private ImageView imageViewVentas, imageViewVisitas;
    private String cuentaMatriz = "";
    private String sucursalMatriz = "";
    RelativeLayout rlprogress_venta;

    private boolean confirmPrecaptureClicked = false;
    private boolean isBackPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        rlprogress_venta = findViewById(R.id.rlprogress_venta);
        //donwloadCobranza();
        activity = this;
        this.deleteVentaTemp();

        this.initToolBar();
        this.initControls();
        this.initRecyclerView();
        locationStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            return;

        String cantidad = data.getStringExtra(Actividades.PARAM_1);
        String articulo = data.getStringExtra(Actividades.PARAM_2);

        final ProductDao productDao = new ProductDao();
        final ProductoBean productoBean = productDao.getProductoByArticulo(articulo);

        if (productoBean == null) {
            Toast.makeText(this, "Ha ocurrido un problema, vuelve a intentarlo", Toast.LENGTH_SHORT).show();
            return;
        }


        //Validamos si existe el producto
        if (validaProducto(productoBean.getArticulo())) {
            final PrettyDialog dialogo = new PrettyDialog(this);
            dialogo.setTitle("Existe")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("El producto ingresado ya existe en la venta")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialogo.dismiss();
                        }
                    })
                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialogo.dismiss();
                        }
                    });
            dialogo.setCancelable(false);
            dialogo.show();
            return;
        }

        //TODO PERMITIMOS QUE EL USUARIO VENTA A PRECIO 0
        // if (productoBean.getPrecio() == 0) {
        //     final PrettyDialog dialogo = new PrettyDialog(this);
        //     dialogo.setTitle("Precio")
        //             .setTitleColor(R.color.purple_500)
        //             .setMessage("El precio debe de ser mayor a 0")
        //             .setMessageColor(R.color.purple_700)
        //             .setAnimationEnabled(false)
        //             .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
        //                 @Override
        //                 public void onClick() {
        //                     dialogo.dismiss();
        //                 }
        //             })
        //             .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
        //                 @Override
        //                 public void onClick() {
        //                     dialogo.dismiss();
        //                 }
        //             });
        //     dialogo.setCancelable(false);
        //     dialogo.show();
        //     return;
        // }

        if (cantidad == null || cantidad.isEmpty()) {
            Toast.makeText(this, "Ha ocurrido un problema, vuelve a intentarlo", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidadVendida = Integer.parseInt(cantidad);

        //Validamos los datos del cliente
        ClientDao clientDao = new ClientDao();
        ClienteBean clienteBean = clientDao.getClientByAccount(idCliente);

        //Validamos si hay precio especial del cliente
        final SpecialPricesDao specialPricesDao = new SpecialPricesDao();
        final PreciosEspecialesBean preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

        //Hay precio especial entonces aplica el precio especial

        if (preciosEspecialesBean != null) {
            addItem(productoBean.getArticulo(), productoBean.getDescripcion(), preciosEspecialesBean.getPrecio(), productoBean.getCosto(), productoBean.getIva(), cantidadVendida);

        } else {//No apliques precio especial
            addItem(productoBean.getArticulo(), productoBean.getDescripcion(), productoBean.getPrecio(), productoBean.getCosto(), productoBean.getIva(), cantidadVendida);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_ventas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:
                if (!isBackPressed) {
                    isBackPressed = true;
                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Salir")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Desea salir de la venta")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    isBackPressed = false;
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {

                                    deleteVentaTemp();
                                    finish();
                                    dialog.dismiss();
                                    isBackPressed = false;
                                }
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    isBackPressed = false;
                                }
                            });
                    dialog.setCancelable(false);
                    dialog.show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle("Salir")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea salir de la venta")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            deleteVentaTemp();
                            finish();
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();

                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_ventas);
        toolbar.setTitle("Venta");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    private void initRecyclerView() {

        mData = (List<VentasModelBean>) (List<?>) new SellsModelDao().list();

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_ventas);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterItemsVenta(mData, position -> {

            VentasModelBean item = mData.get(position);

            final PrettyDialog dialog = new PrettyDialog(VentasActivity.this);
            dialog.setTitle("Eliminar")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea eliminar el articulo " + "\n" + item.getDescripcion())
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            VentasModelBean item = mData.get(position);
                            SellsModelDao dao = new SellsModelDao();
                            dao.delete(item);
                            mData = (List<VentasModelBean>) (List<?>) new SellsModelDao().list();
                            mAdapter.setData(mData);
                            ocultaLinearLayouth();
                            calculaImportes();
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();

                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
            return false;
        }, new AdapterItemsVenta.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VentasModelBean item = mData.get(position);

                final Dialog dialogo = new Dialog(VentasActivity.this);
                dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialogo.setContentView(R.layout.dialog_cantidad_venta);

                final EditText editTextCantidad = dialogo.findViewById(R.id.edittext_cantidad_venta_seleccionada_dialog);

                //Cuando el usuario da click en el boton aceptar
                final Button buttonAceptar = dialogo.findViewById(R.id.button_seleccionar_cantidad_venta_dialog);
                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cantidad = editTextCantidad.getText().toString();

                        if (cantidad == null || cantidad.isEmpty()) {
                            return;
                        }

                        int cantidadVenta = Integer.parseInt(cantidad);

                        if (cantidadVenta == 0) {
                            final PrettyDialog dialog = new PrettyDialog(VentasActivity.this);
                            dialog.setTitle("Precio")
                                    .setTitleColor(R.color.purple_500)
                                    .setMessage("El precio debe de ser mayor a cero")
                                    .setMessageColor(R.color.purple_700)
                                    .setAnimationEnabled(false)
                                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialog.dismiss();
                                        }
                                    })
                                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.setCancelable(false);
                            dialog.show();
                            return;
                        }

                        final SellsModelDao dao = new SellsModelDao();
                        item.setCantidad(cantidadVenta);
                        dao.save(item);

                        mData = (List<VentasModelBean>) (List<?>) new SellsModelDao().list();
                        mAdapter.setData(mData);
                        ocultaLinearLayouth();
                        calculaImportes();
                        dialogo.dismiss();
                    }
                });

                dialogo.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextCantidad.getWindowToken(), 0);
                editTextCantidad.requestFocus();
                showKeyboards(VentasActivity.this);
            }
        });
        recyclerView.setAdapter(mAdapter);
        ocultaLinearLayouth();
        calculaImportes();
    }

    private void initControls() {

        textViewCliente = findViewById(R.id.textView_cliente_venta_view);
        textViewNombre = findViewById(R.id.textView_cliente_nombre_venta_view);
        textViewSubtotal = findViewById(R.id.textView_subtotal_venta_view);
        textViewImpuesto = findViewById(R.id.textView_impuesto_venta_view);
        textViewTotal = findViewById(R.id.textView_total_venta_view);

        Intent intent = getIntent();
        idCliente = intent.getStringExtra(Actividades.PARAM_1);

        ClientDao clientDao = new ClientDao();

        ClienteBean clienteBean = clientDao.getClientByAccount(idCliente);

        new ChargeInteractorImp().executeGetChargeByClient(clienteBean.getCuenta(), new ChargeInteractor.OnGetChargeByClientListener() {
            @Override
            public void onGetChargeByClientSuccess(@NonNull List<? extends CobranzaBean> chargeByClientList) {
                PaymentDao paymentDao1 = new PaymentDao();
                double saldoCliente =  paymentDao1.getSaldoByCliente(clienteBean.getCuenta());
                clienteBean.setSaldo_credito(saldoCliente);
                clienteBean.setDate_sync(Utils.fechaActual());
                clientDao.save(clienteBean);

                if (clienteBean.getMatriz() == null || (clienteBean.getMatriz() != null && clienteBean.getMatriz().compareToIgnoreCase("null") == 0)) {
                    textViewCliente.setText(clienteBean.getCuenta());
                    textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteBean.getSaldo_credito()) + ")");
                } else {
                    ClienteBean clienteMatriz = clientDao.getClientByAccount(clienteBean.getMatriz());
                    textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteMatriz.getSaldo_credito()) + ")");
                }
            }

            @Override
            public void onGetChargeByClientError() {

            }
        });

        PaymentDao paymentDao1 = new PaymentDao();
        double saldoCliente =  paymentDao1.getSaldoByCliente(clienteBean.getCuenta());
        clienteBean.setSaldo_credito(saldoCliente);
        clienteBean.setDate_sync(Utils.fechaActual());
        clientDao.save(clienteBean);

        imageViewVentas = findViewById(R.id.img_btn_finish_sale);
        imageViewVentas.setOnClickListener(v -> {
            imageViewVentas.setEnabled(false);
            if (existenPartidas()) {


                if (radio_contado != null && !radio_contado.isChecked() && !radio_credito.isChecked()){
                    final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                    dialogo.setTitle("Tipo de venta")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de seleccionar el tipo de venta")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                    imageViewVentas.setEnabled(true);
                    return;
                }

                if (tipoVenta.compareToIgnoreCase("SIN DEFINIR") == 0) {
                    final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                    dialogo.setTitle("Tipo de venta")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de seleccionar el tipo de venta")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                    imageViewVentas.setEnabled(true);
                    return;
                }

                double saldo_disponible = 0;

                ClientDao clientDao1 = new ClientDao();
                ClienteBean clienteBean1 = clientDao1.getClientByAccount(idCliente);

                if (tipoVenta.compareToIgnoreCase("CREDITO") == 0) {
                    if (clienteBean1 != null) {
                        if (clienteBean1.getIs_credito()) {
                            if (clienteBean1.getMatriz() != null && clienteBean1.getMatriz().length() > 0) {
                                ClienteBean clienteMatriz = clientDao1.getClientByAccount(clienteBean1.getMatriz());
                                if (clienteMatriz != null) {
                                    isCreditMatriz = true;
                                    cuentaMatriz = clienteMatriz.getCuenta();
                                    sucursalMatriz = clienteMatriz.getNombre_comercial();
                                    saldo_disponible = clienteMatriz.getLimite_credito() - clienteMatriz.getSaldo_credito();
                                    if (saldo_disponible < importe_venta) {
                                        final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                                        dialogo.setTitle("Crédito insuficiente")
                                                .setTitleColor(R.color.purple_500)
                                                .setMessage("La matriz solo cuenta con un saldo " + Utils.FDinero(saldo_disponible) + "  para terminar la venta a crédito  ")
                                                .setMessageColor(R.color.purple_700)
                                                .setAnimationEnabled(false)
                                                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                                    @Override
                                                    public void onClick() {
                                                        dialogo.dismiss();
                                                    }
                                                })
                                                .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                                    @Override
                                                    public void onClick() {
                                                        dialogo.dismiss();
                                                    }
                                                });
                                        dialogo.setCancelable(false);
                                        dialogo.show();
                                        imageViewVentas.setEnabled(true);
                                        return;
                                    }
                                } else {
                                    if (is_credito) {
                                        saldo_disponible = credito - saldo_credito;
                                        if (saldo_disponible < importe_venta) {
                                            final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                                            dialogo.setTitle("Crédito insuficiente")
                                                    .setTitleColor(R.color.purple_500)
                                                    .setMessage("Este cliente solo cuenta con " + Utils.FDinero(saldo_disponible) + "  de credito disponible para ventas a credito")
                                                    .setMessageColor(R.color.purple_700)
                                                    .setAnimationEnabled(false)
                                                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                                        @Override
                                                        public void onClick() {
                                                            dialogo.dismiss();
                                                        }
                                                    })
                                                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                                        @Override
                                                        public void onClick() {
                                                            dialogo.dismiss();
                                                        }
                                                    });
                                            dialogo.setCancelable(false);
                                            dialogo.show();
                                            imageViewVentas.setEnabled(true);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }




                final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                dialogo.setTitle("Terminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("¿Desea terminal la venta?")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                if (!confirmPrecaptureClicked) {
                                    confirmPrecaptureClicked = true;
                                    Utils.addActivity2Stack(activity);

                                    final ArrayList<PartidasBean> lista = new ArrayList<>();

                                    final SellsDao sellsDao = new SellsDao();
                                    final VentasBean ventasBean = new VentasBean();


                                    int ultimoFolio = sellsDao.getUltimoFolio();

                                    final ProductDao productDao = new ProductDao();
                                    //Recorremos las partidas
                                    for (int x = 0; x < mData.size(); x++) {
                                        //Validamos si el articulo existe en la base de datos
                                        final ProductoBean productosBean = productDao.getProductoByArticulo(mData.get(x).getArticulo());
                                        final PartidasBean partida = new PartidasBean();
                                        partida.setArticulo(productosBean);
                                        partida.setCantidad(mData.get(x).getCantidad());
                                        partida.setPrecio(mData.get(x).getPrecio());
                                        partida.setCosto(mData.get(x).getCosto());
                                        partida.setImpuesto(mData.get(x).getImpuesto());
                                        partida.setObserv(productosBean.getDescripcion());
                                        partida.setFecha(new Date());
                                        partida.setHora(Utils.getHoraActual());
                                        partida.setVenta(Long.valueOf(ultimoFolio));
                                        partida.setDescripcion(productosBean.getDescripcion());
                                        lista.add(partida);
                                    }

                                    //Le indicamos al sistema que el cliente ya se ah visitado
                                    final ClientDao clientDao1 = new ClientDao();
                                    final ClienteBean clienteBean1 = clientDao1.getClientByAccount(idCliente);
                                    final String clienteID = String.valueOf(clienteBean1.getId());
                                    clienteBean1.setVisitado(1);
                                    clienteBean1.setVisitasNoefectivas(0);
                                    clienteBean1.setDate_sync(Utils.fechaActual());
                                    clientDao1.save(clienteBean1);

                                    ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
                                    progressDialog.setMessage("Espere un momento");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                                        progressDialog.dismiss();
                                        if (connected)
                                            testLoadClientes(String.valueOf(clienteBean1.getId()));

                                        final RuteClientDao ruteClientDao = new RuteClientDao();
                                        final ClientesRutaBean clientesRutaBean = ruteClientDao.getClienteByCuentaCliente(idCliente);
                                        if (clientesRutaBean != null) {
                                            clientesRutaBean.setVisitado(1);
                                            ruteClientDao.save(clientesRutaBean);
                                        }

                                        //Obtiene el nombre del vendedor
                                        EmpleadoBean vendedoresBean = AppBundle.getUserBean();

                                        if (vendedoresBean == null) {
                                            vendedoresBean = new CacheInteractor().getSeller();
                                        }

                                        ventasBean.setTipo_doc("TIK");
                                        ventasBean.setFecha(Utils.fechaActual());
                                        ventasBean.setHora(Utils.getHoraActual());
                                        ventasBean.setCliente(clienteBean1);
                                        ventasBean.setEmpleado(vendedoresBean);
                                        ventasBean.setImporte(Double.parseDouble(textViewSubtotal.getText().toString().replace(",", "")));
                                        ventasBean.setImpuesto(Double.parseDouble(textViewImpuesto.getText().toString().replace(",", "")));
                                        ventasBean.setDatos(clienteBean1.getNombre_comercial());
                                        ventasBean.setEstado("CO");
                                        ventasBean.setCorte("N");
                                        ventasBean.setTemporal(1);
                                        ventasBean.setVenta(ultimoFolio);
                                        ventasBean.setLatidud(latidud);
                                        ventasBean.setLongitud(longitud);
                                        ventasBean.setSync(0);
                                        ventasBean.setTipo_venta(tipoVenta);
                                        ventasBean.setUsuario_cancelo("");
                                        if (cuentaMatriz.length() == 0) {
                                            ventasBean.setFactudado("");
                                        } else {
                                            ventasBean.setFactudado(cuentaMatriz);
                                        }
                                        ventasBean.setTicket(Utils.getHoraActual().replace(":", "") + Utils.getFechaRandom().replace("-", ""));


                                        double totalVenta = Double.parseDouble(textViewSubtotal.getText().toString().replace(",", "")) + Double.parseDouble(textViewImpuesto.getText().toString().replace(",", ""));
                                        String ticketRamdom = ticketRamdom() + ventasBean.getFecha().replace("-", "") + "" + ventasBean.getHora().replace(":", "");
                                        if (tipoVenta.compareToIgnoreCase("CREDITO") == 0) {
                                            //Si la cobranza es de matriz entonces creamos la cobranza a matriz
                                            if (isCreditMatriz) {
                                                CobranzaBean cobranzaBean = new CobranzaBean();
                                                PaymentDao paymentDao = new PaymentDao();
                                                cobranzaBean.setCobranza(ticketRamdom);
                                                cobranzaBean.setCliente(cuentaMatriz);
                                                cobranzaBean.setImporte(totalVenta);
                                                cobranzaBean.setSaldo(totalVenta);
                                                cobranzaBean.setVenta(Integer.valueOf(ventasBean.getTicket()));
                                                cobranzaBean.setEstado("PE");
                                                cobranzaBean.setObservaciones("Se realiza la venta a crédito para sucursal \n " + clienteBean1.getCuenta() + " " + clienteBean1.getNombre_comercial() + " \n con cargo a Matriz " + cuentaMatriz + " " + sucursalMatriz + "\n" + ventasBean.getFecha() + " hora " + ventasBean.getHora());
                                                cobranzaBean.setFecha(ventasBean.getFecha());
                                                cobranzaBean.setHora(ventasBean.getHora());
                                                if (vendedoresBean != null) {
                                                    cobranzaBean.setEmpleado(vendedoresBean.getIdentificador());
                                                }
                                                cobranzaBean.setAbono(false);
                                                paymentDao.save(cobranzaBean);

                                                //Actualizamos el documento de la venta con el de la cobranza
                                                ventasBean.setCobranza(ticketRamdom);
                                                //ventasDao.save(ventasBean);

                                                //Actualizamos el saldo del cliente
                                                ClienteBean clienteMatriz = clientDao1.getClientByAccount(cuentaMatriz);
                                                double saldoNuevo = clienteMatriz.getSaldo_credito() + totalVenta;
                                                clienteMatriz.setSaldo_credito(saldoNuevo);
                                                clienteMatriz.setDate_sync(Utils.fechaActual());

                                                clientDao1.save(clienteMatriz);

                                                if (connected)
                                                    testLoadClientes(String.valueOf(clienteMatriz.getId()));

                                            } else {
                                                CobranzaBean cobranzaBean = new CobranzaBean();
                                                PaymentDao paymentDao = new PaymentDao();
                                                cobranzaBean.setCobranza(ticketRamdom);
                                                cobranzaBean.setCliente(clienteBean1.getCuenta());
                                                cobranzaBean.setImporte(totalVenta);
                                                cobranzaBean.setSaldo(totalVenta);
                                                cobranzaBean.setVenta(Integer.valueOf(ventasBean.getTicket()));
                                                cobranzaBean.setEstado("PE");
                                                cobranzaBean.setObservaciones("Venta a crédito " + ventasBean.getFecha() + " hora " + ventasBean.getHora());
                                                cobranzaBean.setFecha(ventasBean.getFecha());
                                                cobranzaBean.setHora(ventasBean.getHora());
                                                if (vendedoresBean != null) {
                                                    cobranzaBean.setEmpleado(vendedoresBean.getIdentificador());
                                                }
                                                paymentDao.save(cobranzaBean);

                                                //Actualizamos el documento de la venta con el de la cobranza
                                                ventasBean.setCobranza(ticketRamdom);
                                                //ventasDao.save(ventasBean);

                                                //Actualizamos el saldo del cliente
                                                double saldoNuevo = clienteBean1.getSaldo_credito() + totalVenta;
                                                clienteBean1.setSaldo_credito(saldoNuevo);
                                                clienteBean1.setVisitasNoefectivas(0);
                                                clienteBean1.setDate_sync(Utils.fechaActual());

                                                clientDao1.save(clienteBean1);

                                                if (connected) testLoadClientes(clienteID);
                                            }
                                        }

                                        sellsDao.save(ventasBean);


                                        //Creamos la venta
                                        sellsDao.creaVenta(ventasBean, lista);

                                        String ventaID = String.valueOf(ventasBean.getVenta());

                                        //Creamos el template del timbre
                                        SellTicket sellTicket = new SellTicket();
                                        sellTicket.setBean(ventasBean);
                                        sellTicket.template();

                                        String ticket = sellTicket.getDocument();

                                        Intent intent1 = new Intent(VentasActivity.this, ViewPDFActivity.class);
                                        intent1.putExtra("ticket", ticket);
                                        intent1.putExtra("venta", ventaID);
                                        intent1.putExtra("clienteID", clienteID);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);

                                        dialogo.dismiss();
                                        imageViewVentas.setEnabled(true);
                                        confirmPrecaptureClicked = false;
                                    }).execute(), 100);
                                }
                            }
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();

                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();
                imageViewVentas.setEnabled(true);

            } else {
                final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                dialogo.setTitle("Finalizar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe de agregar un producto para finalizar la venta")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();
                imageViewVentas.setEnabled(true);
            }
            imageViewVentas.setEnabled(true);
        });

        imageViewVisitas = findViewById(R.id.img_btn_finish_visita);
        imageViewVisitas.setOnClickListener(v -> {
            imageViewVisitas.setEnabled(false);
            ClientDao clientDao12 = new ClientDao();
            ClienteBean clienteBean12 = clientDao12.getClientByAccount(idCliente);
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put(Actividades.PARAM_1, clienteBean12.getCuenta());
            parametros.put(Actividades.PARAM_2, clienteBean12.getCalle());
            parametros.put(Actividades.PARAM_3, clienteBean12.getNumero());
            parametros.put(Actividades.PARAM_4, clienteBean12.getColonia());
            parametros.put(Actividades.PARAM_5, clienteBean12.getNombre_comercial());
            parametros.put(Actividades.PARAM_6, clienteBean12.getLatitud());
            parametros.put(Actividades.PARAM_7, clienteBean12.getLongitud());
            Utils.addActivity2Stack(activity);
            Actividades.getSingleton(VentasActivity.this, PrecaptureActivity.class).muestraActividad(parametros);
            imageViewVisitas.setEnabled(true);
        });

        if (clienteBean != null) {
            is_credito = clienteBean.getIs_credito();
            saldo_credito = clienteBean.getSaldo_credito();
            credito = clienteBean.getLimite_credito();
            textViewNombre.setText(clienteBean.getNombre_comercial());

            new ClientInteractorImp().executeGetAllClients(new ClientInteractor.GetAllClientsListener() {
                @Override
                public void onGetAllClientsSuccess(@NonNull List<? extends ClienteBean> clientList) {
                    ClienteBean clienteBean = clientDao.getClientByAccount(idCliente);
                    if (clienteBean.getMatriz() == null || (clienteBean.getMatriz() != null && clienteBean.getMatriz().compareToIgnoreCase("null") == 0)) {
                        textViewCliente.setText(clienteBean.getCuenta());
                        textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteBean.getSaldo_credito()) + ")");
                    } else {
                        ClienteBean clienteMatriz = clientDao.getClientByAccount(clienteBean.getMatriz());
                        textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteMatriz.getSaldo_credito()) + ")");
                    }
                }

                @Override
                public void onGetAllClientsError() {

                }
            });
            if (clienteBean.getMatriz() == null || (clienteBean.getMatriz() != null && clienteBean.getMatriz().compareToIgnoreCase("null") == 0)) {
                textViewCliente.setText(clienteBean.getCuenta());
                textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteBean.getSaldo_credito()) + ")");
            } else {
                ClienteBean clienteMatriz = clientDao.getClientByAccount(clienteBean.getMatriz());
                textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteMatriz.getSaldo_credito()) + ")");
            }
            if (clienteBean.getRecordatorio() == null || clienteBean.getRecordatorio().compareToIgnoreCase("null") == 0 || clienteBean.getRecordatorio().isEmpty()) {
                testLoadClientes(String.valueOf(clienteBean.getId()));
            } else {
                if (clienteBean.getRecordatorio().compareToIgnoreCase("null") == 0 || clienteBean.getRecordatorio() == null) {

                } else {
                    showCustomDialog(clienteBean);
                }
            }
        }

        FloatingActionButton fb = findViewById(R.id.fbAddProductos);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.setEnabled(false);
                Actividades.getSingleton(VentasActivity.this, ListaProductosActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
                fb.setEnabled(true);
            }
        });


        radio_contado = findViewById(R.id.radio_contado);
        radio_credito = findViewById(R.id.radio_credito);

        if (clienteBean.getIs_credito()) {
            tipoVenta = "CREDITO";
            radio_contado.setVisibility(View.VISIBLE);
            radio_credito.setVisibility(View.VISIBLE);
        } else {
            tipoVenta = "CONTADO";
            radio_contado.setVisibility(View.VISIBLE);
            radio_credito.setVisibility(View.GONE);
        }
        ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
        progressDialog.setMessage("Espere un momento");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
                new PriceInteractorImp().executeGetPricesByClient(idCliente, new PriceInteractor.GetPricesByClientListener() {
                    @Override
                    public void onGetPricesByClientSuccess(@NonNull List<? extends PreciosEspecialesBean> pricesByClientList) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onGGetPricesByClientError() {
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al obtener precios", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, no tiene Internet", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }).execute(), 100);
    }

    private void showCustomDialog(ClienteBean clienteBean) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_recordatorio);
        dialog.setCancelable(true);

        ClientDao clientDao = new ClientDao();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_recordatorio);
        et_post.setEnabled(false);
        if (clienteBean.getRecordatorio() != "null" || !clienteBean.getRecordatorio().isEmpty() || clienteBean.getRecordatorio() != null) {
            et_post.setText(clienteBean.getRecordatorio());
        }
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clienteBean.setRecordatorio("");
                clienteBean.setIs_recordatorio(true);
                clientDao.save(clienteBean);

                ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (connected)
                        testLoadClientes(String.valueOf(clienteBean.getId()));
                }).execute(), 100);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void testLoadClientes(String idCliente) {
        final ClientDao clientDao = new ClientDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clientDao.getByIDClient(idCliente);

        List<Client> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB) {
            Client cliente = new Client();
            cliente.setNombreComercial(item.getNombre_comercial());
            cliente.setCalle(item.getCalle());
            cliente.setNumero(item.getNumero());
            cliente.setColonia(item.getColonia());
            cliente.setCiudad(item.getCiudad());
            cliente.setCodigoPostal(item.getCodigo_postal());
            cliente.setFechaRegistro(item.getFecha_registro());
            cliente.setFechaBaja(item.getFecha_baja());
            cliente.setCuenta(item.getCuenta());
            cliente.setGrupo(item.getGrupo());
            cliente.setCategoria(item.getCategoria());
            if (item.getStatus() == false) {
                cliente.setStatus(0);
            } else {
                cliente.setStatus(1);
            }
            cliente.setConsec(item.getConsec());
            cliente.setRegion(item.getRegion());
            cliente.setSector(item.getSector());
            cliente.setRango(item.getRango());
            cliente.setSecuencia(item.getSecuencia());
            cliente.setPeriodo(item.getPeriodo());
            cliente.setRuta(item.getRuta());
            cliente.setLun(item.getLun());
            cliente.setMar(item.getMar());
            cliente.setMie(item.getMie());
            cliente.setJue(item.getJue());
            cliente.setVie(item.getVie());
            cliente.setSab(item.getSab());
            cliente.setDom(item.getDom());
            cliente.setLatitud("" + item.getLatitud());
            cliente.setLongitud("" + item.getLongitud());
            cliente.setPhone_contacto("" + item.getContacto_phone());
            cliente.setRecordatorio("" + item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()) {
                cliente.setCredito(1);
            } else {
                cliente.setCredito(0);
            }
            cliente.setSaldo_credito(item.getSaldo_credito());
            cliente.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz() == "null" && item.getMatriz() == null) {
                cliente.setMatriz("null");
            } else {
                cliente.setMatriz(item.getMatriz());
            }
            listaClientes.add(cliente);
        }

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_contado:
                if (checked)
                    tipoVenta = "CONTADO";
                break;
            case R.id.radio_credito:
                if (checked)
                    tipoVenta = "CREDITO";
                break;
        }
    }

    public static void showKeyboards(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean existenPartidas() {

        boolean valida = false;
        if (mData != null && mData.size() > 0) {
            valida = true;
        }
        return valida;
    }

    private String ticketRamdom() {
        char[] chars = "0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((10 + rnd.nextInt(900)) + Utils.getHoraActual().replace(":", ""));
        for (int i = 0; i < 5; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);

        return sb.toString();
    }

    private void addItem(String articulo, String descripcion, double precio, double costo,
                         int impuesto, int cantidad) {

        final VentasModelBean item = new VentasModelBean();
        final SellsModelDao dao = new SellsModelDao();

        item.setArticulo(articulo);
        item.setDescripcion(descripcion);
        item.setCantidad(cantidad);
        item.setPrecio(precio);
        item.setCosto(costo);
        item.setImpuesto(impuesto);
        item.setObserv(descripcion);
        dao.insert(item);

        mData.add(item);
        mAdapter.setData(mData);

        ocultaLinearLayouth();
        calculaImportes();
    }

    private boolean validaProducto(String articulo) {
        boolean existe = false;
        for (VentasModelBean item : mData) {
            if (item.getArticulo() == articulo) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    private void ocultaLinearLayouth() {
        LinearLayout linearLayout = null;
        linearLayout = findViewById(R.id.empty_state_container);
        if (mData.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {

            linearLayout.setVisibility(View.GONE);
        }
    }

    private void deleteVentaTemp() {
        SellsModelDao dao = new SellsModelDao();
        dao.clear();
    }

    private void calculaImportes() {

        double subTotal = 0;
        double totalImpuesto = 0;


        for (int i = 0; i < mData.size(); i++) {
            subTotal += (mData.get(i).getPrecio() * mData.get(i).getCantidad());
            totalImpuesto += (mData.get(i).getPrecio() * mData.get(i).getCantidad()) * (mData.get(i).getImpuesto() / 100);
            countProducts = mData.get(i).getCantidad();
        }
        importe_venta = subTotal + totalImpuesto;
        String subtotalFormato = Utils.formatMoneyMX(subTotal);
        String impuestoFormato = Utils.formatMoneyMX(totalImpuesto);
        String totalFormato = Utils.formatMoneyMX(subTotal + totalImpuesto);

        if (subtotalFormato.startsWith("$ ."))
            subtotalFormato = "$ 0" + subtotalFormato.substring(2);

        if (impuestoFormato.startsWith("$ ."))
            impuestoFormato = "$ 0" + impuestoFormato.substring(2);

        if (totalFormato.startsWith("$ ."))
            totalFormato = "$ 0" + totalFormato.substring(2);

        //Contie el SubTotal de la venta
        this.textViewSubtotal.setText(subtotalFormato);

        //Contiene el total de los impuesto
        this.textViewImpuesto.setText(impuestoFormato);

        //Contiene el total de la venta
        this.textViewTotal.setText(totalFormato);

        if (existenPartidas()) {
            imageViewVentas.setVisibility(View.VISIBLE);
            imageViewVisitas.setVisibility(View.GONE);
        } else {
            imageViewVentas.setVisibility(View.GONE);
            imageViewVisitas.setVisibility(View.VISIBLE);
        }
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Local);
    }

    public class Localizacion implements LocationListener {
        VentasActivity registrarClientesController;

        public VentasActivity getMainActivity() {
            return registrarClientesController;
        }

        public void setMainActivity(VentasActivity mainActivity) {
            this.registrarClientesController = mainActivity;
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
                new Thread(() -> {
                    if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                        try {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> list = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            if (!list.isEmpty()) {
                                latidud = "" + list.get(0).getLatitude();
                                longitud = "" + list.get(0).getLongitude();
                            }

                        } catch (Exception e) {
                            runOnUiThread(()->{
                                Toast.makeText(VentasActivity.this, "Ha ocurrido un error, vuelva a intentar", Toast.LENGTH_LONG).show();
                            });
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> list = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        if (!list.isEmpty()) {
                            Address DirCalle = list.get(0);
                            // editTextDireccion.setText(DirCalle.getAddressLine(0));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}*/