package com.app.syspoint.ui.clientes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.AppBundle;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.models.Client;
import com.app.syspoint.ui.clientes.PreciosEspeciales.PreciosEspecialesActivity;
import com.app.syspoint.ui.cobranza.CobranzaActivity;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                }).execute(), 100);

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

        mData = (List<ClienteBean>) (List<?>) new ClientDao().list();

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaClientes(
                mData,
                (view, obj, position, onDialogShownListener) -> {
                    showDialogList(obj, onDialogShownListener);
                },
                position -> false
        );

        recyclerView.setAdapter(mAdapter);

    }

    private void showDialogList(ClienteBean cliente, AdapterListaClientes.OnDialogShownListener onDialogShownListener) {

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
                EmpleadoBean vendedoresBean = AppBundle.getUserBean();

                if (vendedoresBean == null) {
                    vendedoresBean = new CacheInteractor().getSeller();
                }

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
                            downloadCharge(clienteBean.getCuenta());
                        }else {
                            HashMap<String, String> parametros = new HashMap<>();
                            parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                            Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                        }
                    }).execute(), 100);
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

                        final RuteClientDao daoRuta = new RuteClientDao();
                        final ClientesRutaBean bean = daoRuta.getClienteByCuentaCliente(clienteBean.getCuenta());

                        if (bean == null) {

                            final RuteClientDao dao = new RuteClientDao();
                            final ClientesRutaBean beanCliente = dao.getClienteFirts();

                            long id = dao.getUltimoConsec();

                            if (beanCliente != null){
                                final ClientesRutaBean clientesRutaBean = new ClientesRutaBean();
                                final RuteClientDao ruteClientDao = new RuteClientDao();

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
                                ruteClientDao.insert(clientesRutaBean);

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

        onDialogShownListener.onDialogShown();
    }


    private void downloadCharge(String cuenta) {

        final PaymentDao paymentDao = new PaymentDao();
        List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
        cobranzaBeanList = paymentDao.getDocumentsByCliente(cuenta);
        for(CobranzaBean cob : cobranzaBeanList){
            if (cob.getCliente().compareToIgnoreCase(cuenta) == 0 && cob.getFecha().compareToIgnoreCase(Utils.fechaActual())!=0){
                paymentDao.delete(cob);
            }
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere un momento obteniendo datos....");
        progressDialog.show();


        new ChargeInteractorImp().executeGetChargeByClient(cuenta, new ChargeInteractor.OnGetChargeByClientListener() {
            @Override
            public void onGetChargeByClientSuccess(@NonNull List<? extends CobranzaBean> chargeByClientList) {
                progressDialog.dismiss();
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, cuenta);
                Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
            }

            @Override
            public void onGetChargeByClientError() {
                progressDialog.dismiss();
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, cuenta);
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
                    ClientDao clientDao = new ClientDao();
                    clienteBean.setRecordatorio(review);
                    clienteBean.setDate_sync(Utils.fechaActual());
                    clientDao.save(clienteBean);
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

        final ClientDao clientDao = new ClientDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clientDao.getByIDClient(idCliente);

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
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()){
                cliente.setCredito(1);
            }else{
                cliente.setCredito(0);
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

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                progresshide();
                Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
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
        new ClientInteractorImp().executeGetAllClients(new ClientInteractor.GetAllClientsListener() {
            @Override
            public void onGetAllClientsSuccess(@NonNull List<? extends ClienteBean> clientList) {
                mData = new ArrayList<>();
                mData.addAll(clientList);
                mAdapter.setClients((List<ClienteBean>) clientList);

                if (mData.size() > 0) {
                    lyt_clientes.setVisibility(View.GONE);
                } else {
                    lyt_clientes.setVisibility(View.VISIBLE);
                }
                progresshide();
            }

            @Override
            public void onGetAllClientsError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un problema al obtener clientes", Toast.LENGTH_LONG).show();
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
        mData = (List<ClienteBean>) (List<?>) new ClientDao().list();
        mAdapter.setClients(mData);

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }
    }
}
