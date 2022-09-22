package com.app.syspoint.ui.cobranza;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.utils.Utils;

import java.util.List;

public class AdapterCobranzaDocumento extends RecyclerView.Adapter<AdapterCobranzaDocumento.Holder> {


    private List<CobranzaModel> mData;
    private OnItemLongClickListener onItemLongClickListener;

    public AdapterCobranzaDocumento(List<CobranzaModel> mData, OnItemLongClickListener onItemLongClickListener) {
        this.mData = mData;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
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
            this.textViewSaldo = itemView.findViewById(R.id.tv_saldo_cobranza_view);
            this.textViewFecha = itemView.findViewById(R.id.tv_fecha_cobranza_view);
            this.textViewAbono = itemView.findViewById(R.id.tv_abono_cobranza_view);
            this.textViewSaldoNuevo = itemView.findViewById(R.id.tv_saldo_nuevo_cobranza_view);
        }

        public void bind(final CobranzaModel items) {

            double importe = 0, acuenta = 0, saldoNuevo = 0;
            importe = items.getImporte();
            acuenta = items.getAcuenta();
            saldoNuevo = (importe - acuenta);

            textViewVenta.setText("" + items.getVenta());
            textViewCobranza.setText("" + items.getCobranza());
            textViewImporte.setText(Utils.FDinero(items.getImporte()));
            textViewSaldo.setText(Utils.FDinero(items.getSaldo()));

            try {
                final PaymentDao paymentDao = new PaymentDao();
                final CobranzaBean cobranzaBean = paymentDao.getByCobranza("" + items.getCobranza());
                textViewFecha.setText(cobranzaBean.getFecha());
                //textViewFechaVence.setText(cobranzaBean.getFECHA_VENC());
                textViewAbono.setText(Utils.FDinero(items.getAcuenta()));
                textViewSaldoNuevo.setText(Utils.FDinero(saldoNuevo));
            } catch (Exception e) {

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
