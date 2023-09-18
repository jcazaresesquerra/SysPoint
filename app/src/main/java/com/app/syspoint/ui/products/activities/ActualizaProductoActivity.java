package com.app.syspoint.ui.products.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.syspoint.interactor.product.GetProductInteractor;
import com.app.syspoint.interactor.product.GetProductsInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.models.Product;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActualizaProductoActivity extends AppCompatActivity {

    byte[] imageByteArray;
    Bitmap decoded;
    private EditText editTextArticulo;
    private EditText editTextDescripcion;
    private EditText editTextPrecio;
    private EditText editTextIVA;
    private EditText editTextCodigoDeBarras;
    private String status_seleccionado;
    private List<String> listaCamposValidos;
    Spinner spinner_producto_status;
    ImageView imgView;
    private String statusSeleccioandoDB;
    private Long idProducto;

    private RelativeLayout rlprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza_producto);
        rlprogress = findViewById(R.id.rlprogress_producto_actualiza);
        initToolBar();
        initConstrols();
        initTextView();
        getData();
        loadStatusSpinner();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = this.getContentResolver().openInputStream(Objects.requireNonNull(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);
                Matrix matrix = new Matrix();
                ExifInterface exif;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                imgView.setImageBitmap(rotatedBitmap);
                imageByteArray = baos.toByteArray();
                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
            }else {
                String resultadoLector = data.getStringExtra(Actividades.PARAM_1);

                if (!resultadoLector.isEmpty()){
                    editTextCodigoDeBarras.setText(resultadoLector);
                }
            }
        } else if (resultCode == ScannerActivity.SCANNER_RESULT) {
            String resultadoLector = data.getStringExtra(Actividades.PARAM_1);

            if (!resultadoLector.isEmpty()){
                editTextCodigoDeBarras.setText(resultadoLector);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualiza_producto, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;
            case R.id.searchProducto:
                selectImage();
                return true;

            case R.id.actualizaProducto:

                if (!validaCamposRequeridos()){
                    StringBuilder campos = new StringBuilder();
                    for (String validItem : listaCamposValidos) {
                        campos.append(validItem).append("\n");
                    }

                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Campos requeridos")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de completar los campos requeridos " + "\n" + campos)
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, dialog::dismiss)
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, dialog::dismiss);

                    dialog.setCancelable(false);
                    dialog.show();
                    return true;
                }


                if (exitProduct()){

                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Actualizar")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Desea actualizar el producto")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.ic_save_white, R.color.purple_500, dialog::dismiss)
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, () -> {
                                saveProducto();
                                dialog.dismiss();
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, dialog::dismiss);
                    dialog.setCancelable(false);
                    dialog.show();


                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void initTextView() {
        imgView = findViewById(R.id.img_producto_actualiza);
        editTextArticulo = findViewById(R.id.inp_articulo_actualiza);
        editTextDescripcion = findViewById(R.id.inp_descripcion_articulo_actualiza);
        editTextPrecio = findViewById(R.id.inp_articulo_precio_actualiza);
        editTextIVA = findViewById(R.id.inp_articulo_iva_actualiza);
        editTextCodigoDeBarras = findViewById(R.id.inp_articulo_codigo_barras_actualiza);

    }

    void initToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar_actualiza_productos);
        toolbar.setTitle("Actualiza producto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }

    private void initConstrols() {
        ImageButton imageButtonScanner = findViewById(R.id.img_scanner_actualiza);
        imageButtonScanner.setOnClickListener(v -> Actividades.getSingleton(ActualizaProductoActivity.this, ScannerActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1));
    }

    private void getData() {
        Intent intent = getIntent();
        String productoGlobal = intent.getStringExtra(Actividades.PARAM_1);

        ProductDao productDao = new ProductDao();
        ProductBox productoBean = productDao.getProductoByArticulo(productoGlobal);

        if (productoBean != null) {
            editTextArticulo.setText(productoBean.getArticulo());
            editTextDescripcion.setText(productoBean.getDescripcion());
            editTextPrecio.setText(String.valueOf(productoBean.getPrecio()));
            editTextIVA.setText(String.valueOf(productoBean.getIva()));
            editTextCodigoDeBarras.setText(String.valueOf(productoBean.getCodigo_barras()));
            statusSeleccioandoDB = productoBean.getStatus();

            if (productoBean.getPath_img() != null){
                byte[] decodedString = Base64.decode(productoBean.getPath_img(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgView.setImageBitmap(decodedByte);
            }
        }
    }

    private void loadStatusSpinner(){
        String[] array = getArrayString(R.array.status_producto);
        List<String> arrayList = Utils.convertArrayStringListString(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_producto_status = findViewById(R.id.spinner_producto_status);
        spinner_producto_status.setAdapter(adapter);
        spinner_producto_status.setSelection(arrayList.indexOf(statusSeleccioandoDB));
        spinner_producto_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                status_seleccionado = spinner_producto_status.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    protected String[] getArrayString(final int id){
        return this.getResources().getStringArray(id);
    }

    private boolean validaCamposRequeridos(){
        boolean valida = true;

        listaCamposValidos = new ArrayList<>();

        String articulo = editTextArticulo.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        String precio  = editTextPrecio.getText().toString();
        String iva = editTextIVA.getText().toString();

        if (articulo.isEmpty()){
            valida = false;
            listaCamposValidos.add("articulo");
        }

        if (descripcion.isEmpty()){
            valida = false;
            listaCamposValidos.add("descripcion");
        }

        if (precio.isEmpty()){
            valida = false;
            listaCamposValidos.add("precio");
        }

        if (iva.isEmpty()){
            valida = false;
            listaCamposValidos.add("iva");

        }

        return valida;
    }


    private boolean exitProduct(){
        ProductDao dao = new ProductDao();
        ProductBox producto = dao .getProductoByArticulo(editTextArticulo.getText().toString());

        return producto != null;
    }

    private void saveProducto(){

        ProductDao dao = new ProductDao();
        ProductBox producto = dao .getProductoByArticulo(editTextArticulo.getText().toString());

        if (producto != null) {
            producto.setArticulo(editTextArticulo.getText().toString());
            producto.setDescripcion(editTextDescripcion.getText().toString());
            producto.setStatus(status_seleccionado);
            producto.setPrecio(Double.parseDouble(editTextPrecio.getText().toString()));
            producto.setIva(Integer.parseInt(editTextIVA.getText().toString()));
            producto.setCodigo_barras(editTextCodigoDeBarras.getText().toString());
            producto.setUpdatedAt(Utils.fechaActualHMS());
            if (decoded != null) {
                producto.setPath_img(getStringImage(decoded));
            }
            dao.insertBox(producto);

            idProducto = producto.getId();

            if (!Utils.isNetworkAvailable(getApplication())) {
                //showDialogNotConnectionInternet();
            } else {
                testLoadProductos(idProducto);
            }
        } else {
            Log.d("SysPoint", "Ha ocurrido un error, intente nuevamente saveProducto");
        }
    }

    private void showDialogNotConnectionInternet() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {

            ProgressDialog progressDialog = new ProgressDialog(ActualizaProductoActivity.this);
            progressDialog.setMessage("Espere un momento");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                progressDialog.dismiss();
                if (connected) testLoadProductos(idProducto);
            }).execute(), 100);
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void testLoadProductos(Long idProducto){
         progressshow();
        final ProductDao productDao = new ProductDao();
        List<ProductBox> listaProductosDB =  productDao.getProductoByID(idProducto);

        List<Product> listaProductos = new ArrayList<>();

        for (ProductBox item : listaProductosDB){
            Product producto = new Product();
            producto.setArticulo(item.getArticulo());
            producto.setDescripcion(item.getDescripcion());
            producto.setStatus(item.getStatus());
            producto.setPrecio(item.getPrecio());
            producto.setIva(item.getIva());
            producto.setUpdatedAt(item.getUpdatedAt());

            if (item.getCodigo_barras().isEmpty()){
                producto.setCodigoBarras("");
            }else {
                producto.setCodigoBarras(item.getCodigo_barras());
            }

            if (item.getPath_img() == null || item.getPath_img().isEmpty()){
                producto.setPathImage("");
            }else {
                producto.setPathImage(item.getPath_img());
            }

            listaProductos.add(producto);
        }

        new GetProductsInteractorImp().executeSaveProducts(listaProductos, new GetProductInteractor.OnSaveProductsListener() {
            @Override
            public void onSaveProductsSuccess() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Sincronizacion de productos exitosa", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSaveProductsError() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los productos", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    /**
     * uploadfoto-------------start.
     */
    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.permission_Read_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private void selectImage() {
        if (check_ReadStoragepermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "No encontrado";
        }
        return result;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        imageByteArray = baos.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }
}