package com.app.syspoint.ui.cobranza;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterListaDocumentosCobranza extends RecyclerView.Adapter<AdapterListaDocumentosCobranza.Holder> {

    private List<CobranzaBean> mData;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener onItemLongClickListener;



    public AdapterListaDocumentosCobranza(List<CobranzaBean> mData, OnItemClickListener itemClickListener, OnItemLongClickListener onItemLongClickListener) {
        this.mData = mData;
        this.itemClickListener = itemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_documentos_cardview, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position), itemClickListener,onItemLongClickListener);

    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()){
            return 0;
        }else {
            return this.mData.size();
        }
    }
    public void setData(List<CobranzaBean>data){
        this.mData = data;
        notifyDataSetChanged();
    }


    public class Holder extends RecyclerView.ViewHolder {

        private TextView textViewVenta;
        private TextView textViewCobranza;
        private TextView textViewImporte;
        private TextView textViewSaldo;
        private TextView textViewFecha;
        private TextView textViewFechaVence;
        private ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.textViewVenta = itemView.findViewById(R.id.tv_venta_cobranza_view);
            this.textViewCobranza = itemView.findViewById(R.id.tv_cobranza_cobranza_view);
            this.textViewImporte = itemView.findViewById(R.id.tv_importe_cobranza_view);
            this.textViewSaldo = itemView.findViewById(R.id.tv_saldo_cobranza_view);
            this.textViewFecha = itemView.findViewById(R.id.tv_fecha_cobranza_view);
            this.imageView = itemView.findViewById(R.id.imageView_check);
        }

        public void bind(final CobranzaBean cobranzaBean, final OnItemClickListener listener, final OnItemLongClickListener onItemLongClickListener){

            this.textViewVenta.setText(""+cobranzaBean.getVenta());
            this.textViewCobranza.setText(""+cobranzaBean.getCobranza());
            this.textViewImporte.setText(Utils.FDinero(cobranzaBean.getImporte()));
            this.textViewSaldo.setText(Utils.FDinero(cobranzaBean.getSaldo()));
            this.textViewFecha.setText(cobranzaBean.getFecha());
            if (cobranzaBean.getIsCheck()){
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }

}
