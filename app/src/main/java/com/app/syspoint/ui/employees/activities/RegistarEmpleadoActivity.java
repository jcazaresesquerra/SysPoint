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
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.app.syspoint.R;
import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.roles.RolInteractor;
import com.app.syspoint.interactor.roles.RolInteractorImp;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Role;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.dao.EmployeeDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.utils.Constants;
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
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class RegistarEmpleadoActivity extends AppCompatActivity {

    private EditText ip_registro_empleado_nombre;
    private EditText ip_registro_empleado_direccion;
    private EditText ip_registro_empleado_email;
    private EditText ip_registro_empleado_telefono;
    private EditText ip_registro_empleado_fechaNacimiento;
    private EditText ip_registro_empleado_fecha_ingreso;
    private EditText ip_registro_empleado_contrasenia;
    private EditText ip_registro_empleado_contrasenia_valida;
    private EditText ip_registro_empleado_id;
    private Spinner rute_employee_spinner;
    private Spinner spinner_region_registro_empleado;
    private Spinner spinner_registro_empleado_status;
    private SwitchCompat checkbox_clientes_registro_empleado;
    private SwitchCompat checkbox_productos_registro_empleado;
    private SwitchCompat checkbox_ventas_registro_empleado;
    private SwitchCompat checkbox_empleados_registro_empleado;
    private SwitchCompat checkbox_inventarios_registro_empleado;
    private SwitchCompat checkbox_cobranza_registro_empleado;
    private SwitchCompat checkbox_edit_rute;
    private CircleImageView circleImageView;
    private RelativeLayout rlprogress;

    private List<String> listaCamposValidos;
    private String ruta_seleccionado;
    private String region_seleccionada;
    private String status_seleccionado;
    private int mYear, mMonth, mDay;
    byte[] imageByteArray;
    Bitmap decoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_empleado);
        rlprogress = findViewById(R.id.rlprogress_empleado_registro);
        this.initToolBar();
        this.initControls();

        ip_registro_empleado_fecha_ingreso.setText(Utils.fechaActualPicker());
        ip_registro_empleado_fechaNacimiento.setText(Utils.fechaActualPicker());
    }

    private void initControls() {
        circleImageView = findViewById(R.id.perfil_img);
        ImageButton imageButtonFechaIngreso = findViewById(R.id.img_button_fecha_ingreso);
        imageButtonFechaIngreso.setOnClickListener(v -> dateIngreso());

        ImageButton imageButtonFechaNacimiento = findViewById(R.id.img_button_fecha_nacimiento);
        imageButtonFechaNacimiento.setOnClickListener(v -> setBirdDate());

        ip_registro_empleado_nombre = findViewById(R.id.ip_registro_empleado_nombre);
        ip_registro_empleado_direccion = findViewById(R.id.ip_registro_empleado_direccion);
        ip_registro_empleado_email = findViewById(R.id.ip_registro_empleado_email);
        ip_registro_empleado_telefono = findViewById(R.id.ip_registro_empleado_telefono);
        ip_registro_empleado_fechaNacimiento = findViewById(R.id.ip_registro_empleado_fechaNacimiento);
        ip_registro_empleado_fecha_ingreso = findViewById(R.id.ip_registro_empleado_fecha_ingreso);
        ip_registro_empleado_contrasenia = findViewById(R.id.ip_registro_empleado_contrasenia);
        ip_registro_empleado_contrasenia_valida = findViewById(R.id.ip_registro_empleado_contrasenia_valida);
        ip_registro_empleado_id = findViewById(R.id.ip_registro_empleado_id);

        checkbox_clientes_registro_empleado = findViewById(R.id.checkbox_clientes_registro_empleado);
        checkbox_productos_registro_empleado = findViewById(R.id.checkbox_productos_registro_empleado);
        checkbox_ventas_registro_empleado = findViewById(R.id.checkbox_ventas_registro_empleado);
        checkbox_empleados_registro_empleado = findViewById(R.id.checkbox_empleados_registro_empleado);
        checkbox_inventarios_registro_empleado = findViewById(R.id.checkbox_inventario_registro_empleado);
        checkbox_cobranza_registro_empleado = findViewById(R.id.checkbox_cobranza_registro_empleado);
        checkbox_edit_rute = findViewById(R.id.checkbox_edit_rute);

        loadSpinnerRegion();
        loadSpinnerStatus();
        loadSpinnerRuteAndDay();
        loadSpinnerSelectedRegion();
    }

    private void loadSpinnerRuteAndDay() {
        /*List<String> arrayListRute = dao.getAllRutes();
        if (arrayListRute.size() < 2) {
            String[] array = getArrayString(R.array.ruteo_rango_rutas);
            arrayListRute = Utils.convertArrayStringListString(array);
        }*/
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

    private void loadSpinnerSelectedRegion() {
        String[] array = getArrayString(R.array.region);
        List<String> arrayList = Utils.convertArrayStringListString(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_region_registro_empleado = findViewById(R.id.spinner_region_registro_empleado);
        spinner_region_registro_empleado.setAdapter(adapter);
        spinner_region_registro_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionada = spinner_region_registro_empleado.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSpinnerRegion() {
        String[] array = getArrayString(R.array.region);
        List<String> arrayList = Utils.convertArrayStringListString(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_region_registro_empleado = findViewById(R.id.spinner_region_registro_empleado);
        spinner_region_registro_empleado.setAdapter(adapter);
        spinner_region_registro_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionada = spinner_region_registro_empleado.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSpinnerStatus() {
        String[] array = getArrayString(R.array.status_producto);
        List<String> arrayList = Utils.convertArrayStringListString(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_registro_empleado_status = findViewById(R.id.spinner_registro_empleado_status);
        spinner_registro_empleado_status.setAdapter(adapter);
        spinner_registro_empleado_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_seleccionado = spinner_registro_empleado_status.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void dateIngreso() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) ->
                        ip_registro_empleado_fecha_ingreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void setBirdDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) ->
                        ip_registro_empleado_fechaNacimiento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_registro_empleado, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.searchImagen:
                selectImage();
                return true;
            case R.id.guardaEmpleado:
                if (validaCampos()) {
                    if (!validaEmpleado()) {
                        final PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Registrar")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Desea registar el empleado")
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

                    } else {
                        PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Exitente")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Ya existe un registro con el identificador ingresado " + ip_registro_empleado_id.getText().toString())
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog.dismiss();
                                    }
                                })
                                .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.light_blue_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog.dismiss();
                                    }
                                });

                        dialog.setCancelable(false);
                        dialog.show();
                    }
                } else {

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

        String nombre = ip_registro_empleado_nombre.getText().toString();
        String contrasenia = ip_registro_empleado_contrasenia.getText().toString();
        String confirmar = ip_registro_empleado_contrasenia_valida.getText().toString();

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
        Toolbar toolbar = findViewById(R.id.toolbar_registro_empleado);
        toolbar.setTitle("Empleados");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    private boolean validaEmpleado() {
        EmployeeDao dao = new EmployeeDao();
        EmpleadoBean bean = dao.getEmployeeByIdentifier(ip_registro_empleado_id.getText().toString());
        return bean != null;
    }

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

    String idEmpleado;
    private void saveEmpleado() {
        EmployeeDao employeeDao = new EmployeeDao();
        EmpleadoBean employee = new EmpleadoBean();
        employee.setNombre(ip_registro_empleado_nombre.getText().toString());
        employee.setDireccion(ip_registro_empleado_direccion.getText().toString());
        employee.setEmail(ip_registro_empleado_email.getText().toString());
        employee.setTelefono(ip_registro_empleado_telefono.getText().toString());
        employee.setFecha_nacimiento(ip_registro_empleado_fechaNacimiento.getText().toString());
        employee.setFecha_ingreso(ip_registro_empleado_fecha_ingreso.getText().toString());
        employee.setContrasenia(ip_registro_empleado_contrasenia.getText().toString());
        employee.setIdentificador(ip_registro_empleado_id.getText().toString());
        employee.setRegion(region_seleccionada);
        employee.setStatus(status_seleccionado.compareToIgnoreCase("Activo") == 0);
        employee.setRute(ruta_seleccionado);
        if (decoded != null) employee.setPath_image(getStringImage(decoded));

        employeeDao.insert(employee);

        RolesDao rolEmployeeDao = new RolesDao();
        RolesBean rolRoutes = new RolesBean();
        rolRoutes.setEmpleado(employee);
        rolRoutes.setModulo("Rutas");
        rolRoutes.setActive(checkbox_edit_rute.isChecked());
        rolRoutes.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolRoutes);

        RolesBean rolCliente = new RolesBean();
        rolCliente.setEmpleado(employee);
        rolCliente.setModulo("Clientes");
        rolCliente.setActive(checkbox_clientes_registro_empleado.isChecked());
        rolCliente.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolCliente);

        RolesBean rolProducto = new RolesBean();
        rolProducto.setEmpleado(employee);
        rolProducto.setModulo("Productos");
        rolProducto.setActive(checkbox_productos_registro_empleado.isChecked());
        rolProducto.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolProducto);

        RolesBean rolVentas = new RolesBean();
        rolVentas.setEmpleado(employee);
        rolVentas.setModulo("Ventas");
        rolVentas.setActive(checkbox_ventas_registro_empleado.isChecked());
        rolVentas.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolVentas);

        RolesBean rolEmpleado = new RolesBean();
        rolEmpleado.setEmpleado(employee);
        rolEmpleado.setModulo("Empleados");
        rolEmpleado.setActive(checkbox_empleados_registro_empleado.isChecked());
        rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolEmpleado);

        RolesBean rolStock = new RolesBean();
        rolStock.setEmpleado(employee);
        rolStock.setModulo("Inventarios");
        rolStock.setActive(checkbox_inventarios_registro_empleado.isChecked());
        rolStock.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolStock);

        RolesBean rolCharge = new RolesBean();
        rolCharge.setEmpleado(employee);
        rolCharge.setModulo("Cobranza");
        rolCharge.setActive(checkbox_cobranza_registro_empleado.isChecked());
        rolCharge.setIdentificador(ip_registro_empleado_id.getText().toString());
        rolEmployeeDao.insert(rolCharge);

        idEmpleado = String.valueOf(employee.getId());

        if (!Utils.isNetworkAvailable(getApplication())){
            //showDialogNotConnectionInternet();
        }else {
            testLoadEmpleado(idEmpleado);
            enviaRolsServidor(employee.identificador);
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
        List<RolesBean> listaRolDB = rolesDao.getListaRolesByEmpleado(id);
        List<Role> listaRoles = new ArrayList<>();

        for (RolesBean items : listaRolDB){
            Role role = new Role();
            role.setEmpleado(items.getEmpleado().identificador);
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

    private void testLoadEmpleado(String id){
        progressshow();
        EmployeeDao employeeDao = new EmployeeDao();
        List<EmpleadoBean> listaEmpleadosDB = employeeDao.getEmployeeById(id);

        List<Employee> listEmpleados = new ArrayList<>();
        for (EmpleadoBean item : listaEmpleadosDB){
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
            empleado.setRegion(item.getRegion());
            empleado.setStatus(item.getStatus()? 1 : 0);

            if (item.getPath_image() == null || item.getPath_image().isEmpty()){
                empleado.setPathImage("");
            }else {
                empleado.setPathImage(item.getPath_image());
            }

            if (!item.rute.isEmpty()) {
                empleado.setRute(item.rute);
            } else  {
                empleado.setRute("");
            }

            listEmpleados.add(empleado);
        }

        new GetEmployeesInteractorImp().executeSaveEmployees(listEmpleados, new GetEmployeeInteractor.SaveEmployeeListener() {
            @Override
            public void onSaveEmployeeSuccess() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Empleados sincronizados", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSaveEmployeeError() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los empleados", Toast.LENGTH_LONG).show();
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