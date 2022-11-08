package com.app.syspoint.ui.clientes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.ClientesRutaBean;

import java.util.ArrayList;
import java.util.List;

public class AdapterListaClientes extends RecyclerView.Adapter<AdapterListaClientes.Holder> implements Filterable {

    private List<ClientesRutaBean>mData;
    private List<ClientesRutaBean>mDataFiltrable;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    public AdapterListaClientes(List<ClientesRutaBean> mData, OnItemClickListener mOnItemClickListener, OnItemLongClickListener onItemLongClickListener ) {
        this.mData = mData;
        this.mDataFiltrable = mData;
        this.mOnItemClickListener = mOnItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_clientes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mDataFiltrable.get(position), mOnItemClickListener, onItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mDataFiltrable.size()>0 ? mDataFiltrable.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filtro = constraint.toString();

                if (filtro.isEmpty()){
                    mDataFiltrable = mData;
                }else {

                    //TODO filtro productos
                    List<ClientesRutaBean> filtroEmpleados = new ArrayList<>();

                    for (ClientesRutaBean row : mData){
                        if (row.getNombre_comercial().toLowerCase().contains(filtro) || row.getCalle().toLowerCase().contains(filtro) ){
                            filtroEmpleados.add(row);
                        }
                    }
                    mDataFiltrable = filtroEmpleados;
                }

                FilterResults results = new FilterResults();
                results.values = mDataFiltrable;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFiltrable = (ArrayList<ClientesRutaBean>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setClients(List<ClientesRutaBean> data) {
        this.mDataFiltrable  = data;
        notifyDataSetChanged();
    }

    public void setListaRuta(List<ClientesRutaBean> mData) {
        this.mDataFiltrable = mData;
        this.notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textViewNombre;
        TextView textViewCuenta;
        TextView textViewCategoria;
        TextView textViewColonia;
        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.textViewNombre = itemView.findViewById(R.id.textView_item_nombre_cliente_lista);
            this.textViewCuenta = itemView.findViewById(R.id.textView_item_cuenta_cliente_lista);
            this.textViewCategoria = itemView.findViewById(R.id.textView_item_categoria_cliente_lista);
            this.textViewColonia = itemView.findViewById(R.id.textView_item_colonia_cliente_lista);
            //this.imageView = itemView.findViewById(R.id.img_more);
        }

        private void bind(ClientesRutaBean cliente, final OnItemClickListener onItemClickListener, final OnItemLongClickListener onItemLongClickListener){

            textViewNombre.setText(""+ cliente.getNombre_comercial());
            textViewCuenta.setText("" + cliente.getCuenta());
            textViewCategoria.setText("" + cliente.getCategoria());
            textViewColonia.setText("Col. " + cliente.getColonia());

            itemView.setOnLongClickListener(v -> {
                onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                return true;
            });


            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    itemView.setEnabled(false);
                    onItemClickListener.onItemClick(v, mDataFiltrable.get(getAdapterPosition()), getAdapterPosition(), () -> itemView.setEnabled(true));
                    itemView.setEnabled(true);
                }
            });
        }
    }

    //Cuando el usuario da click en el item del cliente

    /**
     * Evento Click Largo
     * **/
    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ClientesRutaBean obj, int position, OnDialogShownListener onDialogShownListener);
    }

    public interface OnDialogShownListener {
        void onDialogShown();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
}
