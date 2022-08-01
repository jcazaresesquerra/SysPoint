package com.app.syspoint.ui.clientes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.ClientesRutaBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.ClientesRutaDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.RolesDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.CobranzaJson;
import com.app.syspoint.json.RequestCobranza;
import com.app.syspoint.ui.clientes.PreciosEspeciales.PreciosEspecialesActivity;
import com.app.syspoint.ui.cobranza.CobranzaActivity;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteFragment extends Fragment {

    AdapterListaClientes mAdapter;
    List<ClienteBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_clientes;

    private ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cliente, container, false);
        setHasOptionsMenu(true);
        lyt_clientes = root.findViewById(R.id.lyt_clientes);
        rlprogress = root.findViewById(R.id.rlprogress_cliente);
        initRecyclerView(root);

        FloatingActionButton actionButton = root.findViewById(R.id.fab_add_clientes);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegistroClienteActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cliente_fragment, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search_ciente);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);

                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                mAdapter.getFilter().filter(arg0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                mAdapter.getFilter().filter(arg0);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.syncClientes:
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
                }, getActivity()).execute(), 100);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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

    private void initRecyclerView(View root) {

        mData = (List<ClienteBean>) (List<?>) new ClienteDao().list();

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaClientes(mData, new AdapterListaClientes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ClienteBean obj, int position) {

                showDialogList(obj);
            }
        }, new AdapterListaClientes.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {
                return false;
            }
        });

        recyclerView.setAdapter(mAdapter);

    }

    private void showDialogList(ClienteBean cliente) {

        final ClienteBean clienteBean = cliente;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Editar");
        arrayAdapter.add("Nueva venta");
        arrayAdapter.add("Ver Mapa");
        arrayAdapter.add("Editar precios especiales");
        arrayAdapter.add("Agregar a ruta");
        arrayAdapter.add(("Recordatorio"));
        if (clienteBean.getIs_credito()) {
            arrayAdapter.add(("Cobranza"));
        }

        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                String identificador = "";

                //Obtiene el nombre del vendedor
                final EmpleadoBean vendedoresBean = AppBundle.getUserBean();


                if (vendedoresBean != null) {
                    identificador = vendedoresBean.getIdentificador();
                }
                final RolesDao rolesDao = new RolesDao();
                final RolesBean rolesBean = rolesDao.getRolByEmpleado(identificador, "Clientes");

                if (strName == null || strName.compareToIgnoreCase("Editar") == 0) {
                    if (rolesBean != null) {
                        if (rolesBean.getActive() == true) {
                            editCliente(clienteBean.getCuenta());
                        } else {

                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                } else if (strName.compareToIgnoreCase("Nueva venta") == 0) {

                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Espere un momento");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                        progressDialog.dismiss();
                        if (connected) {
                            donwloadCobranza(clienteBean.getCuenta());
                        }else {
                            HashMap<String, String> parametros = new HashMap<>();
                            parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                            Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                        }
                    }, getActivity()).execute(), 100);
                } else if (strName.compareToIgnoreCase("Ver Mapa") == 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + clienteBean.getLatitud() + "," + clienteBean.getLongitud()));
                    startActivity(intent);
                } else if (strName.compareToIgnoreCase("Editar precios especiales") == 0) {

                    if (rolesBean != null) {
                        if (rolesBean.getActive() == true) {
                            HashMap<String, String> parametros = new HashMap<>();
                            parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                            Actividades.getSingleton(getActivity(), PreciosEspecialesActivity.class).muestraActividad(parametros);
                        } else {
                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } else if (strName.compareToIgnoreCase("Agregar a ruta") == 0) {

                        final ClientesRutaDao daoRuta = new ClientesRutaDao();
                        final ClientesRutaBean bean = daoRuta.getClienteByCuentaCliente(clienteBean.getCuenta());

                        if (bean == null) {

                            final ClientesRutaDao dao = new ClientesRutaDao();
                            final ClientesRutaBean beanCliente = dao.getClienteFirts();

                            long id = dao.getUltimoConsec();

                            if (beanCliente != null){
                                final ClientesRutaBean clientesRutaBean = new ClientesRutaBean();
                                final ClientesRutaDao clientesRutaDao = new ClientesRutaDao();

                                clientesRutaBean.setId(id);
                                clientesRutaBean.setNombre_comercial(clienteBean.getNombre_comercial());
                                clientesRutaBean.setCalle(clienteBean.getCalle());
                                clientesRutaBean.setNumero(clienteBean.getNumero());
                                clientesRutaBean.setColonia(clienteBean.getColonia());
                                clientesRutaBean.setCuenta(clienteBean.getCuenta());
                                clientesRutaBean.setRango(clienteBean.getRango());
                                clientesRutaBean.setLun(beanCliente.getLun());
                                clientesRutaBean.setMar(beanCliente.getMar());
                                clientesRutaBean.setMie(beanCliente.getMie());
                                clientesRutaBean.setJue(beanCliente.getJue());
                                clientesRutaBean.setVie(beanCliente.getVie());
                                clientesRutaBean.setSab(beanCliente.getSab());
                                clientesRutaBean.setDom(beanCliente.getDom());
                                clientesRutaBean.setVisitado(0);
                                clientesRutaBean.setLatitud(clienteBean.getLatitud());
                                clientesRutaBean.setLongitud(clienteBean.getLongitud());
                                clientesRutaDao.insert(clientesRutaBean);

                                Toast.makeText(getContext(), "El cliente se agrego exitosamente", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "El cliente ya existe en ruta", Toast.LENGTH_LONG).show();
                        }
                }else if (strName.compareToIgnoreCase("Recordatorio") == 0) {
                    showCustomDialog(clienteBean);
                }else if(strName.compareToIgnoreCase("Cobranza") == 0 ){

                    if (rolesBean != null) {
                        if (rolesBean.getActive() == true) {

                                HashMap<String, String> parametros = new HashMap<>();
                                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                                Actividades.getSingleton(getContext(), CobranzaActivity.class).muestraActividad(parametros);


                        } else {
                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                }

                dialog.dismiss();
            }
        });
        builderSingle.show();
    }


    private void donwloadCobranza(String cuenta) {


        final CobranzaDao cobranzaDao = new CobranzaDao();
        List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
        cobranzaBeanList = cobranzaDao.getDocumentsByCliente(cuenta);
        for(CobranzaBean cob : cobranzaBeanList){
            if (cob.getCliente().compareToIgnoreCase(cuenta) == 0 && cob.getFecha().compareToIgnoreCase(Utils.fechaActual())!=0){
                cobranzaDao.delete(cob);
            }
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere un momento obteniendo datos....");
        progressDialog.show();

        ClienteDao clienteDao = new ClienteDao();
        ClienteBean clienteBean = clienteDao.getClienteByCuenta(cuenta);
        RequestCobranza requestCobranza = new RequestCobranza();
        requestCobranza.setCuenta(clienteBean.getCuenta());

        //Obtiene la respuesta
        Call<CobranzaJson> getCobranza = ApiServices.getClientRestrofit().create(PointApi.class).getCobranzaByCliente(requestCobranza);
        getCobranza.enqueue(new Callback<CobranzaJson>() {
            @Override
            public void onResponse(Call<CobranzaJson> call, Response<CobranzaJson> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    CobranzaDao cobranzaDao = new CobranzaDao();
                    for (Cobranza item : response.body().getCobranzas()) {

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

                    progressDialog.dismiss();
                    HashMap<String, String> parametros = new HashMap<>();
                    parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                    Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                }
            }

            @Override
            public void onFailure(Call<CobranzaJson> call, Throwable t) {
                progressDialog.dismiss();
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
            }
        });

    }




    private void showCustomDialog(ClienteBean clienteBean) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_recordatorio);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_recordatorio);
        if (clienteBean.getRecordatorio() == null ||  clienteBean.getRecordatorio() == "null" || clienteBean.getRecordatorio().isEmpty()  ){
        }else {
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
                String review = et_post.getText().toString().trim();
                if (review.isEmpty()) {
                    Toast.makeText(getContext(), "Ingrese un recordatorio", Toast.LENGTH_SHORT).show();
                } else {
                    ClienteDao clienteDao = new ClienteDao();
                    clienteBean.setRecordatorio(review);
                    clienteBean.setDate_sync(Utils.fechaActual());
                    clienteDao.save(clienteBean);
                    testLoadClientes(String.valueOf(clienteBean.getId()));
                }

                dialog.dismiss();
                Toast.makeText(getContext(), "Recordatorio exitoso", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void testLoadClientes(String idCliente) {

        final ClienteDao clienteDao = new ClienteDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clienteDao.getByIDCliente(idCliente);

        List<Cliente> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB) {
            Cliente cliente = new Cliente();
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
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()){
                cliente.setIsCredito(1);
            }else{
                cliente.setIsCredito(0);
            }
            cliente.setSaldo_credito(item.getSaldo_credito());
            cliente.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz()== "null" && item.getMatriz() == null) {
                cliente.setMatriz("null");
            }else{
                cliente.setMatriz(item.getMatriz());
            }
            listaClientes.add(cliente);
        }

        ClienteJson clienteRF = new ClienteJson();
        clienteRF.setClientes(listaClientes);
        String json = new Gson().toJson(clienteRF);
        Log.d("SinEmpleados", json);

        Call<ClienteJson> loadClientes = ApiServices.getClientRestrofit().create(PointApi.class).sendCliente(clienteRF);

        loadClientes.enqueue(new Callback<ClienteJson>() {
            @Override
            public void onResponse(Call<ClienteJson> call, Response<ClienteJson> response) {
                if (response.isSuccessful()) {
                    progresshide();

                }
            }

            @Override
            public void onFailure(Call<ClienteJson> call, Throwable t) {
                progresshide();
            }
        });
    }


    private void editCliente(String cuenta) {
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, cuenta);
        Actividades.getSingleton(getActivity(), ActualizaClienteActivity.class).muestraActividad(parametros);
    }

    private void getData() {

        progressshow();
        Call<ClienteJson> getClientes = ApiServices.getClientRestrofit().create(PointApi.class).getAllClientes();
        getClientes.enqueue(new Callback<ClienteJson>() {
            @Override
            public void onResponse(Call<ClienteJson> call, Response<ClienteJson> response) {

                if (response.isSuccessful()) {
                    progresshide();

                    for (Cliente item : response.body().getClientes()) {

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

                            if (item.getIsCredito() == 1){
                                clienteBean.setIs_credito(true);
                            }else{
                                clienteBean.setIs_credito(false);
                            }

                            clienteBean.setLimite_credito(item.getLimite_credito());
                            clienteBean.setSaldo_credito(item.getSaldo_credito());

                            clienteDao.insert(clienteBean);
                            mData.add(clienteBean);
                            mAdapter.setClients(mData);

                            if (mData.size() > 0) {
                                lyt_clientes.setVisibility(View.GONE);
                            } else {
                                lyt_clientes.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ClienteJson> call, Throwable t) {
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

    @Override
    public void onResume() {
        super.onResume();
        mData = (List<ClienteBean>) (List<?>) new ClienteDao().list();
        mAdapter.setClients(mData);

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }
    }
}
