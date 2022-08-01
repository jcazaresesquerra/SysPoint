package com.app.syspoint.ui.cobranza;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterCobranza extends RecyclerView.Adapter<AdapterCobranza.Holder>{


    private List<CobranzaModel> mData;
    private OnItemLongClickListener onItemLongClickListener;

    public AdapterCobranza(List<CobranzaModel> mData, OnItemLongClickListener onItemLongClickListener) {
        this.mData = mData;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cobranza_cardview, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()){
            return 0;
        }else {
            return mData.size();
        }


    }

    public void setItems(List<CobranzaModel> items){
        this.mData = items;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView textViewVenta;
        TextView textViewCobranza;
        TextView textViewImporte;
        TextView textViewSaldo;
        TextView textViewFecha;
        TextView textViewFechaVence;
        TextView textViewAbono;
        TextView textViewSaldoNuevo;

        public Holder(View itemView) {
            super(itemView);
            this.textViewVenta = itemView.findViewById(R.id.tv_venta_cobranza_view);
            this.textViewCobranza = itemView.findViewById(R.id.tv_cobranza_cobranza_view);
            this.textViewImporte = itemView.findViewById(R.id.tv_importe_cobranza_view);
            this.textViewSaldo= itemView.findViewById(R.id.tv_saldo_cobranza_view);
            this.textViewFecha = itemView.findViewById(R.id.tv_fecha_cobranza_view);
            this.textViewAbono = itemView.findViewById(R.id.tv_abono_cobranza_view);
            this.textViewSaldoNuevo = itemView.findViewById(R.id.tv_saldo_nuevo_cobranza_view);
        }


        public void bind(final CobranzaModel items){


            double importe = 0, acuenta = 0, saldoNuevo = 0;
            importe = items.getImporte();
            acuenta = items.getAcuenta();
            saldoNuevo = (importe - acuenta);

            textViewVenta.setText(""+items.getVenta());
            textViewCobranza .setText(""+items.getCobranza());
            textViewImporte.setText(Utils.FDinero(items.getImporte()));
            textViewSaldo.setText(Utils.FDinero(items.getSaldo()));

            try  {
                final CobranzaDao cobranzaDao = new CobranzaDao();
                final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(""+items.getCobranza());
                textViewFecha.setText(cobranzaBean.getFecha());
                textViewAbono.setText(Utils.FDinero(items.getAcuenta()));
                textViewSaldoNuevo.setText(Utils.FDinero(saldoNuevo));
            }catch (Exception e){

            }


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClicked(getAdapterPosition());
                    return false;
                }
            });


        }
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position);
    }
}