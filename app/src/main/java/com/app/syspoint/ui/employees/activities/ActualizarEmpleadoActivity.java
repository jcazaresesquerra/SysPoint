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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.syspoint.models.json.EmployeeJson;
import com.app.syspoint.models.json.RolJson;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.dao.EmployeeDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.repository.request.http.ApiServices;
import com.app.syspoint.repository.request.http.PointApi;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Role;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.ValidaCampos;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarEmpleadoActivity extends AppCompatActivity {

    byte[] imageByteArray;
    Bitmap decoded;
    private EditText ip_actualiza_empleado_nombre;
    private EditText ip_actualiza_empleado_direccion;
    private EditText ip_actualiza_empleado_email;
    private EditText ip_actualiza_empleado_telefono;
    private EditText ip_actualiza_empleado_fechaNacimiento;
    private EditText ip_actualiza_empleado_fecha_ingreso;
    private EditText ip_actualiza_empleado_fecha_egreso;
    private EditText ip_actualiza_empleado_contrasenia;
    private EditText ip_actualiza_empleado_contrasenia_valida;
    private EditText ip_actualiza_empleado_id;
    private EditText ip_actualiza_empleado_nss;
    private EditText ip_actualiza_empleado_rfc;
    private EditText ip_actualiza_empleado_curp;
    private EditText ip_actualiza_empleado_puesto;
    private EditText ip_actualiza_empleado_departamento;
    private Spinner spinner_tipo_contrato_actualiza_empleado;
    private String tipo_contrato_seleccionado;
    private String region_seleccionada;
    private EditText ip_actualiza_empleado_sueldo;

    private Spinner spinner_region_actualiza_empleado;
    private EditText ip_actualiza_empleado_hora_entrada;
    private EditText ip_actualiza_empleado_hora_salida;
    private EditText ip_actualiza_empleado_salida_comida;
    private EditText ip_actualiza_empleado_entrada_comida;
    private EditText ip_actualiza_empleado_turno;
    private Spinner spinner_actualiza_empleado_status;
    private String status_seleccionado;

    private ImageButton imageButtonFechaIngreso;
    private ImageButton imageButtonFechaEgreso;
    private ImageButton imageButtonFechaNacimiento;
    private int mYear, mMonth, mDay;
    private List<ValidaCampos> listaCamposValidos;
    private String empladoGlobal;
    EmpleadoBean empleadoBean = null;
    CircleImageView circleImageView;
    private RelativeLayout rlprogress;



    private SwitchCompat checkbor_clientes_actualiza_empleado;
    private SwitchCompat checkbor_productos_actualiza_empleado;
    private SwitchCompat checkbor_ventas_actualiza_empleado;
    private SwitchCompat checkbor_empleados_actualiza_empleado;
    private SwitchCompat checkbor_inventario_actualiza_empleado;
    private SwitchCompat checkbor_cobranza_actualiza_empleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_empleado);
        rlprogress = findViewById(R.id.rlprogress_empleado_actualiza);
        this.initToolBar();
        this.initControls();
        getData();
        loadSpinnerRegion();
        loadSpinnerTipoContrato();
        loadSpinnerStatus();
    }

    private void getData(){
        Intent intent = getIntent();
        empladoGlobal = intent.getStringExtra(Actividades.PARAM_1);

        try {

            EmployeeDao employeeDao = new EmployeeDao();
            EmpleadoBean empleadoBean = employeeDao.getEmployeeByIdentifier(empladoGlobal);

            if (empleadoBean != null){

                ip_actualiza_empleado_nombre.setText(empleadoBean.getNombre());
                ip_actualiza_empleado_direccion.setText(empleadoBean.getDireccion());
                ip_actualiza_empleado_email.setText(empleadoBean.getEmail());
                ip_actualiza_empleado_telefono.setText(empleadoBean.getTelefono());
                ip_actualiza_empleado_fechaNacimiento.setText(empleadoBean.getFecha_nacimiento());
                ip_actualiza_empleado_fecha_ingreso.setText(empleadoBean.getFecha_ingreso());
                ip_actualiza_empleado_fecha_egreso.setText(empleadoBean.getFecha_egreso());
                ip_actualiza_empleado_contrasenia.setText(empleadoBean.contrasenia);
                ip_actualiza_empleado_contrasenia_valida.setText(empleadoBean.contrasenia);
                ip_actualiza_empleado_nss.setText(empleadoBean.getNss());
                ip_actualiza_empleado_rfc.setText(empleadoBean.getRfc());
                ip_actualiza_empleado_curp.setText(empleadoBean.getCurp());
                ip_actualiza_empleado_puesto.setText(empleadoBean.getPuesto());
                ip_actualiza_empleado_id.setText(empleadoBean.getIdentificador());
                //ip_actualiza_empleado_departamento.setText(empleadoBean.getPuesto());
                ip_actualiza_empleado_hora_entrada.setText(empleadoBean.getHora_entrada());
                ip_actualiza_empleado_hora_salida.setText(empleadoBean.getHora_salida());
                ip_actualiza_empleado_salida_comida.setText(empleadoBean.getSalida_comer());
                ip_actualiza_empleado_entrada_comida.setText(empleadoBean.entrada_comer);
                ip_actualiza_empleado_sueldo.setText("" +empleadoBean.getSueldo_diario());
                ip_actualiza_empleado_turno.setText(empleadoBean.getTurno());

                tipo_contrato_seleccionado = empleadoBean.getTipo_contrato();
                if (empleadoBean.getStatus() ==  false) {
                    status_seleccionado = "Activo";
                }else {
                    status_seleccionado = "InActivo";
                }


                final RolesDao rolesDao = new RolesDao();
                final List<RolesBean> listRoles = rolesDao.getListaRolesByEmpleado(empleadoBean.getIdentificador());

                //Contiene la lista de roles
                for(RolesBean item: listRoles){

                    if (item.getModulo().compareToIgnoreCase("Clientes") ==  0 && item.getActive() == true){
                        checkbor_clientes_actualiza_empleado.setChecked(true);
                    }else if (item.getModulo().compareToIgnoreCase("Productos") ==  0 && item.getActive() == true){
                        checkbor_productos_actualiza_empleado.setChecked(true);
                    }else if (item.getModulo().compareToIgnoreCase("Ventas") ==  0 && item.getActive() == true){
                        checkbor_ventas_actualiza_empleado.setChecked(true);
                    }else if (item.getModulo().compareToIgnoreCase("Empleados") ==  0 && item.getActive() == true){
                        checkbor_empleados_actualiza_empleado.setChecked(true);
                    }else if (item.getModulo().compareToIgnoreCase("Inventarios") ==  0 && item.getActive() == true){
                        checkbor_inventario_actualiza_empleado.setChecked(true);
                    }else if(item.getModulo().compareToIgnoreCase("Cobranza") == 0 && item.getActive() == true){
                        checkbor_cobranza_actualiza_empleado.setChecked(true);
                    }
                }

                region_seleccionada = empleadoBean.getRegion();

                if (empleadoBean.getPath_image() != null){
                    byte[] decodedString = Base64.decode(empleadoBean.getPath_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    circleImageView.setImageBitmap(decodedByte);
                }
            }

        }catch (Exception e){

        }

    }

    private void initControls() {

        circleImageView = findViewById(R.id.perfil_actualiza_img);
        imageButtonFechaIngreso = findViewById(R.id.img_button_fecha_ingreso);
        imageButtonFechaIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateIngreso();
            }
        });

        imageButtonFechaEgreso = findViewById(R.id.img_button_fecha_egreso);
        imageButtonFechaEgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEgreso();
            }
        });

        imageButtonFechaNacimiento = findViewById(R.id.img_button_fecha_nacimiento);
        imageButtonFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFechaNacimiento();
            }
        });

        ip_actualiza_empleado_nombre = findViewById(R.id.ip_actualiza_empleado_nombre);
        ip_actualiza_empleado_direccion = findViewById(R.id.ip_actualiza_empleado_direccion);
        ip_actualiza_empleado_email = findViewById(R.id.ip_actualiza_empleado_email);
        ip_actualiza_empleado_telefono = findViewById(R.id.ip_actualiza_empleado_telefono);
        ip_actualiza_empleado_fechaNacimiento = findViewById(R.id.ip_actualiza_empleado_fechaNacimiento);
        ip_actualiza_empleado_fecha_ingreso = findViewById(R.id.ip_actualiza_empleado_fecha_ingreso);
        ip_actualiza_empleado_fecha_egreso = findViewById(R.id.ip_actualiza_empleado_fecha_egreso);
        ip_actualiza_empleado_contrasenia = findViewById(R.id.ip_actualiza_empleado_contrasenia);
        ip_actualiza_empleado_contrasenia_valida = findViewById(R.id.ip_actualiza_empleado_contrasenia_valida);
        ip_actualiza_empleado_id = findViewById(R.id.ip_actualiza_empleado_id);
        ip_actualiza_empleado_nss = findViewById(R.id.ip_actualiza_empleado_nss);
        ip_actualiza_empleado_rfc = findViewById(R.id.ip_actualiza_empleado_rfc);
        ip_actualiza_empleado_curp = findViewById(R.id.ip_actualiza_empleado_curp);
        ip_actualiza_empleado_puesto = findViewById(R.id.ip_actualiza_empleado_puesto);
        ip_actualiza_empleado_departamento = findViewById(R.id.ip_actualiza_empleado_departamento);
        ip_actualiza_empleado_hora_entrada = findViewById(R.id.ip_actualiza_empleado_hora_entrada);
        ip_actualiza_empleado_hora_salida = findViewById(R.id.ip_actualiza_empleado_hora_salida);
        ip_actualiza_empleado_salida_comida = findViewById(R.id.ip_actualiza_empleado_salida_comida);
        ip_actualiza_empleado_entrada_comida = findViewById(R.id.ip_actualiza_empleado_entrada_comida);
        ip_actualiza_empleado_sueldo = findViewById(R.id.ip_actualiza_empleado_sueldo);
        ip_actualiza_empleado_turno = findViewById(R.id.ip_actualiza_empleado_turno);

        checkbor_clientes_actualiza_empleado = findViewById(R.id.checkbor_clientes_actualiza_empleado);
        checkbor_productos_actualiza_empleado = findViewById(R.id.checkbor_productos_actualiza_empleado);
        checkbor_ventas_actualiza_empleado = findViewById(R.id.checkbor_ventas_actualiza_empleado);
        checkbor_empleados_actualiza_empleado = findViewById(R.id.checkbor_empleados_actualiza_empleado);
        checkbor_inventario_actualiza_empleado = findViewById(R.id.checkbor_inventarios_actualiza_empleado);
        checkbor_cobranza_actualiza_empleado = findViewById(R.id.checkbor_cobranza_actualiza_empleado);
    }

    private void loadSpinnerRegion() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.region);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_region_actualiza_empleado = findViewById(R.id.spinner_region_actualiza_empleado);
        spinner_region_actualiza_empleado.setAdapter(adapter);
        spinner_region_actualiza_empleado.setSelection(arrayList.indexOf(region_seleccionada));
        spinner_region_actualiza_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionada = spinner_region_actualiza_empleado.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerTipoContrato() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.tipo_contrato);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_tipo_contrato_actualiza_empleado = findViewById(R.id.spinner_tipo_contrato_actualiza_empleado);
        spinner_tipo_contrato_actualiza_empleado.setAdapter(adapter);
        spinner_tipo_contrato_actualiza_empleado.setSelection(arrayList.indexOf(tipo_contrato_seleccionado));
        spinner_tipo_contrato_actualiza_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo_contrato_seleccionado = spinner_tipo_contrato_actualiza_empleado.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerStatus() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.status_producto);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void dateIngreso() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        ip_actualiza_empleado_fecha_ingreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void dateEgreso() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        ip_actualiza_empleado_fecha_egreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void dateFechaNacimiento() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        ip_actualiza_empleado_fechaNacimiento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
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
                finish();
                return true;

            case R.id.searchActualizaImagenEmpleado:
                selectImage();
                return true;

            case R.id.actualizaEmpleado:

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

                    String campos = "";
                    for (ValidaCampos elemento : listaCamposValidos) {
                        campos += "" + elemento.getCampo() + "\n";
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
            listaCamposValidos.add(new ValidaCampos("nombre"));
        }

        if (contrasenia.isEmpty()) {
            valida = false;
            listaCamposValidos.add(new ValidaCampos("contrasenia"));
        }

        if (confirmar.isEmpty()) {
            valida = false;
            listaCamposValidos.add(new ValidaCampos("confirmar"));
        }

        return valida;
    }

    void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_actualiza_empleado);
        toolbar.setTitle("Actualizar empleado");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }

    }

    private boolean validaEmpleado() {

        boolean valida = true;

        EmployeeDao dao = new EmployeeDao();
        EmpleadoBean bean = dao.getEmployeeByIdentifier(ip_actualiza_empleado_id.getText().toString());

        if (bean == null) {
            valida = false;
        } else {
            valida = true;
        }
        return valida;
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
        EmployeeDao dao = new EmployeeDao();
        EmpleadoBean bean = dao.getEmployeeByIdentifier(ip_actualiza_empleado_id.getText().toString());
        bean.setNombre(ip_actualiza_empleado_nombre.getText().toString());
        bean.setDireccion(ip_actualiza_empleado_direccion.getText().toString());
        bean.setEmail(ip_actualiza_empleado_email.getText().toString());
        bean.setTelefono(ip_actualiza_empleado_telefono.getText().toString());
        bean.setFecha_nacimiento(ip_actualiza_empleado_fechaNacimiento.getText().toString());
        bean.setFecha_ingreso(ip_actualiza_empleado_fecha_ingreso.getText().toString());
        bean.setFecha_egreso(ip_actualiza_empleado_fecha_egreso.getText().toString());
        bean.setContrasenia(ip_actualiza_empleado_contrasenia.getText().toString());
        bean.setIdentificador(ip_actualiza_empleado_id.getText().toString());
        bean.setNss(ip_actualiza_empleado_nss.getText().toString());
        bean.setRfc(ip_actualiza_empleado_rfc.getText().toString());
        bean.setCurp(ip_actualiza_empleado_curp.getText().toString());
        bean.setPuesto(ip_actualiza_empleado_puesto.getText().toString());
        bean.setArea_depto(ip_actualiza_empleado_departamento.getText().toString());
        bean.setTipo_contrato(tipo_contrato_seleccionado);
        bean.setRegion(region_seleccionada);
        bean.setHora_entrada(ip_actualiza_empleado_hora_entrada.getText().toString());
        bean.setHora_salida(ip_actualiza_empleado_hora_salida.getText().toString());
        bean.setSalida_comer(ip_actualiza_empleado_salida_comida.getText().toString());
        bean.setEntrada_comer(ip_actualiza_empleado_entrada_comida.getText().toString());
        String sueldo = ip_actualiza_empleado_sueldo.getText().toString();

        if (sueldo.isEmpty()){
            bean.setSueldo_diario(0);
        }else {
            bean.setSueldo_diario(Double.parseDouble(sueldo));
        }
        bean.setTurno(ip_actualiza_empleado_turno.getText().toString());
        if (status_seleccionado.compareToIgnoreCase("Activo") == 0) {
            bean.setStatus(true);
        } else {
            bean.setStatus(false);
        }
        if (decoded != null) {
            bean.setPath_image(getStringImage(decoded));
        }
        //Registra el empleado
        dao.save(bean);


        final RolesDao rolesDao = new RolesDao();
        final RolesBean moduloClientes;
        final RolesBean moduloProductos;
        final RolesBean moduloVentas;
        final RolesBean moduloEmpleado;
        final RolesBean moduloInventario;
        final RolesBean moduloCobranza;

        if  (checkbor_clientes_actualiza_empleado.isChecked()){
            moduloClientes = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Clientes");
            if (moduloClientes != null){
                moduloClientes.setActive(true);
                rolesDao.save(moduloClientes);
            }
        }else {
            moduloClientes = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Clientes");
            if (moduloClientes != null){
                moduloClientes.setActive(false);
                rolesDao.save(moduloClientes);
            }
        }

        if (checkbor_productos_actualiza_empleado.isChecked()) {
            moduloProductos = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Productos");
            if (moduloProductos != null){
                moduloProductos.setActive(true);
                rolesDao.save(moduloProductos);
            }
        }else {
            moduloProductos = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Productos");
            if (moduloProductos != null){
                moduloProductos.setActive(false);
                rolesDao.save(moduloProductos);
            }
        }

        if (checkbor_ventas_actualiza_empleado.isChecked()){
            moduloVentas = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Ventas");
            if (moduloVentas != null){
                moduloVentas.setActive(true);
                rolesDao.save(moduloVentas);
            }

        }else {
            moduloVentas = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Ventas");
            if (moduloVentas != null){
                moduloVentas.setActive(false);
                rolesDao.save(moduloVentas);
            }
        }


        if (checkbor_empleados_actualiza_empleado.isChecked()){
            moduloEmpleado = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Empleados");
            if (moduloEmpleado != null){
                moduloEmpleado.setActive(true);
                rolesDao.save(moduloEmpleado);
            }
        }else {
            moduloEmpleado = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Empleados");
            if (moduloEmpleado != null){
                moduloEmpleado.setActive(false);
                rolesDao.save(moduloEmpleado);
            }
        }


        if (checkbor_inventario_actualiza_empleado.isChecked()){
            moduloInventario = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Inventarios");
            if (moduloInventario != null){
                moduloInventario.setActive(true);
                rolesDao.save(moduloInventario);
            }else{
                RolesBean rolEmpleado = new RolesBean();
                RolesDao rolEmpleadoDao = new RolesDao();
                rolEmpleado.setEmpleado(bean);
                rolEmpleado.setModulo("Inventarios");
                rolEmpleado.setActive(true);
                rolEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
                rolEmpleadoDao.insert(rolEmpleado);
            }
        }else {
            moduloInventario = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Inventarios");
            if (moduloInventario != null){
                moduloInventario.setActive(false);
                rolesDao.save(moduloInventario);
            }else{
                RolesBean rolEmpleado = new RolesBean();
                RolesDao rolEmpleadoDao = new RolesDao();
                rolEmpleado.setEmpleado(bean);
                rolEmpleado.setModulo("Inventarios");
                rolEmpleado.setActive(true);
                rolEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
                rolEmpleadoDao.insert(rolEmpleado);
            }
        }

        if (checkbor_cobranza_actualiza_empleado.isChecked()){
            moduloCobranza = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Cobranza");
            if (moduloCobranza != null){
                moduloCobranza.setActive(true);
                rolesDao.save(moduloCobranza);
            }else{
                RolesBean rolEmpleado = new RolesBean();
                RolesDao rolEmpleadoDao = new RolesDao();
                rolEmpleado.setEmpleado(bean);
                rolEmpleado.setModulo("Cobranza");
                rolEmpleado.setActive(true);
                rolEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
                rolEmpleadoDao.insert(rolEmpleado);
            }
        }else {
            moduloCobranza = rolesDao.getRolByEmpleado(ip_actualiza_empleado_id.getText().toString(), "Cobranza");
            if (moduloCobranza != null){
                moduloCobranza.setActive(false);
                rolesDao.save(moduloCobranza);
            }else{
                RolesBean rolEmpleado = new RolesBean();
                RolesDao rolEmpleadoDao = new RolesDao();
                rolEmpleado.setEmpleado(bean);
                rolEmpleado.setModulo("Cobranza");
                rolEmpleado.setActive(true);
                rolEmpleado.setIdentificador(ip_actualiza_empleado_id.getText().toString());
                rolEmpleadoDao.insert(rolEmpleado);
            }
        }

        idEmpleado = String.valueOf(bean.getId());

        if (!Utils.isNetworkAvailable(getApplication())){
            showDialogNotConnectionInternet();
        }else {
            testLoadEmpleado(idEmpleado);
            enviaRolsServidor(bean.identificador);
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

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLoadEmpleado(idEmpleado);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void enviaRolsServidor(String id){
     progressshow();

        final RolesDao rolesDao = new RolesDao();
        List<RolesBean> listaRolDB = new ArrayList<>();
        listaRolDB = rolesDao.getListaRolesByEmpleado(id);

        List<Role> listaRoles = new ArrayList<>();
        for (RolesBean items : listaRolDB){
            final Role role = new Role();

            role.setEmpleado(items.getEmpleado().identificador);
            role.setModulo(items.getModulo());
            if (items.getActive()== true){
                role.setActivo(1);
            }else {
                role.setActivo(0);
            }
            listaRoles.add(role);
        }


        final RolJson rolsJson = new RolJson();
        rolsJson.setRoles(listaRoles);

        Call<RolJson> enviaRoles = ApiServices.getClientRestrofit().create(PointApi.class).saveRoles(rolsJson);
        String json = new Gson().toJson(rolsJson);
        Log.d("Roles", json);
        enviaRoles.enqueue(new Callback<RolJson>() {
            @Override
            public void onResponse(Call<RolJson> call, Response<RolJson> response) {

                if (response.isSuccessful()){
                }
            }

            @Override
            public void onFailure(Call<RolJson> call, Throwable t) {
            }
        });

    }

    private void testLoadEmpleado(String id){

        progressshow();
        final EmployeeDao employeeDao = new EmployeeDao();
        List<EmpleadoBean> listaEmpleadosDB = new ArrayList<>();
        listaEmpleadosDB =  employeeDao.getEmployeeById(id);

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

            if (item.getFecha_egreso().isEmpty()){
                empleado.setFechaEgreso("-");
            }else{
                empleado.setFechaEgreso(item.getFecha_egreso());
            }

            empleado.setContrasenia(item.getContrasenia());
            empleado.setIdentificador(item.getIdentificador());
            if (item.getNss().isEmpty()){
                empleado.setNss("-");
            }else{
                empleado.setNss(item.getNss());
            }

            if (item.getRfc().isEmpty()){
                empleado.setRfc("-");
            }else{
                empleado.setRfc(item.getRfc());
            }

            if (item.getCurp().isEmpty()){
                empleado.setCurp("-");
            }else{
                empleado.setCurp(item.getCurp());
            }

            if (item.getPuesto().isEmpty()){
                empleado.setPuesto("-");
            }else{
                empleado.setPuesto(item.getPuesto());
            }

            if (item.getArea_depto().isEmpty()){
                empleado.setAreaDepto("--");
            }else{
                empleado.setAreaDepto(item.getArea_depto());
            }

            empleado.setTipoContrato(item.getTipo_contrato());
            empleado.setRegion(item.getRegion());

            if (item.getHora_entrada().isEmpty()){
                empleado.setHoraEntrada("");
            }else{
                empleado.setHoraEntrada(item.getHora_entrada());
            }

            if (item.getHora_salida().isEmpty()){
                empleado.setHoraSalida("");
            }else{
                empleado.setHoraSalida(item.getHora_salida());
            }

            if (item.getSalida_comer().isEmpty()){
                empleado.setSalidaComer("");
            }else{
                empleado.setSalidaComer(item.getSalida_comer());
            }

            if (item.getEntrada_comer().isEmpty()){
                empleado.setEntradaComer("");
            }else{
                empleado.setEntradaComer(item.getEntrada_comer());
            }

            empleado.setSueldoDiario(0);
            if (item.getStatus() == false){
                empleado.setStatus(0);
            }else {
                empleado.setStatus(1);
            }

            if (item.getPath_image() == null || item.getPath_image().isEmpty()){
                empleado.setPathImage("");
            }else {
                empleado.setPathImage(item.getPath_image());
            }

            if (item.getTurno().isEmpty()){
                empleado.setTurno("--");
            }else{
                empleado.setTurno(item.getEntrada_comer());
            }

            listEmpleados.add(empleado);
        }

        EmployeeJson empleadoRF = new EmployeeJson();
        empleadoRF.setEmployees(listEmpleados);
        String json = new Gson().toJson(empleadoRF);
        Log.d("SinEmpleados", json);

        Call<EmployeeJson> loadEmleado = ApiServices.getClientRestrofit().create(PointApi.class).sendEmpleado(empleadoRF);

        loadEmleado.enqueue(new Callback<EmployeeJson>() {
            @Override
            public void onResponse(Call<EmployeeJson> call, Response<EmployeeJson> response) {
                if(response.isSuccessful()){
                    progresshide();
                    Toast.makeText(ActualizarEmpleadoActivity.this, "Empleados sincronizados", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<EmployeeJson> call, Throwable t) {
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
}