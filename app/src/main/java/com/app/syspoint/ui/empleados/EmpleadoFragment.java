package com.app.syspoint.ui.empleados;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.syspoint.R;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.dao.EmpleadoDao;
import com.app.syspoint.db.dao.RolesDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Empleado;
import com.app.syspoint.json.EmpleadoJson;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpleadoFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CALL = 992;
    private List<EmpleadoBean> mData;
    private AdapterListaEmpleados mAdapter;
    private RelativeLayout rlprogress;
    private LinearLayout lyt_empleados;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_empleado, container, false);
        setHasOptionsMenu(true);

        lyt_empleados = root.findViewById(R.id.lyt_empleados);
        rlprogress = root.findViewById(R.id.rlprogress_empleados);

        FloatingActionButton fb = root.findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegistarEmpleadoActivity.class);
                startActivity(intent);
            }
        });
        initRecyclerView(root);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_empleado_fragment, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
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

            case R.id.syncEmpleados:

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (!connected) {
                        showDialogNotConnectionInternet();
                    }else {
                        getData();
                    }
                }, getActivity()).execute(), 100);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initRecyclerView(View root){
        mData = new ArrayList<>();
        mData = (List<EmpleadoBean>) (List<?>) new EmpleadoDao().list();

        if (mData.size() > 0){
            lyt_empleados.setVisibility(View.GONE);
        }else {
            lyt_empleados.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.rv_lista_empleados);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaEmpleados(mData, new AdapterListaEmpleados.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showSelecctionFunction(position);
            }
        });
        recyclerView.setAdapter(mAdapter);
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

    private void getData(){

        progressshow();

        Call<EmpleadoJson> getEmpleado = ApiServices.getClientRestrofit().create(PointApi.class).getAllEmpleados();

        getEmpleado.enqueue(new Callback<EmpleadoJson>() {
            @Override
            public void onResponse(Call<EmpleadoJson> call, Response<EmpleadoJson> response) {
                if (response.isSuccessful()){

                    progresshide();

                    for (Empleado item : response.body().getEmpleados()){

                        //Instancia el DAO
                        final EmpleadoDao dao = new EmpleadoDao();

                        //Validamos si existe el empleado en la base de datos en base al identificador
                        final EmpleadoBean empleadoBean  = dao.getEmpleadoByIdentificador(item.getIdentificador());

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
                            mData.add(empleado);
                        }
                    }
                    mAdapter.setEmpleados(mData);

                    if(mAdapter.getItemCount() > 0){
                        lyt_empleados.setVisibility(View.GONE);
                    }else {
                        lyt_empleados.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<EmpleadoJson> call, Throwable t) {
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
        mData = (List<EmpleadoBean>) (List<?>) new EmpleadoDao().list();
        mAdapter.setEmpleados(mData);
        if(mData.size() > 0){
            lyt_empleados.setVisibility(View.GONE);
        }else {
            lyt_empleados.setVisibility(View.VISIBLE);
        }
    }

    private void showSelecctionFunction(int position){

        final EmpleadoBean empleadoBean = mData.get(position);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Editar");
        arrayAdapter.add("Llamar");
        arrayAdapter.add("Enviar email");

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


                if (vendedoresBean != null){
                    identificador = vendedoresBean.getIdentificador();
                }
                final RolesDao rolesDao = new RolesDao();
                final RolesBean rolesBean = rolesDao.getRolByEmpleado(identificador, "Empleados");

                if (strName == null || strName.compareToIgnoreCase("Editar") == 0 ){
                    if (rolesBean != null){
                        if (rolesBean.getActive()){
                            editEmpleado(empleadoBean.identificador);

                        }else {
                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                        }
                    }

                }else if(strName.compareToIgnoreCase("Llamar") == 0){
                    llamar(empleadoBean.getTelefono());
                }else if(strName.compareToIgnoreCase("Enviar email") == 0){
                    enviarEmail();
                }

                dialog.dismiss();
            }
        });
        builderSingle.show();

    }

    private void editEmpleado(String identificador){
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put(Actividades.PARAM_1, identificador);
        Actividades.getSingleton(getActivity(), ActualizarEmpleadoActivity.class).muestraActividad(parametros);
    }

    private void llamar(String numero){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + numero));
        startActivity(callIntent);
    }
    private void enviarEmail(){
        String[] TO = {"someone@gmail.com"};
        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
           getActivity().finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}