package com.app.syspoint.ui.login;

import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.syspoint.MainActivity;
import com.app.syspoint.MainAdminActivity;
import com.app.syspoint.R;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.PersistenciaPrecioBean;
import com.app.syspoint.db.bean.PreciosEspecialesBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.bean.SesionBean;
import com.app.syspoint.db.bean.TaskBean;
import com.app.syspoint.db.bean.UserSesion;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.ClientesRutaDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.CobrosDao;
import com.app.syspoint.db.dao.EmpleadoDao;
import com.app.syspoint.db.dao.InventarioDao;
import com.app.syspoint.db.dao.InventarioHistorialDao;
import com.app.syspoint.db.dao.PartidasDao;
import com.app.syspoint.db.dao.PersistenciaPrecioDao;
import com.app.syspoint.db.dao.PreciosEspecialesDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.db.dao.RolesDao;
import com.app.syspoint.db.dao.RuteoDao;
import com.app.syspoint.db.dao.SesionDao;
import com.app.syspoint.db.dao.TaskDao;
import com.app.syspoint.db.dao.VentasDao;
import com.app.syspoint.db.dao.VisitasDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.Data;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.Empleado;
import com.app.syspoint.json.Precio;
import com.app.syspoint.json.Producto;
import com.app.syspoint.json.Role;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.cache.CacheInteractor;

import java.io.IOException;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private RelativeLayout rlprogress_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControls();
        creaUsuario();
        validaPermisos();
        validaPersistencia();
        rlprogress_login = findViewById(R.id.rlprogress_login);
        TextView appVersion = findViewById(R.id.app_version);
        appVersion.setText(getString(R.string.app_name) + " Ver 1.40.0" );
        if  (!existTask()) {
            Call<Data> getData = ApiServices.getClientRestrofit().create(PointApi.class).getAllData();
            new donwloadGetDataAsync().execute(getData);
        }
    }

    private boolean existTask(){

        boolean exist = false;
        TaskDao taskDao = new TaskDao();
        TaskBean taskBean = taskDao.getTask(Utils.fechaActual());

        if (taskBean == null)
        {

            final InventarioDao inventarioDao = new InventarioDao();
            inventarioDao.clear();

            final InventarioHistorialDao historialDao = new InventarioHistorialDao();
            historialDao.clear();

            final VentasDao ventasDao = new VentasDao();
            ventasDao.clear();

            final PartidasDao partidasDao = new PartidasDao();
            partidasDao.clear();

            final VisitasDao visitasDao = new VisitasDao();
            visitasDao.clear();

            final CobranzaDao cobranzaDao = new CobranzaDao();
            cobranzaDao.clear();

            final CobrosDao cobrosDao = new CobrosDao();
            cobrosDao.clear();

            final RuteoDao ruteoDao = new RuteoDao();
            ruteoDao.clear();

            final EmpleadoDao empleadoDao = new EmpleadoDao();
            empleadoDao.clear();

            final RolesDao rolesDao = new RolesDao();
            rolesDao.clear();

            final ClientesRutaDao clientesRutaDao = new ClientesRutaDao();
            clientesRutaDao.clear();

            final PreciosEspecialesDao preciosEspecialesDao = new PreciosEspecialesDao();
            preciosEspecialesDao.clear();

            TaskDao dao = new TaskDao();
            dao.clear();

            TaskBean bean = new TaskBean();
            bean.setDate(Utils.fechaActual());
            bean.setTask("Sincronización");
            dao.insert(bean);
            exist = false;
        }else {
            exist = true;
        }

        return exist;
    }
    private void validaPersistencia() {

        PersistenciaPrecioDao persisteDao = new PersistenciaPrecioDao();


        int existe = persisteDao.existePersistencia();

        if (existe == 0){

            PersistenciaPrecioBean persistenciaPrecioBean = new PersistenciaPrecioBean();
            PersistenciaPrecioDao persistenciaPrecioDao = new PersistenciaPrecioDao();
            persistenciaPrecioBean.setId(Long.valueOf(1));
            persistenciaPrecioBean.setMostrar("All");
            persistenciaPrecioBean.setValor(Long.valueOf(1));
            persistenciaPrecioDao.insert(persistenciaPrecioBean);

        }
    }
    private void initControls() {

        Button buttonLogin = findViewById(R.id.btnSignIn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usuario = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (validaUsuarioLogin()) {

                    final UserSesion userSesion = new UserSesion();
                    userSesion.setUsuario(usuario);
                    userSesion.setPassword(password);

                    final SesionDao sesionDao = new SesionDao();
                    sesionDao.clear();

                    final SesionBean sesionBean = new SesionBean();
                    final EmpleadoDao empleadoDao = new EmpleadoDao();
                    final EmpleadoBean usuarioBean = empleadoDao.getByEmail(userSesion.getUsuario());

                    if (usuarioBean != null) {
                        sesionBean.setEmpleado(usuarioBean);
                        sesionBean.setEmpleadoId(usuarioBean.getId());
                        sesionBean.setRemember(false);
                        sesionDao.saveSesion(sesionBean);
                    } else {
                        showDialog();
                        return;
                    }

                    AppBundle.setUserSession(userSesion);

                    showActivityMain();
                } else {
                    showDialog();
                }
                //showActivityMain();
            }
        });

        editTextEmail = findViewById(R.id.et_login_email);
        editTextPassword = findViewById(R.id.et_login_password);
    }
    private boolean validaUsuarioLogin() {

        boolean valida = false;
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        final EmpleadoDao empleadoDao = new EmpleadoDao();
        final EmpleadoBean empleadoBean = empleadoDao.getValidaLogin(email, password);

        if (empleadoBean == null) {
            valida = false;
        } else {
            valida = true;
        }

        return valida;
    }

    private void showDialog() {
        final PrettyDialog dialog = new PrettyDialog(this);
        dialog.setTitle("No encontrado")
                .setTitleColor(R.color.purple_500)
                .setMessage("Usuario no encontrado verifique los datos de acceso")
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

    private void showActivityMain() {

        //Obtiene el nombre del vendedor
        final EmpleadoBean vendedoresBean = AppBundle.getUserBean();

        // guarda al vendedor en cache
        CacheInteractor cacheInteractor = new CacheInteractor(LoginActivity.this);
        cacheInteractor.saveSeller(vendedoresBean);
        EmpleadoBean empleado = cacheInteractor.getSeller();

        String identificador = "";
        if (vendedoresBean != null){
            identificador = vendedoresBean.getIdentificador();
        }
        final RolesDao rolesDao = new RolesDao();
        final RolesBean rolesBean = rolesDao.getRolByEmpleado(identificador, "Inventarios");

        if (rolesBean!=null){
            if (rolesBean.getActive() == true){
                Intent intent = new Intent(getApplicationContext(), MainAdminActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        }else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (

                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        &&   (checkSelfPermission(BLUETOOTH) == PackageManager.PERMISSION_GRANTED)
                        && (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        && (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        if (

                (shouldShowRequestPermissionRationale(CAMERA))
                        ||
                        (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))
                        ||
                        (shouldShowRequestPermissionRationale(BLUETOOTH))
                        ||
                        (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))
                        ||
                        (shouldShowRequestPermissionRationale(CALL_PHONE))) {

        } else {
            requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, BLUETOOTH, CALL_PHONE}, 100);
        }
        return false;
    }

    private void creaUsuario() {

        final EmpleadoDao empleadoDao = new EmpleadoDao();
        int cuantos = empleadoDao.getTotalEmpleados();

        if (cuantos == 0) {

            final EmpleadoBean empleado = new EmpleadoBean();
            final EmpleadoDao dao = new EmpleadoDao();

            empleado.setNombre("Osvaldo Cazares");
            empleado.setDireccion("Conocida");
            empleado.setEmail("dev@gmail.com");
            empleado.setTelefono("6672081920");
            empleado.setFecha_nacimiento("00/00/0000");
            empleado.setFecha_ingreso("00/00/0000");
            empleado.setFecha_egreso("00/00/0000");
            empleado.setContrasenia("123");
            empleado.setIdentificador("E001");
            empleado.setNss("");
            empleado.setRfc("");
            empleado.setCurp("");
            empleado.setPuesto("");
            empleado.setArea_depto("SYS");
            empleado.setTipo_contrato("INDETERMINADO");
            empleado.setRegion("UNO");
            empleado.setHora_entrada("10:00");
            empleado.setHora_salida("17:00");
            empleado.setSalida_comer("13:00");
            empleado.setEntrada_comer("13:30");
            empleado.setSueldo_diario(0);
            empleado.setTurno("");
            dao.insert(empleado);

            RolesBean rolCliente = new RolesBean();
            RolesDao rolClienteDao = new RolesDao();
            rolCliente.setEmpleado(empleado);
            rolCliente.setModulo("Clientes");
            rolCliente.setActive(true);
            rolCliente.setIdentificador(empleado.getIdentificador());
            rolClienteDao.insert(rolCliente);

            RolesBean rolProducto = new RolesBean();
            RolesDao rolProductoDao = new RolesDao();
            rolProducto.setEmpleado(empleado);
            rolProducto.setModulo("Productos");
            rolProducto.setActive(true);
            rolProducto.setIdentificador(empleado.getIdentificador());
            rolProductoDao.insert(rolProducto);

            RolesBean rolVentas = new RolesBean();
            RolesDao rolVentasDao = new RolesDao();
            rolVentas.setEmpleado(empleado);
            rolVentas.setModulo("Ventas");
            rolVentas.setActive(true);
            rolVentas.setIdentificador(empleado.getIdentificador());
            rolVentasDao.insert(rolVentas);

            RolesBean rolEmpleado = new RolesBean();
            RolesDao rolEmpleadoDao = new RolesDao();
            rolEmpleado.setEmpleado(empleado);
            rolEmpleado.setModulo("Empleados");
            rolEmpleado.setActive(true);
            rolEmpleado.setIdentificador(empleado.getIdentificador());
            rolEmpleadoDao.insert(rolEmpleado);

            RolesBean rolCobranza = new RolesBean();
            RolesDao rolCobranzaDao = new RolesDao();
            rolCobranza.setEmpleado(empleado);
            rolCobranza.setModulo("Cobranza");
            rolCobranza.setActive(true);
            rolCobranza.setIdentificador(empleado.getIdentificador());
            rolCobranzaDao.insert(rolCobranza);
        }
    }

    private void showProgress(){
        rlprogress_login.setVisibility(View.VISIBLE);
    }
    private void shideProgress(){
        rlprogress_login.setVisibility(View.GONE);

    }
    private class donwloadGetDataAsync extends AsyncTask<Call, Void, String>{
        Response<Data> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            shideProgress();
        }

        @Override
        protected String doInBackground(Call... calls) {

            Call<Data> call = calls[0];
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response == null) {
                Toast.makeText(LoginActivity.this, "Ha ocurrido un error al iniciar", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (response.isSuccessful()) {

                if (response.code() == 200){

                    //Contiene la lista de empledos
                    for (Empleado item : response.body().getData().getEmpleados()) {

                        //Instancia el DAO
                        final EmpleadoDao dao = new EmpleadoDao();

                        //Validamos si existe el empleado en la base de datos en base al identificador
                        final EmpleadoBean empleadoBean = dao.getEmpleadoByIdentificador(item.getIdentificador());

                        //NO existe entonces lo creamos
                        if (empleadoBean == null) {
                            EmpleadoBean empleado = new EmpleadoBean();
                            EmpleadoDao empleadoDao = new EmpleadoDao();
                            empleado.setNombre(item.getNombre());
                            empleado.setDireccion(item.getDireccion());
                            empleado.setEmail(item.getEmail());
                            empleado.setTelefono(item.getTelefono());
                            empleado.setFecha_nacimiento(item.getFechaNacimiento());
                            empleado.setFecha_ingreso(item.getFechaIngreso());
                            empleado.setFecha_egreso(item.getFechaEgreso());
                            empleado.setContrasenia(item.getContrasenia());
                            empleado.setIdentificador(item.getIdentificador());
                            empleado.setNss(item.getNss());
                            empleado.setRfc(item.getRfc());
                            empleado.setCurp(item.getCurp());
                            empleado.setPuesto(item.getPuesto());
                            empleado.setArea_depto(item.getAreaDepto());
                            empleado.setTipo_contrato(item.getTipoContrato());
                            empleado.setRegion(item.getRegion());
                            empleado.setHora_entrada(item.getHoraEntrada());
                            empleado.setHora_salida(item.getHoraSalida());
                            empleado.setSalida_comer(item.getSalidaComer());
                            empleado.setEntrada_comer(item.getEntradaComer());
                            empleado.setSueldo_diario(item.getSueldoDiario());
                            empleado.setTurno(item.getTurno());
                            empleado.setPath_image(item.getPathImage());
                            empleadoDao.insert(empleado);
                        }else {
                            empleadoBean.setNombre(item.getNombre());
                            empleadoBean.setDireccion(item.getDireccion());
                            empleadoBean.setEmail(item.getEmail());
                            empleadoBean.setTelefono(item.getTelefono());
                            empleadoBean.setFecha_nacimiento(item.getFechaNacimiento());
                            empleadoBean.setFecha_ingreso(item.getFechaIngreso());
                            empleadoBean.setFecha_egreso(item.getFechaEgreso());
                            empleadoBean.setContrasenia(item.getContrasenia());
                            empleadoBean.setIdentificador(item.getIdentificador());
                            empleadoBean.setNss(item.getNss());
                            empleadoBean.setRfc(item.getRfc());
                            empleadoBean.setCurp(item.getCurp());
                            empleadoBean.setPuesto(item.getPuesto());
                            empleadoBean.setArea_depto(item.getAreaDepto());
                            empleadoBean.setTipo_contrato(item.getTipoContrato());
                            empleadoBean.setRegion(item.getRegion());
                            empleadoBean.setHora_entrada(item.getHoraEntrada());
                            empleadoBean.setHora_salida(item.getHoraSalida());
                            empleadoBean.setSalida_comer(item.getSalidaComer());
                            empleadoBean.setEntrada_comer(item.getEntradaComer());
                            empleadoBean.setSueldo_diario(item.getSueldoDiario());
                            empleadoBean.setTurno(item.getTurno());
                            empleadoBean.setPath_image(item.getPathImage());
                            dao.save(empleadoBean);
                        }
                    }
                    //Contiene la lista de permidos
                    for (Role rol : response.body().getData().getRoles()){

                        final RolesDao rolesDao = new RolesDao();
                        final RolesBean rolesBean = rolesDao.getRolByModule(rol.getEmpleado(), rol.getModulo());

                        if (rolesBean == null){

                            final RolesBean bean = new RolesBean();
                            final RolesDao dao = new RolesDao();

                            final EmpleadoDao empleadoDao = new EmpleadoDao();
                            final EmpleadoBean empleadoBean = empleadoDao.getEmpleadoByIdentificador(rol.getEmpleado());

                            bean.setEmpleado(empleadoBean);
                            bean.setModulo(rol.getModulo());

                            if (rol.getActivo() == 1){
                                bean.setActive(true);
                            }else {
                                bean.setActive(false);
                            }
                            bean.setIdentificador(rol.getEmpleado());
                            dao.insert(bean);
                        }else {
                            final EmpleadoDao empleadoDao = new EmpleadoDao();
                            final EmpleadoBean empleadoBean = empleadoDao.getEmpleadoByIdentificador(rol.getEmpleado());

                            rolesBean.setEmpleado(empleadoBean);
                            rolesBean.setModulo(rol.getModulo());

                            if (rol.getActivo() == 1){
                                rolesBean.setActive(true);
                            }else {
                                rolesBean.setActive(false);
                            }
                            rolesBean.setIdentificador(rol.getEmpleado());
                            rolesDao.save(rolesBean);
                        }
                    }
                    //Contiene la lista de productos
                    for (Producto items : response.body().getData().getProductos()) {

                        final ProductoDao productoDao = new ProductoDao();
                        final ProductoBean productoBean = productoDao.getProductoByArticulo(items.getArticulo());

                        if (productoBean == null) {
                            //Creamos el producto
                            ProductoBean producto = new ProductoBean();
                            ProductoDao dao = new ProductoDao();
                            producto.setArticulo(items.getArticulo());
                            producto.setDescripcion(items.getDescripcion());
                            producto.setStatus(items.getStatus());
                            producto.setUnidad_medida(items.getUnidadMedida());
                            producto.setClave_sat(items.getClaveSat());
                            producto.setUnidad_sat(items.getUnidadSat());
                            producto.setPrecio(items.getPrecio());
                            producto.setCosto(items.getCosto());
                            producto.setIva(items.getIva());
                            producto.setIeps(items.getIeps());
                            producto.setPrioridad(items.getPrioridad());
                            producto.setRegion(items.getRegion());
                            producto.setCodigo_alfa(items.getCodigoAlfa());
                            producto.setCodigo_barras(items.getCodigoBarras());
                            producto.setPath_img(items.getPathImage());
                            dao.insert(producto);
                        }else {
                            productoBean.setArticulo(items.getArticulo());
                            productoBean.setDescripcion(items.getDescripcion());
                            productoBean.setStatus(items.getStatus());
                            productoBean.setUnidad_medida(items.getUnidadMedida());
                            productoBean.setClave_sat(items.getClaveSat());
                            productoBean.setUnidad_sat(items.getUnidadSat());
                            productoBean.setPrecio(items.getPrecio());
                            productoBean.setCosto(items.getCosto());
                            productoBean.setIva(items.getIva());
                            productoBean.setIeps(items.getIeps());
                            productoBean.setPrioridad(items.getPrioridad());
                            productoBean.setRegion(items.getRegion());
                            productoBean.setCodigo_alfa(items.getCodigoAlfa());
                            productoBean.setCodigo_barras(items.getCodigoBarras());
                            productoBean.setPath_img(items.getPathImage());
                            productoDao.save(productoBean);
                        }
                    }

                    for (Cliente item : response.body().getData().getClientes()) {

                        //Validamos si existe el cliente
                        final ClienteDao dao = new ClienteDao();
                        final ClienteBean bean = dao.getClienteByCuenta(item.getCuenta());

                        if (bean == null) {

                            final ClienteBean clienteBean = new ClienteBean();
                            final ClienteDao clienteDao = new ClienteDao();
                            clienteBean.setNombre_comercial(item.getNombreComercial());
                            clienteBean.setCalle(item.getCalle());
                            clienteBean.setNumero(item.getNumero());
                            clienteBean.setColonia(item.getColonia());
                            clienteBean.setCiudad(item.getCiudad());
                            clienteBean.setCodigo_postal(item.getCodigoPostal());
                            clienteBean.setFecha_registro(item.getFechaRegistro());
                            clienteBean.setFecha_baja(item.getFechaBaja());
                            clienteBean.setCuenta(item.getCuenta());
                            clienteBean.setGrupo(item.getGrupo());
                            clienteBean.setCategoria(item.getCategoria());
                            if (item.getStatus() == 1) {
                                clienteBean.setStatus(true);
                            } else {
                                clienteBean.setStatus(false);
                            }
                            clienteBean.setConsec(item.getConsec());
                            clienteBean.setVisitado(0);
                            clienteBean.setRegion(item.getRegion());
                            clienteBean.setSector(item.getSector());
                            clienteBean.setRango(item.getRango());
                            clienteBean.setSecuencia(item.getSecuencia());
                            clienteBean.setPeriodo(item.getPeriodo());
                            clienteBean.setRuta(item.getRuta());
                            clienteBean.setLun(item.getLun());
                            clienteBean.setMar(item.getMar());
                            clienteBean.setMie(item.getMie());
                            clienteBean.setJue(item.getJue());
                            clienteBean.setVie(item.getVie());
                            clienteBean.setSab(item.getSab());
                            clienteBean.setDom(item.getDom());
                            clienteBean.setLatitud(item.getLatitud());
                            clienteBean.setLongitud(item.getLongitud());
                            clienteBean.setContacto_phone(item.getPhone_contacto());
                            clienteBean.setRecordatorio(item.getRecordatorio());
                            clienteBean.setVisitasNoefectivas(item.getVisitas());

                            if (item.getIsCredito() == 1) {
                                clienteBean.setIs_credito(true);
                            } else {
                                clienteBean.setIs_credito(false);
                            }

                            clienteBean.setLimite_credito(item.getLimite_credito());
                            clienteBean.setSaldo_credito(item.getSaldo_credito());
                            clienteBean.setMatriz(item.getMatriz());
                            clienteDao.insert(clienteBean);
                        } else {
                            bean.setNombre_comercial(item.getNombreComercial());
                            bean.setCalle(item.getCalle());
                            bean.setNumero(item.getNumero());
                            bean.setColonia(item.getColonia());
                            bean.setCiudad(item.getCiudad());
                            bean.setCodigo_postal(item.getCodigoPostal());
                            bean.setFecha_registro(item.getFechaRegistro());
                            bean.setFecha_baja(item.getFechaBaja());
                            bean.setCuenta(item.getCuenta());
                            bean.setGrupo(item.getGrupo());
                            bean.setCategoria(item.getCategoria());
                            if (item.getStatus() == 1) {
                                bean.setStatus(true);
                            } else {
                                bean.setStatus(false);
                            }
                            bean.setConsec(item.getConsec());
                            if (bean.getVisitado() == 0) {
                                bean.setVisitado(0);
                            } else if (bean.getVisitado() == 1) {
                                bean.setVisitado(1);
                            }
                            bean.setRegion(item.getRegion());
                            bean.setSector(item.getSector());
                            bean.setRango(item.getRango());
                            bean.setSecuencia(item.getSecuencia());
                            bean.setPeriodo(item.getPeriodo());
                            bean.setRuta(item.getRuta());
                            bean.setLun(item.getLun());
                            bean.setMar(item.getMar());
                            bean.setMie(item.getMie());
                            bean.setJue(item.getJue());
                            bean.setVie(item.getVie());
                            bean.setSab(item.getSab());
                            bean.setDom(item.getDom());
                            bean.setLatitud(item.getLatitud());
                            bean.setLongitud(item.getLongitud());
                            bean.setContacto_phone(item.getPhone_contacto());
                            bean.setRecordatorio(item.getRecordatorio());
                            bean.setVisitasNoefectivas(item.getVisitas());
                            if (item.getIsCredito() == 1) {
                                bean.setIs_credito(true);
                            } else {
                                bean.setIs_credito(false);
                            }
                            bean.setLimite_credito(item.getLimite_credito());
                            bean.setSaldo_credito(item.getSaldo_credito());
                            bean.setMatriz(item.getMatriz());
                            dao.save(bean);
                        }
                    }


                    for (Cobranza item : response.body().getData().getCobranzas()) {
                        CobranzaDao cobranzaDao = new CobranzaDao();
                        CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(item.getCobranza());
                        if (cobranzaBean == null) {
                            final CobranzaBean cobranzaBean1 = new CobranzaBean();
                            final CobranzaDao cobranzaDao1 = new CobranzaDao();
                            cobranzaBean1.setCobranza(item.getCobranza());
                            cobranzaBean1.setCliente(item.getCuenta());
                            cobranzaBean1.setImporte(item.getImporte());
                            cobranzaBean1.setSaldo(item.getSaldo());
                            cobranzaBean1.setVenta(item.getVenta());
                            cobranzaBean1.setEstado(item.getEstado());
                            cobranzaBean1.setObservaciones(item.getObservaciones());
                            cobranzaBean1.setFecha(item.getFecha());
                            cobranzaBean1.setHora(item.getHora());
                            cobranzaBean1.setEmpleado(item.getIdentificador());
                            cobranzaBean1.setIsCheck(false);
                            cobranzaDao1.insert(cobranzaBean1);
                        } else {
                            cobranzaBean.setCobranza(item.getCobranza());
                            cobranzaBean.setCliente(item.getCuenta());
                            cobranzaBean.setImporte(item.getImporte());
                            cobranzaBean.setSaldo(item.getSaldo());
                            cobranzaBean.setVenta(item.getVenta());
                            cobranzaBean.setEstado(item.getEstado());
                            cobranzaBean.setObservaciones(item.getObservaciones());
                            cobranzaBean.setFecha(item.getFecha());
                            cobranzaBean.setHora(item.getHora());
                            cobranzaBean.setEmpleado(item.getIdentificador());
                            cobranzaBean.setIsCheck(false);
                            cobranzaDao.save(cobranzaBean);
                        }
                    }

                    for (Precio item : response.body().getData().getPrecios()) {

                        //Para obtener los datos del cliente
                        final ClienteDao clienteDao = new ClienteDao();
                        final ClienteBean clienteBean = clienteDao.getClienteByCuenta(item.getCliente());
                        if (clienteBean == null) {
                            return null;
                        }

                        //Para obtener los datos del producto
                        final ProductoDao productoDao = new ProductoDao();
                        final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo());

                        if (productoBean == null) {
                            return null;
                        }

                        final PreciosEspecialesDao preciosEspecialesDao = new PreciosEspecialesDao();
                        final PreciosEspecialesBean preciosEspecialesBean = preciosEspecialesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                        //Si no hay precios especiales entonces crea un precio
                        if (preciosEspecialesBean == null) {

                            final PreciosEspecialesDao dao = new PreciosEspecialesDao();
                            final PreciosEspecialesBean bean = new PreciosEspecialesBean();
                            bean.setCliente(clienteBean.getCuenta());
                            bean.setArticulo(productoBean.getArticulo());
                            bean.setPrecio(item.getPrecio());
                            if (item.getActive() == 1){
                                bean.setActive(true);
                            }else {
                                bean.setActive(false);
                            }

                            dao.insert(bean);

                        } else {
                            preciosEspecialesBean.setCliente(clienteBean.getCuenta());
                            preciosEspecialesBean.setArticulo(productoBean.getArticulo());
                            preciosEspecialesBean.setPrecio(item.getPrecio());
                            if (item.getActive() == 1){
                                preciosEspecialesBean.setActive(true);
                            }else {
                                preciosEspecialesBean.setActive(false);
                            }
                            preciosEspecialesDao.save(preciosEspecialesBean);
                        }
                    }
                }

            }

            return response.body().toString();
        }
    }



}