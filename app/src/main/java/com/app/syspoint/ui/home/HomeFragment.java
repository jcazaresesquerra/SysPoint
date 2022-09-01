package com.app.syspoint.ui.home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.app.syspoint.models.json.ClientJson;
import com.app.syspoint.models.json.EmployeeJson;
import com.app.syspoint.models.json.PaymentJson;
import com.app.syspoint.models.json.ProductJson;
import com.app.syspoint.models.json.RolJson;
import com.app.syspoint.models.json.SpecialPriceJson;
import com.app.syspoint.models.json.VisitJson;
import com.app.syspoint.utils.cache.CacheInteractor;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.AppBundle;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.PreciosEspecialesBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.bean.VisitasBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.EmployeeDao;
import com.app.syspoint.repository.database.dao.SpecialPricesDao;
import com.app.syspoint.repository.database.dao.ProductDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.repository.database.dao.VisitsDao;
import com.app.syspoint.repository.request.http.ApiServices;
import com.app.syspoint.models.Data;
import com.app.syspoint.repository.request.http.PointApi;
import com.app.syspoint.repository.request.http.Servicio;
import com.app.syspoint.repository.request.http.SincVentas;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Payment;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Price;
import com.app.syspoint.models.Product;
import com.app.syspoint.models.Role;
import com.app.syspoint.models.Visit;
import com.app.syspoint.ui.customs.DialogoRuteo;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static String TAG = "HomeFragment";
    AdapterRutaClientes mAdapter;
    List<ClientesRutaBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_clientes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        if (Constants.solictaRuta) {
            creaRutaSeleccionada();
        }

        lyt_clientes = root.findViewById(R.id.lyt_clientes);
        rlprogress = root.findViewById(R.id.rlprogress_cliente);

        initRecyclerView(root);
        updateCreditos();
        return root;
    }

    private void updateCreditos() {

        final ClientDao clientDao = new ClientDao();
        final List<ClienteBean> listaClientesCredito = clientDao.getClientsByDay(Utils.fechaActual());
        final PaymentDao paymentDao = new PaymentDao();
        for (ClienteBean item : listaClientesCredito) {

            try {
                final ClientDao dao = new ClientDao();
                item.setSaldo_credito(paymentDao.getTotalSaldoDocumentosCliente(item.getCuenta()));
                dao.save(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere un momento");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            progressDialog.dismiss();
            if (connected) {
                Call<Data> getData = ApiServices.getClientRestrofit().create(PointApi.class).getAllDataByDate();
                new donwloadGetDataAsync().execute(getData);
                new SendVentas().execute();
                new loadCobranza().execute();
                new loadAbonos().execute();
                new loadVisitas().execute();
                new loadClientes().execute();
                new loadPreciosEspeciales().execute();
            }
        }).execute(), 100);
    }

    private class donwloadGetDataAsync extends AsyncTask<Call, Void, String>{
        Response<Data> response;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Obteniendo actualizaciones...");

            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(Call... calls) {

            Call<Data> call = calls[0];
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()){

                if (response.code() == 200){

                    //Contiene la lista de empledos
                    for (Employee item : response.body().getData().getEmpleados()) {

                        //Instancia el DAO
                        final EmployeeDao dao = new EmployeeDao();

                        //Validamos si existe el empleado en la base de datos en base al identificador
                        final EmpleadoBean empleadoBean = dao.getEmployeeByIdentifier(item.getIdentificador());

                        //NO existe entonces lo creamos
                        if (empleadoBean == null) {
                            EmpleadoBean empleado = new EmpleadoBean();
                            EmployeeDao employeeDao = new EmployeeDao();
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
                            employeeDao.insert(empleado);
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

                            final EmployeeDao employeeDao = new EmployeeDao();
                            final EmpleadoBean empleadoBean = employeeDao.getEmployeeByIdentifier(rol.getEmpleado());

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
                            final EmployeeDao employeeDao = new EmployeeDao();
                            final EmpleadoBean empleadoBean = employeeDao.getEmployeeByIdentifier(rol.getEmpleado());

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
                    for (Product items : response.body().getData().getProductos()) {

                        final ProductDao productDao = new ProductDao();
                        final ProductoBean productoBean = productDao.getProductoByArticulo(items.getArticulo());

                        if (productoBean == null) {
                            //Creamos el producto
                            ProductoBean producto = new ProductoBean();
                            ProductDao dao = new ProductDao();
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
                            productDao.save(productoBean);
                        }
                    }

                    for (Client item : response.body().getData().getClientes()) {

                        //Validamos si existe el cliente
                        final ClientDao dao = new ClientDao();
                        final ClienteBean bean = dao.getClientByAccount(item.getCuenta());

                        if (bean == null) {

                            final ClienteBean clienteBean = new ClienteBean();
                            final ClientDao clientDao = new ClientDao();
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

                            if (item.isCredito() == 1) {
                                clienteBean.setIs_credito(true);
                            } else {
                                clienteBean.setIs_credito(false);
                            }

                            clienteBean.setLimite_credito(item.getLimite_credito());
                            clienteBean.setSaldo_credito(item.getSaldo_credito());
                            clienteBean.setMatriz(item.getMatriz());
                            clientDao.insert(clienteBean);
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
                            if (item.isCredito() == 1) {
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


                    for (Payment item : response.body().getData().getCobranzas()) {
                        PaymentDao paymentDao = new PaymentDao();
                        CobranzaBean cobranzaBean = paymentDao.getByCobranza(item.getCobranza());
                        if (cobranzaBean == null) {
                            final CobranzaBean cobranzaBean1 = new CobranzaBean();
                            final PaymentDao paymentDao1 = new PaymentDao();
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
                            paymentDao1.insert(cobranzaBean1);
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
                            paymentDao.save(cobranzaBean);
                        }
                    }

                    for (Price item : response.body().getData().getPrecios()) {

                        //Para obtener los datos del cliente
                        final ClientDao clientDao = new ClientDao();
                        final ClienteBean clienteBean = clientDao.getClientByAccount(item.getCliente());
                        if (clienteBean == null) {
                            return null;
                        }

                        //Para obtener los datos del producto
                        final ProductDao productDao = new ProductDao();
                        final ProductoBean productoBean = productDao.getProductoByArticulo(item.getArticulo());

                        if (productoBean == null) {
                            return null;
                        }

                        final SpecialPricesDao specialPricesDao = new SpecialPricesDao();
                        final PreciosEspecialesBean preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                        //Si no hay precios especiales entonces crea un precio
                        if (preciosEspecialesBean == null) {

                            final SpecialPricesDao dao = new SpecialPricesDao();
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
                            specialPricesDao.save(preciosEspecialesBean);
                        }
                    }
                }

            }

            return response.body() != null? response.body().toString(): null;
        }
    }


    private class loadAbonos extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            final PaymentDao paymentDao = new PaymentDao();
            List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
            cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual());

            List<Payment> listaCobranza = new ArrayList<>();
            for (CobranzaBean item : cobranzaBeanList) {
                Payment cobranza = new Payment();
                cobranza.setCobranza(item.getCobranza());
                cobranza.setCuenta(item.getCliente());
                cobranza.setImporte(item.getImporte());
                cobranza.setSaldo(item.getSaldo());
                cobranza.setVenta(item.getVenta());
                cobranza.setEstado(item.getEstado());
                cobranza.setObservaciones(item.getObservaciones());
                cobranza.setFecha(item.getFecha());
                cobranza.setHora(item.getHora());
                cobranza.setIdentificador(item.getEmpleado());
                listaCobranza.add(cobranza);
            }

            PaymentJson cobranzaJson = new PaymentJson();
            cobranzaJson.setPayments(listaCobranza);
            String json = new Gson().toJson(cobranzaJson);
            Log.d("Sin Cobranza", json);

            Call<PaymentJson> loadCobranza = ApiServices.getClientRestrofit().create(PointApi.class).updateCobranza(cobranzaJson);

            loadCobranza.enqueue(new Callback<PaymentJson>() {
                @Override
                public void onResponse(Call<PaymentJson> call, Response<PaymentJson> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<PaymentJson> call, Throwable t) {

                }
            });
            return null;
        }
    }


    private void creaRutaSeleccionada() {
        RoutingDao dao = new RoutingDao();
        RuteoBean bean = dao.getRutaEstablecidaFechaActual(Utils.fechaActual());

        if (bean != null) {

            final PrettyDialog dialog = new PrettyDialog(getContext());
            dialog.setTitle("Establecer")
                    .setTitleColor(R.color.red_500)
                    .setMessage("¡Ya existe una configuración inicial!" + "" + "\n ¿Desea actualizar la ruta?")
                    .setMessageColor(R.color.red_500)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.red_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            showDialog();
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
            showDialog();
        }
    }

    private void showDialog() {
        DialogoRuteo dialogoRuteo = new DialogoRuteo(getActivity(), new DialogoRuteo.DialogListener() {
            @Override
            public void ready(String dia, String ruta) {

                //Preguntamos si queremos agregar un nuevo ruteo
                final PrettyDialog dialog = new PrettyDialog(getContext());
                dialog.setTitle("Establecer")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea establecer la ruta inicial")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                //Clientes normales
                                ClientDao clientDao = new ClientDao();
                                clientDao.updateVisited();

                                RuteClientDao ruteClientDao = new RuteClientDao();
                                ruteClientDao.clear();

                                RoutingDao routingDao = new RoutingDao();
                                routingDao.clear();

                                RuteoBean ruteoBean = new RuteoBean();

                                if (dia.compareToIgnoreCase("Lunes") == 0) {
                                    ruteoBean.setDia(1);
                                } else if (dia.compareToIgnoreCase("Martes") == 0) {
                                    ruteoBean.setDia(2);
                                } else if (dia.compareToIgnoreCase("Miercoles") == 0) {
                                    ruteoBean.setDia(3);
                                } else if (dia.compareToIgnoreCase("Jueves") == 0) {
                                    ruteoBean.setDia(4);
                                } else if (dia.compareToIgnoreCase("Viernes") == 0) {
                                    ruteoBean.setDia(5);
                                } else if (dia.compareToIgnoreCase("Sabado") == 0) {
                                    ruteoBean.setDia(6);
                                } else if (dia.compareToIgnoreCase("Domingo") == 0) {
                                    ruteoBean.setDia(7);
                                }
                                ruteoBean.setId(Long.valueOf(1));
                                ruteoBean.setFecha(Utils.fechaActual());
                                ruteoBean.setRuta(ruta);

                                routingDao.insert(ruteoBean);
                                Toast.makeText(getActivity(), "La ruta se cargo con exito!", Toast.LENGTH_LONG).show();
                                estableceRuta();
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

            @Override
            public void cancelled() {

            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogoRuteo.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogoRuteo.show();
        dialogoRuteo.getWindow().setAttributes(lp);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRuta();
        showHideImage();
    }

    private void showHideImage() {
        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }
    }

    private void setDataList(List<ClientesRutaBean> list) {
        mData = list;
        mAdapter.setListaRuta(mData);
        showHideImage();
    }

    private void saveData(List<ClienteBean> listaClientes) {


        if (mData.isEmpty()) {

            int count = 0;
            for (ClienteBean item : listaClientes) {

                count += 1;
                final RuteClientDao ruteClientDao = new RuteClientDao();
                final ClientesRutaBean clientesRutaBean = ruteClientDao.getClienteByCuentaCliente(item.getCuenta());
                //Guardamos al clientes en la ruta actual
                if (clientesRutaBean == null) {
                    ClientesRutaBean bean = new ClientesRutaBean();
                    RuteClientDao dao = new RuteClientDao();
                    bean.setId(Long.valueOf(count));
                    bean.setNombre_comercial(item.getNombre_comercial());
                    bean.setCalle(item.getCalle());
                    bean.setNumero(item.getNumero());
                    bean.setColonia(item.getColonia());
                    bean.setCuenta(item.getCuenta());
                    bean.setRango(item.getRango());
                    bean.setLun(item.getLun());
                    bean.setMar(item.getMar());
                    bean.setMie(item.getMie());
                    bean.setJue(item.getJue());
                    bean.setVie(item.getVie());
                    bean.setSab(item.getSab());
                    bean.setDom(item.getDom());
                    bean.setVisitado(0);
                    bean.setLatitud(item.getLatitud());
                    bean.setLongitud(item.getLongitud());
                    bean.setPhone_contact(item.getContacto_phone());
                    dao.insert(bean);
                }
            }
            loadRuta();
        }
    }

    private void loadRuta() {
        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            mData = (List<ClientesRutaBean>) (List<?>) new RuteClientDao().getAllRutaClientes();
        }
        setDataList(mData);
    }

    private void estableceRuta() {

        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {

            if (ruteoBean.getDia() == 1) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getClientsByMondayRute(ruteoBean.getRuta(), 1));
            } else if (ruteoBean.getDia() == 2) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaMartes(ruteoBean.getRuta(), 1));
            }
            if (ruteoBean.getDia() == 3) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaMiercoles(ruteoBean.getRuta(), 1));
            }
            if (ruteoBean.getDia() == 4) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaJueves(ruteoBean.getRuta(), 1));
            }
            if (ruteoBean.getDia() == 5) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaViernes(ruteoBean.getRuta(), 1));
            }
            if (ruteoBean.getDia() == 6) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaSabado(ruteoBean.getRuta(), 1));
            }
            if (ruteoBean.getDia() == 7) {
                saveData((List<ClienteBean>) (List<?>) new ClientDao().getListaClientesRutaDomingo(ruteoBean.getRuta(), 1));
            }

            mData = (List<ClientesRutaBean>) (List<?>) new RuteClientDao().getAllRutaClientes();
        }

        loadRuta();
    }

    private void initRecyclerView(View root) {

        mData = new ArrayList<>();

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterRutaClientes(mData, new AdapterRutaClientes.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ClientesRutaBean clienteBean = mData.get(position);
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
            }
        }, new AdapterRutaClientes.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {
                // ClientesRutaBean clienteBean = mData.get(position);
                // HashMap<String, String> parametros = new HashMap<>();
                // parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                // parametros.put(Actividades.PARAM_2, clienteBean.getCalle());
                // parametros.put(Actividades.PARAM_3, clienteBean.getNumero());
                // parametros.put(Actividades.PARAM_4, clienteBean.getColonia());
                // parametros.put(Actividades.PARAM_5, clienteBean.getNombre_comercial());
                // parametros.put(Actividades.PARAM_6, clienteBean.getLatitud());
                // parametros.put(Actividades.PARAM_7, clienteBean.getLongitud());
                // Actividades.getSingleton(getActivity(), PreCapturaActivity.class).muestraActividad(parametros);

                return false;
            }
        });

        recyclerView.setAdapter(mAdapter);

        loadRuta();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sinronizaAll:

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (!connected) {
                        showDialogNotConnectionInternet();
                    } else {
                        getData();
                    }
                }).execute(), 100);

                return true;

            case R.id.close_caja:
                closeBox();
                return true;

            case R.id.viewMap:
                Actividades.getSingleton(getActivity(), MapsRuteoActivity.class).muestraActividad();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void closeBox() {
        final PrettyDialog dialog = new PrettyDialog(getContext());
        dialog.setTitle("Corte del día")
                .setTitleColor(R.color.purple_500)
                .setMessage("Desea realizar el corte del día")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                })
                .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {

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

    private void showDialogNotConnectionInternet() {

        final Dialog dialog = new Dialog(getActivity());
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
                getData();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getData() {

        progressshow();

        Call<PaymentJson> getCobranza = ApiServices.getClientRestrofit().create(PointApi.class).getCobranza();
        getCobranza.enqueue(new Callback<PaymentJson>() {
            @Override
            public void onResponse(Call<PaymentJson> call, Response<PaymentJson> response) {
                if (response.isSuccessful()) {
                    progresshide();
                    PaymentDao paymentDao = new PaymentDao();
                    for (Payment item : response.body().getPayments()) {

                        CobranzaBean cobranzaBean = paymentDao.getByCobranza(item.getCobranza());
                        if (cobranzaBean == null) {

                            final CobranzaBean cobranzaBean1 = new CobranzaBean();
                            final PaymentDao paymentDao1 = new PaymentDao();
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
                            paymentDao1.insert(cobranzaBean1);
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
                            paymentDao.save(cobranzaBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentJson> call, Throwable t) {
                progresshide();
            }
        });

        Call<EmployeeJson> getEmpleado = ApiServices.getClientRestrofit().create(PointApi.class).getAllEmpleados();

        getEmpleado.enqueue(new Callback<EmployeeJson>() {
            @Override
            public void onResponse(Call<EmployeeJson> call, Response<EmployeeJson> response) {
                if (response.isSuccessful()) {

                    progresshide();

                    for (Employee item : response.body().getEmployees()) {

                        //Instancia el DAO
                        final EmployeeDao dao = new EmployeeDao();

                        //Validamos si existe el empleado en la base de datos en base al identificador
                        final EmpleadoBean empleadoBean = dao.getEmployeeByIdentifier(item.getIdentificador());

                        //NO existe entonces lo creamos
                        if (empleadoBean == null) {
                            EmpleadoBean empleado = new EmpleadoBean();
                            EmployeeDao employeeDao = new EmployeeDao();
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
                            employeeDao.insert(empleado);
                        } else {
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
                }
            }

            @Override
            public void onFailure(Call<EmployeeJson> call, Throwable t) {
                progresshide();
            }
        });


        progressshow();
        Call<ClientJson> getClientes = ApiServices.getClientRestrofit().create(PointApi.class).getAllClientes();
        getClientes.enqueue(new Callback<ClientJson>() {
            @Override
            public void onResponse(Call<ClientJson> call, Response<ClientJson> response) {

                if (response.isSuccessful()) {
                    progresshide();

                    for (Client item : response.body().getClients()) {

                        //Validamos si existe el cliente
                        final ClientDao dao = new ClientDao();
                        final ClienteBean bean = dao.getClientByAccount(item.getCuenta());

                        if (bean == null) {

                            final ClienteBean clienteBean = new ClienteBean();
                            final ClientDao clientDao = new ClientDao();
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
                            clienteBean.setIs_recordatorio(false);
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
                            clienteBean.setVisitasNoefectivas(0);
                            if (item.isCredito() == 1) {
                                clienteBean.setIs_credito(true);
                            } else {
                                clienteBean.setIs_credito(false);
                            }
                            clienteBean.setLimite_credito(item.getLimite_credito());
                            clienteBean.setSaldo_credito(item.getSaldo_credito());
                            clienteBean.setMatriz(item.getMatriz());
                            clientDao.insert(clienteBean);
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
                            if (item.isCredito() == 1) {
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
                }
            }

            @Override
            public void onFailure(Call<ClientJson> call, Throwable t) {
                progresshide();
            }
        });


        progressshow();
        Call<ProductJson> getProducto = ApiServices.getClientRestrofit().create(PointApi.class).getAllProductos();
        getProducto.enqueue(new Callback<ProductJson>() {
            @Override
            public void onResponse(Call<ProductJson> call, Response<ProductJson> response) {

                if (response.isSuccessful()) {
                    progresshide();

                    for (Product items : response.body().getProducts()) {

                        final ProductDao productDao = new ProductDao();
                        final ProductoBean productoBean = productDao.getProductoByArticulo(items.getArticulo());

                        if (productoBean == null) {
                            //Creamos el producto
                            ProductoBean producto = new ProductoBean();
                            ProductDao dao = new ProductDao();
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
                        } else {
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
                            productDao.save(productoBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductJson> call, Throwable t) {
                progresshide();
            }
        });

        progressshow();
        Call<RolJson> getRols = ApiServices.getClientRestrofit().create(PointApi.class).getAllRols();
        getRols.enqueue(new Callback<RolJson>() {
            @Override
            public void onResponse(Call<RolJson> call, Response<RolJson> response) {
                progresshide();
                if (response.isSuccessful()) {

                    for (Role rol : response.body().getRoles()) {

                        final RolesDao rolesDao = new RolesDao();
                        final RolesBean rolesBean = rolesDao.getRolByModule(rol.getEmpleado(), rol.getModulo());

                        if (rolesBean == null) {

                            final RolesBean bean = new RolesBean();
                            final RolesDao dao = new RolesDao();

                            final EmployeeDao employeeDao = new EmployeeDao();
                            final EmpleadoBean empleadoBean = employeeDao.getEmployeeByIdentifier(rol.getEmpleado());

                            bean.setEmpleado(empleadoBean);
                            bean.setModulo(rol.getModulo());

                            if (rol.getActivo() == 1) {
                                bean.setActive(true);
                            } else {
                                bean.setActive(false);
                            }
                            bean.setIdentificador(rol.getEmpleado());
                            dao.insert(bean);
                        } else {
                            final EmployeeDao employeeDao = new EmployeeDao();
                            final EmpleadoBean empleadoBean = employeeDao.getEmployeeByIdentifier(rol.getEmpleado());

                            rolesBean.setEmpleado(empleadoBean);
                            rolesBean.setModulo(rol.getModulo());

                            if (rol.getActivo() == 1) {
                                rolesBean.setActive(true);
                            } else {
                                rolesBean.setActive(false);
                            }
                            rolesBean.setIdentificador(rol.getEmpleado());
                            rolesDao.save(rolesBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RolJson> call, Throwable t) {
                progresshide();
            }
        });


        progressshow();
        Call<SpecialPriceJson> preciosJson = ApiServices.getClientRestrofit().create(PointApi.class).getPricesEspecial();
        preciosJson.enqueue(new Callback<SpecialPriceJson>() {
            @Override
            public void onResponse(Call<SpecialPriceJson> call, Response<SpecialPriceJson> response) {
                progresshide();
                if (response.isSuccessful()) {

                    for (Price item : response.body().getPrices()) {

                        //Para obtener los datos del cliente
                        final ClientDao clientDao = new ClientDao();
                        final ClienteBean clienteBean = clientDao.getClientByAccount(item.getCliente());
                        if (clienteBean == null) {
                            return;
                        }

                        //Para obtener los datos del producto
                        final ProductDao productDao = new ProductDao();
                        final ProductoBean productoBean = productDao.getProductoByArticulo(item.getArticulo());

                        if (productoBean == null) {
                            return;
                        }

                        final SpecialPricesDao specialPricesDao = new SpecialPricesDao();
                        final PreciosEspecialesBean preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                        //Si no hay precios especiales entonces crea un precio
                        if (preciosEspecialesBean == null) {

                            final SpecialPricesDao dao = new SpecialPricesDao();
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
                            specialPricesDao.save(preciosEspecialesBean);
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<SpecialPriceJson> call, Throwable t) {
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

    public class SendVentas extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            try {
                final SincVentas sincVentas = new SincVentas(getActivity());

                sincVentas.setOnSuccess(new Servicio.ResponseOnSuccess() {
                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onSuccessObject(JSONObject response) throws Exception {

                    }
                });

                sincVentas.setOnError(new Servicio.ResponseOnError() {
                    @Override
                    public void onError(ANError error) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });

                sincVentas.postObject();

            } catch (Exception e) {

            }
            return null;
        }
    }

    public class loadVisitas extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            final VisitsDao visitsDao = new VisitsDao();
            List<VisitasBean> visitasBeanListBean = new ArrayList<>();
            visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual());
            final ClientDao clientDao = new ClientDao();
            EmpleadoBean vendedoresBean = AppBundle.getUserBean();

            if (vendedoresBean == null && getContext() != null) {
                vendedoresBean = new CacheInteractor().getSeller();
            }

            List<Visit> listaVisitas = new ArrayList<>();
            for (VisitasBean item : visitasBeanListBean) {
                Visit visita = new Visit();
                visita.setFecha(item.getFecha());
                visita.setHora(item.getHora());
                final ClienteBean clienteBean = clientDao.getClientByAccount(item.getCliente().getCuenta());
                visita.setCuenta(clienteBean.getCuenta());
                visita.setLatidud(item.getLatidud());
                visita.setLongitud(item.getLongitud());
                visita.setMotivo_visita(item.getMotivo_visita());
                if (vendedoresBean != null) {
                    visita.setIdentificador(vendedoresBean.getIdentificador());
                } else {
                    Log.e(TAG, "vendedoresBean is null");
                }

                listaVisitas.add(visita);
            }

            VisitJson visitaJsonRF = new VisitJson();
            visitaJsonRF.setVisits(listaVisitas);
            String json = new Gson().toJson(visitaJsonRF);
            Log.d("SinEmpleados", json);

            Call<VisitJson> loadVisitas = ApiServices.getClientRestrofit().create(PointApi.class).sendVisita(visitaJsonRF);

            loadVisitas.enqueue(new Callback<VisitJson>() {
                @Override
                public void onResponse(Call<VisitJson> call, Response<VisitJson> response) {
                    if (response.isSuccessful()) {

                    }
                }

                @Override
                public void onFailure(Call<VisitJson> call, Throwable t) {

                }
            });
            return null;
        }
    }

    public class loadCobranza extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            final PaymentDao paymentDao = new PaymentDao();
            List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
            cobranzaBeanList = paymentDao.getCobranzaFechaActual(Utils.fechaActual());

            List<Payment> listaCobranza = new ArrayList<>();
            for (CobranzaBean item : cobranzaBeanList) {
                Payment cobranza = new Payment();
                cobranza.setCobranza(item.getCobranza());
                cobranza.setCuenta(item.getCliente());
                cobranza.setImporte(item.getImporte());
                cobranza.setSaldo(item.getSaldo());
                cobranza.setVenta(item.getVenta());
                cobranza.setEstado(item.getEstado());
                cobranza.setObservaciones(item.getObservaciones());
                cobranza.setFecha(item.getFecha());
                cobranza.setHora(item.getHora());
                cobranza.setIdentificador(item.getEmpleado());
                listaCobranza.add(cobranza);
            }

            PaymentJson cobranzaJson = new PaymentJson();
            cobranzaJson.setPayments(listaCobranza);
            String json = new Gson().toJson(cobranzaJson);
            Log.d("Sin Cobranza", json);

            Call<PaymentJson> loadCobranza = ApiServices.getClientRestrofit().create(PointApi.class).sendCobranza(cobranzaJson);

            loadCobranza.enqueue(new Callback<PaymentJson>() {
                @Override
                public void onResponse(Call<PaymentJson> call, Response<PaymentJson> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<PaymentJson> call, Throwable t) {

                }
            });
            return null;
        }
    }


    public class loadPreciosEspeciales extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            //Instancia la base de datos
            final SpecialPricesDao dao = new SpecialPricesDao();

            //Contiene la lista de precios de la db local
            List<PreciosEspecialesBean> listaDB = new ArrayList<>();

            //Obtenemos la lista por id cliente
            listaDB = dao.getPreciosBydate(Utils.fechaActual());


            //Contiene la lista de lo que se envia al servidor
            final List<Price> listaPreciosServidor = new ArrayList<>();

            //Contien la lista de precios especiales locales
            for (PreciosEspecialesBean items : listaDB) {

                final Price precio = new Price();
                if (items.getActive() == true) {
                    precio.setActive(1);
                } else {
                    precio.setActive(0);
                }

                precio.setArticulo(items.getArticulo());
                precio.setCliente(items.getCliente());
                precio.setPrecio(items.getPrecio());
                listaPreciosServidor.add(precio);

            }

            final SpecialPriceJson precioEspecialJson = new SpecialPriceJson();
            precioEspecialJson.setPrices(listaPreciosServidor);

            String json = new Gson().toJson(precioEspecialJson);
            Log.d("Sinc especiales", json);

            Call<SpecialPriceJson> sendPreciosServer = ApiServices.getClientRestrofit().create(PointApi.class).sendPrecios(precioEspecialJson);
            sendPreciosServer.enqueue(new Callback<SpecialPriceJson>() {
                @Override
                public void onResponse(Call<SpecialPriceJson> call, Response<SpecialPriceJson> response) {

                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<SpecialPriceJson> call, Throwable t) {
                }
            });

            return null;
        }
    }

    public class loadClientes extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            final ClientDao clientDao = new ClientDao();
            List<ClienteBean> listaClientesDB = new ArrayList<>();
            listaClientesDB = clientDao.getClientsByDay(Utils.fechaActual());

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
                cliente.setLatitud(item.getLatitud());
                cliente.setLongitud(item.getLongitud());
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

            ClientJson clienteRF = new ClientJson();
            clienteRF.setClients(listaClientes);
            String json = new Gson().toJson(clienteRF);
            Log.d("Sinc Cientes", json);

            Call<ClientJson> loadClientes = ApiServices.getClientRestrofit().create(PointApi.class).sendCliente(clienteRF);

            loadClientes.enqueue(new Callback<ClientJson>() {
                @Override
                public void onResponse(Call<ClientJson> call, Response<ClientJson> response) {
                    if (response.isSuccessful()) {
                    }
                }

                @Override
                public void onFailure(Call<ClientJson> call, Throwable t) {
                }
            });
            return null;
        }
    }

}