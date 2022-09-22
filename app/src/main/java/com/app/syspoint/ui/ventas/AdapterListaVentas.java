package com.app.syspoint.ui.ventas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.VentasBean;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterListaVentas extends RecyclerView.Adapter<AdapterListaVentas.Holder> {

    private List<VentasBean> mData;
    private OnItemClickListener itemClickListener;

    public AdapterListaVentas(List<VentasBean> mData, OnItemClickListener itemClickListener) {
        this.mData = mData;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_ventas, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 0;
    }

    public void setVentas(List<VentasBean> venta) {
        this.mData = venta;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView textViewCliente;
        private TextView textViewNombre;
        private TextView textViewFecha;
        private TextView textViewEstado;
        private TextView textViewPrecio;

        public Holder(@NonNull View itemView) {
            super(itemView);

            this.textViewCliente = itemView.findViewById(R.id.textView_lista_venta_cliente_view);
            this.textViewNombre = itemView.findViewById(R.id.textView_lista_venta_cliente_nombre_view);
            this.textViewFecha = itemView.findViewById(R.id.textView_lista_venta_fecha_view);
            this.textViewEstado = itemView.findViewById(R.id.textView_lista_venta_estado_view);
            this.textViewPrecio = itemView.findViewById(R.id.textView_lista_venta_importe_view);
        }

        private void bind(VentasBean venta, final OnItemClickListener itemClickListener) {
            this.textViewCliente.setText("" + venta.getCliente().getCuenta());
            this.textViewNombre.setText("" + venta.getCliente().getNombre_comercial());
            this.textViewFecha.setText("" + venta.getFecha());
            this.textViewEstado.setText("" + venta.getEstado());
            this.textViewPrecio.setText("" + Utils.formatMoneyMX(venta.getImporte() + venta.getImpuesto()));


            if (venta.getEstado().compareToIgnoreCase("CA") == 0){
                textViewEstado.setTextColor(Color.RED);
                textViewEstado.setText("CANCELADO");
            }else {
                textViewEstado.setTextColor(Color.GREEN);
                textViewEstado.setText("CONFIRMADO");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
