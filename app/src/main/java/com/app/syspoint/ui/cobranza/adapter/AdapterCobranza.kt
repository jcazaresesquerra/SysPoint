package com.app.syspoint.ui.cobranza.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemCobranzaCardviewBinding
import com.app.syspoint.repository.database.dao.PaymentDao
import com.app.syspoint.ui.cobranza.CobranzaModel
import com.app.syspoint.utils.Utils

class AdapterCobranza(
    data: List<CobranzaModel?>,
    val onItemLongClickListener: OnItemLongClickListener
    ): RecyclerView.Adapter<AdapterCobranza.Holder>() {

    var mData = data

    interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCobranzaCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, onItemLongClickListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int =  if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<CobranzaModel?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(
        val binding: ItemCobranzaCardviewBinding,
        val onItemLongClickListener: OnItemLongClickListener
        ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CobranzaModel?) {
            item?.let { charge ->
                val import = charge.importe
                val acuenta = charge.acuenta
                val saldoNuevo = import - acuenta
                binding.tvVentaCobranzaView.text = charge.venta.toString()
                binding.tvCobranzaCobranzaView.text = charge.cobranza
                binding.tvImporteCobranzaView.text = Utils.FDinero(charge.importe)
                binding.tvSaldoCobranzaView.text = Utils.FDinero(charge.saldo)
                try {
                    val paymentDao = PaymentDao()
                    val chargeBean = paymentDao.getByCobranza(charge.cobranza)
                    chargeBean?.let {
                        binding.tvFechaCobranzaView.text = it.fecha
                    }
                    binding.tvAbonoCobranzaView.text = Utils.FDinero(charge.acuenta)
                    binding.tvSaldoNuevoCobranzaView.text = Utils.FDinero(saldoNuevo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                itemView.setOnLongClickListener {
                    onItemLongClickListener.onItemLongClicked(adapterPosition)
                    false
                }
            }
        }
    }

}