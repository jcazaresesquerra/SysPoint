package com.app.syspoint.ui.stock.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.R;
import com.app.syspoint.repository.cache.SharedPreferencesManager;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.StockDao;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.StockBox;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class CantidadInventarioActivity extends AppCompatActivity {

    private EditText EditTextCantidad, EditTextPrecio, EditTextTotal;
    private String cantidad;
    private String ariculo;
    private double precio;
    private TextView tv_inventario_descripcion, tv_inventario_articulo;
    private ProductBox productoBean;
    int qty = 0;
    private ImageView img_inv_producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cantidad_inventario);

        this.initControls();
        loadDataProduct();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(EditTextCantidad.getWindowToken(), 0);
        EditTextCantidad.requestFocus();
        showKeyboards(this);
    }

    private void loadDataProduct() {
        img_inv_producto = findViewById(R.id.img_inv_producto);
        ariculo = ListaProductosInventarioActivity.articuloSeleccionado;
        final ProductDao productDao = new ProductDao();
        productoBean = productDao.getProductoByArticulo(ariculo);

        if (productoBean != null) {
            EditTextPrecio.setText(Utils.FDinero(productoBean.getPrecio() * (1 + productoBean.getIva() / 100)));
            precio = (productoBean.getPrecio() * (1 + productoBean.getIva() / 100));
            tv_inventario_descripcion.setText(productoBean.getDescripcion());
            tv_inventario_articulo.setText(productoBean.getArticulo());
            if (productoBean.getPath_img() != null) {
                byte[] decodedString = Base64.decode(productoBean.getPath_img(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                img_inv_producto.setImageBitmap(decodedByte);
            }
        }
    }

    private void initControls() {

        EditTextCantidad = findViewById(R.id.tv_inventario_cantidad);
        EditTextPrecio = findViewById(R.id.tv_inventario_precio);
        EditTextTotal = findViewById(R.id.tv_inventario_cantidad_importe);
        tv_inventario_descripcion = findViewById(R.id.tv_inventario_descripcion);
        tv_inventario_articulo = findViewById(R.id.tv_inventario_articulo);
        EditTextCantidad.setText("0");


        EditTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    EditTextCantidad.setText(String.valueOf(0));
                    EditTextCantidad.setSelectAllOnFocus(true);
                    EditTextCantidad.requestFocus();
                    return;
                }

                cantidad = EditTextCantidad.getText().toString();
                qty = Integer.parseInt(cantidad);

                if (qty == 0) {
                    EditTextTotal.setText(Utils.FDinero(precio));
                } else {
                    double tota = precio * qty;
                    EditTextTotal.setText(Utils.FDinero(tota));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button buttonCerrar = findViewById(R.id.btn_inventario_cerrar);
        buttonCerrar.setOnClickListener(v -> finish());

        Button buttonConfirmar = findViewById(R.id.btn_inventario_confirmar);
        buttonConfirmar.setOnClickListener(v -> {

            if (qty == 0) {
                final PrettyDialog dialogo = new PrettyDialog(CantidadInventarioActivity.this);
                dialogo.setTitle("Existencia")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe indicar la cantidad a ingresar")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, dialogo::dismiss)
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, dialogo::dismiss);
                dialogo.setCancelable(false);
                dialogo.show();
                return;
            }

            //Validamos si el inventario esta pendiente de lo contrario no ingresamos nada

            List<StockBox> mData = new StockDao().getInventarioPendiente();

            /*int count = mData.size();

            if (count > 0){
                final PrettyDialog dialogo = new PrettyDialog(CantidadInventarioActivity.this);
                dialogo.setTitle("Inventario")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("El inventario ya fue confirmado no puede agregar mas productos")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.quantum_orange, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogo.dismiss();
                            }
                        });
                dialogo.setCancelable(false);
                dialogo.show();
                return;
            }else {
*/
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
            int currentStockId = sharedPreferencesManager.getCurrentStockId();
            int currentLoadId = sharedPreferencesManager.getCurrentLoadId();

            StockDao stockDao = new StockDao();
            StockBox inventarioBean = stockDao.getProductoByArticulo(productoBean.getArticulo());

            if (inventarioBean != null){
                int total = inventarioBean.getTotalCantidad() + qty;
                int cantidad = inventarioBean.getCantidad() + qty;
                inventarioBean.setCantidad(cantidad);
                inventarioBean.setLastCantidad(qty);
                inventarioBean.setTotalCantidad(total);
                inventarioBean.setStockId(currentStockId);
                inventarioBean.setLoadId(currentLoadId);
                stockDao.insertBox(inventarioBean);

                Intent intent = new Intent();
                intent.putExtra(Actividades.PARAM_1, cantidad);
                setResult(Activity.RESULT_OK, intent);
                Toast.makeText(CantidadInventarioActivity.this, "La cantidad se actualizo", Toast.LENGTH_LONG).show();
                finish();
                return;
            }else {
                StockBox bean = new StockBox();
                StockDao dao = new StockDao();
                bean.getArticulo().setTarget(productoBean);
                bean.setCantidad(qty);
                bean.setLastCantidad(qty);
                bean.setTotalCantidad(qty);
                bean.setEstado("PE");
                bean.setPrecio(productoBean.getPrecio());
                bean.setFecha(Utils.fechaActual());
                bean.setHora(Utils.getHoraActual());
                bean.setImpuesto(productoBean.getIva());
                bean.setArticulo_clave(productoBean.getArticulo());
                bean.setStockId(currentStockId);
                bean.setLoadId(currentLoadId);
                dao.insertBox(bean);

                Intent intent = new Intent();
                intent.putExtra(Actividades.PARAM_1, cantidad);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return;
            }
        });

    }

    public static void showKeyboards(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}