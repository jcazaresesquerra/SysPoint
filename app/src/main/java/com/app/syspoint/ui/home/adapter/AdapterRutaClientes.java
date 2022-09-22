package com.app.syspoint.ui.home.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;

import java.util.Calendar;
import java.util.List;

public class AdapterRutaClientes  extends RecyclerView.Adapter<AdapterRutaClientes.Holder> {

    private List<ClientesRutaBean> mData;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public AdapterRutaClientes(List<ClientesRutaBean> mData, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        this.mData = mData;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_clientes_ruta, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position), onItemClickListener, onItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 0;
    }

    public void setListaRuta(List<ClientesRutaBean> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewCliente;
        TextView textViewDireccion;
        TextView textViewColonia;
        TextView textViewFechaSigVisita;

        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewCliente = itemView.findViewById(R.id.textView_nombre_cliente_ruta);
            textViewDireccion  = itemView.findViewById(R.id.textView_direccion_cliente_ruta);
            textViewColonia  = itemView.findViewById(R.id.textView_colonia_cliente_ruta);
            textViewFechaSigVisita  = itemView.findViewById(R.id.textView_fecha_visita_cliente_ruta);
            imageView = itemView.findViewById(R.id.img_call);
        }

        private void bind(final ClientesRutaBean clienteBean, final OnItemClickListener onItemClickListener, final OnItemLongClickListener onItemLongClickListener){

            textViewCliente.setText(clienteBean.getNombre_comercial());
            textViewDireccion.setText(clienteBean.getCalle() + " " + clienteBean.getNumero());
            textViewColonia.setText("Col. " + clienteBean.getColonia());



            imageView.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), imageView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {

                    if (clienteBean.getPhone_contact() == null || clienteBean.getPhone_contact().isEmpty() || clienteBean.getPhone_contact().equals( "null")){

                        Toast.makeText(itemView.getContext(), "El cliente no cuenta con número de contacto", Toast.LENGTH_LONG).show();

                        return false;
                    }else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+52" + clienteBean.getPhone_contact()));
                        itemView.getContext().startActivity(intent);

                    }


                    return true;
                });

                popup.show();//showing popup menu
            });


            itemView.setOnLongClickListener(v -> {

                onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                return false;
            });

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));


            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case Calendar.SUNDAY: //TODO DOMINGO
                    if (clienteBean.getLun() == 1) {
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    }else if (clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita MARTES");
                        return;
                    }else if(clienteBean.getMie() == 1){
                        textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                        return;
                    }else if(clienteBean.getJue() == 1){
                        textViewFechaSigVisita.setText("Sig visita JUEVES");
                        return;

                    }else if(clienteBean.getVie() == 1){
                        textViewFechaSigVisita.setText("Sig visita VIERNES");
                        return;

                    }else if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;
                    }
                    break;
                case Calendar.MONDAY: //TODO LUNES
                   if (clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita MARTES");
                        return;
                    }else if(clienteBean.getMie() == 1){
                        textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                        return;
                    }else if(clienteBean.getJue() == 1){
                        textViewFechaSigVisita.setText("Sig visita JUEVES");
                        return;

                    }else if(clienteBean.getVie() == 1){
                        textViewFechaSigVisita.setText("Sig visita VIERNES");
                        return;

                    }else if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;
                    }
                    else if(clienteBean.getDom() == 1){
                       textViewFechaSigVisita.setText("Sig visita DOMINGO");
                       return;
                   }
                    break;
                case Calendar.TUESDAY:  //TODO MARTES

                    if (clienteBean.getMie() == 1){
                        textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                        return;
                    }else if(clienteBean.getJue() == 1){
                        textViewFechaSigVisita.setText("Sig visita JUEVES");
                        return;
                    }else if(clienteBean.getVie() == 1){
                        textViewFechaSigVisita.setText("Sig visita VIERNES");
                        return;

                    }else if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;

                    }else if(clienteBean.getDom() == 1){
                        textViewFechaSigVisita.setText("Sig visita DOMINGO");
                        return;
                    }else if(clienteBean.getLun() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    }

                    break;
                case Calendar.WEDNESDAY:  //TODO MIERCOLES
                    if(clienteBean.getJue() == 1){
                        textViewFechaSigVisita.setText("Sig visita JUEVES");
                        return;
                    }else if(clienteBean.getVie() == 1){
                        textViewFechaSigVisita.setText("Sig visita VIERNES");
                        return;

                    }else if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;

                    }else if(clienteBean.getDom() == 1){
                        textViewFechaSigVisita.setText("Sig visita DOMINGO");
                        return;
                    }else if(clienteBean.getLun() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    }
                    if(clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    }
                    break;
                case Calendar.THURSDAY: //TODO JUEVEZ
                     if(clienteBean.getVie() == 1){
                        textViewFechaSigVisita.setText("Sig visita VIERNES");
                        return;

                    }else if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;

                    }else if(clienteBean.getDom() == 1){
                        textViewFechaSigVisita.setText("Sig visita DOMINGO");
                        return;
                    }else if(clienteBean.getLun() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    } else if(clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita MARTES");
                        return;
                    }
                     else if(clienteBean.getMar() == 1){
                         textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                         return;
                     }
                    break;
                case Calendar.FRIDAY: //TODO VIERNES
                   if(clienteBean.getSab() == 1){
                        textViewFechaSigVisita.setText("Sig visita SABADO");
                        return;
                    }else if(clienteBean.getDom() == 1){
                        textViewFechaSigVisita.setText("Sig visita DOMINGO");
                        return;
                    }else if(clienteBean.getLun() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    } else if(clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita MARTES");
                        return;
                    }
                    else if(clienteBean.getMie() == 1){
                        textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                        return;
                    }else if(clienteBean.getJue() == 1){
                       textViewFechaSigVisita.setText("Sig visita JUEVES");
                       return;
                   }
                    break;
                case Calendar.SATURDAY: // TODO Sabado
                   if(clienteBean.getDom() == 1){
                        textViewFechaSigVisita.setText("Sig visita DOMINGO");
                        return;
                    }else if(clienteBean.getLun() == 1){
                        textViewFechaSigVisita.setText("Sig visita LUNES");
                        return;
                    } else if(clienteBean.getMar() == 1){
                        textViewFechaSigVisita.setText("Sig visita MARTES");
                        return;
                    }
                    else if(clienteBean.getMie() == 1){
                        textViewFechaSigVisita.setText("Sig visita MIERCOLES");
                        return;
                    }else if(clienteBean.getJue() == 1){
                        textViewFechaSigVisita.setText("Sig visita JUEVES");
                        return;
                    }else if(clienteBean.getVie() == 1){
                       textViewFechaSigVisita.setText("Sig visita VIERNES");
                       return;
                   }
                    break;
            }



        }
    }

    //Cuando el usuario da click en el item del cliente
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    /**
     * Evento Click Largo
     * **/
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }

}
