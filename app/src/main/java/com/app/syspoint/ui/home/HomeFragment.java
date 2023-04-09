package com.app.syspoint.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.models.sealed.GetClientsByRuteViewState;
import com.app.syspoint.models.sealed.HomeLoadingViewState;
import com.app.syspoint.models.sealed.HomeViewState;
import com.app.syspoint.models.sealed.SetRuteViewState;
import com.app.syspoint.repository.database.bean.AppBundle;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;
import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.RolesBean;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.dao.RolesDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.RoutingDao;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    AdapterRutaClientes mAdapter;
    List<ClientesRutaBean> mData;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_clientes;

    private HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getHomeLoadingViewState().observe(getViewLifecycleOwner(), (Observer) o -> {
            if (o instanceof HomeLoadingViewState.LoadingStart) {
                rlprogress.setVisibility(View.VISIBLE);
            } else if (o instanceof HomeLoadingViewState.LoadingFinish) {
                rlprogress.setVisibility(View.GONE);
            }
        });
        viewModel.getGetClientsByRuteViewState().observe(getViewLifecycleOwner(), (Observer) o -> {
            if (o instanceof GetClientsByRuteViewState.GetClientsByRuteSuccess) {
                loadRuta();
            } else if (o instanceof GetClientsByRuteViewState.GetClientsByRuteError) {
                showDialogNotConnectionInternet();
            }
        });

        viewModel.getSetUpRuteViewState().observe(getViewLifecycleOwner(), (Observer) o -> {
            if (o instanceof SetRuteViewState.RuteDefined) {
                loadRuta();
                Toast.makeText(getActivity(), "La ruta se cargo con exito!", Toast.LENGTH_LONG)
                        .show();
                mData = ((SetRuteViewState.RuteDefined) o).getClientRute();
            } else  if (o instanceof SetRuteViewState.RuteDefinedWithOutClients) {
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
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (!connected) {
                        //showDialogNotConnectionInternet();
                    } else {
                        getData(false);
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

    private void setDataList(List<ClientesRutaBean> list) {
        mData = list;
        mAdapter.setData(mData);
        showHideImage();
    }

    private void loadRuta() {
        mData = new ArrayList<>();
        RoutingDao routingDao = new RoutingDao();
        RuteoBean ruteoBean = routingDao.getRutaEstablecida();

        if (ruteoBean != null && ruteoBean.getDia() > 0) {
            EmpleadoBean vendedoresBean = AppBundle.getUserBean();
            String ruta = ruteoBean.getRuta() != null && !ruteoBean.getRuta().isEmpty() ? ruteoBean.getRuta(): vendedoresBean.getRute();

            mData = new RuteClientDao().getAllRutaClientes(ruta, ruteoBean.getDia());
        }

        setDataList(mData);
    }

    private void initRecyclerView(View root) {
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

        mAdapter = new AdapterRutaClientes(mData, position -> {
            if (position >= 0) {
                ClientesRutaBean clienteBean = mData.get(position);
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                Actividades.getSingleton(getActivity(), VentasActivity.class).muestraActividad(parametros);
            }
        }, position -> false);

        recyclerView.setAdapter(mAdapter);

        loadRuta();
    }

    private void getData(Boolean isUpdate) {
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
                if (isUpdate){
                    viewModel.getUpdates();
                } else {
                    viewModel.getData();
                }
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

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> {
            viewModel.getClientsByRute(false);
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void registerNetworkBroadcastForNougat() {
        MainActivity.NetworkChangeReceiver mNetworkChangeReceiver = new MainActivity.NetworkChangeReceiver(new MainActivity.ConnectionNetworkListener() {
            @Override
            public void onConnected() {
                getData(false);
            }

            @Override
            public void onDisconnected() {

            }
        });
        getActivity().registerReceiver(
                mNetworkChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }
}