package com.app.syspoint.ui.employees.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.roles.RolInteractor;
import com.app.syspoint.interactor.roles.RolInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Role;
import com.app.syspoint.models.enums.RoleType;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
import com.app.syspoint.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class ActualizarEmpleadoActivity extends AppCompatActivity {

    private final static String TAG = "ActualizarEmpleadoActivity";
    private EditText ip_actualiza_empleado_nombre;
    private EditText ip_actualiza_empleado_direccion;
    private EditText ip_actualiza_empleado_email;
    private EditText ip_actualiza_empleado_telefono;
    private EditText ip_actualiza_empleado_fechaNacimiento;
    private EditText ip_actualiza_empleado_fecha_ingreso;
    private EditText ip_actualiza_empleado_contrasenia;
    private EditText ip_actualiza_empleado_contrasenia_valida;
    private EditText ip_actualiza_empleado_id;
    private Spinner spinner_actualiza_empleado_status;
    private Spinner rute_employee_spinner;
    private SwitchCompat checkbox_clientes_actualiza_empleado;
    private SwitchCompat checkbox_productos_actualiza_empleado;
    private SwitchCompat checkbox_ventas_actualiza_empleado;
    private SwitchCompat checkbox_empleados_actualiza_empleado;
    private SwitchCompat checkbox_inventario_actualiza_empleado;
    private SwitchCompat checkbox_cobranza_actualiza_empleado;
    private SwitchCompat checkbox_edit_rute;
    private SwitchCompat checkbox_edit_rute_order;
    private CircleImageView circleImageView;
    private RelativeLayout rlprogress;

    private List<String> listaCamposValidos;
    private String status_seleccionado;
    private String ruta_seleccionado;
    private Long idEmpleado;
    private String empladoGlobal;
    byte[] imageByteArray;
    private Bitmap decoded;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_empleado);
        rlprogress = findViewById(R.id.rlprogress_empleado_actualiza);
        this.initToolBar();
        this.initControls();
        getData();
        loadSpinnerStatus();
        loadSpinnerRute();
    }

    private void getData(){
        Intent intent = getIntent();
        empladoGlobal = intent.getStringExtra(Actividades.PARAM_1);

        try {
            EmployeeDao employeeDao = new EmployeeDao();
            EmployeeBox empleadoBean = employeeDao.getEmployeeByIdentifier(empladoGlobal);

            if (empleadoBean != null){
                ip_actualiza_empleado_nombre.setText(empleadoBean.getNombre());
                ip_actualiza_empleado_direccion.setText(empleadoBean.getDireccion());
                ip_actualiza_empleado_email.setText(empleadoBean.getEmail());
                ip_actualiza_empleado_telefono.setText(empleadoBean.getTelefono());
                ip_actualiza_empleado_fechaNacimiento.setText(empleadoBean.getFecha_nacimiento());
                ip_actualiza_empleado_fecha_ingreso.setText(empleadoBean.getFecha_ingreso());
                ip_actualiza_empleado_contrasenia.setText(empleadoBean.getContrasenia());
                ip_actualiza_empleado_contrasenia_valida.setText(empleadoBean.getContrasenia());
                ip_actualiza_empleado_id.setText(empleadoBean.getIdentificador());

                if (empleadoBean.getStatus()) {
                    status_seleccionado = "InActivo";
                } else {
                    status_seleccionado = "Activo";
                }

                // set rute and day
                ruta_seleccionado = empleadoBean.getRute();

                final RolesDao rolesDao = new RolesDao();
                final List<RolesBox> listRoles = rolesDao.getListaRolesByEmpleado(empleadoBean.getIdentificador());

                //Contiene la lista de roles
                for(RolesBox item: listRoles){
                    if (item.getModulo().compareToIgnoreCase(RoleType.CLIENTS.getValue()) == 0){
                        checkbox_clientes_actualiza_empleado.setChecked(item.getActive());
                    }
                    if (item.getModulo().compareToIgnoreCase(RoleType.PRODUCTS.getValue()) == 0){
                        checkbox_productos_actualiza_empleado.setChecked(item.getActive());
                    }
                    if (item.getModulo().compareToIgnoreCase(RoleType.SELLS.getValue()) == 0){
                        checkbox_ventas_actualiza_empleado.setChecked(item.getActive());
                    }
                    if (item.getModulo().compareToIgnoreCase(RoleType.EMPLOYEES.getValue()) == 0){
                        checkbox_empleados_actualiza_empleado.setChecked(item.getActive());
                    }
                    if (item.getModulo().compareToIgnoreCase(RoleType.STOCK.getValue()) == 0){
                        checkbox_inventario_actualiza_empleado.setChecked(item.getActive());
                    }
                    if(item.getModulo().compareToIgnoreCase(RoleType.CHARGE.getValue()) == 0){
                        checkbox_cobranza_actualiza_empleado.setChecked(item.getActive());
                    }
                    if(item.getModulo().compareToIgnoreCase(RoleType.RUTES.getValue()) == 0){
                        checkbox_edit_rute.setChecked(item.getActive());
                    }
                    if(item.getModulo().compareToIgnoreCase(RoleType.ORDER_RUTES.getValue()) == 0){
                        checkbox_edit_rute_order.setChecked(item.getActive());
                    }
                }

                if (empleadoBean.getPath_image() != null){
                    byte[] decodedString = Base64.decode(empleadoBean.getPath_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    circleImageView.setImageBitmap(decodedByte);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initControls() {
        circleImageView = findViewById(R.id.perfil_actualiza_img);
        ImageButton imageButtonFechaIngreso = findViewById(R.id.img_button_fecha_ingreso);
        imageButtonFechaIngreso.setOnClickListener(v -> dateIngreso());

        ImageButton imageButtonFechaNacimiento = findViewById(R.id.img_button_fecha_nacimiento);
        imageButtonFechaNacimiento.setOnClickListener(v -> setBirdDate());

        ip_actualiza_empleado_nombre = findViewById(R.id.ip_actualiza_empleado_nombre);
        ip_actualiza_empleado_direccion = findViewById(R.id.ip_actualiza_empleado_direccion);
        ip_actualiza_empleado_email = findViewById(R.id.ip_actualiza_empleado_email);
        ip_actualiza_empleado_telefono = findViewById(R.id.ip_actualiza_empleado_telefono);
        ip_actualiza_empleado_fechaNacimiento = findViewById(R.id.ip_actualiza_empleado_fechaNacimiento);
        ip_actualiza_empleado_fecha_ingreso = findViewById(R.id.ip_actualiza_empleado_fecha_ingreso);
        ip_actualiza_empleado_contrasenia = findViewById(R.id.ip_actualiza_empleado_contrasenia);
        ip_actualiza_empleado_contrasenia_valida = findViewById(R.id.ip_actualiza_empleado_contrasenia_valida);
        ip_actualiza_empleado_id = findViewById(R.id.ip_actualiza_empleado_id);
        checkbox_clientes_actualiza_empleado = findViewById(R.id.checkbox_clientes_actualiza_empleado);
        checkbox_productos_actualiza_empleado = findViewById(R.id.checkbox_productos_actualiza_empleado);
        checkbox_ventas_actualiza_empleado = findViewById(R.id.checkbox_ventas_actualiza_empleado);
        checkbox_empleados_actualiza_empleado = findViewById(R.id.checkbox_empleados_actualiza_empleado);
        checkbox_inventario_actualiza_empleado = findViewById(R.id.checkbox_inventarios_actualiza_empleado);
        checkbox_cobranza_actualiza_empleado = findViewById(R.id.checkbox_cobranza_actualiza_empleado);
        checkbox_edit_rute = findViewById(R.id.checkbox_edit_rute);
        checkbox_edit_rute_order = findViewById(R.id.checkbox_edit_rute_order);
    }

    private void loadSpinnerStatus() {
        String[] array = getArrayString(R.array.status_producto);
        List<String> arrayList = Utils.convertArrayStringListString(array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_actualiza_empleado_status = findViewById(R.id.spinner_actualiza_empleado_status);
        spinner_actualiza_empleado_status.setAdapter(adapter);
        spinner_actualiza_empleado_status.setSelection(arrayList.indexOf(status_seleccionado));
        spinner_actualiza_empleado_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_seleccionado = spinner_actualiza_empleado_status.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSpinnerRute() {
        /*RuteClientDao dao = new RuteClientDao();
        List<String> arrayListRute = dao.getAllRutes();*/
        String[] array = getArrayString(R.array.ruteo_rango_rutas);
        List<String> arrayListRute = Utils.convertArrayStringListString(array);

        if (ruta_seleccionado == null || ruta_seleccionado.isEmpty()) {
            ruta_seleccionado = arrayListRute.get(0);
        }

        ArrayAdapter<String> adapterRute = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayListRute);
        rute_employee_spinner = findViewById(R.id.rute_employee_spinner);
        rute_employee_spinner.setAdapter(adapterRute);
        rute_employee_spinner.setSelection(arrayListRute.indexOf(ruta_seleccionado));
        rute_employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ruta_seleccionado = rute_employee_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void dateIngreso() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) ->
                        ip_actualiza_empleado_fecha_ingreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void setBirdDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) ->
                        ip_actualiza_empleado_fechaNacimiento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualiza_empleado, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Timber.tag(TAG).d("home -> click");
                finish();
                return true;
            case R.id.searchActualizaImagenEmpleado:
                Timber.tag(TAG).d("searchActualizaImagenEmpleado -> click");
                selectImage();
                return true;
            case R.id.actualizaEmpleado:
                Timber.tag(TAG).d("actualizaEmpleado -> click");
                if (validaCampos()) {
                    if (validaEmpleado()) {
                        final PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Actualizar")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("¿Desea actualizar el empleado?")
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog.dismiss();
                                    }
                                })
                                .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        saveEmpleado();
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
                    }
                }else {
                    StringBuilder campos = new StringBuilder();
                    for (String validItem : listaCamposValidos) {
                        campos.append(validItem).append("\n");
                    }

                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Campor requeridos")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de completar los campos requeridos " + "\n" + campos)
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });

                    dialog.setCancelable(false);
                    dialog.show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean validaCampos() {

        listaCamposValidos = new ArrayList<>();
        boolean valida = true;

        String nombre = ip_actualiza_empleado_nombre.getText().toString();
        String contrasenia = ip_actualiza_empleado_contrasenia.getText().toString();
        String confirmar = ip_actualiza_empleado_contrasenia_valida.getText().toString();

        if (nombre.isEmpty()) {
            valida = false;
            listaCamposValidos.add("nombre");
        }

        if (contrasenia.isEmpty()) {
            valida = false;
            listaCamposValidos.add("contrasenia");
        }

        if (confirmar.isEmpty()) {
            valida = false;
            listaCamposValidos.add("confirmar");
        }

        return valida;
    }

    void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_actualiza_empleado);
        toolbar.setTitle("Actualizar empleado");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
    }

    private boolean validaEmpleado() {
        EmployeeDao dao = new EmployeeDao();
        EmployeeBox bean = dao.getEmployeeByIdentifier(ip_actualiza_empleado_id.getText().toString());
        return bean != null;
    }

    private boolean check_ReadStoragepermission() {
        int permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        Constants.permission_Read_data);
                return false;
            }
        } else {
            permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.permission_Read_data);
                return false;
            }
        }

        return true;

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
                circleImageView.setImageBitmap(rotatedBitmap);
                imageByteArray = baos.toByteArray();
                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        imageByteArray = baos.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }


    private void saveEmpleado() {
        EmployeeDao dao = new EmployeeDao();
        EmployeeBox bean = dao.getEmployeeByIdentifier(ip_actualiza_empleado_id.getText().toString());

        bean.setNombre(ip_actualiza_empleado_nombre.getText().toString());
        bean.setDireccion(ip_actualiza_empleado_direccion.getText().toString());
        bean.setEmail(ip_actualiza_empleado_email.getText().toString());
        bean.setTelefono(ip_actualiza_empleado_telefono.getText().toString());
        bean.setFecha_nacimiento(ip_actualiza_empleado_fechaNacimiento.getText().toString());
        bean.setFecha_ingreso(ip_actualiza_empleado_fecha_ingreso.getText().toString());
        bean.setContrasenia(ip_actualiza_empleado_contrasenia.getText().toString());
        bean.setIdentificador(ip_actualiza_empleado_id.getText().toString());
        bean.setStatus(status_seleccionado.compareToIgnoreCase("Activo") == 0);
        bean.setUpdatedAt(Utils.fechaActualHMS());
        bean.setRute(ruta_seleccionado);

        if (decoded != null) bean.setPath_image(getStringImage(decoded));

        dao.insertBox(bean);

        final RolesDao rolesDao = new RolesDao();

        RolesBox rutesRolBox = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.RUTES.getValue());
        if (rutesRolBox != null){
            rutesRolBox.setActive(checkbox_edit_rute.isChecked());
            rolesDao.insertBox(rutesRolBox);
        } else {
            rutesRolBox = new RolesBox();
            rutesRolBox.getEmpleado().setTarget(bean);
            rutesRolBox.setModulo(RoleType.RUTES.getValue());
            rutesRolBox.setActive(checkbox_edit_rute.isChecked());
            rutesRolBox.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(rutesRolBox);
        }

        RolesBox orderRutesRolBox = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.ORDER_RUTES.getValue());
        if (orderRutesRolBox != null){
            orderRutesRolBox.setActive(checkbox_edit_rute_order.isChecked());
            rolesDao.insertBox(orderRutesRolBox);
        } else {
            orderRutesRolBox = new RolesBox();
            orderRutesRolBox.getEmpleado().setTarget(bean);
            orderRutesRolBox.setModulo(RoleType.ORDER_RUTES.getValue());
            orderRutesRolBox.setActive(checkbox_edit_rute_order.isChecked());
            orderRutesRolBox.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(orderRutesRolBox);
        }

        RolesBox employeesRolBox = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.CLIENTS.getValue());
        if (employeesRolBox != null){
            employeesRolBox.setActive(checkbox_clientes_actualiza_empleado.isChecked());
            rolesDao.insertBox(employeesRolBox);
        } else {
            employeesRolBox = new RolesBox();
            employeesRolBox.getEmpleado().setTarget(bean);
            employeesRolBox.setModulo(RoleType.CLIENTS.getValue());
            employeesRolBox.setActive(checkbox_clientes_actualiza_empleado.isChecked());
            employeesRolBox.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(employeesRolBox);
        }

        RolesBox productsRolBox = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.PRODUCTS.getValue());
        if (productsRolBox != null){
            productsRolBox.setActive(checkbox_productos_actualiza_empleado.isChecked());
            rolesDao.insertBox(productsRolBox);
        } else {
            productsRolBox = new RolesBox();
            productsRolBox.getEmpleado().setTarget(bean);
            productsRolBox.setModulo(RoleType.PRODUCTS.getValue());
            productsRolBox.setActive(checkbox_productos_actualiza_empleado.isChecked());
            productsRolBox.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(productsRolBox);
        }

        RolesBox moduloVentas = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.SELLS.getValue());
        if (moduloVentas != null){
            moduloVentas.setActive(checkbox_ventas_actualiza_empleado.isChecked());
            rolesDao.insertBox(moduloVentas);
        } else {
            moduloVentas = new RolesBox();
            moduloVentas.getEmpleado().setTarget(bean);
            moduloVentas.setModulo(RoleType.SELLS.getValue());
            moduloVentas.setActive(checkbox_ventas_actualiza_empleado.isChecked());
            moduloVentas.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(moduloVentas);
        }

        RolesBox moduloEmpleado = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.EMPLOYEES.getValue());
        if (moduloEmpleado != null){
            moduloEmpleado.setActive(checkbox_empleados_actualiza_empleado.isChecked());
            rolesDao.insertBox(moduloEmpleado);
        } else {
            moduloEmpleado = new RolesBox();
            moduloEmpleado.getEmpleado().setTarget(bean);
            moduloEmpleado.setModulo(RoleType.EMPLOYEES.getValue());
            moduloEmpleado.setActive(checkbox_empleados_actualiza_empleado.isChecked());
            moduloEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(moduloEmpleado);
        }

        RolesBox moduloInventario = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.STOCK.getValue());
        if (moduloInventario != null){
            moduloInventario.setActive(checkbox_inventario_actualiza_empleado.isChecked());
            rolesDao.insertBox(moduloInventario);
        }else{
            RolesBox rolEmpleado = new RolesBox();
            rolEmpleado.getEmpleado().setTarget(bean);
            rolEmpleado.setModulo(RoleType.STOCK.getValue());
            rolEmpleado.setActive(checkbox_inventario_actualiza_empleado.isChecked());
            rolEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(rolEmpleado);
        }

        RolesBox moduloCobranza = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), RoleType.CHARGE.getValue());
        if (moduloCobranza != null){
            moduloCobranza.setActive(checkbox_cobranza_actualiza_empleado.isChecked());
            rolesDao.insertBox(moduloCobranza);
        }else{
            moduloCobranza = new RolesBox();
            moduloCobranza.getEmpleado().setTarget(bean);
            moduloCobranza.setModulo(RoleType.CHARGE.getValue());
            moduloCobranza.setActive(checkbox_cobranza_actualiza_empleado.isChecked());
            moduloCobranza.setIdentificador(ip_actualiza_empleado_id.getText().toString());
            rolesDao.insertBox(moduloCobranza);
        }

        idEmpleado = bean.getId();

        if (!Utils.isNetworkAvailable(getApplication())){
            //showDialogNotConnectionInternet();
        }else {
            testLoadEmpleado(idEmpleado);
            enviaRolsServidor(bean.getIdentificador());
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

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            testLoadEmpleado(idEmpleado);
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void enviaRolsServidor(String id){
        progressshow();
        RolesDao rolesDao = new RolesDao();
        List<RolesBox> listaRolDB = rolesDao.getListaRolesByEmpleado(id);
        List<Role> listaRoles = new ArrayList<>();

        for (RolesBox items : listaRolDB){
            Role role = new Role();
            role.setEmpleado(items.getIdentificador());
            role.setModulo(items.getModulo());
            role.setActivo(items.getActive()? 1 : 0);

            listaRoles.add(role);
        }

        new RolInteractorImp().executeSaveRoles(listaRoles, new RolInteractor.OnSaveRolesListener() {
            @Override
            public void onSaveRolesSuccess() {
                //Toast.makeText(getApplicationContext(), "Rol sincronizado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveRolesError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar el rol", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testLoadEmpleado(Long id){

        progressshow();
        final EmployeeDao employeeDao = new EmployeeDao();
        List<EmployeeBox> listaEmpleadosDB = employeeDao.getEmployeeById(id);

        List<Employee> listEmpleados = new ArrayList<>();
        for (EmployeeBox item : listaEmpleadosDB){
            Employee empleado = new Employee();
            empleado.setNombre(item.getNombre());
            if (item.getDireccion().isEmpty()){
                empleado.setDireccion("-");
            }else{
                empleado.setDireccion(item.getDireccion());
            }
            empleado.setEmail(item.getEmail());
            if (item.getTelefono().isEmpty()){
                empleado.setTelefono("-");
            }else{
                empleado.setTelefono(item.getTelefono());
            }

            if (item.getFecha_nacimiento().isEmpty()){
                empleado.setFechaNacimiento("-");
            }else{
                empleado.setFechaNacimiento(item.getFecha_nacimiento());
            }

            if (item.getFecha_ingreso().isEmpty()){
                empleado.setFechaIngreso("-");
            }else{
                empleado.setFechaIngreso(item.getFecha_ingreso());
            }

            empleado.setContrasenia(item.getContrasenia());
            empleado.setIdentificador(item.getIdentificador());
            empleado.setStatus(item.getStatus()? 1 : 0);
            empleado.setUpdatedAt(item.getUpdatedAt());

            if (item.getPath_image() == null || item.getPath_image().isEmpty()){
                empleado.setPathImage("");
            }else {
                empleado.setPathImage(item.getPath_image());
            }

            if (!item.getRute().isEmpty()) {
                empleado.setRute(item.getRute());
            } else  {
                empleado.setRute("");
            }

            listEmpleados.add(empleado);
        }

        new GetEmployeesInteractorImp().executeSaveEmployees(listEmpleados, new GetEmployeeInteractor.SaveEmployeeListener() {
            @Override
            public void onSaveEmployeeSuccess() {
                progresshide();
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Empleados sincronizados", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSaveEmployeeError() {
                progresshide();
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Ha ocurrido un error al sincronizar los empleados", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }
}