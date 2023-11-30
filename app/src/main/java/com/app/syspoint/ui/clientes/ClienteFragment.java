package com.app.syspoint.ui.clientes;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.app.syspoint.R;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.models.Client;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.dao.RuteClientDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.entities.ChargeBox;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.repository.objectBox.entities.RuteClientBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;
import com.app.syspoint.ui.clientes.PreciosEspeciales.PreciosEspecialesActivity;
import com.app.syspoint.ui.cobranza.CobranzaActivity;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClienteFragment extends Fragment {

    AdapterListaClientes mAdapter;
    List<ClientBox> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_clientes;

    private ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cliente, container, false);
        setHasOptionsMenu(true);
        lyt_clientes = root.findViewById(R.id.lyt_clientes);
        rlprogress = root.findViewById(R.id.rlprogress_cliente);

        View generalPublic = root.findViewById(R.id.general_public_container);
        generalPublic.setOnClickListener(v -> {

            ClientDao clientDao = new ClientDao();
            ClientBox client = clientDao.getClientGeneralPublic();
            if (client == null) {
                client = new ClientBox(1L, "Publico General",
                        "Industrias del Valle", "1", "Parque Canacintra",
                        "Culiacán Rosales", 80150, "22-08-2021",
                        "000000", true, "000001", "01", 0,0,0,0,
                        0,0,0, 0,0,0,0,0,
                        0,0, 0, "24.777435983809422",
                        "-107.437107128804", null, null,
                        false, 0, false, 0.0,
                        0.0, null, "2022-11-08 00:00:00", Utils.fechaActualHMS(), 0, "", "", "");
                try {
                    clientDao.insertBox(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            showGeneralPublicDialog(client);
        });

        initRecyclerView(root);

        FloatingActionButton actionButton = root.findViewById(R.id.fab_add_clientes);
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RegistroClienteActivity.class);
            startActivity(intent);
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
                //mAdapter.getFilter().filter(arg0);
                new ClientInteractorImp().executeFindClient(arg0, new ClientInteractor.FindClientListener() {
                    @Override
                    public void onFindClientSuccess(List<ClientBox> clientList) {
                        List<ClientBox> clientBeanList = (List<ClientBox> ) clientList;
                        mAdapter.setClients(clientBeanList);

                        if (clientBeanList.size() > 0) {
                            lyt_clientes.setVisibility(View.GONE);
                        } else {
                            lyt_clientes.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFindClientError() {
                        mAdapter.getFilter().filter(arg0);
                    }
                });
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
        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            mData = (List<ClientBox>) new ClientDao().getClientsByRute(ruteoBean.getRuta());
        } else {
            //mData = (List<ClientesRutaBean>) new RuteClientDao().list();
        }

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        // remove inactive users
        mData.removeIf(item -> !item.getStatus());
        mData.removeIf(item -> item.getCuenta().equals("000000"));

        mAdapter = new AdapterListaClientes(
                mData,
                (view, obj, position, onDialogShownListener) -> {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Espere un momento");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new ClientInteractorImp().executeGetClientByAccount(obj.getCuenta(), new ClientInteractor.GetClientByAccount() {
                        @Override
                        public void onGetClientSuccess() {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onGetClientError() {
                            progressDialog.dismiss();
                        }
                    });
                    showDialogList(obj, onDialogShownListener);
                },
                position -> false
        );

        recyclerView.setAdapter(mAdapter);

    }

    private void showGeneralPublicDialog(ClientBox cliente) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.tenet_icon);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Nueva venta");

        builderSingle.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);

            if (strName.compareToIgnoreCase("Nueva venta") == 0) {

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (connected) {
                        downloadCharge(cliente.getCuenta());
                    } else {
                        HashMap<String, String> parametros = new HashMap<>();
                        parametros.put(Actividades.PARAM_1, cliente.getCuenta());
                        Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                    }
                }).execute(), 100);
            }

            dialog.dismiss();
        });
        builderSingle.show();

    }

    private void showDialogList(ClientBox cliente, AdapterListaClientes.OnDialogShownListener onDialogShownListener) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.tenet_icon);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);

        arrayAdapter.add("Editar");
        arrayAdapter.add("Nueva venta");
        arrayAdapter.add("Ver Mapa");
        arrayAdapter.add("Editar precios especiales");
        arrayAdapter.add("Agregar a ruta");
        arrayAdapter.add(("Recordatorio"));
        arrayAdapter.add(("Cobranza"));

        builderSingle.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);

            String identificador = "";

            //Obtiene el nombre del vendedor
            EmployeeBox vendedoresBean = getEmployee();

            if (vendedoresBean != null) {
                identificador = vendedoresBean.getIdentificador();
            }
            final RolesDao rolesDao = new RolesDao();
            RolesBox rolesBean = rolesDao.getRolByEmpleado(identificador, "Clientes");
            RolesBox rolesBean1 = rolesDao.getRolByEmpleado(identificador, "EditarClientes");

            if (strName == null || strName.compareToIgnoreCase("Editar") == 0) {
                if (rolesBean != null && rolesBean1 != null) {
                    if (rolesBean.getActive()) {
                        editCliente(cliente.getCuenta()); //btnEditarCliente #codigotemporal
                        //Toast.makeText(getContext(), "Opcion desactivada temporalmente", Toast.LENGTH_LONG).show();
                        return;
                    } else {

                        Toast.makeText(getContext(), "No tienes privilegios para editar clientes", Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    Toast.makeText(getContext(), "No tienes privilegios para editar clientes", Toast.LENGTH_LONG).show();
                    return;
                }

            } else if (strName.compareToIgnoreCase("Nueva venta") == 0) {

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (connected) {
                        downloadCharge(cliente.getCuenta());
                    }else {
                        HashMap<String, String> parametros = new HashMap<>();
                        parametros.put(Actividades.PARAM_1, cliente.getCuenta());
                        Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                    }
                }).execute(), 100);
            } else if (strName.compareToIgnoreCase("Ver Mapa") == 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + cliente.getLatitud() + "," + cliente.getLongitud()));
                startActivity(intent);
            } else if (strName.compareToIgnoreCase("Editar precios especiales") == 0) {

                if (rolesBean != null) {
                    if (rolesBean.getActive() == true) {
                        HashMap<String, String> parametros = new HashMap<>();
                        parametros.put(Actividades.PARAM_1, cliente.getCuenta());
                        Actividades.getSingleton(getActivity(), PreciosEspecialesActivity.class).muestraActividad(parametros);
                    } else {
                        Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } else if (strName.compareToIgnoreCase("Agregar a ruta") == 0) {

                RoutingDao routingDao = new RoutingDao();
                RoutingBox ruteoBean = routingDao.getRutaEstablecida();

                if (ruteoBean == null) {
                    ruteoBean = new RoutingBox();
                    routingDao.clear();
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String dia = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
                    if (dia.compareToIgnoreCase("Monday") == 0) {
                        ruteoBean.setDia(1);
                    } else if (dia.compareToIgnoreCase("Tuesday") == 0) {
                        ruteoBean.setDia(2);
                    } else if (dia.compareToIgnoreCase("Wednesday") == 0) {
                        ruteoBean.setDia(3);
                    } else if (dia.compareToIgnoreCase("Thursday") == 0) {
                        ruteoBean.setDia(4);
                    } else if (dia.compareToIgnoreCase("Friday") == 0) {
                        ruteoBean.setDia(5);
                    } else if (dia.compareToIgnoreCase("Saturday") == 0) {
                        ruteoBean.setDia(6);
                    } else if (dia.compareToIgnoreCase("Sunday") == 0) {
                        ruteoBean.setDia(7);
                    }
                    ruteoBean.setId(1L);
                    ruteoBean.setFecha(Utils.fechaActual());
                    ruteoBean.setRuta("0");

                    try {
                        routingDao.insertBox(ruteoBean);
                    } catch (Exception e) {
                        routingDao.insertBox(ruteoBean);
                    }
                }

                final RuteClientDao ruteClientDao = new RuteClientDao();
                final RuteClientBox bean = ruteClientDao.getClienteByCuentaCliente(cliente.getCuenta(), ruteoBean.getDia(), ruteoBean.getRuta());

                    if (bean == null) {
                        RuteClientBox beanCliente = ruteClientDao.getClienteByCuentaCliente(cliente.getCuenta());

                        if (beanCliente == null) beanCliente = ruteClientDao.getClienteFirts();

                        long id = ruteClientDao.getUltimoConsec();
                        int lastOrder = ruteClientDao.getLastClientInOrder(ruteoBean.getDia(), ruteoBean.getRuta());

                        if (beanCliente != null){
                            RuteClientBox ruteClientBox = new RuteClientBox();

                            ruteClientBox.setId(id);
                            ruteClientBox.setNombre_comercial(cliente.getNombre_comercial());
                            ruteClientBox.setPhone_contact(cliente.getContacto_phone());
                            ruteClientBox.setCalle(cliente.getCalle());
                            ruteClientBox.setNumero(cliente.getNumero());
                            ruteClientBox.setColonia(cliente.getColonia());
                            ruteClientBox.setCuenta(cliente.getCuenta());
                            ruteClientBox.setRango(ruteoBean.getRuta());
                            ruteClientBox.setStatus(cliente.getStatus());

                            ruteClientBox.setVentaClientId(cliente.getVentaClientId());
                            ruteClientBox.setVentaFecha(cliente.getVentaFecha());
                            ruteClientBox.setVentaCreatedAt(cliente.getVentaCreatedAt());
                            ruteClientBox.setVentaUpdatedAt(cliente.getVentaUpdatedAt());


                            if (ruteoBean.getDia() == 1)
                                ruteClientBox.setLun(1);
                            else if (ruteoBean.getDia() == 2)
                                ruteClientBox.setMar(1);
                            else if (ruteoBean.getDia() == 3)
                                ruteClientBox.setMie(1);
                            else if (ruteoBean.getDia() == 4)
                                ruteClientBox.setJue(1);
                            else if (ruteoBean.getDia() == 5)
                                ruteClientBox.setVie(1);
                            else if (ruteoBean.getDia() == 6)
                                ruteClientBox.setSab(1);
                            else if (ruteoBean.getDia() == 7)
                                ruteClientBox.setDom(1);

                            ruteClientBox.setOrder(beanCliente.getOrder());

                            if (ruteoBean.getDia() == 1) {
                                ruteClientBox.setLunOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 2) {
                                ruteClientBox.setMarOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 3) {
                                ruteClientBox.setMieOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 4) {
                                ruteClientBox.setJueOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 5) {
                                ruteClientBox.setVieOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 6) {
                                ruteClientBox.setSabOrder(lastOrder);
                            } else if (ruteoBean.getDia() == 7) {
                                ruteClientBox.setDomOrder(lastOrder);
                            } else {
                                ruteClientBox.setLun(beanCliente.getLun());
                                ruteClientBox.setMar(beanCliente.getMar());
                                ruteClientBox.setMie(beanCliente.getMie());
                                ruteClientBox.setJue(beanCliente.getJue());
                                ruteClientBox.setVie(beanCliente.getVie());
                                ruteClientBox.setSab(beanCliente.getSab());
                                ruteClientBox.setDom(beanCliente.getDom());
                            }

                            ruteClientBox.setVisitado(0);
                            ruteClientBox.setLatitud(cliente.getLatitud());
                            ruteClientBox.setLongitud(cliente.getLongitud());
                            ruteClientDao.insertBox(ruteClientBox);

                            Toast.makeText(getContext(), "El cliente se agrego exitosamente", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "El cliente ya existe en ruta", Toast.LENGTH_LONG).show();
                    }
            }else if (strName.compareToIgnoreCase("Recordatorio") == 0) {
                showCustomDialog(cliente);
            }else if (strName.compareToIgnoreCase("Cobranza") == 0 ){
                rolesBean = rolesDao.getRolByEmpleado(identificador, "Cobranza");

                if (rolesBean != null && rolesBean.getActive()) {

                        HashMap<String, String> parametros = new HashMap<>();
                        parametros.put(Actividades.PARAM_1, cliente.getCuenta());
                        Actividades.getSingleton(getContext(), CobranzaActivity.class).muestraActividad(parametros);

                } else {
                    Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            dialog.dismiss();
        });
        builderSingle.show();

        onDialogShownListener.onDialogShown();
    }


    private void downloadCharge(String cuenta) {

        ChargeDao chargeDao = new ChargeDao();
        List<ChargeBox> chargeBoxList = new ArrayList<>();
        chargeBoxList = chargeDao.getDocumentsByCliente(cuenta);
        for(ChargeBox cob : chargeBoxList){
            if (cob.getCliente().compareToIgnoreCase(cuenta) == 0 && cob.getFecha().compareToIgnoreCase(Utils.fechaActual())!=0){
                chargeDao.removeBox(cob.getId());
            }
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere un momento obteniendo datos....");
        progressDialog.show();


        new ChargeInteractorImp().executeGetChargeByClient(cuenta, new ChargeInteractor.OnGetChargeByClientListener() {
            @Override
            public void onGetChargeByClientSuccess(@NonNull List<ChargeBox> chargeByClientList) {
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

    private void showCustomDialog(ClientBox clientBox) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_recordatorio);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_recordatorio);
        if (clientBox.getRecordatorio() == null ||  clientBox.getRecordatorio() == "null" || clientBox.getRecordatorio().isEmpty()  ){
        }else {
            et_post.setText(clientBox.getRecordatorio());

        }
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_submit)).setOnClickListener(v -> {
            String review = et_post.getText().toString().trim();
            if (review.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese un recordatorio", Toast.LENGTH_SHORT).show();
            } else {
                ClientDao clientDao = new ClientDao();
                clientBox.setRecordatorio(review);
                clientBox.setDate_sync(Utils.fechaActual());
                clientBox.setUpdatedAt(Utils.fechaActualHMS());
                clientDao.insertBox(clientBox);
                testLoadClientes(clientBox.getId());
            }

            dialog.dismiss();
            Toast.makeText(getContext(), "Recordatorio exitoso", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void testLoadClientes(Long idCliente) {

        final ClientDao clientDao = new ClientDao();
        List<ClientBox> listaClientesDB = clientDao.getByIDClient(idCliente);
        List<Client> listaClientes = new ArrayList<>();

        for (ClientBox item : listaClientesDB) {
            Client cliente = new Client();
            cliente.setNombreComercial(item.getNombre_comercial());
            cliente.setCalle(item.getCalle());
            cliente.setNumero(item.getNumero());
            cliente.setColonia(item.getColonia());
            cliente.setCiudad(item.getCiudad());
            cliente.setCodigoPostal(item.getCodigo_postal());
            cliente.setFechaRegistro(item.getFecha_registro());
            cliente.setCuenta(item.getCuenta());
            cliente.setStatus(item.getStatus()? 1 : 0);
            cliente.setConsec(item.getConsec());
            cliente.setRango(item.getRango());
            cliente.setLun(item.getLun());
            cliente.setMar(item.getMar());
            cliente.setMie(item.getMie());
            cliente.setJue(item.getJue());
            cliente.setVie(item.getVie());
            cliente.setSab(item.getSab());
            cliente.setDom(item.getDom());
            cliente.setLatitud(item.getLatitud());
            cliente.setLongitud(item.getLongitud());
            cliente.setPhone_contacto(item.getContacto_phone());
            cliente.setRecordatorio(item.getRecordatorio());
            cliente.setUpdatedAt(item.getUpdatedAt());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.isCredito()){
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
                //Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                progresshide();
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void editCliente(String cuenta) {
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, cuenta);
        Actividades.getSingleton(getActivity(), UpdateClientActivity.class).muestraActividad(parametros);
    }

    private void getData() {

        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            progressshow();
            new ClientInteractorImp().executeGetAllClientsAndLastSellByRute(ruteoBean.getRuta(), ruteoBean.getDia(), new ClientInteractor.GetAllClientsListener() {
                @Override
                public void onGetAllClientsSuccess(@NonNull List<ClientBox> clientList) {
                    mData = new ClientDao().getClientsByRute(ruteoBean.getRuta());

                    // remove inactive users
                    mData.removeIf(item -> !item.getStatus());
                    mData.removeIf(item -> item.getCuenta().equals("000000"));

                    mAdapter.setClients(mData);

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
                    //Toast.makeText(requireActivity(), "Ha ocurrido un problema al obtener clientes", Toast.LENGTH_LONG).show();
                }
            });
        }
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
        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            mData = (List<ClientBox>) new ClientDao().getClientsByRute(ruteoBean.getRuta());
        } else {
            //mData = (List<ClientesRutaBean>) new RuteClientDao().list();
        }
        // remove inactive users
        mData.removeIf(item -> !item.getStatus());
        mData.removeIf(item -> item.getCuenta().equals("000000"));
        mAdapter.setClients(mData);

        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }
    }

    private EmployeeBox getEmployee() {
        EmployeeBox vendedoresBean = AppBundle.getUserBox();
        if (vendedoresBean == null) {
            SessionBox sessionBox = new SessionDao().getUserSession();
            if (sessionBox != null) {
                vendedoresBean = new EmployeeDao().getEmployeeByID(sessionBox.getEmpleadoId());
            } else {
                vendedoresBean = new CacheInteractor().getSeller();
            }
        }
        return vendedoresBean;
    }
}
