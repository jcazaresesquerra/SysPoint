package com.app.syspoint.ui.home;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.data.GetAllDataInteractor;
import com.app.syspoint.interactor.data.GetAllDataInteractorImp;
import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.interactor.product.GetProductInteractor;
import com.app.syspoint.interactor.product.GetProductsInteractorImp;
import com.app.syspoint.interactor.roles.RolInteractor;
import com.app.syspoint.interactor.roles.RolInteractorImp;
import com.app.syspoint.interactor.visit.VisitInteractor;
import com.app.syspoint.interactor.visit.VisitInteractorImp;
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
                progressDialog.setMessage("Obteniendo actualizaciones...");

                progressDialog.show();
                new GetAllDataInteractorImp().executeGetAllDataByDate(new GetAllDataInteractor.OnGetAllDataByDateListener() {
                    @Override
                    public void onGetAllDataByDateSuccess() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onGetAllDataByDateError() {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "Ha ocurrido un error", Toast.LENGTH_LONG).show();
                    }
                });

                sendVentas();
                loadCobranza();
                loadAbonos();
                loadVisitas();
                loadClientes();
                loadPreciosEspeciales();
            }
        }).execute(), 100);
    }

    private void loadAbonos() {

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

        new ChargeInteractorImp().executeUpdateCharge(listaCobranza, new ChargeInteractor.OnUpdateChargeListener() {
            @Override
            public void onUpdateChargeSuccess() {
                Toast.makeText(requireActivity(), "Cobranza actualizada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUpdateChargeError() {
                Toast.makeText(requireActivity(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
            }
        });
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
        new ChargeInteractorImp().executeGetCharge(new ChargeInteractor.OnGetChargeListener() {
            @Override
            public void onGetChargeSuccess(@NonNull List<? extends CobranzaBean> chargeList) {
                progresshide();
            }

            @Override
            public void onGetChargeError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener cobranzas", Toast.LENGTH_LONG).show();
            }
        });

        progressshow();
        new GetEmployeesInteractorImp().executeGetEmployees(new GetEmployeeInteractor.GetEmployeesListener() {
            @Override
            public void onGetEmployeesSuccess(@NonNull List<? extends EmpleadoBean> employees) {
                progresshide();
            }

            @Override
            public void onGetEmployeesError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener empleados", Toast.LENGTH_LONG).show();
            }
        });

        progressshow();
        new ClientInteractorImp().executeGetAllClients(new ClientInteractor.GetAllClientsListener() {
            @Override
            public void onGetAllClientsSuccess(@NonNull List<? extends ClienteBean> clientList) {
                progresshide();
            }

            @Override
            public void onGetAllClientsError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener clientes", Toast.LENGTH_LONG).show();
            }
        });


        progressshow();
        new GetProductsInteractorImp().executeGetProducts(new GetProductInteractor.OnGetProductsListener() {
            @Override
            public void onGetProductsSuccess(@NonNull List<? extends ProductoBean> products) {
                progresshide();
            }

            @Override
            public void onGetProductsError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener productos", Toast.LENGTH_LONG).show();

            }
        });


        progressshow();
        new RolInteractorImp().executeGetAllRoles(new RolInteractor.OnGetAllRolesListener() {
            @Override
            public void onGetAllRolesSuccess(@NonNull List<? extends RolesBean> roles) {
                progresshide();
            }

            @Override
            public void onGetAllRolesError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_LONG).show();
            }
        });

        progressshow();
        new PriceInteractorImp().executeGetSpecialPrices(new PriceInteractor.GetSpecialPricesListener() {
            @Override
            public void onGetSpecialPricesSuccess(@NonNull List<? extends PreciosEspecialesBean> priceList) {
                progresshide();
            }

            @Override
            public void onGetSpecialPricesError() {
                progresshide();
                Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener precios", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }

    private void sendVentas() {
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
    }

    private void loadVisitas() {

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

        new VisitInteractorImp().executeSaveVisit(listaVisitas, new VisitInteractor.OnSaveVisitListener() {
            @Override
            public void onSaveVisitSuccess() {
                Toast.makeText(requireActivity(), "Visita registrada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveVisitError() {
                Toast.makeText(requireActivity(), "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadCobranza() {

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

            new ChargeInteractorImp().executeSaveCharge(listaCobranza, new ChargeInteractor.OnSaveChargeListener() {
                @Override
                public void onSaveChargeSuccess() {
                    Toast.makeText(requireActivity(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSaveChargeError() {
                    Toast.makeText(requireActivity(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
                }
            });
    }


    private void loadPreciosEspeciales() {

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

        new PriceInteractorImp().executeSendPrices(listaPreciosServidor, new PriceInteractor.SendPricesListener() {
            @Override
            public void onSendPricesSuccess() {
                progresshide();
                Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSendPricesError() {
                progresshide();
                Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadClientes() {

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

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        });
    }

}