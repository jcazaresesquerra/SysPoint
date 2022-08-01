package com.app.syspoint.ui.empleados;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.syspoint.R;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.dao.EmpleadoDao;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.ValidaCampos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditarEmpleadoActivity extends AppCompatActivity {


    private EditText ip_editar_empleado_nombre;
    private EditText ip_editar_empleado_direccion;
    private EditText ip_editar_empleado_email;
    private EditText ip_editar_empleado_telefono;
    private EditText ip_editar_empleado_fechaNacimiento;
    private EditText ip_editar_empleado_fecha_ingreso;
    private EditText ip_editar_empleado_fecha_egreso;
    private EditText ip_editar_empleado_contrasenia;
    private EditText ip_editar_empleado_contrasenia_valida;
    private EditText ip_editar_empleado_id;
    private EditText ip_editar_empleado_nss;
    private EditText ip_editar_empleado_rfc;
    private EditText ip_editar_empleado_curp;
    private EditText ip_editar_empleado_puesto;
    private EditText ip_editar_empleado_departamento;
    private Spinner spinner_tipo_contrato_editar_empleado;
    private String tipo_contrato_seleccionado;
    private String region_seleccionada;
    private EditText ip_editar_empleado_sueldo;

    private Spinner spinner_region_editar_empleado;
    private EditText ip_editar_empleado_hora_entrada;
    private EditText ip_editar_empleado_hora_salida;
    private EditText ip_editar_empleado_salida_comida;
    private EditText ip_editar_empleado_entrada_comida;
    private EditText ip_editar_empleado_turno;
    private Spinner spinner_editar_empleado_status;
    private String status_seleccionado;

    private ImageButton imageButtonFechaIngreso;
    private ImageButton imageButtonFechaEgreso;
    private ImageButton imageButtonFechaNacimiento;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private List<ValidaCampos> listaCamposValidos;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empleado);

        this.initToolBar();
        this.initControls();
        this.iniParametros();
    }


    private void iniParametros() {
    }

    private void initControls() {

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

        ip_editar_empleado_nombre = findViewById(R.id.ip_editar_empleado_nombre);
        ip_editar_empleado_direccion = findViewById(R.id.ip_editar_empleado_direccion);
        ip_editar_empleado_email = findViewById(R.id.ip_editar_empleado_email);
        ip_editar_empleado_telefono = findViewById(R.id.ip_editar_empleado_telefono);
        ip_editar_empleado_fechaNacimiento = findViewById(R.id.ip_editar_empleado_fechaNacimiento);
        ip_editar_empleado_fecha_ingreso = findViewById(R.id.ip_editar_empleado_fecha_ingreso);
        ip_editar_empleado_fecha_egreso = findViewById(R.id.ip_editar_empleado_fecha_egreso);
        ip_editar_empleado_contrasenia = findViewById(R.id.ip_editar_empleado_contrasenia);
        ip_editar_empleado_contrasenia_valida = findViewById(R.id.ip_editar_empleado_contrasenia_valida);
        ip_editar_empleado_id = findViewById(R.id.ip_editar_empleado_id);
        ip_editar_empleado_nss = findViewById(R.id.ip_editar_empleado_nss);
        ip_editar_empleado_rfc = findViewById(R.id.ip_editar_empleado_rfc);
        ip_editar_empleado_curp = findViewById(R.id.ip_editar_empleado_curp);
        ip_editar_empleado_puesto = findViewById(R.id.ip_editar_empleado_puesto);
        ip_editar_empleado_departamento = findViewById(R.id.ip_editar_empleado_departamento);
        ip_editar_empleado_hora_entrada = findViewById(R.id.ip_editar_empleado_hora_entrada);
        ip_editar_empleado_hora_salida = findViewById(R.id.ip_editar_empleado_hora_salida);
        ip_editar_empleado_salida_comida = findViewById(R.id.ip_editar_empleado_salida_comida);
        ip_editar_empleado_entrada_comida = findViewById(R.id.ip_editar_empleado_entrada_comida);
        ip_editar_empleado_sueldo = findViewById(R.id.ip_editar_empleado_sueldo);
        ip_editar_empleado_turno = findViewById(R.id.ip_editar_empleado_turno);

        loadSpinnerRegion();
        loadSpinnerTipoContrato();
        loadSpinnerStatus();
    }


    private void loadSpinnerRegion() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.region);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_tipo_contrato_editar_empleado = findViewById(R.id.spinner_tipo_contrato_editar_empleado);
        spinner_tipo_contrato_editar_empleado.setAdapter(adapter);
        spinner_tipo_contrato_editar_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo_contrato_seleccionado = spinner_tipo_contrato_editar_empleado.getSelectedItem().toString();
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
        spinner_region_editar_empleado = findViewById(R.id.spinner_region_editar_empleado);
        spinner_region_editar_empleado.setAdapter(adapter);
        spinner_region_editar_empleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionada = spinner_region_editar_empleado.getSelectedItem().toString();
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
        spinner_editar_empleado_status = findViewById(R.id.spinner_editar_empleado_status);
        spinner_editar_empleado_status.setAdapter(adapter);
        spinner_editar_empleado_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                status_seleccionado = spinner_editar_empleado_status.getSelectedItem().toString();
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

                        ip_editar_empleado_fecha_ingreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                        ip_editar_empleado_fecha_egreso.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                        ip_editar_empleado_fechaNacimiento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    private boolean validaCampos() {

        listaCamposValidos = new ArrayList<>();
        boolean valida = true;

        String nombre = ip_editar_empleado_nombre.getText().toString();

        String contrasenia = ip_editar_empleado_contrasenia.getText().toString();

        String confirmar = ip_editar_empleado_contrasenia_valida.getText().toString();


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
        Toolbar toolbar = findViewById(R.id.toolbar_edita_empleado);
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

        EmpleadoDao dao = new EmpleadoDao();
        EmpleadoBean bean = dao.getEmpleadoByIdentificador(ip_editar_empleado_id.getText().toString());

        if (bean == null) {
            valida = false;
        } else {
            valida = true;
        }
        return valida;
    }

    private void updateEmpleado() {
        EmpleadoDao empleadoDao = new EmpleadoDao();
        EmpleadoBean empleado = new EmpleadoBean();
        empleado.setNombre(ip_editar_empleado_nombre.getText().toString());
        empleado.setDireccion(ip_editar_empleado_direccion.getText().toString());
        empleado.setEmail(ip_editar_empleado_email.getText().toString());
        empleado.setTelefono(ip_editar_empleado_telefono.getText().toString());
        empleado.setFecha_nacimiento(ip_editar_empleado_fechaNacimiento.getText().toString());
        empleado.setFecha_ingreso(ip_editar_empleado_fecha_ingreso.getText().toString());
        empleado.setFecha_egreso(ip_editar_empleado_fecha_egreso.getText().toString());
        empleado.setContrasenia(ip_editar_empleado_contrasenia.getText().toString());
        empleado.setIdentificador(ip_editar_empleado_id.getText().toString());
        empleado.setNss(ip_editar_empleado_nss.getText().toString());
        empleado.setRfc(ip_editar_empleado_rfc.getText().toString());
        empleado.setCurp(ip_editar_empleado_curp.getText().toString());
        empleado.setPuesto(ip_editar_empleado_puesto.getText().toString());
        empleado.setArea_depto(ip_editar_empleado_departamento.getText().toString());
        empleado.setTipo_contrato(tipo_contrato_seleccionado);
        empleado.setRegion(region_seleccionada);
        empleado.setHora_entrada(ip_editar_empleado_hora_entrada.getText().toString());
        empleado.setHora_salida(ip_editar_empleado_hora_salida.getText().toString());
        empleado.setSalida_comer(ip_editar_empleado_salida_comida.getText().toString());
        empleado.setEntrada_comer(ip_editar_empleado_entrada_comida.getText().toString());
        empleado.setSueldo_diario(Double.parseDouble(ip_editar_empleado_sueldo.getText().toString()));
        empleado.setTurno(ip_editar_empleado_turno.getText().toString());
        if (status_seleccionado.compareToIgnoreCase("Activo") == 0) {
            empleado.setStatus(true);
        } else {
            empleado.setStatus(false);
        }
        //Registra el empleado
        empleadoDao.insert(empleado);
        finish();
    }
}