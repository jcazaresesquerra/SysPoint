package com.app.syspoint.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.RuteClientDao;
import com.app.syspoint.repository.database.dao.RoutingDao;
import com.app.syspoint.ui.customs.DialogoRuteo;
import com.app.syspoint.utils.Utils;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class RutaFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_ruta, container, false);
        return view;
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
                                Toast.makeText(getActivity(), "Dia: " + dia + " Ruta: " + ruta, Toast.LENGTH_LONG).show();

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



}