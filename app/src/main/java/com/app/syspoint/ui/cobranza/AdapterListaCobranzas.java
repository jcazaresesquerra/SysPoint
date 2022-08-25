package com.app.syspoint.ui.cobranza;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.CobrosBean;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterListaCobranzas extends RecyclerView.Adapter<AdapterListaCobranzas.Holder> {

    private List<CobrosBean> mData;
    private  OnItemClickListener onItemClickListener;

    public AdapterListaCobranzas(List<CobrosBean> mData, OnItemClickListener onItemClickListener) {
        this.mData = mData;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_cobranza_cardview, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position),onItemClickListener);
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()) {
            return 0;
        } else {
            return this.mData.size();
        }
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView tvCliente;
        private TextView tvNombre;
        private TextView tvCobranza;
        private TextView tvHora;
        private TextView tvFecha;
        private TextView tvTotal;


        public Holder(@NonNull View itemView) {
            super(itemView);


            this.tvCliente = itemView.findViewById(R.id.tv_lista_cobranza_cliente_view);
            this.tvNombre = itemView.findViewById(R.id.tv_lista_cobranza_cliente_nombre_view);
            this.tvCobranza = itemView.findViewById(R.id.tv_lista_cobranza_cobranza_view);
            this.tvHora = itemView.findViewById(R.id.tv_lista_cobranza_hora_cobranza_view);
            this.tvFecha = itemView.findViewById(R.id.tv_lista_cobranza_fecha_view);
            this.tvTotal = itemView.findViewById(R.id.tv_lista_cobranza_importe_view);
        }

        public void bind(final CobrosBean cobrosBean, OnItemClickListener onItemClickListener) {

            double total = (cobrosBean.getImporte());
            String subtotalFormato = Utils.formatMoneyMX((total));
            tvCliente.setText(cobrosBean.getCliente().getCuenta());
            tvNombre.setText(cobrosBean.getCliente().getNombre_comercial());
            tvCobranza.setText(String.valueOf(cobrosBean.getCobro()));
            tvHora.setText(cobrosBean.getHora());
            tvFecha.setText(cobrosBean.getFecha());
            tvTotal.setText("$" + subtotalFormato);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

        }
    }




    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
