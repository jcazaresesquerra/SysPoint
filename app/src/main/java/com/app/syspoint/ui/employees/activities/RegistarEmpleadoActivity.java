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

import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.roles.RolInteractor;
import com.app.syspoint.interactor.roles.RolInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.dao.EmployeeDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Role;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
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


    byte[] imageByteArray;
    Bitmap decoded;

    private EditText ip_registro_empleado_nombre;
    private EditText ip_registro_empleado_direccion;
    private EditText ip_registro_empleado_email;
    private EditText ip_registro_empleado_telefono;
    private EditText ip_registro_empleado_fechaNacimiento;
    private EditText ip_registro_empleado_fecha_ingreso;
    private EditText ip_registro_empleado_fecha_egreso;
    private EditText ip_registro_empleado_contrasenia;
    private EditText ip_registro_empleado_contrasenia_valida;
    private EditText ip_registro_empleado_id;
    private EditText ip_registro_empleado_nss;
    private EditText ip_registro_empleado_rfc;
    private EditText ip_registro_empleado_curp;
    private EditText ip_registro_empleado_puesto;
    private EditText ip_registro_empleado_departamento;
    private Spinner spinner_tipo_contrato_registro_empleado;
    private Spinner rute_employee_spinner;
    private Spinner day_employee_spinner;
    private String ruta_seleccionado;
    private String dia_seleccionado;
    private String tipo_contrato_seleccionado;
    private String region_seleccionada;
    private EditText ip_registro_empleado_sueldo;

    private Spinner spinner_region_registro_empleado;
    private EditText ip_registro_empleado_hora_entrada;
    private EditText ip_registro_empleado_hora_salida;
    private EditText ip_registro_empleado_salida_comida;
    private EditText ip_registro_empleado_entrada_comida;
    private EditText ip_registro_empleado_turno;
    private Spinner spinner_registro_empleado_status;
    private String status_seleccionado;

    private ImageButton imageButtonFechaIngreso;
    private ImageButton imageButtonFechaEgreso;
    private ImageButton imageButtonFechaNacimiento;
    private int mYear, mMonth, mDay;
    private List<String> listaCamposValidos;
    CircleImageView circleImageView;
    private RelativeLayout rlprogress;


    private SwitchCompat checkbor_clientes_registro_empleado;
    private SwitchCompat checkbor_productos_registro_empleado;
    private SwitchCompat checkbor_ventas_registro_empleado;
    private SwitchCompat checkbor_empleados_registro_empleado;
    private SwitchCompat checkbor_inventarios_registro_empleado;
    private SwitchCompat checkbor_cobranza_registro_empleado;
    private SwitchCompat checkbor_edit_rute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_empleado);
        rlprogress = findViewById(R.id.rlprogress_empleado_registro);
        this.initToolBar();
        this.initControls();


        //Establece la fecha actual en los controles
        ip_registro_empleado_fecha_ingreso.setText(Utils.fechaActualPicker());
        ip_registro_empleado_fecha_egreso.setText(Utils.fechaActualPicker());
        ip_registro_empleado_fechaNacimiento.setText(Utils.fechaActualPicker());

    }

    private void initControls() {

        circleImageView = findViewById(R.id.perfil_img);
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

        ip_registro_empleado_nombre = findViewById(R.id.ip_registro_empleado_nombre);
        ip_registro_empleado_direccion = findViewById(R.id.ip_registro_empleado_direccion);
        ip_registro_empleado_email = findViewById(R.id.ip_registro_empleado_email);
        ip_registro_empleado_telefono = findViewById(R.id.ip_registro_empleado_telefono);
        ip_registro_empleado_fechaNacimiento = findViewById(R.id.ip_registro_empleado_fechaNacimiento);
        ip_registro_empleado_fecha_ingreso = findViewById(R.id.ip_registro_empleado_fecha_ingreso);
        ip_registro_empleado_fecha_egreso = findViewById(R.id.ip_registro_empleado_fecha_egreso);
        ip_registro_empleado_contrasenia = findViewById(R.id.ip_registro_empleado_contrasenia);
        ip_registro_empleado_contrasenia_valida = findViewById(R.id.ip_registro_empleado_contrasenia_valida);
        ip_registro_empleado_id = findViewById(R.id.ip_registro_empleado_id);
        ip_registro_empleado_nss = findViewById(R.id.ip_registro_empleado_nss);
        ip_registro_empleado_rfc = findViewById(R.id.ip_registro_empleado_rfc);
        ip_registro_empleado_curp = findViewById(R.id.ip_registro_empleado_curp);
        ip_registro_empleado_puesto = findViewById(R.id.ip_registro_empleado_puesto);
        ip_registro_empleado_departamento = findViewById(R.id.ip_registro_empleado_departamento);
        ip_registro_empleado_hora_entrada = findViewById(R.id.ip_registro_empleado_hora_entrada);
        ip_registro_empleado_hora_salida = findViewById(R.id.ip_registro_empleado_hora_salida);
        ip_registro_empleado_salida_comida = findViewById(R.id.ip_registro_empleado_salida_comida);
        ip_registro_empleado_entrada_comida = findViewById(R.id.ip_registro_empleado_entrada_comida);
        ip_registro_empleado_sueldo = findViewById(R.id.ip_registro_empleado_sueldo);
        ip_registro_empleado_turno = findViewById(R.id.ip_registro_empleado_turno);

        checkbor_clientes_registro_empleado = findViewById(R.id.checkbor_clientes_registro_empleado);
        checkbor_productos_registro_empleado = findViewById(R.id.checkbor_productos_registro_empleado);
        checkbor_ventas_registro_empleado = findViewById(R.id.checkbor_ventas_registro_empleado);
        checkbor_empleados_registro_empleado = findViewById(R.id.checkbor_empleados_registro_empleado);


        checkbor_inventarios_registro_empleado = findViewById(R.id.checkbor_inventario_registro_empleado);
        checkbor_cobranza_registro_empleado = findViewById(R.id.checkbor_cobranza_registro_empleado);
        checkbor_edit_rute = findViewById(R.id.checkbor_edit_rute);

        loadSpinnerRegion();
        loadSpinnerTipoContrato();
        loadSpinnerStatus();
        loadSpinnerRuteAndDay();
    }

    private void loadSpinnerRuteAndDay() {
        //Obtiene el array de las unidades de medida

        final RuteClientDao dao = new RuteClientDao();

        //Obtiene la lista de Strings
        List<String> arrayListRute = dao.getAllRutes();

        if (ruta_seleccionado.isEmpty()) {
            ruta_seleccionado = arrayListRute.get(0);
        }

        //Creamos el adaptador
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Obtiene el array de las unidades de medida
        String[] arrayDay = getArrayString(R.array.edit_day);

        //Obtiene la lista de Strings
        List<String> arrayListDay = Utils.convertArrayStringListString(arrayDay);

        //Creamos el adaptador
        ArrayAdapter<String> adapterDay = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayListDay);
        day_employee_spinner = findViewById(R.id.day_employee_spinner);
        day_employee_spinner.setAdapter(adapterDay);
        day_employee_spinner.setSelection(arrayListDay.indexOf(dia_seleccionado));
        day_employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dia_seleccionado = day_employee_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerRegion() {
        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.region);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_tipo_contrato_registro_empleado = findViewById(R.id.spinner_tipo_contrato_registro_empleado);
        spinner_tipo_contrato_registro_empleado.setAdapter(adapter);
        spinner_tipo_contrato_registro_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo_contrato_seleccionado = spinner_tipo_contrato_registro_empleado.getSelectedItem().toString();
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
        spinner_region_registro_empleado = findViewById(R.id.spinner_region_registro_empleado);
        spinner_region_registro_empleado.setAdapter(adapter);
        spinner_region_registro_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionada = spinner_region_registro_empleado.getSelectedItem().toString();
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
        spinner_registro_empleado_status = findViewById(R.id.spinner_registro_empleado_status);
        spinner_registro_empleado_status.setAdapter(adapter);
        spinner_registro_empleado_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                status_seleccionado = spinner_registro_empleado_status.getSelectedItem().toString();
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

                        ip_registro_empleado_fecha_ingreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                        ip_registro_empleado_fecha_egreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                        ip_registro_empleado_fechaNacimiento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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
                        final PrettyDialog dialog = new PrettyDialog(this);
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

        boolean valida = true;

        EmployeeDao dao = new EmployeeDao();
        EmpleadoBean bean = dao.getEmployeeByIdentifier(ip_registro_empleado_id.getText().toString());

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
        EmployeeDao employeeDao = new EmployeeDao();
        EmpleadoBean empleado = new EmpleadoBean();
        empleado.setNombre(ip_registro_empleado_nombre.getText().toString());
        empleado.setDireccion(ip_registro_empleado_direccion.getText().toString());
        empleado.setEmail(ip_registro_empleado_email.getText().toString());
        empleado.setTelefono(ip_registro_empleado_telefono.getText().toString());
        empleado.setFecha_nacimiento(ip_registro_empleado_fechaNacimiento.getText().toString());
        empleado.setFecha_ingreso(ip_registro_empleado_fecha_ingreso.getText().toString());
        empleado.setFecha_egreso(ip_registro_empleado_fecha_egreso.getText().toString());
        empleado.setContrasenia(ip_registro_empleado_contrasenia.getText().toString());
        empleado.setIdentificador(ip_registro_empleado_id.getText().toString());
        empleado.setNss(ip_registro_empleado_nss.getText().toString());
        empleado.setRfc(ip_registro_empleado_rfc.getText().toString());
        empleado.setCurp(ip_registro_empleado_curp.getText().toString());
        empleado.setPuesto(ip_registro_empleado_puesto.getText().toString());
        empleado.setArea_depto(ip_registro_empleado_departamento.getText().toString());
        empleado.setTipo_contrato(tipo_contrato_seleccionado);
        empleado.setRegion(region_seleccionada);
        empleado.setHora_entrada(ip_registro_empleado_hora_entrada.getText().toString());
        empleado.setHora_salida(ip_registro_empleado_hora_salida.getText().toString());
        empleado.setSalida_comer(ip_registro_empleado_salida_comida.getText().toString());
        empleado.setEntrada_comer(ip_registro_empleado_entrada_comida.getText().toString());
        String sueldo = ip_registro_empleado_sueldo.getText().toString();
        if (sueldo.isEmpty()) {
            empleado.setSueldo_diario(0);
        } else {
            empleado.setSueldo_diario(Double.parseDouble(sueldo));
        }
        empleado.setTurno(ip_registro_empleado_turno.getText().toString());
        if (status_seleccionado.compareToIgnoreCase("Activo") == 0) {
            empleado.setStatus(true);
        } else {
            empleado.setStatus(false);
        }

        if (decoded != null) {
            empleado.setPath_image(getStringImage(decoded));
        }

        if (checkbor_edit_rute.isChecked()){
            empleado.setEdit_ruta(1);
        } else {
            empleado.setEdit_ruta(0);
        }

        empleado.setRute(ruta_seleccionado);

        if (dia_seleccionado.compareToIgnoreCase("Lunes") == 0) {
            empleado.setDay(1);
        } else if (dia_seleccionado.compareToIgnoreCase("Martes") == 0) {
            empleado.setDay(2);
        } else if (dia_seleccionado.compareToIgnoreCase("Miercoles") == 0) {
            empleado.setDay(3);
        } else if (dia_seleccionado.compareToIgnoreCase("Jueves") == 0) {
            empleado.setDay(4);
        } else if (dia_seleccionado.compareToIgnoreCase("Viernes") == 0) {
            empleado.setDay(5);
        } else if (dia_seleccionado.compareToIgnoreCase("Sabado") == 0) {
            empleado.setDay(6);
        } else if (dia_seleccionado.compareToIgnoreCase("Domingo") == 0) {
            empleado.setDay(7);
        } else {
            empleado.setDay(0);
        }

        employeeDao.insert(empleado);

        //Insera el registro de los modulos
        if (checkbor_clientes_registro_empleado.isChecked()) {
            RolesBean rolCliente = new RolesBean();
            RolesDao rolClienteDao = new RolesDao();
            rolCliente.setEmpleado(empleado);
            rolCliente.setModulo("Clientes");
            rolCliente.setActive(true);
            rolCliente.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolClienteDao.insert(rolCliente);
        }else {
            RolesBean rolCliente = new RolesBean();
            RolesDao rolClienteDao = new RolesDao();
            rolCliente.setEmpleado(empleado);
            rolCliente.setModulo("Clientes");
            rolCliente.setActive(false);
            rolCliente.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolClienteDao.insert(rolCliente);
        }

        if (checkbor_productos_registro_empleado.isChecked()) {
            RolesBean rolProducto = new RolesBean();
            RolesDao rolProductoDao = new RolesDao();
            rolProducto.setEmpleado(empleado);
            rolProducto.setModulo("Productos");
            rolProducto.setActive(true);
            rolProducto.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolProductoDao.insert(rolProducto);
        }else{
            RolesBean rolProducto = new RolesBean();
            RolesDao rolProductoDao = new RolesDao();
            rolProducto.setEmpleado(empleado);
            rolProducto.setModulo("Productos");
            rolProducto.setActive(false);
            rolProducto.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolProductoDao.insert(rolProducto);
        }

        if (checkbor_ventas_registro_empleado.isChecked()) {
            RolesBean rolVentas = new RolesBean();
            RolesDao rolVentasDao = new RolesDao();
            rolVentas.setEmpleado(empleado);
            rolVentas.setModulo("Ventas");
            rolVentas.setActive(true);
            rolVentas.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolVentasDao.insert(rolVentas);
        }else{
            RolesBean rolVentas = new RolesBean();
            RolesDao rolVentasDao = new RolesDao();
            rolVentas.setEmpleado(empleado);
            rolVentas.setModulo("Ventas");
            rolVentas.setActive(false);
            rolVentas.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolVentasDao.insert(rolVentas);
        }

        if (checkbor_empleados_registro_empleado.isChecked()) {
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Empleados");
            rolEmpleado.setActive(true);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }else{
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Empleados");
            rolEmpleado.setActive(false);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }

        if (checkbor_inventarios_registro_empleado.isChecked()) {
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Inventarios");
            rolEmpleado.setActive(true);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }else{
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Inventarios");
            rolEmpleado.setActive(false);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }


        if (checkbor_cobranza_registro_empleado.isChecked()) {
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Cobranza");
            rolEmpleado.setActive(true);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }else{
            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Cobranza");
            rolEmpleado.setActive(false);
            rolEmpleado.setIdentificador(ip_registro_empleado_id.getText().toString());
            rolEmpleadoDao.insert(rolEmpleado);
        }




        idEmpleado = String.valueOf(empleado.getId());

        if (!Utils.isNetworkAvailable(getApplication())){
            //showDialogNotConnectionInternet();
        }else {
            testLoadEmpleado(idEmpleado);
            enviaRolsServidor(empleado.identificador);
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

            if (!item.rute.isEmpty()) {
                empleado.setRute(item.rute);
            } else  {
                empleado.setRute("");
            }

            if (item.day > 0 && item.day <=7) {
                empleado.setDay(item.getDay());
            } else {
                empleado.setDay(0);
            }

            if (item.getEdit_ruta() == 1){
                empleado.setEditRuta(1);
            }else{
                empleado.setEditRuta(0);
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