package com.app.syspoint.ui.home;

import static com.app.syspoint.utils.Constants.REQUEST_PERMISSION_LOCATION;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.models.enums.RoleType;
import com.app.syspoint.models.sealed.GetClientsByRuteViewState;
import com.app.syspoint.models.sealed.HomeLoadingViewState;
import com.app.syspoint.models.sealed.SetRuteViewState;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.dao.RuteClientDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.repository.objectBox.entities.RuteClientBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;
import com.app.syspoint.ui.MainActivity;
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
import com.app.syspoint.viewmodel.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    AdapterRutaClientes mAdapter;
    List<RuteClientBox> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_clientes;

    private HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getHomeLoadingViewState().observe(getViewLifecycleOwner(), viewState -> {
            if (viewState instanceof HomeLoadingViewState.LoadingStart) {
                showLoading();
            } else if (viewState instanceof HomeLoadingViewState.LoadingFinish) {
                hideLoading();
            }
        });
        viewModel.getGetClientsByRuteViewState().observe(getViewLifecycleOwner(), o -> {
            if (o instanceof GetClientsByRuteViewState.GetClientsByRuteSuccess) {
                Timber.tag(TAG).d("GetClientsByRuteSuccess call loadRuta");
                loadRuta();
            } else if (o instanceof GetClientsByRuteViewState.GetClientsByRuteError) {
                Timber.tag(TAG).d("GetClientsByRuteError call showDialogNotConnectionInternet");
                showDialogNotConnectionInternet();
            }
        });

        viewModel.getSetUpRuteViewState().observe(getViewLifecycleOwner(), o -> {
            if (o instanceof SetRuteViewState.RuteDefined) {
                loadRuta();
                Timber.tag(TAG).d("RuteDefined -> called loadRuta and call getClientRute");
                Toast.makeText(getActivity(), "La ruta se cargo con exito!", Toast.LENGTH_LONG)
                        .show();
                mData = ((SetRuteViewState.RuteDefined) o).getClientRute();
            } else  if (o instanceof SetRuteViewState.RuteDefinedWithOutClients) {
                Timber.tag(TAG).d("RuteDefinedWithOutClients call loadRuta");
                loadRuta();
            }
        });

        if (Constants.Companion.getSolictaRuta()) {
            creaRutaSeleccionada();
        }

        lyt_clientes = root.findViewById(R.id.lyt_clientes);
        rlprogress = root.findViewById(R.id.rlprogress_cliente);

        initRecyclerView(root);
        getData(true);

        FloatingActionButton publicSale = root.findViewById(R.id.fb_public_sale);
        publicSale.setOnClickListener(v -> {
            makePublicSale();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerNetworkBroadcastForNougat();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRuta();
        showHideImage();
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
                Timber.tag(TAG).d("sinronizaAll Clicked");
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (!connected) {
                        //showDialogNotConnectionInternet();
                    } else {
                        Timber.tag(TAG).d("sinronizaAll NetworkStateTask connected call getData(false)");
                        getData(false);
                    }
                }).execute(), 100);

                return true;

            case R.id.close_caja:
                Timber.tag(TAG).d("close_caja clicked call closeBox");
                closeBox();
                return true;

            case R.id.viewMap:
                Timber.tag(TAG).d("viewMap clicked call MapsRuteoActivity");
                Actividades.getSingleton(getActivity(), MapsRuteoActivity.class).muestraActividad();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makePublicSale() {
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

        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, client.getCuenta());

        Timber.tag(TAG).d("Public Sale -> click -> open VentasActivity -> %s", client.getCuenta());

        Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
    }

    private void creaRutaSeleccionada() {
        Timber.tag(TAG).d("creaRutaSeleccionada");
        RoutingDao dao = new RoutingDao();
        RoutingBox bean = dao.getRutaEstablecidaFechaActual(Utils.fechaActual());

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
        Timber.tag(TAG).d("showDialog");
        EmployeeBox vendedoresBean = getEmployee();

        if (vendedoresBean !=  null) {
            RolesBox rutasRol = new RolesDao().getRolByEmpleado(vendedoresBean.getIdentificador(), RoleType.RUTES.getValue());

            boolean editRuta = rutasRol != null && rutasRol.getActive();

            DialogoRuteo dialogoRuteo = new DialogoRuteo(getActivity(), editRuta, new DialogoRuteo.DialogListener() {
                @Override
                public void ready(String dia, String ruta) {

                    PrettyDialog dialog = new PrettyDialog(getContext());
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
                                viewModel.setUpRute(dia, ruta);

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

    private void showHideImage() {
        if (mData.size() > 0) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }
    }

    private void setDataList(List<RuteClientBox> list) {
        mData = list;
        mAdapter.setData(mData);
        showHideImage();
    }

    private void loadRuta() {
        Timber.tag(TAG).d("loadRuta");
        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RoutingBox ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null && ruteoBean.getDia() > 0) {
            EmployeeBox vendedoresBean = getEmployee();
            String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();

            mData = new RuteClientDao().getAllRutaClientes(ruta, ruteoBean.getDia());
        }

        setDataList(mData);
    }

    private void initRecyclerView(View root) {
        Timber.tag(TAG).d("initRecyclerView");
        mData = new ArrayList<>();

        if (!mData.isEmpty()) {
            lyt_clientes.setVisibility(View.GONE);
        } else {
            lyt_clientes.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = root.findViewById(R.id.rv_lista_clientes);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        boolean isOrderRute = false;
        EmployeeBox vendedoresBean = getEmployee();
        if (vendedoresBean !=  null) {
            RolesBox rutasRol = new RolesDao().getRolByEmpleado(vendedoresBean.getIdentificador(), RoleType.ORDER_RUTES.getValue());
            isOrderRute = rutasRol != null && rutasRol.getActive();
        }

        boolean finalIsOrderRute = isOrderRute;
        mAdapter = new AdapterRutaClientes(mData, position -> {
            Timber.tag(TAG).d("initRecyclerView -> AdapterRutaClientes -> %s -> %s", position, mData);
            if (position >= 0) {
                boolean canSell = true;
                if (finalIsOrderRute) {
                    if (position != 0) {
                        canSell = false;
                        showOrderRuteMessage();
                    }
                }

                if (canSell) {
                    RuteClientBox clienteBean = mData.get(position);
                    HashMap<String, String> parametros = new HashMap<>();
                    parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());

                    Timber.tag(TAG).d("initRecyclerView -> AdapterRutaClientes -> open VentasActivity -> %s", clienteBean.getCuenta());

                    Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
                }
            }
        }, position -> false, () -> {
            Timber.tag(TAG).d("initRecyclerView -> AdapterRutaClientes -> requestCallPermissions ");
            ActivityCompat.requestPermissions(HomeFragment.this.requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PERMISSION_CALL);
        }

        );


        recyclerView.setAdapter(mAdapter);

        loadRuta();
    }

    private void getData(Boolean isUpdate) {
        Timber.tag(TAG).d("getData %s", isUpdate);
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
                Timber.tag(TAG).d("NetworkStateTask connected %s", isUpdate);
                if (isUpdate){
                    viewModel.getUpdates();
                } else {
                    viewModel.getData();
                }
            } else {
                Timber.tag(TAG).d("NetworkStateTask not connected ");
                showDialogNotConnectionInternet();
            }
        }).execute(), 100);
    }

    private void closeBox() {
        Timber.tag(TAG).d("closeBox");

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

    private void showLoading() {
        Timber.tag(TAG).d("LoadingStart");
        rlprogress.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            Timber.tag(TAG).d("LoadingFinish by runnabler");
            hideLoading();
            /*new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                if (connected) {
                    Timber.tag(TAG).d("Check connection by runnabler -> NetworkStateTask connected ");
                } else {
                    Timber.tag(TAG).d("Check connection by runnabler -> NetworkStateTask not connected ");
                    showDialogNotConnectionInternet();
                }
            }).execute(), 100);*/
        }, 4000);

        if (getActivity() != null)
            ((MainActivity) getActivity()).blockInput();
    }

    private void hideLoading() {
        Timber.tag(TAG).d("LoadingFinish");
        rlprogress.setVisibility(View.GONE);
        if (getActivity() != null)
            ((MainActivity) getActivity()).unblockInput();
    }

    private void showOrderRuteMessage() {
        Timber.tag(TAG).d("showOrderRuteMessage");
        Toast.makeText(getActivity(), "Es obligatorio seguir la secuencia del listado", Toast.LENGTH_SHORT).show();
    }

    private void showDialogNotConnectionInternet() {
        Timber.tag(TAG).d("showDialogNotConnectionInternet");
        if (getActivity() != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(true);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.bt_close).setOnClickListener(v -> {
                viewModel.getClientsByRute(false);
                dialog.dismiss();
            });

            if (!dialog.isShowing()) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            dialog.getWindow().setAttributes(lp);
        }
    }

    private void registerNetworkBroadcastForNougat() {
        Timber.tag(TAG).d("registerNetworkBroadcastForNougat");
        MainActivity.NetworkChangeReceiver mNetworkChangeReceiver = new MainActivity.NetworkChangeReceiver(new MainActivity.ConnectionNetworkListener() {
            @Override
            public void onConnected() {
                Timber.tag(TAG).d("registerNetworkBroadcastForNougat -> NetworkChangeReceiver -> onConnected-> getData(false)");
                getData(false);
            }

            @Override
            public void onDisconnected() {
                Timber.tag(TAG).d("registerNetworkBroadcastForNougat -> NetworkChangeReceiver -> onDisconnected -> getData(false)");
            }
        });
        getActivity().registerReceiver(
                mNetworkChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
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