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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.interactor.product.GetProductInteractor;
import com.app.syspoint.interactor.product.GetProductsInteractorImp;
import com.app.syspoint.interactor.roles.RolInteractor;
import com.app.syspoint.interactor.roles.RolInteractorImp;
import com.app.syspoint.interactor.token.TokenInteractor;
import com.app.syspoint.interactor.token.TokenInteractorImpl;
import com.app.syspoint.interactor.visit.VisitInteractor;
import com.app.syspoint.interactor.visit.VisitInteractorImp;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.R;
import com.app.syspoint.models.Employee;
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
import com.app.syspoint.repository.database.dao.EmployeeDao;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.SpecialPricesDao;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.repository.database.dao.VisitsDao;
import com.app.syspoint.repository.request.http.Servicio;
import com.app.syspoint.repository.request.http.SincVentas;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Payment;
import com.app.syspoint.models.Price;
import com.app.syspoint.models.Visit;
import com.app.syspoint.ui.customs.DialogoRuteo;
import com.app.syspoint.ui.home.activities.MapsRuteoActivity;
import com.app.syspoint.ui.home.adapter.AdapterRutaClientes;
import com.app.syspoint.ui.ventas.VentasActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Constants;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
import com.app.syspoint.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        if (Constants.Companion.getSolictaRuta()) {
            creaRutaSeleccionada();
        }

        lyt_clientes = root.findViewById(R.id.lyt_clientes);
        rlprogress = root.findViewById(R.id.rlprogress_cliente);

        initRecyclerView(root);
        validateToken(true);

        return root;
    }

    private void getUpdates() {

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

        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
                getClientsByRute();
                getCobranzasByRute();
                getRoles();

                saveVentas();
                //saveCobranza();
                saveVisitas();
                //saveClientes();
                savePreciosEspeciales();
            }
        }).execute(), 100);
    }

    private void saveAbonos() {

        final PaymentDao paymentDao = new PaymentDao();
        List<CobranzaBean> cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual());
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
            cobranza.setUpdatedAt(item.getUpdatedAt());
            listaCobranza.add(cobranza);
        }

        new ChargeInteractorImp().executeUpdateCharge(listaCobranza, new ChargeInteractor.OnUpdateChargeListener() {
            @Override
            public void onUpdateChargeSuccess() {
                //Toast.makeText(requireActivity(), "Cobranza actualizada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUpdateChargeError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void creaRutaSeleccionada() {
        RoutingDao dao = new RoutingDao();
        RuteoBean bean = dao.getRutaEstablecidaFechaActual(Utils.fechaActual());

        if (bean != null) {
            PrettyDialog dialog = new PrettyDialog(getContext());
            dialog.setTitle("Establecer")
                    .setTitleColor(R.color.red_500)
                    .setMessage("¡Ya existe una configuración inicial!" + "\n ¿Desea actualizar la ruta?")
                    .setMessageColor(R.color.red_500)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.red_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            if (getActivity() != null) {
                                //((MainActivity) getActivity()).goHome();
                            }
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            showDialog();
                            if (getActivity() != null) {
                                //((MainActivity) getActivity()).goHome();
                            }
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            if (getActivity() != null) {
                                //((MainActivity) getActivity()).goHome();
                            }
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
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();
        if (vendedoresBean !=  null) {
            RolesBean rutasRol = new RolesDao().getRolByEmpleado(vendedoresBean.identificador, "Rutas");

            boolean editRuta = rutasRol != null && rutasRol.getActive();

            DialogoRuteo dialogoRuteo = new DialogoRuteo(getActivity(), editRuta, new DialogoRuteo.DialogListener() {
                @Override
                public void ready(String dia, String ruta) {

                    //Preguntamos si queremos agregar un nuevo ruteo
                    final PrettyDialog dialog = new PrettyDialog(getContext());
                    dialog.setTitle("Establecer")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Desea establecer la ruta inicial")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> {
                                if (getActivity() != null) {
                                    //((MainActivity) getActivity()).goHome();
                                }
                                dialog.dismiss();
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, () -> {

                                //Clientes normales
                                ClientDao clientDao = new ClientDao();
                                clientDao.updateVisited();

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
                                ruteoBean.setId(1L);
                                ruteoBean.setFecha(Utils.fechaActual());

                                EmpleadoBean vendedoresBean1 = AppBundle.getUserBean();
                                String ruta_ = ruta != null && !ruta.isEmpty() ? ruta : vendedoresBean1.getRute();

                                if (ruta_.equals("0")) {
                                    EmpleadoBean vendedoresBean = new CacheInteractor().getSeller();
                                    if (vendedoresBean != null)
                                        ruta_ = vendedoresBean.rute;
                                }

                                ruteoBean.setRuta(ruta_);

                                try {
                                    routingDao.insert(ruteoBean);
                                } catch (Exception e) {
                                    routingDao.save(ruteoBean);
                                }

                                estableceRuta();
                                vendedoresBean1.setRute(ruta_);
                                vendedoresBean1.setUpdatedAt(Utils.fechaActualHMS());

                                new EmployeeDao().save(vendedoresBean1);
                                String idEmpleado = String.valueOf(vendedoresBean1.getId());
                                testLoadEmpleado(idEmpleado);


                                if (getActivity() != null) {
                                    //((MainActivity) getActivity()).goHome();
                                }
                                dialog.dismiss();
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, () -> {
                                if (getActivity() != null) {
                                    //((MainActivity) getActivity()).goHome();
                                }
                                dialog.dismiss();
                            });
                    dialog.setCancelable(false);
                    dialog.show();

                }

                @Override
                public void cancelled() {}
            });


            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialogoRuteo.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogoRuteo.show();
            dialogoRuteo.getWindow().setAttributes(lp);
        } else {
            Toast.makeText(getContext(), "Error al obtener usuario", Toast.LENGTH_SHORT).show();
        }
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
        mAdapter.setData(mData);
        showHideImage();
    }

    private void saveData(List<ClienteBean> listaClientes, int day) {

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
                    if (day == 1)
                        bean.setOrder(item.getLunOrder());
                    else if (day == 2)
                        bean.setOrder(item.getMarOrder());
                    else if (day == 3)
                        bean.setOrder(item.getMieOrder());
                    else if (day == 4)
                        bean.setOrder(item.getJueOrder());
                    else if (day == 5)
                        bean.setOrder(item.getVieOrder());
                    else if (day == 6)
                        bean.setOrder(item.getSabOrder());
                    else if (day == 7)
                        bean.setOrder(item.getDomOrder());
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

        /*if (ruteoBean == null) {
            ruteoBean = new RuteoBean();
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
            EmpleadoBean vendedoresBean = new CacheInteractor().getSeller();
            if (vendedoresBean != null)
                ruteoBean.setRuta(vendedoresBean.getRute());
            else
                ruteoBean.setRuta("0");

            try {
                routingDao.insert(ruteoBean);
            } catch (Exception e) {
                routingDao.save(ruteoBean);
            }
        }*/

        if (ruteoBean != null && ruteoBean.getDia() > 0) {
            EmpleadoBean vendedoresBean = AppBundle.getUserBean();
            String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();

            mData = new RuteClientDao().getAllRutaClientes(ruta, ruteoBean.getDia());
        }

        setDataList(mData);
    }

    private void estableceRuta() {

        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            EmpleadoBean vendedoresBean = AppBundle.getUserBean();
            String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();

            List<ClientesRutaBean> clients = new RuteClientDao().getAllRutaClientes(ruta, ruteoBean.getDia());
            if (clients != null && !clients.isEmpty()) {
                loadRuta();
                Toast.makeText(getActivity(), "La ruta se cargo con exito!", Toast.LENGTH_LONG).show();
                //saveData(clients, ruteoBean.getDia());
            } else {
                getClientsByRute();
            }

            mData = clients;
        } else {
            loadRuta();
        }
    }

    private void initRecyclerView(View root) {

        mData = new ArrayList<>();

        if (!mData.isEmpty()) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterRutaClientes(mData, position -> {
            if (position >= 0) {
                ClientesRutaBean clienteBean = mData.get(position);
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
            }
        }, position -> {
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
                        //showDialogNotConnectionInternet();
                    } else {
                        validateToken(false);
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

    private void validateToken(Boolean isUpdate) {
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
                new TokenInteractorImpl().executeGetToken(new TokenInteractor.OnGetTokenListener() {
                    @Override
                    public void onGetTokenSuccess(@Nullable String token, String currentVersion) {
                        if (isUpdate){
                            getUpdates();
                        } else {
                            getData();
                        }
                    }

                    @Override
                    public void onGetTokenError(String baseUpdateUrl, String currentVersion) {
                        showVersionErrorDialog("Su versión no esta soportada, por favor, actualice su aplicación");
                    }
                });
            } else {
                showDialogNotConnectionInternet();
            }
        }).execute(), 100);
    }

    private void closeBox() {
        final PrettyDialog dialog = new PrettyDialog(requireActivity());
        dialog.setTitle("Corte del día")
                .setTitleColor(R.color.purple_500)
                .setMessage("Desea realizar el corte del día")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, dialog::dismiss)
                .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, dialog::dismiss)
                .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, dialog::dismiss);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showDialogNotConnectionInternet() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            getClientsByRute();
            dialog.dismiss();
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
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener cobranzas", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener empleados", Toast.LENGTH_SHORT).show();
            }
        });

       getClientsByRute();

        progressshow();
        new GetProductsInteractorImp().executeGetProducts(new GetProductInteractor.OnGetProductsListener() {
            @Override
            public void onGetProductsSuccess(@NonNull List<? extends ProductoBean> products) {
                progresshide();
            }

            @Override
            public void onGetProductsError() {
                progresshide();
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener productos", Toast.LENGTH_SHORT).show();

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
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener precios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }

    private void getClientsByRute() {

        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null) {
            EmpleadoBean vendedoresBean = AppBundle.getUserBean();
            String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();
            progressshow();
            new ClientInteractorImp().executeGetAllClientsByDate(ruta, ruteoBean.getDia(), new ClientInteractor.GetAllClientsListener() {
                @Override
                public void onGetAllClientsSuccess(@NonNull List<? extends ClienteBean> clientList) {
                    loadRuta();
                    saveClientes();
                    progresshide();
                }

                @Override
                public void onGetAllClientsError() {
                    //loadRuta();
                    progresshide();
                    saveClientes();
                    showDialogNotConnectionInternet();
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error. Conectate a internet para cambiar de ruta u obtener los clientes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getCobranzasByRute() {
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();

        if (vendedoresBean != null) {
            progressshow();
            new ChargeInteractorImp().executeGetCharge(new ChargeInteractor.OnGetChargeListener() {
                @Override
                public void onGetChargeSuccess(@NonNull List<? extends CobranzaBean> chargeList) {
                    saveCobranza();
                    saveAbonos();
                    progresshide();
                }

                @Override
                public void onGetChargeError() {
                    saveCobranza();
                    saveAbonos();
                    progresshide();
                }
            });
            /*new ChargeInteractorImp().executeGetChargeByEmployee(vendedoresBean.identificador, new ChargeInteractor.OnGetChargeByEmployeeListener() {
                @Override
                public void onGetChargeByEmployeeSuccess(@NonNull List<? extends CobranzaBean> chargeByClientList) {
                    saveCobranza();
                    saveAbonos();
                    progresshide();
                }
                @Override
                public void onGetChargeByEmployeeError() {
                    saveCobranza();
                    saveAbonos();
                    progresshide();
                }
            });*/
        }
    }

    private void saveVentas() {
        try {
            final SincVentas sincVentas = new SincVentas();

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
            e.printStackTrace();
        }
    }

    private void saveVisitas() {

        final VisitsDao visitsDao = new VisitsDao();
        List<VisitasBean> visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual());
        final ClientDao clientDao = new ClientDao();
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();

        if (vendedoresBean == null && getContext() != null) {
            vendedoresBean = new CacheInteractor().getSeller();
        }

        List<Visit> visitList = new ArrayList<>();
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

            visitList.add(visita);
        }

        new VisitInteractorImp().executeSaveVisit(visitList, new VisitInteractor.OnSaveVisitListener() {
            @Override
            public void onSaveVisitSuccess() {
                //Toast.makeText(requireActivity(), "Visita registrada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveVisitError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveCobranza() {

        final PaymentDao paymentDao = new PaymentDao();
        List<CobranzaBean> cobranzaBeanList = paymentDao.getCobranzaFechaActual(Utils.fechaActual());

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
                //Toast.makeText(requireActivity(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveChargeError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void savePreciosEspeciales() {

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
            if (items.getActive()) {
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
                //Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSendPricesError() {
                progresshide();
                //Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveClientes() {

        final ClientDao clientDao = new ClientDao();
        List<ClienteBean> clientListDB = clientDao.getClientsByDay(Utils.fechaActual());

        List<Client> clientList = new ArrayList<>();

        for (ClienteBean item : clientListDB) {
            Client client = new Client();
            client.setNombreComercial(item.getNombre_comercial());
            client.setCalle(item.getCalle());
            client.setNumero(item.getNumero());
            client.setColonia(item.getColonia());
            client.setCiudad(item.getCiudad());
            client.setCodigoPostal(item.getCodigo_postal());
            client.setFechaRegistro(item.getFecha_registro());
            client.setCuenta(item.getCuenta());
            client.setStatus(item.getStatus()? 1 : 0);
            client.setConsec(item.getConsec());
            client.setRango(item.getRango());
            client.setLun(item.getLun());
            client.setMar(item.getMar());
            client.setMie(item.getMie());
            client.setJue(item.getJue());
            client.setVie(item.getVie());
            client.setSab(item.getSab());
            client.setDom(item.getDom());
            client.setLatitud(item.getLatitud());
            client.setLongitud(item.getLongitud());
            client.setPhone_contacto("" + item.getContacto_phone());
            client.setRecordatorio("" + item.getRecordatorio());
            client.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()) {
                client.setCredito(1);
            } else {
                client.setCredito(0);
            }
            client.setSaldo_credito(item.getSaldo_credito());
            client.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz() == null || (item.getMatriz() != null && item.getMatriz().equals("null"))) {
                client.setMatriz("null");
            } else {
                client.setMatriz(item.getMatriz());
            }
            client.setUpdatedAt(item.getUpdatedAt());

            clientList.add(client);
        }

        new ClientInteractorImp().executeSaveClient(clientList, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                //Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
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

            empleado.setContrasenia(item.getContrasenia());
            empleado.setIdentificador(item.getIdentificador());
            empleado.setStatus(item.getStatus()? 1 : 0);
            empleado.setUpdatedAt(item.getUpdatedAt());

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
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Empleados sincronizados", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveEmployeeError() {
                progresshide();
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Ha ocurrido un error al sincronizar los empleados", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRoles() {
        new RolInteractorImp().executeGetAllRoles(new RolInteractor.OnGetAllRolesListener() {
            @Override
            public void onGetAllRolesSuccess(@NonNull List<? extends RolesBean> roles) {
                //progresshide();
            }

            @Override
            public void onGetAllRolesError() {
                //progresshide();
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showVersionErrorDialog(String message) {
        PrettyDialog dialog = new PrettyDialog(getContext());
        dialog.setTitle("Error")
                .setTitleColor(R.color.purple_500)
                .setMessage(message)
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> {});

        dialog.setCancelable(false);
        dialog.show();
    }
}